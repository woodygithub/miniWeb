package com.org.miniweb.model;

public class StringData extends Response {
	String info;
    int status;

    public int getStatus() {
        return status;
    }

    public String getInfo() {
        return info;
    }
    public boolean isOK(){
        return status == 1;
    }
}
