package com.egs.app.rest.message;

import java.time.LocalDate;

import com.egs.app.model.entity.HitsEntity;
import com.egs.app.types.ClubType;
import com.egs.app.types.HitCategory;

public class HitsWriteRequest extends RestRequest {

	private HitsEntity hitsEntity;	

    public HitsWriteRequest(HitsEntity hitsEntity) {
		super("CS 0.0.1");
		this.hitsEntity = hitsEntity;
    }
    
    public HitsWriteRequest(Long userId, LocalDate sessionDate, HitCategory hitCategory, ClubType clubType, Integer hitsGood, Integer hitsNeutral, Integer hitsBad) {
		super("CS 0.0.1");
		hitsEntity = new HitsEntity(userId, sessionDate, hitCategory, clubType, hitsGood, hitsNeutral, hitsBad);
    }
    
    public HitsEntity getHitsEntity() {
    	return hitsEntity;
    }
    
}