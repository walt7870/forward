package com.ntech.forward;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * 用于不带参数的http请求转发
 */
public class ConnectionSDK {
	
	private final Logger logger = Logger.getLogger(ConnectionSDK.class);
	//单例模式
	private static ConnectionSDK instance;
	private ConnectionSDK() {}
	  public static ConnectionSDK getInstance(){    //对获取实例的方法进行同步
	     if (instance == null){
	         synchronized(ConnectionSDK.class){
	            if (instance == null)
	                instance = new ConnectionSDK(); 
	         }
	     }
	   return instance;
	 }
	public synchronized String httpURLConnectionSDK (Map<String,String> header) {
		BufferedReader bufferedReader = null;
		StringBuilder stringBuilder = null;
		try {
			URL url = new URL(new StringBuilder(Constant.SDK_IP).append(header.get("API")).toString());
			logger.info("URL: "+url);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true); 
			connection.setConnectTimeout(10000);
			connection.setReadTimeout(50000);
			connection.setRequestMethod(header.get("Method"));
			connection.setRequestProperty("Authorization","Token "+Constant.TOKEN);
			connection.connect();
			
			if(200==connection.getResponseCode()) {
				InputStreamReader inputStream =new InputStreamReader(connection.getInputStream());
				bufferedReader = new BufferedReader(inputStream);                
				String lines;                
				stringBuilder = new StringBuilder();  
				while ((lines = bufferedReader.readLine()) != null) { 
					lines = new String(lines.getBytes(),"utf-8");
					stringBuilder.append(lines);   
				}
			}
			connection.disconnect();
		}catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			return null;
		}finally {
			try {
				if(bufferedReader!=null)
					bufferedReader.close();
			} catch (IOException e) {
				logger.error(e.getMessage());
				e.printStackTrace();
			}
		}
		if(stringBuilder!=null)
			return stringBuilder.toString();
		logger.info("ERROR: noReply");
		return null;
		
	}
}
