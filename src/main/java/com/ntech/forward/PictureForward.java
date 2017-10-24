package com.ntech.forward;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.ntech.exception.ErrorTokenException;
import com.ntech.util.Base64Encrypt;
import com.ntech.util.ErrorPrompt;

/**
 * 从FindFaceSDK获取图片并转发给用户
 */

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
		HttpURLConnection connection = null;
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
			   connection = (HttpURLConnection) url.openConnection();
	           connection.setDoInput(true); 
	           connection.setConnectTimeout(10000);
	           connection.setReadTimeout(30000);
	           connection.setRequestMethod("GET");
	           connection.connect();
        	   logger.info("http_code:"+connection.getResponseCode());
	           if(connection.getResponseCode()==200) {
	        	   inputStream =connection.getInputStream();
	        	   byte[] b = streamToByte(inputStream);
	        	   bufferedOutputStream = new BufferedOutputStream(dataOutputStream);
	        	   bufferedOutputStream.write(b);
	           }

		} catch (MalformedURLException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}finally {
			if(bufferedOutputStream!=null)
				try {
					bufferedOutputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			connection.disconnect();
		}
	}
	private static byte[] streamToByte(InputStream is) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int c = 0;
		byte[] buffer = new byte[8 * 1024];
		try {
			while ((c = is.read(buffer)) != -1) {
				baos.write(buffer, 0, c);
				baos.flush();
			}
			return baos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
					baos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}
