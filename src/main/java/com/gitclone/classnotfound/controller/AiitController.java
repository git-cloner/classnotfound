package com.gitclone.classnotfound.controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.gitclone.classnotfound.service.AiitService;

import net.sf.json.JSONObject;

@RestController
@EnableAsync
public class AiitController {
	
	@Autowired
	private AiitService aiitService ;

	@PostMapping("aiit/tasknew")
	public String taskNew(@RequestParam("multipartFiles[]") List<MultipartFile> multipartFiles,
			@RequestParam("body") String body) throws IOException {
		String uuid = UUID.randomUUID().toString();
		String extention = ".jpg" ;
		for (MultipartFile multipartFile : multipartFiles) {
			BufferedImage image = ImageIO.read(multipartFile.getInputStream());
			// ext
			extention = "." + multipartFile.getContentType().split("/")[1];
			// path
			String path = "./task_images/" + uuid + extention;
			File outputFile = new File(path);
			outputFile.getParentFile().mkdirs();
			// save
			if (image!=null) {
				ImageIO.write(image, "jpg", outputFile);	
			}			
		}
		aiitService.taskNew(uuid, body) ;
		aiitService.taskNewBroadCast(uuid, body,extention) ;
		return "{\"taskid\":\"" + uuid + "\"}" ;
	}
	
	@PostMapping("aiit/taskquery")
	public String taskQuery(
			@RequestBody JSONObject body) throws IOException {
		String rnt = aiitService.taskQuery(body) ;
		return rnt ;		
	}
	
	@PostMapping("aiit/tasknotify")
	public String taskNotify(
			@RequestBody JSONObject body) throws IOException {
		String rnt = aiitService.taskNotify(body) ;
		return rnt ;		
	}
	
	@PostMapping("aiit/taskcancel")
	public String taskCancel(
			@RequestBody JSONObject body) throws IOException {
		aiitService.taskCancel(body) ;
		return body.toString() ;		
	}
	
	@PostMapping("aiit/taskconfirm")
	public String taskConfirm(
			@RequestBody JSONObject body) throws IOException {
		aiitService.taskConfirm(body) ;
		return body.toString() ;		
	}
	
	@PostMapping("aiit/meeting")
	public String meetingCmd(@RequestBody JSONObject body) {
		try {
			return  aiitService.execMeetingCmd(body) ;
		} catch (IOException e) {
			return "{\"code\":\"1\",\"message\":\"unknow exception\",\"result\":\"\"}" ;
		}
	}
	
	@PostMapping("aiit/useravatar")
	public String userAvatar(@RequestParam("multipartFiles[]") List<MultipartFile> multipartFiles,
			@RequestParam("body") String body) throws IOException{
		String extention = ".jpg" ;
		JSONObject jsonObject= JSONObject.fromObject(body) ;
		String userName = jsonObject.getString("username") ;
		String token = jsonObject.getString("token") ;
		if (!aiitService.checkUserPwd(userName, token)) {
			return "{\"code\":\"1\",\"message\":\"user auth error\"}" ;
		}
		boolean havaImage = false ;
		for (MultipartFile multipartFile : multipartFiles) {
			BufferedImage image = ImageIO.read(multipartFile.getInputStream());
			// ext
			extention = "." + multipartFile.getContentType().split("/")[1];
			// path
			String path = "./avatar/" + userName + extention;
			File outputFile = new File(path);
			outputFile.getParentFile().mkdirs();
			// save
			if (image!=null) {
				ImageIO.write(image, "jpg", outputFile);
				havaImage = true ;
			}	
		}
		if (!havaImage) {
			return "{\"code\":\"1\",\"message\":\"no image data\"}" ;
		}else {
			return "{\"code\":\"0\",\"message\":\"\"}" ;
		}
		
	}
	
}
