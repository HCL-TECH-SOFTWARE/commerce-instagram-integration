package com.hcl.commerce.instagram.ras;
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
import com.ibm.commerce.ras.ECMessage;
import com.ibm.commerce.ras.ECMessageSeverity;
import com.ibm.commerce.ras.ECMessageType;

public class InstagramECMessage {

	// Resource bundle used to extract the text for an exception
	static final String errorBundle = "InstagramErrorMessages";

	// An ECMessage describes an ECException and is passed into the ECException when
	// thrown
	public static final ECMessage FACEBOOK_ID_ERROR = new ECMessage(ECMessageSeverity.ERROR, ECMessageType.USER,
			"FACEBOOK_ID_ERROR", errorBundle, errorBundle, "INSTA0001");
	public static final ECMessage INSTAGRAM_ID_ERROR = new ECMessage(ECMessageSeverity.ERROR, ECMessageType.USER,
			"INSTAGRAM_ID_ERROR", errorBundle, errorBundle, "INSTA0002");
	public static final ECMessage MEDIA_ERROR_RETRY = new ECMessage(ECMessageSeverity.ERROR, ECMessageType.USER,
			"MEDIA_ERROR_RETRY", errorBundle, errorBundle, "INSTA0003");
	public static final ECMessage MEDIA_ERROR_NOT_RETRY = new ECMessage(ECMessageSeverity.ERROR, ECMessageType.USER,
			"MEDIA_ERROR_NOT_RETRY", errorBundle, errorBundle, "INSTA0004");
	public static final ECMessage PUBLISH_MEDIA_ERROR = new ECMessage(ECMessageSeverity.ERROR, ECMessageType.USER,
			"PUBLISH_MEDIA_ERROR", errorBundle, errorBundle, "INSTA0005");
	public static final ECMessage QUOTA_USAGE_ERROR = new ECMessage(ECMessageSeverity.ERROR, ECMessageType.USER,
			"QUOTA_USAGE_ERROR", errorBundle, errorBundle, "INSTA0006");
	public static final ECMessage ALT_MEDIA_ERROR = new ECMessage(ECMessageSeverity.ERROR, ECMessageType.USER,
			"ALT_MEDIA_ERROR", errorBundle, errorBundle, "INSTA0007");
}
