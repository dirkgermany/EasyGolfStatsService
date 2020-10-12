package com.egs.app.rest.message;

import org.springframework.http.HttpStatus;

import com.egs.app.model.entity.ConfigEntity;

public class WriteResponse extends RestResponse{
    private ConfigEntity configEntity;
    	
	public WriteResponse (ConfigEntity configEntity) {
		super(HttpStatus.CREATED, "OK", "Configuration created");
		
		setUser(configEntity);
	}  

	private void setUser(ConfigEntity configEntity) {
		this.configEntity = configEntity;
	}
	
	public ConfigEntity getUser() {
		return configEntity;
	}
	
}
