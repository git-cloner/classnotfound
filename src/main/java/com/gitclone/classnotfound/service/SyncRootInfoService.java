package com.gitclone.classnotfound.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gitclone.classnotfound.model.Cnf_roots;

@Service
public class SyncRootInfoService {
	
	@PersistenceContext
    private EntityManager em;
	
	@Autowired
	private FetchSerivce fetchSerivce ;
	
	public List<String> FetchFirstRoot(String url) {
		return fetchPath(url,false);
	}
	
	public List<String> FetchSencondRoot(String url) {
		return fetchPath(url,false);
	}
	
	@Transactional
	public List<String> FetchThirdRoot(String url) {
		return fetchPath(url,true);
	}

	private List<String> fetchPath(String url,boolean saveToDb) {
		Document doc = fetchSerivce.fetchUrlWithTimeout(url);
		if (doc == null) {
			return null;
		}
		List<String> roots = new ArrayList<String>() ;
		Elements links = doc.getElementsByTag("a") ;
		for (Element link : links) {
			if (link.hasAttr("href")) {
				String linkHref = link.attr("href");
				if (linkHref.endsWith("/") && !linkHref.contains("..")) {
					roots.add(url  + linkHref) ;
				}				
			}
		}
		if (saveToDb) {
			this.saveRootsToDb(roots) ;	
		}
		return roots ;
	}
	
	public void saveRootsToDb(List<String> roots) {
		for(String root : roots) {
			List list = em.createQuery("from Cnf_roots where root = :root")
					.setParameter("root", root)
					.getResultList() ;
			if (list.size()==0) {
				Cnf_roots cnf_root = new Cnf_roots() ;
				cnf_root.setRoot(root); 
				cnf_root.setSync_flag("0");
				em.persist(cnf_root);	
			}			
		}
	}
	
	public boolean ifSyncinng(String root) {
		List list = em.createQuery("from Cnf_roots where root = :root and sync_flag in (1,2) ")
				.setParameter("root", root)
				.getResultList() ;
		return (list.size() > 0) ;
	}
	
	@Transactional
	@Modifying
	public void setSyncing(String root) {
		em.createQuery("update Cnf_roots set sync_flag = 2,sync_time = :sync_time where root = :root")
		.setParameter("root", root)
		.setParameter("sync_time", new Date())
		.executeUpdate();
	}
	
	@Transactional
	@Modifying
	public void setSynced(String root) {
		em.createQuery("update Cnf_roots set sync_flag = 1,sync_time = :sync_time where root = :root")
		.setParameter("root", root)
		.setParameter("sync_time", new Date())
		.executeUpdate();
	}
	
	@Transactional
	@Modifying
	public void setNotSynced(String root) {
		em.createQuery("update Cnf_roots set sync_flag = 0,sync_time = null where root = :root")
		.setParameter("root", root).executeUpdate();
	}
}
