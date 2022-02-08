package com.gitclone.classnotfound.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

public class LocalCache {
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

	public static Set<String> getCacheKeys() {
		return map.keySet();
	}

	public static void addCache(String key, Object ce) {
		map.put(key, ce);
	}

	public static Object getCache(String key) {
		return map.get(key);
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
			LocalCache.removeCache(ceKey);
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

	public static void main(String[] args) throws InterruptedException {
		LocalCache.addCache("key", "ok", 1);
		Thread.sleep(900);
		System.out.println(LocalCache.getCache("key"));
		System.out.println(LocalCache.getCacheSize());
		Thread.sleep(110);
		System.out.println(LocalCache.getCache("key"));
		if (LocalCache.getCache("key") == null) {
			System.out.println("null value");
		}
		System.out.println(LocalCache.getCacheSize());
	}

}