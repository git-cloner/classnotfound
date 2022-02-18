package com.gitclone.classnotfound.utils;

import javax.servlet.http.HttpServletRequest;

public class BaseUtils {

	public static String[] mirror = new String[] { "https://repo1.maven.org/maven2/",
			"https://mirrors.163.com/maven/repository/maven-central/",
			"https://maven.aliyun.com/nexus/content/groups/public/", "https://repo.maven.apache.org/maven2/" };

	public static String getIP(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("X-Real-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}
}
