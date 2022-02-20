package com.gitclone.classnotfound.service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import com.gitclone.classnotfound.model.Cnf_jars;
import com.gitclone.classnotfound.model.Cnf_roots;
import com.gitclone.classnotfound.model.JarInfo;

@Service
public class SyncJarInfoService {

	@PersistenceContext
	private EntityManager em;

	@Value("${repo.baseurl}")
	private String baseurl;

	@Autowired
	private FetchSerivce fetchSerivce;

	@Async
	public List<JarInfo> asyncFetchLinks(String homeUrl) {
		Map<String, Boolean> LinkMap = new ConcurrentHashMap<String, Boolean>();
		List<JarInfo> jarList = new ArrayList<JarInfo>();
		fetchLinks(LinkMap, jarList, homeUrl);
		return jarList;
	}

	private void parseJarInfo(String html, JarInfo jarInfo) {
		html = html.trim();
		String dt = html.substring(0, 16).trim();
		String si = html.substring(17).trim();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		try {
			jarInfo.setUpt_date(df.parse(dt));
		} catch (ParseException e) {
			jarInfo.setUpt_date(new Date());
		}
		jarInfo.setSize(Long.parseLong(si));
	}

	private void fetchLinks(Map<String, Boolean> LinkMap, List<JarInfo> jarList, String url) {
		Document doc = fetchSerivce.fetchUrlWithTimeout(url);
		if (doc == null) {
			LinkMap.replace(url, false, true);
			return;
		}
		LinkMap.replace(url, false, true);
		System.out.println("fetch:" + url);
		Elements links = doc.getAllElements();
		for (Element link : links) {
			if (link.hasAttr("href")) {
				String linkHref = url + link.attr("href");
				if (linkHref.endsWith("/") 
						&& (!linkHref.contains("..")) 
						&& (!link.attr("href").contains("\""))
						&& (!link.attr("href").trim().equals(""))) {
					LinkMap.put(linkHref, false);
				} else if (linkHref.endsWith(".jar") 
						&& (!linkHref.endsWith("-javadoc.jar"))
						&& (!linkHref.endsWith("-sources.jar"))
						&& (!linkHref.endsWith("-tests-sources.jar"))
						&& (!linkHref.endsWith("-with-dependencies.jar"))) {
					if (link.nextSibling() != null) {
						JarInfo jarInfo = new JarInfo();
						jarInfo.setUrl(linkHref);
						this.parseJarInfo(link.nextSibling().outerHtml(), jarInfo);
						jarList.add(jarInfo);
					}
				}
			}
		}
		Iterator iter = LinkMap.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			if (!(Boolean) entry.getValue()) {
				fetchLinks(LinkMap, jarList, (String) entry.getKey());
			}
		}
	}

	@Transactional
	@Modifying
	public boolean FetchJarsByRoot(Cnf_roots roots) {
		try {
			List<JarInfo> jarList = asyncFetchLinks(roots.getRoot());
			for (JarInfo jarInfo : jarList) {
				String jar_hash = DigestUtils.md5DigestAsHex(jarInfo.getUrl().getBytes());
				List<Cnf_jars> listTemp = em.createQuery("from Cnf_jars where jar_hash = :jar_hash ")
						.setParameter("jar_hash", jar_hash).getResultList();
				if (listTemp.size() == 0) {
					Cnf_jars cnf_jars = new Cnf_jars();
					cnf_jars.setJar(jarInfo.getUrl());
					cnf_jars.setJar_hash(jar_hash);
					cnf_jars.setUpt_date(jarInfo.getUpt_date());
					cnf_jars.setSize(jarInfo.getSize());
					cnf_jars.setShort_name(jarInfo.getUrl().substring(jarInfo.getUrl().lastIndexOf('/')+1));
					em.persist(cnf_jars);
				}
			}
		} catch (Exception e) {
			return false ;
		}
		return true ;
	}

	@Transactional
	@Modifying
	public List<Cnf_roots> getAllSyncRoots() {
		List<Cnf_roots> list = em.createQuery("from Cnf_roots where sync_flag = 0").getResultList();
		return list;
	}
}
