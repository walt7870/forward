package com.ntech.controller;

import com.ntech.model.Customer;
import com.ntech.model.Log;
import com.ntech.model.OperationLog;
import com.ntech.model.SetMeal;
import com.ntech.service.inf.ICustomerService;
import com.ntech.service.inf.ILogService;
import com.ntech.service.inf.IOperationLogService;
import com.ntech.service.inf.ISetMealService;
import com.ntech.util.SHAencrypt;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.mail.MessagingException;
import javax.servlet.http.HttpSession;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final static Logger logger = Logger.getLogger(AdminController.class);
    @Autowired
    private ISetMealService setMealService;
    @Autowired
    private ICustomerService customerService;
    @Autowired
    private ILogService logService;
    @Autowired
    private IOperationLogService operationLogService;


    //跳转登陆
    @RequestMapping("/login")
    public String loginJump(HttpSession session) {
        return "admin/login-admin";
    }

//
//    //跳转日志页面
//    @RequestMapping("log")
//    public ModelAndView logJump(HttpSession session) {
//        ModelAndView mav = new ModelAndView();
//        String name = (String) session.getAttribute("admin");
//        if (name.equals("ntech")) {
//            logger.info("log succeed");
//            session.setAttribute("logList", logService.findAll());
//            mav.setViewName("log");
//            return mav;
//        }
//        logger.info("log failed");
//        mav.setViewName("error");
//        return mav;
//    }

    //跳转到首页
    @RequestMapping("index")
    public String IndexJump(HttpSession session) {
        String adminFlag= (String) session.getAttribute("admin");
        if(null!=adminFlag&&!"".equals(adminFlag)&&adminFlag.equals("sessionStatus")){
            return "admin/customerManager";
        }
        return "admin/login-admin";
    }
    //退出登录
    @RequestMapping("logout")
    public String logout(HttpSession session) {
        session.removeAttribute("admin");
        session.removeAttribute("adminName");
        return "admin/login-admin";
    }
    //跳转到首页
    @RequestMapping("logManager")
    public String LogJump(HttpSession session) {
        String adminFlag= (String) session.getAttribute("admin");
        if(null!=adminFlag&&!"".equals(adminFlag)&&adminFlag.equals("sessionStatus")){
            return "admin/logManager";
        }
        return "admin/login-admin";
    }
    //跳转到首页
    @RequestMapping("mealManager")
    public String setMealJump(HttpSession session) {
        String adminFlag= (String) session.getAttribute("admin");
        if(null!=adminFlag&&!"".equals(adminFlag)&&adminFlag.equals("sessionStatus")){
            return "admin/mealManager";
        }
        return "admin/login-admin";
    }

    @RequestMapping("/checkName")
    @ResponseBody
    public boolean checkUserName(String userName) {
        logger.info("checkName");

        if(customerService.checkUserName(userName)){

            return true;
        }
        return false;
    }

    //登录
    @RequestMapping("/loginCheck")
    @ResponseBody
    public boolean login(String name, String password, HttpSession session) {
        logger.info("login check user:" + name);
        boolean result = false;
        if(null==name||!name.equals("admin")){
            return false;
        }
        result = customerService.loginCheck(name, password);
        if (result) {
            session.setAttribute("admin", "sessionStatus");
            session.setAttribute("adminName", name);
        }
        return result;
    }

    //用户名验证
    @RequestMapping("checkNameAndMeal")
    @ResponseBody
    public boolean checkName(String name) {
        if (name != null) {
            Customer customer = customerService.findByName(name);
            if (customer != null) {
                SetMeal meal = setMealService.findByName(name);
                if (meal == null) {
                    return true;
                }
                return false;
            }
            return false;
        }

        return false;
    }

    //-------------------------------------用户----------------------------------------------//
    //添加用户
    @RequestMapping("/addCustomer")
    @ResponseBody
    public boolean addCustomer(String userName,String password,String active,String email,HttpSession session) throws MessagingException {
        String adminFlag =(String) session.getAttribute("admin");
        String adminName =(String)session.getAttribute("adminName");
        if(adminFlag.equals("sessionStatus")){
            Customer customer=new Customer();
            if(userName!=null&&password!=null){
                customer.setName(userName);
//            customer.setToken(token);
                customer.setPassword(SHAencrypt.encryptSHA(password));
                customer.setEmail(email);
                customer.setContype("user");
                customer.setRegtime(new Date());
                customer.setFaceNumber(0);
                if(active.equals("active")){
                    customer.setActive(1);
                }else{
                    customer.setActive(0);
                }
                if(customerService.add(customer)==1){
                    String log="用户管理：添加用户 "+userName;
                    operateLog(adminName,log);
                    return  true;
                }
            }
            return false;
        }else{
            return false;
        }
    }

    //插入操作日志记录
    private void operateLog(String operator,String log) {
        OperationLog operationLog =new OperationLog();
        operationLog.setOperator(operator);
        operationLog.setContent(log);
        operationLog.setTime(new Date());
        operationLogService.add(operationLog);
    }

    //修改密码
    @RequestMapping("updatePassword")
    public ModelAndView updatePassword(HttpSession session, String name, String password) {
        String adminFlag = (String) session.getAttribute("admin");
        String adminName =(String)session.getAttribute("adminName");
        ModelAndView modelAndView = new ModelAndView();
        if (adminFlag.equals("sessionStatus")) {
            Customer customer = customerService.findByName(name);
            if (customer != null) {
                //密码加密
                customer.setPassword(SHAencrypt.encryptSHA(password));
                if (customerService.modify(customer) == 1) {
                    String log="更改用户 "+name+" 的密码";
                    operateLog(adminName,log);
                    logger.info("updatePassword succeed");
                    modelAndView.addObject("msg", "更改密码成功");
                }
            } else {
                logger.info("updatePassword failed");
                modelAndView.addObject("msg", "查无此人");
            }
            modelAndView.setViewName("information");
            return modelAndView;
        }
        modelAndView.setViewName("error");
        return modelAndView;
    }

    //更新用户
    @RequestMapping("updateCustomer")
    @ResponseBody
    public boolean updateCustomer(HttpSession session,String
            userName,String contype,String active,String email,
                                  String token,String regtime) {
        String adminFlag = (String) session.getAttribute("admin");
        String adminName =(String) session.getAttribute("adminName");
        if (adminFlag.equals("sessionStatus")) {
            Customer customer=new Customer();
            customer.setName(userName);
            if(active.equals("active")){
                customer.setActive(1);
            }else if(active.equals("unactive")){
                customer.setActive(0);
            }
            if(contype!=null&&!contype.equals("")){
                customer.setContype(contype);
            }
            customer.setEmail(email);
            customer.setToken(token);
            if(regtime!=null){
                SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
                try{
                    customer.setRegtime(format.parse(regtime));
                }catch (Exception e){
                    throw  new RuntimeException(e);
                }
            }
//        ModelAndView mav = new ModelAndView();
//        //根据session判断可否操作
//        if (admin.equals("ntech")) {
//            if (customerService.modify(customer) == 1) {
//                logger.info("customer update success");
//                mav.setViewName("customer");
//                session.setAttribute("customerList", customerService.findAll());
//                return mav;
//            }
//        }
//        mav.setViewName("error");
//        logger.info("customer update fail");
//        return mav;
            if(customerService.modify(customer)==1){
                logger.info("update customer succeed"+customer.getName());
                String log ="用户管理：修改用户 "+userName+" 的信息";
                operateLog(adminName,log);
                return true;
            }
        }

        return false;
    }

    //查询所有用户
    @RequestMapping("findCustomers")
    @ResponseBody
    public JSONObject findCustomers(HttpSession session,int limit,int offset,String name) {
        String adminFlag = (String) session.getAttribute("admin");
        JSONObject jsonObject=new JSONObject();
        if (adminFlag.equals("sessionStatus")) {
            if(name.equals("")){

                jsonObject.put("rows",customerService.findPage(limit,offset));
                jsonObject.put("total",customerService.totalCount());
            }else{
                List<Customer> list=new ArrayList<Customer>();
                list=customerService.findLikeName(name);
                jsonObject.put("rows",list);
                jsonObject.put("total",list.size());
            }
        }
        return jsonObject;
    }

    //根据用户名删除用户
    @RequestMapping("deleteCustomers")
    @ResponseBody
    public boolean deleteCustomerByName(HttpSession session, String[] nameSelected) {
        String adminFlag = (String) session.getAttribute("admin");
        String adminName = (String) session.getAttribute("adminName");
        if(adminFlag.equals("sessionStatus")){
            if(nameSelected.length>0) {
                for (int i = 0; i < nameSelected.length; i++) {
                    if (customerService.findByName(nameSelected[i]) != null) {
                        logger.info("delete " + nameSelected[i]);
                        customerService.deleteByName(nameSelected[i]);
                        String log ="用户管理：删除用户 "+nameSelected[i];
                        operateLog(adminName,log);
                    }
                }
                logger.info("delete customers success");
                return true;
            }
        }
//        ModelAndView mav = new ModelAndView();
//        if (admin.equals("ntech")) {
//            if (customerService.findByName(name) != null) {
//                logger.info("delete customer succeed");
//                customerService.deleteByName(name);
//            }
//            logger.info("delete customer succeed");
//            session.setAttribute("customerList", customerService.findAll());
//            mav.setViewName("customer");
//            return mav;
        return  false;
    }

    // 根据用户名删除用户和用户订单信息
    public Boolean deleteCustomerAndMealByName(HttpSession session, String name) {
        String adminFlag = (String) session.getAttribute("admin");
        String adminName = (String) session.getAttribute("adminName");
        if (adminFlag.equals("sessionStatus")) {
            if (customerService.findByName(name) != null) {

                customerService.deleteByName(name);
                String log ="用户管理：删除用户 "+name;
                operateLog(adminName,log);
            }
            if (setMealService.findByName(name) != null) {
                setMealService.delete(name);
                String log ="订单管理：删除用户 "+name+" 的订单信息";
                operateLog(adminName,log);
            }
            logger.info("deleteCustomerAll succeed ");
            return true;
        }

        return false;
    }
//----------------------------日志-------------------------------------------------//

    //查询日志
    @RequestMapping("findLogByName")
    public ModelAndView findLogByName(HttpSession session, String name) {
        String adminFlag = (String) session.getAttribute("admin");
        ModelAndView mav = new ModelAndView();
        if (adminFlag.equals("sessionStatus")) {

            List<Log> list = logService.findByName(name);
            if (list != null) {
                logger.info("findLogByName succeed");
                session.setAttribute("logList", logService.findByName(name));
                mav.setViewName("log");
                return mav;
            }

            logger.info("findLogByName failed");
            mav.setViewName("error");
            return mav;
        }
        logger.info("findLogByName failed");
        mav.setViewName("error");
        return mav;
    }

    @RequestMapping("findLogs")
    @ResponseBody
    public JSONObject findLogs(HttpSession session, int limit,int offset,String name,
                               String type,String start,String over)  {
        String adminFlag= (String) session.getAttribute("admin");
        JSONObject jsonObject=new JSONObject();
        if(!"".equals(adminFlag)&&adminFlag!=null&&adminFlag.equals("sessionStatus")){
            if("".equals(name)){
                name=null;
            }
            if("".equals(type)){
                type=null;
            }
            if ("".equals(start)){
                start=null;
            }
            if ("".equals(over)){
                over=null;
            }
            List<Log> list=logService.findWithConditions(name,limit,offset,type,start,over);
            long total=logService.findCount(name, limit, offset, type, start, over);
            jsonObject.put("total",total);
            jsonObject.put("rows",list);
        }else{
            jsonObject.put("total",0);
            jsonObject.put("rows",null);
        }
        return jsonObject;
    }

    //------------------------------订单------------------------------------------------//
    //查询所有订单
    @RequestMapping("findSetMeals")
    public ModelAndView findSetMeals(HttpSession session) {
        String adminFlag = (String) session.getAttribute("admin");
        ModelAndView mav = new ModelAndView();
        if (adminFlag.equals("sessionStatus")) {
            logger.info("findSetMeals succeed");
            session.setAttribute("mealList", setMealService.findAll());
            mav.setViewName("meal");
            return mav;
        }
        logger.info("findSetMeal failed");
        mav.setViewName("error");
        return mav;
    }

    @RequestMapping("findMeals")
    @ResponseBody
    public JSONObject testMeals(HttpSession session,int limit,int offset,String name,String type) {
        String adminFlag = (String) session.getAttribute("admin");
        if("".equals(name)){
            name=null;
        }
        if("".equals(type)){
            type=null;
        }
        JSONObject jsonObject = new JSONObject();
        //logger.info("findSetMeals succeed");
        if(!"".equals(adminFlag)&&null!=adminFlag&&adminFlag.equals("sessionStatus")){
            List<SetMeal> list=setMealService.findByConditions(offset,limit,name,type);
            jsonObject.put("rows",list);
            jsonObject.put("total",setMealService.findCount(name,type));
            logger.info("findSetMeals succeed");
        }else{
            logger.info("dont hava privilege");
            jsonObject.put("rows",null);
            jsonObject.put("total",0);
        }

        return jsonObject;
    }

    //根据姓名查询订单
    @RequestMapping("findSetMealByName")
    public ModelAndView findSetMealByName(HttpSession session, String name) {
        String adminFlag = (String) session.getAttribute("admin");
        ModelAndView mav = new ModelAndView();
        if (adminFlag.equals("sessionStatus")) {
            logger.info("findSetMeals succeed");
            ArrayList<SetMeal> mealList = new ArrayList<SetMeal>();
            mealList.add(setMealService.findByName(name));
            session.setAttribute("mealList", mealList);
            mav.setViewName("meal");
            return mav;
        }
        logger.info("findSetMeal failed");
        mav.setViewName("error");
        return mav;
    }

    @RequestMapping("/updateSetMeal")
    @ResponseBody
    public boolean updateSetMeal(String id, String userName, String contype,
                                 String beginTime, String endTime, String totalTimes,
                                 String leftTimes, String enable,HttpSession session) {
        String adminFlag =(String)session.getAttribute("admin");
        String adminName =(String)session.getAttribute("adminName");
        if(adminFlag.equals("sessionStatus")){
            SetMeal setMeal = new SetMeal();
            try {
                //给对象赋值
                setMeal.setId(Integer.parseInt(id));
                setMeal.setUserName(userName);
                setMeal.setContype(contype);

                //字符串向时间类型进行转换
                if (contype.equals("date")) {
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    setMeal.setBeginTime(format.parse(beginTime));
                    setMeal.setEndTime(format.parse(endTime));

                    setMeal.setLeftTimes(null);
                    setMeal.setTotalTimes(null);
                } else if (contype.equals("times")) {
                    setMeal.setBeginTime(null);
                    setMeal.setEndTime(null);
                    setMeal.setTotalTimes(Integer.parseInt(totalTimes));
                    setMeal.setLeftTimes(Integer.parseInt(leftTimes));
                }
                if (enable.equals("able")) {
                    setMeal.setEnable(1);
                } else if (enable.equals("enable")) {
                    setMeal.setEnable(0);
                }
//            System.out.println(setMeal.toString());
                setMealService.modify(setMeal);
                String log ="套餐管理：修改用户 "+userName+" 的套餐信息";
                operateLog(adminName,log);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {

                return true;
            }
        }
        return false;
    }
    @RequestMapping("addMeal")
    @ResponseBody
    public boolean addMeal(String userName, String contype,
                           String beginTime, String endTime, String totalTimes,
                           String leftTimes, String enable,HttpSession session){
        String adminFlag =(String)session.getAttribute("admin");
        String adminName =(String)session.getAttribute("adminName");
        SetMeal setMeal = new SetMeal();
        if(adminFlag.equals("sessionStatus")){
            try {
                //给对象赋值
                setMeal.setUserName(userName);
                setMeal.setContype(contype);
                //字符串向时间类型进行转换
                if (contype.equals("date")) {
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    setMeal.setBeginTime(format.parse(beginTime));
                    setMeal.setEndTime(format.parse(endTime));
                } else if (contype.equals("times")) {
                    setMeal.setTotalTimes(Integer.parseInt(totalTimes));
                    setMeal.setLeftTimes(Integer.parseInt(leftTimes));
                }
                if (enable.equals("able")) {
                    setMeal.setEnable(1);
                } else if (enable.equals("enable")) {
                    setMeal.setEnable(0);
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {

                if(setMealService.add(setMeal)){
                    String log ="套餐管理：新增用户 "+userName+" 的套餐信息";
                    operateLog(adminName,log);
                    return  true;
                }
            }
        }
        return  false;
    }
    //批量删除选择的订单
    @RequestMapping("deleteMealByName")
    @ResponseBody
    public boolean deleteMealByName(String[] selectedName,HttpSession session) {
        // System.out.println("-------------------");
        String adminFlag =(String)session.getAttribute("admin");
        String adminName =(String)session.getAttribute("adminName");
        if(adminFlag.equals("sessionStatus")){
            if (selectedName != null) {
                for (int i = 0; i < selectedName.length; i++) {
                    setMealService.delete(selectedName[i]);
                    String log ="套餐管理：删除用户 "+selectedName[i]+" 的套餐信息";
                    operateLog(adminName,log);
                }
                logger.info("deleteMeal by selectedName[] succeed");
                return true;
            }
        }
        return false;
    }
}