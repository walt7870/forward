//上传图片探测
function uploadPicDetect(obj) {
    // var img = document.getElementById("imgShowDetectDiv");
    for (var i = 0; i < num; i++) {
        if ($('#detect' + i).length > 0) {
            $('#detect' + i).remove();
        }
    }
    var attributeCheckedUpload = [];
    $('input[name="attribute"]:checked').each(function(){
        console.log($(this).val());
        attributeCheckedUpload.push($(this).val());
    });
    // attributeCheckedUpload.push("age")
    // attributeCheckedUpload.push("gender")
    // attributeCheckedUpload.push("emotions")
    var file = obj.files[0];
    // attributeChecked["photo"]=file
    var reader = new FileReader();
    reader.onload = function (e) {

        var imgUrl=e.target.result
        // setPicSize(imgUrl)
        $("#imgShowDetect").attr("src", imgUrl)
        getImageWidth(imgUrl,function (widthImg,heightImg) {

            var imgShowDiv = document.getElementById("imgShowDetectDiv");
            var imgShow = document.getElementById("imgShowDetect");
            imgShow.src=imgUrl
            // if(widthImg*heightImg>1000000){
            //     var text=compress(imgShow,widthImg*0.1,heightImg*0.1,1)
            //
            //     imgShow.src=text
            //        getImageWidth(imgShow.src,function (widthImg,heightImg) {
            //            resizePic(imgShow,imgShowDiv,widthImg,heightImg)
            //        })
            //     file=dataURItoBlob(text);
            // }else{
            resizePic(imgShow,imgShowDiv,widthImg,heightImg)
            // }
            var detectForm = new FormData();
            detectForm.append("photo", file);
            //清除input框的状态
            obj.value = "";
            for (var inx in attributeCheckedUpload) {
                detectForm.append(attributeCheckedUpload[inx], "true");
            }
            $.ajax({
                url: 'customer/detect-face',
                type: 'POST',
                // dataType: "json",
                data: detectForm,
                processData: false,
                contentType: false,
                async: true,
                success: function (data) {
                    var dataObj=eval('(' + data + ')')
                    if (data == ""||dataObj['faces'].length==0) {
                        $('#responseDetect').html("未检查到人脸")
                        $('#faceProperties').html("未检查到人脸")
                        return false
                    }
                    checkProperties(eval('(' + data + ')'));
                    handleData($('#imgShowDetect').attr("src"), eval('(' + data + ')'))
                },
                error: function (data) {
                    $('#responseDetect').html("文件格式不符或文件太大,支持png,jpeg,webp格式且像素小于1920*1080的图片")

                    $('#faceProperties').html("文件格式不符或文件太大,支持png,jpeg,webp格式且像素小于1920*1080的图片")
                    // }
                    return false
                }
            });


        })

    }
    reader.readAsDataURL(file)


}

//
// function invokeCompress(imgObj) {
//     var fileObj=null;
//     // var fileObj=getImageWidth(imgObj.src,function (w,h) {
//     //     console.log({"w":w,"h":h})
//         text=compress(imgObj,w*0.5,h*0.5,0.7)
//         // document.getElementById("imgShow1").src=text
//         fileObj=dataURItoBlob(text);
//         return fileObj;
//     // })
//     return fileObj;
// }

//将base64图片转成input能处理的二进制流
function dataURItoBlob(dataURI) {
    var byteString = atob(dataURI.split(',')[1]);
    var mimeString = dataURI.split(',')[0].split(':')[1].split(';')[0];
    var ab = new ArrayBuffer(byteString.length);
    var ia = new Uint8Array(ab);
    for (var i = 0; i < byteString.length; i++) {
        ia[i] = byteString.charCodeAt(i);
    }
    console.log([ab])
    return new Blob([ab], {type: mimeString});
}

//图片width*heigh>1000000则对图片进行压缩处理
function compress(img, width, height, ratio) {
    var canvas, ctx, img64;

    canvas = document.createElement('canvas');
    canvas.width = width;
    canvas.height = height;

    ctx = canvas.getContext("2d");
    ctx.drawImage(img, 0, 0, width, height);

    img64 = canvas.toDataURL("image/jpeg",ratio);

    return img64;
}
function resizePic(imgShow,imgShowDiv,widthImg,heightImg){
    // console.log("height:"+heightImg)
    // console.log("widht:"+widthImg)
    console.log(imgShowDiv.offsetHeight)
    if(heightImg!=widthImg){
        if(heightImg>widthImg){
            imgShow.style.width=widthImg/heightImg*100+"%"
            imgShow.style.height="100%"
        }
        if(heightImg<widthImg){
            imgShow.style.height=heightImg/widthImg*100+"%"
            imgShow.style.width="100%"
        }
    }else{
        imgShow.style.height="100%"
        imgShow.style.width="100%"
    }
}

//定义全局变量num记录图片中人脸的个数



var num = 0;

function detectUrl() {
    $('#imgShowDetect').attr("src", $("#inputUrlDetect").val());
    detectReq($("#inputUrlDetect").val())
}

//url图片探测,并画人脸div
function detectReq(imgUrl) {

    document.getElementById("imgShowDetect").style.height="100%";
    document.getElementById("imgShowDetect").style.width="100%";
    for (var i = 0; i < num; i++) {
        if ($('#detect' + i).length > 0) {
            $('#detect' + i).remove();
        }
    }
    getImageWidth(imgUrl,function (widthImg,heightImg) {
        var imgShowDiv = document.getElementById("imgShowDetectDiv");
        var imgShow = document.getElementById("imgShowDetect");
        console.log("height:"+heightImg)
        console.log("widht:"+widthImg)
        console.log(imgShowDiv.offsetHeight)
        if(heightImg!=widthImg){
            if(heightImg>widthImg){
                imgShow.style.width=widthImg/heightImg*100+"%"
                imgShow.style.height="100%"
            }
            if(heightImg<widthImg){
                imgShow.style.height=heightImg/widthImg*100+"%"
                imgShow.style.width="100%"
            }
        }else{
            imgShow.style.height="100%"
            imgShow.style.width="100%"
        }
        $("#imgShowDetect").attr("src", imgUrl)
    })


    var attributeChecked = {};
    $('input[name="attribute"]:checked').each(function(){
        // console.log($(this).val());
        attributeChecked[$(this).val()]="true";
    });
    attributeChecked["photo"] = imgUrl
    // attributeChecked["age"] = "true"
    // attributeChecked["gender"] = "true"
    // attributeChecked["emotions"] = "true"
    $.ajax({
        url: 'customer/detect-face',
        type: 'POST',
        // dataType: "json",
        data: attributeChecked,
        async: true,
        success: function (data) {
            if (data == "") {
                $('#responseDetect').html("no face detected")
                $('#faceProperties').html("")
                return false
            }
            localData = eval('(' + data + ')')
            checkProperties(localData);
            handleData(imgUrl, localData)
        },
        error: function (data) {
            $('#responseDetect').html("no face detected")
            $('#faceProperties').html("")
            // handleData(img, data)
        }
    });
}

//画人脸div
function handleData(imgContent, data) {
    $('#responseDetect').html(syntaxHighlight(data))
    var imgSrc = $('#imgShowDetect').attr("src");
    getImageWidth(imgSrc, function (w, h) {
        img = document.getElementById("imgShowDetect");
        imgDiv = document.getElementById("imgShowDetectDiv");
        console.log({width: w, height: h});
        console.log({width: imgDiv.offsetWidth, height: imgDiv.offsetHeight});
        console.log({picwidth: img.naturalWidth, picheight: img.naturalWidth});

        // hratew = img.naturalHeight / img.naturalWidth
        // hratew = img.height / img.width
        hratew = img.height / img.width
        /*
        * 画图处理根据图片的尺寸分三种情况,分别是
        * 1.长宽比例是1;
        * 2.长宽比例大于1;
        * 3.长宽比例小于1;
        * */
        // var widthRate = imgDiv.offsetWidth / img.naturalWidth
        // var heightRate = imgDiv.offsetHeight / img.naturalHeight
        var widthRate = img.width / w
        var heightRate = img.height / h
        var dataObj = eval(data)
        var rect;
        var results = [];
        if (dataObj.hasOwnProperty("faces")) {
            rect = dataObj['faces'];
            for (var i = 0; i < rect.length; i++) {
                rect[i].x1 = rect[i].x1 * widthRate
                rect[i].x2 = rect[i].x2 * widthRate
                rect[i].y1 = rect[i].y1 * heightRate
                rect[i].y2 = rect[i].y2 * heightRate
                width = (rect[i].x2 - rect[i].x1) * 0.8
                height = (rect[i].y2 - rect[i].y1) * 0.9
                left = rect[i].x1 + width * 0.15
                divtop = rect[i].y1 + 2
                var result = new Result(width, height, left, divtop);
                results[i] = result;
                // console.log(results);
            }
        }
        if (results) {
            for (var i = 0; i < num; i++) {
                if ($('#detect' + i).length > 0) {
                    $('#detect' + i).remove();
                }
            }
            num = results.length;
            for (var i = 0; i < results.length; i++) {
                $('#imgShowDetectDiv').prepend("<div style='font-size:10px!important;' id='detect" + i + "'><span style='color: #00c3bb' >"+(i+1)+"</span></div>");
                $('#detect' + i).css({
                    "position": "absolute",
                    "outline": "rgb(70, 171, 232) solid 2px",
                    "width": results[i].width,
                    "height": results[i].height,
                    "left": results[i].left,
                    "top": results[i].top
                })
            }


        }
    });
}

//检查人脸属性
function checkProperties(data) {

    faceData = data['faces']
    elementData = [];
    var status;
    for (var single in faceData) {
        var status = null;
        elementData.push("<p>第" + (parseInt(single) + 1) + "张人脸的属性</p>")
        // emotion=faceData[single]['emotions']
        // age=faceData[single]['age']
        // gender=faceData[single] ['gender']
        if (faceData[single]['age']) {
            status = true
            var age=Math.round(faceData[single]['age'])
            if(age<1){
                age=1
            }
            elementData.push("<p>年龄:" + age + "</p>");
        }
        if (faceData[single]['emotions']) {
            status = true
            elementData.push("<p>情绪:" + faceData[single]['emotions'] + "</p>");
        }
        if (faceData[single] ['gender']) {
            status = true
            elementData.push("<p>性别:" + faceData[single] ['gender'] + "</p>");
        }
        if(elementData.length>0){
            elementData.push("<hr>")
        }
    }
    if (status) {
        $('#faceProperties').html(elementData.join(""))
    } else {
        $('#faceProperties').html("")
    }
}


/********* detect end************************************/

/***********单个与集合对比开始*****************************/
//定义两个全局变量用于记录两个框的上传文件内容
var leftUploadFileCol
var rightUploadFileCol

function verifyUrlCol(id) {
    verifyReqCol($("#inputUrlCol" + id).val(), id);
}

function uploadPicVerifyCol(obj, id) {
    var threshold =$("#confCol").val();
    if(threshold>0&&threshold<=1) {
        document.getElementById("imgShowCol" + id).style.height = "100%";
        document.getElementById("imgShowCol" + id).style.width = "100%";
        removeDivCol()
        var img = document.getElementById("imgShowCol" + id)
        var file = obj.files[0];
        if (!file) {
            return false;
        }
        var reader = new FileReader();
        reader.onloadend = function (e) {
            var imgUrl = e.target.result
            getImageWidth(imgUrl, function (widthImg, heightImg) {
                var imgShowDiv = document.getElementById("picDivCol" + id);
                var imgShow = document.getElementById("imgShowCol" + id);
                // console.log("height:"+heightImg)
                // console.log("widht:"+widthImg)
                // console.log(imgShowDiv.offsetHeight)
                if (heightImg != widthImg) {
                    if (heightImg > widthImg) {
                        imgShow.style.width = widthImg / heightImg * 100 + "%"
                        imgShow.style.height = "100%"
                    }
                    if (heightImg < widthImg) {
                        imgShow.style.height = heightImg / widthImg * 100 + "%"
                        imgShow.style.width = "100%"
                    }
                } else {
                    imgShow.style.height = "100%"
                    imgShow.style.width = "100%"
                }
                $("#imgShowCol" + id).attr("src", imgUrl)
                var checkFile = checkLeftFile()
                $.ajax({
                    url: "customer/faceNumber",
                    type: "post",
                    processData: false,
                    contentType: false,
                    data: checkFile,
                    success: function (data) {
                        console.log(data);
                        if (data == 1) {
                            another = id == 1 ? 2 : 1
                            var imgAno = document.getElementById("imgShowCol" + another)
                            var formData = new FormData();
                            if (imgAno.src.indexOf("data:image") == 0 || imgAno.src.indexOf("data:;base64") == 0) {
                                formData.append("photo1", file)
                                if (id == 1) {
                                    formData.append("photo2", rightUploadFileCol)
                                } else if (id == 2) {
                                    formData.append("photo2", leftUploadFileCol)
                                } else {
                                    return false
                                }
                            } else {
                                formData.append("photo1", file)
                                formData.append("photo2", imgAno.src)
                            }
                            formData.append("mf_selector", "all");
                            formData.append("threshold", threshold);
                            $.ajax({
                                url: 'customer/verify-face',
                                type: 'POST',
                                // dataType: "json",
                                data: formData,
                                processData: false,
                                contentType: false,
                                async: true,
                                success: function (data) {
                                    var dataObj = eval('(' + data + ')')
                                    if (data == "") {
                                        $("#resultVerifyCol").html("有图片未检测到人脸");
                                        $("#reponseVerifyCol").html("有图片未检测到人脸");
                                        return false;
                                    }
                                    readResDataCol(dataObj, id)
                                },
                                error: function (data) {
                                    removeDivCol()
                                    $("#resultVerifyCol").html("文件格式不符或文件太大,支持png,jpeg,webp格式且像素小于1920*1080的图片");
                                    $("#reponseVerifyCol").html("文件格式不符或文件太大,支持png,jpeg,webp格式且像素小于1920*1080的图片");
                                    return false;
                                }
                            });
                        } else {
                            $("#resultVerifyCol").html("");
                            $("#reponseVerifyCol").html("");
                            alert("左侧框的人脸数不是1,请确保左侧人脸数只有一张")
                        }
                    },
                    error: function (data) {
                        console.log(data);
                        alert("出现错误")
                    }
                })
            })
            if (id == 1) {
                leftUploadFileCol = file
            }
            if (id == 2) {
                rightUploadFileCol = file
            }
            obj.value = ""
        }
        reader.readAsDataURL(file)
        //上传文件时当前的图片内容是文件,对比的对象可能时文件或者url

    }else {
        removeDivCol()
        $('#resultVerifyCol').html("");
        $('#reponseVerifyCol').html("请输入范围在0~1的阀值！");
    }


}

//读取返回的结果
function readResDataCol(data,id) {
    removeDivCol()
    console.log(data);
    //读取返回的json数据
    var dataObj = eval(data);
    $('#reponseVerifyCol').html(syntaxHighlight(data))

    var rect1
    var rect2
    var maxConfident=0
    var flagForVerify=false
    var responseHtml=[];
    multFaceNum=0;
    var each=0;
    for(var index in dataObj["results"]){
        if(dataObj["results"][index]["verified"]){
            maxConfident=dataObj["results"][index]["confidence"]
            rect1 = dataObj["results"][index]["bbox1"];
            //第二个正方形框
            rect2 = dataObj["results"][index]["bbox2"];
            multFaceNum++;
            responseHtml.push("<p>第"+(++each)+"张,可信度:"+maxConfident+"</p>")
            multiFace(rect1,rect2,id,flagForVerify,each)
            flagForVerify=true;
        }
    }
    if(!flagForVerify){
        $("#resultVerifyCol").html("所设阈值内未得到匹配信息,请减小阈值再尝试");
        // $("#reponseVerifyCol").html("文件格式不符或文件太大,支持png,jpeg,webp格式且像素小于1920*1080的图片");
        return false;
    }
    //
    // var confidence = dataObj["results"][index]["confidence"];
    //
    // var val = dataObj["verified"];
    // //第一个正方形框
    // rect1 = dataObj["results"][index]["bbox1"];
    // //第二个正方形框
    // rect2 = dataObj["results"][index]["bbox2"];
    // //
    // // console.log(rect1);
    // // console.log(rect2);
    //
    //
    //
    //
    $("#resultVerifyCol").html(responseHtml.join(""));


}

function multiFace(rect1,rect2,id,flagForVerify,index) {
    var imgshow1=document.getElementById("imgShowCol1");
    var imgshow2=document.getElementById("imgShowCol2");
    if(id==1){
        if(!flagForVerify){
            getImageWidth(imgshow1.src,function (w,h) {
                drawDivCol(imgshow1,w,h,rect1,"picDivCol1")
            })
        }
        getImageWidth(imgshow2.src,function (w,h) {
            drawDivCol(imgshow2,w,h,rect2,'picDivCol2',index)
        })


    }else{
        if(!flagForVerify) {
            getImageWidth(imgshow1.src,function (w,h) {
                drawDivCol(imgshow1,w,h,rect2,"picDivCol1")
            })
        }
        getImageWidth(imgshow2.src,function (w,h) {
            drawDivCol(imgshow2,w,h,rect1,'picDivCol2',index)
        })
    }
}
var multFaceNum=0;
//人脸对比画人脸div
function drawDivCol(image,w,h,result,parentId,index){
    var imgVerifyDiv = document.getElementById(parentId);
    var widthRate = image.width / w
    var heightRate = image.height / h
    result.x1 = result.x1 * widthRate
    result.x2 = result.x2 * widthRate
    result.y1 = result.y1 * heightRate
    result.y2 = result.y2 * heightRate

    width = (result.x2 - result.x1) * 0.8
    height = (result.y2 - result.y1) * 0.9
    left = result.x1 + width * 0.15
    divtop = result.y1 + 2
    // var result = new Result(width, height, left, divtop);
    picId=image.id+multFaceNum;
    if(index){
        index=parseInt(index)
        $('#'+parentId).prepend("<div style='font-size:10px!important;' id='"+picId + "'><span style='color: #00c3bb;' >"+(index)+"</span></div>");
    }else{
        $('#'+parentId).prepend("<div id='"+picId + "'></div>");
    }
    // $('#picShow2').prepend("<div id=" + id + "></div>");
    $('#' + picId).css({
        "position": "absolute",
        "outline": "rgb(70, 171, 232) solid 2px",
        "width": width,
        "height": height,
        "left": left,
        "top": divtop
    })

}

function getFaceNum(){
    console.log("customer/getFaceNum")
    var formData= new FormData();
    formData.append("photo","http://yun.anytec.cn:8080/img/index/two/max_img3.png")
    $.ajax({
        url:"customer/faceNumber",
        type:"post",
        processData: false,
        contentType: false,
        data:formData,
        success:function (data) {
            console.log(data);
        },
        error:function (data) {
            console.log(data);

        }
    })
}

function checkLeftFile() {
    var leftImg=document.getElementById("imgShowCol1")
    var formDataCheck = new FormData();
    if(leftImg.src.indexOf("data:image")==0||leftImg.src.indexOf("data:;base64")==0){
        formDataCheck.append("photo",leftUploadFileCol)
    }else{
        formDataCheck.append("photo",leftImg.src)
    }
    return formDataCheck;
}

function verifyReqCol(imgUrl, id) {
    var threshold =$("#confCol").val();
    if(threshold>0&&threshold<=1) {
        document.getElementById("imgShowCol" + id).style.height = "100%";
        document.getElementById("imgShowCol" + id).style.width = "100%";
        removeDivCol()
        getImageWidth(imgUrl, function (widthImg, heightImg) {
            var imgShowDiv = document.getElementById("picDivCol" + id);
            var imgShow = document.getElementById("imgShowCol" + id);
            // console.log("height:"+heightImg)
            // console.log("widht:"+widthImg)
            // console.log(imgShowDiv.offsetHeight)
            if (heightImg != widthImg) {
                if (heightImg > widthImg) {
                    imgShow.style.width = widthImg / heightImg * 100 + "%"
                    imgShow.style.height = "100%"
                }
                if (heightImg < widthImg) {
                    imgShow.style.height = heightImg / widthImg * 100 + "%"
                    imgShow.style.width = "100%"
                }
            } else {
                imgShow.style.height = "100%"
                imgShow.style.width = "100%"
            }
            $("#imgShowCol" + id).attr("src", imgUrl);
            var checkFile = checkLeftFile()
            $.ajax({
                url: "customer/faceNumber",
                type: "post",
                processData: false,
                contentType: false,
                data: checkFile,
                success: function (data) {
                    console.log(data);
                    if (data == 1) {
                        var notId = id == 1 ? 2 : 1
                        var selfImg = document.getElementById("imgShowCol" + id)
                        var anotherImg = document.getElementById("imgShowCol" + notId)
                        var formData = new FormData();
                        if (anotherImg.src.indexOf("data:image") == 0 || anotherImg.src.indexOf("data:;base64") == 0) {
                            formData.append("photo1", selfImg.src)
                            if (id == 1) {
                                formData.append("photo2", rightUploadFileCol)
                            } else if (id == 2) {
                                formData.append("photo2", leftUploadFileCol)
                            } else {
                                return false
                            }
                        } else {
                            formData.append("photo1", selfImg.src)
                            formData.append("photo2", anotherImg.src)
                        }
                        formData.append("mf_selector", "all");
                        formData.append("threshold", threshold);
                        $.ajax({
                            url: 'customer/verify-face',
                            type: 'POST',
                            // dataType: "json",
                            data: formData,
                            processData: false,
                            contentType: false,
                            async: true,
                            success: function (data) {
                                if (data == "") {
                                    $("#resultVerifyCol").html("有图片未检测到人脸");
                                    $("#reponseVerifyCol").html("有图片未检测到人脸");
                                    return false;
                                }
                                readResDataCol(eval('(' + data + ')'), id)
                            },
                            error: function (data) {
                                $("#resultVerifyCol").html("文件格式不符或文件太大");
                                $("#reponseVerifyCol").html("文件格式不符或文件太大");
                                return false;
                            }
                        });
                    } else {
                        $("#resultVerifyCol").html("");
                        $("#reponseVerifyCol").html("");
                        alert("左侧框的人脸数不是1,请确保左侧人脸数只有一张")
                    }
                },
                error: function (data) {
                    console.log(data);
                    alert("出现错误")
                }
            })
        })


    }else {
        removeDivCol()
        $('#resultVerifyCol').html("请输入范围在0~1的阀值！");
        $('#reponseVerifyCol').html("请输入范围在0~1的阀值！");
    }



}
function removeDivCol() {
    if ($('#imgShowCol11')) {
        $('#imgShowCol11').remove();
    }
    for(var i=1;i<=multFaceNum;i++){
        if ($('#imgShowCol2'+i)) {
            $('#imgShowCol2'+i).remove();
        }
    }

}



/***********单个与集合对比结束*****************************/




/*********************verify----begin**********************************/
//定义两个全局变量用于记录两个框的上传文件内容
var leftUploadFile
var rightUploadFile

//人脸对比url处理
function verifyUrl(id) {
    verifyReq($("#inputUrl" + id).val(), id);
}

//人脸对比上传文件
function uploadPicVerify(obj, id) {
    document.getElementById("imgShow" + id).style.height="100%";
    document.getElementById("imgShow" + id).style.width="100%";
    removeDiv()
    var img = document.getElementById("imgShow" + id)
    var file = obj.files[0];
    if(!file){
        return false;
    }
    var reader = new FileReader();
    reader.onloadend = function (e) {
        var imgUrl=e.target.result
        getImageWidth(imgUrl,function (widthImg,heightImg) {
            var imgShowDiv = document.getElementById("picDiv"+id);
            var imgShow = document.getElementById("imgShow" + id);
            console.log("height:"+heightImg)
            console.log("widht:"+widthImg)
            console.log(imgShowDiv.offsetHeight)
            if(heightImg!=widthImg){
                if(heightImg>widthImg){
                    imgShow.style.width=widthImg/heightImg*100+"%"
                    imgShow.style.height="100%"
                }
                if(heightImg<widthImg){
                    imgShow.style.height=heightImg/widthImg*100+"%"
                    imgShow.style.width="100%"
                }
            }else{
                imgShow.style.height="100%"
                imgShow.style.width="100%"
            }
            $("#imgShow" + id).attr("src", imgUrl)
        })
        img.src = e.target.result;
        if(id==1){
            leftUploadFile=file
        }
        if(id==2){
            rightUploadFile=file
        }
        obj.value=""
        //或者 img.src = this.result;  //e.target == this
    }
    reader.readAsDataURL(file)
    //上传文件时当前的图片内容是文件,对比的对象可能时文件或者url
    another=id==1?2:1
    var imgAno = document.getElementById("imgShow" + another)
    var formData = new FormData();
    if(imgAno.src.indexOf("data:image")==0||imgAno.src.indexOf("data:;base64")==0){
        formData.append("photo1", file)
        if(id==1) {
            formData.append("photo2", rightUploadFile)
        }else if(id==2){
            formData.append("photo2", leftUploadFile)
        }else {
            return false
        }
    }else {
        formData.append("photo1", file)
        formData.append("photo2", imgAno.src)
    }

    $.ajax({
        url: 'customer/verify-face',
        type: 'POST',
        // dataType: "json",
        data: formData,
        processData: false,
        contentType: false,
        async: true,
        success: function (data) {
            var dataObj=eval('(' + data + ')')
            if(data==""){
                $("#resultVerify").html("有图片未检测到人脸");
                $("#reponseVerify").html("有图片未检测到人脸");
                return false;
            }
            readResData(dataObj,id)
        },
        error:function (data) {
            removeDiv()
            // if(data==""){
            $("#resultVerify").html("文件格式不符或文件太大,支持png,jpeg,webp格式且像素小于1920*1080的图片");
            $("#reponseVerify").html("文件格式不符或文件太大,支持png,jpeg,webp格式且像素小于1920*1080的图片");
            // }
            return false;
        }
    });

}

//读取返回的结果
function readResData(data,id) {
    removeDiv()
    console.log(data);
    //读取返回的json数据
    var dataObj = eval(data);
    $('#reponseVerify').html(syntaxHighlight(data))
    var confidence = dataObj['results']['0']['confidence'];
    var val = dataObj['verified'];
    //第一个正方形框
    var rect1 = dataObj['results']['0']['bbox1'];
    //第二个正方形框
    var rect2 = dataObj['results']['0']['bbox2'];


    console.log(rect1);
    console.log(rect2);
    var imgshow1=document.getElementById("imgShow1");
    var imgshow2=document.getElementById("imgShow2");
    if(id==1){
        getImageWidth(imgshow1.src,function (w,h) {
            drawDiv(imgshow1,w,h,rect1,"picDiv1")
        })
        getImageWidth(imgshow2.src,function (w,h) {
            drawDiv(imgshow2,w,h,rect2,'picDiv2')
        })


    }else{
        getImageWidth(imgshow1.src,function (w,h) {
            drawDiv(imgshow1,w,h,rect2,"picDiv1")
        })
        getImageWidth(imgshow2.src,function (w,h) {
            drawDiv(imgshow2,w,h,rect1,'picDiv2')
        })
    }

    $("#resultVerify").html("是同一张人脸的可信度是:<br>"+confidence);


}


//人脸对比画人脸div
function drawDiv(image,w,h,result,parentId){
    var imgVerifyDiv = document.getElementById(parentId);
    var widthRate = image.width / w
    var heightRate = image.height / h
    result.x1 = result.x1 * widthRate
    result.x2 = result.x2 * widthRate
    result.y1 = result.y1 * heightRate
    result.y2 = result.y2 * heightRate

    width = (result.x2 - result.x1) * 0.8
    height = (result.y2 - result.y1) * 0.9
    left = result.x1 + width * 0.15
    divtop = result.y1 + 2
    // var result = new Result(width, height, left, divtop);
    picId=image.id+"1"
    $('#'+parentId).prepend("<div id='"+picId + "'></div>");
    // $('#picShow2').prepend("<div id=" + id + "></div>");
    $('#' + picId).css({
        "position": "absolute",
        "outline": "rgb(70, 171, 232) solid 2px",
        "width": width,
        "height": height,
        "left": left,
        "top": divtop
    })

}



function verifyReq(imgUrl, id) {
    document.getElementById("imgShow" + id).style.height="100%";
    document.getElementById("imgShow" + id).style.width="100%";
    removeDiv()
    getImageWidth(imgUrl,function (widthImg,heightImg) {
        var imgShowDiv = document.getElementById("picDiv"+id);
        var imgShow = document.getElementById("imgShow" + id);
        console.log("height:"+heightImg)
        console.log("widht:"+widthImg)
        console.log(imgShowDiv.offsetHeight)
        if(heightImg!=widthImg){
            if(heightImg>widthImg){
                imgShow.style.width=widthImg/heightImg*100+"%"
                imgShow.style.height="100%"
            }
            if(heightImg<widthImg){
                imgShow.style.height=heightImg/widthImg*100+"%"
                imgShow.style.width="100%"
            }
        }else{
            imgShow.style.height="100%"
            imgShow.style.width="100%"
        }
        $("#imgShow" + id).attr("src",imgUrl);
        var notId=id==1?2:1
        var selfImg = document.getElementById("imgShow" + id)
        var anotherImg = document.getElementById("imgShow" + notId)
        var formData = new FormData();
        if(anotherImg.src.indexOf("data:image")==0||anotherImg.src.indexOf("data:;base64")==0){
            formData.append("photo1", selfImg.src)
            if(id==1) {
                formData.append("photo2", rightUploadFile)
            }else if(id==2){
                formData.append("photo2", leftUploadFile)
            }else {
                return false
            }
        }else {
            formData.append("photo1", selfImg.src)
            formData.append("photo2", anotherImg.src)
        }
        $.ajax({
            url: 'customer/verify-face',
            type: 'POST',
            // dataType: "json",
            data:formData,
            processData: false,
            contentType: false,
            async: true,
            success: function (data) {
                if(data==""){
                    $("#resultVerify").html("有图片未检测到人脸");
                    $("#reponseVerify").html("有图片未检测到人脸");
                    return false;
                }
                readResData(eval('(' + data + ')'),id)
            },
            error:function(data){
                $("#resultVerify").html("文件格式不符或文件太大");
                $("#reponseVerify").html("文件格式不符或文件太大");
                return false;
            }
        });
    })
}

/*********************verify----begin**********************************/

//记录上一次的搜索结果的数量
var preSize=0;
function searchUrlDemo(input) {
    if($("#inputUrlSearchDemo").val()==""){
        alert("url不能为空");
        return false;
    }
    $("#imgShowSearchDemo").attr("src",$("#inputUrlSearchDemo").val())
    var img = new Image();
    img.src = $("#inputUrlSearchDemo").val();
    searchReqDemo(img);

}

function searchReqDemo(img) {
    var threshold =$("#confidence").val();
    if(threshold>0&&threshold<=1){
        document.getElementById("imgShowSearchDemo").style.height="100%";
        document.getElementById("imgShowSearchDemo").style.width="100%";
        getImageWidth(img.src,function (widthImg,heightImg) {
            var imgShowDiv = document.getElementById("imgShowSearchDemoDiv");
            var imgShow = document.getElementById("imgShowSearchDemo");
            console.log("height:"+heightImg)
            console.log("widht:"+widthImg)
            console.log(imgShowDiv.offsetHeight)
            if(heightImg!=widthImg){
                if(heightImg>widthImg){
                    imgShow.style.width=widthImg/heightImg*100+"%"
                    imgShow.style.height="100%"
                }
                if(heightImg<widthImg){
                    imgShow.style.height=heightImg/widthImg*100+"%"
                    imgShow.style.width="100%"
                }
            }else{
                imgShow.style.height="100%"
                imgShow.style.width="100%"
            }
        })
        var formData = new FormData();
        formData.append("n",4);
        formData.append("photo",img.src)
        formData.append("threshold",threshold);
        $.ajax({
            url: 'customer/getDemoFace',
            type: 'POST',
            // dataType: "json",
            data: formData,
            processData: false,
            contentType: false,
            async: true,
            success: function (data) {
                if(data==""||data.length==0){
                    $('#reponseSearchDemo').html("")
                    $('#searchResultDemo').html("图库中未搜索相似人脸")
                    $('#resultShowSearchDemo').html("")
                    return false;
                }
                $('#imgShowSearchDemo').attr("src",img.src);
                // console.log(data)
                dataObj=eval(data);
                showResult(dataObj)
                $('#reponseSearchDemo').html(syntaxHighlight(filter(dataObj)))

            },
            error: function (data) {
                $('#reponseSearchDemo').html("")
                $('#resultShowSearchDemo').html("未在库中搜索到相似人脸或图片格式不符")
                $('#searchResultDemo').html("")
                return false
            }
        });
    }else {
        $('#reponseSearchDemo').html("");
        $('#resultShowSearchDemo').html("请输入范围在0~1的阀值！");
        $('#searchResultDemo').html("");
    }

}

function removeBefore(size) {
    for(var i=1;i<=size;i++){
        if ($("#div"+i)) {
            $("#div"+i).remove();
        }
        if ($("#divResult"+i)) {
            $("#divResult"+i).remove();
        }
    }
}
function uploadImgSearcheDemo(img) {
    var threshold =$("#confidence").val();
    if(threshold>0&&threshold<=1) {
        document.getElementById("imgShowSearchDemo").style.height = "100%";
        document.getElementById("imgShowSearchDemo").style.width = "100%";

        var imgShow = document.getElementById("imgShowSearchDemo")
        var file = img.files[0];
        if (!file) {
            return false;
        }
        var reader = new FileReader();
        reader.onload = function (e) {

            // imgShow.src = e.target.result;

            imgShow.src = e.target.result;
            getImageWidth(imgShow.src, function (widthImg, heightImg) {
                var imgShowDiv = document.getElementById("imgShowSearchDemoDiv");
                // var imgShow = document.getElementById("imgShowSearchDemo");
                console.log("height:" + heightImg)
                console.log("widht:" + widthImg)
                console.log(imgShowDiv.offsetHeight)
                if (heightImg != widthImg) {
                    if (heightImg > widthImg) {
                        imgShow.style.width = widthImg / heightImg * 100 + "%"
                        imgShow.style.height = "100%"
                    }
                    if (heightImg < widthImg) {
                        imgShow.style.height = heightImg / widthImg * 100 + "%"
                        imgShow.style.width = "100%"
                    }
                } else {
                    imgShow.style.height = "100%"
                    imgShow.style.width = "100%"
                }
            })


        }
        reader.readAsDataURL(file)
        //上传文件时当前的图片内容是文件,对比的对象可能时文件或者url
        var formData = new FormData();
        formData.append("n", 3);
        formData.append("photo", file)
        //清除input框的文件状态,解决两次同一张照片不触发事件的问题
        img.value = "";
        $.ajax({
            url: 'customer/getDemoFace',
            type: 'POST',
            data: formData,
            processData: false,
            contentType: false,
            async: true,
            success: function (data) {
                if (data == "" || data.length == 0) {
                    $('#reponseSearchDemo').html("")
                    $('#searchResultDemo').html("图库中未搜索相似人脸")
                    $('#resultShowSearchDemo').html("")
                }
                dataObj = eval(data);
                showResult(dataObj)
                $('#reponseSearchDemo').html(syntaxHighlight(filter(dataObj)))
            },
            error: function (data) {
                $('#reponseSearchDemo').html("")
                $('#searchResultDemo').html("未在库中搜索到相似人脸或图片格式不符")
                $('#resultShowSearchDemo').html("")
                return false
            }
        });
    }else {
        $('#reponseSearchDemo').html("");
        $('#resultShowSearchDemo').html("请输入范围在0~1的阀值！");
        $('#searchResultDemo').html("");
    }
}

//search showResult
function showResult(dataObj) {
    // $('#reponseSearchDemo').html("文件格式不符或文件太大")
    // $('#reponse').html("")
    $("#searchResultDemo").html("")
    $("#resultShowSearchDemo").html("")
    removeBefore(preSize)
    preSize=0;
    for (var v in dataObj){
        preSize++;
        var imgEle=
            "<div>第"+preSize+"张</div>" +
            "<div class='graphic_img4'><img src='"+dataObj[v]['face']['normalized']+"'></div>"
        var confidence="第"+preSize+"张是同一个人的可信度是:"+dataObj[v]['confidence']
        $("#searchResultDemo").append("<div  id='divResult"+preSize+"'>"+ confidence +"</div>");
        $("#resultShowSearchDemo").append("<div class='results_graphic4'  class='imgboxShow' id='div"+preSize+"'>"+ imgEle +"</div>");
    }

}

function filter(data) {
    if(data.length==0){
        return false;
    }
    for(var obj in data){
        data[obj]['face'].normalized="normalizedUrl"
    }
    return data;
}




function removeDiv() {
    if ($('#imgShow11')) {
        $('#imgShow11').remove();
    }
    if ($('#imgShow21')) {
        $('#imgShow21').remove();
    }
}

function Result(width, height, left, top) {
    this.width = width;
    this.height = height;
    this.left = left;
    this.top = top;
}

function getImageWidth(url, callback) {
    var img = new Image();
    img.src = url;

    // 如果图片被缓存，则直接返回缓存数据
    if (img.complete) {
        callback(img.width, img.height);
    } else {
        // 完全加载完毕的事件
        img.onload = function () {
            callback(img.width, img.height);
        }
    }

}

function syntaxHighlight(json) {
    if (typeof json != 'string') {
        json = JSON.stringify(json, undefined, 2);
    }
    json = json.replace(/&/g, '&').replace(/</g, '<').replace(/>/g, '>');
    return json.replace(/("(\\u[a-zA-Z0-9]{4}|\\[^u]|[^\\"])*"(\s*:)?|\b(true|false|null)\b|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?)/g, function (match) {
        var cls = 'number';
        if (/^"/.test(match)) {
            if (/:$/.test(match)) {
                cls = 'key';
            } else {
                cls = 'string';
            }
        } else if (/true|false/.test(match)) {
            cls = 'boolean';
        } else if (/null/.test(match)) {
            cls = 'null';
        }
        return match;
    });
}