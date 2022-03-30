package com.gitclone.classnotfound.service;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gitclone.classnotfound.model.Aiit_tasks;

import net.sf.json.JSONObject;

@Service
public class AiitService {

	@PersistenceContext
	private EntityManager em;
	
	@Transactional
	@Modifying
	@Async
	public void taskNew(String taskid,String body) {
		//json params
		JSONObject jsonObject= JSONObject.fromObject(body) ;
		String params = "" ;
		if (jsonObject.get("params") != null) {
			params = jsonObject.get("params").toString() ;
		}
		//insert into db
		Aiit_tasks aiit_tasks = new Aiit_tasks() ;
		aiit_tasks.setTaskid(taskid); 
		aiit_tasks.setTask(jsonObject.getString("task"));
		aiit_tasks.setType(jsonObject.getString("type")); 
		aiit_tasks.setParams(params);
		aiit_tasks.setCreate_time(new Date());
		aiit_tasks.setStatus("0");
		em.persist(aiit_tasks);
		//post to calc node
	}
	
	public String taskQuery(JSONObject body) {
		List<Aiit_tasks> list = em.createQuery("from Aiit_tasks where taskid = :tid")
			.setParameter("tid", body.getString("taskid"))
			.getResultList() ;
		String rnt = "";
		if(list.size()>0) {
			rnt = list.get(0).getResults() ;
		}
		else {
			rnt = "{\"taskid\":\"\"}" ;
		}
		if ( (rnt==null) || "".equals(rnt)){
			rnt = "{\"taskid\":\"\"}" ;
		}
		return rnt ;
	}
}
