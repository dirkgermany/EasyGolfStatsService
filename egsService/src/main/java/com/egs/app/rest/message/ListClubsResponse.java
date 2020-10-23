package com.egs.app.rest.message;

import java.util.List;
import org.springframework.http.HttpStatus;
import com.egs.app.model.entity.ClubEntity;

public class ListClubsResponse extends RestResponse{
	
	public Boolean getIsList() {
		return isList;
	}

	public void setIsList(Boolean isList) {
		this.isList = isList;
	}

	private List<ClubEntity> clubEntities;
    private ClubEntity clubEntity;
    private Boolean isList;
    	
	public ListClubsResponse (List<ClubEntity> clubEntities) {
		super(HttpStatus.OK, "OK", "Clubs found");
		
		if (clubEntities.size() == 1) {
			setIsList(false);
			setClub(clubEntities.get(0));
			setClubs(clubEntities);
		} else {
			setIsList(true);
			setClubs(clubEntities);
		}
	}  

    public List<ClubEntity> getClubs() {
		return clubEntities;
	}

	public void setClubs(List<ClubEntity> clubEntities) {
		this.clubEntities = clubEntities;
	}

	public ClubEntity getClub() {
		return clubEntity;
	}

	public void setClub(ClubEntity clubEntity) {
		this.clubEntity = clubEntity;
	}
}
