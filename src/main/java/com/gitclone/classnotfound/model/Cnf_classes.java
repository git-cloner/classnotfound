package com.gitclone.classnotfound.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "cnf_classes")
public class Cnf_classes {
	private long id ;
	private String jar_hash ;
	private String class_name ;
	
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Id
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getJar_hash() {
		return jar_hash;
	}
	public void setJar_hash(String jar_hash) {
		this.jar_hash = jar_hash;
	}
	public String getClass_name() {
		return class_name;
	}
	public void setClass_name(String class_name) {
		this.class_name = class_name;
	}
	 
	 
}
