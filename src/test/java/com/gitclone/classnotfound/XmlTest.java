package com.gitclone.classnotfound;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.gitclone.classnotfound.model.Cnf_pomattr;

public class XmlTest {
	public static void main(String[] args) throws Exception {
		String url = "https://repo1.maven.org/maven2/org/springframework/spring-context/5.3.15/spring-context-5.3.15.pom" ;
		Cnf_pomattr cnf_pomattr = parsePom(url);
		if (cnf_pomattr!=null) {
			System.out.println(cnf_pomattr.getUrl().substring(0,5)) ;
		}
	}

	private static Cnf_pomattr parsePom(String url) {		
		Document doc;
		try {
			Cnf_pomattr cnf_pomattr = new Cnf_pomattr() ;
			doc = Jsoup.connect(url).timeout(60000).get();
			cnf_pomattr.setGroupId(doc.select("groupId").first().text());
			cnf_pomattr.setArtifactId(doc.select("artifactId").first().text());
			cnf_pomattr.setUrl(doc.select("url").first().text());
			cnf_pomattr.setVersion(doc.select("version").first().text());
			cnf_pomattr.setName(doc.select("name").first().text());
			cnf_pomattr.setDescription(doc.select("description").first().text()); 
			return cnf_pomattr ;
		} catch (IOException e) {
			return null ;
		}
		
	}

}
