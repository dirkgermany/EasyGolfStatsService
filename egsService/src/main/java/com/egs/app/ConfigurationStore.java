package com.egs.app;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;

import com.egs.app.model.ConfigurationModel;
import com.egs.app.model.entity.ConfigEntity;
import com.egs.app.rest.message.WriteRequest;
import com.egs.exception.CsServiceException;

/**
 * Handles active and non active Tokens
 * 
 * @author dirk
 *
 */
@Controller
@ComponentScan
public class ConfigurationStore {

	@Autowired
	private ConfigurationModel configurationModel;

	public long count() {
		return configurationModel.count();
	}

	public List<ConfigEntity> listConfigurationsSafe(Map<String, String> requestParams, Map<String, String> headers)
			throws CsServiceException {
		String requestorUserIdAsString = requestParams.get("requestorUserId");
		if (null == requestorUserIdAsString) {
			requestorUserIdAsString = headers.get("requestoruserid");
		}
		String rights = requestParams.get("rights");
		if (null == rights) {
			rights = headers.get("rights");
		}

		PermissionCheck.checkRequestedParams(requestorUserIdAsString, rights);
		Long requestorUserId = extractLong(requestorUserIdAsString);

		String userIdAsString = requestParams.get("userId");
		String key = requestParams.get("key");
		String indexAsString = requestParams.get("index");

		if (null == key) {
			throw new CsServiceException(404L, "Cannot search configuration", "Search key is empty");
		}
		Long userId = null;
		if (null != userIdAsString) {
			userId = extractLong(userIdAsString);
		}
		Integer index = null;
		if (null != indexAsString) {
			index = extractInteger(indexAsString);
		}

		List<ConfigEntity> configEntitys = getConfigurations(requestorUserId, userId, key, index);
		if (null == configEntitys) {
			throw new CsServiceException(404L, "Configuration not found", "No match for userId, key, index");
		}

		List<ConfigEntity> returnList = new ArrayList<>();
		Iterator<ConfigEntity> it = configEntitys.iterator();
		while (it.hasNext()) {
			ConfigEntity conf = it.next();
			try {
				PermissionCheck.isReadPermissionSet(requestorUserId, conf.getUserId(), rights);
				returnList.add(conf);
			} catch (CsServiceException cse) {
				// User may not read this configuration value
				// try next entry
			}
		}

		return returnList;
	}

	public List<ConfigEntity> listAllConfigurationsSafe(Map<String, String> headers) throws CsServiceException {
		String requestorUserIdAsString = headers.get("requestoruserid");
		String rights = headers.get("rights");

		PermissionCheck.checkRequestedParams(requestorUserIdAsString, rights);
		Long requestorUserId = extractLong(requestorUserIdAsString);
		PermissionCheck.isReadPermissionSet(requestorUserId, null, rights);

		ArrayList<ConfigEntity> configEntitys = new ArrayList<>();
		configurationModel.findAll().forEach(conf -> {
			configEntitys.add(conf);
		});

		return configEntitys;
	}

	public ConfigEntity createConfigurationSafe(WriteRequest requestBody, Map<String, String> requestParams,
			Map<String, String> headers) throws CsServiceException {

		String requestorUserIdAsString = headers.get("requestoruserid");
		String rights = headers.get("rights");
		PermissionCheck.checkRequestedParams(requestBody, requestorUserIdAsString, rights);
		PermissionCheck.checkRequestedEntity(requestBody.getConfigEntity(), ConfigEntity.class, "");

		Long requestorUserId = extractLong(requestorUserIdAsString);
		PermissionCheck.isWritePermissionSet(requestorUserId, requestBody.getConfigEntity().getUserId(), rights);

		// if autoIndex than ignore listIndex of entity in request
		String autoIndexAsString = requestParams.get("autoIndex");
		Boolean autoIndex = extractBoolean(autoIndexAsString);
		if (autoIndex) {
			List<ConfigEntity> configList = getConfigurations(null, requestBody.getConfigEntity().getUserId(),
					requestBody.getConfigEntity().getConfKey(), null);
			if (null != configList) {
				requestBody.getConfigEntity().setListIndex(configList.size());
			} else {
				requestBody.getConfigEntity().setListIndex(0);
			}
		}

		return createConfiguration(requestBody.getConfigEntity());
	}

	public ConfigEntity updateConfigurationSafe(WriteRequest requestBody, Map<String, String> requestParams,
			Map<String, String> headers) throws CsServiceException {

		String requestorUserIdAsString = headers.get("requestoruserid");
		String rights = headers.get("rights");
		PermissionCheck.checkRequestedParams(requestBody, requestorUserIdAsString, rights);
		PermissionCheck.checkRequestedEntity(requestBody.getConfigEntity(), ConfigEntity.class, "");

		ConfigEntity configurationUpdateData = requestBody.getConfigEntity();
		if (null == configurationUpdateData.getUserId() && null == configurationUpdateData.getConfKey()) {
			throw new CsServiceException(401L, "Cannot update configuration", "UserId and key are empty");
		}
		Long requestorUserId = extractLong(requestorUserIdAsString);
		PermissionCheck.isWritePermissionSet(requestorUserId, configurationUpdateData.getUserId(), rights);

		List<ConfigEntity> configList = getConfigurations(requestorUserId, configurationUpdateData.getUserId(),
				configurationUpdateData.getConfKey(), configurationUpdateData.getListIndex());
		if (null == configList) {
			throw new CsServiceException(404L, "Cannot update configuration", "No matching entity found");
		} else if (configList.size() > 1) {
			throw new CsServiceException(404L, "Cannot update configuration",
					"Result not unique, found more than one entity");
		}
		ConfigEntity entityToDrop = configList.get(0);
		entityToDrop.updateFrom(configurationUpdateData);

		return updateConfiguration(entityToDrop);
	}

	private List<ConfigEntity> getConfigurations(Long requestorUserId, Long userId, String key, Integer index) throws CsServiceException {
		List<ConfigEntity> configList = new ArrayList<>();
		ConfigEntity config = null;

		// USER KEY INDEX
		if (null != userId && null != index) {
			config = configurationModel.find(userId, key, index);
			if (null != config && checkPrivate(requestorUserId, config)) {
				configList.add(config);
			}
		}
		// KEY INDEX
		else if (null != index) {
			config = configurationModel.find(key, index);
			if (null != config && checkPrivate(requestorUserId, config)) {
				configList.add(config);
			}
		}
		// USER KEY
		else if (null != userId) {
			Iterator<ConfigEntity> it =  configurationModel.findList(userId, key).iterator();
			while (it.hasNext()) {
				ConfigEntity nextEntity = it.next();
				if (null != nextEntity && checkPrivate(requestorUserId, nextEntity)) {
					configList.add(nextEntity);
				}
			}
		}
		// KEY
		else {
			Iterator<ConfigEntity> it = configurationModel.findList(key).iterator();
			while (it.hasNext()) {
				ConfigEntity nextEntity = it.next();
				if (null != nextEntity && checkPrivate(requestorUserId, nextEntity)) {
					configList.add(nextEntity);
				}
			}
		}

		if (configList.size() < 1) {
			return null;
		}

		return configList;
	}
	
	private boolean checkPrivate(Long requestorUserId, ConfigEntity config) {
		if (null == requestorUserId && null == config) {
			return true;
		}
		
		if (config.getHidden() && !config.getUserId().equals(requestorUserId)) {
			return false;
		}
		return true;
	}

	/**
	 * Creates a user. Checks if the userName still exists (must be distinct)
	 * 
	 * @param userName
	 * @param password
	 * @param givenName
	 * @param lastName
	 * @return
	 */
	private ConfigEntity createConfiguration(ConfigEntity configContainer) throws CsServiceException {

		if (null == configContainer.getUserId() || null == configContainer.getConfKey()
				|| null == configContainer.getValue()) {
			return null;
		}

		// does the Configuration already exists?
		//
		if (null != getConfigurations(null, configContainer.getUserId(), configContainer.getConfKey(),
				configContainer.getListIndex())) {
			throw new CsServiceException(409L, "Configuration not created",
					"Configuration already exists, try update method");
		}

		try {
			ConfigEntity configEntity = configurationModel.save(configContainer);
			return configEntity;
		} catch (Exception ex) {
			return null;
		}
	}

	private ConfigEntity updateConfiguration(ConfigEntity confUpdateData) throws CsServiceException {

		ConfigEntity storedConfiguration = getConfiguration(confUpdateData.get_id());
		if (null == storedConfiguration) {
			throw new CsServiceException(401L, "Cannot update configuration", "Configuration not found by parameters");
		}
		storedConfiguration.updateFrom(confUpdateData);
		storedConfiguration = configurationModel.save(storedConfiguration);
		if (null == storedConfiguration) {
			throw new CsServiceException(401L, "Configuration not updated",
					"Unknown reason but configuration was not saved");
		}
		return storedConfiguration;
	}

	public void dropConfigurationSafe(Map<String, String> requestParams, Map<String, String> headers)
			throws CsServiceException {
		String requestorUserIdAsString = headers.get("requestoruserid");
		String rights = headers.get("rights");
		PermissionCheck.checkRequestedParams(requestorUserIdAsString, rights);
		Long requestorUserId = extractLong(requestorUserIdAsString);

		String configurationIdAsString = requestParams.get("configurationId");
		if (null == configurationIdAsString) {
			throw new CsServiceException(404L, "Cannot drop configuration", "Configuration id is empty");
		}
		Long configurationId = extractLong(configurationIdAsString);

		ConfigEntity configEntity = getConfiguration(configurationId);
		if (null == configEntity) {
			throw new CsServiceException(404L, "Configuration not found", "No match for userId, key, index");
		}

		PermissionCheck.isDeletePermissionSet(requestorUserId, configEntity.getUserId(), rights);
		dropConfiguration(configEntity.get_id());
	}

	public ConfigEntity getConfiguration(Long _id) {
		Optional<ConfigEntity> optionalConfiguration = configurationModel.findById(_id);
		if (null != optionalConfiguration && optionalConfiguration.isPresent()) {
			ConfigEntity configEntity = optionalConfiguration.get();
			return configEntity;
		}
		return null;
	}

	private void dropConfiguration(Long _id) throws CsServiceException {
		configurationModel.deleteById(_id);
		ConfigEntity deletedConfiguration = getConfiguration(_id);
		if (null != deletedConfiguration) {
			throw new CsServiceException(404L, "Configuration not deleted",
					"Unknown reason but configuration was not deleted");
		}
	}

	private Long extractLong(String longString) throws CsServiceException {
		try {
			return Long.valueOf(longString);
		} catch (Exception e) {
			throw new CsServiceException(500L, "Extraction Long from String failed",
					"Parameter is required but null or does not represent a Long value");
		}
	}

	private Integer extractInteger(String integerString) throws CsServiceException {
		try {
			return Integer.valueOf(integerString);
		} catch (Exception e) {
			throw new CsServiceException(500L, "Extraction Integer from String failed",
					"Parameter is required but null or does not represent a Long value");
		}
	}

	private Boolean extractBoolean(String booleanString) throws CsServiceException {
		try {
			return Boolean.valueOf(booleanString);
		} catch (Exception e) {
			throw new CsServiceException(500L, "Extraction Boolean from String failed",
					"Parameter is required but null or does not represent a Boolean value");
		}
	}
}
