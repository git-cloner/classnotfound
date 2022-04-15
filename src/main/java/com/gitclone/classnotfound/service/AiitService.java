package com.gitclone.classnotfound.service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.tomcat.util.codec.binary.Base64;
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
		JSONObject jsonObject= JSONObject.fromObject(body) ;
		if ("generate".equals(jsonObject.getString("task"))) {
			//String nodeUrl= "https://classnotfound.com.cn/aiit/node0/generate" ;
			jsonObject.put("taskid", taskid) ;
			jsonObject.put("timestamp", this.getCurrentTimeStamp()) ;
			this.setToChain(jsonObject.toString());
		}
		else {			
			//String nodeUrl= "https://classnotfound.com.cn/aiit/node0/newtask" ;
			jsonObject.put("taskid", taskid) ;
			JSONObject paramsObject = (JSONObject) jsonObject.get("params") ;
			paramsObject.put("photo", "https://classnotfound.com.cn/aiit/task_images/" + taskid + ".jpg") ;
			jsonObject.put("timestamp", this.getCurrentTimeStamp()) ;
			this.setToChain(jsonObject.toString());
		}
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
		String nodeid = body.getString("nodeid") ;
		List<Aiit_tasks> list = 
				em.createQuery("from Aiit_tasks where  taskid = :tid")
				.setParameter("tid", taskid)
				.getResultList() ;
		if (list.size()>0) {
			String result = list.get(0).getResults() ;
			if(result != null) {
				if(!result.contains("\"" + nodeid + "\"")) {
					 throw new RuntimeException("task has been received") ;
				}	
			}			
		}
		em.createQuery(
				"update Aiit_tasks set finished_time = :ftime,status = '1',results = :results where taskid = :tid")
			.setParameter("tid", taskid)
			.setParameter("ftime", new Date())
			.setParameter("results", body.toString())
			.executeUpdate() ;
		removeImage(taskid);		
		return "{}" ;
	}

	private void removeImage(String taskid) {
		String path = "./task_images/" + taskid + ".jpg";
		File file = new File(path);
		if(file.exists()) {
			file.delete() ;	
		}
	}
	
	@Async
	public void taskCancel(JSONObject body) {
		//post to calc node
		//String nodeUrl= "https://classnotfound.com.cn/aiit/node0/canceltask" ;
		//sendToNode(nodeUrl, body);
		String taskid = body.getString("taskid") ;
		removeImage(taskid);
		body.put("timestamp", this.getCurrentTimeStamp()) ;
		this.setToChain(body.toString());
	}
	
	public String getCurrentTimeStamp() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		return formatter.format(new Date());
	}
	
	public void setToChain(String body) {
		System.out.println("send to chain:\n" + body) ;
		String url = "http://120.253.47.149:26657/broadcast_tx_commit?tx=\"" + Base64.encodeBase64String(body.getBytes()) + "\"" ;
		String result = restTemplate.getForObject(url,String.class) ;
		System.out.println("recieve from chain:\n" + result) ;
	}
	
	public static void main(String[] args) {
		String body = "Base64.encodeBase64String(body.getBytes()" ;
		System.out.println(Base64.encodeBase64String(body.getBytes())) ;
	}
}
