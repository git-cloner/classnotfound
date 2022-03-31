package com.gitclone.classnotfound.service;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.gitclone.classnotfound.model.Aiit_tasks;

import net.sf.json.JSONObject;

@Service
public class AiitService {

	@PersistenceContext
	private EntityManager em;
	
	@Autowired
	private RestTemplate restTemplate ;
	
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
	}
	
	@Async
	public void taskNewBroadCast(String taskid,String body) {
		//post to calc node
		String nodeUrl= "http://127.0.0.1/node0/newtask" ;
		JSONObject jsonObject= JSONObject.fromObject(body) ;
		jsonObject.put("taskid", taskid) ;
		JSONObject paramsObject = (JSONObject) jsonObject.get("params") ;
		paramsObject.put("photo", "https://classnotfound.com.cn/aiit/task_images/" + taskid + ".jpg") ;
		JSONObject json = restTemplate.postForEntity(nodeUrl, jsonObject, JSONObject.class).getBody();	
		System.out.println(json) ;
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
	
	@Transactional
	@Modifying
	public String taskNotify(JSONObject body) {
		em.createQuery(
				"update Aiit_tasks set finished_time = :ftime,status = '1',results = :results where taskid = :tid")
			.setParameter("tid", body.getString("taskid"))
			.setParameter("ftime", new Date())
			.setParameter("results", body.toString())
			.executeUpdate() ;
		return "{}" ;
	}
	
	@Async
	public void taskCancel(JSONObject body) {
		//post to calc node
		String nodeUrl= "http://127.0.0.1/node0/taskcancel" ;
		JSONObject json = restTemplate.postForEntity(nodeUrl, body, JSONObject.class).getBody();
		System.out.println(json) ;
	}
}
