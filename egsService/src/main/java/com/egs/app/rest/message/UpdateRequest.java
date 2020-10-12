package com.egs.app.rest.message;

import com.egs.app.model.entity.HitsEntity;

public class UpdateRequest extends RestRequest {

	private HitsEntity configStored = new HitsEntity();
	private HitsEntity configContainer = new HitsEntity();

    public UpdateRequest(HitsEntity userStored, HitsEntity userUpdate) {
		super("CS 0.0.1");
		this.configStored = userStored;
		this.configContainer = userUpdate;
    }
    
    public HitsEntity getConfigStored() {
    	return configStored;
    }
    
    public HitsEntity getConfigContainer() {
    	return configContainer;
    }
    
}