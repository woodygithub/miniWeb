package com.org.miniweb.model;

import java.util.ArrayList;

public class MiniWebData {
	String fu_title;
	String title;
	String uid;
	String username;
	ArrayList<Content> xinxi=new ArrayList<Content>();

	public ArrayList<Content> getXinxi() {
		return xinxi;
	}
	public void addXinxi(String content, int type) {
		this.xinxi.add(new Content(content, type, this.xinxi.size()));
	}
	public String getFu_title() {
		return fu_title;
	}
	public void setFu_title(String fu_title) {
		this.fu_title = fu_title;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public class Content{
		String content;
		int type;
		int sort;
		public Content(String content, int type, int sort){
			this.content=content;
			this.type=type;
			this.sort=sort;
		}
		public String getContent() {
			return content;
		}
		public void setContent(String content) {
			this.content = content;
		}
		public int getType() {
				return type;
		}
		public void setType(int type) {
			this.type = type;
		}
		public boolean isImageType(){
			return 0==type;
		}
		public int getSort() {
				return sort;
		}
		public void setSort(int sort) {
			this.sort = sort;
		}
	}
}
