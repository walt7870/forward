package com.ntech.forward;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.ntech.exception.ErrorTokenException;
import com.ntech.util.Base64Encrypt;
import com.ntech.util.ErrorPrompt;


public class PictureForward {
	
	private Logger logger = Logger.getLogger(PictureForward.class);
	private static final String FORWARD_URL = Constant.PIC;
	
	private static PictureForward instance;
	private PictureForward() {}
	  public static PictureForward getInstance(){    //对获取实例的方法进行同步
	     if (instance == null){
	         synchronized(PictureForward.class){
	            if (instance == null)
	                instance = new PictureForward(); 
	         }
	     }
	   return instance;
	 }
	public synchronized void requestForward (HttpServletRequest request,HttpServletResponse response) {
		
		InputStream inputStream;
		OutputStream outputStream;
		DataInputStream dataInputStream = null;
		DataOutputStream dataOutputStream = null;
		BufferedInputStream bufferedInputStream = null;
		BufferedOutputStream bufferedOutputStream = null;
		try {
			outputStream = response.getOutputStream();
			dataOutputStream = new DataOutputStream(outputStream);
			response.setContentType("image");
			response.setHeader("Accept-Ranges", "bytes");
			String userName = (String) request.getAttribute("userName");
			logger.info("USER: "+userName);
			String pictureLocation = request.getRequestURI().split("uploads")[1];
			String encrypt = pictureLocation.substring(1,pictureLocation.indexOf("//"));
			logger.info(encrypt);
			String picMaster = Base64Encrypt.decryptUserName(encrypt);
			logger.info("PIC_MASTER: "+picMaster);
			if(userName==null||!userName.equals(picMaster))
				try {
					throw new ErrorTokenException("getPictureError");
				} catch (ErrorTokenException e) {
					ErrorPrompt.addInfo("error","bad_master");
					response.setContentType("application/json");
					response.setStatus(403);
					outputStream.write(ErrorPrompt.getJSONInfo().getBytes());
					e.printStackTrace();
					return;
				}
			URL url = new URL(FORWARD_URL+pictureLocation.substring(encrypt.length()+1));
			logger.info("URL: "+url);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	           connection.setDoInput(true); 
	           connection.setConnectTimeout(10000);
	           connection.setReadTimeout(30000);
	           connection.setRequestMethod("GET");
	           connection.connect();
        	   logger.info("http_code:"+connection.getResponseCode());
	           if(connection.getResponseCode()==200) {
	        	   inputStream =connection.getInputStream();
	        	   dataInputStream = new DataInputStream(inputStream);
	        	   bufferedInputStream = new BufferedInputStream(dataInputStream);
	        	   bufferedOutputStream = new BufferedOutputStream(dataOutputStream);
	        	   int n;
	               while ((n = bufferedInputStream.read())!=-1) { 
	            	   bufferedOutputStream.write(n);
	               }
	           }
	           connection.disconnect();
		} catch (MalformedURLException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}finally {
			if(bufferedInputStream!=null)
				try {
					dataInputStream.close();
				} catch (IOException e) {
					logger.error(e.getMessage());
					e.printStackTrace();
				}
			if(bufferedOutputStream!=null)
				try {
					dataOutputStream.close();
				} catch (IOException e) {
					logger.error(e.getMessage());
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}
}
