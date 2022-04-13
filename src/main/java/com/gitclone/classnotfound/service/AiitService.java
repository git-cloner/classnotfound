package com.gitclone.classnotfound.service;

import java.io.File;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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
		System.out.println(body) ;
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
		JSONObject jsonObject= JSONObject.fromObject(body) ;
		if ("generate".equals(jsonObject.getString("task"))) {
			String nodeUrl= "https://classnotfound.com.cn/aiit/node0/generate" ;
			jsonObject.put("taskid", taskid) ;
			sendToNode(nodeUrl, jsonObject);
		}
		else {			
			String nodeUrl= "https://classnotfound.com.cn/aiit/node0/newtask" ;
			jsonObject.put("taskid", taskid) ;
			JSONObject paramsObject = (JSONObject) jsonObject.get("params") ;
			paramsObject.put("photo", "https://classnotfound.com.cn/aiit/task_images/" + taskid + ".jpg") ;
			sendToNode(nodeUrl, jsonObject);
		}
	}

	private void sendToNode(String nodeUrl, JSONObject jsonObject) {
		HttpHeaders headers = new HttpHeaders() ;
		headers.setContentType(MediaType.APPLICATION_JSON) ;
		HttpEntity<String> request = new HttpEntity<String>(jsonObject.toString(),headers) ;
		restTemplate.postForEntity(nodeUrl, request, String.class);
	}
	
	public String taskQuery(JSONObject body) {
		System.out.println(body) ;
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
		System.out.println(body) ;
		String taskid = body.getString("taskid") ;
		em.createQuery(
				"update Aiit_tasks set finished_time = :ftime,status = '1',results = :results where taskid = :tid")
			.setParameter("tid", taskid)
			.setParameter("ftime", new Date())
			.setParameter("results", body.toString())
			.executeUpdate() ;
		String path = "./task_images/" + taskid + ".jpg";
		File file = new File(path);
		if(file.exists()) {
			file.delete() ;	
		}		
		return "{}" ;
	}
	
	@Async
	public void taskCancel(JSONObject body) {
		System.out.println(body) ;
		//post to calc node
		String nodeUrl= "https://classnotfound.com.cn/aiit/node0/canceltask" ;
		sendToNode(nodeUrl, body);
	}
}
