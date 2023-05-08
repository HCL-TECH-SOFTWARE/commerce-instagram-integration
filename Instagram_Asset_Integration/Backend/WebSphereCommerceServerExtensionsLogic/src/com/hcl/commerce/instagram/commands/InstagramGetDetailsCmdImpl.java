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
import com.ibm.commerce.command.ControllerCommandImpl;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.ibm.commerce.datatype.TypedProperty;
import com.ibm.commerce.exception.ECException;
import com.ibm.commerce.exception.ECSystemException;
import com.ibm.commerce.foundation.logging.LoggingHelper;
import com.ibm.commerce.ras.ECMessage;
import com.ibm.commerce.foundation.internal.server.services.registry.StoreConfigurationRegistry;

public class InstagramGetDetailsCmdImpl extends ControllerCommandImpl implements InstagramGetDetailsCmd {

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
		Map<String, Object> details = new HashMap<String, Object>();
		try {
			details.put("appId", storeConfigurationRegistry.getValue(0, INSTAGRAM_APP_ID));
			details.put("appVersion", storeConfigurationRegistry.getValue(0, INSTAGRAM_API_VERSION));
			responseProperties.putAll(details);
		} catch (Exception e) {
			LOGGER.logp(Level.SEVERE, CLASS_NAME, methodName, e.toString());
			throw new ECSystemException(ECMessage._ERR_GENERIC, CLASS_NAME, methodName, e.getMessage());
		}
		if (LoggingHelper.isEntryExitTraceEnabled(LOGGER))
			LOGGER.exiting(CLASS_NAME, methodName);
	}

	private static final long serialVersionUID = 1L;
	private static final String CLASS_NAME = "InstagramGetDetailsCmdImpl";
	private static final Logger LOGGER = Logger.getLogger(CLASS_NAME);
	private TypedProperty requestProperties = new TypedProperty();
	StoreConfigurationRegistry storeConfigurationRegistry = StoreConfigurationRegistry.getSingleton();
	private String INSTAGRAM_APP_ID = "instagram.app.id";
	private String INSTAGRAM_API_VERSION = "instagram.api.version";
}
