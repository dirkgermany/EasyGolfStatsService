package com.egs.app.rest.message;

import org.springframework.http.HttpStatus;

import com.egs.app.model.entity.HitsEntity;

public class WriteResponse extends RestResponse{
    private HitsEntity hitsEntity;
    	
	public WriteResponse (HitsEntity hitsEntity) {
		super(HttpStatus.CREATED, "OK", "Configuration created");
		
		setUser(hitsEntity);
	}  

	private void setUser(HitsEntity hitsEntity) {
		this.hitsEntity = hitsEntity;
	}
	
	public HitsEntity getUser() {
		return hitsEntity;
	}
	
}
