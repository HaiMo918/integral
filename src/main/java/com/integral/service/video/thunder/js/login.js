/**
 * Created by kris on 2017/3/4.
 */
/**
 * 登录相关代码
 *
 * 整理：V哥
 */

/*
 登录后的回调函数数组，其它JS如果要用到要先引入这个JS文件，然后再在自己的JS文件中注册一个函数，
 例如：
 --------------------------------------------
 logined_callback['index'] = function(){
 alert('登录后的回调');
 }
 --------------------------------------------
 其实 'index' 为下标变量，可以任何取值。
 */
var logined_callback = [];
// 判断当前浏览器是否支持placeholder属性
var testInput = document.createElement("input");
var placeholderSupport = 1;
if(!('placeholder' in testInput))
    placeholderSupport = 0;
var loginBusiness = (function(){
    var userPanelConfig = {
        'unloginTab' : $('#unloginTab'),	// 未登录时显示的元素
        'loginTab' : $('#loginTab'),	// 登录后显示的元素
        'login' : $('#showLogin,[name="showLogin"]'),	// 登录按钮
        'logout' : $('[name="logout"]'),	// 退出按钮
        'userName' : $('[name="userName"]:not("input")'),
        'nickPic' : $('[name="nickPic"]')
    };

    var prototype = {
        init : function(){
            this.initLoginInfo();
            this.listenUserPanel();
            this.listenLoginBlock(); //监听登录a标签
        },
        initLoginInfo : function(){
            if(haslogin()){
                this.showLoginInfo();
            }else{
                this.showUnloginInfo();
            }
        },
        // 监听用户信息模块的事件
        listenUserPanel : function(){
            var that = this;
            // 点击【用户登录】
            userPanelConfig.login.click(function(){
                that.showLoginPanel();
                return false;
            });

            // 点击【退出】
            userPanelConfig.logout.click(function(){
                // logout();
                xlQuickLogin.logout(); // 这里用新的登录组件登出方法
                clearCookie(); // 清除cookie
                return false;
            });
            //点击用户头像
            userPanelConfig.nickPic.click(function(){
                if(haslogin()){
                    document.location.href='http://jifen.xunlei.com/myinfo';
                }else{
                    that.showLoginPanel();
                }
                return false;
            });

        },
        // 监听后台所有配置块需要登录态的#id a链接
        listenLoginBlock : function(){
            // $('.needLogin').unbind().bind('click',function(){
            // 	if(!haslogin()){
            // 		loginBusiness.showLoginPanel();
            // 		return false;
            // 	}
            // });
            $('body').unbind().on('click', '.needLogin',function(){
                if(!haslogin()){
                    loginBusiness.showLoginPanel();
                    return false;
                }
            })
        },
        // 显示登录用户信息界面
        showLoginInfo : function(){
            var nickname = getCookie("vip_nickname").chinesecut(8).strip();
            nickname = nickname ? nickname : getCookie("usernick");
            var userName = nickname ? nickname : getCookie("usernewno");
            var userid = getCookie('userid');
            userPanelConfig.userName.attr({'title':userName,'href':'http://i.xunlei.com'}).html(userName);
            userPanelConfig.nickPic.attr('src','http://img.user.kanimg.com/usrimg/'+userid+'/50x50');
            if(getCookie('isvip') == 1){
                userPanelConfig.userName.css('color', '#F60');
            }else{
                userPanelConfig.userName.css('color', '#959595');
            }
            userPanelConfig.unloginTab.hide();
            userPanelConfig.loginTab.show();
            // 登录回调
            for(var key in logined_callback){
                logined_callback[key]();
            }
        },
        // 显示未登录时的用户界面
        showUnloginInfo : function(){

        },

        // 显示登录框
        showLoginPanel : function(){
            xlQuickLogin.popup();
        },
        //刷新当前页面的用户会员信息
        refreshVipUserInfo : function(){
            var url = getnocacheurl("http://dynamic.vip.xunlei.com/login/asynlogin_contr/asynProxy/?rt="+Math.random());
            $.ajax({
                type: "GET",url: url,data: '',dataType:'jsonp',
                success: function(data){
                    var regcookie = function(cname , name){
                        if(data[name]) setCookie(cname,data[name]);
                    };
                    // 这里去掉一些和登录后从帐号返回来cookie
                    regcookie('isvip' , 'isvip');
                    regcookie('vas_type' , 'vas_type');
                    regcookie('vip_nickname' , 'nickname');
                    regcookie('vip_growvalue' , 'growvalue');
                    regcookie('vip_expiredate' , 'expiredate');
                    regcookie('vip_level' , 'level');
                    regcookie('vip_isyear' , 'isyear');
                    regcookie('vip_paytype' , 'paytype');
                    regcookie('vip_daily' , 'daily');
                    regcookie('vip_is_good_number' , 'is_good_number');
                    loginBusiness.initLoginInfo();
                }
            });
        }
    };
    return prototype;
})();
var login_init = function(){
    xlQuickLogin.init({
        loginID : '106', 	// 一个唯一标识符，方便统计，建议设置成域名中的字符串，如 jifen.xunlei.com域名要使用的话，可把它设置成 act
        registerID : 'score_shop',    // 注册id
        popupMask: true, 		// 是否在弹窗后面加个半透明的遮罩层（只对弹窗有效，对内嵌登录无效）
        UITheme: 'popup',	// 使用弹窗主题，另外可以通过 xlQuickLogin.registerUI(theme, obj) 去注册其它主题
        UIStyle: '',  // 是否加载系统默认的样式，如果是使用系统主题，则可以设置成 true，如果是自定义的主题，可以设置为 false，或者对应 css 的地址
        // 登录成功后的默认回调， 有三种登录方式，通过参数 type 来识别， 系统默认的是刷新页面
        loginSuccessFunc: function(type){
            loginBusiness.refreshVipUserInfo(); // 登录后加载vip cookie
            // location.reload();
        }
    });
};
// 系统全局自动运行脚本
$(function(){
    login_init();
    window.login = function(){
        xlQuickLogin.popup();
    }
    loginBusiness.init();
});