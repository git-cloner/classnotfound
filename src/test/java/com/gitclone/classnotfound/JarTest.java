package com.gitclone.classnotfound;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.Random;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.gitclone.classnotfound.service.SyncClassInfoService;

public class JarTest {
	public static void getJarName(String jarFile) throws Exception { 
        
        try{ 
            File f = new File(jarFile); 
            URL url1 = f.toURI().toURL(); 
            URLClassLoader myClassLoader = new URLClassLoader(new URL[]{url1},Thread.currentThread().getContextClassLoader()); 
               
            JarFile jar = new JarFile(jarFile); 
            Enumeration<JarEntry> enumFiles = jar.entries(); 
            JarEntry entry; 
            while(enumFiles.hasMoreElements()){ 
                entry = (JarEntry)enumFiles.nextElement(); 
                if(entry.getName().indexOf("META-INF")<0){ 
                    String classFullName = entry.getName(); 
                    if(!classFullName.endsWith(".class")){ 
                        classFullName = classFullName.substring(0,classFullName.length()-1); 
                    } else{ 
                        String className = classFullName.substring(0,classFullName.length()-6).replace("/", "."); 
                        Class<?> myclass = myClassLoader.loadClass(className); 
                        System.out.println(className); 
                    } 
                } 
             } 
        } catch(IOException e){ 
            e.printStackTrace(); 
        } 
    } 
       
   
    /*public static void main(String[] args) throws Exception { 
        getJarName("F:\\slf4j-api-1.6.6.jar"); 
         
    }*/ 
    
	/*public static void main(String[] args) throws Exception {
    	String[] mirror = new String[] {
    			"https://repo1.maven.org/maven2/",
    			"https://mirrors.163.com/maven/repository/maven-central/",
    			"https://maven.aliyun.com/nexus/content/groups/public/",
    			"https://repo.maven.apache.org/maven2/"} ;

    	Random random = new Random() ;
    	int x = random.nextInt(4) ;
    	String url = mirror[x]  ;
    	System.out.println(x + ":" + url) ;
    	for(int i=0;i<4;i++) {
    		if(i==x) {
    			continue ;
    		}
    		url = mirror[i]  ;
    		System.out.println(i + ":" + url) ;    		
    	}
    }*/
	
	public static void main(String[] args) throws Exception {
		Long startTime = System.currentTimeMillis();
		SyncClassInfoService syncClassInfoService = new SyncClassInfoService() ;
		syncClassInfoService.doDownLoadJar("https://repo1.maven.org/maven2/ai/catboost/catboost-spark_2.3_2.11/1.0.3/catboost-spark_2.3_2.11-1.0.3.jar") ;
				//"https://repo1.maven.org/maven2/acegisecurity/acegi-security-catalina-server/0.51/acegi-security-catalina-server-0.51.jar") ;
				//"https://repo1.maven.org/maven2/ai/catboost/catboost-spark_2.3_2.11/1.0.3/catboost-spark_2.3_2.11-1.0.3.jar"
		System.out.println("downlaod time:" + (System.currentTimeMillis() - startTime) + "ms");
	}
}
