package com.egs.exception;

public class CsServiceException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 9166254022620287445L;
	
	private static final String SERVICE_NAME = "userService";
	private Long errorId;
	private String shortMsg;
	private String description;
	
	public CsServiceException (Long errorId, String shortMsg, String description) {
		super (description);
		this.errorId = errorId;
		this.shortMsg = shortMsg;
		this.description = description;
	}

	public Long getErrorId() {
		return errorId;
	}

	public String getShortMsg() {
		return shortMsg;
	}

	public String getDescription() {
		return description;
	}

	public String getServiceName() {
		return SERVICE_NAME;
	}

}
