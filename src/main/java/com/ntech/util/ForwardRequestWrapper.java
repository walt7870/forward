package com.ntech.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import com.ntech.model.Customer;
import org.apache.log4j.Logger;

import com.ntech.exception.ErrorTokenException;
import com.ntech.exception.IllegalAPIException;
import com.ntech.forward.Constant;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * request请求包装类
 * Token验证
 */
public class ForwardRequestWrapper extends HttpServletRequestWrapper {
	//获取用于与持久化层做数据交互的工具类实例
	private static Check check = (Check) Constant.GSB.getBean("check");
	
	private static Logger logger = Logger.getLogger(ForwardRequestWrapper.class);

	public ForwardRequestWrapper(HttpServletRequest request) throws ErrorTokenException {
		
		super(request);

		String inputToken = request.getHeader("Token");
		if(inputToken==null||inputToken.equals(""))
			throw new ErrorTokenException("bad_token");
		//检查用户token是否有效
		boolean isToken = check.checkToken(inputToken);
		if(!isToken)
			throw new ErrorTokenException("bad_token");
		//将用户token放入request作用域
		request.setAttribute("inputToken",inputToken);
		logger.info("InputToken :"+inputToken);
		//在用户token有效时获取用户名
		Customer customer = check.getCustomerByToken(inputToken);
		logger.info("UserName: "+customer.getName());
		//将用户对象和用户名放入request作用域
		request.setAttribute("userName",customer.getName());
		request.setAttribute("customer",customer);
		request.setAttribute("Method",request.getMethod());
		if(request.getMethod().equals("PUT")){
			Map<String,String> param = new HashMap<String,String>();
			try {
				InputStreamReader inputStreamReader = new InputStreamReader(request.getInputStream());
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
				String s;
				String key = "";
				while((s = bufferedReader.readLine())!=null){
					if(s.contains("Content-Disposition: form-data")) {
						key = s.substring(38, s.length() - 1);
						param.put(key, null);
						continue;
					}
					if(s.equals("")||s.startsWith("----"))
						continue;
					param.put(key,s);
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
			request.setAttribute("PUT_PARAM",param);
		}
	}
}
