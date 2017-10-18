package com.ntech.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import com.ntech.model.Customer;
import org.apache.log4j.Logger;

import com.ntech.exception.ErrorTokenException;
import com.ntech.exception.IllegalAPIException;
import com.ntech.forward.Constant;

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

	}
}
