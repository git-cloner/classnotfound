package com.gitclone.classnotfound.controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import javax.imageio.ImageIO;

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
		for (MultipartFile multipartFile : multipartFiles) {
			BufferedImage image = ImageIO.read(multipartFile.getInputStream());
			// ext
			String extention = "." + multipartFile.getContentType().split("/")[1];
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
		aiitService.taskNewBroadCast(uuid, body) ;
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
	
}
