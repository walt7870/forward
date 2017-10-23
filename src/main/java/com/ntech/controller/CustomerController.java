package com.ntech.controller;

import com.mongodb.util.JSON;
import com.ntech.demo.ConnectionSDK;
import com.ntech.forward.Constant;
import com.ntech.demo.HttpUploadFile;
import com.ntech.demo.MethodUtil;
import com.ntech.model.Customer;
import com.ntech.model.LibraryKey;
import com.ntech.model.SetMeal;
import com.ntech.service.inf.ICustomerService;
import com.ntech.service.inf.ILibraryService;
import com.ntech.service.inf.ISetMealService;
import com.ntech.util.CommonUtil;
import com.ntech.util.PayUtil;
import com.ntech.util.PictureShow;
import com.ntech.util.SHAencrypt;
import com.pingplusplus.model.Charge;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import javax.imageio.ImageIO;
import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.security.PublicKey;
import java.util.*;
import java.util.List;


@Controller
@RequestMapping("/customer")
public class CustomerController {

    private static final Logger logger = Logger.getLogger(CustomerController.class);

//    @Autowired
//    private HttpServletResponse response;

    @Autowired
    ICustomerService customerService;

    @Autowired
    ISetMealService setMealService;

    @Autowired
    ILibraryService libraryService;

    @RequestMapping("/register")
    public String jumpToRegister() {
        logger.info("jump to register page");
        return "register";
    }


    @RequestMapping("/check")
    @ResponseBody
    public Boolean checkUserName(@RequestParam String userName) {
        logger.info("checkLogin");
        return customerService.checkUserName(userName);
    }

    @RequestMapping("/appCommitReg")
    @ResponseBody
    public boolean appRegister(@RequestParam("name") String name, String password, String email) {
        if (null == name || "".equals(name) || null == password || "".equals(password) || null == email || "".equals(email)) {
            return false;
        }
        logger.info("commit appregister");
        Customer customer = new Customer();
        customer.setName(name);
        customer.setContype("0");
        customer.setFaceNumber(0);
        customer.setPassword(SHAencrypt.encryptSHA(password));
        customer.setEmail(email);
        try {
            customerService.add(customer);
        } catch (MessagingException e) {
            logger.error(e.getMessage());
            return false;
        }
        return true;
    }

    @RequestMapping("/commitReg")
    public ModelAndView register(@RequestParam("name") String name, String password, String email) {
        ModelAndView mav = new ModelAndView("msg");
        if (null == name || "".equals(name) || null == password || "".equals(password) || null == email || "".equals(email)) {
            mav.addObject("msg", "注册信息有误，请重新填写");
            return mav;
        }

        logger.info("commit register");
        Customer customer = new Customer();
        customer.setName(name);
        customer.setFaceNumber(0);
        customer.setContype("0");
        customer.setPassword(SHAencrypt.encryptSHA(password));
        customer.setEmail(email);
        try {
            customerService.add(customer);
        } catch (MessagingException e) {
            logger.error(e.getMessage());
            mav.addObject("msg", "注册失败");
            mav.setViewName("error");
        }
        mav.setViewName("login");
//        mav.addObject("msg", "注册成功，请到邮箱激活账号");
        return mav;

    }

    @RequestMapping("record")
    public String recordLogJump(HttpSession session) {
        String name = (String) session.getAttribute("name");
        if(null==name||"".equals(name)){
            return "error";
        }
        return "record-log";
    }

    @RequestMapping("login")
    public String loginJump(HttpSession session) {
        String name = (String) session.getAttribute("name");
        if(null!=name&&!name.equals("")){
            return "info";
        }
        return "login";
    }

    @RequestMapping("exit")
    public String exitJump(HttpSession session) {
        session.removeAttribute("name");
        return "login";
    }

    //登录
    @RequestMapping("loginCheck")
    @ResponseBody
    public boolean login(String name, String password, HttpSession session) {
        logger.info("login check user:" + name);
        boolean result = false;
        result = customerService.loginCheck(name, password);
        if (result) {
            session.setAttribute("name", name);
        }
        return result;
    }

    //修改密码
    @RequestMapping("modify-password")
    public String modifyPassword(){
        return "modify-password";
    }

    @RequestMapping("validate-change")
    @ResponseBody
    public String validateAndChangePwd(HttpSession session,String oldPwd,String newPwd){
        String name = (String) session.getAttribute("name");
        if(name==null||"".equals(name)){
            return "null";
        }
        if(null==newPwd||"".equals(newPwd)||null==oldPwd||"".equals(oldPwd)){
            return "errorParam";
        }
        if(!CommonUtil.checkPassword(newPwd)){
            return "errorNewPwd";
        }
        if(oldPwd.equals(newPwd)){
            return "equals";
        }
        if(customerService.modifyPwd(name,oldPwd,newPwd)){
            session.removeAttribute("name");
            return "success";
        }
        return "errorInfo";
    }

    //忘记密码跳转
    @RequestMapping("forget-password")
    public String forgetPasswrod(){
        return "forget-password";
    }

    @RequestMapping("handle-forget")
    @ResponseBody
    public String handleForgetPwd(HttpSession session,String name,String email){
        if(null==name||null==email||"".equals(name)||"".equals(email)){
            return "null";
        }
        if(!CommonUtil.checkUserName(name)){
            return "name";
        }
        if(!CommonUtil.checkEmail(email)){
            return "email";
        }
        if(customerService.forgetPwd(name,email)){
            session.removeAttribute("name");
            return "success";
        }
        return "error";
    }


    @RequestMapping("forgetPwdPageCheck")
    public ModelAndView forgetPwdPageCheck(HttpSession session,String name, String validateCode, String email) {
        ModelAndView mav = new ModelAndView("error");
        if (null == name || "".equals(name) || null == validateCode || "".equals(validateCode)) {
            return mav;
        }
        Customer customer = customerService.findByName(name);
        if (validateCode.equals(SHAencrypt.encryptSHA(customer.getEmail()))&&customer.getEmail().equals(email)) {
            session.setAttribute("ForgetPwdFlag",name);
            mav.setViewName("forget-change");
        }
        return mav;
    }

    @RequestMapping("change-newPwd")
    @ResponseBody
    public String changeToNewPwd(HttpSession session,String newPwd){
        String name = (String) session.getAttribute("ForgetPwdFlag");
        if(name==null||"".equals(name)){
            return "errorPage";
        }
        if(null==newPwd||"".equals(newPwd)||!CommonUtil.checkPassword(newPwd)){
            return "errorParam";
        }
        Customer customer = customerService.findByName(name);
        if(customer!=null){
            customer.setPassword(SHAencrypt.encryptSHA(newPwd));
            if(customerService.modify(customer)==1){
                session.removeAttribute("ForgetPwdFlag");
                return "success";
            }
        }
        return "error";
    }

    @RequestMapping("active")
    public ModelAndView activeEmail(String name, String validateCode, String email) {
        ModelAndView mav = new ModelAndView();
        if (null == name || "".equals(name) || null == validateCode || "".equals(validateCode)) {
            mav.addObject("msg", "验证信息有误，请重新注册");
            mav.setViewName("error");
            return mav;
        }
        Customer customer = customerService.findByName(name);
        if (validateCode.equals(SHAencrypt.encryptSHA(customer.getEmail()))) {
            customer.setActive(1);
            if (customerService.modify(customer) == 1) {
                logger.info(customer.getName() + "激活成功");
                mav.setViewName("msg");
                mav.addObject("msg", "激活成功<a href=\"login\">点击登录</a>");
                return mav;
            }
            ;
        }
        mav.addObject("msg", "出现未知错误");
        mav.setViewName("error");
        return mav;
    }

    @RequestMapping("verifyCode")
    public void VerifyCode(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException {
        int width = 63;
        int height = 37;
        Random random = new Random();
        //设置response头信息
        //禁止缓存
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);

        //生成缓冲区image类
        BufferedImage image = new BufferedImage(width, height, 1);
        //产生image类的Graphics用于绘制操作
        Graphics g = image.getGraphics();
        //Graphics类的样式
        g.setColor(this.getRandColor(200, 250));
        g.setFont(new Font("Times New Roman", 0, 28));
        g.fillRect(0, 0, width, height);
        //绘制干扰线
        for (int i = 0; i < 40; i++) {
            g.setColor(this.getRandColor(130, 200));
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            int x1 = random.nextInt(12);
            int y1 = random.nextInt(12);
            g.drawLine(x, y, x + x1, y + y1);
        }

        //绘制字符
        String strCode = "";
        for (int i = 0; i < 4; i++) {
            String rand = String.valueOf(random.nextInt(10));
            strCode = strCode + rand;
            g.setColor(new Color(20 + random.nextInt(110), 20 + random.nextInt(110), 20 + random.nextInt(110)));
            g.drawString(rand, 13 * i + 6, 28);
        }
        //将字符保存到session中用于前端的验证

        session.setAttribute("strCode", strCode);
        logger.info(strCode);
        g.dispose();

        ImageIO.write(image, "JPEG", response.getOutputStream());
        response.getOutputStream().flush();
    }

    @RequestMapping("checkCode")
    @ResponseBody
    public boolean checkCode(HttpSession session, String authCode) {
        String preCode = (String) session.getAttribute("strCode");
        logger.info("precode" + preCode);
        logger.info("commitcode" + authCode);
        if (preCode.equalsIgnoreCase(authCode)) {
            logger.info("validate success");
            return true;
        }

        return false;

    }


    /**
     * 页面跳转控制
     * 1.个人中心
     * 2.套餐购买
     * 3.人脸探测
     * 4.人脸对比
     * 5.图库管理
     * 6.使用记录
     * 8.登录跳转
     */
    //登录成功后进入主页面
    @RequestMapping("personInfo")
    public ModelAndView customerInfo(HttpSession session) {
        ModelAndView mav = new ModelAndView();
        String name = (String) session.getAttribute("name");
        if (name == null || name.equals("") || !customerService.checkUserName(name)) {
//            mav.addObject("msg", "用户存在异常");
            mav.setViewName("login");
            return mav;
        }
        SetMeal meal = setMealService.findByName(name);
        Customer customer = customerService.findByName(name);
        if (meal != null) {

            if (meal.getContype().equals("date")) {
                //计算剩余天数
                int leftDay = (int) ((meal.getEndTime().getTime() -
                        new Date().getTime()) / (1000 * 3600 * 24));
                if(leftDay<0){leftDay=0;}
                session.setAttribute("leftDay", leftDay);
            }
        }
        session.setAttribute("meal", meal);
        session.setAttribute("customer", customer);

        mav.setViewName("info");
        return mav;
    }

    @RequestMapping("setMeal")
    public String setMealJump(HttpSession session) {
        String name = (String) session.getAttribute("name");
        if(null==name||"".equals(name)){
            return "login";
        }
        return "set-meal";
    }

//    @RequestMapping("detect")
//    public String faceDetectJump() {
//        return "show-detect";
//    }
//
//    @RequestMapping("verify")
//    public String faceVerifyJump() {
//        return "show-verify";
//    }

    //个人相册,判断session中是否有用户名,根据session中的用户名来获取该用户的相册
    @RequestMapping("gallery")
    public ModelAndView galleryManageJump(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        ModelAndView modelAndView = new ModelAndView();
        try {
            String name = (String) session.getAttribute("name");
            if (null != name) {
                List<LibraryKey> galleries = libraryService.findLKByUserName(name);
                modelAndView.setViewName("show-gallery");
                logger.info(galleries);
                modelAndView.addObject("galleries", galleries);
//                modelAndView.addObject("test1", "test1Content");
//                modelAndView.addObject("test2", "test2Content");
            } else {
//                logger.info("");
                modelAndView.setViewName("login");
//                modelAndView.addObject("msg", "用户会话过期,请重新登录 <a href='login'>点击登录</a>");

            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            modelAndView.setViewName("msg");
            modelAndView.addObject("msg", "操作异常");
            return modelAndView;
        }
        return modelAndView;
    }

    @RequestMapping("gallery-demo")
    public ModelAndView getDemoGallery(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("show-gallery-demo");
        String name = (String) session.getAttribute("name");
        if(null==name||"".equals(name)){
            modelAndView.setViewName("login");
            return modelAndView;
        }
        String result = null;
        try {
            logger.info(request.getRequestURI());
            if (request.getRequestURI().equals("/customer/gallery-demo")) {
                request.setAttribute("localAPI", "/v0/faces/gallery/demo_default");
            }
            if (null != name && !"".equals(name)) {
                modelAndView.setViewName("show-gallery");
                request.setAttribute("localAPI", "/v0/faces/gallery/" + name);
            }
            result = MethodUtil.getInstance().requestForward(request, response);
            logger.info(result);
            modelAndView.addObject("galleryDemo", wrapResponse(result));
        } catch (Exception e) {
            logger.error("request error");
            modelAndView.setViewName("msg");
            modelAndView.addObject("msg", "request error");
        }
//        modelAndView.addObject("demoGallery",result);
        return modelAndView;
    }


    //在用户库中的人脸做搜索,如果session中的用户名不存在则则demo_defalut库中搜索
    @RequestMapping("getDemoFace")
    @ResponseBody
    public JSONArray getDemoSearchFace(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String name = (String) session.getAttribute("name");
        String result = null;
        if (null != name && !"".equals(name)) {
            request.setAttribute("localAPI", "/v0/faces/gallery/" + name + "/identify");
        } else {
            request.setAttribute("localAPI", "/v0/faces/gallery/demo_default/identify");
        }
        result = MethodUtil.getInstance().requestForward(request, response);
        if (null != result && !"".equals(result)) {
            return wrapResponseForSearch(result);
        }


        return null;

    }
    //在用户库中的人脸做搜索,如果session中的用户名不存在则则demo_defalut库中搜索
    @RequestMapping("getDemoAllFace")
    @ResponseBody
    public JSONArray getDemoSearchAllFace(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String name = (String) session.getAttribute("name");
        String result = null;
        if (null != name && !"".equals(name)) {
            request.setAttribute("localAPI", "/v0/faces/gallery/" + name + "/identify");
        } else {
            request.setAttribute("localAPI", "/v0/faces/gallery/demo_default/identify");
        }
        result = MethodUtil.getInstance().requestForward(request, response);
        if (null != result && !"".equals(result)) {
            return wrapResponseForSearchAll(result);
        }


        return null;

    }


    //通过用户名来获取图库信息,如果session中没有用户名则获取demo_default库中的用户名
    @RequestMapping("getMyGallery")
    @ResponseBody
    public JSONArray getMyGallery(HttpSession session, HttpServletRequest request, HttpServletResponse response) throws ParseException {
        String name = (String) session.getAttribute("name");
        String result = null;
        if (null != name && !"".equals(name)) {
            request.setAttribute("localAPI", "/v0/faces/gallery/" + name);
        } else {
            request.setAttribute("localAPI", "/v0/faces/gallery/demo_default");
        }
        result = MethodUtil.getInstance().requestForward(request, response);

        logger.info(result);

        return wrapResponse(result);
    }


    //添加人脸到个人库

    @RequestMapping("addToGallery")
    @ResponseBody
    public String addToGallery(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
        String name = (String) session.getAttribute("name");
        String result = null;
        if (null != name && !"".equals(name)) {
            if(!customerService.checkFaceNumber(name)){
                return "0";
            }
            if (request.getRequestURI().equals("/customer/addToGallery")) {
                request.setAttribute("localAPI", "/v0/face/");
            }
            request.setAttribute("personFaceInsert", name);
            result = MethodUtil.getInstance().requestForward(request, response);
            JSONObject tmpResultObj=null;
            try {
                tmpResultObj= (JSONObject) new JSONParser().parse(result);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (tmpResultObj != null&&tmpResultObj.get("results")!=null) {
                JSONArray faceNumArray= (JSONArray) tmpResultObj.get("results");
                //添加后获取最新的图片列表
                int faceNumber=1;
                if(null!=faceNumArray&&faceNumArray.size()>0){
                    faceNumber=faceNumArray.size();
                    customerService.operateFaceNumber(name,1,faceNumber);
                    result = getMyGalleryLocal(name);
                }


            }
            return wrapResponse(result).toJSONString();
        }
        return "login";
    }


    //通过id删除个人人脸库中的图片
    @RequestMapping("deleteToGallery")
    @ResponseBody
    public String deleteToGallery(String id , HttpSession session, HttpServletRequest request, HttpServletResponse response) {
        String name = (String) session.getAttribute("name");
        logger.info(id);
        HashMap<String,String> header = new HashMap<String,String>();
        header.put("Method","DELETE");
        header.put("API","/v0/face/id/"+id);
        String result = null;
        if (null != name && !"".equals(name)) {
            if(!customerService.checkFaceNumber(name)){
                return "1";
            }
            try {
                HttpUploadFile.getInstance().httpURLConnectionSDK(header, null, null, "no");
            } catch (IOException e) {
                e.printStackTrace();
            }
            request.setAttribute("idCheck", name);
            customerService.operateFaceNumber(name,0,1);
            //检查图片id是否合法,检查方法待定,id合法后才能进    行下一步操作
//            if(null!=picId&&!"".equals(picId)) {
//                request.setAttribute("localApi","/v0/face/id/"+picId);
//                MethodUtil.getInstance().requestForward(request, response);
//            }
        }
        result = getMyGalleryLocal(name);
        return wrapResponse(result).toJSONString();
    }

//    @RequestMapping("addGallery")
//    @ResponseBody
//    public boolean addGallery(String libraryName, HttpSession session) {
//        if (null == libraryName || "".equals(libraryName)) {
//            return false;
//        }
//        String name = (String) session.getAttribute("name");
//        if (null != name) {
//            if (!libraryService.checkLibrary(name, libraryName)) {
//                LibraryKey libraryKey = new LibraryKey();
//                libraryKey.setUserName(name);
//                libraryKey.setLibraryName(libraryName);
//                libraryService.insert(libraryKey);
//            }
//        }
//        return false;
//    }

//    //删除图库
//    @RequestMapping("deleteGallery")
//    @ResponseBody
//    public boolean deleteGallery(String libraryName) {
//
//        return false;
//    }


    @RequestMapping("setMealBuy")
    @ResponseBody
    public boolean setMeal(HttpSession session, @RequestParam("type") String type, @RequestParam("value") String value) {
        String name = (String) session.getAttribute("name");
        if (null != name) {
            if ("".equals(name) || null == type || "".equals(type) || null == value || "".equals(value)) {
                return false;
            }
            if (!(type.equals("date") || type.equals("times"))) {
                return false;
            }
            if (!customerService.checkUserName(name)) {
                logger.error("username is not exist");
                return false;
            }

            int intValue = Integer.parseInt(value);
            SetMeal setMeal = new SetMeal();
            setMeal.setUserName(name);
            setMeal.setContype(type);

            //找出数据库中的订单
            SetMeal meal = setMealService.findByName(setMeal.getUserName());
            if (meal != null) {
                //判断订单类型  一个用户只能购买一种类型的订单
                if (!meal.getContype().equals(setMeal.getContype())) {
                    logger.info("chose two diffence contype");
                    return false;

                }
            }

            if (type.equals("date")) {
                if (intValue == 1 || intValue == 3 || intValue == 6 || intValue == 12) {
                    setMeal.setBeginTime(new Date());
                    setMeal.setEndTime(count(intValue));
                } else {
                    return false;
                }
            } else {
                if (intValue == 100 || intValue == 300 || intValue == 500 || intValue == 1000) {
                    setMeal.setTotalTimes(intValue);
                    setMeal.setLeftTimes(intValue);
                } else {
                    return false;
                }
            }
            if (setMealService.add(setMeal)) {
                return true;
            }
        }
        return false;
    }


    //转发接口
    @RequestMapping(value = {"detect-face", "verify-face",})
    @ResponseBody
    public String forwardDetect(HttpServletRequest request, HttpServletResponse response) {
        String result = null;
        try {
            logger.info(request.getRequestURI());
            if (request.getRequestURI().equals("/customer/detect-face")) {
                request.setAttribute("localAPI", "/v1/detect");
            }
            if (request.getRequestURI().equals("/customer/verify-face")) {
                request.setAttribute("localAPI", "/v0/verify");
            }
            result = MethodUtil.getInstance().requestForward(request, response);
        } catch (Exception e) {
            logger.error("request error");
            return "request error";
        }
        return result;
    }
//
//    @RequestMapping("")
//    @ResponseBody
//    public String forwardVerify(HttpServletRequest request, HttpServletResponse response) {
//        String result = null;
//        return result;
//    }

    //探测人脸个数

    @RequestMapping("faceNumber")
    @ResponseBody
    public int getFaceNum(HttpServletRequest request, HttpServletResponse response){
        request.setAttribute("localAPI", "/v1/detect");
        String result = MethodUtil.getInstance().requestForward(request, response);
        if(result!=null){
            try {
                JSONObject faceResult= (JSONObject) new JSONParser().parse(result);
                JSONArray faceArray= (JSONArray) faceResult.get("faces");
                if(faceArray==null){
                    response.setStatus(200);
                    return 0;
                }
                return faceArray.size();
            } catch (Exception e) {
                logger.error("parse error");
                return 0;
            }
        }
        return 0;
    }


    //获取用户图库信息
    private String getMyGalleryLocal(String name) {
        String result = null;
        HashMap<String, String> header = new HashMap<String, String>();
        header.put("Method", "GET");
        header.put("Authorization", Constant.TOKEN);
        header.put("API", "/v0/faces/gallery/" + name);

        result = ConnectionSDK.getInstance().httpURLConnectionSDK(header);

        return result;
    }

    private JSONArray wrapResponseForSearchAll(String result) {
        JSONObject jsonResult = null;
        JSONArray jsonArrayResult = new JSONArray();
        try {
            logger.info(result);
            jsonResult = (JSONObject) new JSONParser().parse(result);
            JSONObject jsonResult1 = (JSONObject) jsonResult.get("results");
            JSONArray jsonArrayFace=null;
            for(Object key:jsonResult1.keySet()){
                JSONObject finalSingle = new JSONObject();
                jsonArrayFace= (JSONArray) jsonResult1.get(key);
                if(jsonArrayFace.size()==0){
                    continue;
                }
                for (int i = 0; i < jsonArrayFace.size(); i++) {
                    JSONObject tmpJson = (JSONObject) jsonArrayFace.get(i);
                    JSONObject tmpJson1 = (JSONObject) tmpJson.get("face");
                    String confidence=tmpJson.get("confidence")+"";
                    String tmpStrng = (String) tmpJson1.get("normalized");
//                    tmpStrng = "http://192.168.10.208" + tmpStrng.substring(16);
                    String picBase64=PictureShow.getInstance().getBase64Picture(tmpStrng);
                    JSONObject objectTemp=(JSONObject) jsonArrayFace.get(i);
                    tmpJson1.remove("normalized");
                    tmpJson1.remove("photo");
                    tmpJson1.remove("thumbnail");
                    tmpJson1.put("normalized", picBase64);
                    tmpJson1.put("photo", "photoUrl");
                    tmpJson1.put("thumbnail", "thumbnailUrl");

                    finalSingle.put("confidence",confidence);
                    finalSingle.put("face",tmpJson1);
                    finalSingle.put("box",key);
                }
                jsonArrayResult.add(finalSingle);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return jsonArrayResult;
    }
    //包装返回的请求
    private JSONArray wrapResponseForSearch(String result) {
        JSONObject jsonResult = null;
        JSONArray jsonArrayResult = new JSONArray();
        try {
            logger.info(result);
            jsonResult = (JSONObject) new JSONParser().parse(result);
            JSONObject jsonResult1 = (JSONObject) jsonResult.get("results");
            JSONArray jsonArrayFace=null;
            for(Object key:jsonResult1.keySet()){
                jsonArrayFace= (JSONArray) jsonResult1.get(key);
            }
            for (int i = 0; i < jsonArrayFace.size(); i++) {
                JSONObject tmpJson = (JSONObject) jsonArrayFace.get(i);

                JSONObject tmpJson1 = (JSONObject) tmpJson.get("face");
                String confidence=tmpJson.get("confidence")+"";
                String tmpStrng = (String) tmpJson1.get("normalized");
//                tmpStrng = "http://192.168.10.208" + tmpStrng.substring(16);
                String picBase64=PictureShow.getInstance().getBase64Picture(tmpStrng);
                JSONObject objectTemp=(JSONObject) jsonArrayFace.get(i);
                tmpJson1.remove("normalized");
                tmpJson1.remove("photo");
                tmpJson1.remove("thumbnail");
                tmpJson1.put("normalized", picBase64);
                tmpJson1.put("photo", "photoUrl");
                tmpJson1.put("thumbnail", "thumbnailUrl");
                JSONObject finalSingle = new JSONObject();
                finalSingle.put("confidence",confidence);
                finalSingle.put("face",tmpJson1);
                jsonArrayResult.add(finalSingle);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return jsonArrayResult;
    }
  //包装返回的请求
    private JSONArray wrapResponse(String result) {
        JSONObject jsonResult = null;
        JSONArray jsonArray = null;
        try {
            logger.info(result);
            jsonResult = (JSONObject) new JSONParser().parse(result);
            jsonArray = (JSONArray) jsonResult.get("results");
             for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject tmpJson = (JSONObject) jsonArray.get(i);
                String tmpStrng = (String) tmpJson.get("normalized");
//                tmpStrng = "http://192.168.10.208" + tmpStrng.substring(16);
                String picBase64=PictureShow.getInstance().getBase64Picture(tmpStrng);
                ((JSONObject) jsonArray.get(i)).put("normalized", picBase64);
                ((JSONObject) jsonArray.get(i)).put("photo", "photoUrl");
                ((JSONObject) jsonArray.get(i)).put("thumbnail", "thumbnailUrl");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return jsonArray;
    }
    //创建订单
    @RequestMapping("creatCharge")
    @ResponseBody
    public Charge Creat(int  value, String type,String channel,int amount){
        Charge charge= PayUtil.createCharge(1000,"alipay_pc_direct");


        return charge;

    }
    //撤销订单
    public Charge reserve(String id){
        Charge charge=PayUtil.reverse(id);


        return  charge;
    }
    //查询订单
    public Charge retrieve(String id){

        return PayUtil.retrieve(id);
    }
    private Date count(int value) {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(new Date());
        gc.add(2, value);
        return gc.getTime();
    }
    @RequestMapping(value = "webhooks")
    @ResponseBody
    public void webhooks ( HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException  {
       /*System.out.println("ping++　webhooks");*/
        request.setCharacterEncoding("UTF8");
        //获取头部所有信息
        Enumeration headerNames = request.getHeaderNames();
        String signature=null;
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            String value = request.getHeader(key);
            if("x-pingplusplus-signature".equals(key)){
                signature=value;
            }
        }
       /*System.out.println("signature"+signature);*/
        // 获得 http body 内容
        Enumeration bodyNames=request.getParameterNames();
        JSONObject event=new JSONObject();
        while(bodyNames.hasMoreElements()) {
            String key = (String) bodyNames.nextElement();
            String value = request.getParameter(key);
            event.put(key, value);
        }
        boolean verifyRS=false;
        try {
            PublicKey publicKey= PayUtil.getPublicKey();
         /*  System.out.println(publicKey);*/
            verifyRS=PayUtil.verifyData(event.toJSONString(),signature,publicKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("result:"+verifyRS);
        if(verifyRS) {
            //支付成功
//            System.out.println();
            if ("charge.succeeded".equals(event.get("type"))) {
                JSONObject data = (JSONObject) JSON.parse(event.get("data").toString());
                JSONObject object = (JSONObject) JSON.parse(data.get("object").toString());
                String orderId = (String) object.get("order_no");
                String channel = (String) object.get("channel");
                String payType = null;
                int amountFen = (int) object.get("amount");
                Double amountYuan = amountFen * 1.0 / 100;//ping++扣款,精确到分，而数据库精确到元
                Double weiXinInput = null;
                Double aliPayInput = null;
                Double bankCardInput = null;
                response.setStatus(200);
            }
        }else{
            response.setStatus(500);
        }
    }
    //创建颜色
    Color getRandColor(int fc, int bc) {
        Random random = new Random();
        if (fc > 255)
            fc = 255;
        if (bc > 255)
            bc = 255;
        int r = fc + random.nextInt(bc - fc);
        int g = fc + random.nextInt(bc - fc);
        int b = fc + random.nextInt(bc - fc);
        return new Color(r, g, b);
    }
}
