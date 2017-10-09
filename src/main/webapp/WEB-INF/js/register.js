$(function() {
    /*
     * 思路大概是先为每一个required添加必填的标记，用each()方法来实现。
     * 在each()方法中先是创建一个元素。然后通过append()方法将创建的元素加入到父元素后面。
     * 这里面的this用的很精髓，每一次的this都对应着相应的input元素，然后获取相应的父元素。
     * 然后为input元素添加失去焦点事件。然后进行用户名、邮件的验证。
     * 这里用了一个判断is()，如果是用户名，做相应的处理，如果是邮件做相应的验证。
     * 在jQuery框架中，也可以适当的穿插一写原汁原味的javascript代码。比如验证用户名中就有this.value，和this.value.length。对内容进行判断。
     * 然后进行的是邮件的验证，貌似用到了正则表达式。
     * 然后为input元素添加keyup事件与focus事件。就是在keyup时也要做一下验证，调用blur事件就行了。用triggerHandler()触发器，触发相应的事件。
     * 最后提交表单时做统一验证 做好整体与细节的处理
     */
    // 如果是必填的，则加红星标识.
    // $("form :input.required").each(function() {
    //     var $required = $("<strong style='float: right;color: red;'> *</strong>"); // 创建元素
    //     $(this).parent().append($required); // 然后将它追加到文档中
    // });
    // 文本框失去焦点后
    $('input').blur(function() {
        var $parent = $(this).parent();
        $parent.find(".formtips").remove();

        // 验证用户名
        if ($(this).is('#userName')) {
            if (this.value == "" ||this.value != "" && !/^([a-zA-Z0-9_]{6,20})$/.test(this.value)) {
                var errorMsg = '用户名由6位以上数字、字母或下划线组成';
                $parent.append('<span class="formtips onError">' + errorMsg + '</span>');
            }else {
                $.ajax({
                    url : 'check',
                    type : 'POST',
                    data : {userName:$("#userName").val()},
                    async: false,
                    dataType : 'json',
                    success : function(data) {
                        if(!data){
                            // $parent.append('<span class="formtips">用户名可以使用</span>');
                            // $parent.removeClass("has-error");
                            // $parent.addClass("has-success");
                        }
                        else{
                            // $parent.addClass("has-error");
                            $parent.append('<span class="formtips onError">该用户已被注册</span>');
                        }
                    }
                });
            }
        }
        // 验证密码
        if ($(this).is('#userPassword')) {
            if(this.value == ""||this.value.length<6){
                $parent.append('<span class="onError formtips">密码长度必须大于6位</span>');
            }
        }
        // 保证密码一致
        if ($(this).is('#surePassword')) {
            if (this.value == ""||this.value.length<6 ||this.value != $('#userPassword' ).val()) {
                var errorMsg = '请确保两次密码一致且长度大于6';
                $parent.append('<span class="formtips onError">' + errorMsg + '</span>');
            }
        }
        // 验证邮件
        if ($(this).is('#email')) {
            if (this.value == "" || !/.+@.+\.[a-zA-Z]{2,4}$/.test(this.value)) {
                var errorMsg = '请输入正确的E-Mail地址.';
                $parent.append('<span class="formtips onError">' + errorMsg + '</span>');
            }
        }
        // // 验证手机号
        // if ($(this).is('#userPhoneNumber')) {
        //     if ((this.value != "" && !/^(((13[0-9]{1})|(15[0-9]{1})|(18[0-9]{1}))+\d{8})$/.test(this.value))) {
        //         var errorMsg = '手机号格式错误';
        //         $parent.addClass("has-error");
        //         $parent.append('<span class="formtips onError">' + errorMsg + '</span>');
        //     }else if(this.value==""){
        //         $parent.removeClass("has-error");
        //         $parent.removeClass("has-success");
        //     }
        //     else {
        //         $parent.removeClass("has-error");
        //         $parent.addClass("has-success");
        //
        //     }
        // }






    }).keyup(function() {
        $(this).triggerHandler("blur");
    }).focus(function() {
        $(this).triggerHandler("blur");
    }); // end blur
    // 提交，最终验证。

    $('#send').click(function() {
        $('input').trigger("blur");
        if($('#authCode').val()==''){
            $('#codeMsg').text("验证码不能为空");
            return false;
        }

        $.ajax({
            url : 'checkCode',
            type : 'POST',
            data : {"authCode":$('#authCode').val()},
            async: true,
            // dataType : 'json',
            success : function(data) {
                if(!data){
                    $('#codeMsg').text("验证码错误");
                    return false;
                }else{
                    $('#codeMsg').text("");
                    $('input').trigger("blur");
                    // $("form :input.required").trigger('blur');
                    var numError = $('.onError').length;
                    if (numError) {
                        return false;
                    };
                    $.ajax({
                        url : 'appCommitReg',
                        type : 'POST',
                        data : {name:$('#userName').val(),password:$('#userPassword').val(),email:$('#email').val()},
                        async: true,
                        // dataType : 'json',
                        success : function(data) {
                            if(data){
                                alert("注册成功");
                                window.location.href="login";
                            }else{
                                alert("注册失败")
                            }
                        }
                    })
                }
            },
            error:function () {
                $('#codeMsg').text("验证码验证出错");
                return false;
            }
        });

        // $('input').each(function () {
        //     $(this).trigger('focus')
        //     console.log($(this).trigger('blur'));
        //     // $(this).triggerHandler("blur");
        // })

    });


})
function chageCode(){
    $('#codeImage').attr('src','verifyCode?timestamp=' + (new Date()).valueOf());//链接后添加Math.random，确保每次产生新的验证码，避免缓存问题。
}
