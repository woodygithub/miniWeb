package com.org.miniweb.model;

import java.io.Serializable;

public class Response implements Serializable{
	String error;
	String msg;
	public String getError() {
		return error;
	}
	public String getMsg() {
		return msg;
	}
	public boolean isOk() {
		return "0".equals(error);
	}
	
}
