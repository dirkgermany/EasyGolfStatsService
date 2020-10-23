package com.egs.app.rest.message;

import java.util.List;
import org.springframework.http.HttpStatus;
import com.egs.app.model.entity.HitsEntity;

public class HitsListWriteResponse extends RestResponse{
    private List<HitsEntity> hitsEntityList;
    private String fileName;

	public HitsListWriteResponse (List<HitsEntity> hitsEntityList, String fileName) {
		super(HttpStatus.CREATED, "OK", "Hits created");
		
		setHitsList(hitsEntityList);
		setFileName(fileName);
	}  

	public void setHitsList(List<HitsEntity> hitsEntityList) {
		this.hitsEntityList = hitsEntityList;
	}
	
	public List<HitsEntity> getHitsList() {
		return hitsEntityList;
	}
	

	public String getFileName() {
		return this.fileName;
	}
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}
