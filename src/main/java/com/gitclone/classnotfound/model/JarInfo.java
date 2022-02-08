package com.gitclone.classnotfound.model;

import java.util.Date;

public class JarInfo {
	private String url ;
	private Date upt_date ;
	private long size ;
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public Date getUpt_date() {
		return upt_date;
	}
	public void setUpt_date(Date upt_date) {
		this.upt_date = upt_date;
	}
	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}	
}
