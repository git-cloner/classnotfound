package com.gitclone.classnotfound.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gitclone.classnotfound.model.Cnf_classes;
import com.gitclone.classnotfound.model.Cnf_jars;
import com.gitclone.classnotfound.utils.BaseUtils;

@Service
public class SyncClassInfoService {
	
	@PersistenceContext
	private EntityManager em;
	
	@Value("${repo.baseurl}")
	private String baseurl;
	
	public String downLoadJar(String jarUrl) {
		Random random = new Random() ;
		int x = random.nextInt(4) ;
		if(baseurl==null) {
			baseurl = "https://repo1.maven.org/maven2/" ;
		}
		String url = jarUrl.replaceAll(baseurl, BaseUtils.mirror[x]) ;
		String fileName = "";
		for(int i=0;i<4;i++) {  
			System.out.println("  mirror: " + url) ;
			fileName = doDownLoadJar(url) ;
			if(!"".equals(fileName)) {
				break ;
			}
			if(i==x) {
				continue ;
			}
			url = jarUrl.replaceAll(baseurl, BaseUtils.mirror[i]) ;
		}
		return fileName ;		
	}

	public String doDownLoadJar(String jarUrl) {
		String fileName = jarUrl.substring(jarUrl.lastIndexOf("/") + 1);
		int bytesum = 0;
		int byteread = 0;
		try {
			URL url = new URL(jarUrl);
			URLConnection conn = url.openConnection();
			InputStream inStream = conn.getInputStream();
			FileOutputStream fs = new FileOutputStream(fileName);
			byte[] buffer = new byte[65536]; //64k 
			int length = conn.getContentLength() ;
			while ((byteread = inStream.read(buffer)) != -1) {
				bytesum += byteread;
				fs.write(buffer, 0, byteread);
				//
				/*System.out.println(jarUrl + "\n" + 
						Math.round((bytesum + 0.0) / 1024) + "k of " +
						Math.round((length + 0.0) / 1024) + "k   " +
						Math.round( (bytesum + 0.0)/ length * 10000.00) / 100.00 + "%"
						) ;*/
			}
			fs.close() ;
			inStream.close() ;
		} catch (Exception e) {
			e.printStackTrace() ;
			fileName = "";
		}
		return fileName;
	}

	private List<String> getClassByJar(String jarFile)  {
		List<String> list = new ArrayList();
		try {
			File f = new File(jarFile);
			URL url1 = f.toURI().toURL();
			URLClassLoader myClassLoader = new URLClassLoader(new URL[] { url1 },
					Thread.currentThread().getContextClassLoader());

			JarFile jar = new JarFile(jarFile);
			Enumeration<JarEntry> enumFiles = jar.entries();
			JarEntry entry;
			while (enumFiles.hasMoreElements()) {
				entry = (JarEntry) enumFiles.nextElement();
				if (entry.getName().indexOf("META-INF") < 0) {
					String classFullName = entry.getName();
					if (!classFullName.endsWith(".class")) {
						classFullName = classFullName.substring(0, classFullName.length() - 1);
					} else {
						String className = classFullName.substring(0, classFullName.length() - 6).replace("/", ".");
						list.add(className);
					}
				}
			}
			jar.close() ;
			myClassLoader.close() ;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}

	public static void main(String[] args) throws IOException {
		String fileName = "acegi-security-catalina-server-0.51.jar" ;
		SyncClassInfoService syncClassInfoService = new SyncClassInfoService() ;
		fileName = syncClassInfoService.downLoadJar("https://repo1.maven.org/maven2/acegisecurity/acegi-security-catalina-server/0.51/acegi-security-catalina-server-0.51.jar");
		if (!fileName.equals("")) {
			List<String> list = syncClassInfoService.getClassByJar(fileName) ;
			for(String className : list) {
				System.out.println(className) ;
			}
		}
	}
	
	@Transactional
	@Modifying
	public boolean parseClassesFromMainJar(String headWords) {
		Long startTime = System.currentTimeMillis();
		String otherCond = "" ;
		if (!"".equals(headWords)) {
			String[] str = headWords.split(",") ;
			for(int i=0;i<str.length;i++) {
				if(otherCond.equals("")) {
					otherCond = " jar like '" + baseurl + str[i] + "%'" ;	
				}
				else {
					otherCond = otherCond + " or "  + " jar like '" + baseurl + str[i] + "%'" ;	
				}				
			}
			otherCond = "(" + otherCond + ") and " ;
		}
		//fetch one jar
		List<Cnf_jars> list = em.createQuery("from Cnf_jars where " + otherCond + " (download_flag = 0 or download_flag is null) ")
				.setMaxResults(1)
				.getResultList() ;
		if (list.size()==0) {
			return false ;
		}
		System.out.println("单条耗时：" + (System.currentTimeMillis() - startTime) + "ms");
		startTime = System.currentTimeMillis();
		//fetch jar group
		String jar = list.get(0).getJar() ;
		String jarGroup = parseJarUrl(jar);
		List<Cnf_jars> listJar = em.createQuery(
				"from Cnf_jars where jar like :jar and (download_flag = 0 or download_flag is null) "
				+ "order by upt_date desc")
				.setParameter("jar", jarGroup)
				.getResultList() ;
		System.out.println("多条耗时：" + (System.currentTimeMillis() - startTime) + "ms");
		startTime = System.currentTimeMillis();
		String downloadJar = listJar.get(0).getJar() ;
		String jarHash = listJar.get(0).getJar_hash() ;
		for(Cnf_jars cnf_jar : listJar) {
			if (cnf_jar.getJar().equals(downloadJar)) {
				cnf_jar.setDownload_flag("2") ;
			}
			else {
				cnf_jar.setDownload_flag("1") ;
			}			
			em.merge(cnf_jar) ;
		}
		System.out.println("更新耗时：" + (System.currentTimeMillis() - startTime) + "ms");
		//if size > 10M ,then ignore
		if (listJar.get(0).getSize() > 10485760) {
			Cnf_jars cnf_jar = list.get(0) ;
			cnf_jar.setDownload_flag("4");
			em.merge(cnf_jar) ;
			System.out.println(cnf_jar.getJar() + " exceed 10M") ;
			return true ;
		}
		startTime = System.currentTimeMillis();
		//choice first jar and download
		System.out.println("download: " + downloadJar) ;
		String fileName = downLoadJar(downloadJar);
		System.out.println("下载耗时：" + (System.currentTimeMillis() - startTime) + "ms");
		startTime = System.currentTimeMillis();
		if (!fileName.equals("")) {
			List<String> listClasses = getClassByJar(fileName) ;
			System.out.println("解析耗时：" + (System.currentTimeMillis() - startTime) + "ms");
			startTime = System.currentTimeMillis();
			//int x = listClasses.size() ;
			//int y  = 0 ;
			for(String className : listClasses) {
				/*List listTemp = em.createQuery("from Cnf_classes where jar_hash =:jar_hash and class_name = :class_name")
						.setParameter("jar_hash", jarHash)
						.setParameter("class_name", className)
						.getResultList() ;
				if(listTemp.size()==0) {*/
					Cnf_classes cnf_class = new Cnf_classes() ;
					cnf_class.setJar_hash(jarHash);
					cnf_class.setClass_name(className);
					em.persist(cnf_class);	
					//y++ ;
					//System.out.println("insert classes: " + y  + " of " + x) ;
				//}				
			}
			System.out.println("写库耗时：" + (System.currentTimeMillis() - startTime) + "ms");
			//remove file
			File file = new File(System.getProperty("user.dir") + File.separator + fileName) ;
			if (file.exists()) {
				file.delete() ;
			}
			System.out.println(listClasses.size() + " classes") ;
		}
		else {			
			 em.createQuery("update Cnf_jars set download_flag = 3 where jar like :jar")
			 	.setParameter("jar", jarGroup)
			 	.executeUpdate() ;
		}
		return true ;
	}

	public String parseJarUrl(String jar) {
		String[] lines = jar.split("/") ;
		int j = lines.length ;
		String jarPath = lines[j-2] ;
		lines[j-2] = "%" ;
		lines[j-1] = lines[j-1].replaceAll(jarPath, "%") ;
		String jarGroup = "" ;
		for(String s : lines) {
			if(jarGroup.equals("")) {
				jarGroup = s;	
			}
			else {
				jarGroup = jarGroup + "/"  + s;	
			}			
		}
		return jarGroup;
	}

}
