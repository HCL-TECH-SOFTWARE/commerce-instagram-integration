package com.hcl.commerce.instagram.handler;
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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import com.ibm.commerce.datatype.TypedProperty;
import com.ibm.commerce.foundation.logging.LoggingHelper;
import com.ibm.commerce.rest.classic.core.AbstractConfigBasedClassicHandler;
import com.ibm.commerce.rest.javadoc.ParameterDescription;
import com.ibm.commerce.rest.javadoc.ResponseSchema;
import com.ibm.commerce.rest.javadoc.ResponseType;
import com.ibm.commerce.rest.javadoc.StoreIdProvider;

@Path("store/{storeId}/instagram")
public class InstagramCreateMediaHandler extends AbstractConfigBasedClassicHandler {

	private static final String CLASSNAME = InstagramCreateMediaHandler.class.getName();
	private static final Logger LOGGER = LoggingHelper.getLogger(InstagramCreateMediaHandler.class);
	private static final String RESOURCE_NAME = "instagram";
	private static final String INSTAGRAM_CREATE_CMD = "com.hcl.commerce.instagram.commands.InstagramCreateMediaCmd";
	private static final String INSTAGRAM_PUBLISH_CMD = "com.hcl.commerce.instagram.commands.InstagramPublishMediaCmd";
	private static final String INSTAGRAM_DETAILS_CMD = "com.hcl.commerce.instagram.commands.InstagramGetDetailsCmd";

	@Override
	public String getResourceName() {
		return RESOURCE_NAME;
	}

	/*
	 * This API is used to create media containers for the media to be posted on
	 * Instagram.
	 */
	@POST
	@Path("create_media")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_XHTML_XML,
			MediaType.APPLICATION_ATOM_XML })
	@ResponseSchema(parameterGroup = RESOURCE_NAME, responseCodes = {
			@com.ibm.commerce.rest.javadoc.ResponseCode(code = 204, reason = "The requested completed successfully. No content is returned in the response."),
			@com.ibm.commerce.rest.javadoc.ResponseCode(code = 400, reason = "Bad request. Some of the inputs provided to the request aren't valid."),
			@com.ibm.commerce.rest.javadoc.ResponseCode(code = 401, reason = "Not authenticated. The user session isn't valid."),
			@com.ibm.commerce.rest.javadoc.ResponseCode(code = 403, reason = "The user isn't authorized to perform the specified request."),
			@com.ibm.commerce.rest.javadoc.ResponseCode(code = 404, reason = "The specified resource couldn't be found."),
			@com.ibm.commerce.rest.javadoc.ResponseCode(code = 500, reason = "Internal server error. Additional details will be contained on the server logs.") })
	public Response createContainerForMedia(
			@PathParam("storeId") @ParameterDescription(description = "The store identifier.", valueProviderClass = StoreIdProvider.class, required = true) String storeId,
			@QueryParam(value = "responseFormat") @ParameterDescription(description = "The response format. If the request has an input body, that body must also use the format specified in \"responseFormat\". Valid values include \"json\" and \"xml\" without the quotes. If the responseFormat isn't specified, the \"accept\" HTTP header shall be used to determine the format of the response. If the \"accept\" HTTP header isn't specified as well, the default response format shall be in json.", valueProviderClass = ResponseType.class) String responseFormat)
			throws Exception {
		String methodName = "createContainerForMedia";
		boolean entryExitTraceEnabled = LoggingHelper.isEntryExitTraceEnabled(LOGGER);
		Object[] objArr;
		if (entryExitTraceEnabled) {
			objArr = new Object[] { responseFormat, storeId };
			LOGGER.entering(CLASSNAME, methodName, objArr);
		}
		Response response = null;
		try {
			TypedProperty requestProperties = initializeRequestPropertiesFromRequestMap(responseFormat);
			LOGGER.logp(Level.INFO, CLASSNAME, methodName, "requestProperties::" + requestProperties);
			if (responseFormat == null) {
				responseFormat = "application/json";
			}
			response = executeControllerCommandWithContext(storeId, INSTAGRAM_CREATE_CMD, requestProperties,
					responseFormat);

		} catch (Exception ex) {
			response = this.handleException(responseFormat, ex, methodName);
		}
		if (entryExitTraceEnabled) {
			LOGGER.exiting(CLASSNAME, methodName, response);
		}
		return response;
	}

	/*
	 * This API is used to publish media container for the media to be posted on
	 * Instagram.
	 */
	@POST
	@Path("publish_media")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_XHTML_XML,
			MediaType.APPLICATION_ATOM_XML })
	@ResponseSchema(parameterGroup = RESOURCE_NAME, responseCodes = {
			@com.ibm.commerce.rest.javadoc.ResponseCode(code = 204, reason = "The requested completed successfully. No content is returned in the response."),
			@com.ibm.commerce.rest.javadoc.ResponseCode(code = 400, reason = "Bad request. Some of the inputs provided to the request aren't valid."),
			@com.ibm.commerce.rest.javadoc.ResponseCode(code = 401, reason = "Not authenticated. The user session isn't valid."),
			@com.ibm.commerce.rest.javadoc.ResponseCode(code = 403, reason = "The user isn't authorized to perform the specified request."),
			@com.ibm.commerce.rest.javadoc.ResponseCode(code = 404, reason = "The specified resource couldn't be found."),
			@com.ibm.commerce.rest.javadoc.ResponseCode(code = 500, reason = "Internal server error. Additional details will be contained on the server logs.") })
	public Response publishContainerForMedia(
			@PathParam("storeId") @ParameterDescription(description = "The store identifier.", valueProviderClass = StoreIdProvider.class, required = true) String storeId,
			@QueryParam(value = "responseFormat") @ParameterDescription(description = "The response format. If the request has an input body, that body must also use the format specified in \"responseFormat\". Valid values include \"json\" and \"xml\" without the quotes. If the responseFormat isn't specified, the \"accept\" HTTP header shall be used to determine the format of the response. If the \"accept\" HTTP header isn't specified as well, the default response format shall be in json.", valueProviderClass = ResponseType.class) String responseFormat)
			throws Exception {
		String methodName = "publishContainerForMedia";
		boolean entryExitTraceEnabled = LoggingHelper.isEntryExitTraceEnabled(LOGGER);
		Object[] objArr;
		if (entryExitTraceEnabled) {
			objArr = new Object[] { responseFormat, storeId };
			LOGGER.entering(CLASSNAME, methodName, objArr);
		}
		Response response = null;
		try {
			TypedProperty requestProperties = initializeRequestPropertiesFromRequestMap(responseFormat);
			LOGGER.logp(Level.INFO, CLASSNAME, methodName, "requestProperties::" + requestProperties);
			if (responseFormat == null) {
				responseFormat = "application/json";
			}
			response = executeControllerCommandWithContext(storeId, INSTAGRAM_PUBLISH_CMD, requestProperties,
					responseFormat);

		} catch (Exception ex) {
			response = this.handleException(responseFormat, ex, methodName);
		}
		if (entryExitTraceEnabled) {
			LOGGER.exiting(CLASSNAME, methodName, response);
		}
		return response;
	}
	
	/*
	 * This API is provide app id and version.
	 */
	@GET
	@Path("details")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_XHTML_XML,
			MediaType.APPLICATION_ATOM_XML })
	@ResponseSchema(parameterGroup = RESOURCE_NAME, responseCodes = {
			@com.ibm.commerce.rest.javadoc.ResponseCode(code = 204, reason = "The requested completed successfully. No content is returned in the response."),
			@com.ibm.commerce.rest.javadoc.ResponseCode(code = 400, reason = "Bad request. Some of the inputs provided to the request aren't valid."),
			@com.ibm.commerce.rest.javadoc.ResponseCode(code = 401, reason = "Not authenticated. The user session isn't valid."),
			@com.ibm.commerce.rest.javadoc.ResponseCode(code = 403, reason = "The user isn't authorized to perform the specified request."),
			@com.ibm.commerce.rest.javadoc.ResponseCode(code = 404, reason = "The specified resource couldn't be found."),
			@com.ibm.commerce.rest.javadoc.ResponseCode(code = 500, reason = "Internal server error. Additional details will be contained on the server logs.") })
	public Response getDetails(
			@PathParam("storeId") @ParameterDescription(description = "The store identifier.", valueProviderClass = StoreIdProvider.class, required = true) String storeId,
			@QueryParam(value = "responseFormat") @ParameterDescription(description = "The response format. If the request has an input body, that body must also use the format specified in \"responseFormat\". Valid values include \"json\" and \"xml\" without the quotes. If the responseFormat isn't specified, the \"accept\" HTTP header shall be used to determine the format of the response. If the \"accept\" HTTP header isn't specified as well, the default response format shall be in json.", valueProviderClass = ResponseType.class) String responseFormat)
			throws Exception {
		String methodName = "getDetails";
		boolean entryExitTraceEnabled = LoggingHelper.isEntryExitTraceEnabled(LOGGER);
		Object[] objArr;
		if (entryExitTraceEnabled) {
			objArr = new Object[] { responseFormat, storeId };
			LOGGER.entering(CLASSNAME, methodName, objArr);
		}
		Response response = null;
		try {
			TypedProperty requestProperties = initializeRequestPropertiesFromRequestMap(responseFormat);
			LOGGER.logp(Level.INFO, CLASSNAME, methodName, "requestProperties::" + requestProperties);
			if (responseFormat == null) {
				responseFormat = "application/json";
			}
			response = executeControllerCommandWithContext(storeId, INSTAGRAM_DETAILS_CMD, requestProperties,
					responseFormat);

		} catch (Exception ex) {
			response = this.handleException(responseFormat, ex, methodName);
		}
		if (entryExitTraceEnabled) {
			LOGGER.exiting(CLASSNAME, methodName, response);
		}
		return response;
	}
}
