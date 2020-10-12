package com.egs.app.rest.message;

import com.egs.app.model.entity.ClubEntity;
import com.egs.app.types.ClubType;

public class WriteRequest extends RestRequest {

	private ClubEntity clubEntity;	

    public WriteRequest(ClubEntity clubEntity) {
		super("CS 0.0.1");
		this.clubEntity = clubEntity;
    }
    
    public WriteRequest(Long userId, ClubType clubType, String clubName) {
		super("CS 0.0.1");
		clubEntity = new ClubEntity(userId, clubType, clubName);
    }
    
    public ClubEntity getClubEntity() {
    	return clubEntity;
    }
    
}