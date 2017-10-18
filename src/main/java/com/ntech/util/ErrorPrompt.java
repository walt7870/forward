package com.ntech.util;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;

/**
 * 错误提示记录工具类
 */
public class ErrorPrompt {
	
	private static final Map<String,String> map = new HashMap<String,String>();
	
	public static void addInfo(String key,String value) {
		map.put(key, value);
	}
	public static void clear() {
		map.clear();
	}
	public static String getJSONInfo() {
		return JSONObject.toJSONString(map);
	}
	public static int size() {
		return map.size();
	}
}
