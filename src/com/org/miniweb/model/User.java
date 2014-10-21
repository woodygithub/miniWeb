package com.org.miniweb.model;

import java.io.Serializable;

public class User extends Response implements Serializable{
	String username;
	String success;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getSuccess() {
		return success;
	}
	
	public boolean isSuccess(){
		return "1".equals(success);
	}
}