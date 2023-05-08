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

import { Component, OnInit } from '@angular/core';
import { Output, Input, EventEmitter } from '@angular/core';
import { SocialMainService } from "../services/social-main.service";

declare const FB: any;

@Component({
  selector: 'fb-login-button',
  templateUrl: './fb-login-button.component.html',
  styleUrls: ["./social-media/social-media.component.scss"]

})
export class FBLoginButtonComponent implements OnInit {
  static isDisabled: boolean;
  static getAccessToken: string;
  @Output() loginStatus = new EventEmitter<string>();
  @Output() accessToken = new EventEmitter<string>();
  @Input() storeId; 

  constructor(public _socialMainService:SocialMainService) {}

  ngOnInit(): void {
    this.getDetails();
    FBLoginButtonComponent.isDisabled = false;
    //this.checkLoginState();
  }

  get fbLoginStatus(){
    return FBLoginButtonComponent.isDisabled;
  }

  get getAccessToken(){
    return FBLoginButtonComponent.getAccessToken;
  }

  //Check Facebook login status on Login click and Login to user.
  loginWithFacebook(): void {
    FB.login((response) => {
      if (response.status === 'connected') {   // Logged into your webpage and Facebook.
        FBLoginButtonComponent.isDisabled = true;
        this.loginStatus.emit(`${this.fbLoginStatus}`);
        if (response.authResponse) {
          FBLoginButtonComponent.getAccessToken=response.authResponse.accessToken;
          this.accessToken.emit(this.getAccessToken);
        }        
      } else {                                 // Not logged into your webpage or we are unable to tell.
        FBLoginButtonComponent.isDisabled = false; 
        this.loginStatus.emit(`${this.fbLoginStatus}`);
      }
    }, { scope: "public_profile,email,ads_management,business_management,instagram_basic,instagram_content_publish,pages_read_engagement" });
  }

  // Check Facebook login status on page load. 
  checkLoginState() {            // Called when a person is finished with the Login Button.
    FB.getLoginStatus(function(response) {   // See the onlogin handler // The current login status of the person.
      if (response.status === 'connected') {   // Logged into your webpage and Facebook.       
        FBLoginButtonComponent.isDisabled = true;
        FBLoginButtonComponent.getAccessToken=response.authResponse.accessToken;       
      } else {                                 // Not logged into your webpage or we are unable to tell.
        FBLoginButtonComponent.isDisabled = false;
      }
    });
    this.loginStatus.emit(`${this.fbLoginStatus}`);
    this.accessToken.emit(this.getAccessToken);
  }

  //To invoke deatils API
	public getDetails() {
		let url = "details";
		this._socialMainService.getConfigration(url,null,this.storeId).subscribe(res=>{
      FB.init({
        appId: res.appId,
        cookie: true,
        xfbml: true,
        version: res.appVersion,
        status: true
        });
        this.checkLoginState();
		},err=>{
			
		})
	}
}

