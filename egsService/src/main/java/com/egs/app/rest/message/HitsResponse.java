package com.egs.app.rest.message;

import org.springframework.http.HttpStatus;

import com.egs.app.model.entity.HitsEntity;

public class HitsResponse extends RestResponse{
    private HitsEntity hitsEntity;
    	
	public HitsResponse (HitsEntity hitsEntity) {
		super(HttpStatus.OK, "OK", "Hits found");
		
		setConfigEntity(hitsEntity);
	}  

	private void setConfigEntity(HitsEntity hitsEntity) {
		this.hitsEntity = hitsEntity;
	}
	
	public HitsEntity getConfigEntity() {
		return this.hitsEntity;
	}
	
}
