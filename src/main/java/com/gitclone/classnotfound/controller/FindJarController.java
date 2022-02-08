package com.gitclone.classnotfound.controller;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.gitclone.classnotfound.model.Cnf_search;
import com.gitclone.classnotfound.service.FindJarService;
import com.gitclone.classnotfound.service.StatService;
import com.gitclone.classnotfound.utils.Page;

@Controller
public class FindJarController {
	
	@Autowired
	private FindJarService findJarService ;
	
	@Autowired
	private StatService statService ;
	
	@GetMapping("/")
    public String rootPage(Model model,String className, Integer currentPage,Integer pageSize) {
		return this.homePage(model, className, currentPage, pageSize) ;
	}
	
	@GetMapping("/home")
    public String homePage(Model model,String className, Integer currentPage,Integer pageSize) {
		Long startTime = System.currentTimeMillis();
		Page page = new Page() ;
		page.setCurrentPage(currentPage != null ? currentPage : 1 ) ;
		page.setPageSize(pageSize != null ? pageSize : 10) ;		
		findJarService.findJarByClassName(className, page);		
		model.addAttribute("page", page);
		Cnf_search cnf_search = new Cnf_search() ;
		cnf_search.setClassName(className) ;
		//
		HashMap<String,Integer> map = statService.getJarClassCount() ;
		cnf_search.setJarCount(map.get("cnf_jars")) ;
		cnf_search.setClassCount(map.get("cnf_classes")) ;
		cnf_search.setFindT((System.currentTimeMillis() - startTime)) ;
		//
		model.addAttribute("cnf_search",cnf_search) ;
        return "home";
    }
	
	@PostMapping("/home")
	public String submit(Model model,@ModelAttribute("cnf_search") Cnf_search cnf_search) {
		Long startTime = System.currentTimeMillis();
		if("".equals(cnf_search.getClassName())|| (cnf_search.getClassName() == null)) {
			cnf_search.setClassName("org.springframework.stereotype.Service");
		}
		String className = cnf_search.getClassName() ;
		Page page = new Page() ;	
		findJarService.findJarByClassName(className, page);		
		model.addAttribute("page", page);
		//
		HashMap<String,Integer> map = statService.getJarClassCount() ;
		cnf_search.setJarCount(map.get("cnf_jars")) ;
		cnf_search.setClassCount(map.get("cnf_classes")) ;
		cnf_search.setFindT((System.currentTimeMillis() - startTime)) ;
		model.addAttribute("cnf_search",cnf_search) ;
		return "home";
	}
	
	@GetMapping("/otherver")
    public String Otherver(Model model,String jarUrl, Integer currentPage,Integer pageSize) {
		Page page = new Page() ;
		page.setCurrentPage(currentPage != null ? currentPage : 1 ) ;
		page.setPageSize(pageSize != null ? pageSize : 10) ;		
		findJarService.findJarByLeaseJar(jarUrl, page);		
		model.addAttribute("page", page);
		Cnf_search cnf_search = new Cnf_search() ;
		cnf_search.setJar(jarUrl) ;
		model.addAttribute("cnf_search",cnf_search) ;
		return "otherver";
    }
}
