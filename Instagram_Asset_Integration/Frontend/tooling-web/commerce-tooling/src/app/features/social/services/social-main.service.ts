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
 import { Observer, Observable } from "rxjs";
 import { ConnectionSpecsService } from "../../../rest/services/connection-specs.service";
 import { HttpClient, HttpRequest, HttpResponse, HttpHeaders } from "@angular/common/http";
 import { BaseService as __BaseService } from "../../../rest/base-service";
 import { ApiConfiguration as __Configuration } from "../../../rest/api-configuration";
 import { StrictHttpResponse as __StrictHttpResponse } from "../../../rest/strict-http-response";
 
 import { map as __map, filter as __filter } from "rxjs/operators";
import { JsonPipe } from "@angular/common";

@Injectable({
	providedIn: "root"
})
export class SocialMainService extends __BaseService{
	readonly basePath1 = "/wcs/resources/store/";
	readonly basePath2 = "/instagram/";
	constructor(
		config: __Configuration,
		http: HttpClient
	) {
		super(config, http);
	}

	postConfigration(url,body,storeId, param?: any): Observable<any> {
		return new Observable<Array<any>>((observer: Observer<Array<any>>) => {
			this.reqRespConfig("POST", this.basePath1+storeId+this.basePath2+url, body, param).pipe(
				__map(_r => _r.body as null)
			).subscribe((res: any) => {
				observer.next(res);
			},
			error => {
				observer.error(error);
			});
		});
	}

	getConfigration(url,body,storeId, param?: any): Observable<any> {
		return new Observable<Array<any>>((observer: Observer<Array<any>>) => {
			this.reqRespConfig("GET", this.basePath1+storeId+this.basePath2+url, body, param).pipe(
				__map(_r => _r.body as null)
			).subscribe((res: any) => {
				observer.next(res);
			},
			error => {
				observer.error(error);
			});
		});
	}

	reqRespConfig(reqType, url, body?: any, params?: any,): Observable<__StrictHttpResponse<null>> {
		let __headers = new HttpHeaders();
		let __body: any = null;

		if (body) {
			__body = body;
		}

		// once testing is done remove this line;
		// this.rootUrl = "https://localhost:5443";

		let req = new HttpRequest<any>(
			reqType,
			this.rootUrl + url,
			__body,
			{
				headers: __headers,
				responseType: "json",
				withCredentials: true
			});
		return this.http.request<any>(req).pipe(
			__filter(_r => _r instanceof HttpResponse),
			__map((_r) => {
				return _r as __StrictHttpResponse<null>;
			})
		);
	}
}

