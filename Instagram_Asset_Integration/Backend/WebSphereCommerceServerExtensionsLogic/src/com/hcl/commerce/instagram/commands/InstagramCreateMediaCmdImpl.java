package com.hcl.commerce.instagram.commands;
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
import com.hcl.commerce.instagram.constants.InstagramConstants;
import com.hcl.commerce.instagram.ras.InstagramECMessage;
import com.hcl.commerce.instagram.utils.InstagramUtil;
import com.ibm.commerce.command.ControllerCommandImpl;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringUtils;
import com.ibm.commerce.datatype.TypedProperty;
import com.ibm.commerce.exception.ECApplicationException;
import com.ibm.commerce.exception.ECException;
import com.ibm.commerce.exception.ECSystemException;
import com.ibm.commerce.foundation.internal.server.services.registry.StoreConfigurationRegistry;
import com.ibm.commerce.foundation.logging.LoggingHelper;
import com.ibm.commerce.ras.ECMessage;

public class InstagramCreateMediaCmdImpl extends ControllerCommandImpl implements InstagramCreateMediaCmd {

	/**
	 * Execute this command's business logic. This method is called by its
	 * associated Action class.
	 */
	@Override
	public void performExecute() throws ECException {
		final String methodName = "performExecute";
		if (LoggingHelper.isEntryExitTraceEnabled(LOGGER))
			LOGGER.entering(CLASS_NAME, methodName);

		responseProperties = new TypedProperty();
		requestProperties = getCommandContext().getRequestProperties();
		LOGGER.logp(Level.INFO, CLASS_NAME, methodName, "requestProperties::" + requestProperties);
		Map<String, Object> apiResponseMap = new HashMap<String, Object>();
		try {
			accessToken = requestProperties.getString(ACCESS_TOKEN, "");
			if (StringUtils.isEmpty(accessToken)) {
				throw new ECApplicationException(ECMessage._ERR_MISSING_CMD_PARAMETER, CLASS_NAME, methodName,
						new Object[] { ACCESS_TOKEN });
			}
			if (requestProperties.containsKey(InstagramConstants.FBID)
					&& !StringUtils.isEmpty(requestProperties.getString(InstagramConstants.FBID))) {
				fbPageId = requestProperties.getString(InstagramConstants.FBID);
			} else {
				fbPageId = instagramUtil.getFacebookPageId(accessToken);
			}
			if (requestProperties.containsKey(InstagramConstants.INSTAID)
					&& !StringUtils.isEmpty(requestProperties.getString(InstagramConstants.INSTAID))) {
				instaAccId = requestProperties.getString(InstagramConstants.INSTAID);
			} else {
				instaAccId = instagramUtil.getInstagramAccountId(fbPageId, accessToken);
			}
			instagramUtil.getInstagramQuotaUsage(instaAccId, accessToken);
			Map<String, Object> product = new HashMap<String, Object>();
			if (!requestProperties.containsKey(PRODUCT_TO_POST)) {
				throw new ECApplicationException(ECMessage._ERR_MISSING_CMD_PARAMETER, CLASS_NAME, methodName,
						new Object[] { PRODUCT_TO_POST });
			}
			product = (Map<String, Object>) requestProperties.get(PRODUCT_TO_POST);
			String caption = instagramUtil.createCaption(product);
			LOGGER.logp(Level.INFO, CLASS_NAME, methodName, "CAPTION::" + caption);
			if (!product.containsKey(HERO_MEDIA_URL) || StringUtils.isEmpty(HERO_MEDIA_URL)) {
				throw new ECApplicationException(ECMessage._ERR_MISSING_CMD_PARAMETER, CLASS_NAME, methodName,
						new Object[] { HERO_MEDIA_URL });
			}
			if (!product.containsKey(HERO_MEDIA_TYPE) || StringUtils.isEmpty(HERO_MEDIA_TYPE)) {
				throw new ECApplicationException(ECMessage._ERR_MISSING_CMD_PARAMETER, CLASS_NAME, methodName,
						new Object[] { HERO_MEDIA_TYPE });
			}
			String containerId = "";
			if (product.containsKey(ALTERNATE_MEDIA) && !StringUtils.isEmpty(ALTERNATE_MEDIA)) {
				List altMedia = new ArrayList();
				altMedia = (List) product.get(ALTERNATE_MEDIA);
				if(altMedia.size() > Integer.valueOf(storeConfigurationRegistry.getValue(0, INSTAGRAM_ALT_LIMIT))) {
					throw new ECApplicationException(InstagramECMessage.ALT_MEDIA_ERROR, CLASS_NAME, methodName,
							new Object[] { storeConfigurationRegistry.getValue(0, INSTAGRAM_ALT_LIMIT) });
				}
				apiResponseMap = instagramUtil.createMedia(accessToken, instaAccId,
						product.get(HERO_MEDIA_URL).toString(), product.get(HERO_MEDIA_TYPE).toString().toUpperCase(),
						InstagramConstants.TRUE, "", caption);
				if (apiResponseMap.get(STATUS).equals(InstagramConstants.SUCCESS)) {
					containerId = apiResponseMap.get(CONTAINERID).toString();
					LOGGER.logp(Level.INFO, CLASS_NAME, methodName, "ALTERNATE MEDIA::" + altMedia);
					for (int j = 0; j < altMedia.size(); j++) {
						Map<String, Object> media = new HashMap<String, Object>();
						media = (Map) altMedia.get(j);
						apiResponseMap = instagramUtil.createMedia(accessToken, instaAccId,
								media.get(MEDIA_URL).toString(), media.get(MEDIA_TYPE).toString().toUpperCase(),
								InstagramConstants.TRUE, "", caption);
						if (apiResponseMap.get(STATUS).equals(InstagramConstants.SUCCESS)) {
							containerId = containerId + "," + apiResponseMap.get(CONTAINERID).toString();
						} else {
							break;
						}
					}
					if (apiResponseMap.get(STATUS).equals(InstagramConstants.SUCCESS)) {
						LOGGER.logp(Level.INFO, CLASS_NAME, methodName, "CHILDREN::" + containerId);
						apiResponseMap = instagramUtil.createMedia(accessToken, instaAccId, "",
								InstagramConstants.CAROUSEL, InstagramConstants.TRUE, containerId, caption);
						LOGGER.logp(Level.INFO, CLASS_NAME, methodName, "CAROUSEL IDS::" + containerId);
					}
				}
			} else {
				apiResponseMap = instagramUtil.createMedia(accessToken, instaAccId,
						product.get(HERO_MEDIA_URL).toString(), product.get(HERO_MEDIA_TYPE).toString().toUpperCase(),
						InstagramConstants.FALSE, "", caption);
			}
			apiResponseMap.put(InstagramConstants.FBID, fbPageId);
			apiResponseMap.put(InstagramConstants.INSTAID, instaAccId);
			responseProperties.putAll(apiResponseMap);
			LOGGER.logp(Level.INFO, CLASS_NAME, methodName, "responseProperties::" + responseProperties);
			setResponseProperties(responseProperties);
		} catch (ECApplicationException e) {
			LOGGER.logp(Level.SEVERE, CLASS_NAME, methodName, e.toString());
			throw e;
		} catch (Exception e) {
			LOGGER.logp(Level.SEVERE, CLASS_NAME, methodName, e.toString());
			throw new ECSystemException(ECMessage._ERR_GENERIC, CLASS_NAME, methodName, e.getMessage());
		}
		if (LoggingHelper.isEntryExitTraceEnabled(LOGGER))
			LOGGER.exiting(CLASS_NAME, methodName);
	}

	private static final long serialVersionUID = 1L;
	private static final String CLASS_NAME = "InstagramCreateMediaCmdImpl";
	private static final Logger LOGGER = Logger.getLogger(CLASS_NAME);
	private TypedProperty requestProperties = new TypedProperty();
	InstagramUtil instagramUtil = new InstagramUtil();
	StoreConfigurationRegistry storeConfigurationRegistry = StoreConfigurationRegistry.getSingleton();
	private String accessToken = "";
	private String fbPageId = "";
	private String instaAccId = "";
	private String STATUS = "status";
	private String CONTAINERID = "containerId";
	private String PRODUCT_TO_POST = "product";
	private String ALTERNATE_MEDIA = "alternateMedia";
	private String HERO_MEDIA_URL = "heroMediaURL";
	private String HERO_MEDIA_TYPE = "heroMediaType";
	private String MEDIA_URL = "mediaURL";
	private String MEDIA_TYPE = "mediaType";
	private String INSTAGRAM_ALT_LIMIT = "instagram.alt.image.limit";
	private String ACCESS_TOKEN = "accessToken";
}
