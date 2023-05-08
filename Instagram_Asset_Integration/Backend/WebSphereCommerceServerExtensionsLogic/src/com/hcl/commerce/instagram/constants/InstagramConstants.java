package com.hcl.commerce.instagram.constants;
/**
*-----------------------------------------------------------------
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

*-----------------------------------------------------------------
**/
import java.util.Arrays;
import java.util.List;

public class InstagramConstants {

	public static final String SUCCESS = "success";
	public static final String FAIL = "fail";
	public static final String FBID = "facebookPageID";
	public static final String INSTAID = "instagramAccountId";
	public static final String CAROUSEL = "CAROUSEL";
	public static final String VIDEO = "VIDEO";
	public static final String MEDIA = "media";
	public static final String PUBLISH = "publish";
	public static final String TRUE = "true";
	public static final String FALSE = "false";
	public static final String QUOTA_ERROR_CODE = "2207042";
	public static final List<String> MEDIA_ERROR_CODE_RETRY = Arrays.asList("2207003", "2207001", "2207032", "2207053");
	public static final List<String> MEDIA_ERROR_CODE_NOT_RETRY = Arrays.asList("2207020", "2207057", "2207051",
			"2207042", "2207006", "2207050", "2207023", "2207028", "2207026", "2207052", "2207004", "2207005",
			"2207009", "2207010");
}
