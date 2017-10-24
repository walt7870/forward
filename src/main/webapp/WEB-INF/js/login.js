$(function() {
    $("body").keydown(function() {
        if (event.keyCode == "13") {//keyCode=13是回车键
            $('#submit').click();
        }
    });
    $('#submit').click(function () {
        var name =$("#name").val();
        if($("#name").val()==''){
            $('#msg').text("用户名不能为空");
            return false;
        }
        if($("#password").val()==''){
            $('#msg').text("密码不能为空");
            return false;
        }
        var regEn = /[`~!@#$%^&*()+<>?:"{},.\/;'[\]\s]/im,
            regCn = /[·！#￥（——）：；“”‘、，|《。》？、【】[\]\s]/im;

        if(regEn.test(name) || regCn.test(name)) {
            $('#msg').text("用户名不能包含特殊字符");
            return false;
        }
        $.ajax({
            type:"POST",
            url:"loginCheck",
            async: false,
            data:{name:$("#name").val(),password:$("#password").val()},
            dataType:'json',
            success:function(msg){
                if(msg){
                    $('#msg').text("");
                    window.location.href="personInfo"
                }else{
                    $('#msg').text("用户未激活或者用户名或密码错误");

                }
            }
        });
    });
})