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
	private String short_name ;
	private String group_id ;
	private String artifact_id ;
	private String url ;
	private String name ;
	private String description ;
	
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
	public String getShort_name() {
		return short_name;
	}
	public void setShort_name(String short_name) {
		this.short_name = short_name;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getGroup_id() {
		return group_id;
	}
	public void setGroup_id(String group_id) {
		this.group_id = group_id;
	}
	public String getArtifact_id() {
		return artifact_id;
	}
	public void setArtifact_id(String artifact_id) {
		this.artifact_id = artifact_id;
	}
	
}
