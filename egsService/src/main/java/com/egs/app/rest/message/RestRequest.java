package com.egs.app.rest.message;

public class RestRequest {
	
	private String clientSource;
    private String requestorUserId;

	
	public RestRequest (String clientSource) {
		setClientSource(clientSource);
	}

	public String getClientSource() {
		return clientSource;
	}

	private void setClientSource(String clientSource) {
		this.clientSource = clientSource;
	}
	
    public String getRequestorUserId() {
    	return requestorUserId;
    }
    
    public void setRequestorUserId(String requestorUserId) {
    	this.requestorUserId = requestorUserId;
    }

}
