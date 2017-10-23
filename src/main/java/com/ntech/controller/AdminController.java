package com.ntech.controller;

import com.ntech.model.Customer;
import com.ntech.model.Log;
import com.ntech.model.SetMeal;
import com.ntech.service.inf.ICustomerService;
import com.ntech.service.inf.ILogService;
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
        String name= (String) session.getAttribute("admin");
        if(null!=name&&!"".equals(name)&&name.equals("sessionStatus")){
            return "admin/customerManager";
        }
        return "admin/login-admin";
    }
    //退出登录
    @RequestMapping("logout")
    public String logout(HttpSession session) {
        session.removeAttribute("admin");
        return "admin/login-admin";
    }
    //跳转到首页
    @RequestMapping("logManager")
    public String LogJump(HttpSession session) {
        String name= (String) session.getAttribute("admin");
        if(null!=name&&!"".equals(name)&&name.equals("sessionStatus")){
            return "admin/logManager";
        }
        return "admin/login-admin";
    }
    //跳转到首页
    @RequestMapping("mealManager")
    public String setMealJump(HttpSession session) {
        String name= (String) session.getAttribute("admin");
        if(null!=name&&!"".equals(name)&&name.equals("sessionStatus")){
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
    public boolean addCustomer(String userName,String password,String active,String email) throws MessagingException {
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
                return  true;
            }
            return false;
        }
        return false;
    }

    //修改密码
    @RequestMapping("updatePassword")
    public ModelAndView updatePassword(HttpSession session, String name, String password) {
        String adminName = (String) session.getAttribute("admin");
        ModelAndView modelAndView = new ModelAndView();
        if (adminName.equals("ntech")) {
            Customer customer = customerService.findByName(name);
            if (customer != null) {
                //密码加密
                customer.setPassword(SHAencrypt.encryptSHA(password));
                if (customerService.modify(customer) == 1) {
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
        String admin = (String) session.getAttribute("admin");
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
//               session.setAttribute("customerList", customerService.findAll());
//                 return mav;
//            }
//        }
//        mav.setViewName("error");
//        logger.info("customer update fail");
//        return mav;
        if(customerService.modify(customer)==1){
            logger.info("update customer succeed"+customer.getName());
            return true;
        }
        return false;
    }

    //查询所有用户
    @RequestMapping("findCustomers")
    @ResponseBody
    public JSONObject findCustomers(HttpSession session,int limit,int offset,String name) {
        String admin = (String) session.getAttribute("admin");

        JSONObject jsonObject=new JSONObject();
        if(name.equals("")){

            jsonObject.put("rows",customerService.findPage(limit,offset));
            jsonObject.put("total",customerService.totalCount());
        }else{
            List<Customer> list=new ArrayList<Customer>();
            list=customerService.findLikeName(name);
            jsonObject.put("rows",list);
            jsonObject.put("total",list.size());
        }
        return jsonObject;
    }

    //根据用户名删除用户
    @RequestMapping("deleteCustomers")
    @ResponseBody
    public boolean deleteCustomerByName(HttpSession session, String[] nameSelected) {
        String admin = (String) session.getAttribute("admin");
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
        if(nameSelected.length>0) {
            for (int i = 0; i < nameSelected.length; i++) {
                if (customerService.findByName(nameSelected[i]) != null) {
                    logger.info("delete " + nameSelected[i]);
                    customerService.deleteByName(nameSelected[i]);
                }
            }
            logger.info("delete customers success");
            return true;
        }
        return  false;
    }

    // 根据用户名删除用户和用户订单信息
    public Boolean deleteCustomerAndMealByName(HttpSession session, String name) {
        String admin = (String) session.getAttribute("admin");
        if (admin.equals("ntech")) {
            if (customerService.findByName(name) != null) {

                customerService.deleteByName(name);
            }
            if (setMealService.findByName(name) != null) {
                setMealService.delete(name);
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
        String admin = (String) session.getAttribute("admin");
        ModelAndView mav = new ModelAndView();
        if (admin.equals("ntech")) {

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
        String admin= (String) session.getAttribute("admin");
        JSONObject jsonObject=new JSONObject();
        if(!"".equals(admin)&&admin!=null&&admin.equals("sessionStatus")){
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
        String admin = (String) session.getAttribute("admin");
        ModelAndView mav = new ModelAndView();
        if (admin.equals("ntech")) {
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
        String admin = (String) session.getAttribute("admin");
        if("".equals(name)){
            name=null;
        }
        if("".equals(type)){
            type=null;
        }
        JSONObject jsonObject = new JSONObject();
        //logger.info("findSetMeals succeed");
        if(!"".equals(admin)&&null!=admin&&admin.equals("sessionStatus")){
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
        String admin = (String) session.getAttribute("admin");
        ModelAndView mav = new ModelAndView();
        if (admin.equals("ntech")) {
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
                                 String leftTimes, String enable) {
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
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            return true;
        }
    }
    @RequestMapping("addMeal")
    @ResponseBody
    public boolean addMeal(String userName, String contype,
                           String beginTime, String endTime, String totalTimes,
                           String leftTimes, String enable){
        SetMeal setMeal = new SetMeal();
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
                return  true;
            }
            return  false;
        }
    }
    //批量删除选择的订单
    @RequestMapping("deleteMealByName")
    @ResponseBody
    public boolean deleteMealByName(String[] selectedName) {
        // System.out.println("-------------------");
        if (selectedName != null) {
            for (int i = 0; i < selectedName.length; i++) {
                setMealService.delete(selectedName[i]);
            }
            logger.info("deleteMeal by selectedName[] succeed");
            return true;
        }

        return true;
    }
    @RequestMapping("apiChart")
    @ResponseBody
    /***
     *param session 域对象
     * return 各种api调用的次数的json数据
     */
    public JSONObject getApiData(HttpSession session){
        logger.info("get  api chart data ");
        JSONObject jsonObject=new JSONObject();
        String name=(String) session.getAttribute("name");
        int countDetect=0;
        int countIdentify=0;
        int countVerify=0;
        int countFace=0;
        // 根据session中的name判断查询权限
        if(!"".equals(name)&&name!=null){
            //根据姓名查出此人的使用api的日志
            List<Log> logList=logService.findByName(name);
            /*
             * 集合有三种遍历方式  传统for循环方式遍历最快，并发的时候有问题
             *迭代器性能稍差，但它是线程安全的 删除的时候只能用迭代器的remove方法
             * for each 性能最差，不建议使用
             */
            for(int i=0;i<logList.size();i++){
                Log log=logList.get(i);
                String content=log.getContent();
                if(content.contains("detect")){
                    countDetect++;
                }
                if (content.contains("identify")){
                    countIdentify++;
                }
                if (content.contains("verify")){
                    countVerify++;
                }
                if (content.contains("face")){
                    countFace++;
                }
            }

        }
        // 将数据存入jsonObject中
        jsonObject.put("detect",countDetect);
        jsonObject.put("verify",countVerify);
        jsonObject.put("identify",countIdentify);
        jsonObject.put("face",countFace);
        return jsonObject;
    }
}
