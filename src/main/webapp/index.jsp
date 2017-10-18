<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>

<head>
    <meta charset="utf-8" />
    <!--[if lte IE 10]><script>window.location.href='/html/browser.html'</script><![endif]-->
    <%--<meta name="viewport" content="width=device-width,initial-scale=1,minimum-scale=1,maximum-scale=1,user-scalable=no" />--%>
    <title>恩钛</title>
    <link href="favicon.ico" rel="shortcut icon" type="image/x-icon" />

    <link rel="stylesheet" href="css/bootstrap.min.css" />
    <link rel="stylesheet" href="css/jquery.fullPage.css" />
    <!--竖屏切换css-->
    <link rel="stylesheet" href="css/index.css" />
    <!--竖屏切换js-->
    <%--<script type="text/javascript" src="js/index.js"></script>--%>
    <%--<script src="../js/show-detect.js"></script>--%>
    <%--<script src="../js/show-verify.js"></script>--%>
    <%--<script src="../js/show-search.js"></script>--%>


    <!--百度地图API-->
    <script type="text/javascript" src="http://api.map.baidu.com/api?key=&v=1.1&services=true"></script>
</head>

<body>
<div id="fullpage">

    <!--第一屏(主页)-->
    <div class="section" id="section1">
        <div class="container-fluid fluid_one">
            <nav class="navbar navbar-default" role="navigation">
                <div class="navbar-header">
                    <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#target-menu">
                        <span class="sr-only"></span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                    </button>
                    <div class="navbar-brand"><img src="img/index/one/logo.png"></div>
                </div>
                <div class="collapse navbar-collapse navbar-right" id="target-menu">
                    <ul class="nav navbar-nav">
                        <li class="active">
                            <a href="#">主页</a>
                        </li>
                        <li class="class-dropdown">
                            <a href="#" class="dropdown-toggle click_css" data-toggle="dropdown">核心技术<span class="caret"></span></a>
                            <ul class="dropdown-menu">
                                <li>
                                    <a class="click_css" href="#page2">人脸检测</a>
                                </li>
                                <li>
                                    <a class="click_css" href="#page3">人脸对比</a>
                                </li>
                                <li>
                                    <a class="click_css" href="#page4">人脸集合搜索</a>
                                </li>
                                <li>
                                    <a class="click_css" href="#page5">人脸搜索</a>
                                </li>
                            </ul>
                        </li>
                        <li>
                            <a class="click_css" href="#page6">联系我们</a>
                        </li>
                        <c:choose>
                            <c:when test="${! empty name}">
                                <li><a href="/customer/personInfo">${name}</a></li>
                                <li>
                                    <a class="click_css" href="/customer/exit">退出登录</a>
                                </li>
                            </c:when>
                            <c:otherwise>
                                <li>
                                    <a class="click_css" href="/customer/register">注册</a>
                                </li>
                                <li>
                                    <a class="click_css" href="/customer/login">登录</a>
                                </li>
                            </c:otherwise>
                        </c:choose>

                    </ul>
                </div>
            </nav>
            <div class="text-img">
                <img class="img-responsive center-block" src="img/index/one/text_img.png" />
            </div>
            <div class="prospect">
                <img class="img-responsive center-block" src="img/index/one/prospect.png" />
            </div>
        </div>
    </div>
    <!--第二屏(人脸检测)-->
    <div class="section">
        <div class="container-fluid fluid_two" id="fluid_two">
            <!--右侧倒三角背景以及文字-->
            <div class="title_bg2">
                <img class="img-responsive center-block" src="img/index/two/title_img1.png" />
                <div class="title_text2">人脸检测</div>
                <div class="introduce_text2">检测并定位图片中的人脸，返回<br/>高精度的人脸框坐标。</div>
            </div>
            <div class="formal_box2">
                <div class="left_formal2">
                    <!--左侧代码框-->
                    <div class="code_box2">
                        <div class="code_title2">
                            <span><img class="code_img2" src="img/index/two/icon-right.png"></span>&nbsp;&nbsp;Response JSON
                        </div>
                        <div class="code_text2">
									<textarea id="responseDetect" disabled="disabled">

                                    </textarea>
                        </div>
                    </div>
                    <!--左侧人脸属性框-->
                    <div class="attribute_box2">
                        <div class="attribute_title2">
                            <span><img class="attribute_img2" src="img/index/two/icon-bottom.png"></span>&nbsp;&nbsp;人脸属性
                        </div>
                        <div id="faceProperties" class="attribute_text2"></div>
                    </div>
                </div>
                <div class="middle_formal2">
                    <div class="select_box2">
                        <div class="select_text2">请勾选您需要展示的属性:</div>
                        <form>
                            <label>
                                <div class="checkbox_box2"><img class="checkbox_img2" src="img/index/two/yes.png"/></div>
                                <input name="attribute" checked="checked" value="age" class="attribute2" type="checkbox" />
                                年龄
                            </label>
                            <label>
                                <div class="checkbox_box2"><img class="checkbox_img2" src="img/index/two/yes.png"/></div>
                                <input name="attribute" checked="checked" value="gender" class="attribute2" type="checkbox" />
                                性别
                            </label>
                            <label>
                                <div class="checkbox_box2"><img class="checkbox_img2" src="img/index/two/yes.png"/></div>
                                <input name="attribute" checked="checked" value="emotions" class="attribute2" type="checkbox" />
                                情绪
                            </label>
                        </form>
                    </div>
                    <div class="maximg_box2">
                        <div class="img_bgbox2" reveal_sign="width">
                            <div  class="detect_img2">
                                <div id="imgShowDetectDiv" style="position: relative;height: 100%;width: 100%;">
                                <img id="imgShowDetect" src="http://yun.anytec.cn:8080/img/index/two/max_img1.png">
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="right_formal2">
                    <div class="bottom_left_f2">
                        <div class="upload2-1">
                            <%--<div><button id="testBtn" onclick="testBtnEvent()">testBtn</button></div>--%>
                            <div class="upload_btn2">
                                本地上传
                                <input accept="image/png,image/jpeg" onchange="uploadPicDetect(this)" class="upload_file2" type="file" value="本地上传">
                            </div>
                        </div>
                        <div class="upload2-2">
                            <div class="upload_text2">
                                <input id="inputUrlDetect"  class="upload_mintext2"  type="text" placeholder="请输入图片URL"/>
                                <div class="detect_btn2"  onclick="detectUrl()" id="checkInputUrlDetect" >检测</div>
                            </div>
                        </div>
                        <div class="upload2-3">
                            <div class="upload2-3img">
                                <div id="" class="detect_fiveimg2" reveal_sign="width"><img onclick="detectReq(this.src)" class="img_active" src="http://yun.anytec.cn:8080/img/index/two/max_img1.png"></div>
                                <div class="detect_fiveimg2" reveal_sign="width"><img onclick="detectReq(this.src)"src="http://yun.anytec.cn:8080/img/index/two/max_img2.png"></div>
                                <div class="detect_fiveimg2" reveal_sign="width"><img onclick="detectReq(this.src)" src="http://yun.anytec.cn:8080/img/index/two/max_img3.png"></div>
                                <div class="detect_fiveimg2" reveal_sign="width"><img onclick="detectReq(this.src)" src="http://yun.anytec.cn:8080/img/index/two/max_img4.png"></div>
                                <div class="detect_fiveimg2" reveal_sign="width"><img onclick="detectReq(this.src)" src="http://yun.anytec.cn:8080/img/index/two/max_img5.png"></div>
                                <br style="clear:both;"/><!--清除浮动-->
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <!--第三屏(人脸对比)-->
    <div class="section">
        <div class="container-fluid fluid_three">
            <!--左侧倒三角背景以及文字-->
            <div class="title_bg3">
                <img class="img-responsive center-block" src="img/index/three/title_img2.png">
                <div class="title_text3">人脸对比</div>
                <div class="introduce_text3">分析两张人脸属<br/>于同一个人的<br/>可能性大小
                </div>
            </div>
            <div class="formal_box3">
                <div class="left_formal3">
                    <!--左侧代码框-->
                    <div class="code_box3">
                        <div class="code_title3">
                            <span><img class="code_img3" src="img/index/two/icon-right.png"></span>&nbsp;&nbsp;Response JSON
                        </div>
                        <div class="code_text3">
										<textarea id="reponseVerify" disabled="disabled">
                                        </textarea>
                        </div>
                    </div>
                    <!--左侧人脸属性框-->
                    <div class="attribute_box3">
                        <div class="attribute_title3">
                            <span><img class="attribute_img3" src="img/index/two/icon-bottom.png"></span>&nbsp;&nbsp;比对结果
                        </div>
                        <div id="resultVerify"  class="attribute_text3">

                        </div>
                    </div>
                </div>
                <!--右侧人脸对比演示-->
                <div class="middle_formal3">
                    <!--左侧图片上传区-->
                    <div class="results_gightimg3">
                        <div class="results_maxbox3" reveal_sign="width">
                            <div class="results_maximg3" reveal_sign="width">
                                <div id="picDiv1" style="position: relative;height: 100%;width: 100%">
                                    <img id="imgShow1" class="showcasing_img3" src="http://yun.anytec.cn:8080/img/index/two/max_img1.png">
                                </div>
                            </div>
                        </div>
                        <div class="upload_box3">

                            <div class="contrast_btn3">
                                本地上传
                                <input onchange="uploadPicVerify(this,1)" class="contrast_file3" type="file" value="本地上传">
                            </div>
                            <div class="upload_text3">
                                <input class="upload_mintext3" id="inputUrl1"  type="text" placeholder="请输入图片URL"/>
                                <div class="detect_btn3" onclick="verifyUrl(1)">检测</div>
                            </div>
                            <div class="detect_minimg3">
                                <div class="detect_fiveimg3" reveal_sign="width"><img onclick="verifyReq(this.src,1)" src="http://yun.anytec.cn:8080/img/index/two/max_img1.png"></div>
                                <div class="detect_fiveimg3" reveal_sign="width"><img onclick="verifyReq(this.src,1)" src="http://yun.anytec.cn:8080/img/index/two/max_img2.png"></div>
                                <div class="detect_fiveimg3" reveal_sign="width"><img onclick="verifyReq(this.src,1)" src="http://yun.anytec.cn:8080/img/index/two/max_img3.png"></div>
                                <div class="detect_fiveimg3" reveal_sign="width"><img onclick="verifyReq(this.src,1)" src="http://yun.anytec.cn:8080/img/index/two/max_img4.png"></div>
                                <div class="detect_fiveimg3" reveal_sign="width"><img onclick="verifyReq(this.src,1)" class="img_active"  src="http://yun.anytec.cn:8080/img/index/two/max_img1_5.png"></div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="right_formal3">
                    <!--右侧图片上传区-->
                    <div class="results_gightimg3">
                        <div class="results_maxbox3" reveal_sign="width">
                            <div class="results_maximg3" reveal_sign="width">
                                <div id="picDiv2" style="position: relative;height: 100%;width: 100%;">
                                    <img id="imgShow2" class="showcasing_img3" src="http://yun.anytec.cn:8080/img/index/three/max_img1_1.png">
                                </div>
                                <%--<img class="showcasing_img3" src="img/index/two/max_img5.png" />--%>
                            </div>
                        </div>
                        <div class="upload_box3">
                            <div class="contrast_btn3">
                                本地上传
                                <input onchange="uploadPicVerify(this,2)" class="contrast_file3" type="file" value="本地上传">
                            </div>
                            <div class="upload_text3">
                                <input class="upload_mintext3" id="inputUrl2" type="text" placeholder="请输入图片URL"/>
                                <div class="detect_btn3"  onclick="verifyUrl(2)">检测</div>
                            </div>
                            <div class="detect_minimg3">
                                <div class="detect_fiveimg3" reveal_sign="width"><img onclick="verifyReq(this.src,2)" src="http://yun.anytec.cn:8080/img/index/three/max_img1_1.png"></div>
                                <div class="detect_fiveimg3" reveal_sign="width"><img onclick="verifyReq(this.src,2)" src="http://yun.anytec.cn:8080/img/index/three/max_img2_1.png"></div>
                                <div class="detect_fiveimg3" reveal_sign="width"><img onclick="verifyReq(this.src,2)" src="http://yun.anytec.cn:8080/img/index/three/max_img3_1.png"></div>
                                <div class="detect_fiveimg3" reveal_sign="width"><img onclick="verifyReq(this.src,2)" src="http://yun.anytec.cn:8080/img/index/three/max_img4_1.png"></div>
                                <div class="detect_fiveimg3" reveal_sign="width"><img onclick="verifyReq(this.src,2)" class="img_active" src="http://yun.anytec.cn:8080/img/index/two/max_img1_5.png"></div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <!--第四屏(人脸集合对比)-->
    <div class="section">
        <div class="container-fluid fluid_three_four">
            <!--右侧倒三角背景以及文字-->
            <div class="title_bg3_4">
                <img class="img-responsive center-block" src="img/index/three/title_img3_4.png">
                <div class="title_text3_4">人脸集合</div>
                <div class="introduce_text3_4">在多张人脸中找到和<br/>当前人脸相似度<br/>最高的人脸
                </div>
            </div>
            <div class="formal_box3_4">

                <!--右侧人脸对比演示-->
                <div class="middle_formal3_4">
                    <!--左侧图片上传区-->
                    <div class="results_gightimg3_4">
                        <div class="results_maxbox3_4" reveal_sign="width">
                            <div class="results_maximg3_4" reveal_sign="width">
                                <div id="picDivCol1" style="position: relative;height: 100%;width: 100%">
                                    <img id="imgShowCol1" class="showcasing_img3_4" src="http://yun.anytec.cn:8080/img/index/two/max_img1_5.png">
                                </div>
                            </div>
                        </div>
                        <div class="upload_box3_4">

                            <div class="contrast_btn3_4">
                                本地上传
                                <input onchange="uploadPicVerifyCol(this,1)" class="contrast_file3_4" type="file" value="本地上传">
                            </div>
                            <div class="upload_text3_4">
                                <input class="upload_mintext3_4" id="inputUrlCol1" type="text" placeholder="请输入图片URL" />
                                <div class="detect_btn3_4" onclick="verifyUrlCol(1)">检测</div>
                            </div>
                            <div class="faBox">
                                <div class="fa_text">设置阀值：</div>
                                <div class="fa_input"><input min="0" max="100" value="75" type="range" id="threCol"></div>
                                <div class="fa_value"><input id="confCol" type="text" value="0.75"/></div>
                            </div>
                            <div class="detect_minimg3_4">
                                <div class="detect_fiveimg3_4" reveal_sign="width"><img onclick="verifyReqCol(this.src,1)" src="http://yun.anytec.cn:8080/img/index/two/max_img1_1.png"></div>
                                <div class="detect_fiveimg3_4" reveal_sign="width"><img onclick="verifyReqCol(this.src,1)" src="http://yun.anytec.cn:8080/img/index/two/max_img1_2.png"></div>
                                <div class="detect_fiveimg3_4" reveal_sign="width"><img onclick="verifyReqCol(this.src,1)" src="http://yun.anytec.cn:8080/img/index/two/max_img1_3.png"></div>
                                <div class="detect_fiveimg3_4" reveal_sign="width"><img onclick="verifyReqCol(this.src,1)" src="http://yun.anytec.cn:8080/img/index/two/max_img1_4.png"></div>
                                <div class="detect_fiveimg3_4" reveal_sign="width"><img onclick="verifyReqCol(this.src,1)" class="img_active" src="http://yun.anytec.cn:8080/img/index/two/max_img1_5.png"></div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="right_formal3_4">
                    <!--右侧图片上传区-->
                    <div class="results_gightimg3_4">
                        <div class="results_maxbox3_4" reveal_sign="width">
                            <div class="results_maximg3_4" reveal_sign="width">
                                <div id="picDivCol2" style="position: relative;height: 100%;width: 100%;">
                                    <img id="imgShowCol2" class="showcasing_img3_4" src="http://yun.anytec.cn:8080/img/index/two/group4.png">
                                </div>
                                <%--<img class="showcasing_img3" src="img/index/two/max_img5.png" />--%>
                            </div>
                        </div>
                        <div class="upload_box3_4">
                            <div class="contrast_btn3_4">
                                本地上传
                                <input onchange="uploadPicVerifyCol(this,2)" class="contrast_file3_4" type="file" value="本地上传">
                            </div>
                            <div class="upload_text3_4">
                                <input class="upload_mintext3_4" id="inputUrlCol2" type="text" placeholder="请输入图片URL" />
                                <div class="detect_btn3_4" onclick="verifyUrlCol(2)">检测</div>
                            </div>
                            <div class="detect_minimg3_4">
                                <div class="detect_fiveimg3_4" reveal_sign="width"><img onclick="verifyReqCol(this.src,2)" src="http://yun.anytec.cn:8080/img/index/two/group1.png"></div>
                                <div class="detect_fiveimg3_4" reveal_sign="width"><img onclick="verifyReqCol(this.src,2)" src="http://yun.anytec.cn:8080/img/index/two/group2.png"></div>
                                <div class="detect_fiveimg3_4" reveal_sign="width"><img onclick="verifyReqCol(this.src,2)" src="http://yun.anytec.cn:8080/img/index/two/group3.png"></div>
                                <div class="detect_fiveimg3_4" reveal_sign="width"><img onclick="verifyReqCol(this.src,2)" class="img_active" src="http://yun.anytec.cn:8080/img/index/two/group4.png"></div>
                                <div class="detect_fiveimg3_4" reveal_sign="width"><img onclick="verifyReqCol(this.src,2)" src="http://yun.anytec.cn:8080/img/index/two/group5.png"></div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="left_formal3_4">
                    <!--左侧代码框-->
                    <div class="code_box3_4">
                        <div class="code_title3_4">
                            <span><img class="code_img3_4" src="img/index/two/icon-right.png"></span>&nbsp;&nbsp;Response JSON
                        </div>
                        <div class="code_text3_4">
									<textarea id="reponseVerifyCol" disabled="disabled">
                            </textarea>
                        </div>
                    </div>
                    <!--左侧人脸属性框-->
                    <div class="attribute_box3_4">
                        <div class="attribute_title3_4">
                            <span><img class="attribute_img3_4" src="img/index/two/icon-bottom.png"></span>&nbsp;&nbsp;比对结果
                        </div>
                        <div id="resultVerifyCol" class="attribute_text3_4">

                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>


    <!--第五屏(人脸搜索)-->
    <div class="section">
        <div class="container-fluid fluid_four">
            <!--顶部倒三角背景以及文字-->
            <div class="title_bg4">
                <img class="img-responsive center-block" src="img/index/four/title_img3.png" />
                <div class="title_text4">人脸搜索</div>
                <div class="introduce_text4">针对一个新的人脸，在一个已有的人脸<br/>集合中搜索相似的人脸。</div>
            </div>
            <div class="formal_box4">
                <div class="left_formal4">
                    <!--左侧搜索框-->
                    <div class="results_gightimg4">
                        <div class="results_maxbox4" reveal_sign="width">
                            <div id="imgShowSearchDemoDiv" class="results_maximg4">
                                <img id="imgShowSearchDemo" class="showcasing_img4" src="img/index/two/max_img2.png" />
                            </div>
                        </div>
                        <div class="contrast_btn4">
                            本地上传
                            <input class="contrast_file4" onchange="uploadImgSearcheDemo(this)" type="file" value="本地上传">
                        </div>
                        <div class="upload_text4">
                            <input class="upload_mintext4" id="inputUrlSearchDemo" type="text" placeholder="请输入图片URL"/>
                            <div onclick="searchUrlDemo(this)" class="detect_btn4">检测</div>
                        </div>
                        <div class="faBox">
                        				<div class="fa_text">设置阀值：</div>
                        				<div class="fa_input"><input min="0" max="100" type="range" id="threshold"></div>
                        				<div class="fa_value"><input id="confidence" type="text" value="0.5"/></div>
                        			</div>
                        <div class="detect_minimg4">
                            <div class="detect_fiveimg4"><img onclick="searchReqDemo(this)" src="http://yun.anytec.cn:8080/img/index/two/max_img1.png"></div>
                            <div class="detect_fiveimg4"><img onclick="searchReqDemo(this)" class="img_active" src="http://yun.anytec.cn:8080/img/index/two/max_img2.png"></div>
                            <div class="detect_fiveimg4"><img onclick="searchReqDemo(this)" src="http://yun.anytec.cn:8080/img/index/two/max_img3.png"></div>
                            <div class="detect_fiveimg4"><img onclick="searchReqDemo(this)" src="http://yun.anytec.cn:8080/img/index/two/max_img4.png"></div>
                            <div class="detect_fiveimg4"><img onclick="searchReqDemo(this)" src="http://yun.anytec.cn:8080/img/index/two/max_img5.png"></div>
                        </div>
                    </div>
                </div>
                <div class="middle_formal4">
                    <!--中间搜索结果-->
                    <!--左侧结果图片-->
                    <div class="code_box4">
                        <div class="results_title4">
                            &nbsp;&nbsp;搜索结果
                        </div>
                        <div id="resultShowSearchDemo" class="code_text4">
                            <%--<div class="results_graphic4">--%>
                                <%--<div class="graphic_img4" reveal_sign="width"><img src="img/index/four/results_img1.png"></div>--%>
                                <%--<div class="graphic_text">结果1</div>--%>
                            <%--</div>--%>
                            <%--<div class="results_graphic4">--%>
                                <%--<div class="graphic_img4" reveal_sign="width"><img src="img/index/four/results_img2.png"></div>--%>
                                <%--<div class="graphic_text">结果2</div>--%>
                            <%--</div>--%>
                            <%--<div class="results_graphic4">--%>
                                <%--<div class="graphic_img4" reveal_sign="width"><img src="img/index/four/results_img3.png"></div>--%>
                                <%--<div class="graphic_text">结果3</div>--%>
                            <%--</div>--%>
                            <%--<div class="results_graphic4">--%>
                                <%--<div class="graphic_img4" reveal_sign="width"><img src="img/index/four/results_img4.png"></div>--%>
                                <%--<div class="graphic_text">结果4</div>--%>
                            <%--</div>--%>
                            <%--<div class="results_graphic4">--%>
                                <%--<div class="graphic_img4" reveal_sign="width"><img src="img/index/four/results_img5.png"></div>--%>
                                <%--<div class="graphic_text">结果5</div>--%>
                            <%--</div>--%>
                        </div>
                    </div>
                    <!--左侧结果文字-->
                    <div class="results_box4">
                        <div class="results_title4">
                            &nbsp;&nbsp;搜索结果
                        </div>
                        <div id="searchResultDemo" class="results_text4">
                            <%--<p>结果1：是同一个人可能性很高</p>--%>
                            <%--<p>结果2：是同一个人可能性高</p>--%>
                            <%--<p>结果3：是同一个人可能性低</p>--%>
                            <%--<p>结果4：是同一个人可能性低</p>--%>
                            <%--<p>结果5：是同一个人可能性低</p>--%>
                        </div>
                    </div>
                </div>
                <div class="right_formal4">
                    <!--右边代码展示-->
                    <div class="results_gightimg4_2">
                        <!--上边代码框-->
                        <div class="code_box4_2">
                            <div class="code_title4">
                                <span><img class="code_img4" src="img/index/two/icon-bottom.png"></span>&nbsp;&nbsp;Response JSON
                            </div>
                            <div class="code_text4-2">
									<textarea id="reponseSearchDemo" disabled="disabled">

        							</textarea>
                            </div>
                        </div>
                        <!--下边图片集合框-->
                        <div class="attribute_box4">
                            <div class="attribute_title4">
                                <span><img class="attribute_img4" src="img/index/two/icon-right.png"></span>&nbsp;&nbsp;管理您的人脸图片集合
                            </div>
                            <div class="attribute_text4">

                                <c:choose>
                                    <c:when test="${! empty name}">
                                        <a href="/customer/gallery-demo">点击跳转至个人中心</a>
                                    </c:when>
                                    <c:otherwise>
                                        请先<a href="/customer/login">登录</a>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </div>
                </div>
            </div>



        </div>
    </div>
    <!--第六屏(联系我们)-->
    <div class="section">
        <div class="container-fluid fluid_six">
            <!--顶部倒三角背景以及文字-->
            <div class="title_bg6">
                <img class="img-responsive center-block" src="img/index/six/title_img6.png" />
                <div class="title_text6">联系我们</div>
            </div>
            <!--正文部分-->
            <div class="formal_box6">
                <div class="left_box6">
                    <div class="contact6_1">
                        <div class="contact_icon6"><img src="img/index/six/icon1.png"/></div>
                        <div class="contact_text6">
                            <div class="contact_text6_1">联系电话</div>
                            <div class="contact_text6_2">0755 - 8653 7776</div>
                        </div>
                    </div>
                    <div class="contact6_1">
                        <div class="contact_icon6"><img src="img/index/six/icon2.png"/></div>
                        <div class="contact_text6">
                            <div class="contact_text6_1">客服邮箱</div>
                            <div class="contact_text6_2">info@anytec.cn</div>
                        </div>
                    </div>
                    <div class="contact6_1">
                        <div class="contact_icon6"><img src="img/index/six/icon3.png"/></div>
                        <div class="contact_text6">
                            <div class="contact_text6_1">商务邮箱</div>
                            <div class="contact_text6_2">sales@anytec.cn</div>
                        </div>
                    </div>
                    <div class="contact6_1">
                        <div class="contact_icon6"><img src="img/index/six/icon4.png"/></div>
                        <div class="contact_text6">
                            <div class="contact_text6_1">地址</div>
                            <div class="contact_text6_2">518063 深圳市南山区粤海街道高新南七道018号高新工业村R3-A座四层</div>
                        </div>
                    </div>
                </div>
                <div class="right_box6">
                    <div class="mapapi" id="dituContent"></div>
                </div>
            </div>
            <!--底部版权-->
            <div class="copyright6"><div class="copyright6_1">Copyright 2017 All Right Reserved 深圳市恩钛控股有限公司  ICP:08118166 网站地图</div>
            <div class="copyright6_2">地址：深圳市南山区粤海街道高新南七道高新工业村018号R3-A座四层</div>
            </div>
        </div>
    </div>

</div>
</body>
<script type="text/javascript" src="js/lib/jquery.min.js"></script>
<script type="text/javascript" src="js/index.js"></script>
<%--<script type="text/javascript" src="js/index-function.min.js"></script>--%>
<%--<script type="text/javascript" src="js/index-function.min.js"></script>--%>
<script type="text/javascript" src="js/index-function.js"></script>
<script type="text/javascript" src="js/lib/bootstrap.min.js"></script>
<script type="text/javascript" src="js/lib/jquery.fullPage.js"></script>
</div>
</body>

</html>
