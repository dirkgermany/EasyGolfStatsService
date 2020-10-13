package com.egs.app.rest.message;

import java.util.List;

import org.springframework.http.HttpStatus;

import com.egs.app.model.entity.HitsEntity;

public class ListHitsResponse extends RestResponse{
	
	public Boolean getIsList() {
		return isList;
	}

	public void setIsList(Boolean isList) {
		this.isList = isList;
	}

	private List<HitsEntity> hitsEntities;
    private HitsEntity hitsEntity;
    private Boolean isList;
    	
	public ListHitsResponse (List<HitsEntity> hitsEntities) {
		super(HttpStatus.OK, "OK", "Configurations found");
		
		if (hitsEntities.size() == 1) {
			setIsList(false);
			setConfiguration(hitsEntities.get(0));
		} else {
			setIsList(true);
			setConfigurations(hitsEntities);
		}
	}  

    public List<HitsEntity> getConfigurations() {
		return hitsEntities;
	}

	public void setConfigurations(List<HitsEntity> hitsEntities) {
		this.hitsEntities = hitsEntities;
	}

	public HitsEntity getConfiguration() {
		return hitsEntity;
	}

	public void setConfiguration(HitsEntity hitsEntity) {
		this.hitsEntity = hitsEntity;
	}


}
