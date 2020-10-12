package com.egs.app.rest.message;

import com.egs.app.model.entity.ConfigEntity;

public class WriteRequest extends RestRequest {

	private ConfigEntity configEntity;	

    public WriteRequest(ConfigEntity configEntity) {
		super("CS 0.0.1");
		this.configEntity = configEntity;
    }
    
    public WriteRequest(Long userId, String confKey, String value, Integer listIndex, Boolean hidden) {
		super("CS 0.0.1");
		configEntity = new ConfigEntity(userId, confKey, value, listIndex, hidden);
    }
    
    public ConfigEntity getConfigEntity() {
    	return configEntity;
    }
    
}