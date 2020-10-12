package com.egs.app.rest.message;

import java.util.List;

import org.springframework.http.HttpStatus;

import com.egs.app.model.entity.ConfigEntity;

public class ListConfigurationResponse extends RestResponse{
	
	public Boolean getIsList() {
		return isList;
	}

	public void setIsList(Boolean isList) {
		this.isList = isList;
	}

	private List<ConfigEntity> configEntities;
    private ConfigEntity configEntity;
    private Boolean isList;
    	
	public ListConfigurationResponse (List<ConfigEntity> configEntities) {
		super(HttpStatus.OK, "OK", "Configurations found");
		
		if (configEntities.size() == 1) {
			setIsList(false);
			setConfiguration(configEntities.get(0));
		} else {
			setIsList(true);
			setConfigurations(configEntities);
		}
	}  

    public List<ConfigEntity> getConfigurations() {
		return configEntities;
	}

	public void setConfigurations(List<ConfigEntity> configEntities) {
		this.configEntities = configEntities;
	}

	public ConfigEntity getConfiguration() {
		return configEntity;
	}

	public void setConfiguration(ConfigEntity configEntity) {
		this.configEntity = configEntity;
	}


}
