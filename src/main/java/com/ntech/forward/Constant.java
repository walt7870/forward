package com.ntech.forward;

import com.ntech.util.ConfigManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 实体类
 */
public class Constant {
	
	private static final ConfigManager sdk = ConfigManager.getInstance();
	//FindFaceSDK转发配置
	public static final String TOKEN = sdk.getParameter("TOKEN");
	public static final String SDK_IP = sdk.getParameter("SDK_IP");
	public static final String PIC = sdk.getParameter("PIC");
	public static final String CHARSET = sdk.getParameter("CHARSET");
	//Spring上下文---依赖注入工具类
	public static final ApplicationContext GSB = new ClassPathXmlApplicationContext("/spring-mybatis.xml");
	//计费数据
	public static final String Detect0 = sdk.getParameter("Detect0");
	public static final String Detect1 = sdk.getParameter("Detect1");
	public static final String Verify = sdk.getParameter("Verify");
	public static final String Identify = sdk.getParameter("Identify");
	public static final String Face = sdk.getParameter("Face");
	public static final String Gallery = sdk.getParameter("Gallery");

}
