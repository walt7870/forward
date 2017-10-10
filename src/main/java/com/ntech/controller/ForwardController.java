package com.ntech.controller;

import com.ntech.exception.IllegalAPIException;
import com.ntech.exception.IllegalGalleryException;
import com.ntech.exception.IllegalIDException;
import com.ntech.forward.Constant;
import com.ntech.forward.HttpUploadFile;
import com.ntech.forward.MethodUtil;
import com.ntech.forward.PictureForward;
import com.ntech.model.Customer;
import com.ntech.util.Base64Encrypt;
import com.ntech.util.Check;
import com.ntech.util.ConfigManager;
import com.ntech.util.ErrorPrompt;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * API转发Controller
 */
@Controller
@RequestMapping("/anytec")
public class ForwardController {

    //日志
    private final static Logger logger = Logger.getLogger(ForwardController.class);
    //获取用于与持久化层做数据交互的工具类实例
    private static Check check = (Check)Constant.GSB.getBean("check");
    //************************************************************************************
    //                      DETECT  人脸探测
    //************************************************************************************
    @RequestMapping(method = RequestMethod.POST,value="/{version:[v][01]}/detect")
    @ResponseBody
    public String detect(@PathVariable("version")String version, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        logger.info("*****enter Controller*****");
        String API = new StringBuilder("/").append(version).append("/detect").toString();
        //判断探测API的venison是v1还是v0并对需要计费的API做记录
        if(version.equals("v1")){
            request.setAttribute("chargeAPI", "detect1");
            request.setAttribute("charge", Constant.Detect1);
        }else {
            request.setAttribute("chargeAPI", "detect0");
            request.setAttribute("charge", Constant.Detect0);
        }
        //将API放到request作用域中，用于MethodUtil调用
        request.setAttribute("API",API);
        logger.info("CHECK_API :" + API);
        //获取FindFaceSDK响应
        String reply = MethodUtil.getInstance().requestForward(request, response);
        //程序是否正常执行，若有异常则返回错误提示
        if (ErrorPrompt.size() != 0)
            reply = ErrorPrompt.getJSONInfo();
        return reply;
    }
    //************************************************************************************
    //                     VERIFY  人脸对比
    //************************************************************************************

    @RequestMapping(value = "/{version:[v][01]}/verify",method = RequestMethod.POST)
    @ResponseBody
    public String verify(@PathVariable("version")String version, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        logger.info("*****enter Controller*****");
        String API = new StringBuilder("/").append(version).append("/verify").toString();
        //计费API记录
        request.setAttribute("chargeAPI", "verify");
        request.setAttribute("charge", Constant.Verify);
        //将API放到request作用域中，用于MethodUtil调用
        request.setAttribute("API",API);
        logger.info("CHECK_API :" + API);
        //获取FindFaceSDK响应
        String reply = MethodUtil.getInstance().requestForward(request, response);
        //异常检查
        if (ErrorPrompt.size() != 0)
            reply = ErrorPrompt.getJSONInfo();
        return reply;
    }
    //************************************************************************************
    //                     IDENTIFY  人脸搜索匹配
    //************************************************************************************
    @RequestMapping(value = "/{version:[v][01]}/identify",method = RequestMethod.POST)
    @ResponseBody
    public String identify(@PathVariable("version")String version, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        logger.info("*****enter Controller*****");
        String userName = (String)request.getAttribute("userName");
        String API = new StringBuilder("/").append(version).append("/faces/gallery/").append(userName).append("/identify").toString();
        //计费API记录
        request.setAttribute("chargeAPI", "identify");
        request.setAttribute("charge", Constant.Identify);
        //将API放到request作用域中，用于MethodUtil调用
        request.setAttribute("API",API);
        logger.info("CHECK_API :" + API);
        //获取FindFaceSDK响应
        String reply = MethodUtil.getInstance().requestForward(request, response);
        //异常检查
        if (ErrorPrompt.size() != 0)
            reply = ErrorPrompt.getJSONInfo();
        return reply;
    }
    //************************************************************************************
    //                IDENTIFY（GALLERY） 指定库的人脸搜索匹配
    //************************************************************************************

    @RequestMapping(method = RequestMethod.POST,value = "/{version:[v][01]}/faces/gallery/{gallery:\\w{1,48}}/identify")
    @ResponseBody
    public String identifyGallery(@PathVariable("version")String version,@PathVariable("gallery")String gallery, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        logger.info("*****enter Controller*****");
        String userName = (String)request.getAttribute("userName");
        //根据从token，检查并返回用户所有库列表
        List<String> galleries = check.getGalleries(request.getHeader("Token"));
        //用户库名包装
        if(!gallery.equals(userName))
            gallery = new StringBuilder(gallery).append("_anytec_" + userName).toString();
        //用户指定搜索库权限检测，用户是否拥有该库
        if (!galleries.contains(gallery))
            try {
                throw new IllegalGalleryException("bad_gallery");
            } catch (IllegalGalleryException e) {
                response.setStatus(403);
                logger.error("*****BAD_GALLERY*****@"+userName);
                ErrorPrompt.addInfo("error","bad_gallery");
                e.printStackTrace();
                return ErrorPrompt.getJSONInfo();
            }
        String API = new StringBuilder("/").append(version).append("/faces/gallery/").append(gallery).append("/identify").toString();
        //计费API
        request.setAttribute("chargeAPI", "identify");
        request.setAttribute("charge", Constant.Identify);
        //将API放到request作用域中，用于MethodUtil调用
        request.setAttribute("API",API);
        logger.info("CHECK_API :" + API);
        //获取FindFaceSDK响应
        String reply = MethodUtil.getInstance().requestForward(request, response);
        //异常检查
        if (ErrorPrompt.size() != 0)
            reply = ErrorPrompt.getJSONInfo();
        return reply;
    }
    //************************************************************************************
    //                      FACE  人脸探测和信息入库
    //************************************************************************************
    @RequestMapping(value = "/{version:[v][01]}/face",method = RequestMethod.POST)
    @ResponseBody
    public String face(@PathVariable("version")String version, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, ParseException {
        logger.info("*****enter Controller*****");
        //获取用户model
        Customer customer =(Customer) request.getAttribute("customer");
        //用户已添加的人脸数检查
        if(!(customer.getFaceNumber()<51)){
            ErrorPrompt.addInfo("error","more than 200 faces in your galleries");
            return ErrorPrompt.getJSONInfo();
        }
        //计费API
        request.setAttribute("chargeAPI", "face");
        request.setAttribute("charge", Constant.Face);
        String API = new StringBuilder("/").append(version).append("/face").toString();
        logger.info("CHECK_API :" + API);
        //获取用户拥有库列表，用于MethodUtil类的galleries参数检查
        List<String> galleries = check.getGalleries(request.getHeader("Token"));
        request.setAttribute("galleries", galleries);
        //将API放到request作用域中，用于MethodUtil调用
        request.setAttribute("API",API);
        String reply = MethodUtil.getInstance().requestForward(request, response);
        if (ErrorPrompt.size() != 0)
            return ErrorPrompt.getJSONInfo();
        // 解析响应，获取用户添加的人脸数并记录日志
        if(HttpUploadFile.status!=200)
            return reply;
        if(reply.contains("\"galleries\": [\"default\"]")){
            response.setStatus(500);
            ErrorPrompt.addInfo("error","add face failed");
            return ErrorPrompt.getJSONInfo();
        }
        JSONParser jsonParser = new JSONParser();
        JSONArray results = (JSONArray) ((JSONObject)jsonParser.parse(reply)).get("results");
        int addFace = results.size();
        boolean result = check.setFaceNum(customer,addFace,1);
        if(!result)
            return ErrorPrompt.getJSONInfo();
        check.setLog(customer.getName(),new StringBuilder("face ￥").append(Constant.Face).append(" * ").append(addFace).toString(),1);
        return reply;
    }
    //************************************************************************************
    //              FACE/META  根据指定meta获取人脸
    //************************************************************************************

    @RequestMapping(method = RequestMethod.GET,value = "/{version:[v][01]}/face/meta/{meta:\\S*}")
    @ResponseBody
    public String faceMeta(@PathVariable("version")String version,@PathVariable("meta")String meta, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        logger.info("*****enter Controller*****");
        String userName = (String)request.getAttribute("userName");
        StringBuilder API = new StringBuilder("/").append(version).append("/face/gallery/").append(userName).append("/meta/").append(meta);
        String nextPage = request.getParameter("max_id");
        String prePage = request.getParameter("min_id");
        if (nextPage != null && !nextPage.equals(""))
            API.append("?max_id=").append(nextPage);
        if (prePage != null && !prePage.equals(""))
            API.append("?min_id=").append(prePage);
        logger.info("CHECK_API :" + API.toString());
        request.setAttribute("API",API.toString());
        String reply = MethodUtil.getInstance().requestForward(request, response);
        if (ErrorPrompt.size() != 0)
            reply = ErrorPrompt.getJSONInfo();
        return reply;
    }
    //************************************************************************************
    //            FACE/META  同上,获取meta为空值的人脸
    //************************************************************************************
    @RequestMapping(method = RequestMethod.GET,value = "/{version:[v][01]}/face/meta/")
    @ResponseBody
    public String faceMetaS(@PathVariable("version")String version,HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        logger.info("*****enter Controller*****");
        String userName = (String)request.getAttribute("userName");
        StringBuilder API = new StringBuilder("/").append(version).append("/face/gallery/").append(userName).append("/meta/");
        String nextPage = request.getParameter("max_id");
        String prePage = request.getParameter("min_id");
        if (nextPage != null && !nextPage.equals(""))
            API.append("?max_id=").append(nextPage);
        if (prePage != null && !prePage.equals(""))
            API.append("?min_id=").append(prePage);
        logger.info("CHECK_API :" + API.toString());
        request.setAttribute("API",API.toString());
        String reply = MethodUtil.getInstance().requestForward(request, response);
        if (ErrorPrompt.size() != 0)
            reply = ErrorPrompt.getJSONInfo();
        return reply;
    }
    //************************************************************************************
    //               FACE/META  根据meta获取指定库的人脸
    //************************************************************************************

    @RequestMapping(method = RequestMethod.GET,value = "/{version:[v][01]}/face/gallery/{gallery:\\w+}/meta/{meta:\\S*}")
    @ResponseBody
    public String faceMetaGallery(@PathVariable("version")String version,@PathVariable("gallery")String galleryName,@PathVariable("meta")String meta, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        logger.info("*****enter Controller*****");
        String userName = (String)request.getAttribute("userName");
        if(!galleryName.equals(userName))
            galleryName = new StringBuilder(galleryName).append("_anytec_" + userName).toString();
        List<String> galleries = check.getGalleries((String)request.getAttribute("inputToken"));
        if (!galleries.contains(galleryName))
            try {
                throw new IllegalGalleryException("bad_gallery");
            } catch (IllegalGalleryException e) {
                response.setStatus(403);
                logger.error("*****BAD_GALLERY*****@"+userName);
                ErrorPrompt.addInfo("error","bad_gallery");
                e.printStackTrace();
                return ErrorPrompt.getJSONInfo();
            }
        StringBuilder API = new StringBuilder("/").append(version).append("/face/gallery/").append(galleryName).append("/meta/").append(meta);
        String nextPage = request.getParameter("max_id");
        String prePage = request.getParameter("min_id");
        if (nextPage != null && !nextPage.equals(""))
            API.append("?max_id=").append(nextPage);
        if (prePage != null && !prePage.equals(""))
            API.append("?min_id=").append(prePage);
        logger.info("CHECK_API:" + API.toString());
        request.setAttribute("API",API.toString());
        String reply = MethodUtil.getInstance().requestForward(request, response);
        if (ErrorPrompt.size() != 0)
            reply = ErrorPrompt.getJSONInfo();
        return reply;
    }
    //************************************************************************************
    //              FACE/META   同上,获取指定库meta为空值的人脸
    //************************************************************************************
    @RequestMapping(method = RequestMethod.GET,value = "/{version:[v][01]}/face/gallery/{gallery:\\w+}/meta/")
    @ResponseBody
    public String faceMetaGalleryS(@PathVariable("version")String version,@PathVariable("gallery")String galleryName, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        logger.info("*****enter Controller*****");
        String userName = (String)request.getAttribute("userName");
        if(!galleryName.equals(userName))
            galleryName = new StringBuilder(galleryName).append("_anytec_" + userName).toString();
        List<String> galleries = check.getGalleries((String)request.getAttribute("inputToken"));
        if (!galleries.contains(galleryName))
            try {
                throw new IllegalGalleryException("bad_gallery");
            } catch (IllegalGalleryException e) {
                response.setStatus(403);
                logger.error("*****BAD_GALLERY*****@"+userName);
                ErrorPrompt.addInfo("error","bad_gallery");
                e.printStackTrace();
                return ErrorPrompt.getJSONInfo();
            }
        StringBuilder API = new StringBuilder("/").append(version).append("/face/gallery/").append(galleryName).append("/meta/");
        String nextPage = request.getParameter("max_id");
        String prePage = request.getParameter("min_id");
        if (nextPage != null && !nextPage.equals(""))
            API.append("?max_id=").append(nextPage);
        if (prePage != null && !prePage.equals(""))
            API.append("?min_id=").append(prePage);
        logger.info("CHECK_API:" + API.toString());
        request.setAttribute("API",API.toString());
        String reply = MethodUtil.getInstance().requestForward(request, response);
        if (ErrorPrompt.size() != 0)
            reply = ErrorPrompt.getJSONInfo();
        return reply;
    }
    //************************************************************************************
    //                META  根据meta分组展示指定库人脸
    //************************************************************************************

    @RequestMapping(method = RequestMethod.GET,value = "/{version:[v][01]}/meta/gallery/{gallery:\\w+}")
    @ResponseBody
    public String metaGallery(@PathVariable("version")String version,@PathVariable("gallery")String galleryName, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        logger.info("*****enter Controller*****");
        String userName = (String)request.getAttribute("userName");
        List<String> galleries = check.getGalleries((String)request.getAttribute("inputToken"));
        if(!galleryName.equals(userName))
            galleryName = new StringBuilder(galleryName).append("_anytec_" + userName).toString();
        if (!galleries.contains(galleryName))
            try {
                throw new IllegalGalleryException("bad-gallery");
            } catch (IllegalGalleryException e) {
                response.setStatus(403);
                logger.error("*****BAD_GALLERY*****@"+userName);
                ErrorPrompt.addInfo("error","bad_gallery");
                e.printStackTrace();
                return ErrorPrompt.getJSONInfo();
            }
        String API = new StringBuilder("/").append(version).append("/meta/gallery/").append(galleryName).toString();
        logger.info("CHECK_API :" + API);
        //将API放到request作用域中，用于MethodUtil调用
        request.setAttribute("API",API);
        String reply = MethodUtil.getInstance().requestForward(request, response);
        if (ErrorPrompt.size() != 0)
            reply = ErrorPrompt.getJSONInfo();
        return reply;
    }
    //************************************************************************************
    //                 META  根据META展示多有库人脸
    //************************************************************************************
    @RequestMapping(method = RequestMethod.GET,value = "/{version:[v][01]}/meta")
    @ResponseBody
    public String meta(@PathVariable("version")String version, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        logger.info("*****enter Controller*****");
        String userName = (String)request.getAttribute("userName");
        String API = new StringBuilder("/").append(version).append("/meta/gallery/").append(userName).toString();
        logger.info("CHECK_API :" + API);
        request.setAttribute("API",API);
        String reply = MethodUtil.getInstance().requestForward(request, response);
        if (ErrorPrompt.size() != 0)
            reply = ErrorPrompt.getJSONInfo();
        return reply;
    }
    //************************************************************************************
    //              GET - FACE/ID  根据id获取人脸
    //************************************************************************************
    @RequestMapping(value="/{version:[v][01]}/face/id/{id:\\d{1,24}}",method = RequestMethod.GET)
    @ResponseBody
    public String faceIdGET(@PathVariable("version")String version,@PathVariable("id")String id,HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        logger.info("*****enter Controller*****");
        Customer customer =(Customer) request.getAttribute("customer");
        String userName = customer.getName();
        boolean isMaster = false;
        List<String> galleries = check.getGalleries((String)request.getAttribute("inputToken"));
        List<String> list = check.checkId(id);
        if(list==null)
            return ErrorPrompt.getJSONInfo();
        Iterator<String> iterator = list.iterator();
        while (iterator.hasNext()) {
            if (galleries.contains(iterator.next())) {
                isMaster = true;
                break;
            }
        }
        if (!isMaster)
            try {
                throw new IllegalIDException("bad_id");
            } catch (IllegalIDException e) {
                response.setStatus(403);
                logger.error("*****BAD_ID*****@"+userName);
                ErrorPrompt.addInfo("error","bad_id");
                e.printStackTrace();
                return ErrorPrompt.getJSONInfo();
            }
        String API = new StringBuilder("/").append(version).append("/face/id/").append(id).toString();
        logger.info("CHECK_API :" + API);
        request.setAttribute("API",API);
        String reply = MethodUtil.getInstance().requestForward(request, response);
        if (ErrorPrompt.size() != 0)
            reply = ErrorPrompt.getJSONInfo();
        return reply;
    }
    //************************************************************************************
    //                DELETE - FACE/ID 根据id删除人脸
    //************************************************************************************
    @RequestMapping(method=RequestMethod.DELETE,value="/{version:[v][01]}/face/id/{id:\\d{1,24}}")
    @ResponseBody
    public String faceIdDELETE(@PathVariable("version")String version,@PathVariable("id")String id,HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        logger.info("*****enter Controller*****");
        Customer customer =(Customer) request.getAttribute("customer");
        String userName = customer.getName();
        boolean isMaster = false;
        List<String> galleries = check.getGalleries((String)request.getAttribute("inputToken"));
        List<String> list = check.checkId(id);
        if(list==null)
            return ErrorPrompt.getJSONInfo();
        Iterator<String> iterator = list.iterator();
        while (iterator.hasNext()) {
            if (galleries.contains(iterator.next())) {
                isMaster = true;
                break;
            }
        }
        if (!isMaster)
            try {
                throw new IllegalIDException("bad_id");
            } catch (IllegalIDException e) {
                response.setStatus(403);
                logger.error("*****BAD_ID*****@"+userName);
                ErrorPrompt.addInfo("error","bad_id");
                e.printStackTrace();
                return ErrorPrompt.getJSONInfo();
            }
        String API = new StringBuilder("/").append(version).append("/face/id/").append(id).toString();
        logger.info("CHECK_API :" + API);
        request.setAttribute("API",API);
        String reply = MethodUtil.getInstance().requestForward(request, response);
        if(HttpUploadFile.status==200&&request.getMethod().equals("DELETE")){
            boolean result = check.setFaceNum(customer,1,0);
            if(!result)
                return ErrorPrompt.getJSONInfo();
        }
        if (ErrorPrompt.size() != 0)
            reply = ErrorPrompt.getJSONInfo();
        return reply;
    }
    //************************************************************************************
    //            PUT - FACE/ID 根据id更新人脸
    //************************************************************************************
    @RequestMapping(method=RequestMethod.PUT,value = "/{version:[v][01]}/face/id/{id:\\d{1,24}}")
    @ResponseBody
    public String faceIdPUT(@PathVariable("version")String version,@PathVariable("id")String id,HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        logger.info("*****enter Controller*****");
        Customer customer =(Customer) request.getAttribute("customer");
        String userName = customer.getName();
        boolean isMaster = false;
        List<String> galleries = check.getGalleries((String)request.getAttribute("inputToken"));
        List<String> list = check.checkId(id);
        if(list==null)
            return ErrorPrompt.getJSONInfo();
        Iterator<String> iterator = list.iterator();
        while (iterator.hasNext()) {
            if (galleries.contains(iterator.next())) {
                isMaster = true;
                break;
            }
        }
        if (!isMaster)
            try {
                throw new IllegalIDException("bad_id");
            } catch (IllegalIDException e) {
                response.setStatus(403);
                logger.error("*****BAD_ID*****@"+userName);
                ErrorPrompt.addInfo("error","bad_id");
                e.printStackTrace();
                return ErrorPrompt.getJSONInfo();
            }
        String API = new StringBuilder("/").append(version).append("/face/id/").append(id).toString();
        logger.info("CHECK_API :" + API);
        request.setAttribute("galleries",galleries);
        request.setAttribute("chargeAPI","facePut");
        Map<String,String> header = new HashMap<String,String>();
        header.put("Method","PUT");
        header.put("API",API);
        String reply = null;
        if(request.getAttribute("PUT_PARAM")!=null){
            Map<String,Object> param = (Map<String,Object>)request.getAttribute("PUT_PARAM");
            reply = HttpUploadFile.getInstance().httpURLConnectionSDK(header,param,null,"yes");
        }
        if (ErrorPrompt.size() != 0||reply==null)
            reply = ErrorPrompt.getJSONInfo();
        String string = ConfigManager.getInstance().getParameter("PICTURE") + "/" + Base64Encrypt.encryptUserName((String) request.getAttribute("userName"));
        return reply.replaceAll("_anytec_"+userName,"").replaceAll("http://127.0.0.1:3333/uploads", string);
    }
    //************************************************************************************
    //           POST - FACE/ID  根据id更新人脸
    //************************************************************************************
    @RequestMapping(method=RequestMethod.POST,value = "/{version:[v][01]}/face/id/{id:\\d{1,24}}")
    @ResponseBody
    public String faceIdPOST(@PathVariable("version")String version,@PathVariable("id")String id,HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        logger.info("*****enter Controller*****");
        request.setAttribute("Method","PUT");
        Customer customer =(Customer) request.getAttribute("customer");
        String userName = customer.getName();
        boolean isMaster = false;
        List<String> galleries = check.getGalleries((String)request.getAttribute("inputToken"));
        List<String> list = check.checkId(id);
        if(list==null)
            return ErrorPrompt.getJSONInfo();
        Iterator<String> iterator = list.iterator();
        while (iterator.hasNext()) {
            if (galleries.contains(iterator.next())) {
                isMaster = true;
                break;
            }
        }
        if (!isMaster)
            try {
                throw new IllegalIDException("bad_id");
            } catch (IllegalIDException e) {
                response.setStatus(403);
                logger.error("*****BAD_ID*****@"+userName);
                ErrorPrompt.addInfo("error","bad_id");
                e.printStackTrace();
                return ErrorPrompt.getJSONInfo();
            }
        String API = new StringBuilder("/").append(version).append("/face/id/").append(id).toString();
        logger.info("CHECK_API :" + API);
        request.setAttribute("API",API);
        request.setAttribute("galleries",galleries);
        request.setAttribute("chargeAPI","facePut");
        String reply = MethodUtil.getInstance().requestForward(request, response);
        if (ErrorPrompt.size() != 0)
            reply = ErrorPrompt.getJSONInfo();
        return reply;
    }
    //************************************************************************************
    //             FACES/GALLERY  获取指定库所有人脸
    //************************************************************************************
    @RequestMapping(method = RequestMethod.GET,value = "/{version:[v][01]}/faces/gallery/{gallery:\\w{1,48}}")
    @ResponseBody
    public String facesGallery(@PathVariable("version")String version,@PathVariable("gallery")String inputGallery, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        logger.info("*****enter Controller*****");
        String userName = (String)request.getAttribute("userName");
        List<String> galleries = check.getGalleries((String)request.getAttribute("inputToken"));
        if(!inputGallery.equals(userName))
            inputGallery = new StringBuilder(inputGallery).append("_anytec_" + userName).toString();
        boolean existGallery = galleries.contains(inputGallery);
        if (!existGallery)
            try {
                throw new IllegalGalleryException("bad_gallery");
            }catch (IllegalGalleryException e) {
                response.setStatus(403);
                logger.error("*****BAD_GALLERY*****@"+userName);
                ErrorPrompt.addInfo("error","bad_gallery");
                e.printStackTrace();
                return ErrorPrompt.getJSONInfo();
            }
        StringBuilder API = new StringBuilder("/").append(version).append("/faces/gallery/").append(inputGallery);
        String nextPage = request.getParameter("max_id");
        String prePage = request.getParameter("min_id");
        if (nextPage != null && !nextPage.equals(""))
            API.append("?max_id=").append(nextPage);
        if (prePage != null && !prePage.equals(""))
            API.append("?min_id=").append(prePage);
        logger.info("CHECK_API :" + API.toString());
        request.setAttribute("API",API.toString());
        String reply = MethodUtil.getInstance().requestForward(request, response);
        if (ErrorPrompt.size() != 0)
            reply = ErrorPrompt.getJSONInfo();
        return reply;
    }
    //************************************************************************************
    //             FACES  获取所有人脸
    //************************************************************************************
    @RequestMapping(method = RequestMethod.GET,value = "/{version:[v][01]}/faces")
    @ResponseBody
    public String faces(@PathVariable("version")String version, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        logger.info("*****enter Controller*****");
        String userName = (String)request.getAttribute("userName");
        StringBuilder API = new StringBuilder("/").append(version).append("/faces/gallery/").append(userName);
        String nextPage = request.getParameter("max_id");
        String prePage = request.getParameter("min_id");
        if (nextPage != null && !nextPage.equals(""))
            API.append("?max_id=").append(nextPage);
        if (prePage != null && !prePage.equals(""))
            API.append("?min_id=").append(prePage);
        logger.info("CHECK_API :" + API.toString());
        request.setAttribute("API",API.toString());
        String reply = MethodUtil.getInstance().requestForward(request, response);
        if (ErrorPrompt.size() != 0)
            reply = ErrorPrompt.getJSONInfo();
        return reply;
    }

    //************************************************************************************
    //           GET - GALLERIES  获取人脸库列表
    //************************************************************************************
    @RequestMapping(value= "v0/galleries",method = RequestMethod.GET)
    @ResponseBody
    public String getGalleries(HttpServletRequest request)
            throws ServletException, IOException {
        logger.info("*****enter Controller*****");
        String userName = (String)request.getAttribute("userName");
        String API = "allGalleries";
        List<String> galleries = check.getGalleries((String)request.getAttribute("inputToken"));
        logger.info("CHECK_API :" + API);
        String reply = JSONArray.toJSONString(galleries);
        if (ErrorPrompt.size() != 0)
            reply = ErrorPrompt.getJSONInfo();
        return reply.replaceAll("_anytec_"+userName,"");
    }
    //************************************************************************************
    //           POST - GALLERIES  新增人脸库
    //************************************************************************************
    @RequestMapping(value = "/{version:[v][01]}/galleries/{gallery:\\w{1,48}}",method = RequestMethod.POST)
    @ResponseBody
    public String postGallery(@PathVariable("version")String version,@PathVariable("gallery")String createGallery, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        logger.info("*****enter Controller*****");
        String userName = (String)request.getAttribute("userName");
        List<String> galleries = check.getGalleries((String)request.getAttribute("inputToken"));
        try {
            if (galleries.size() > 10)
                throw new IllegalAPIException("too many galleries");
            createGallery = new StringBuilder(createGallery).append("_anytec_" + userName).toString();
            if (galleries.contains(createGallery))
                throw new IllegalAPIException("already exist");
        } catch (IllegalAPIException e) {
            response.setStatus(400);
            logger.error("*****BAD_API*****@"+userName);
            ErrorPrompt.addInfo("error",e.getMessage());
            e.printStackTrace();
            return ErrorPrompt.getJSONInfo();
        }
        request.setAttribute("charge", Constant.Gallery);
        String API = new StringBuilder("/").append(version).append("/galleries/").append(createGallery).toString();
        logger.info("CHECK_API :" + API);
        request.setAttribute("API",API);
        String reply = MethodUtil.getInstance().requestForward(request, response);
        if(HttpUploadFile.status==200) {
            boolean result = check.createGallery(userName, createGallery);
            logger.info("createResult: " + result);
            if (!result)
                return ErrorPrompt.getJSONInfo();
        }
        if (ErrorPrompt.size() != 0)
            reply = ErrorPrompt.getJSONInfo();
        return reply;
    }
    //************************************************************************************
    //             DELETE - GALLERIES  删除人脸库
    //************************************************************************************
    @RequestMapping(value= "/{version:[v][01]}/galleries/{gallery:\\w{1,48}}",method = RequestMethod.DELETE)
    @ResponseBody
    public String deleteGallery(@PathVariable("version")String version,@PathVariable("gallery")String deleteGallery, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        logger.info("*****enter Controller*****");
        String userName = (String)request.getAttribute("userName");
        deleteGallery = new StringBuilder(deleteGallery).append("_anytec_" + userName).toString();
        List<String> galleries = check.getGalleries((String)request.getAttribute("inputToken"));
        if (!galleries.contains(deleteGallery))
            try {
                throw new IllegalGalleryException("bad_gallery");
            }catch (IllegalGalleryException e) {
                response.setStatus(403);
                logger.error("*****BAD_GALLERY*****@"+userName);
                ErrorPrompt.addInfo("error","bad_gallery");
                e.printStackTrace();
                return ErrorPrompt.getJSONInfo();
            }
        String API = new StringBuilder("/").append(version).append("/galleries/").append(deleteGallery).toString();
        logger.info("CHECK_API :" + API);
        request.setAttribute("API",API);
        String reply = MethodUtil.getInstance().requestForward(request, response);
        if(HttpUploadFile.status==200) {
            logger.info("deleteGallerySQL");
            boolean result = check.deleteGallery(userName, deleteGallery);
            logger.info("deleteResult: " + result);
            if (!result)
                return ErrorPrompt.getJSONInfo();
        }
        if (ErrorPrompt.size() != 0)
            reply = ErrorPrompt.getJSONInfo();
        return reply;
    }
    //************************************************************************************
    //           GET - PERSON/ID  获取人物对应meta信息
    //************************************************************************************
    @RequestMapping(method = RequestMethod.GET,value="/{version:[v][01]}/person/id/{id:\\d+}")
    @ResponseBody
    public String getPersonMeta(@PathVariable("version")String version,@PathVariable("id")Long id, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        logger.info("*****enter Controller*****");
        String API = new StringBuilder("/").append(version).append("/person/id/").append(id).toString();
        //将API放到request作用域中，用于MethodUtil调用cd
        request.setAttribute("API",API);
        logger.info("CHECK_API :" + API);
        //获取FindFaceSDK响应
        String reply = MethodUtil.getInstance().requestForward(request, response);
        //程序是否正常执行，若有异常则返回错误提示
        if (ErrorPrompt.size() != 0)
            reply = ErrorPrompt.getJSONInfo();
        return reply;
    }
    //************************************************************************************
    //           PUT - PERSON/ID  更新人物meta信息
    //************************************************************************************
    @RequestMapping(method = RequestMethod.PUT,value="/{version:[v][01]}/person/id/{id:\\d+}")
    @ResponseBody
    public String putPersonMeta(@PathVariable("version")String version,@PathVariable("id")Long id, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        logger.info("*****enter Controller*****");
        String API = new StringBuilder("/").append(version).append("/person/id/").append(id).toString();
        logger.info("CHECK_API :" + API);
        Map<String,String> header = new HashMap<String,String>();
        header.put("Method","PUT");
        header.put("API",API);
        String reply = null;
        if(request.getAttribute("PUT_PARAM")!=null){
            Map<String,Object> param = (Map<String,Object>)request.getAttribute("PUT_PARAM");
            reply = HttpUploadFile.getInstance().httpURLConnectionSDK(header,param,null,"yes");
        }
        //程序是否正常执行，若有异常则返回错误提示
        if (ErrorPrompt.size() != 0)
            reply = ErrorPrompt.getJSONInfo();
        return reply;
    }
    //************************************************************************************
    //              用户获取人脸相关图片
    //************************************************************************************
    @RequestMapping("/picture/**")
    @ResponseBody
    public void pictureHandler(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        logger.info("*****enter Controller*****");
        PictureForward.getInstance().requestForward(request,response);

    }
}