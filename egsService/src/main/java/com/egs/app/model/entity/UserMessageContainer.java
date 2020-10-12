package com.egs.app.model.entity;

import org.springframework.stereotype.Component;

@Component
public class UserMessageContainer {
	
	private String userName;
	private String givenName;
	private String lastName;
	
	public UserMessageContainer () {
		
	}
	
	public UserMessageContainer (String userName, String givenName, String lastName) {
		setUserName(userName);
		setGivenName(givenName);
		setLastName(lastName);
	}
	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getGivenName() {
		return givenName;
	}

	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

}
