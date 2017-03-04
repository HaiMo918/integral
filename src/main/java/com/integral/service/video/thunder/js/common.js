/**
 * Created by kris on 2017/3/4.
 */
// 得到URL查询的键值,如 ?id=10 中的ID的值
function get_url_param(key)
{
    key = key .replace(/\[/g,"%5B");
    key = key .replace(/\]/g,"%5D");
    var query = location.search;
    var reg = "/^.*[\\?|\\&]" + key + "\\=([^\\&]*)/";
    reg = eval(reg);
    var ret = query.match(reg);
    if (ret != null) {
        return decodeURIComponent(ret[1]);
    } else {
        return "";
    }
}

// 给URL加随机数防止缓存
function no_cache_url(url)
{
    if(url.indexOf('?') == -1)
    {
        url += '?';
    }else{
        url += '&';
    }
    url += 'rand='+Math.random();
    return url;
}


// 得到COOKIE值
function get_cookie(name){
    try{
        return (document.cookie.match(new RegExp("(^"+name+"| "+name+")=([^;]*)"))==null)?"":decodeURIComponent(RegExp.$2);
    }
    catch(e){
        return (document.cookie.match(new RegExp("(^"+name+"| "+name+")=([^;]*)"))==null)?"":RegExp.$2;
    }
}

// 写COOKIE
function set_cookie(name,value,second,domain){
    if(!domain){
        domain = location.hostname;
    }
    if(arguments.length>2){
        var expireDate=new Date(new Date().getTime()+(second*3600*1000));
        document.cookie = name + "=" + encodeURIComponent(value) + "; path=/; domain="+domain+"; expires=" + expireDate.toGMTString() ;
    }else{
        document.cookie = name + "=" + encodeURIComponent(value) + "; path=/; domain="+domain;
    }
}



//解决跨域的AJAX回调处理
function jsonCallBack(url,callback){
    url += '&callback=?';
    $.get(url,function(json){
        callback(json);
    },'jsonp');
}

// 是否登录了
function haslogin(){
    var sessionid = get_cookie("sessionid");
    if(sessionid== null){
        sessionid="";
    }
    var vip_sessionid = get_cookie("vip_sessionid");
    if(vip_sessionid== null){
        vip_sessionid="";
    }
    if(vip_sessionid == "-1" || sessionid.length < 32){
        if(vip_sessionid != ""){
            clearCookie();
        }
        return false;
    }
    return true;
}

// 字符串替换
function str_replace(str,search,replace){
    str = str.toString();
    search = search.toString();
    replace = replace .toString();
    return str.replace(new RegExp(search,'gm'),replace);
}


//VIP中心相关域名
var URL_ACT = "http://act.vip.xunlei.com/";
var URL_DACT = "http://dyact.vip.xunlei.com/";
var URL_PAY = "http://vip.xunlei.com/";
var URL_IPAY = "http://img.vip.xunlei.com/";
var URL_DPAY = "http://dynamic.vip.xunlei.com/";

isUndef = function(a){ return typeof a == "undefined";};
isNull = function(a){ return typeof a == "object" && !a; };
var isFF = (navigator.userAgent.toLowerCase().indexOf("firefox")!=-1);
var isIE = (navigator.appVersion.indexOf("MSIE") != -1) ? true : false;
var isIE6  = (navigator.appVersion.indexOf("MSIE 6.0") != -1) ? true : false;
var isOpera = (navigator.userAgent.indexOf("Opera") != -1) ? true : false;
var isChrome = (navigator.userAgent.indexOf("Chrome") != -1) ? true : false;
var isSafari = (navigator.userAgent.indexOf("Safari") != -1) ? true : false;
var isWin= (navigator.platform == "Win32" || navigator.platform == "Windows");
function isFunction(wh){if(!wh){return false;}return(wh instanceof Function || typeof wh=="function");};
var $id = function(id){return document.getElementById(id);}

if(isIE6){
    try{//IE6下缓存背景图片
        document.execCommand("BackgroundImageCache", false, true);
    }catch(e){}
}

String.prototype.escapeHTML = function(){
    return this.replace(/<.+?>/mig,"");
}
String.prototype.bytesLength = function(){
    var cArr = this.match(/[^\x00-\xff]/ig);
    return this.length + (cArr == null ? 0 : cArr.length);
}
String.prototype.strip = function(){
    var temp = this;
    temp = temp.replace(/&amp;/ig, "&");
    temp = temp.replace(/&/ig, "&amp;");
    temp = temp.replace(/</ig, "&lt;");
    temp = temp.replace(/>/ig, "&gt;");
    temp = temp.replace(/\"/ig, "&quot;");
    temp = temp.replace(/\'/ig, "&#39;");
    temp = temp.replace(/ /ig, "&nbsp;");
    temp = temp.replace(/(\r?\n)|\r/ig, "<br />");
    return temp;
}
String.prototype.trim = function(){
    return this.replace(/(^\s*)|(\s*$)/g,"");
}

$P = function(parameter, url){
    url = url || window.location.href;
    return getParameter(parameter, url);
};

function getParameter(parameter,url) {
    var reg = "/^.*[\\?|\\&]" + parameter + "\\=([^\\&]*)/";
    reg = eval(reg);
    var ret = url.match(reg);
    if (ret != null) {
        return decodeURIComponent(ret[1]);
    } else {
        return "";
    }
}

function jsonStrToObject(jsonString){
    if($.browser.msie){
        return eval('('+jsonString+')');
    }else{
        return new Function('return '+jsonString)();
    }
}

function getJson(url, data, f) {
    if(url.toString().indexOf('?') != -1)
        url = url.toString() + '&';
    else
        url = url.toString() + '?';
    $.getJSON(url+"format=json&jsoncallback=?", data, f);
}

//注意：这个函数不能跨域。如果要跨域post，请用xlrequest
function postJson(url, data, f) {
    if(url.toString().indexOf('?') != -1)
        url = url.toString() + '&';
    else
        url = url.toString() + '?';
    $.post(url+"jsoncallback=?", data, f, 'json');
}

function remove_array_item(item,array){
    var tmp = Array();
    for(var i=0;i<array.length;i++)
    {
        if(array[i] == item) continue;
        tmp.push(array[i]);
    }
    array = tmp;
    return array;
}
function unique_array(data){
    data = data || [];
    var a = {};
    for (var i=0; i<data.length; i++) {
        var v = data[i];
        if (typeof(a[v]) == 'undefined'){
            a[v] = 1;
        }
    };
    data.length=0;
    for (var i in a){
        data[data.length] = i;
    }
    return data;
}

/*
 * VIP的cookie数量较多，因此设立一个vipcookie的cookie，将大部分
 * 不重要的信息压缩为一个cookie，防止cookie丢失。
 **/
function getCookie(name){
    var val = getRealCookie(name);
    if(val.trim() == ''){
        var vipcookie = getRealCookie('vipcookie');
        if(vipcookie.trim()==''){
            return '';
        }
        var cookies = vipcookie.split('&');
        for(var i=0;i<cookies.length;i++){
            ary = cookies[i].split('=');
            if(ary.length>1 && ary[0] == name){
                return decodeURIComponent(ary[1]);
            }
        }
        return '';
    }else{
        return val.trim();//如果cookie中有该值，优先使用该值
    }
}
function setCookie(name,value,hours,isBaseDomain){
    value = value + '';
    if(isBaseDomain != undefined && isBaseDomain == 1){
        setRealCookie(name,value,hours,1);
    }else{
        var vipcookie = getRealCookie('vipcookie');
        if(value.trim()==''){//删除cookie
            if(vipcookie!=''){
                var check = getCookie(name);
                if(check!=''){
                    var cookies = vipcookie.split('&');
                    var newcookie = new Array;
                    for(var i=0;i<cookies.length;i++){
                        ary = cookies[i].split('=');
                        if(ary.length>1 && ary[0] != name){
                            newcookie.push(cookies[i]);
                        }
                    }
                    vipcookie = newcookie.join('&');
                }
            }
        }else{//添加cookie
            //删除原生cookie中的此值
            setRealCookie(name,'',0);
            if(vipcookie==''){
                vipcookie = name.trim()+'='+encodeURIComponent(value);
            }else{
                //check if has the same item , if so , replace it , otherwise add it.
                var check = getCookie(name);
                if(check!=''){
                    var cookies = vipcookie.split('&');
                    for(var i=0;i<cookies.length;i++){
                        ary = cookies[i].split('=');
                        if(ary.length>1 && ary[0] == name){
                            cookies[i] = name+'='+encodeURIComponent(value);
                            break;
                        }
                    }
                    vipcookie = cookies.join('&');
                }else{
                    vipcookie = vipcookie+'&'+name.trim()+'='+encodeURIComponent(value);
                }
            }
        }
        if(hours != undefined){
            setRealCookie('vipcookie',vipcookie,hours);
        }else{
            setRealCookie('vipcookie',vipcookie);
        }
    }
}
function getRealCookie(name){
    return (document.cookie.match(new RegExp("(^"+name+"| "+name+")=([^;]*)"))==null)?"":decodeURIComponent(RegExp.$2);
}
function setRealCookie(name,value,hours,isBaseDomain){
    if(arguments.length>2){
        var expireDate=new Date(new Date().getTime()+hours*3600000);
        if(isBaseDomain != undefined && isBaseDomain == 1){
            document.cookie = name + "=" + encodeURIComponent(value) + "; path=/; domain=xunlei.com; expires=" + expireDate.toGMTString() ;
        }else{
            document.cookie = name + "=" + encodeURIComponent(value) + "; path=/; domain=jifen.xunlei.com; expires=" + expireDate.toGMTString() ;
        }
    }else
        document.cookie = name + "=" + encodeURIComponent(value) + "; path=/; domain=jifen.xunlei.com";
}
// *** cookie函数end

function getnocacheurl(urlstr){
    var returnurl = "";
    var cachetime = new Date().getTime();
    var index = urlstr.indexOf("cachetime=");
    var param = urlstr.indexOf("?");
    if(index == -1){
        if(param == -1){returnurl = urlstr + "?cachetime=" + cachetime;}
        else{returnurl = urlstr + "&cachetime=" + cachetime;}
    }else{
        if(param == -1){returnurl = urlstr.substring(0,index) + "?cachetime=" + cachetime;}
        else{returnurl = urlstr.substring(0,index) + "&cachetime=" + cachetime;}
    }
    return returnurl.replace('&&','&').replace('?&','?');
}

function op(page){
    window.open(getnocacheurl(page));
}
function go(page){
    top.location.href = getnocacheurl(page);
}

// 将内容复制到剪贴板
function copy_alert(txt, title) {
    if(copy_code(txt)) {
        alert(title);
    }
}
function copy_code(copy){
    if (window.clipboardData){
        window.clipboardData.setData("Text", copy);
        return true;
    }else if (window.netscape){
        try{
            netscape.security.PrivilegeManager.enablePrivilege('UniversalXPConnect');
        }catch(err){
            return false;
        }
        var clip = Components.classes['@mozilla.org/widget/clipboard;1'].createInstance(Components.interfaces.nsIClipboard);
        if (!clip) return false;
        var trans = Components.classes['@mozilla.org/widget/transferable;1'].createInstance(Components.interfaces.nsITransferable);
        if (!trans) return false;
        trans.addDataFlavor('text/unicode');
        var str = new Object();
        var len = new Object();
        var str = Components.classes["@mozilla.org/supports-string;1"].createInstance(Components.interfaces.nsISupportsString);
        var copytext=copy;
        str.data=copytext;
        trans.setTransferData("text/unicode",str,copytext.length*2);
        var clipid=Components.interfaces.nsIClipboard;
        if (!clip) return false;
        clip.setData(trans,null,clipid.kGlobalClipboard);
        return true;
    }
    return false;
}


// 汉字处理
String.prototype.lenB = function(){
    return this.unHtmlReplace().replace(/\*/g," ").replace(/[^\x00-\xff]/g,"**").length;
}
String.prototype.unHtmlReplace = function () {
    var s = (this).replace(/&amp;/g,"&").replace(/&lt;/g,"<").replace(/&gt;/g,">").replace(/&nbsp;/g," ").replace(/&quot;/g,"\"");
    return s.replace(/&#(\d{2});/g,function($0,$1) {return unescape("%"+parseInt($1).toString(16));});
}
String.prototype.chinesecut = function(leng , pad){
    pad = pad||"";
    str = this.unHtmlReplace();
    len = 0;
    jslen = 0;
    for(var i=0;i<str.length - 1;i++){
        if(str.substr(i,1).replace(/[^\x00-\xff]/g,"**") == '**'){
            len = len+2;
        }else{
            len++;
        }
        jslen++;
        if(len>=leng){
            break;
        }
    }
    if(len<leng){
        return this;
    }else{
        var ret = str.substr(0,jslen);
        if(len<str.lenB()){
            return ret+pad;
        }else{
            return ret;
        }
    }
}

function xlrequest(url , jobj){
    var ifr_name="poster_iframe_"+(new Date().getTime())+"_"+parseInt(Math.random()*100000);

    ifr = IFrame.create( {name:ifr_name,
        width: "0",
        height: "0",
        id: ifr_name} );
    document.body.appendChild(ifr);

    if(jobj.method.toLowerCase() == 'get'){
        if(jobj.parameters&&jobj.parameters!=''){
            ifr.src=url+"?"+jobj.parameters;
        }else{
            ifr.src=url;
        }
    }else{
        var formParam = {
            method: "post",
            target: ifr_name,
            action: url
        };
        if(jobj.acceptCharset&&jobj.acceptCharset.toLowerCase()=='gbk'){
            formParam.acceptCharset = jobj.acceptCharset;
            if ($.browser.msie) {
                //不能绑定onsubmit，因为JS进行submit的情况下不会触发onsubmit事件
                document.charset = "gbk";
            }
        }
        var form=Form.create(formParam);
        Form.addfield(form,getPostData(jobj.parameters));
        document.body.appendChild(form);
        form.submit();
    }

    var dataLoadSuccess = function(){
        if(jobj.onSuccess){
            jobj.onSuccess(ifr);
        }
        try{
            //一个页面加载时同时多次执行xlrequest会出错，这里直接将错误抛出。
            if(ifr) document.body.removeChild(ifr);
        }catch(e){}
        try{
            if(form) document.body.removeChild(form);
        }catch(e){}
    }

    if(window.attachEvent){
        ifr.attachEvent("onload",dataLoadSuccess);
    }else{
        ifr.addEventListener("load",dataLoadSuccess,false);
    }
}

var Form = {
    create : function (attr) {
        var form=document.createElement("form");
        for( name in attr )
            eval("form."+name+"='"+attr[name]+"'");
        return form;
    },
    addfield : function (form, data) {
        for( name in data ) {
            try{
                var input = document.createElement("<input type='text' name='"+name+"'>");
            }catch(e){
                var input = document.createElement("input");
                input.type = "text";
                input.setAttribute("name", name);
            }
            input.style.display = 'none';
            input.value = data[name];
            form.appendChild(input);
        }
    }
}

function getPostData(parameters){
    var data={};
    var param=parameters;
    if( !param || param.length==0 ) return data;

    var arr=param.split("&");
    for(var i=0; i<arr.length; i++)
    {
        var pos=arr[i].indexOf("=");
        if(pos<0) continue;
        var name=arr[i].substring(0, pos);
        var value=decodeURIComponent( arr[i].substr(pos+1) );
        data[name]=value;
    }
    return data;
}

function makePostData(obj){
    var param = new Array;
    for(var item in obj){
        param.push(item + '=' + encodeURIComponent(obj[item]));
    }
    return param.join('&');
}


/* ============== 登录和退出是公共方法 ============= */

// 退出
// function logout(){
// 	$.ajax({type:"GET" , url:"http://login.xunlei.com/unregister?sessionid="+getCookie("sessionid") , data:null , dataType:"script"});
// 	clearCookie();
// 	var url = getnocacheurl(URL_DPAY+"login/indexlogin_contr/logout/");
// 	xlrequest(url,{onSuccess: refreshPage,parameters: '',method: "POST"});
// }
// 清除cookie
function clearCookie() {
    var ckeys = ["vipcookie","vip_nickname"];
    for(var i=0;i<ckeys.length;i++){
        setRealCookie(ckeys[i],"",0);
    }
    //主域名
    var ckeys1 = ["sessionid","usrname","nickname","usernick","userid","usertype","usernewno","blogresult","score","order","isvip","sex","upgrade","logintype","checkans","checkpwd",'jifen_sign','jifen_isvip'];
    for(var i=0;i<ckeys1.length;i++){
        setRealCookie(ckeys1[i],"",0,1);
    }
    // 积分商城域名
    ckeys1 = ['jifen_sign','jifen_isvip'];
    for(var i=0;i<ckeys1.length;i++){
        setRealCookie(ckeys1[i],"",0);
    }
}

// 退出之后的回调
function refreshPage(){
    location.reload();
}
var MsgShow  = {
    popDiv:null,
    backDiv:null,
    leftoffset:207,
    topoffset:60,
    timeout:null,
    showInterface: function(mydiv){
        MsgShow.exit();
        window.onresize=window.onscroll=null;
        var isIE=!!window.ActiveXObject;
        var isIE6=isIE&&!window.XMLHttpRequest;
        if(MsgShow.backDiv == null) {
            MsgShow.backDiv = $('<div></div>');
            MsgShow.backDiv.css({backgroundColor:"Black",minWidth:'960px',filter:"alpha(opacity=40)",opacity:0.4,position:'absolute',left:0,top:0,zIndex:80,width:'100%'});
            MsgShow.backDiv.appendTo('body');
        }
        MsgShow.popDiv = document.getElementById(mydiv);
        if(!MsgShow.popDiv){
            MsgShow.popDiv=mydiv;
        }
        MsgShow.popDiv.style.position = "fixed";
        if(isIE6){
            MsgShow.popDiv.style.position = "absolute";
            MsgShow.topoffset=0;
        }
        MsgShow.popDiv.style.zIndex  = 901;
        MsgShow.popDiv.style.display ="";
        MsgShow.backDiv.width(Math.min(document.body.scrollWidth, document.documentElement.scrollWidth));
        MsgShow.backDiv.show();
        if(isIE6){
            window.onscroll = MsgShow.setPos;
        }
        window.onresize = MsgShow.refresh;
        MsgShow.refresh();
    },
    setPos:function(){
        MsgShow.timeout&&clearTimeout(MsgShow.timeout);
        MsgShow.timeout=setTimeout(function(){
            MsgShow.refresh(true)
        },10);
    },
    refresh:function(flag){
        // console.log(flag)
        var isIE=!!window.ActiveXObject;
        var isIE6=isIE&&!window.XMLHttpRequest;
        var clientHeight=Math.max(document.body.scrollHeight, document.documentElement.scrollHeight);
        //var clientHeight=Math.min(document.body.scrollHeight, document.documentElement.scrollHeight);
        var clientWidth=Math.max(document.body.clientWidth, document.documentElement.clientWidth);
        var left = (clientWidth - MsgShow.popDiv.scrollWidth)/2;
        var scrollTop = document.body.scrollTop + document.documentElement.scrollTop;
        if(isIE6){
            var o_top=(document.documentElement.clientHeight - MsgShow.popDiv.scrollHeight)/2+MsgShow.topoffset+scrollTop;
        }else{
            var o_top=(document.documentElement.clientHeight - MsgShow.popDiv.scrollHeight)/2+MsgShow.topoffset
        }
        // alert(o_top)
        var top = o_top;
        var height = clientHeight;
        // if(isIE6){
        //     MsgShow.backDiv.width(document.body.clientWidth);
        // }else{
        //     MsgShow.backDiv.width(Math.max(document.body.scrollWidth, document.documentElement.scrollWidth));
        // }
        if(flag!==true){
            // console.log('here')
            MsgShow.backDiv.width(clientWidth);
            MsgShow.backDiv.height(height);
        }
        MsgShow.popDiv.style.left = left + "px";
        MsgShow.popDiv.style.top = top + "px";
    },
    exit: function(){
        if(MsgShow.popDiv) MsgShow.popDiv.style.display ="none";
        if(MsgShow.backDiv) MsgShow.backDiv.hide();
        window.onscroll = window.onresize = null;
    }
}


$(function(){
    try{
        if ((document.characterSet || document.charset).toLowerCase() != 'utf-8' && navigator.userAgent.indexOf("MSIE")>0){
            window.location = window.location;
        }
    }catch (exp) {}
});