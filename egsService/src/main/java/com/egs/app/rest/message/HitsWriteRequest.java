package com.egs.app.rest.message;

import java.time.LocalDateTime;

import com.egs.app.model.entity.HitsEntity;
import com.egs.app.types.ClubType;
import com.egs.app.types.HitCategory;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer; 


public class HitsWriteRequest extends RestRequest {
	@JsonSerialize(using = ToStringSerializer.class) 

	private HitsEntity hitsEntity;	

    public HitsWriteRequest(HitsEntity hitsEntity) {
		super("CS 0.0.1");
		this.hitsEntity = hitsEntity;
    }
    
    public HitsWriteRequest(Long userId, LocalDateTime sessionDateTime, HitCategory hitCategory, ClubType clubType, Integer hitCountGood, Integer hitCountNeutral, Integer hitCountBad) {
		super("CS 0.0.1");
		hitsEntity = new HitsEntity(userId, sessionDateTime, hitCategory, clubType, hitCountGood, hitCountNeutral, hitCountBad);
    }
    
    public HitsEntity getHitsEntity() {
    	return hitsEntity;
    }
    
}