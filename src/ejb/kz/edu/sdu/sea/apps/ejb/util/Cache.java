package kz.edu.sdu.sea.apps.ejb.util;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class Cache {
	
	private static Map<String,Long> memory = new LinkedHashMap<String, Long>();
	
	public Cache() {
		memory = (Map<String, Long>)Collections.synchronizedMap(memory);
	}
	
	public static void add(String link, Long linkId) {
		memory.put(link, linkId);
	}
	
	public static Long get(String link) {
		return memory.get(link);
	}
	
	public static Map<String, Long> getMemory() {
		return memory;
	}
	
	public static boolean isAlreadyExists(String link) {
		if( memory.get(link) == null ) return false;
		else return true;
	}
	
	public static int size() {
		return memory.size();
	}
}
