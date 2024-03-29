package com.gitclone.classnotfound.service;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import com.gitclone.classnotfound.model.Aiit_tasks;

import net.sf.json.JSONObject;

@Service
public class AiitService {

	@PersistenceContext
	private EntityManager em;
	
	@Autowired
	private RestTemplate restTemplate ;
	
	@Autowired
	private RestTemplate restTemplateGet ;
	
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
	public void taskNewBroadCast(String taskid,String body,String ext) {
		JSONObject jsonObject= JSONObject.fromObject(body) ;
		if ("generate".equals(jsonObject.getString("type"))) {
			//String nodeUrl= "https://classnotfound.com.cn/aiit/node0/generate" ;
			jsonObject.put("taskid", taskid) ;
			jsonObject.put("timestamp", this.getCurrentTimeStamp()) ;
			this.setToChain(jsonObject.toString());
		}
		else {			
			//String nodeUrl= "https://classnotfound.com.cn/aiit/node0/newtask" ;
			jsonObject.put("taskid", taskid) ;
			JSONObject paramsObject = (JSONObject) jsonObject.get("params") ;
			paramsObject.put("photo", "https://classnotfound.com.cn/aiit/task_images/" + taskid + ext) ;
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
		//remove jpg
		String path = "./task_images/" + taskid + ".jpg";
		File file = new File(path);
		if(file.exists()) {
			file.delete() ;	
		}
		//remove jpeg
		path = "./task_images/" + taskid + ".jpeg";
		File file1 = new File(path);
		if(file1.exists()) {
			file1.delete() ;	
		}
	}
	
	@Async
	public void taskCancel(JSONObject body) {
		String taskid = body.getString("taskid") ;
		removeImage(taskid);
		body.put("timestamp", this.getCurrentTimeStamp()) ;
		body.put("type","cancel") ;
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
	
	@Async
	public void taskConfirm(JSONObject body) {
		List<Aiit_tasks> list = em.createQuery("from Aiit_tasks where taskid = :tid and status = 1")
			.setParameter("tid", body.getString("taskid"))
			.getResultList() ;
		if(list.size()>0) {
			JSONObject results = JSONObject.fromObject(list.get(0).getResults()) ;
			if(results != null) {
				body.put("timestamp", this.getCurrentTimeStamp()) ;
				body.put("type","confirm") ;
				body.put("nodeid", results.getString("nodeid")) ;
				body.put("fee",1) ;
				this.setToChain(body.toString());	
			}
			
		}
	}
	
	private String genRandomNumber(int l) {
		String x = "";
        for(int n =0 ; n<l;n++ ) {
            x+=(int)(10*(Math.random()));
        }
        return x; 
	}
	
	private String createNewMeetingRoom() throws IOException {
		String api_key = System.getenv("MEETING_API_KEY") ;
		String api_secret = System.getenv("MEETING_API_SECRET") ;
		String api_url = System.getenv("MEETING_URL") ;
		String roomNum = "" ;
		int i = 0 ;
		while(true) {
			roomNum = this.genRandomNumber(6) ;
			if (AiitCache.getCache(roomNum)==null) {
				String cmd = "./livekit-cli list-rooms"  + " --api-key "+ api_key + " --api-secret " + api_secret + " --url " + api_url + " |grep "+ roomNum;
				String list = exec(cmd) ;
				if (list.indexOf(roomNum)==-1){
					AiitCache.addCache(roomNum,"0000", 60 * 120); //120 min
				}			
				break ;
			}
			i++ ;
			if (i==100) {
				roomNum = "" ; 
				break ;
			}
		}
		return roomNum ;
	}
	
	private String setMeetingRoomPwd(String params) throws IOException {
		int j = params.indexOf("-p ") ;
		String password = params.substring(j + 2).trim() ;
		int k = params.indexOf("--room") ;
		String roomNum = params.substring(k+6,j-1).trim() ;
		if (AiitCache.getCache(roomNum)==null) {
			return "{\"code\":\"1\",\"message\":\"未找到会议室信息\",\"result\":{}}" ;
		}
		else {
			AiitCache.setCache(roomNum,password) ;
			return  "{\"code\":\"0\",\"message\":\"\",\"result\":{}}" ;
		}		 
	}
	
	public String execMeetingCmd(JSONObject body) throws IOException {
		String params = body.getString("params") ;
		String api_key = System.getenv("MEETING_API_KEY") ;
		String api_secret = System.getenv("MEETING_API_SECRET") ;
		String api_url = System.getenv("MEETING_URL") ;
		if (params.contains("create-room")) {
			return meeting_create_room(api_key, api_secret, api_url);
		}
		else if (params.contains("create-token")) {
			return meeting_create_token(params, api_key, api_secret);
		}
		else if (params.contains("start-egress")) {
			return meeting_start_egress(params, api_key, api_secret,api_url);		
		}
		else if (params.contains("stop-egress")) {
			return meeting_stop_egress(params, api_key, api_secret,api_url);		
		}
		else if (params.contains("set-password")) {
			return setMeetingRoomPwd(params);		
		}
		else {
			return "{\"code\":\"1\",\"message\":\"unknow params\",\"result\":{}}" ;
		}		
	}
	
	private String meeting_stop_egress(String params, String api_key, String api_secret,String api_url) throws IOException {
		String cmd = "./livekit-cli " + params + " --api-key "+ api_key + " --api-secret " + api_secret + " --url " + api_url ;  
		String result = exec(cmd) ;		 
		if (result.contains("EgressID")) {
			result = result.replaceAll("EgressID:","").replaceAll("Status: EGRESS_ENDING", "").trim() ;
			return "{\"code\":\"0\",\"message\":\"\",\"result\":\"" + result + "\"}" ;
		}
		else {
			return "{\"code\":\"1\",\"message\":\"" +  result.trim() + "\",\"result\":\"\"}" ;
		}
	}
	
	private String meeting_start_egress(String params, String api_key, String api_secret,String api_url) throws IOException {
		int j = params.indexOf("--room") ;
		int x = params.indexOf("--name") ;
		int y = params.indexOf("--avatar") ;
		int k = params.indexOf("--track") ;
		String roomnum = params.substring(j+6,x-1).trim() ;
		String name = params.substring(x+6,y-1).trim() ;
		String avatar = params.substring(y+8,k-1).trim() ;
		String trackid = params.substring(k+7).trim() ;
		String liveurl = "rtmp://172.16.62.88:1935/live/" + roomnum +"__" + name + "__" + avatar ;
		String requestBody = 
			"{" + "\n" +
			"	  \"room_name\": \"" + roomnum + "\"," +"\n" +
			"	  \"video_track_id\": \"" + trackid + "\"," +"\n" +
			"	   \"stream\": {" +"\n" +
			"	    \"urls\": [" +"\n" +
			"	      \"" + liveurl + "\" " +"\n" +
			"	    ]" +"\n" +
			"	  }" +"\n" +
			"}" ;
		System.out.println(requestBody) ;
		String fileName = trackid + ".json" ;
		FileWriter writer = new FileWriter(fileName ) ;
		writer.write(requestBody) ;
		writer.flush() ;
		writer.close() ;
		String cmd = "./livekit-cli start-track-composite-egress --api-key "+ api_key + " --api-secret " + api_secret + " --url " + api_url  +
				" --request " + fileName;
		String result = exec(cmd) ;
		File file1 = new File(fileName);
		if(file1.exists()) {
			file1.delete() ;	
		}
		if (result.contains("EgressID")) {
			result = result.replaceAll("EgressID:","").replaceAll("Status: EGRESS_STARTING", "").trim() ;
			return "{\"code\":\"0\",\"message\":\"\",\"result\":{\"egressid\":\"" + result + "\",\"liveurl\":\"" + liveurl + "\"}}" ;
		}
		else {
			return "{\"code\":\"1\",\"message\":\"" +  result.trim() + "\",\"result\":\"\"}" ;
		}
	}

	private String meeting_create_token(String params, String api_key, String api_secret) throws IOException {
		if (!params.contains("-p")) {
			return "{\"code\":\"1\",\"message\":\"密码不能为空\",\"result\":{}}" ;
		}
		int j = params.indexOf("-p") ;
		String password = params.substring(j + 2).trim() ;
		params = params.substring(0, j-1).trim() ;
		j = params.indexOf("--room") ;
		int k = params.indexOf("--join") ;
		String roomnum = params.substring(j+6,k-1).trim() ;
		String sourcePassword = (String) AiitCache.getCache(roomnum);
		if(!password.equals(sourcePassword)) {
			return "{\"code\":\"1\",\"message\":\"密码错误\",\"result\":{}}" ;
		}
		String cmd = "./livekit-cli " + params + " --api-key "+ api_key + " --api-secret " + api_secret ;
		String result = exec(cmd) ;
		if (result.contains("access token: ")) {
			int i = result.indexOf("access token: ") ;
			result = "{\"token\":\"" + result.substring(i+14).trim() + "\"}";
			return "{\"code\":\"0\",\"message\":\"\",\"result\":" + result + "}" ;
		}else {
			return "{\"code\":\"1\",\"message\":\"unkown exception\",\"result\":{}}" ;
		}
	}

	private String meeting_create_room(String api_key, String api_secret, String api_url) throws IOException {
		String roomNum = createNewMeetingRoom() ;			
		if("".equals(roomNum)) {
			return "{\"code\":\"1\",\"message\":\"cannot get a room number\",\"result\":{}}" ;
		}
		else {
			String cmd = "./livekit-cli create-room --name " + roomNum + " --api-key "+ api_key + " --api-secret " + api_secret + " --url " + api_url ;
			exec(cmd) ;
			return "{\"code\":\"0\",\"message\":\"\",\"result\":{\"roomnum\":\"" + roomNum+ "\"}}" ;
		}
	}

	private String exec(String cmd) throws IOException {
		java.lang.Process process = null;
		System.out.println(cmd) ;
		process = Runtime.getRuntime().exec(cmd);
		ByteArrayOutputStream resultOutStream = new ByteArrayOutputStream();
		InputStream errorInStream = new BufferedInputStream(process.getErrorStream());
		InputStream processInStream = new BufferedInputStream(process.getInputStream());
		int num = 0;
		byte[] bs = new byte[1024];
		while((num=errorInStream.read(bs))!=-1){
			resultOutStream.write(bs,0,num);
		}
		while((num=processInStream.read(bs))!=-1){
			resultOutStream.write(bs,0,num);
		}
		String result = new String(resultOutStream.toByteArray());
		return result;
	}
	
	public boolean checkUserPwd(String userName,String token) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-type", "application/json");
		headers.set("Accept", "application/json; charset=utf-8");
		headers.set("Authorization", "Basic " + token) ;
		String url = "https://gitclone.com/gogs/api/v1/users/" + userName + "/tokens" ;
		restTemplateGet.setErrorHandler((ResponseErrorHandler) new DefaultResponseErrorHandler(){
		      @Override
		      public void handleError(ClientHttpResponse response) throws IOException{
		        if(response.getRawStatusCode() != 401){
		           super.handleError(response);
		        }
		      }
		 });
		ResponseEntity<String> rnt = restTemplateGet.exchange(url,
                		 HttpMethod.GET,
                		 new HttpEntity(headers), 
                		 String.class); 
		return rnt.getStatusCodeValue() == 200 ;
	}
}