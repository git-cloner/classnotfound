package com.gitclone.classnotfound.service;

import java.math.BigInteger;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.gitclone.classnotfound.utils.LocalCache;

@Service
public class AsyncStatService {
	
	@PersistenceContext
	private EntityManager em;
	
	public static boolean caching = false ;
	
	@Async
	public void getRealJarClassCount(){
		caching = true ;
		List list = em.createNativeQuery("select count(*) from  cnf_classes").getResultList() ;
		String s = (BigInteger) list.get(0)  + "" ;
		LocalCache.addCache("cnf_classes", Integer.valueOf(s),3600);
		list = em.createNativeQuery("select count(*) from  cnf_jars").getResultList() ;
		s = (BigInteger) list.get(0)  + ""  ;
		LocalCache.addCache("cnf_jars", Integer.valueOf(s),3600);
		caching = false ;
	}
}
