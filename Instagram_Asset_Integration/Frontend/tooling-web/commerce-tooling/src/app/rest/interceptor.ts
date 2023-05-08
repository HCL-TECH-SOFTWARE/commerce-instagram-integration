/*
*==================================================
Copyright [2022] [HCL America, Inc.]
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*================================================== 
*/

import { Injectable } from "@angular/core";
import { HttpEvent, HttpInterceptor, HttpHandler, HttpRequest, HttpResponse } from "@angular/common/http";
import { Observable, Observer, Subscription } from "rxjs";
import { AuthService } from "../services/auth.service";
import { ApiConfiguration } from "./api-configuration";
import { LanguageService } from "../services/language.service";

@Injectable()
export class JwtInterceptor implements HttpInterceptor {
	constructor(private authService: AuthService, private apiConfiguration: ApiConfiguration, private languageService: LanguageService) { }

	intercept(request: HttpRequest<any>, nextHttpHandler: HttpHandler): Observable<HttpEvent<any>> {
		if (request.url.indexOf(this.apiConfiguration.rootUrl + "/wcs/resources") === 0
			|| request.url.indexOf(this.apiConfiguration.rootUrl + "/rest") === 0
			|| request.url.indexOf(this.apiConfiguration.searchQueryRootUrl) === 0
			|| request.url.indexOf(this.apiConfiguration.searchDataQueryRootUrl) === 0
			// || request.url.indexOf('https://localhost:5443') === 0      /* for testing in localhost, add this line */
			) {
			return new Observable<HttpEvent<any>>((observer: Observer<HttpEvent<any>>) => {
				if (AuthService.jwt) {
					this.sendAuthenticatedRequest(request, nextHttpHandler, observer, null);
				} else {
					const isTokenValidSubscription: Subscription = this.authService.isTokenValid.subscribe((isTokenValid: boolean) => {
						if (isTokenValid) {
							this.sendAuthenticatedRequest(request, nextHttpHandler, observer, isTokenValidSubscription);
						}
					});
				}
			});
		}
		return nextHttpHandler.handle(request);
	}

	sendAuthenticatedRequest(request: HttpRequest<any>, nextHttpHandler: HttpHandler, observer: Observer<HttpEvent<any>>,
		isTokenValidSubscription: Subscription): void {
		const requestUpdate: any = {
			setHeaders: {
				"Authorization": `Bearer ${AuthService.jwt}`
			}
		};
		if (request.url.indexOf(this.apiConfiguration.rootUrl + "/rest") === 0) {
			requestUpdate.setParams = {
				locale: LanguageService.locale
			};
		}
		const jwtRequest: HttpRequest<any> = request.clone(requestUpdate);
		window.parent.postMessage({"action": "START_PROGRESS_INDICATOR"}, "*");
		nextHttpHandler.handle(jwtRequest).subscribe(
			response => {
				if (isTokenValidSubscription) {
					isTokenValidSubscription.unsubscribe();
				}
				observer.next(response);
			},
			err => {
				window.parent.postMessage({"action": "STOP_PROGRESS_INDICATOR"}, "*");
				if (err.status === 401) {
					this.authService.setJwt(null);
					if (!isTokenValidSubscription) {
						isTokenValidSubscription = this.authService.isTokenValid.subscribe((isTokenValid: boolean) => {
							if (isTokenValid) {
								this.sendAuthenticatedRequest(request, nextHttpHandler, observer, isTokenValidSubscription);
							}
						});
					}
				} else {
					if (isTokenValidSubscription) {
						isTokenValidSubscription.unsubscribe();
					}
					observer.error(err);
				}
			},
			() => {
				window.parent.postMessage({"action": "STOP_PROGRESS_INDICATOR"}, "*");
				observer.complete();
			}
		);
	}
}
