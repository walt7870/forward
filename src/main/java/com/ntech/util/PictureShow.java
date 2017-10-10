package com.ntech.util;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;

public class PictureShow {
	
	private Logger logger = Logger.getLogger(PictureShow.class);
	
	private static PictureShow instance;
	private PictureShow() {}
	  public static PictureShow getInstance(){    //对获取实例的方法进行同步
	     if (instance == null){
	         synchronized(PictureShow.class){
	            if (instance == null)
	                instance = new PictureShow(); 
	         }
	     }
	   return instance;
	 }
	public synchronized String getBase64Picture (String Url) {
		InputStream inputStream ;
		BufferedInputStream bufferedInputStream;
		DataInputStream dataInputStream = null;
		HttpURLConnection connection = null;
		try {
			URL url = new URL(Url);
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
	        	   byte[] b=streamToByte(inputStream);
	        	   /*dataInputStream = new DataInputStream(inputStream);
	        	   bufferedInputStream = new BufferedInputStream(dataInputStream);

	        	   byte[] b = new byte[connection.getContentLength()];
	        	   logger.info("ContentLength: "+connection.getContentLength());
	        	   bufferedInputStream.read(b);

				   byte[] bytes = new byte[1024];

				   while ((byteCount = inputStream.read(bytes)) != -1)
				   {
				   outputStream.write(bytes, bytesWritten, byteCount);
					   bytesWritten += byteCount;
				   }*/
	        	   return "data:image/jpeg;base64,"+Base64Encrypt.byteArrayToString(b);
	           }
		} catch (MalformedURLException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}finally {
			if(connection!=null)
				connection.disconnect();

		}
		return null;
	}


	public static byte[] streamToByte(InputStream is) {
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
