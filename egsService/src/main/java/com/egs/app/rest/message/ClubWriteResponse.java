package com.egs.app.rest.message;

import org.springframework.http.HttpStatus;

import com.egs.app.model.entity.ClubEntity;

public class ClubWriteResponse extends RestResponse{
    private ClubEntity clubEntity;
    	
	public ClubWriteResponse (ClubEntity clubEntity) {
		super(HttpStatus.CREATED, "OK", "Club created");
		
		setClub(clubEntity);
	}  

	private void setClub(ClubEntity clubEntity) {
		this.clubEntity = clubEntity;
	}
	
	public ClubEntity getClub() {
		return clubEntity;
	}
	
}
