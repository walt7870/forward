package com.ntech.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ntech.forward.ConnectionSDK;
import com.ntech.forward.Constant;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.ntech.util.ConfigManager;
import com.ntech.util.Base64Encrypt;;

public class PutUtil {
	
	private static PutUtil instance;
	private static Logger logger = Logger.getLogger(PutUtil.class);
	private Map<String,String> param = new HashMap<String,String>();
	private Map<String,Object> body = new HashMap<String,Object>();
	
	private PutUtil() {}
	
	  public static PutUtil getInstance(){    //对获取实例的方法进行同步
	         if (instance == null){
	             synchronized(PutUtil.class){
	                if (instance == null)
	                    instance = new PutUtil(); 
	             }
	         }
	       return instance;
	  }
	public String requestForward(HttpServletRequest request,
			HttpServletResponse response) {
		logger.info("METHOD:"+request.getMethod());
		StringBuffer stringBuffer = new StringBuffer();
		try {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			BufferedReader bufferedReader = null;
			InputStream inputStream = request.getInputStream();
			String key = null;
			String value = null;
			//读取报文参数
			String meta = "no";
			if(inputStream.available()!=0) {
				logger.info("HAS PARAM");
				meta = "yes";
				bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"utf-8"));
				stringBuffer.append(bufferedReader.readLine());
				String putString = stringBuffer.toString();
				logger.info(putString);
				if((putString.indexOf("&"))!=-1) {
					String[] paramString = putString.split("&");
					for(int i=0;i<paramString.length;i++) {
						key = paramString[i].split("=")[0];
						value = paramString[i].split("=")[1];
						if(key!=""&&key!=null&&value!=""&&value!=null)
						body.put(key, value);
						logger.info(key+":"+value);
					}
				}else {
					key = putString.split("=")[0];
					value = putString.split("=")[1];
					if(key!=""&&key!=null&&value!=""&&value!=null)
					body.put(key, value);
					logger.info(key+":"+value);
				}
			}
			logger.info("META: "+meta);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonObject = null;
			try {
				jsonObject = (JSONObject) jsonParser.parse(stringBuffer.toString());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logger.info("ERROR:ParseException");
			}
			//转发报文参数
			@SuppressWarnings("unchecked")
			Iterator<Entry<String,Object>> iterator = jsonObject.entrySet().iterator();
			while(iterator.hasNext()) {
				Entry<String, Object> entry = iterator.next();
				logger.info("PARAM---"+entry.getKey()+":"+entry.getValue());
				body.put(entry.getKey(), entry.getValue());
			}
		//转发和设定报头信息
		param.put("Method",request.getMethod());
		param.put("Authorization", Constant.TOKEN);
		param.put("Content-Type", "application/json");
		param.put("API",(String) request.getAttribute("API"));
		
		String SDKreply =  ConnectionSDK.getInstance().httpURLConnectionSDK(param);
		
		if(SDKreply!=null&&!SDKreply.equals("")) {
			String string = Constant.PIC+"/"+Base64Encrypt.encryptUserName((String)request.getAttribute("userName"));
			return SDKreply.replaceAll("http://127.0.0.1:3333/uploads",string);
		}
		return null;
	}
}
