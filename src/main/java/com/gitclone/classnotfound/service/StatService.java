package com.gitclone.classnotfound.service;

import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StatService {
	
	@Autowired
	private AsyncStatService asyncStatService ;
	
	@PersistenceContext
	private EntityManager em;
	
	public HashMap getJarClassCount() {
		HashMap<String,Integer> map = new HashMap() ;
		getJarClassCountFromSysTable(map);
		/*
		select count(*) is too slows!!!
		Integer x = (Integer) LocalCache.getCache("cnf_classes") ;
		Integer y = (Integer) LocalCache.getCache("cnf_jars") ;
		if (x==null || y == null) {
			if(!asyncStatService.caching) {
				asyncStatService.getRealJarClassCount() ;
			}
			getJarClassCountFromSysTable(map);
		}
		else {
			map.put("cnf_classes", x) ;
			map.put("cnf_jars", y) ;
		}*/
		return map ;
	}

	private void getJarClassCountFromSysTable(HashMap<String, Integer> map) {
		List<Object[]> list = em.createNativeQuery(
				"select table_name,table_rows "
				+ "from information_schema.tables "
				+ "where table_name in ('cnf_classes','cnf_jars')").getResultList() ;
		for(Object[] objs : list) {
			map.put(objs[0].toString(), Integer.valueOf(objs[1].toString())) ;
		}
	}
}
