package com.gitclone.classnotfound.model;

public class Cnf_search {
	private String className ;
	private Integer jarCount ;
	private Integer classCount ;
	private String jar ;
	private Long findT ;
	private String message ;

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public Integer getJarCount() {
		return jarCount;
	}

	public void setJarCount(Integer jarCount) {
		this.jarCount = jarCount;
	}

	public Integer getClassCount() {
		return classCount;
	}

	public void setClassCount(Integer classCount) {
		this.classCount = classCount;
	}

	public String getJar() {
		return jar;
	}

	public void setJar(String jar) {
		this.jar = jar;
	}

	public Long getFindT() {
		return findT;
	}

	public void setFindT(Long findT) {
		this.findT = findT;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
