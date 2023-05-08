package com.hcl.commerce.instagram.utils;
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
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HttpsURLConnection;
import org.apache.commons.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import com.hcl.commerce.instagram.constants.InstagramConstants;
import com.hcl.commerce.instagram.ras.InstagramECMessage;
import com.ibm.commerce.exception.ECApplicationException;
import com.ibm.commerce.foundation.internal.server.services.registry.StoreConfigurationRegistry;
import com.ibm.commerce.foundation.logging.LoggingHelper;
import com.ibm.commerce.ras.ECMessage;

public class InstagramUtil {

	/*
	 * This method is used to create containers using Instagram API, for the media
	 * to be posted on Instagram
	 */
	public Map<String, Object> createMedia(String accessToken, String instaAccId, String mediaURL, String mediaType,
			String isCarouselItem, String children, String caption) throws Exception {
		final String methodName = "createMedia";
		if (LoggingHelper.isEntryExitTraceEnabled(LOGGER))
			LOGGER.entering(CLASS_NAME, methodName);

		Map<String, Object> apiResponseMap = new HashMap<String, Object>();
		String apiURL = storeConfigurationRegistry.getValue(0, INSTAGRAM_API) + instaAccId
				+ storeConfigurationRegistry.getValue(0, INSTAGRAM_MEDIA_API) + URL_ACCESS_TOKEN + accessToken;
		if (isCarouselItem.equalsIgnoreCase(InstagramConstants.TRUE)) {
			if (InstagramConstants.CAROUSEL.equalsIgnoreCase(mediaType)) {
				apiURL = apiURL + URL_CAPTION + caption + URL_MEDIA_TYPE + mediaType + URL_CHILDREN + children;
			} else {
				if (InstagramConstants.VIDEO.equalsIgnoreCase(mediaType)) {
					apiURL = apiURL + URL_VIDEO + mediaURL + URL_MEDIA_TYPE + mediaType + URL_IS_CAROUSEL
							+ isCarouselItem;
				} else {
					apiURL = apiURL + URL_IMAGE + mediaURL + URL_IS_CAROUSEL + isCarouselItem;
				}
			}
		} else {
			if (InstagramConstants.VIDEO.equalsIgnoreCase(mediaType)) {
				apiURL = apiURL + URL_VIDEO + mediaURL + URL_MEDIA_TYPE + mediaType + URL_CAPTION + caption;
			} else {
				apiURL = apiURL + URL_IMAGE + mediaURL + URL_CAPTION + caption;
			}
		}
		URL url = new URL(apiURL);
		LOGGER.logp(Level.INFO, CLASS_NAME, methodName, "URL::" + url);
		HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
		con.setRequestMethod("POST");
		con.setRequestProperty("Content-Type", "application/json");
		int responseCode = con.getResponseCode();
		LOGGER.logp(Level.INFO, CLASS_NAME, methodName, "RESPONSE CODE::" + responseCode);
		BufferedReader in;
		if (responseCode < HttpsURLConnection.HTTP_BAD_REQUEST) {
			in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		} else {
			in = new BufferedReader(new InputStreamReader(con.getErrorStream()));
		}
		String inputLine;
		StringBuffer response = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		String result = response.toString();
		LOGGER.logp(Level.INFO, CLASS_NAME, methodName, "RESULT::" + result);
		JSONObject map = new JSONObject(result);
		LOGGER.logp(Level.INFO, CLASS_NAME, methodName, "MAP::" + map);
		if (responseCode == 200) {
			if (map.containsKey(ID)) {
				apiResponseMap.put(STATUS, InstagramConstants.SUCCESS);
				apiResponseMap.put(CONTAINERID, map.get(ID).toString());
			}
		} else {
			if (map.containsKey(ERROR)) {
				map = (JSONObject) map.get(ERROR);
				String errorcode = "";
				if (map.containsKey(ERROR_SUBCODE) && !StringUtils.isEmpty(map.getString(ERROR_SUBCODE))) {
					errorcode = map.getString(ERROR_SUBCODE);
				}
				String isretry = checkErrorCode(errorcode, InstagramConstants.MEDIA);
				if (InstagramConstants.TRUE.equals(isretry)) {
					throw new ECApplicationException(InstagramECMessage.MEDIA_ERROR_RETRY, CLASS_NAME, methodName,
							new Object[] { map.getString(MESSAGE) });
				} else {
					throw new ECApplicationException(InstagramECMessage.MEDIA_ERROR_NOT_RETRY, CLASS_NAME, methodName,
							new Object[] { map.getString(MESSAGE) });
				}
			} else {
				throw new ECApplicationException(InstagramECMessage.MEDIA_ERROR_NOT_RETRY, CLASS_NAME, methodName,
						new Object[] { "" });
			}
		}
		LOGGER.logp(Level.INFO, CLASS_NAME, methodName, "RESPONE MAP::" + apiResponseMap);
		if (LoggingHelper.isEntryExitTraceEnabled(LOGGER))
			LOGGER.exiting(CLASS_NAME, methodName);
		return apiResponseMap;
	}

	/*
	 * This method is used to publish containers using Instagram API, for the media
	 * to be posted on Instagram
	 */
	public Map<String, Object> publishMedia(String accessToken, String instaAccId, String containerId)
			throws Exception {
		final String methodName = "publishMedia";
		if (LoggingHelper.isEntryExitTraceEnabled(LOGGER))
			LOGGER.entering(CLASS_NAME, methodName);
		Map<String, Object> apiResponseMap = new HashMap<String, Object>();
		String apiURL = storeConfigurationRegistry.getValue(0, INSTAGRAM_API) + instaAccId
				+ storeConfigurationRegistry.getValue(0, INSTAGRAM_PUBLISH_API) + URL_ACCESS_TOKEN + accessToken;
		apiURL = apiURL + URL_CREATION_ID + containerId;
		URL url = new URL(apiURL);
		LOGGER.logp(Level.INFO, CLASS_NAME, methodName, "URL::" + url);
		HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
		con.setRequestMethod("POST");
		con.setRequestProperty("Content-Type", "application/json");
		int responseCode = con.getResponseCode();
		LOGGER.logp(Level.INFO, CLASS_NAME, methodName, "RESPONSE CODE::" + responseCode);
		BufferedReader in;
		if (responseCode < HttpsURLConnection.HTTP_BAD_REQUEST) {
			in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		} else {
			in = new BufferedReader(new InputStreamReader(con.getErrorStream()));
		}
		String inputLine;
		StringBuffer response = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		String result = response.toString();
		LOGGER.logp(Level.INFO, CLASS_NAME, methodName, "RESULT::" + result);
		JSONObject map = new JSONObject(result);
		LOGGER.logp(Level.INFO, CLASS_NAME, methodName, "MAP::" + map);
		if (responseCode == 200) {
			if (map.containsKey(ID)) {
				apiResponseMap.put(STATUS, InstagramConstants.SUCCESS);
				apiResponseMap.put(CREATIONID, map.get(ID).toString());
			}
		} else {
			if (map.containsKey(ERROR)) {
				map = (JSONObject) map.get(ERROR);
				throw new ECApplicationException(InstagramECMessage.PUBLISH_MEDIA_ERROR, CLASS_NAME, methodName,
						new Object[] { map.getString(MESSAGE) });
			} else {
				throw new ECApplicationException(InstagramECMessage.PUBLISH_MEDIA_ERROR, CLASS_NAME, methodName,
						new Object[] { "" });
			}
		}
		LOGGER.logp(Level.INFO, CLASS_NAME, methodName, "RESPONE MAP::" + apiResponseMap);
		if (LoggingHelper.isEntryExitTraceEnabled(LOGGER))
			LOGGER.exiting(CLASS_NAME, methodName);
		return apiResponseMap;
	}

	/*
	 * This method is used to fetch Facebook Page Id
	 */
	public String getFacebookPageId(String accessToken) throws Exception {
		final String methodName = "getFacebookPageId";
		if (LoggingHelper.isEntryExitTraceEnabled(LOGGER))
			LOGGER.entering(CLASS_NAME, methodName);
		String fbPageId = "";
		URL url = new URL(storeConfigurationRegistry.getValue(0, INSTAGRAM_API)
				+ storeConfigurationRegistry.getValue(0, INSTAGRAM_ACCOUNTS_API) + URL_ACCESS_TOKEN + accessToken);
		LOGGER.logp(Level.INFO, CLASS_NAME, methodName, "URL::" + url);
		HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
		con.setRequestMethod("GET");
		con.setRequestProperty("Content-Type", "application/json");
		int responseCode = con.getResponseCode();
		LOGGER.logp(Level.INFO, CLASS_NAME, methodName, "RESPONSE CODE::" + responseCode);
		BufferedReader in;
		if (responseCode < HttpsURLConnection.HTTP_BAD_REQUEST) {
			in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		} else {
			in = new BufferedReader(new InputStreamReader(con.getErrorStream()));
		}
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		String result = response.toString();
		LOGGER.logp(Level.INFO, CLASS_NAME, methodName, result);
		JSONObject map = new JSONObject(result);
		LOGGER.logp(Level.INFO, CLASS_NAME, methodName, "MAP::" + map);
		if (responseCode == 200) {
			if (map.containsKey(DATA)) {
				List list = (List) map.get(DATA);
				if (list.size() > 0) {
					map = (JSONObject) list.get(0);
					fbPageId = map.getString(ID);
				}
			}
		} else {
			if (map.containsKey(ERROR)) {
				map = (JSONObject) map.get(ERROR);
				throw new ECApplicationException(InstagramECMessage.FACEBOOK_ID_ERROR, CLASS_NAME, methodName,
						new Object[] { map.getString(MESSAGE) });
			} else {
				throw new ECApplicationException(InstagramECMessage.FACEBOOK_ID_ERROR, CLASS_NAME, methodName,
						new Object[] { "" });
			}
		}
		LOGGER.logp(Level.INFO, CLASS_NAME, methodName, "FACEBOOK ID::" + fbPageId);
		if (LoggingHelper.isEntryExitTraceEnabled(LOGGER))
			LOGGER.exiting(CLASS_NAME, methodName);
		return fbPageId;
	}

	/*
	 * This method is used to fetch Instagram business account Id
	 */
	public String getInstagramAccountId(String fbPageId, String accessToken) throws Exception {
		final String methodName = "getInstagramAccountId";
		if (LoggingHelper.isEntryExitTraceEnabled(LOGGER))
			LOGGER.entering(CLASS_NAME, methodName);
		String instaAccId = "";
		URL url = new URL(storeConfigurationRegistry.getValue(0, INSTAGRAM_API) + fbPageId + URL_ACCESS_TOKEN
				+ accessToken + storeConfigurationRegistry.getValue(0, INSTAGRAM_INSTA_ID_API));
		LOGGER.logp(Level.INFO, CLASS_NAME, methodName, "URL::" + url);
		HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
		con.setRequestMethod("GET");
		con.setRequestProperty("Content-Type", "application/json");
		int responseCode = con.getResponseCode();
		LOGGER.logp(Level.INFO, CLASS_NAME, methodName, "RESPONSE CODE::" + responseCode);
		BufferedReader in;
		if (responseCode < HttpsURLConnection.HTTP_BAD_REQUEST) {
			in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		} else {
			in = new BufferedReader(new InputStreamReader(con.getErrorStream()));
		}
		String inputLine;
		StringBuffer response = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		String result = response.toString();
		LOGGER.logp(Level.INFO, CLASS_NAME, methodName, result);
		JSONObject map = new JSONObject(result);
		LOGGER.logp(Level.INFO, CLASS_NAME, methodName, "MAP::" + map);
		if (responseCode == 200) {
			if (map.containsKey(INSTAGRAM_BUSINESS_ACCOUNT)) {
				map = (JSONObject) map.get(INSTAGRAM_BUSINESS_ACCOUNT);
				instaAccId = map.getString(ID);
			}
		} else {
			if (map.containsKey(ERROR)) {
				map = (JSONObject) map.get(ERROR);
				throw new ECApplicationException(InstagramECMessage.INSTAGRAM_ID_ERROR, CLASS_NAME, methodName,
						new Object[] { map.getString(MESSAGE) });
			} else {
				throw new ECApplicationException(InstagramECMessage.INSTAGRAM_ID_ERROR, CLASS_NAME, methodName,
						new Object[] { "" });
			}
		}
		LOGGER.logp(Level.INFO, CLASS_NAME, methodName, "INSTAGRAM ID::" + instaAccId);
		if (LoggingHelper.isEntryExitTraceEnabled(LOGGER))
			LOGGER.exiting(CLASS_NAME, methodName);
		return instaAccId;
	}

	/*
	 * This method is used to fetch Instagram content publishing limit
	 */
	public void getInstagramQuotaUsage(String instaAccId, String accessToken) throws Exception {
		final String methodName = "getInstagramQuotaUsage";
		if (LoggingHelper.isEntryExitTraceEnabled(LOGGER))
			LOGGER.entering(CLASS_NAME, methodName);
		URL url = new URL(storeConfigurationRegistry.getValue(0, INSTAGRAM_API) + instaAccId
				+ storeConfigurationRegistry.getValue(0, INSTAGRAM_QUOTA_USAGE_API) + URL_ACCESS_TOKEN + accessToken
				+ storeConfigurationRegistry.getValue(0, INSTAGRAM_QUOTA_USAGE_API_FIELDS));
		LOGGER.logp(Level.INFO, CLASS_NAME, methodName, "URL::" + url);
		HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
		con.setRequestMethod("GET");
		con.setRequestProperty("Content-Type", "application/json");
		int responseCode = con.getResponseCode();
		LOGGER.logp(Level.INFO, CLASS_NAME, methodName, "RESPONSE CODE::" + responseCode);
		BufferedReader in;
		if (responseCode < HttpsURLConnection.HTTP_BAD_REQUEST) {
			in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		} else {
			in = new BufferedReader(new InputStreamReader(con.getErrorStream()));
		}
		String inputLine;
		StringBuffer response = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		String result = response.toString();
		LOGGER.logp(Level.INFO, CLASS_NAME, methodName, result);
		JSONObject map = new JSONObject(result);
		LOGGER.logp(Level.INFO, CLASS_NAME, methodName, "MAP::" + map);
		if (responseCode == 200) {
			if (map.containsKey(DATA)) {
				List list = (List) map.get(DATA);
				map = (JSONObject) list.get(0);
				int quota_usage = 0;
				int quota_total = 0;
				if (map.containsKey(QUOTA_USAGE) && !StringUtils.isEmpty(map.getString(QUOTA_USAGE))) {
					quota_usage = Integer.valueOf(map.getString(QUOTA_USAGE));
				}
				if (map.containsKey(CONFIG)) {
					map = (JSONObject) map.get(CONFIG);
					if (map.containsKey(QUOTA_TOTAL) && !StringUtils.isEmpty(map.getString(QUOTA_TOTAL))) {
						quota_total = Integer.valueOf(map.getString(QUOTA_TOTAL));
					}
				}
				if (quota_usage < Integer.min(quota_total,
						Integer.valueOf(storeConfigurationRegistry.getValue(0, INSTAGRAM_QUOTA_LIMIT)))) {
				} else {
					throw new ECApplicationException(InstagramECMessage.QUOTA_USAGE_ERROR, CLASS_NAME, methodName);
				}
			}
		}
		if (LoggingHelper.isEntryExitTraceEnabled(LOGGER))
			LOGGER.exiting(CLASS_NAME, methodName);
	}

	/*
	 * Checks if Instagram errors can be retried by user or not.
	 */
	public String checkErrorCode(String code, String api) {
		final String methodName = "getInstagramAccountId";
		if (LoggingHelper.isEntryExitTraceEnabled(LOGGER))
			LOGGER.entering(CLASS_NAME, methodName);
		String isretry = InstagramConstants.FALSE;
		if (!StringUtils.isEmpty(code) && InstagramConstants.MEDIA.equalsIgnoreCase(api)) {
			if (InstagramConstants.MEDIA_ERROR_CODE_RETRY.contains(code))
				isretry = InstagramConstants.TRUE;
			else if (InstagramConstants.MEDIA_ERROR_CODE_NOT_RETRY.contains(code))
				isretry = InstagramConstants.FALSE;
			else
				isretry = InstagramConstants.FALSE;
		}
		if (LoggingHelper.isEntryExitTraceEnabled(LOGGER))
			LOGGER.exiting(CLASS_NAME, methodName);
		return isretry;
	}

	/*
	 * Creates caption for the Instagram post using the product and user details
	 */
	public String createCaption(Map<String, Object> product) throws Exception {
		final String methodName = "createCaption";
		if (LoggingHelper.isEntryExitTraceEnabled(LOGGER))
			LOGGER.entering(CLASS_NAME, methodName);
		String caption = "";
		if (product.containsKey(TITLE) && product.get(TITLE) != null) {
			caption = product.get(TITLE).toString();
		} else {
			throw new ECApplicationException(ECMessage._ERR_MISSING_CMD_PARAMETER, CLASS_NAME, methodName,
					new Object[] { TITLE });
		}
		if (product.containsKey(TEXT_IN_POST) && product.get(TEXT_IN_POST) != null) {
			caption = caption + "\n\n" + product.get(TEXT_IN_POST).toString();
		}
		if (product.containsKey(HASHTAGS) && product.get(HASHTAGS) != null) {
			caption = caption + "\n\n" + product.get(HASHTAGS).toString();
		}
		LOGGER.logp(Level.INFO, CLASS_NAME, methodName, "caption---" + caption);
		caption = URLEncoder.encode(caption);

		if (LoggingHelper.isEntryExitTraceEnabled(LOGGER))
			LOGGER.exiting(CLASS_NAME, methodName);
		return caption;
	}

	private static final String CLASS_NAME = "InstagramUtil";
	private static final Logger LOGGER = Logger.getLogger(CLASS_NAME);
	StoreConfigurationRegistry storeConfigurationRegistry = StoreConfigurationRegistry.getSingleton();
	private String STATUS = "status";
	private String ERROR = "error";
	private String ERROR_SUBCODE = "error_subcode";
	private String MESSAGE = "message";
	private String CONTAINERID = "containerId";
	private String MEDIAURL = "mediaURL";
	private String DATA = "data";
	private String INSTAGRAM_BUSINESS_ACCOUNT = "instagram_business_account";
	private String ID = "id";
	private String QUOTA_USAGE = "quota_usage";
	private String ERROR_CODE = "errorCode";
	private String API_ERROR_MESSAGE = "apiErrorMessage";
	private String ERROR_MESSAGE = "errorMessage";
	private String TITLE = "title";
	private String TEXT_IN_POST = "description";
	private String HASHTAGS = "hashTags";
	private String URL_CAPTION = "&caption=";
	private String URL_MEDIA_TYPE = "&media_type=";
	private String URL_VIDEO = "&video_url=";
	private String URL_IMAGE = "&image_url=";
	private String URL_CHILDREN = "&children=";
	private String URL_IS_CAROUSEL = "&is_carousel_item=";
	private String URL_CREATION_ID = "&creation_id=";
	private String URL_ACCESS_TOKEN = "?access_token=";
	private String CREATIONID = "creationId";
	private String CONFIG = "config";
	private String QUOTA_TOTAL = "quota_total";
	private String INSTAGRAM_API = "instagram.api";
	private String INSTAGRAM_MEDIA_API = "instagram.media.api";
	private String INSTAGRAM_PUBLISH_API = "instagram.publish.api";
	private String INSTAGRAM_ACCOUNTS_API = "instagram.accounts.api";
	private String INSTAGRAM_INSTA_ID_API = "instagram.insta.id.api";
	private String INSTAGRAM_QUOTA_USAGE_API = "instagram.quota.usage.api";
	private String INSTAGRAM_QUOTA_USAGE_API_FIELDS = "instagram.quota.usage.api.fields";
	private String INSTAGRAM_QUOTA_LIMIT = "instagram.quota.limit";
}
