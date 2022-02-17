package com.gitclone.classnotfound.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.gitclone.classnotfound.model.Cnf_findjars;
import com.gitclone.classnotfound.model.Cnf_otherver;
import com.gitclone.classnotfound.utils.BaseUtils;
import com.gitclone.classnotfound.utils.Page;

@Service
public class FindJarService {

	@PersistenceContext
	private EntityManager em;
	
	@Autowired
	private SyncClassInfoService syncClassInfoService ;
	
	@Value("${repo.baseurl}")
	private String baseurl;
	
	public void findJarByClassName(String className, Page<Cnf_findjars> page) {
		if(className == null) {
			className = "" ;
		}
		int totalCnt = 0;
		if ("".equals(className)) {
			page.setTotalCount(totalCnt);
			page.setDatas(null);
			return ;
		}
		if (className.indexOf(".") == -1) {
			this.findJarByJarName(className,page) ;
			return ;
		}
		else {
			if (className.length()<10){
				page.setTotalCount(totalCnt);
				page.setDatas(null);
				return ;
			}
		}
		List list = em.createNativeQuery("select count(*) from cnf_classes a,cnf_jars b "
				+ "where a.jar_hash = b.jar_hash and a.class_name like :class_name and a.class_name not like '%$%' limit 200")
				.setParameter("class_name", className + "%")
				.getResultList();
		if (list.size() > 0) {
			String str= list.get(0).toString();
			totalCnt = Integer.valueOf(str) ;
		}
		page.setTotalCount(totalCnt);
		int offset = (page.getCurrentPage() - 1) * page.getPageSize();
		int limit = page.getPageSize();
		List<Cnf_findjars> listResult = em
				.createNativeQuery("select a.id ,b.jar, a.class_name ,b.upt_date,round(b.size / 1024,0) size,"
						+ "'' file_name,'' mirror1,'' mirror2,'' mirror3,'' pom_name "
						+ "from cnf_classes a,cnf_jars b "
						+ "where a.jar_hash = b.jar_hash and a.class_name like :class_name and a.class_name not like '%$%' "
						+ "order by b.upt_date desc limit " + offset + "," + limit, Cnf_findjars.class)
				.setParameter("class_name", className + "%")
				.getResultList();
		for(Cnf_findjars jar : listResult) {
			String url = jar.getJar() ;
			jar.setFile_name(url.substring(url.lastIndexOf('/')+1)) ;
			jar.setPom_name(url.replaceAll("-jar-with-dependencies", "").replaceAll(".jar", ".pom")) ;
			jar.setMirror1(BaseUtils.mirror[1] + url.replaceAll(baseurl, "")) ;
			jar.setMirror2(BaseUtils.mirror[2] + url.replaceAll(baseurl, "")) ;
			jar.setMirror3(BaseUtils.mirror[3] + url.replaceAll(baseurl, "")) ;
		}
		page.setDatas(listResult);
	}
	
	public void findJarByLeaseJar(String jarUrl, Page<Cnf_otherver> page) {
		if(jarUrl == null) {
			jarUrl = "" ;
		}
		int totalCnt = 0;
		if ("".equals(jarUrl)) {
			page.setTotalCount(totalCnt);
			page.setDatas(null);
			return ;
		}
		//
		String jarGroup = syncClassInfoService.parseJarUrl(jarUrl) ;
		List list = em.createNativeQuery("select count(*) from cnf_jars b where b.jar like :jar")
				.setParameter("jar", jarGroup)
				.getResultList();
		if (list.size() > 0) {
			String str= list.get(0).toString();
			totalCnt = Integer.valueOf(str) ;
		}
		page.setTotalCount(totalCnt);
		int offset = (page.getCurrentPage() - 1) * page.getPageSize();
		int limit = page.getPageSize();
		List<Cnf_otherver> listResult = em
				.createNativeQuery("select a.id ,a.jar, a.upt_date,round(a.size / 1024,0) size,'' file_name,'' mirror1,'' mirror2,'' mirror3,'' pom_name "
						+ "from  cnf_jars a where a.jar like :jar "
						+ "order by a.upt_date desc limit " + offset + "," + limit, Cnf_otherver.class)
				.setParameter("jar", jarGroup)
				.getResultList();
		for(Cnf_otherver jar : listResult) {
			String url = jar.getJar() ;
			//jar.setFile_name(url.substring(url.lastIndexOf('/')+1)) ;
			jar.setFile_name(url.replaceAll(baseurl, "")) ;
			jar.setPom_name(url.replaceAll("-jar-with-dependencies", "").replaceAll(".jar", ".pom")) ;
			jar.setMirror1(BaseUtils.mirror[1] + url.replaceAll(baseurl, "")) ;
			jar.setMirror2(BaseUtils.mirror[2] + url.replaceAll(baseurl, "")) ;
			jar.setMirror3(BaseUtils.mirror[3] + url.replaceAll(baseurl, "")) ;
		}
		page.setDatas(listResult);
	}
	
	public void findJarByJarName(String jarName, Page<Cnf_findjars> page) {
		if(jarName == null) {
			jarName = "" ;
		}
		int totalCnt = 0;
		if ("".equals(jarName)) {
			page.setTotalCount(totalCnt);
			page.setDatas(null);
			return ;
		}
		List list = em.createNativeQuery("select count(*) from cnf_jars a "
				+ "where download_flag = 2 and short_name like :shortName limit 200")
				.setParameter("shortName", jarName + "%")
				.getResultList();
		if (list.size() > 0) {
			String str= list.get(0).toString();
			totalCnt = Integer.valueOf(str) ;
		}
		page.setTotalCount(totalCnt);
		int offset = (page.getCurrentPage() - 1) * page.getPageSize();
		int limit = page.getPageSize();
		List<Cnf_findjars> listResult = em
				.createNativeQuery("select a.id ,a.jar, a.jar class_name ,a.upt_date,round(a.size / 1024,0) size,"
						+ "a.short_name file_name,'' mirror1,'' mirror2,'' mirror3,'' pom_name "
						+ "from cnf_jars a "
						+ "where download_flag = 2 and short_name like :shortName "
						+ "order by a.upt_date desc limit " + offset + "," + limit, Cnf_findjars.class)
				.setParameter("shortName", jarName + "%")
				.getResultList();
		for(Cnf_findjars jar : listResult) {
			String url = jar.getJar() ;
			jar.setClass_name(jar.getJar().replaceAll(baseurl, "")) ;
			jar.setPom_name(url.replaceAll("-jar-with-dependencies", "").replaceAll(".jar", ".pom")) ;
			jar.setMirror1(BaseUtils.mirror[1] + url.replaceAll(baseurl, "")) ;
			jar.setMirror2(BaseUtils.mirror[2] + url.replaceAll(baseurl, "")) ;
			jar.setMirror3(BaseUtils.mirror[3] + url.replaceAll(baseurl, "")) ;
		}
		page.setDatas(listResult);
	}

}
