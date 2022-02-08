package com.gitclone.classnotfound.utils;

import java.util.ArrayList;
import java.util.List;

public class Page<T> {

	public enum Sort {
		ASC, DESC;
	}

	private List<T> datas = new ArrayList<T>(0);
	private boolean hasPreviousPage;
	private boolean hasNextPage;
	private int totalCount; 
	private int totalPageCount;
	private int currentPage = 1;
	private int pageSize = 10; 
	private Enum<Sort> sort;

	public void gotoPreviousPage() 
	{
		if (isHasPreviousPage()) {
			currentPage = currentPage - 1;
		}
	}

	public void gotoNextPage() 
	{
		if (isHasNextPage()) {
			currentPage = currentPage + 1;
		}
	}

	public void gotoFirstPage()
	{
		currentPage = 1;
	}

	public void gotoLastPage() 
	{
		currentPage = totalPageCount;
	}

	public List<T> getDatas() {
		return datas;
	}

	public void setDatas(List<T> datas) {
		this.datas = datas;
	}

	public boolean isHasPreviousPage() {
		if (currentPage > 1) {
			hasPreviousPage = true;
		} else {
			hasPreviousPage = false;
		}
		return hasPreviousPage;
	}

	public void setHasPreviousPage(boolean hasPreviousPage) {
		this.hasPreviousPage = hasPreviousPage;
	}

	public boolean isHasNextPage() {
		if (currentPage < getTotalPageCount()) {
			hasNextPage = true;
		} else {
			hasNextPage = false;
		}
		return hasNextPage;
	}

	public void setHasNextPage(boolean hasNextPage) {
		this.hasNextPage = hasNextPage;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public int getTotalPageCount() {
		if (totalCount % pageSize == 0) {
			totalPageCount = totalCount / pageSize; 
		} else {
			totalPageCount = totalCount / pageSize + 1; 
														
		}
		return totalPageCount;
	}

	public void setTotalPageCount(int totalPageCount) {
		this.totalPageCount = totalPageCount;
	}

	public int getCurrentPage() {
		if(this.currentPage > this.getTotalPageCount()){
			currentPage = this.getTotalPageCount();
		}
		return this.currentPage <= 0 ? 1 : this.currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public Enum<Sort> getSort() {
		return sort;
	}

	public void setSort(Enum<Sort> sort) {
		this.sort = sort;
	}

	public boolean getHasPreviousPage() {
		if (currentPage > 1) {
			hasPreviousPage = true;
		} else {
			hasPreviousPage = false;
		}
		return hasPreviousPage;
	}

	public boolean getHasNextPage() {
		if (currentPage < getTotalPageCount()) {
			hasNextPage = true;
		} else {
			hasNextPage = false;
		}
		return hasNextPage;
	}

}