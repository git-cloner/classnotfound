package com.gitclone.classnotfound.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Cnf_otherver {
	private long id ;
	private String jar ;
	private Date upt_date  ;
	private long size ;
	private String file_name ;
	private String mirror1 ;
	private String mirror2 ;
	private String mirror3 ;
	
	@Id
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getJar() {
		return jar;
	}
	public void setJar(String jar) {
		this.jar = jar;
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
	public String getMirror1() {
		return mirror1;
	}
	public void setMirror1(String mirror1) {
		this.mirror1 = mirror1;
	}
	public String getMirror2() {
		return mirror2;
	}
	public void setMirror2(String mirror2) {
		this.mirror2 = mirror2;
	}
	public String getMirror3() {
		return mirror3;
	}
	public void setMirror3(String mirror3) {
		this.mirror3 = mirror3;
	}
	public String getFile_name() {
		return file_name;
	}
	public void setFile_name(String file_name) {
		this.file_name = file_name;
	}
	
}
