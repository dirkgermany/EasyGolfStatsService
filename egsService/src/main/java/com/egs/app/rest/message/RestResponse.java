package com.egs.app.rest.message;

import org.springframework.http.HttpStatus;

public class RestResponse {

	private final String SERVICE_NAME = "ConfigurationService";

	private HttpStatus httpStatus;
	private String description;
	private String result;

	public RestResponse(HttpStatus httpStatus, String result, String description) {
		setHttpStatus(httpStatus);
		setDescription(description);
		setResult(result);
	}
	
	public String getServiceName () {
		return SERVICE_NAME;
	}

	private void setHttpStatus(HttpStatus httpStatus) {
		this.httpStatus = httpStatus;
	}

	public HttpStatus getHttpStatus() {
		return httpStatus;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

}
