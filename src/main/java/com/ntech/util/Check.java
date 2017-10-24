package com.ntech.util;

import java.util.*;

import com.ntech.model.Customer;
import com.ntech.model.LibraryKey;
import com.ntech.model.Log;
import com.ntech.model.SetMeal;
import com.ntech.service.inf.ICustomerService;
import com.ntech.service.inf.ILibraryService;
import com.ntech.service.inf.ILogService;
import com.ntech.service.inf.ISetMealService;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.ntech.exception.IllegalIDException;
import com.ntech.forward.ConnectionSDK;

import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 持久化层数据交互业务类
 */
@Component
public class Check {
	
	private static Logger logger = Logger.getLogger(Check.class);
	private static JSONParser jsonParser = new JSONParser();

	@Resource
	 private ICustomerService customerService ;
	@Resource
	 private ILibraryService libraryService;
	@Resource
	 private ISetMealService setMealService;
	@Resource
	 private ILogService logService;
	//检查用户是否持有对该id的操作权限
	public List<String> checkId(String id) {
		List<String> galleries = new ArrayList<String>();
		Map<String, String> header = new HashMap<String,String>();
		header.put("API","/v0/face/id/"+id);
		header.put("Method","GET");
		String reply = ConnectionSDK.getInstance().httpURLConnectionSDK(header);
		try {
			if(reply==null||"".equals(reply)) 
					throw new IllegalIDException("bad_id");
			JSONObject jsonObject = (JSONObject) jsonParser.parse(reply);
			JSONArray jsonArray =  (JSONArray) jsonObject.get("galleries");
			if(jsonArray!=null&&jsonArray.size()!=0) {
				@SuppressWarnings("unchecked")
				Iterator<String> iterator = jsonArray.iterator();
				while(iterator.hasNext()) {
					String gallery = iterator.next();
					if(!gallery.equals("default"))
						galleries.add(gallery);  
				}
				return galleries;
			}
		}catch (IllegalIDException e) {
			logger.error("BAD ID");
			ErrorPrompt.addInfo("error","bad_id");
			e.printStackTrace();
			return null;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	//检查用户token是否有效
	public  boolean checkToken(String inputToken)  {
		return customerService.checkToken(inputToken);
	}
	//获取用户model
	public  Customer getCustomerByToken(String inputToken)  {
		return customerService.findByToken(inputToken);
	}
	//获取用户持有的库列表
	public  List<String> getGalleries(String inputToken){
		List<String> galleryList = new ArrayList<String>();
		List<LibraryKey> list = libraryService.findByToken(inputToken);
		Iterator<LibraryKey> iterator = list.iterator();
		while(iterator.hasNext()){
			galleryList.add(iterator.next().getLibraryName());
		}
		return galleryList;
	}
	//创建库
	public  boolean createGallery(String userName,String galleryName){
		LibraryKey libraryKey = new LibraryKey();
		libraryKey.setLibraryName(galleryName);
		libraryKey.setUserName(userName);
		int result =  libraryService.create(libraryKey);
		if(result==0)
			try {
				throw new Exception("create failed");
			} catch (Exception e) {
				ErrorPrompt.addInfo("error",e.getMessage());
				e.printStackTrace();
				return false;
			}
		return true;
	}
	//删除库
	public boolean deleteGallery(String userName,String galleryName){
		LibraryKey libraryKey = new LibraryKey();
		libraryKey.setLibraryName(galleryName);
		libraryKey.setUserName(userName);
		int result =  libraryService.delete(libraryKey);
		if(result==0)
			try {
				throw new Exception("delete failed");
			} catch (Exception e) {
				ErrorPrompt.addInfo("error",e.getMessage());
				e.printStackTrace();
				return false;
			}
		return true;
	}
	//计费API剩余次数检查并计数
	public boolean mealTimesCount(String userName){
		SetMeal setMeal = setMealService.findByName(userName);
		if(!(setMeal.getLeftTimes()>0))
			try {
				throw new Exception("You don't have enough times");
			} catch (Exception e) {
				ErrorPrompt.addInfo("error",e.getMessage());
				e.printStackTrace();
				return false;
			}
		setMeal.setLeftTimes(setMeal.getLeftTimes()-1);
		return setMealService.modify(setMeal);
	}
	//套餐到期时间检查
    public boolean mealDateCheck(String userName){
        SetMeal setMeal = setMealService.findByName(userName);
        boolean result = setMeal.getEndTime().after(new Date());
        if(!result)
            try {
                throw new Exception("The set meal has expired");
            } catch (Exception e) {
                ErrorPrompt.addInfo("error",e.getMessage());
                e.printStackTrace();
            }
        return result;
    }
    //写入日志
    public void setLog(String userName,String content,int result) {
        Log log = new Log();
        log.setUserName(userName);
        log.setContent(content);
        log.setResult(result);
        log.setTime(new Date());
        logService.add(log);
    }
    //用户人脸添加计数
    public boolean setFaceNum(Customer customer,int alterNum,int meta){
		if(meta==1){
			customer.setFaceNumber(customer.getFaceNumber()+alterNum);
		}else{
			customer.setFaceNumber(customer.getFaceNumber()-1);
		}
		if(customer.getFaceNumber()<0)
			return false;
			int result = customerService.modify(customer);
			if(result!=1)
				try {
				 throw new Exception("setFaceNum failed");
				} catch (Exception e) {
				 ErrorPrompt.addInfo("code","sql failed");
				 e.printStackTrace();
				 return  false;
				}
		return true;
	}

    public String getContype(String userName) {
	    return setMealService.findByName(userName).getContype();
    }
}
