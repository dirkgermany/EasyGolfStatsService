package com.egs.app.rest.message;

import java.util.List;

import org.springframework.http.HttpStatus;

import com.egs.app.model.entity.ClubEntity;

public class ClubListWriteResponse extends RestResponse{
    private List<ClubEntity> clubEntityList;
    	
	public ClubListWriteResponse (List<ClubEntity> clubEntity) {
		super(HttpStatus.CREATED, "OK", "Clubs created");
		
		setClubs(clubEntityList);
	}  

	public void setClubs(List<ClubEntity> clubEntityList) {
		this.clubEntityList = clubEntityList;
	}
	
	public List<ClubEntity> getClubs() {
		return clubEntityList;
	}
	
}
