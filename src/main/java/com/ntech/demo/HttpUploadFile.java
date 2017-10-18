package com.ntech.demo;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.ntech.forward.Constant;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.ntech.util.ErrorPrompt;

/**
 * java通过模拟post方式提交表单实现图片上传功能
 */
public class HttpUploadFile {
	
	public static int status = 0;
	
	private final Logger logger = Logger.getLogger(HttpUploadFile.class);
	private static final String POST_URL = Constant.SDK_IP;
	private static HttpUploadFile instance;
	
    private BufferedWriter bufferedWriter = null;
    private BufferedReader bufferedReader = null;
    private OutputStream out = null;
	
	private HttpUploadFile() {}
	  public static HttpUploadFile getInstance(){    //对获取实例的方法进行同步
	         if (instance == null){
	             synchronized(HttpUploadFile.class){
	                if (instance == null)
	                    instance = new HttpUploadFile(); 
	             }
	         }
	       return instance;
	       }
	
    public synchronized String httpURLConnectionSDK( Map<String,String> header,Map<String, String> param,
            Map<String, Object> file,String meta) throws IOException {
    	//测试×××××××××××××××××××××××××××××××
    	logger.info("META: "+meta);
    		Iterator<Entry<String,String>>iterator1 = header.entrySet().iterator();
    		while(iterator1.hasNext()) {
    			Entry<String, String> entry = iterator1.next();
    			logger.info(entry.getKey()+":"+entry.getValue());
    		}
    	if(param!=null) {
    		Iterator<Entry<String,String>>iterator2 = param.entrySet().iterator();
    		while(iterator2.hasNext()) {
    			Entry<String, String> entry = iterator2.next();
    			logger.info(entry.getKey()+":"+entry.getValue());
    		}
    	}
     	if(file!=null) {
    		Iterator<Entry<String,Object>>iterator3 = file.entrySet().iterator();
    		while(iterator3.hasNext()) {
    			Entry<String, Object> entry = iterator3.next();
    			logger.info(entry.getKey()+":"+entry.getValue());
    		}
    	}
		//×××××××××××××××××××××××××××××××××××		
        String reply = "";
        String BOUNDARY = "-------------------------"+System.currentTimeMillis(); 
      
            URL url = new URL(POST_URL+header.get("API"));
            logger.info("URL :"+url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(50000);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod(header.get("Method"));
            connection.setRequestProperty("Authorization", "Token "+Constant.TOKEN);
            if(meta.equals("yes")) {
            	logger.info("META=YES");
            	connection.setRequestProperty("Content-Type","application/json");
	        	bufferedWriter = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
		        JSONObject.writeJSONString(param, bufferedWriter);
		        bufferedWriter.close();
		        bufferedWriter = null;
	        }else if(meta.equals("isFile")) {
	        	logger.info("META=ISFILE");
	            connection.setRequestProperty("Content-Type","multipart/form-data; boundary=" + BOUNDARY);
	            out = new DataOutputStream(connection.getOutputStream());
	            // text
	            if (null!=param&&param.size()!=0) {
	            	logger.info("******** FILE-TEXT ********");
	                StringBuffer stringBuffer = new StringBuffer();
	                Iterator<Entry<String, String>> iterator = param.entrySet().iterator();
	                while (iterator.hasNext()) {
	                    Entry<String, String> entry = iterator.next();
	                    String inputName = entry.getKey();
	                    String inputValue = entry.getValue();
	                    logger.info(inputName+":"+inputValue);
	                    if (inputValue == null) {
	                        continue;
	                    }
	                    stringBuffer.append("\r\n").append("--").append(BOUNDARY)
	                            .append("\r\n");
	                    stringBuffer.append("Content-Disposition: form-data; name=\""
	                            + inputName + "\"\r\n\r\n");
	                    stringBuffer.append(inputValue);
	                }
	                out.write(stringBuffer.toString().getBytes());
	            }
	            // file
	            if (null!=file&&file.size()!=0) {
	            	logger.info("******** FILE-PICTURE *******");
	                Iterator<Entry<String,Object>> iter = file.entrySet().iterator();
	                while (iter.hasNext()) {
	                    Entry<String, Object> entry = iter.next();
	                    String filed =  entry.getKey();
	                    if(filed.equals("contentType"))
	                    	   continue;
	                    if (entry.getValue() == null) {
	                        continue; 
	                    }
	                    //没有传入文件类型，同时根据文件获取不到类型，默认采用application/octet-stream
	                    String contentType = (String) file.get("contentType");
	                    logger.info("FILE_CONTENTTYPE: "+contentType);
	                    if (contentType == null || "".equals(contentType)) {
	                        contentType = "application/octet-stream";
	                    }
	                    StringBuffer strBuf = new StringBuffer();
	                    strBuf.append("\r\n").append("--").append(BOUNDARY)
	                            .append("\r\n");
	                    strBuf.append("Content-Disposition: form-data; name=\""
	                            + filed + "\"; filename=\"" + filed
	                            + "\"\r\n");
	                    strBuf.append("Content-Type:" + contentType + "\r\n\r\n");
	                    out.write(strBuf.toString().getBytes());
	                    byte[] pic = (byte[]) entry.getValue();
	                    out.write(pic);
	                }
	            }
	            byte[] endData = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();
	            out.write(endData);
	            out.flush();
	            out.close();
	        } 
            // 读取返回数据
            StringBuilder stringBuilder = new StringBuilder();
            status = connection.getResponseCode();
            if(status==204) {
            	status = 200;
				ErrorPrompt.addInfo("execute","successful");
				return null;
			}
            if(200!=status&&204!=status) {
				logger.error("ERROR_CODE: "+status);
				bufferedReader = new BufferedReader(new InputStreamReader(
	                    connection.getErrorStream()));
			}else {
				bufferedReader = new BufferedReader(new InputStreamReader(
	                    connection.getInputStream()));
			}
            logger.info("HTTP_CODE: "+status);
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            reply = stringBuilder.toString();
            bufferedReader.close();
            if (connection != null) {
                connection.disconnect();
            }
            return reply;
    }
}