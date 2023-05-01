package com.gitclone.classnotfound.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

public class AiitCache {
	private static final ConcurrentHashMap<String, Object> map;
	private static final Timer timer;
	static {
		timer = new Timer();
		map = new ConcurrentHashMap<String, Object>();
	}

	public static void addCache(String key, Object ce, int validityTime) {
		map.put(key, ce);
		timer.schedule(new TimeoutTimerTask(key), validityTime * 1000);
	}
	
	public static void removeMeetingRoom(String key) throws IOException {
		String api_key = System.getenv("MEETING_API_KEY") ;
		String api_secret = System.getenv("MEETING_API_SECRET") ;
		String api_url = System.getenv("MEETING_URL") ;
		String cmd = "./livekit-cli delete-room --room " + key + " --api-key "+ api_key + " --api-secret " + api_secret + " --url " + api_url ;
		System.out.println(cmd) ;
		Runtime.getRuntime().exec(cmd);
	}

	public static Set<String> getCacheKeys() {
		return map.keySet();
	}

	public static void addCache(String key, Object ce) {
		map.put(key, ce);
	}

	public static Object getCache(String key) {
		return map.get(key);
	}
	
	public static void setCache(String key, Object ce) {
		map.replace(key, ce) ; 
	}

	public static List<String> getKeysFuzz(String patton) {
		List<String> list = new ArrayList<String>();
		for (String tmpKey : map.keySet()) {
			if (tmpKey.contains(patton)) {
				list.add(tmpKey);
			}
		}
		if (isNullOrEmpty(list)) {
			return null;
		}
		return list;
	}

	public static boolean contains(String key) {
		return map.containsKey(key);
	}

	public static void removeCache(String key) {
		map.remove(key);
	}

	public static void removeCacheFuzzy(String key) {
		for (String tmpKey : map.keySet()) {
			if (tmpKey.indexOf(key) > -1) {
				map.remove(tmpKey);
			}
		}
	}

	public static int getCacheSize() {
		return map.size();
	}

	public static void clearCache() {
		map.clear();
	}

	static class TimeoutTimerTask extends TimerTask {
		private String ceKey;

		public TimeoutTimerTask(String key) {
			this.ceKey = key;
		}

		@Override
		public void run() {
			AiitCache.removeCache(ceKey);
			try {
				AiitCache.removeMeetingRoom(ceKey) ;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static boolean isNullOrEmpty(Object obj) {
		try {
			if (obj == null)
				return true;
			if (obj instanceof CharSequence) {
				return ((CharSequence) obj).length() == 0;
			}
			if (obj instanceof Collection) {
				return ((Collection<?>) obj).isEmpty();
			}
			if (obj instanceof Map) {
				return ((Map<?, ?>) obj).isEmpty();
			}
			if (obj instanceof Object[]) {
				Object[] object = (Object[]) obj;
				if (object.length == 0) {
					return true;
				}
				boolean empty = true;
				for (int i = 0; i < object.length; i++) {
					if (!isNullOrEmpty(object[i])) {
						empty = false;
						break;
					}
				}
				return empty;
			}
			return false;
		} catch (Exception e) {
			return true;
		}

	}

}