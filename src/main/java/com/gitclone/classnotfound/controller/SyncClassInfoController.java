package com.gitclone.classnotfound.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gitclone.classnotfound.model.Cnf_roots;
import com.gitclone.classnotfound.model.Response;
import com.gitclone.classnotfound.service.SyncClassInfoService;
import com.gitclone.classnotfound.service.SyncJarInfoService;
import com.gitclone.classnotfound.service.SyncRootInfoService;

import net.sf.json.JSONObject;

@RestController
@EnableAsync
public class SyncClassInfoController {
	
	@Autowired
	private SyncJarInfoService syncJarInfoService;
	
	@Autowired
	private SyncRootInfoService syncRootInfoService ;
	
	@Autowired
	private SyncClassInfoService syncClassInfoService ;
	
	@Value("${repo.baseurl}")
	private String baseurl;
	
	
	@PostMapping
	@RequestMapping(value = "getroots")
	public Response getroots(@RequestBody JSONObject body) throws Exception {
		String token = body.getString("token") ;
		if (!"Y2xhc3Nub3Rmb3VuZC5jb20uY24=".equals(token)) {
			throw new Exception("bad token") ;
		}
		
		List<String> list0 = syncRootInfoService.FetchFirstRoot(baseurl);
		int l = list0.size() ;
		int i = 0 ;
		for (String root0 : list0) {
			i++ ;
			System.out.println("total : "+ l + " progress: "+ i) ;
			List<String> list1 = syncRootInfoService.FetchSencondRoot(root0);
			for (String root1 : list1) {
				syncRootInfoService.FetchThirdRoot(root1);
			}
		}
		Response<Map<String, Object>> response = new Response<>();
        Map<String, Object> result = new HashMap<>();
        result.put("result","ok");
        response.setData(result);
        return response;
	}
	
	@PostMapping
	@RequestMapping(value = "getalljars")
	public Response getalljars(@RequestBody JSONObject body) throws Exception {
		String token = body.getString("token") ;
		if (!"Y2xhc3Nub3Rmb3VuZC5jb20uY24=".equals(token)) {
			throw new Exception("bad token") ;
		}
		List<Cnf_roots> list = syncJarInfoService.getAllSyncRoots();
		int l = list.size() ;
		int i = 0 ;
		int j = 0 ;
		for(Cnf_roots cnf_roots : list) {
			if (syncRootInfoService.ifSyncinng(cnf_roots.getRoot())) {
				i++ ;
			}
			else {
				syncRootInfoService.setSyncing(cnf_roots.getRoot()) ;
				boolean b = syncJarInfoService.FetchJarsByRoot(cnf_roots);
				if(b) {
					i++ ;
					syncRootInfoService.setSynced(cnf_roots.getRoot());
				}
				else {
					syncRootInfoService.setNotSynced(cnf_roots.getRoot());
				}
			}			
			j++ ;
			System.out.println("total: " + l + " progress: " + j  + " success: "+ i) ;
		}
		Response<Map<String, Object>> response = new Response<>();
        Map<String, Object> result = new HashMap<>();
        result.put("result", "ok");
        response.setData(result);
        return response;
	}

	@PostMapping
	@RequestMapping(value = "parseclasses")
	public Response parseclasses(@RequestBody JSONObject body) throws Exception {
		String token = body.getString("token") ;
		if (!"Y2xhc3Nub3Rmb3VuZC5jb20uY24=".equals(token)) {
			throw new Exception("bad token") ;
		}
		boolean success = true ;
		String headWords = body.getString("headwords") ;
		while(true) {
			success = syncClassInfoService.parseClassesFromMainJar(headWords) ;
			if (!success) {
				break ;
			}
		}
		Response<Map<String, Object>> response = new Response<>();
        Map<String, Object> result = new HashMap<>();
        result.put("result", success);
        response.setData(result);
        return response;
	}
	
	@PostMapping
	@RequestMapping(value = "getdeeproot")
	public Response getdeeproot(@RequestBody JSONObject body) throws Exception {
		String token = body.getString("token") ;
		String root = body.getString("root") ;
		if (!"Y2xhc3Nub3Rmb3VuZC5jb20uY24=".equals(token)) {
			throw new Exception("bad token") ;
		}
		syncRootInfoService.FetchThirdRoot(root);
		Response<Map<String, Object>> response = new Response<>();
        Map<String, Object> result = new HashMap<>();
        result.put("result","ok");
        response.setData(result);
        return response;
	}
	
	@PostMapping
	@RequestMapping(value = "parsepoms")
	public Response parsePoms(@RequestBody JSONObject body) throws Exception {
		String token = body.getString("token") ;
		if (!"Y2xhc3Nub3Rmb3VuZC5jb20uY24=".equals(token)) {
			throw new Exception("bad token") ;
		}
		List<Long> list = syncJarInfoService.getAllSyncPomsId();
		int l = list.size() ;
		int i = 0 ;
		for(Long id : list) {
			syncJarInfoService.syncJarsPom(id);
			i++ ;
			System.out.println("total: " + l + " progress: " + i) ;
		}

		Response<Map<String, Object>> response = new Response<>();
        Map<String, Object> result = new HashMap<>();
        result.put("result", l);
        response.setData(result);
        return response;
	}
	
}
