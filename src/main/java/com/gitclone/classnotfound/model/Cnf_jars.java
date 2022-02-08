package com.gitclone.classnotfound.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "cnf_jars")
public class Cnf_jars {
	private long id ;
	private String jar ;
	private String jar_hash ;
	private Date upt_date  ;
	private long size ;
	private String download_flag ;
	
	@GeneratedValue(strategy=GenerationType.IDENTITY)
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
	public String getJar_hash() {
		return jar_hash;
	}
	public void setJar_hash(String jar_hash) {
		this.jar_hash = jar_hash;
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
	public String getDownload_flag() {
		return download_flag;
	}
	public void setDownload_flag(String download_flag) {
		this.download_flag = download_flag;
	}	
}
