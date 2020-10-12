package com.egs.app;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties
public class Properties {
	
	
	@Value("${server.port}")
	String serverPort;
	
	@Value("${configStore.updateInterval}")
	Long persistenceUpdateInterval;

	public String getServerPort() {
		return serverPort;
	}

	public void setServerPort(String serverPort) {
		this.serverPort = serverPort;
	}

	public Long getPersistenceUpdateInterval() {
		return persistenceUpdateInterval;
	}

	public void setPersistenceUpdateInterval(Long persistenceUpdateInterval) {
		this.persistenceUpdateInterval = persistenceUpdateInterval;
	}
}
