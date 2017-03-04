/**
 * Created by kris on 2017/3/4.
 */
var msgBox = (function(){
    var callbackFn = null;
    var msgbox = $('#msgBoxDiv');
    var bgbox = $('#msgBoxDivBg');

    var height = $('body').height();
    bgbox.css('height',height);

    // 隐藏层
    var boxHide = function(){
        msgbox.hide();
        bgbox.hide();
    }
    // 关闭事件
    msgbox.find('[name="close"]').click(function(){
        boxHide();
        return false;
    });
    // 取消事件
    msgbox.find('[name="cancel"]').click(function(){
        boxHide();
        return false;
    });
    // 确定事情
    msgbox.find('[name="sure"]').click(function(){
        boxHide();
        // 这里解决事件重担执行问题
        if(callbackFn){
            callbackFn();
            callbackFn = null;
        }
        return false;
    });

    // 使用show方法来显示消息内容
    return {
        // msg:消息内容 callback:确实后的回调函数（可以不用定义）
        show:function(msg,callback){
            msgbox.find('[name="content"]').html(msg);
            if(callback){
                callbackFn = callback;
            }
            msgbox.show();
            var doc = document;
            bgbox.show().height(Math.max(doc.body.scrollHeight, doc.documentElement.clientHeight));
        }
    };

})();

var dmsgBox = (function(){
    var callbackFn = null;
    var msgbox = $('#dmsgBoxDiv');
    var bgbox = $('#msgBoxDivBg');
    // 隐藏层
    var boxHide = function(){
        msgbox.hide();
        bgbox.hide();
    }

    // 关闭事件
    msgbox.find('[name="close"]').click(function(){
        boxHide();
        return false;
    });
    // 取消事件
    msgbox.find('[name="cancel"]').click(function(){
        boxHide();
        return false;
    });

    // 确定事情
    msgbox.find('[name="sure"]').click(function(){
        boxHide();
        if(callbackFn){
            callbackFn();
        }
        return false;
    });


    // 绑定验证码刷新
    $('#msgVerifyRefresh').click(function(){
        $("#msgVerifycode").val('');
        $('#msgVerifyImg').attr('src','http://verify2.xunlei.com/image?t=MEA&cachetime='+(new Date().getTime()));
        return false;
    });

    //初始化验证码输入框
    $("#msgVerifycode").val('');

    // 使用show方法来显示消息内容
    return {
        // msg:消息内容 callback:确实后的回调函数（可以不用定义）
        show:function(msg,callback){
            msgbox.find('#msgContent').html(msg);
            //msgbox.find('msgVerifyWrap').html(pro_name);
            callback && (callbackFn=callback);
            $('#msgVerifyRefresh').click();
            msgbox.show();
            var doc = document;
            bgbox.height(Math.max(doc.body.scrollHeight, doc.documentElement.clientHeight)).show();
            alert(bgbox.css())
            $("#msgVerifycode").focus();
        },
        hide:function(){
            boxHide();
        }
    };


})();


var jumpBox = (function(){
    var callbackFn = null;
    var timehandel=null;
    var jumplink="";
    var msgbox = $('#jumpBoxDiv');
    var bgbox = $('#jumpBoxDivBg');
    var tipsplace=$("#jump_shop");
    var ratesplace=$("#scoreRate");
    // 隐藏层
    var boxHide = function(){
        if(timehandel)
            clearTimeout(timehandel);
        msgbox.hide();
        bgbox.hide();

    }
    // 关闭事件
    msgbox.find('[name="close"]').click(function(){
        boxHide();
        return false;
    });

    // 确定事情
    msgbox.find('[name="clickToJump"]').click(function(){
        boxHide();
        // 这里解决事件重担执行问题
        if(callbackFn){
            callbackFn(jumplink);
            callbackFn = null;
        }
        return false;
    });


    // 使用show方法来显示消息内容
    return {
        // msg:消息内容 callback:确实后的回调函数（可以不用定义）
        show:function(msg,param,callback,rate){
            tipsplace.html(msg);
            if(rate){
                ratesplace.html(rate+'%');
            }

            if(param)
                jumplink=param;
            callback && (callbackFn=callback);

            msgbox.show();
            var doc = document;
            bgbox.show().height(Math.max(doc.body.scrollHeight, doc.documentElement.clientHeight));

            if(callback)
                timehandel = setTimeout("callbacktimeout('"+jumplink+"')",3000);
        },
        hide : function(){
            boxHide();
        }
    };
})();



