package com.egs.app.rest.message;

import java.util.List;
import org.springframework.http.HttpStatus;
import com.egs.app.model.entity.HitsEntity;

public class HitsListWriteResponse extends RestResponse{
    private List<HitsEntity> hitsEntityList;
    	
	public HitsListWriteResponse (List<HitsEntity> hitsEntityList) {
		super(HttpStatus.CREATED, "OK", "Hits created");
		
		setHitsList(hitsEntityList);
	}  

	public void setHitsList(List<HitsEntity> hitsEntityList) {
		this.hitsEntityList = hitsEntityList;
	}
	
	public List<HitsEntity> getHitsList() {
		return hitsEntityList;
	}
	
}
