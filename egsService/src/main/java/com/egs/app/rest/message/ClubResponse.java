package com.egs.app.rest.message;

import org.springframework.http.HttpStatus;
import com.egs.app.model.entity.ClubEntity;

public class ClubResponse extends RestResponse{
    private ClubEntity clubEntity;
    	
	public ClubResponse (ClubEntity clubEntity) {
		super(HttpStatus.OK, "OK", "Club found");
		setConfigEntity(clubEntity);
	}  

	private void setConfigEntity(ClubEntity clubEntity) {
		this.clubEntity = clubEntity;
	}
	
	public ClubEntity getConfigEntity() {
		return this.clubEntity;
	}
	
}
