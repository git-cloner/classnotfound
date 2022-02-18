package com.gitclone.classnotfound.controller;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.gitclone.classnotfound.model.Cnf_search;
import com.gitclone.classnotfound.service.FindJarService;
import com.gitclone.classnotfound.service.StatService;
import com.gitclone.classnotfound.utils.BaseUtils;
import com.gitclone.classnotfound.utils.Page;

@Controller
public class FindJarController {
	
	@Autowired
	private FindJarService findJarService ;
	
	@Autowired
	private StatService statService ;
	
	@Autowired
    HttpServletRequest req;
	
	@GetMapping("/")
    public String rootPage(Model model,String className, Integer currentPage,Integer pageSize) {
		return this.homePage(model, className, currentPage, pageSize) ;
	}
	
	@GetMapping("/home")
    public String homePage(Model model,String className, Integer currentPage,Integer pageSize) {
		Page page = new Page() ;
		Cnf_search cnf_search = new Cnf_search() ;
		cnf_search.setClassName(className) ;
		if (!this.checkParam(cnf_search)) {
			model.addAttribute("cnf_search",cnf_search) ;
			model.addAttribute("page", page);
			return "home";
		}
		Long startTime = System.currentTimeMillis();
		page.setCurrentPage(currentPage != null ? currentPage : 1 ) ;
		page.setPageSize(pageSize != null ? pageSize : 10) ;		
		findJarService.findJarByClassName(className, page);		
		model.addAttribute("page", page);
		//
		HashMap<String,Integer> map = statService.getJarClassCount() ;
		cnf_search.setJarCount(map.get("cnf_jars")) ;
		cnf_search.setClassCount(map.get("cnf_classes")) ;
		long useTime = System.currentTimeMillis() - startTime ;
		cnf_search.setFindT(useTime) ;
		model.addAttribute("cnf_search",cnf_search) ;
		//add visit rec
		this.saveVisitRec(className, useTime) ;
		//
        return "home";
    }
	
	private boolean checkParam(Cnf_search cnf_search){
		/*if ((cnf_search.getClassName()==null) || ("".equals(cnf_search.getClassName()))) {
			return true ;
		}
		cnf_search.setClassName(cnf_search.getClassName().replaceAll("/", ".")) ;
		String[] sl = cnf_search.getClassName().split("[.]") ;
		if(sl.length<3) {
			cnf_search.setMessage("classname is a.b.c ......");
			return false ;
		}
		if(cnf_search.getClassName().length()<10) {
			cnf_search.setMessage("classname's min length is 10");
			return false ;
		}*/
		if ((cnf_search.getClassName()==null) || ("".equals(cnf_search.getClassName()))) {
			return true ;
		}
		cnf_search.setClassName(cnf_search.getClassName().replaceAll("/", ".")) ;
		return true ;
	}
	
	private void saveVisitRec(String visit_content,long userTime) {
		String ip = BaseUtils.getIP(req) ; 
		if("61.133.217.141".equals(ip) || "127.0.0.1".equals(ip)) {
			return ;
		}
		statService.saveVisitRec(visit_content, ip, userTime);
	}
	
	@PostMapping("/home")
	public String submit(Model model,@ModelAttribute("cnf_search") Cnf_search cnf_search) {
		if("".equals(cnf_search.getClassName())|| (cnf_search.getClassName() == null)) {
			cnf_search.setClassName("org.springframework.stereotype.Service");
		}
		Page page = new Page() ;
		if (!this.checkParam(cnf_search)) {
			model.addAttribute("cnf_search",cnf_search) ;
			model.addAttribute("page", page);
			return "home";
		}
		Long startTime = System.currentTimeMillis();
		String className = cnf_search.getClassName() ;
		findJarService.findJarByClassName(className, page);		
		model.addAttribute("page", page);
		//
		HashMap<String,Integer> map = statService.getJarClassCount() ;
		cnf_search.setJarCount(map.get("cnf_jars")) ;
		cnf_search.setClassCount(map.get("cnf_classes")) ;
		long useTime = System.currentTimeMillis() - startTime ;
		cnf_search.setFindT(useTime) ;
		model.addAttribute("cnf_search",cnf_search) ;
		//add visit rec
		this.saveVisitRec(className, useTime) ;
		//
		return "home";
	}
	
	@GetMapping("/otherver")
    public String Otherver(Model model,String jarUrl, Integer currentPage,Integer pageSize) {
		Long startTime = System.currentTimeMillis();
		Page page = new Page() ;
		page.setCurrentPage(currentPage != null ? currentPage : 1 ) ;
		page.setPageSize(pageSize != null ? pageSize : 10) ;		
		findJarService.findJarByLeaseJar(jarUrl, page);		
		model.addAttribute("page", page);
		Cnf_search cnf_search = new Cnf_search() ;
		cnf_search.setJar(jarUrl) ;
		model.addAttribute("cnf_search",cnf_search) ;
		//add visit rec
		long useTime = System.currentTimeMillis() - startTime ;
		this.saveVisitRec(jarUrl, useTime) ;
		//
		return "otherver";
    }
}
