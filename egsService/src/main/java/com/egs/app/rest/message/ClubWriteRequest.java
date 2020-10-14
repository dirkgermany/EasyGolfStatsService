package com.egs.app.rest.message;

import java.util.List;

import com.egs.app.model.entity.ClubEntity;
import com.egs.app.types.ClubType;

public class ClubWriteRequest extends RestRequest {

	private ClubEntity clubEntity;	
	private List<ClubEntity> clubEntities;

    public ClubWriteRequest(ClubEntity clubEntity) {
		super("CS 0.0.1");
		this.clubEntity = clubEntity;
    }
    
    public ClubWriteRequest(Long userId, ClubType clubType, String clubName) {
		super("CS 0.0.1");
		clubEntity = new ClubEntity(userId, clubType, clubName);
    }
    
    public ClubWriteRequest(List<ClubEntity> clubs) {
		super("CS 0.0.1");
		this.clubEntities = clubs;
    }
    
    public ClubEntity getClubEntity() {
    	return clubEntity;
    }
    
}