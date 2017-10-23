$(document).ready(function () {


    // var mylib;

    var app = new Vue({
        el: '#person-gallery',
        data: {
            message: "abcdef",
            myLibUrl: "",
            testModel: "",
            seen: true,
            todos: []
        },
        watch: {
            todos: {
                handler: function () {
                    console.log("change");

                    setTimeout(function () {
                        var administer_img = $(".administer_img").width();
                        $(".delete_icon").css({"width": administer_img / 4, "height": administer_img / 4})
                    }, 10);
                    // width();
                }
            }
        },
        mounted: function () {
            // getMyGallery()
        },
        methods: {
            initDemo: function () {

            },
            myLibUploadInput: function () {
                var regEn = /[`~!@#$%^&*()+<>?:"{},.\/;'[\]]/im
                var regCn = /[·！#￥（——）：；“”‘、，|《。》？、【】[\]]/im;
                var fileContent = document.getElementById('myLibUpload')
                var metaName = prompt("请输入人脸名称:");
                if (metaName == null || metaName == '') {
                    alert("名称不能未空");
                    fileContent.value = "";
                    return false;
                } else if (regEn.test(metaName) || regCn.test(metaName)) {
                    alert("名称不能包含特殊字符.");
                    fileContent.value = "";
                    return false;
                } else if (metaName.trim() == "") {
                    alert("名称不能全是空格");
                    fileContent.value = "";
                    return false;
                } else if (metaName.length > 20) {
                    alert("名称长度必须大于1小于20");
                    fileContent.value = "";
                    return false;
                } else {
                    var file = fileContent.files[0];
                    if (!file) {
                        return false;
                    }
                    var reader = new FileReader();

                    reader.onload = function (e) {
                        var imgUrl = e.target.result;
                        getImageWidth(imgUrl, function (widthImg, heightImg) {
                            while (widthImg >= 4000 || heightImg >= 4000) {
                                var imgShow = new Image;
                                imgShow.src=imgUrl
                                PicBaseText = compress(imgShow, widthImg * 0.5, heightImg * 0.5, 1);
                                widthImg = widthImg * 0.5;
                                heightImg = heightImg * 0.5;
                                file = dataURItoBlob(PicBaseText);
                            }
                            fileContent.value="";
                            var formData = new FormData();
                            formData.append("meta", metaName);
                            formData.append("mf_selector", "all");
                            formData.append("photo", file);
                            $.ajax({
                                url: 'addToGallery',
                                type: 'POST',
                                dataType: "json",
                                data: formData,
                                processData: false,
                                contentType: false,
                                async: true,
                                success: function (data) {
                                    if (data == 0) {
                                        alert("个人人脸数量已达到上限");
                                        return false;
                                    }
                                    if ($('#picPreload')) {
                                        $('#picPreload').remove()
                                    }
                                    app.todos = data;

                                },
                                error: function (data) {
                                    alert("未检查到人脸或图片格式不符")
                                    return false
                                }
                            });


                        });

                    };
                    reader.readAsDataURL(file);
                }

            },


            getMyGallery: function () {

                $.ajax({
                    url: 'getMyGallery',
                    type: 'GET',
                    dataType: "json",
                    success: function (data) {
                        if ($('#picPreload')) {
                            $('#picPreload').remove()
                        }
                        ;
                        app.todos = data;

                    },
                    error: function (data) {
                        alert("未检查到人脸或图片格式不符")
                        return false
                    }
                });
            },

            deleteMyGallery: function (dom) {
                var message = confirm("确认删除");
                if (message == false) {
                    return false
                }
                if ($('#picPreload')) {
                    $('#picPreload').remove()
                }
                $.ajax({
                    url: 'deleteToGallery',
                    type: 'POST',
                    dataType: "json",
                    data: {"id": dom.alt},
                    success: function (data) {
                        if (data == 1) {
                            alert("出现错误");
                            return false;
                        }
                        if ($('#picPreload')) {
                            $('#picPreload').remove()
                        }
                        app.todos = data;
                        width()
//                        $('#' + dom.alt ).remove();
                    },
                    error: function (data) {
                        if ($('#picPreload')) {
                            $('#picPreload').remove()
                        }
                        app.todos = data;
//                        alert("no face")
//                        $('#' + dom.alt ).remove();
                    }
                });
            },

            myLibUrlCheck: function () {
                if (app.myLibUrl == "") {
                    alert("url不能为空")
                    return false
                } else {
                    var regEn = /[`~!@#$%^&*()+<>?:"{},.\/;'[\]]/im
                    var regCn = /[·！#￥（——）：；“”‘、，|《。》？、【】[\]]/im;
                    var metaName = prompt("请输入人脸名称:");
                    if (metaName == null || metaName == '') {
                        alert("名称不能未空");
                        return false;
                    } else if (regEn.test(metaName) || regCn.test(metaName)) {
                        alert("名称不能包含特殊字符.");
                        return false;
                    } else if (metaName.trim() == "") {
                        alert("名称不能全是空格");
                        return false;
                    } else if (metaName.length > 20) {
                        alert("名称长度必须大于1小于20");
                        return false;
                    } else {
                        var formData = new FormData();
                        formData.append("photo", app.myLibUrl)
                        formData.append("meta", metaName)
                        $.ajax({
                            url: 'addToGallery',
                            type: 'POST',
                            dataType: "json",
                            data: formData,
                            processData: false,
                            contentType: false,
                            async: true,
                            success: function (data) {
                                if (data == 0) {
                                    alert("个人人脸数量已达到上限");
                                    return false;
                                }
                                if ($('#picPreload')) {
                                    $('#picPreload').remove()
                                }
                                app.todos = data;

                            },
                            error: function (data) {
                                alert("未检查到人脸或图片格式不符")
                                return false
                            }
                        });
                    }
                }
            },

            myLibSearchUrl: function () {
                var imgValue = $("#myLibSearchInputUrl").val();
                if (imgValue == "") {
                    alert("url不能为空")
                    return false;
                }
                var img = new Image();
                img.src = imgValue
                app.myLibSearchReq(img);
            },
            myLibSearchReq: function (img) {
                $('#imgShow').attr("src", img.src);
                setPicSize(img.src)

                // setPicSize(img.src)
                // document.getElementById("imgShow").style.height="100%"
                // document.getElementById("imgShowDiv").style.height="100%"
                var formData = new FormData();
                // formData.append("n",4);
                formData.append("photo", img.src)
                $.ajax({
                    url: 'getDemoFace',
                    type: 'POST',
                    // dataType: "json",
                    data: formData,
                    processData: false,
                    contentType: false,
                    async: true,
                    success: function (data) {

                        dataObj = eval(data);
                        if (data == "" || data.length == 0) {
                            $('#reponse').html("")
                            $("#searchResult").html("")
                            $("#resultShow").html("未在库中搜索到相似人脸或图片格式不符")
                            return false;
                        }
                        showResult(dataObj)
                        $('#reponse').html(syntaxHighlight(filter(data)))

                    },
                    error: function (data) {
                        $('#reponse').html("")
                        $("#searchResult").html("")
                        $("#resultShow").html("未在库中搜索到相似人脸或图片格式不符")
                        return false;
                    }
                });
            },
            myLibSearchUpload: function (img) {

                var imgShow = document.getElementById("imgShow")
                // var imgShowDiv = document.getElementById("imgShowDiv")
                var file = img.files[0];
                if (!file) {
                    return false;
                }


                var reader = new FileReader();
                reader.onloadend = function (e) {
                    var imgUrl= e.target.result;
                    imgShow.src = imgUrl;
                    getImageWidth(imgUrl,function (widthImg,heightImg) {
                        setPicSize(imgUrl);

                        //图片的宽或高大于4000时使用canvas对图片进行重绘制
                        while(widthImg>=4000||heightImg>=4000){
                            var PicBaseText=compress(imgShow,widthImg*0.5,heightImg*0.5,1);
                            widthImg=widthImg*0.5;
                            heightImg=heightImg*0.5;
                            file=dataURItoBlob(PicBaseText);
                        }

                        var formData = new FormData();
                        formData.append("photo", file);
                        img.value = "";
                        $.ajax({
                            url: 'getDemoFace',
                            type: 'POST',
                            // dataType: "json",
                            data: formData,
                            processData: false,
                            contentType: false,
                            async: true,
                            success: function (data) {
                                dataObj = eval(data);
                                if (data == "" || data.length == 0) {
                                    $('#reponse').html("")
                                    $("#searchResult").html("")
                                    $("#resultShow").html("未在库中搜索到相似人脸或图片格式不符")
                                    return false;
                                }
                                showResult(dataObj)
                                $('#reponse').html(syntaxHighlight(filter(data)))
                            },
                            error: function (data) {
                                // if(data==""||data.length==0){
                                $('#reponse').html("")
                                $("#searchResult").html("")
                                $("#resultShow").html("未在库中搜索到相似人脸或图片格式不符")
                                return false;
                                // }
                            }
                        });

                    });
                };
                reader.readAsDataURL(file)


            }


        }
    });

}); //ready

var preSize = 0;

function setPicSize(imgUrl, imgShowDiv, imgShow) {
    getImageWidth(imgUrl, function (widthImg, heightImg) {
        var imgShow = document.getElementById("imgShow")
        var imgShowDiv = document.getElementById("imgShowDiv")
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


function getMyGallery() {
    $.ajax({
        url: 'getMyGallery',
        type: 'GET',
        dataType: "json",
        success: function (data) {
            app.todos = data
            return data;
        },
        error: function (data) {
            alert("no face")
            app.todos = data
            return data;

        }
    });
}

function removeBefore(size) {
    for (var i = 1; i <= size; i++) {
        if ($("#div" + i)) {
            $("#div" + i).remove();
        }
        if ($("#divResult" + i)) {
            $("#divResult" + i).remove();
        }
    }
}

function showResult(dataObj) {
    $('#reponse').html("")
    $("#searchResult").html("")
    $("#resultShow").html("")
    // removeBefore(preSize)
    preSize = 0;
    for (var v in dataObj) {
        preSize++;
        var imgEle =
            "<div>第" + preSize + "张</div>" +
            "<div><img src='" + dataObj[v]['face']['normalized'] + "'></div>"
        var confidence = "第" + preSize + "张是同一个人的可信度是:" + dataObj[v]['confidence']
        $("#searchResult").append("<div  id='divResult" + preSize + "'>" + confidence + "</div>");
        $("#resultShow").append("<div class='results_minimg'  id='div" + preSize + "'>" + imgEle + "</div>");
    }

}


function filter(data) {
    if (data.length == 0) {
        return false;
    }
    for (var obj in data) {
        data[obj]['face'].normalized = "normalizedUrl"
    }
    return data;
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

//将base64图片转成input能处理的二进制流
function dataURItoBlob(dataURI) {
    var byteString = atob(dataURI.split(',')[1]);
    var mimeString = dataURI.split(',')[0].split(':')[1].split(';')[0];
    var ab = new ArrayBuffer(byteString.length);
    var ia = new Uint8Array(ab);
    for (var i = 0; i < byteString.length; i++) {
        ia[i] = byteString.charCodeAt(i);
    }
    console.log([ab]);
    return new Blob([ab], {type: mimeString});
}


//图片width或heigh>4000则对图片进行像素抽取
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
