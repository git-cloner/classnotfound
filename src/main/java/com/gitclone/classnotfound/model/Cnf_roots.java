package com.gitclone.classnotfound.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "cnf_roots")
public class Cnf_roots {
	private long id ;
	private String root ;
	private String sync_flag ;
	private Date sync_time ;
	
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Id
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	public String getRoot() {
		return root;
	}
	public void setRoot(String root) {
		this.root = root;
	}
	
	public String getSync_flag() {
		return sync_flag;
	}
	public void setSync_flag(String sync_flag) {
		this.sync_flag = sync_flag;
	}
	public Date getSync_time() {
		return sync_time;
	}
	public void setSync_time(Date sync_time) {
		this.sync_time = sync_time;
	}		
}
