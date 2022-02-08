package com.gitclone.classnotfound.service;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FetchSerivce {

	@Value("${proxy.ip}")
	private String proxy_ip;

	@Value("${proxy.port}")
	private int proxy_port;

	public Document fetchUrlWithTimeout(String url) {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Document doc = null;
		try {
			if (proxy_ip.equals("")) {
				doc = Jsoup.connect(url).timeout(60000).get();
			} else {
				doc = Jsoup.connect(url).proxy(proxy_ip, proxy_port).timeout(60000).get();
			}

		} catch (IOException e1) {
			try {
				if (e1.getMessage().contains("Status=404")) {
					return null ;
				}
				Thread.sleep(1000);
				doc = Jsoup.connect(url).timeout(60000).get();
			} catch (Exception e) {
				try {
					Thread.sleep(5000);
					doc = Jsoup.connect(url).timeout(60000).get();
				} catch (Exception e2) {
					try {
						Thread.sleep(10000);
						doc = Jsoup.connect(url).timeout(60000).get();
					} catch (Exception e3) {
						e3.printStackTrace();
					}
				}
			}
		}
		return doc;
	}
}
