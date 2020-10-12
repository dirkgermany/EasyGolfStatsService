package com.egs.app.rest.message;

import org.springframework.http.HttpStatus;

import com.egs.app.model.entity.HitsEntity;

public class ConfigResponse extends RestResponse{
    private HitsEntity hitsEntity;
    	
	public ConfigResponse (HitsEntity hitsEntity) {
		super(HttpStatus.OK, "OK", "Configuration found");
		
		setConfigEntity(hitsEntity);
	}  

	private void setConfigEntity(HitsEntity hitsEntity) {
		this.hitsEntity = hitsEntity;
	}
	
	public HitsEntity getConfigEntity() {
		return this.hitsEntity;
	}
	
}
