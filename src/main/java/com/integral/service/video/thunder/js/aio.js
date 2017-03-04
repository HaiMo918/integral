/**
 * Created by kris on 2017/3/4.
 */
window.xlQuickLogin = {};
(function () {
    var undef = void 0, CONFIG, Util, request, report, UIManager, xlClient, isIE6 = !!("ActiveXObject" in window && !("XMLHttpRequest" in window));
    CONFIG = {
        LOGIN_ID: "",
        REGISTER_ID: "",
        LOGIN_TYPE_COOKIE_NAME: "_x_t_",
        LOGIN_KEY_NAME: "loginkey",
        ALL_HTTPS: false,
        SET_ROOT_DOMAIN: true,
        AUTO_LOGIN_EXPIRE_TIME: 2592e3,
        LOGIN_TYPES: "12",
        REGISTER_TYPES: "2",
        RETRY_LOGIN_ON_SERVER_ERROR: true,
        SERVER_TIMEOUT: 7e3,
        LOGIN_SUCCESS_URL: "",
        REGISTER_SUCCESS_URL: "",
        UI_THEME: "embed",
        UI_STYLE: true,
        THIRD_LOGIN_DISPLAY: true,
        DEFUALT_BACKGROUND: "",
        DEFUALT_UI: "login",
        LOGIN_BUTTON_TEXT: ["登录", "登录中..."],
        REGISTER_BUTTON_TEXT: ["注册", "注册中..."],
        PROXY_URL: "http://test.kankan.com/proxy.html",
        DOMAIN: "xunlei.com",
        DOMAIN_ALLOWED: ["xunlei.com", "kankan.com"],
        REPORT_SERVER: "http://stat.login.xunlei.com:1800/report",
        DEBUG: true,
        DEFAULT_ACCOUNT: "",
        THIRD_LOGIN_TARGET_PARENT: false,
        ALERT_ERROR: false,
        USE_CDN: false,
        CDN_PATH: ""
    };
    Util = function () {
        var binders = [], undef = void 0, self;
        self = {
            randString: function (length, max) {
                var random_string_chars = "abcdefghijklmnopqrstuvwxyz0123456789", len = random_string_chars.length;
                max = max ? Math.min(max, len) : len;
                var i, ret = [];
                for (i = 0; i < length; i++) {
                    ret.push(random_string_chars.charAt(Math.floor(Math.random() * max)))
                }
                return ret.join("")
            }, loadStyle: function (url, cb) {
                var styles = document.styleSheets, k;
                for (k in styles) {
                    if (styles[k].href && styles[k].href === url) {
                        if (typeof cb === "function") {
                            cb()
                        }
                        return
                    }
                }
                var style, h = document.getElementsByTagName("head")[0], done;
                style = document.createElement("link");
                style.rel = "stylesheet";
                style.type = "text/css";
                style.media = "all";
                var sid, complete = function (timeout) {
                    if (!done && (timeout === true || !this.readyState || this.readyState == "loaded" || this.readyState == "complete")) {
                        done = true;
                        clearTimeout(sid);
                        if (typeof cb === "function") {
                            cb()
                        }
                        style.onload = style.onreadystatechange = null
                    }
                };
                sid = setTimeout(function () {
                    complete(true)
                }, 100);
                style.onload = style.onreadystatechange = complete;
                style.href = url;
                h.appendChild(style);
                return style
            }, loadScript: function (url, cb) {
                var script = document.createElement("script"), done = false;
                script.src = url;
                script.type = "text/javascript";
                script.language = "javascript";
                script.onload = script.onreadystatechange = function () {
                    if (!done && (!this.readyState || this.readyState == "loaded" || this.readyState == "complete")) {
                        done = true;
                        if (typeof cb === "function") {
                            cb()
                        }
                        script.onload = script.onreadystatechange = null;
                        script.parentNode.removeChild(script)
                    }
                };
                document.getElementsByTagName("head")[0].appendChild(script);
                return script
            }, loadImage: function (url, timeout, cb) {
                var img = new Image, done, sid;
                img.onerror = function () {
                    img.onerror = img.onload = img.onreadystatechange = null;
                    clearTimeout(sid);
                    cb && cb(false)
                };
                img.onload = img.onreadystatechange = function () {
                    if (!done && (!this.readyState || this.readyState == "loaded" || this.readyState == "complete")) {
                        done = true;
                        img.onerror = img.onload = img.onreadystatechange = null;
                        clearTimeout(sid);
                        cb && cb(true)
                    }
                };
                sid = setTimeout(function () {
                    img.onerror = img.onload = img.onreadystatechange = null;
                    cb && cb(false)
                }, timeout);
                url += (url.indexOf("?") > 0 ? "&" : "?") + "_v_=" + self.randString(8);
                img.src = url
            }, loadScriptOn: function (url, condition, cb) {
                var rtn;
                if (typeof condition === "function") {
                    rtn = condition()
                } else {
                    rtn = !!condition
                }
                if (rtn) {
                    self.loadScript(url, cb)
                } else {
                    if (typeof cb === "function") {
                        cb()
                    }
                }
            }, isSessionid: function (key) {
                return key && key.length && (key.length === 128 || key.length === 160) ? true : false
            }, isJumpkey: function (key) {
                return key && key.length && key.length === 192 ? true : false
            }, getCookie: function (param, decode) {
                var c, cookie = document.cookie, t, i, l;
                decode = decode === undef ? true : decode;
                if (param) {
                    c = cookie.match(new RegExp("(^| )" + param + "=([^;]*)")) == null ? undef : RegExp.$2;
                    if (decode && c !== undef) {
                        try {
                            c = decodeURIComponent(c)
                        } catch (e) {
                            c = unescape(c)
                        }
                    }
                    return c ? c : ""
                } else {
                    var obj = {};
                    cookie = cookie.split("; ");
                    for (i = 0, l = cookie.length; i < l; ++i) {
                        t = cookie[i].split("=");
                        c = t.pop();
                        if (decode && c !== undef) {
                            try {
                                c = decodeURIComponent(c)
                            } catch (e) {
                                c = unescape(c)
                            }
                        }
                        obj[t.shift()] = c
                    }
                    return obj
                }
            }, setCookie: function (name, value, expire, domain, path, secure) {
                var cookie, expire = expire ? new Date((new Date).getTime() + expire).toGMTString() : false;
                cookie = name + "=" + escape(value);
                cookie += "; path=" + (path ? path : "/");
                if (domain) {
                    cookie += "; domain=" + domain
                }
                if (secure) {
                    cookie += "; secure"
                }
                if (expire) {
                    cookie += "; expires=" + expire
                }
                document.cookie = cookie
            }, delCookie: function (name, domain, path, secure) {
                self.setCookie(name, "", -6e4, domain, path, secure)
            }, bind: function (obj, type, func, scope) {
                if (typeof func !== "function") {
                    return
                }
                if (typeof obj === "string") {
                    obj = document.getElementById(obj)
                }
                if (!obj) {
                    throw new Error("bind on an undefined target")
                }
                function handler(e) {
                    e = e || window.event;
                    if (!e.target) {
                        e.target = e.srcElement;
                        e.preventDefault = function () {
                            this.returnValue = false
                        };
                        e.stopPropagation = function () {
                            this.cancelBubble = true
                        }
                    }
                    if (false === func.call(scope || this, e)) {
                        e.preventDefault();
                        e.stopPropagation()
                    }
                }

                var true_type = type.split(".").shift();
                binders.push({obj: obj, handler: handler, type: type});
                if (obj.attachEvent) {
                    obj.attachEvent("on" + true_type, handler)
                } else {
                    if (obj.addEventListener) {
                        obj.addEventListener(true_type, handler, false)
                    }
                }
            }, unbind: function (obj, type) {
                if (typeof obj === "string") {
                    obj = document.getElementById(obj)
                }
                if (!obj) {
                    throw new Error("unbind on an undefined target")
                }
                var binder, ts, t1, t2, i, ret;
                for (i = binders.length - 1; i >= 0; i--) {
                    binder = binders[i];
                    if (binder.obj !== obj) {
                        continue
                    }
                    ts = binder.type.split(".");
                    t1 = ts.shift();
                    t2 = ts.length > 0 ? ts.join(".") : false;
                    if (binder.type === type || type === t1 || t2 !== false && t2 === type) {
                        binders.splice(i, 1);
                        ret = binder;
                        if (obj.detachEvent) {
                            obj.detachEvent("on" + t1, binder.handler)
                        } else {
                            if (obj.removeEventListener) {
                                obj.removeEventListener(t1, binder.handler, false)
                            }
                        }
                    }
                }
                return ret
            }, $: function (str, quiet) {
                var dom = document.getElementById(str);
                if (!dom && !quiet) {
                    throw new Error("not find dom by id: " + str)
                }
                return dom
            }, id: function (str) {
                return document.getElementById(str)
            }, text: function (dom, msg) {
                if (!dom) {
                    return
                }
                var key = false;
                if (dom.innerText !== undef) {
                    key = "innerText"
                } else {
                    if (dom.textContent !== undef) {
                        key = "textContent"
                    } else {
                        if (dom.value !== undef) {
                            key = "value"
                        } else {
                            throw new Error("not support dom innerText or textContent")
                        }
                    }
                }
                return msg === undef ? dom[key] : dom[key] = msg
            }, getDomain: function (url) {
                var tmp = url.split("://");
                tmp.shift();
                var d = tmp.shift().split(/[\/\?|#]/).shift();
                return d
            }, checkDomain: function (domain) {
                var a, ext, rootDomain, i, flag = false;
                a = domain.split(".");
                ext = a.pop();
                rootDomain = (a.pop() + "." + ext).toLowerCase();
                for (i = CONFIG.DOMAIN_ALLOWED.length; i > 0; --i) {
                    if (rootDomain === CONFIG.DOMAIN_ALLOWED[i - 1]) {
                        flag = true;
                        break
                    }
                }
                return flag
            }, checkCss: function (url) {
                var ext, d;
                ext = url.split(".").pop();
                if (ext !== "css") {
                    return false
                }
                d = Util.getDomain(url);
                if (d === "static.webgame.kanimg.com") {
                    return true
                }
                if (!Util.checkDomain(d)) {
                    return false
                } else {
                    return true
                }
            }, checkMobile: function (m) {
                var p = /^(13|14|15|17|18)[\d]{9}$/;
                if (!p.exec(m)) {
                    return false
                } else {
                    return true
                }
            }, checkMail: function (m) {
                var p = /^[\w-]+(\.[\w-]+)*@[\w-]+(\.[\w-]+)+$/;
                if (!p.exec(m)) {
                    return false
                } else {
                    return true
                }
            }, trim: function (str) {
                return str.replace(/(^\s*)|(\s*$)/g, "")
            }, inArray: function (value, obj) {
                if (typeof obj != "object") {
                    return false
                }
                var k;
                for (k in obj) {
                    if (obj[k] === value) {
                        return true
                    }
                }
                return false
            }, loginRequest: function (method, url, data, cb, timeout) {
                if (!method || !url) {
                    throw new Error("loginRequest can't accept empty method and url as param")
                }
                var k, iframe, id, area, form, params = [], hash = "", sid;
                method = method.toUpperCase();
                form = document.createElement("form");
                form.style.display = "none";
                form.style.position = "absolute";
                form.method = method;
                form.enctype = "application/x-www-form-urlencoded";
                form.acceptCharset = "UTF-8";
                data = data || {};
                data.cachetime = (new Date).getTime();
                if (method === "_GET") {
                    for (k in data) {
                        params.push(k + "=" + data[k])
                    }
                    if (url.indexOf("#") > 0) {
                        hash = "#" + url.split("#").pop();
                        url = url.split("#").shift()
                    }
                    url += (url.indexOf("?") >= 0 ? "&" : "?") + params.join("&") + hash
                } else {
                    for (k in data) {
                        area = document.createElement("textarea");
                        area.name = k;
                        area.value = data[k];
                        form.appendChild(area)
                    }
                }
                document.body.appendChild(form);
                id = "f" + self.randString(8);
                form.target = id;
                try {
                    form.action = url
                } catch (e) {
                    form.setAttribute("action", url)
                }
                try {
                    iframe = document.createElement('<iframe name="' + id + '">')
                } catch (x) {
                    iframe = document.createElement("iframe");
                    iframe.name = id
                }
                iframe.id = id;
                iframe.style.display = "none";
                form.appendChild(iframe);
                function completed(e) {
                    if (!iframe.onerror) {
                        return
                    }
                    iframe.onreadystatechange = iframe.onerror = iframe.onload = null;
                    setTimeout(function () {
                        iframe = null;
                        form = null
                    }, 500);
                    sid && clearTimeout(sid);
                    e = typeof e === "string" ? e : undef;
                    typeof cb === "function" && cb(e)
                }

                if (timeout > 0) {
                    sid = setTimeout(function () {
                        completed("TIMEOUT")
                    }, timeout)
                }
                iframe.onerror = iframe.onload = completed;
                iframe.onreadystatechange = function (e) {
                    if (iframe.readyState == "complete") {
                        completed()
                    }
                };
                if (method === "_GET") {
                    iframe.src = url
                } else {
                    try {
                        form.submit()
                    } catch (e) {
                        var k, s = "";
                        for (k in e) {
                            s += k + ":" + e[k] + ";"
                        }
                        alert(s)
                    }
                }
            }, loginRequest2: function (method, url, data, cb, timeout) {
                if (!method || !url) {
                    throw new Error("loginRequest can't accept empty method and url as param")
                }
                var k, iframe, id, area, form, params = [], hash = "", sid;
                method = method.toUpperCase();
                form = document.createElement("form");
                form.style.display = "none";
                form.style.position = "absolute";
                form.method = method;
                form.enctype = "application/x-www-form-urlencoded";
                form.acceptCharset = "UTF-8";
                data = data || {};
                data.cachetime = (new Date).getTime();
                for (k in data) {
                    params.push(k + "=" + data[k])
                }
                if (url.indexOf("#") > 0) {
                    hash = "#" + url.split("#").pop();
                    url = url.split("#").shift()
                }
                url += (url.indexOf("?") >= 0 ? "&" : "?") + params.join("&") + hash;
                id = "f" + self.randString(8);
                form.target = id;
                try {
                    form.action = url
                } catch (e) {
                    form.setAttribute("action", url)
                }
                try {
                    iframe = document.createElement('<iframe name="' + id + '">')
                } catch (x) {
                    iframe = document.createElement("iframe");
                    iframe.name = id
                }
                iframe.id = id;
                iframe.style.display = "none";
                form.appendChild(iframe);
                function completed(e) {
                    if (!iframe.onerror) {
                        return
                    }
                    iframe.onreadystatechange = iframe.onerror = iframe.onload = null;
                    setTimeout(function () {
                        iframe = null;
                        form = null
                    }, 500);
                    sid && clearTimeout(sid);
                    e = typeof e === "string" ? e : undef;
                    if ("function" === typeof cb) {
                        cb(e)
                    }
                }

                if (timeout > 0) {
                    sid = setTimeout(function () {
                        completed("TIMEOUT")
                    }, timeout)
                }
                iframe.onerror = iframe.onload = completed;
                iframe.onreadystatechange = function (e) {
                    if (iframe.readyState == "complete") {
                        completed()
                    }
                };
                Util.getJson2(url, {}, completed)
            }, registerPost: function (url, data, callback) {
                var iframeUrl = "http://i.xunlei.com/login/2.5/post_callback.html", iframeId = "_submitIframe_" + Math.round(Math.random() * 1e3), form, input, k, iframe, domain = CONFIG.SET_ROOT_DOMAIN ? CONFIG.DOMAIN : "";
                if (CONFIG.ALL_HTTPS === true) {
                    iframeUrl = iframeUrl.replace("http", "https")
                }
                if (!callback) {
                    callback = function () {
                    }
                }
                var callbackName = "_" + Math.round(Math.random() * 1e16);
                if ("string" === typeof callback) {
                    window[callbackName] = window[callback]
                } else {
                    window[callbackName] = callback
                }
                form = document.createElement("form");
                form.id = "_postFrom_" + Math.round(Math.random() * 1e3);
                form.style.display = "none";
                form.style.position = "absolute";
                form.method = "post";
                form.action = url + "?domain=" + CONFIG.DOMAIN + "&iframeUrl=" + encodeURIComponent(iframeUrl) + "&callback=" + callbackName + "&csrf_token=" + Util.getCsrfToken();
                form.target = iframeId;
                form.enctype = "application/x-www-form-urlencoded";
                form.acceptCharset = "UTF-8";
                data["domain"] = CONFIG.DOMAIN;
                data["response"] = "iframe";
                for (k in data) {
                    input = document.createElement("input");
                    input.name = k;
                    input.value = data[k];
                    input.type = "hidden";
                    form.appendChild(input)
                }
                document.body.appendChild(form);
                try {
                    iframe = document.createElement('<iframe name="' + iframeId + '">')
                } catch (x) {
                    iframe = document.createElement("iframe");
                    iframe.name = iframeId
                }
                iframe.id = iframeId;
                iframe.style.display = "none";
                form.appendChild(iframe);
                document.body.appendChild(iframe);
                var removeHandlerName = "_remove" + callbackName;
                window[removeHandlerName] = function () {
                    iframe.parentNode.removeChild(iframe);
                    form.parentNode.removeChild(form);
                    iframe = null;
                    form = null;
                    window[callbackName] = null;
                    window[removeHandlerName] = null
                };
                form.submit()
            }, getJson: function (url, data, callback, callbackParamName) {
                var script = document.createElement("script"), head = document.getElementsByTagName("head")[0], callbackName = "jsonp" + (new Date).getTime(), params = [];
                if ("string" === typeof callback) {
                    window[callbackName] = window[callback]
                } else {
                    window[callbackName] = callback
                }
                callbackParamName = callbackParamName ? callbackParamName : "callback";
                for (k in data) {
                    params.push(k + "=" + data[k])
                }
                params.push(callbackParamName + "=" + callbackName);
                url += (url.indexOf("?") >= 0 ? "&" : "?") + params.join("&");
                eval(callbackName + " = function(json){callback(json)}");
                script.src = url;
                head.insertBefore(script, head.firstChild)
            }, getJson2: function (url, data, callback) {
                var img = new Image;
                var params = [];
                for (k in data) {
                    params.push(k + "=" + data[k])
                }
                url += (url.indexOf("?") >= 0 ? "&" : "?") + params.join("&");
                img.onload = callback;
                img.onerror = callback;
                img.src = url
            }, ajax: function (url, method, async, params, callback) {
                params = params || null;
                async = async || true;
                method = method || "post";
                function _getAjaxHttp() {
                    var xmlHttp;
                    try {
                        xmlHttp = new XMLHttpRequest
                    } catch (e) {
                        try {
                            xmlHttp = new ActiveXObject("Msxml2.XMLHTTP")
                        } catch (e) {
                            xmlHttp = new ActiveXObject("Microsoft.XMLHTTP")
                        }
                    }
                    return xmlHttp
                }

                var xmlhttp = _getAjaxHttp();
                xmlhttp.onreadystatechange = function () {
                    if (xmlhttp.readyState == 4) {
                        if (xmlhttp.status == 200) {
                            var data = decodeURI(xmlhttp.responseText);
                            callback && callback(data)
                        }
                    }
                };
                xmlhttp.open(method, url, async);
                xmlhttp.send(params)
            }, errlog: function (msg) {
                if (!CONFIG.DEBUG) {
                    return
                }
                if (typeof console == undef) {
                } else {
                    console.log(msg)
                }
            }, getConfig: function (param) {
                param = param.toUpperCase();
                if (param in CONFIG) {
                    return CONFIG[param]
                }
            }, getCsrfToken: function () {
                var csrf_token = md5(self.getCookie("deviceid").slice(0, 32));
                return csrf_token
            }, appendUri: function (url, data) {
                var key, appendParams = [];
                for (key in data) {
                    appendParams.push(key + "=" + data[key])
                }
                appendParams = appendParams.join("&");
                url = url.indexOf("?") === -1 ? url + "?" + appendParams : url + "&" + appendParams;
                return url
            }
        };
        return self
    }();
    Report = function () {
        var data = [];
        return {
            add: function (params) {
                var k, domain = Util.getDomain(document.referrer), defs;
                defs = {
                    url: "",
                    errorcode: "",
                    responsetime: "",
                    retrynum: "0",
                    serverip: "",
                    cmdid: "",
                    domain: domain,
                    b_type: CONFIG.LOGIN_ID,
                    platform: "1",
                    clientversion: ""
                };
                for (k in defs) {
                    if (params[k] !== undef) {
                        defs[k] = params[k]
                    }
                }
                data.push(defs);
                if (data.length >= 3) {
                    this.exec()
                }
            }, exec: function (cb) {
                var i, k, d, l = data.length, uri = [], url;
                if (l === 0) {
                    return true
                }
                uri.push("cnt=" + l);
                for (i = 0; i < l; ++i) {
                    d = data[i];
                    for (k in d) {
                        if (d.hasOwnProperty(k)) {
                            uri.push(k + i + "=" + encodeURIComponent(d[k]))
                        }
                    }
                }
                data = [];
                url = CONFIG.REPORT_SERVER + "?" + uri.join("&");
                if (CONFIG.ALL_HTTPS === true) {
                    url = url.replace("http://stat.login.xunlei.com:1800", "https://ssl-in-one.xunlei.com/ssl_report")
                }
                if (typeof cb === "function") {
                    Util.loginRequest("GET", url, {}, cb, 1e3)
                } else {
                    (new Image).src = url
                }
                return true
            }
        }
    }();
    Report2 = function () {
        return {
            exec: function (params) {
                var i, k, domain = Util.getDomain(document.referrer), defs, uri = [], url;
                defs = {
                    regtype: "",
                    errorcode: "",
                    responsetime: "",
                    domain: domain,
                    bustype: CONFIG.REGISTER_ID,
                    platform: "1",
                    clientversion: ""
                };
                uri.push("op=regStat");
                uri.push("response=json");
                for (k in defs) {
                    if (params[k] !== undef) {
                        defs[k] = params[k]
                    }
                }
                for (k in defs) {
                    if (defs.hasOwnProperty(k)) {
                        uri.push(k + "=" + encodeURIComponent(defs[k]))
                    }
                }
                url = "https://zhuce.xunlei.com/regapi/?" + uri.join("&");
                (new Image).src = url;
                return true
            }
        }
    }();
    request = function () {
        var DOMAIN, SERVER_LOGIN = ["https://login", "https://login2", "https://login3"], SERVER_CAPTCHA = ["http://verify1", "http://verify2", "http://verify3"], SERVER_LOGIN_STATUS = [1, 1, 1], SERVER_CAPTCHA_STATUS = [1, 1, 1], SERVER_REGISTER = "https://zhuce.xunlei.com/regapi/";

        function getServer(type, path) {
            if (CONFIG.ALL_HTTPS === true) {
                SERVER_CAPTCHA = ["https://ssl-in-one.xunlei.com/ssl_verify1", "https://ssl-in-one.xunlei.com/ssl_verify2", "https://ssl-in-one.xunlei.com/ssl_verify3"]
            }
            var servers = type === "captcha" ? SERVER_CAPTCHA : SERVER_LOGIN, status, index, len = servers.length, count = 0, flag, tmp;
            tmp = Util.getCookie("_s." + type + "_");
            if (tmp && (tmp = tmp.split(",")) && tmp.length === len) {
                status = tmp;
                type === "captcha" ? SERVER_CAPTCHA_STATUS = status : SERVER_LOGIN_STATUS = status
            } else {
                status = type === "captcha" ? SERVER_CAPTCHA_STATUS : SERVER_LOGIN_STATUS
            }
            index = count;
            while (count++ < len) {
                if (status[index] - 1 === 0) {
                    flag = true;
                    break
                } else {
                    index = count
                }
            }
            if (!flag) {
                index = (new Date).getTime() % len
            }
            if (CONFIG.ALL_HTTPS === true && type === "captcha") {
                return servers[index] + (path ? path : "")
            }
            return servers[index] + DOMAIN + (path ? path : "")
        }

        function setServer(server, value, type) {
            if (CONFIG.ALL_HTTPS === true) {
                SERVER_CAPTCHA = ["https://ssl-in-one.xunlei.com/ssl_verify1", "https://ssl-in-one.xunlei.com/ssl_verify2", "https://ssl-in-one.xunlei.com/ssl_verify3"]
            }
            var i, flag, servers = type === "captcha" ? SERVER_CAPTCHA : SERVER_LOGIN, status = type === "captcha" ? SERVER_CAPTCHA_STATUS : SERVER_LOGIN_STATUS;
            if (CONFIG.ALL_HTTPS === true && type === "captcha") {
                server = server.substring(0, 41)
            } else {
                server = server.substring(0, server.indexOf(DOMAIN))
            }
            for (i = servers.length - 1; i >= 0; --i) {
                if (server === servers[i]) {
                    status[i] = value ? 1 : 0;
                    Util.setCookie("_s." + type + "_", status.join(","));
                    flag = true;
                    break
                }
            }
            if (!flag) {
                throw new Error("not find your server: " + server)
            }
        }

        function _request_login_helper(method, path, params, cb, timeout, retry, retry_code, cookie) {
            var rtn, url;
            cookie = cookie || "blogresult";
            Util.delCookie(cookie, DOMAIN);
            url = getServer("login", path);
            var startMsTime = (new Date).getTime();
            params.v = 101;
            if (method.toUpperCase() == "POST") {
                url = Util.appendUri(url, {csrf_token: Util.getCsrfToken()})
            } else if (method.toUpperCase() == "GET") {
                params.csrf_token = Util.getCsrfToken()
            }
            Util.loginRequest(method, url, params, function (msg) {
                rtn = Util.getCookie(cookie);
                if (msg === "TIMEOUT" || rtn === undef) {
                    rtn = -1
                } else {
                    if (cookie !== "check_result") {
                        rtn = parseInt(rtn, 10);
                        if (rtn !== rtn) {
                            rtn = -1
                        }
                    }
                }
                if (retry && retry === true) {
                    retry = 1
                }
                Report.add({
                    url: url,
                    errorcode: typeof rtn === "string" && rtn.indexOf(":") > 0 ? rtn.split(":").shift() : rtn,
                    responsetime: ((new Date).getTime() - startMsTime) / 1e3,
                    retrynum: typeof retry === "number" ? retry - 1 : 0
                });
                if (retry && SERVER_LOGIN.length > retry && (retry_code && retry_code.indexOf("," + rtn + ",") >= 0 || rtn === -1)) {
                    setServer(url, 0, "login");
                    _request_login_helper(method, path, params, cb, timeout, retry + 1, retry_code, cookie)
                } else {
                    if (rtn === 0 || cookie === "check_result" && rtn !== -1) {
                        setServer(url, 1, "login")
                    } else {
                        setServer(url, 0, "login")
                    }
                    Util.delCookie(cookie, DOMAIN);
                    cb(rtn)
                }
            }, timeout)
        }

        function _request_login_helper2(method, path, params, cb, timeout, retry, retry_code, cookie) {
            var rtn, url;
            cookie = cookie || "blogresult";
            Util.delCookie(cookie, DOMAIN);
            url = getServer("login", path);
            var startMsTime = (new Date).getTime();
            params.v = 101;
            if (method.toUpperCase() == "POST") {
                url = Util.appendUri(url, {csrf_token: Util.getCsrfToken()})
            } else if (method.toUpperCase() == "GET") {
                params.csrf_token = Util.getCsrfToken()
            }
            Util.loginRequest2(method, url, params, function (msg) {
                rtn = Util.getCookie(cookie);
                if (msg === "TIMEOUT" || rtn === undef) {
                    rtn = -1
                } else {
                    if (cookie !== "check_result") {
                        rtn = parseInt(rtn, 10);
                        if (rtn !== rtn) {
                            rtn = -1
                        }
                    }
                }
                if (retry && retry === true) {
                    retry = 1
                }
                Report.add({
                    url: url,
                    errorcode: typeof rtn === "string" && rtn.indexOf(":") > 0 ? rtn.split(":").shift() : rtn,
                    responsetime: ((new Date).getTime() - startMsTime) / 1e3,
                    retrynum: typeof retry === "number" ? retry - 1 : 0
                });
                if (retry && SERVER_LOGIN.length > retry && (retry_code && retry_code.indexOf("," + rtn + ",") >= 0 || rtn === -1)) {
                    setServer(url, 0, "login");
                    _request_login_helper2(method, path, params, cb, timeout, retry + 1, retry_code, cookie)
                } else {
                    if (rtn === 0 || cookie === "check_result" && rtn !== -1) {
                        setServer(url, 1, "login")
                    } else {
                        setServer(url, 0, "login")
                    }
                    Util.delCookie(cookie, DOMAIN);
                    cb(rtn)
                }
            }, timeout)
        }

        function _request_register_helper(url, params, callback) {
            var startMsTime = (new Date).getTime();
            Util.registerPost(url, params, function (json) {
                Report2.exec({
                    regtype: params.regtype ? params.regtype : params.op,
                    errorcode: json.result,
                    responsetime: ((new Date).getTime() - startMsTime) / 1e3
                });
                callback(json)
            })
        }

        function _account_locked(username, callback) {
            var url = "https://zhuce.xunlei.com/regapi/?op=canUnseal", params = {
                subUserName: username,
                response: "jsonp"
            }, code = 6, msg = "账号不存在";
            Util.getJson(url, params, function (data) {
                if (data.result == 200) {
                    if (username.match(/:\d{1}$/g)) {
                        msg = "帐号锁定，请用主账号登录个人中心查看"
                    } else {
                        msg = "因异常分享被锁定，请到安全中心解封"
                    }
                }
                callback && callback(code, msg)
            })
        }

        function request(action, data, callback) {
            var url, params = {}, method, timeout = CONFIG.SERVER_TIMEOUT, retry = CONFIG.RETRY_LOGIN_ON_SERVER_ERROR;
            DOMAIN = "." + CONFIG.DOMAIN;
            if (typeof data === "function") {
                callback = data
            }
            switch (action) {
                case"login":
                    if (data.username && data.password && data.captcha) {
                        params.p = Util.trim(data.password);
                        params.u = Util.trim(data.username);
                        params.verifycode = data.captcha;
                        params.login_enable = data.autologin ? 1 : 0;
                        params.business_type = CONFIG.LOGIN_ID
                    } else {
                        throw new Error("post argument error")
                    }
                    method = "post";
                    path = "/sec2login/";
                    _request_login_helper(method, path, params, function (code) {
                        var msg;
                        switch (code) {
                            case-1:
                                code = 1;
                                msg = "连接超时，请重试";
                                break;
                            case 0:
                                msg = "登录成功";
                                break;
                            case 1:
                            case 9:
                            case 10:
                            case 11:
                                code = 2;
                                msg = "验证码错误，请重新输入验证码";
                                break;
                            case 2:
                            case 4:
                                code = 3;
                                msg = "帐号或密码错误，请重新输入";
                                break;
                            case 3:
                            case 7:
                            case 8:
                            case 16:
                                code = 4;
                                msg = "服务器内部错误，请重试";
                                break;
                            case 12:
                            case 13:
                            case 14:
                            case 15:
                                code = 5;
                                msg = "登录页面失效";
                                break;
                            case 22:
                                code = 7;
                                msg = "登录环境异常，请于2小时后重试";
                                break;
                            case 6:
                                _account_locked(params.u, callback);
                                break;
                            default:
                                code = -1;
                                msg = "内部错误，请重试";
                                break
                        }
                        if (code !== 6) {
                            callback && callback(code, msg)
                        }
                    }, timeout, true, ",33333,");
                    break;
                case"sessionlogin":
                    if (data.sessionid && Util.isSessionid(data.sessionid)) {
                        params.sessionid = data.sessionid
                    } else {
                        throw new Error("post argument error")
                    }
                    method = "post";
                    path = "/sessionid/";
                    _request_login_helper(method, path, params, function (code) {
                        var msg;
                        switch (code) {
                            case-1:
                                msg = "连接超时，请重试";
                                break;
                            case 0:
                                msg = "登录成功";
                                break;
                            case 1:
                                msg = "sessionid 失效，请重新登录";
                            case 2:
                                msg = "sessionid 无效，请重新登录";
                            case 5:
                                msg = "无效帐号，请换帐号登录";
                                break;
                            case 6:
                                msg = "帐号被锁定，请换帐号登录";
                                break;
                            default:
                                msg = "内部错误，请重试";
                                break
                        }
                        callback && callback(code, msg)
                    }, timeout, retry, ",3,7,");
                    break;
                case"loginkeylogin":
                    params.loginkey = data.loginkey;
                    params.userid = data.userid;
                    params.business_type = CONFIG.LOGIN_ID;
                    method = "post";
                    path = "/loginkeylogin/";
                    _request_login_helper(method, path, params, function (code) {
                        var msg;
                        callback && callback(code, msg)
                    }, timeout, retry, ",333333,");
                    break;
                case"jumplogin":
                    if (data.jumpkey && Util.isJumpkey(data.jumpkey)) {
                        params.jumpkey = data.jumpkey
                    } else {
                        throw new Error("post argument error")
                    }
                    method = "post";
                    path = "/jumplogin/";
                    _request_login_helper(method, path, params, function (code) {
                        var msg;
                        switch (code) {
                            case-1:
                                code = 1;
                                msg = "连接超时，请重试";
                                break;
                            case 0:
                                msg = "登录成功";
                                break;
                            case 1:
                                code = 9;
                                msg = "jumpkey 失效，请重新登录";
                            case 2:
                                code = 8;
                                msg = "jumpkey 无效，请重新登录";
                            case 5:
                                code = 7;
                                msg = "无效帐号，请换帐号登录";
                                break;
                            case 6:
                                msg = "帐号被锁定，请换帐号登录";
                                break;
                            default:
                                code = -1;
                                msg = "内部错误，请重试";
                                break
                        }
                        callback && callback(code, msg)
                    }, timeout, retry, ",3,7,");
                    break;
                case"checkuser":
                    if (data.username) {
                        params.u = Util.trim(data.username);
                        params.business_type = CONFIG.LOGIN_ID
                    } else {
                        throw new Error("post argument error")
                    }
                    method = "get";
                    path = "/check/";
                    _request_login_helper2(method, path, params, function (rtn) {
                        var ps = (rtn + "").split(":"), code;
                        code = parseInt(ps.shift());
                        if (code !== code) {
                            code = -1
                        }
                        callback && callback(code, ps.pop())
                    }, timeout, retry, false, "check_result");
                    break;
                case"logout":
                    (new Image).src = getServer("login", "/unregister/?sessionid=" + Util.getCookie("sessionid"));
                    var xlCookies = "VERIFY_KEY,blogresult,active,isspwd,score,downbyte,isvip,jumpkey,logintype,nickname,onlinetime,order,safe,downfile,sessionid,sex,upgrade,userid,usernewno,usernick,usertype,usrname,loginkey,xl_autologin", i, cookies = xlCookies.split(",");
                    for (i = cookies.length; i > 0; --i) {
                        Util.delCookie(cookies[i - 1], CONFIG.DOMAIN)
                    }
                    Util.delCookie(CONFIG.LOGIN_TYPE_COOKIE_NAME);
                    callback && callback();
                    break;
                case"captcha":
                    if (!data.img) {
                        throw new Error("post argument error")
                    }
                    var sid, img = data.img, t = data.t ? data.t : Util.getCookie("verify_type");
                    var src = getServer("captcha", "/image?t=" + t + "&cachetime=" + (new Date).getTime()), done = false;
                    img.onerror = function () {
                        img.onerror = img.onload = img.onreadystatechange = null;
                        clearTimeout(sid);
                        setServer(src, 0, "captcha");
                        if (callback) {
                            callback(1, "获取验证码失败，请手动刷新")
                        }
                    };
                    img.onload = img.onreadystatechange = function () {
                        if (!done && (!this.readyState || this.readyState == "loaded" || this.readyState == "complete")) {
                            done = true;
                            clearTimeout(sid);
                            setServer(src, 1, "captcha");
                            img.onerror = img.onload = img.onreadystatechange = null;
                            if (callback) {
                                callback(0, "刷新成功")
                            }
                        }
                    };
                    sid = setTimeout(function () {
                        img.onerror = img.onload = img.onreadystatechange = null;
                        setServer(src, 0, "captcha");
                        if (callback) {
                            callback(1, "获取验证码失败，请手动刷新")
                        }
                    }, timeout);
                    img.src = src;
                    break;
                case"mobilelogin":
                    if (data.mobile && data.code) {
                        params.op = "mobileReg";
                        params.from = CONFIG.REGISTER_ID;
                        params.mobile = data.mobile;
                        params.code = data.code;
                        params.regtype = "mobileLogin"
                    } else {
                        throw new Error("post argument error")
                    }
                    url = SERVER_REGISTER;
                    _request_register_helper(url, params, function (json) {
                        callback && callback(json)
                    });
                    break;
                case"getsmscode":
                    if (data.mobile && data.type) {
                        params.op = "sendSms";
                        params.from = CONFIG.REGISTER_ID;
                        params.mobile = data.mobile;
                        params.verifyCode = data.verifyCode;
                        params.verifyKey = Util.getCookie("VERIFY_KEY");
                        params.verifyType = "MEA";
                        params.v = 2;
                        params.type = data.type == "register" ? 1 : 2
                    } else {
                        throw new Error("post argument error")
                    }
                    url = SERVER_REGISTER;
                    Util.registerPost(url, params, function (json) {
                        callback && callback(json)
                    });
                    break;
                case"checkbind":
                    if (data.account && data.type) {
                        params.op = "checkBind";
                        params.from = CONFIG.REGISTER_ID;
                        params.response = "jsonp";
                        params.account = data.account;
                        params.type = data.type === "mail" ? 4 : 1
                    } else {
                        throw new Error("post argument error")
                    }
                    url = SERVER_REGISTER;
                    Util.getJson(url, params, function (json) {
                        callback && callback(json)
                    });
                    break;
                case"mobileregister":
                    if (data.mobile && data.code) {
                        params.op = "mobileReg";
                        params.from = CONFIG.REGISTER_ID;
                        params.mobile = data.mobile;
                        params.code = data.code
                    } else {
                        throw new Error("post argument error")
                    }
                    url = SERVER_REGISTER;
                    _request_register_helper(url, params, function (json) {
                        callback && callback(json)
                    });
                    break;
                case"mobileregisterpwd":
                    if (data.mobile && data.code && data.password) {
                        params.op = "mobileRegPwd";
                        params.from = CONFIG.REGISTER_ID;
                        params.mobile = data.mobile;
                        params.code = data.code;
                        params.pwd = data.password
                    } else {
                        throw new Error("post argument error")
                    }
                    url = SERVER_REGISTER;
                    _request_register_helper(url, params, function (json) {
                        callback && callback(json)
                    });
                    break;
                case"setpassword":
                    if (data.password) {
                        params.op = "changePassword";
                        params.from = CONFIG.REGISTER_ID;
                        params.pwd = data.password
                    } else {
                        throw new Error("post argument error")
                    }
                    url = SERVER_REGISTER;
                    Util.registerPost(url, params, function (json) {
                        callback && callback(json)
                    });
                    break;
                case"mailregister":
                    if (data.mail && data.password) {
                        params.op = "emailReg";
                        params.from = CONFIG.REGISTER_ID;
                        params.email = data.mail;
                        params.pwd = data.password;
                        if (data.code) {
                            params.code = data.code
                        }
                    } else {
                        throw new Error("post argument error")
                    }
                    url = SERVER_REGISTER;
                    _request_register_helper(url, params, function (json) {
                        callback && callback(json)
                    });
                    break;
                case"accountregister":
                    if (data.account && data.password) {
                        params.op = "usernameReg";
                        params.from = CONFIG.REGISTER_ID;
                        params.username = data.account;
                        params.pwd = data.password;
                        if (data.code) {
                            params.code = data.code
                        }
                    } else {
                        throw new Error("post argument error")
                    }
                    url = SERVER_REGISTER;
                    _request_register_helper(url, params, function (json) {
                        callback && callback(json)
                    });
                    break;
                case"isNeedValidate":
                    params.op = "needValidate";
                    params.from = CONFIG.REGISTER_ID;
                    params.response = "jsonp";
                    url = SERVER_REGISTER;
                    Util.getJson(url, params, function (json) {
                        callback && callback(json)
                    });
                    break;
                case"registerCaptcha":
                    if (!data.img) {
                        throw new Error("post argument error")
                    }
                    var img = data.img, src = SERVER_REGISTER + "?op=validateImg&from=" + CONFIG.REGISTER_ID + "&size=M&chachtime=" + (new Date).getTime(), done = false;
                    img.onerror = function () {
                        img.onerror = img.onload = img.onreadystatechange = null;
                        clearTimeout(sid);
                        if (callback) {
                            callback(1, "获取验证码失败，请手动刷新")
                        }
                    };
                    img.onload = img.onreadystatechange = function () {
                        if (!done && (!this.readyState || this.readyState == "loaded" || this.readyState == "complete")) {
                            done = true;
                            img.onerror = img.onload = img.onreadystatechange = null;
                            if (callback) {
                                callback(0, "刷新成功")
                            }
                        }
                    };
                    img.src = src;
                    break;
                case"checkRegisterCaptcha":
                    var code = data.code;
                    var url = SERVER_REGISTER + "?op=CheckVerifyCode";
                    var params = [];
                    params.code = code;
                    params.key = Util.getCookie("VERIFY_KEY");
                    params.type = "MEA";
                    params.response = "jsonp";
                    Util.getJson(url, params, function (json) {
                        callback && callback(json)
                    });
                    break;
                default:
                    throw new Error("not support action: " + action)
            }
        }

        return request
    }();
    UIManager = function () {
        var inited, loading, UI, UIS = {}, undef = void 0, self, is_requesting = false, container, initType, al_submit, ml_submit, mr_submit, pr_submit, pr_finish, ar_submit, al_doms, ml_doms, mr_doms, pr_doms, tl_doms, ar_doms, al_inited, ml_inited, mr_inited, pr_inited, tl_inited, ar_inited;
        var loginType, LOGIN_TYPE = {
            ORIGIN: -1,
            INITED: 0,
            ACCOUNT: 1,
            CLIENT: 2,
            AUTOWEB: 3,
            AUTOCLIENT: 4,
            THIRD: 5,
            MOBILE: 6
        };
        xlQuickLogin.TYPE = LOGIN_TYPE;
        function setAutoLogin() {
            var t = CONFIG.AUTO_LOGIN_EXPIRE_TIME;
            if (t && typeof t === "number" && t > 0) {
                if (store.enabled) {
                    store.set("xl_autologin", 1)
                } else {
                    Util.setCookie("xl_autologin", 1, t * 1e3, CONFIG.DOMAIN)
                }
            }
        }

        function delAutoLogin() {
            if (store.enabled) {
                store.remove("xl_autologin")
            } else {
                Util.delCookie("xl_autologin")
            }
        }

        function loginSuccess(lt) {
            loginType = lt;
            Util.delCookie("_s.login_");
            Util.delCookie("_s.captcha_");
            Util.setCookie(CONFIG.LOGIN_TYPE_COOKIE_NAME, lt + "_" + "1", 0, CONFIG.DOMAIN);
            if (store.enabled && Util.getCookie(CONFIG.LOGIN_KEY_NAME)) {
                store.set(CONFIG.LOGIN_KEY_NAME, Util.getCookie(CONFIG.LOGIN_KEY_NAME));
                Util.delCookie(CONFIG.LOGIN_KEY_NAME, CONFIG.DOMAIN)
            }
            var input = document.getElementsByTagName("input");
            for (var i = 0; i < input.length; i++) {
            }
            Report.exec(function () {
                try {
                    parent.xlQuickLogin.loginExtFun()
                } catch (err) {
                    Util.errlog(err);
                    if (CONFIG.SET_ROOT_DOMAIN) {
                        parent.xlQuickLogin.loginFunc()
                    } else {
                        window.parent.location.href = CONFIG.LOGIN_SUCCESS_URL
                    }
                }
                clearTimeout(countTime)
            });
            var countTime = setTimeout(function () {
                try {
                    parent.xlQuickLogin.loginExtFun()
                } catch (err) {
                    Util.errlog(err);
                    if (CONFIG.SET_ROOT_DOMAIN) {
                        parent.xlQuickLogin.loginFunc()
                    } else {
                        window.parent.location.href = CONFIG.LOGIN_SUCCESS_URL
                    }
                }
            }, 1e3)
        }

        function registerSuccess() {
            if (CONFIG.SET_ROOT_DOMAIN) {
                parent.xlQuickLogin.registerFunc()
            } else {
                window.parent.location.href = CONFIG.REGISTER_SUCCESS_URL
            }
        }

        function load(cb) {
            var theme = CONFIG.UI_THEME, min = CONFIG.DEBUG ? "" : ".min", loadjs, dir = "http://i.xunlei.com/login/theme/" + theme + "/";
            if (loading) {
                return
            }
            if (CONFIG.USE_CDN) {
                dir = CONFIG.CDN_PATH + "login/theme/" + theme + "/"
            }
            loading = true;
            UI = UIS[theme];
            if (CONFIG.ALL_HTTPS === true) {
                dir = dir.replace("http", "https")
            }
            loadjs = function () {
                var s = typeof UI === "string" ? UI : dir + "style" + min + ".js";
                Util.loadScriptOn(s + "?_t=1488265963741", !UI || typeof UI === "string", function () {
                    loading = false;
                    UI = UIS[theme];
                    if (!UI) {
                        throw new Error("load style error")
                    }
                    container = UI.getContainer && UI.getContainer(CONFIG.USE_CDN, CONFIG.CDN_PATH);
                    if (CONFIG.REGISTER_ID === "" || UI.supportRegister === false) {
                        CONFIG.REGISTER_TYPES = ""
                    }
                    UI.init && UI.init(CONFIG.LOGIN_TYPES, CONFIG.REGISTER_TYPES, CONFIG.DEFUALT_UI);
                    var texts = {
                        login_button_text: CONFIG.LOGIN_BUTTON_TEXT[0],
                        register_button_text: CONFIG.REGISTER_BUTTON_TEXT[0]
                    };
                    UI.setText && UI.setText(texts);
                    if (!CONFIG.AUTO_LOGIN_EXPIRE_TIME) {
                        UI.hideRememberCheckbox()
                    }
                    cb()
                })
            };
            if (CONFIG.UI_STYLE) {
                if (typeof CONFIG.UI_STYLE === "string") {
                    if (!Util.checkCss(CONFIG.UI_STYLE)) {
                        throw new Error("传入的css有误")
                    } else {
                        Util.loadStyle(CONFIG.UI_STYLE, loadjs)
                    }
                } else {
                    Util.loadStyle(dir + "css/style.css?_t=1488265963741", loadjs)
                }
            } else {
                loadjs()
            }
        }

        function initThirdLogin() {
            if (tl_inited) {
                return
            }
            tl_inited = true;
            UI.initThirdLogin(CONFIG.LOGIN_SUCCESS_URL, CONFIG.ALL_HTTPS, CONFIG.USE_CDN, CONFIG.CDN_PATH)
        }

        function initMobileLoginForm() {
            if (ml_inited) {
                return
            }
            if (!document.getElementsByClassName) {
                document.getElementsByClassName = function (className, element) {
                    var children = (element || document).getElementsByTagName("*");
                    var elements = new Array;
                    for (var i = 0; i < children.length; i++) {
                        var child = children[i];
                        var classNames = child.className.split(" ");
                        for (var j = 0; j < classNames.length; j++) {
                            if (classNames[j] == className) {
                                elements.push(child);
                                break
                            }
                        }
                    }
                    return elements
                }
            }
            ml_inited = true;
            ml_doms = UI.mobileLoginDoms();
            Util.bind(ml_doms.input_mobile, "blur.x", _check_user);
            Util.bind(ml_doms.button_getcode, "click.x", _showCaptch);
            Util.bind(ml_doms.captcha_confirm, "click.x", _checkCaptch);
            Util.bind(ml_doms.login_form, "keydown.x", _formKeyDown);
            function _formKeyDown(e) {
                if (ml_doms.captcha_container[0].style.display !== "none") {
                    var keynum;
                    if (window.event) {
                        keynum = e.keyCode
                    } else {
                        if (e.which) {
                            keynum = e.which
                        }
                    }
                    if (keynum == 13) {
                        _checkCaptch();
                        return false
                    }
                }
            }

            function _showCaptch() {
                if (typeof ml_doms.button_getcode != "undefined" && UI.isSmsButtonGrey(ml_doms.button_getcode)) {
                    return
                }
                UI.hideError(ml_doms.warn);
                var m = Util.trim(ml_doms.input_mobile.value);
                if (!m) {
                    UI.showError("手机号不能为空", ml_doms.warn, ml_doms.input_mobile);
                    return
                }
                if (!Util.checkMobile(m)) {
                    UI.showError("手机格式不正确", ml_doms.warn, ml_doms.input_mobile);
                    return
                }
                for (var i = 0; i < document.getElementsByClassName("pl_hide_for_sms_captcha").length; ++i) {
                    document.getElementsByClassName("pl_hide_for_sms_captcha")[i].style.display = "none"
                }
                _display(ml_doms.captcha_container, "");
                _fresh_captch();
                return false
            }

            function _checkCaptch() {
                var code = ml_doms.input_captcha.value;
                if (Util.trim(code) == "") {
                    UI.showError("验证码不能为空", ml_doms.warn, ml_doms.input_captcha);
                    return
                }
                var url = "https://zhuce.xunlei.com/regapi/?op=CheckVerifyCode";
                var params = [];
                params.code = code;
                params.key = Util.getCookie("VERIFY_KEY");
                params.type = "MEA";
                params.response = "jsonp";
                Util.getJson(url, params, function (json) {
                    if (json.result == 200) {
                        if (json.data == 200) {
                            _display(ml_doms.captcha_container, "none");
                            for (var i = 0; i < document.getElementsByClassName("pl_hide_for_sms_captcha").length; ++i) {
                                document.getElementsByClassName("pl_hide_for_sms_captcha")[i].style.display = ""
                            }
                            _get_code()
                        } else {
                            UI.showError("验证码错误", ml_doms.warn, ml_doms.input_captcha);
                            _fresh_captch();
                            return
                        }
                    } else {
                        if (json.result == 1206) {
                            UI.showError("网络异常,请稍后重试", ml_doms.warn, ml_doms.input_captcha)
                        } else {
                            UI.showError("网络异常,请稍后重试", ml_doms.warn, ml_doms.input_captcha)
                        }
                    }
                    return false
                });
                return false
            }

            for (var i = ml_doms.captcha_fresh.length - 1; i >= 0; --i) {
                Util.bind(ml_doms.captcha_fresh[i], "click.x", _fresh_captch)
            }
            function _fresh_captch() {
                ml_doms.input_captcha.value = "";
                request("captcha", {img: ml_doms.captcha_img, t: "MEA"}, function (code, msg) {
                    if (code !== 0) {
                        UI.showError(msg, ml_doms.warn)
                    }
                });
                return false
            }

            function _check_user() {
                var m = Util.trim(ml_doms.input_mobile.value);
                if (m && !Util.checkMobile(m)) {
                    UI.showError("手机格式不正确", ml_doms.warn, ml_doms.input_mobile)
                }
            }

            function _get_code() {
                if (UI.isSmsButtonGrey(ml_doms.button_getcode)) {
                    return
                }
                UI.hideError(ml_doms.warn);
                var m = Util.trim(ml_doms.input_mobile.value);
                if (!m) {
                    UI.showError("手机号不能为空", ml_doms.warn, ml_doms.input_mobile);
                    return
                }
                if (!Util.checkMobile(m)) {
                    UI.showError("手机格式不正确", ml_doms.warn, ml_doms.input_mobile);
                    return
                }
                var code = Util.trim(ml_doms.input_captcha.value);
                if (!code) {
                    UI.showError("验证码不能为空", ml_doms.warn, ml_doms.input_captcha);
                    return
                }
                request("getsmscode", {mobile: m, verifyCode: code, type: "login"}, function (json) {
                    if (json.result == 200) {
                        UI.setSmsButtonGrey(ml_doms.button_getcode)
                    } else {
                        if (json.result == 600) {
                            UI.showError("验证码错误，请重新输入", ml_doms.warn)
                        } else {
                            if (json.result == 401) {
                                UI.showError("您的手机发送短信次数过多，请明天再试", ml_doms.warn)
                            } else {
                                UI.showError("服务器内部错误，请重试", ml_doms.warn)
                            }
                        }
                    }
                })
            }

            function _display(objs, str) {
                if (!objs.pop) {
                    objs = [objs]
                }
                for (var i = objs.length; i > 0; --i) {
                    objs[i - 1].style.display = str
                }
            }

            ml_submit = function () {
                if (is_requesting) {
                    return
                }
                UI.hideError(ml_doms.warn);
                var m = Util.trim(ml_doms.input_mobile.value), c = Util.trim(ml_doms.input_code.value), allowAutoLogin = ml_doms.check_remember.checked == true ? true : false;
                if (!m) {
                    UI.showError("手机号不能为空", ml_doms.warn, ml_doms.input_mobile);
                    return
                }
                if (!Util.checkMobile(m)) {
                    UI.showError("手机格式不正确", ml_doms.warn, ml_doms.input_mobile);
                    return
                }
                if (!c) {
                    UI.showError("短信验证码不能为空", ml_doms.warn, ml_doms.input_code);
                    return
                }
                if (c.length < 4 || c.length > 6) {
                    ml_doms.input_code.value = "";
                    UI.showError("短信验证码错误", ml_doms.warn, ml_doms.input_code);
                    return
                }
                is_requesting = true;
                Util.text(ml_doms.button_submit, CONFIG.LOGIN_BUTTON_TEXT[1]);
                request("mobilelogin", {mobile: m, code: c}, function (json) {
                    if ((json.result == 200 || json.result == 201) && json.sessionid) {
                        request("sessionlogin", {sessionid: json.sessionid}, function (code, msg) {
                            is_requesting = false;
                            Util.text(ml_doms.button_submit, CONFIG.LOGIN_BUTTON_TEXT[0]);
                            if (code !== 0) {
                                UI.showError("服务器内部错误，请重试", ml_doms.warn)
                            } else {
                                if (allowAutoLogin) {
                                    setAutoLogin()
                                } else {
                                    delAutoLogin()
                                }
                                loginSuccess(LOGIN_TYPE.MOBILE)
                            }
                        })
                    } else {
                        is_requesting = false;
                        Util.text(ml_doms.button_submit, CONFIG.LOGIN_BUTTON_TEXT[0]);
                        if (json.result == 600) {
                            UI.showError("短信验证码错误", ml_doms.warn, ml_doms.input_code);
                            ml_doms.input_code.value = ""
                        } else {
                            UI.showError("服务器内部错误，请重试", ml_doms.warn)
                        }
                    }
                })
            };
            Util.bind(ml_doms.button_submit, "click.x", ml_submit)
        }

        function initAccountLoginForm() {
            if (al_inited) {
                return
            }
            al_inited = true;
            var captcha_value, submit_times = 0, i, first_fresh = true, need_captcha = false;
            var userCheck = {user: "", check: 0, time: 0};
            al_doms = UI.accountLoginDoms();
            _display(al_doms.captcha_container, "none");
            al_doms.input_password.value = "";
            al_doms.input_captcha.value = "";
            al_doms.input_username.value = CONFIG.DEFAULT_ACCOUNT;
            function _show_captcha() {
                need_captcha = true;
                _display(al_doms.captcha_container, "")
            }

            Util.bind(al_doms.input_username, "blur.x", _check_user);
            _check_user();
            function _check_user(cb) {
                var username = Util.trim(al_doms.input_username.value), time = (new Date).getTime();
                if (!username || /^\s*$/.test(username)) {
                    if (typeof cb === "function") {
                        cb()
                    }
                    return
                }
                if (username === userCheck.user && userCheck.check === 1 && userCheck.time >= time - 1e3 * 60 * 5) {
                    return
                }
                captcha_value = undef;
                request("checkuser", {username: username}, function (code, msg) {
                    userCheck = {user: username, check: 1, time: time};
                    if (code !== 0) {
                        submit_times = 10;
                        if (first_fresh) {
                            _fresh_captch()
                        }
                        _show_captcha();
                        if (CONFIG.LOGIN_ID == "103") {
                            try {
                                parent.niuCaptchaNotify(1)
                            } catch (err) {
                            }
                        }
                        captcha_value = false
                    } else {
                        _display(al_doms.captcha_container, "none");
                        captcha_value = msg;
                        if (CONFIG.LOGIN_ID == "103") {
                            try {
                                parent.niuCaptchaNotify(0)
                            } catch (err) {
                            }
                        }
                    }
                    notify_code = code === 0 ? 0 : 1;
                    try {
                        parent.xlQuickLogin.changeSizeFunc(notify_code)
                    } catch (err) {
                        Util.errlog(err)
                    }
                    if (typeof cb === "function") {
                        cb()
                    }
                })
            }

            for (i = al_doms.captcha_fresh.length - 1; i >= 0; --i) {
                Util.bind(al_doms.captcha_fresh[i], "click.x", _fresh_captch)
            }
            function _fresh_captch() {
                first_fresh = false;
                captcha_value = false;
                request("captcha", {img: al_doms.captcha_img}, function (code, msg) {
                    if (code !== 0) {
                        UI.showError(msg, al_doms.warn)
                    }
                })
            }

            function _display(objs, str) {
                if (!objs.pop) {
                    objs = [objs]
                }
                for (var i = objs.length; i > 0; --i) {
                    objs[i - 1].style.display = str
                }
            }

            function _login() {
                var u, p, c, allowAutoLogin;
                if (is_requesting) {
                    return
                }
                UI.hideError(al_doms.warn);
                u = Util.trim(al_doms.input_username.value);
                p = al_doms.input_password.value;
                c = captcha_value === false ? al_doms.input_captcha.value : captcha_value;
                allowAutoLogin = al_doms.check_remember.checked == true ? true : false;
                if (!u) {
                    UI.showError("用户名不能为空", al_doms.warn, al_doms.input_username);
                    return
                }
                if (!p) {
                    UI.showError("密码不能为空", al_doms.warn, al_doms.input_password);
                    return
                }
                if (!c || c.length === 0) {
                    if (need_captcha) {
                        UI.showError("验证码不能为空", al_doms.warn, al_doms.input_captcha)
                    } else {
                        UI.showError("网络繁忙，稍后再试", al_doms.warn)
                    }
                    return
                }
                if (c.length < 4) {
                    UI.showError("验证码不正确", al_doms.warn, al_doms.input_captcha);
                    return
                }
                if (captcha_value === undef) {
                    UI.showError("网络繁忙，稍后再试", al_doms.warn);
                    return
                }
                is_requesting = true;
                Util.text(al_doms.button_submit, CONFIG.LOGIN_BUTTON_TEXT[1]);
                request("login", {username: u, password: p, captcha: c}, function (code, msg) {
                    msg = msg + "(" + code + ")";
                    Util.text(al_doms.button_submit, CONFIG.LOGIN_BUTTON_TEXT[0]);
                    is_requesting = false;
                    if (code === 0) {
                        Util.delCookie("VERIFY_KEY", CONFIG.DOMAIN);
                        Util.delCookie("verify_type", CONFIG.DOMAIN);
                        Util.delCookie("check_n", CONFIG.DOMAIN);
                        Util.delCookie("check_e", CONFIG.DOMAIN);
                        Util.delCookie("logindetail", CONFIG.DOMAIN);
                        Util.delCookie("result", CONFIG.DOMAIN);
                        userCheck = {user: "", check: 0, time: 0};
                        if (allowAutoLogin) {
                            setAutoLogin()
                        } else {
                            delAutoLogin()
                        }
                        loginSuccess(LOGIN_TYPE.ACCOUNT);
                        al_doms.input_username.value = "";
                        al_doms.input_password.value = "";
                        al_doms.input_captcha.value = ""
                    } else {
                        userCheck = {user: "", check: 0, time: 0};
                        if (code !== 1 && code !== 2) {
                            al_doms.input_password.value = ""
                        }
                        al_doms.input_captcha.value = "";
                        if (submit_times > 2) {
                            _fresh_captch();
                            _show_captcha();
                            if (CONFIG.LOGIN_ID == "103") {
                                try {
                                    parent.niuCaptchaNotify(1)
                                } catch (err) {
                                }
                            }
                            try {
                                parent.xlQuickLogin.changeSizeFunc(1)
                            } catch (err) {
                                Util.errlog(err)
                            }
                        } else {
                            _check_user()
                        }
                        UI.showError(msg, al_doms.warn)
                    }
                });
                submit_times++;
                return false
            }

            al_submit = function () {
                var username = Util.trim(al_doms.input_username.value), time = (new Date).getTime();
                if ((username !== userCheck.user || userCheck.check !== 1 || userCheck.time < time - 1e3 * 60 * 5) && need_captcha === false) {
                    _check_user(_login)
                } else {
                    _login()
                }
            };
            Util.bind(al_doms.button_submit, "click.x", al_submit)
        }

        function initMailRegisterForm() {
            if (mr_inited) {
                return
            }
            mr_inited = true;
            var i, need_captcha = false;
            var mailCheck = {mail: "", check: 0};
            mr_doms = UI.mailRegisterDoms();
            request("isNeedValidate", {}, function (json) {
                if (json.result == 200 && json.need == 1) {
                    var k;
                    _fresh_captch();
                    for (k in mr_doms.captcha_container) {
                        mr_doms.captcha_container[k].style.display = ""
                    }
                    need_captcha = true
                }
                if (CONFIG.LOGIN_ID == "103") {
                    try {
                        parent.niuCaptchaNotify(1)
                    } catch (err) {
                    }
                } else {
                    if (!need_captcha) {
                        try {
                            parent.niuCaptchaNotify(0)
                        } catch (err) {
                        }
                    }
                }
                notify_code = !need_captcha ? 0 : 1;
                try {
                    parent.xlQuickLogin.changeSizeFunc(notify_code)
                } catch (err) {
                    Util.errlog(err)
                }
            });
            for (i = mr_doms.captcha_fresh.length - 1; i >= 0; --i) {
                Util.bind(mr_doms.captcha_fresh[i], "click.x", _fresh_captch)
            }
            function _fresh_captch() {
                request("registerCaptcha", {img: mr_doms.captcha_img}, function (code, msg) {
                    if (code !== 0) {
                        UI.showError(msg, mr_doms.warn)
                    }
                });
                return false
            }

            Util.bind(mr_doms.input_mail, "blur.x", _check_mail);
            function _check_mail() {
                UI.hideError(mr_doms.warn);
                var m = Util.trim(mr_doms.input_mail.value);
                if (!m) {
                    return
                }
                if (!Util.checkMail(m)) {
                    UI.showError("邮箱格式不正确", mr_doms.warn, mr_doms.input_mail);
                    return
                }
                if (mailCheck.mail === m) {
                    if (mailCheck.check === 1) {
                        return
                    }
                    if (mailCheck.check === -1) {
                        UI.showError("该邮箱已被注册", mr_doms.warn);
                        return
                    }
                }
                request("checkbind", {account: m, type: "mail"}, function (json) {
                    if (json.result == 200) {
                        mailCheck.mail = m;
                        if (json.binded == 1) {
                            mailCheck.check = -1;
                            UI.showError("该邮箱已被注册", mr_doms.warn, mr_doms.input_mail)
                        } else {
                            if (json.binded == 0) {
                                mailCheck.check = 1
                            }
                        }
                    }
                })
            }

            mr_submit = function () {
                if (is_requesting) {
                    return
                }
                UI.hideError(mr_doms.warn);
                var m = Util.trim(mr_doms.input_mail.value), p = Util.trim(mr_doms.input_password.value), c = Util.trim(mr_doms.input_captcha.value);
                if (!m) {
                    UI.showError("邮箱不能为空", mr_doms.warn, mr_doms.input_mail);
                    return
                }
                if (!Util.checkMail(m)) {
                    UI.showError("邮箱格式不正确", mr_doms.warn, mr_doms.input_mail);
                    return
                }
                if (!p) {
                    UI.showError("密码不能为空", mr_doms.warn, mr_doms.input_password);
                    return
                }
                if (p.length < 6) {
                    UI.showError("密码长度不能少于6位", mr_doms.warn, mr_doms.input_password);
                    return
                }
                if (p.length > 16) {
                    UI.showError("密码长度不能大于16位", mr_doms.warn, mr_doms.input_password);
                    return
                }
                if (need_captcha) {
                    if (!c) {
                        UI.showError("验证码不能为空", mr_doms.warn, mr_doms.input_captcha);
                        return
                    }
                    if (c.length < 4 || c.length > 6) {
                        mr_doms.input_captcha.value = "";
                        UI.showError("图片验证码错误", mr_doms.warn, mr_doms.input_captcha);
                        return
                    }
                }
                is_requesting = true;
                Util.text(mr_doms.button_submit, CONFIG.REGISTER_BUTTON_TEXT[1]);
                request("mailregister", {mail: m, password: p, code: c}, function (json) {
                    if (json.result == 200 && json.sessionid) {
                        request("sessionlogin", {sessionid: json.sessionid}, function (code, msg) {
                            is_requesting = false;
                            if (code === 0) {
                                setAutoLogin()
                            }
                            registerSuccess()
                        })
                    } else {
                        is_requesting = false;
                        Util.text(mr_doms.button_submit, CONFIG.REGISTER_BUTTON_TEXT[0]);
                        if (json.result == 702) {
                            UI.showError("该邮箱已被注册", mr_doms.warn, mr_doms.input_mail)
                        } else {
                            if (json.result == 701) {
                                mr_doms.input_password.value = "";
                                UI.showError("您的密码过于简单，请尝试字母、数字、符号的组合", mr_doms.warn, mr_doms.input_password)
                            } else {
                                if (json.result == 600) {
                                    mr_doms.input_captcha.value = "";
                                    UI.showError("图片验证码错误", mr_doms.warn, mr_doms.input_captcha);
                                    _fresh_captch()
                                } else {
                                    UI.showError("服务器内部错误，请重试", mr_doms.warn, mr_doms.input_captcha)
                                }
                            }
                        }
                    }
                })
            };
            Util.bind(mr_doms.button_submit, "click.x", mr_submit)
        }

        function initMobileRegisterForm() {
            if (UI.oneStep) {
                initMobileRegisterForm2();
                return
            }
            if (pr_inited) {
                return
            }
            if (!document.getElementsByClassName) {
                document.getElementsByClassName = function (className, element) {
                    var children = (element || document).getElementsByTagName("*");
                    var elements = new Array;
                    for (var i = 0; i < children.length; i++) {
                        var child = children[i];
                        var classNames = child.className.split(" ");
                        for (var j = 0; j < classNames.length; j++) {
                            if (classNames[j] == className) {
                                elements.push(child);
                                break
                            }
                        }
                    }
                    return elements
                }
            }
            pr_inited = true;
            var mobileCheck = {mobile: "", check: 0};
            pr_doms = UI.mobileRegisterDoms();
            Util.bind(pr_doms.button_getcode, "click.x", _showCaptch);
            Util.bind(pr_doms.captcha_confirm, "click.x", _checkCaptch);
            Util.bind(pr_doms.register_form, "keydown.x", _formKeyDown);
            function _formKeyDown(e) {
                if (pr_doms.captcha_container[0].style.display !== "none") {
                    var keynum;
                    if (window.event) {
                        keynum = e.keyCode
                    } else {
                        if (e.which) {
                            keynum = e.which
                        }
                    }
                    if (keynum == 13) {
                        _checkCaptch();
                        return false
                    }
                }
            }

            function _showCaptch() {
                if (typeof pr_doms.button_getcode != "undefined" && UI.isSmsButtonGrey(pr_doms.button_getcode)) {
                    return
                }
                UI.hideError(pr_doms.warn);
                var m = Util.trim(pr_doms.input_mobile.value);
                if (!m) {
                    UI.showError("手机号不能为空", pr_doms.warn, pr_doms.input_mobile);
                    return
                }
                if (!Util.checkMobile(m)) {
                    UI.showError("手机格式不正确", pr_doms.warn, pr_doms.input_mobile);
                    return
                }
                for (var i = 0; i < document.getElementsByClassName("pr_hide_for_sms_captcha").length; ++i) {
                    document.getElementsByClassName("pr_hide_for_sms_captcha")[i].style.display = "none"
                }
                _fresh_captch();
                _display(pr_doms.captcha_container, "");
                return false
            }

            function _checkCaptch() {
                var code = pr_doms.input_captcha.value;
                if (Util.trim(code) == "") {
                    UI.showError("验证码不能为空", pr_doms.warn, pr_doms.input_captcha);
                    return
                }
                var url = "https://zhuce.xunlei.com/regapi/?op=CheckVerifyCode";
                var params = [];
                params.code = code;
                params.key = Util.getCookie("VERIFY_KEY");
                params.type = "MEA";
                params.response = "jsonp";
                Util.getJson(url, params, function (json) {
                    if (json.result == 200) {
                        if (json.data == 200) {
                            _display(pr_doms.captcha_container, "none");
                            for (var i = 0; i < document.getElementsByClassName("pr_hide_for_sms_captcha").length; ++i) {
                                document.getElementsByClassName("pr_hide_for_sms_captcha")[i].style.display = ""
                            }
                            verifyCode = code;
                            _get_code()
                        } else {
                            UI.showError("验证码错误", pr_doms.warn, pr_doms.input_captcha);
                            _fresh_captch();
                            return
                        }
                    } else {
                        if (json.result == 1206) {
                            UI.showError("网络异常,请稍后重试", pr_doms.warn, pr_doms.input_captcha)
                        } else {
                            UI.showError("网络异常,请稍后重试", pr_doms.warn, pr_doms.input_captcha)
                        }
                    }
                    return false
                });
                return false
            }

            function _display(objs, str) {
                if (!objs.pop) {
                    objs = [objs]
                }
                for (var i = objs.length; i > 0; --i) {
                    objs[i - 1].style.display = str
                }
            }

            for (var i = pr_doms.captcha_fresh.length - 1; i >= 0; --i) {
                Util.bind(pr_doms.captcha_fresh[i], "click.x", _fresh_captch)
            }
            function _fresh_captch() {
                pr_doms.input_captcha.value = "";
                request("captcha", {img: pr_doms.captcha_img, t: "MEA"}, function (code, msg) {
                    if (code !== 0) {
                        UI.showError(msg, pr_doms.warn)
                    }
                });
                return false
            }

            function _check_mobile(cb) {
                var m = Util.trim(pr_doms.input_mobile.value);
                if (!m) {
                    return
                }
                mobileCheck = {mobile: m, check: 0};
                request("checkbind", {account: m, type: "mobile"}, function (json) {
                    if (json.result == 200) {
                        mobileCheck.mobile = m;
                        if (json.binded == 1) {
                            mobileCheck.check = -1;
                            UI.showError("该手机已被注册", pr_doms.warn, pr_doms.input_mobile)
                        } else {
                            if (json.binded == 0) {
                                mobileCheck.check = 1;
                                if (typeof cb === "function") {
                                    cb()
                                }
                            }
                        }
                    }
                })
            }

            function _get_code() {
                if (UI.isSmsButtonGrey(pr_doms.button_getcode)) {
                    return
                }
                UI.hideError(pr_doms.warn);
                var m = Util.trim(pr_doms.input_mobile.value);
                if (!m) {
                    UI.showError("手机号不能为空", pr_doms.warn, pr_doms.input_mobile);
                    return
                }
                if (!Util.checkMobile(m)) {
                    UI.showError("手机格式不正确", pr_doms.warn, pr_doms.input_mobile);
                    return
                }
                if (mobileCheck.mobile === m && mobileCheck.check === -1) {
                    UI.showError("该手机已被注册", pr_doms.warn, pr_doms.input_mobile);
                    return
                }
                var code = Util.trim(pr_doms.input_captcha.value);
                if (!code) {
                    UI.showError("验证码不能为空", pr_doms.warn, pr_doms.input_captcha);
                    return
                }
                if (mobileCheck.mobile === m && mobileCheck.check === 1) {
                    request("getsmscode", {mobile: m, verifyCode: code, type: "register"}, function (json) {
                        if (json.result == 200) {
                            UI.setSmsButtonGrey(pr_doms.button_getcode)
                        } else if (json.result == 401) {
                            UI.showError("您的手机发送短信次数过多，请明天再试", pr_doms.warn)
                        } else {
                            if (json.result == 600) {
                                UI.showError("验证码错误，请重新输入", pr_doms.warn)
                            } else {
                                UI.showError("服务器内部错误，请重试", pr_doms.warn)
                            }
                        }
                    })
                }
                if (mobileCheck.mobile !== m) {
                    _check_mobile(function () {
                        request("getsmscode", {mobile: m, verifyCode: code, type: "register"}, function (json) {
                            if (json.result == 200) {
                                UI.setSmsButtonGrey(pr_doms.button_getcode)
                            } else if (json.result == 401) {
                                UI.showError("您的手机发送短信次数过多，请明天再试", pr_doms.warn)
                            } else {
                                if (json.result == 600) {
                                    UI.showError("验证码错误，请重新输入", pr_doms.warn)
                                } else {
                                    UI.showError("服务器内部错误，请重试", pr_doms.warn)
                                }
                            }
                        })
                    })
                }
            }

            pr_submit = function () {
                if (is_requesting) {
                    return
                }
                UI.hideError(pr_doms.warn);
                var m = Util.trim(pr_doms.input_mobile.value), c = Util.trim(pr_doms.input_code.value);
                if (!m) {
                    UI.showError("手机不能为空", pr_doms.warn, pr_doms.input_mobile);
                    return
                }
                if (!Util.checkMobile(m)) {
                    UI.showError("手机格式不正确", pr_doms.warn, pr_doms.input_mobile);
                    return
                }
                if (!c) {
                    UI.showError("短信验证码不能为空", pr_doms.warn, pr_doms.input_code);
                    return
                }
                if (c.length !== 6) {
                    UI.showError("短信验证码错误", pr_doms.warn, pr_doms.input_code);
                    return
                }
                is_requesting = true;
                Util.text(pr_doms.button_submit, CONFIG.REGISTER_BUTTON_TEXT[1]);
                request("mobileregister", {mobile: m, code: c}, function (json) {
                    if (json.result == 200 && json.sessionid) {
                        request("sessionlogin", {sessionid: json.sessionid}, function (code, msg) {
                            is_requesting = false;
                            Util.text(pr_doms.button_submit, CONFIG.REGISTER_BUTTON_TEXT[0]);
                            if (code === 0) {
                                setAutoLogin()
                            }
                            UI.showUI("mobileRegister2")
                        })
                    } else {
                        is_requesting = false;
                        Util.text(pr_doms.button_submit, CONFIG.REGISTER_BUTTON_TEXT[0]);
                        if (json.result == 702 || json.result == 700) {
                            UI.showError("该手机已被注册", pr_doms.warn, pr_doms.input_mobile)
                        } else {
                            if (json.result == 600) {
                                pr_doms.input_code.value = "";
                                UI.showError("短信验证码错误", pr_doms.warn, pr_doms.input_code)
                            } else {
                                UI.showError("服务器内部错误，请重试", pr_doms.warn)
                            }
                        }
                    }
                })
            };
            pr_finish = function () {
                if (is_requesting) {
                    return
                }
                UI.hideError(pr_doms.warn2);
                var p = Util.trim(pr_doms.input_password.value);
                if (!p) {
                    UI.showError("密码不能为空", pr_doms.warn2, pr_doms.input_password);
                    return
                }
                if (p.length < 6) {
                    UI.showError("密码长度不能少于6位", pr_doms.warn2, pr_doms.input_password);
                    return
                }
                if (p.length > 16) {
                    UI.showError("密码长度不能大于16位", pr_doms.warn2, pr_doms.input_password);
                    return
                }
                is_requesting = true;
                request("setpassword", {password: p}, function (json) {
                    is_requesting = false;
                    if (json.result == 200) {
                        registerSuccess()
                    } else {
                        if (json.result == 701) {
                            UI.showError("您的密码过于简单，请尝试字母、数字、符号的组合", pr_doms.warn2, pr_doms.input_password)
                        } else {
                            UI.showError("服务器内部错误，请重试", pr_doms.warn2, pr_doms.input_password)
                        }
                    }
                })
            };
            function pr_pass() {
                registerSuccess()
            }

            Util.bind(pr_doms.button_submit, "click.x", pr_submit);
            Util.bind(pr_doms.button_finish, "click.x", pr_finish);
            Util.bind(pr_doms.button_pass, "click.x", pr_pass)
        }

        function initMobileRegisterForm2() {
            if (pr_inited) {
                return
            }
            if (!document.getElementsByClassName) {
                document.getElementsByClassName = function (className, element) {
                    var children = (element || document).getElementsByTagName("*");
                    var elements = new Array;
                    for (var i = 0; i < children.length; i++) {
                        var child = children[i];
                        var classNames = child.className.split(" ");
                        for (var j = 0; j < classNames.length; j++) {
                            if (classNames[j] == className) {
                                elements.push(child);
                                break
                            }
                        }
                    }
                    return elements
                }
            }
            pr_inited = true;
            var mobileCheck = {mobile: "", check: 0};
            pr_doms = UI.mobileRegisterDoms();
            Util.bind(pr_doms.button_getcode, "click.x", _showCaptch);
            Util.bind(pr_doms.captcha_confirm, "click.x", _checkCaptch);
            Util.bind(pr_doms.register_form, "keydown.x", _formKeyDown);
            function _formKeyDown(e) {
                if (pr_doms.captcha_container[0].style.display !== "none") {
                    var keynum;
                    if (window.event) {
                        keynum = e.keyCode
                    } else {
                        if (e.which) {
                            keynum = e.which
                        }
                    }
                    if (keynum == 13) {
                        _checkCaptch();
                        return false
                    }
                }
            }

            function _showCaptch() {
                if (typeof pr_doms.button_getcode != "undefined" && UI.isSmsButtonGrey(pr_doms.button_getcode)) {
                    return
                }
                UI.hideError(pr_doms.warn);
                var m = Util.trim(pr_doms.input_mobile.value);
                if (!m) {
                    UI.showError("手机号不能为空", pr_doms.warn, pr_doms.input_mobile);
                    return
                }
                if (!Util.checkMobile(m)) {
                    UI.showError("手机格式不正确", pr_doms.warn, pr_doms.input_mobile);
                    return
                }
                for (var i = 0; i < document.getElementsByClassName("pr_hide_for_sms_captcha").length; ++i) {
                    document.getElementsByClassName("pr_hide_for_sms_captcha")[i].style.display = "none"
                }
                _fresh_captch();
                _display(pr_doms.captcha_container, "");
                return false
            }

            function _checkCaptch() {
                var code = pr_doms.input_captcha.value;
                if (Util.trim(code) == "") {
                    UI.showError("验证码不能为空", pr_doms.warn, pr_doms.input_captcha);
                    return
                }
                var url = "https://zhuce.xunlei.com/regapi/?op=CheckVerifyCode";
                var params = [];
                params.code = code;
                params.key = Util.getCookie("VERIFY_KEY");
                params.type = "MEA";
                params.response = "jsonp";
                Util.getJson(url, params, function (json) {
                    if (json.result == 200) {
                        if (json.data == 200) {
                            _display(pr_doms.captcha_container, "none");
                            for (var i = 0; i < document.getElementsByClassName("pr_hide_for_sms_captcha").length; ++i) {
                                document.getElementsByClassName("pr_hide_for_sms_captcha")[i].style.display = ""
                            }
                            verifyCode = code;
                            _get_code()
                        } else {
                            UI.showError("验证码错误", pr_doms.warn, pr_doms.input_captcha);
                            _fresh_captch();
                            return
                        }
                    } else {
                        if (json.result == 1206) {
                            UI.showError("网络异常,请稍后重试", pr_doms.warn, pr_doms.input_captcha)
                        } else {
                            UI.showError("网络异常,请稍后重试", pr_doms.warn, pr_doms.input_captcha)
                        }
                    }
                    return false
                });
                return false
            }

            function _display(objs, str) {
                if (!objs.pop) {
                    objs = [objs]
                }
                for (var i = objs.length; i > 0; --i) {
                    objs[i - 1].style.display = str
                }
            }

            for (var i = pr_doms.captcha_fresh.length - 1; i >= 0; --i) {
                Util.bind(pr_doms.captcha_fresh[i], "click.x", _fresh_captch)
            }
            function _fresh_captch() {
                pr_doms.input_captcha.value = "";
                request("captcha", {img: pr_doms.captcha_img, t: "MEA"}, function (code, msg) {
                    if (code !== 0) {
                        UI.showError(msg, pr_doms.warn)
                    }
                });
                return false
            }

            function _check_mobile(cb) {
                var m = Util.trim(pr_doms.input_mobile.value);
                if (!m) {
                    return
                }
                mobileCheck = {mobile: m, check: 0};
                request("checkbind", {account: m, type: "mobile"}, function (json) {
                    if (json.result == 200) {
                        mobileCheck.mobile = m;
                        if (json.binded == 1) {
                            mobileCheck.check = -1;
                            UI.showError("该手机已被注册", pr_doms.warn, pr_doms.input_mobile)
                        } else {
                            if (json.binded == 0) {
                                mobileCheck.check = 1;
                                if (typeof cb === "function") {
                                    cb()
                                }
                            }
                        }
                    }
                })
            }

            function _get_code() {
                if (UI.isSmsButtonGrey(pr_doms.button_getcode)) {
                    return
                }
                UI.hideError(pr_doms.warn);
                var m = Util.trim(pr_doms.input_mobile.value);
                if (!m) {
                    UI.showError("手机号不能为空", pr_doms.warn, pr_doms.input_mobile);
                    return
                }
                if (!Util.checkMobile(m)) {
                    UI.showError("手机格式不正确", pr_doms.warn, pr_doms.input_mobile);
                    return
                }
                if (mobileCheck.mobile === m && mobileCheck.check === -1) {
                    UI.showError("该手机已被注册", pr_doms.warn, pr_doms.input_mobile);
                    return
                }
                var code = Util.trim(pr_doms.input_captcha.value);
                if (!code) {
                    UI.showError("验证码不能为空", pr_doms.warn, pr_doms.input_captcha);
                    return
                }
                if (mobileCheck.mobile === m && mobileCheck.check === 1) {
                    request("getsmscode", {mobile: m, verifyCode: code, type: "register"}, function (json) {
                        if (json.result == 200) {
                            UI.setSmsButtonGrey(pr_doms.button_getcode)
                        } else if (json.result == 401) {
                            UI.showError("您的手机发送短信次数过多，请明天再试", pr_doms.warn)
                        } else {
                            if (json.result == 600) {
                                UI.showError("验证码错误，请重新输入", pr_doms.warn)
                            } else {
                                UI.showError("服务器内部错误，请重试", pr_doms.warn)
                            }
                        }
                    })
                }
                if (mobileCheck.mobile !== m) {
                    _check_mobile(function () {
                        request("getsmscode", {mobile: m, verifyCode: code, type: "register"}, function (json) {
                            if (json.result == 200) {
                                UI.setSmsButtonGrey(pr_doms.button_getcode)
                            } else if (json.result == 401) {
                                UI.showError("您的手机发送短信次数过多，请明天再试", pr_doms.warn)
                            } else {
                                if (json.result == 600) {
                                    UI.showError("验证码错误，请重新输入", pr_doms.warn)
                                } else {
                                    UI.showError("服务器内部错误，请重试", pr_doms.warn)
                                }
                            }
                        })
                    })
                }
            }

            pr_submit = function () {
                if (is_requesting) {
                    return
                }
                UI.hideError(pr_doms.warn);
                var m = Util.trim(pr_doms.input_mobile.value), c = Util.trim(pr_doms.input_code.value), p = Util.trim(pr_doms.input_password.value);
                if (!m) {
                    UI.showError("手机不能为空", pr_doms.warn, pr_doms.input_mobile);
                    return
                }
                if (!Util.checkMobile(m)) {
                    UI.showError("手机格式不正确", pr_doms.warn, pr_doms.input_mobile);
                    return
                }
                if (!c) {
                    UI.showError("短信验证码不能为空", pr_doms.warn, pr_doms.input_code);
                    return
                }
                if (c.length !== 6) {
                    UI.showError("短信验证码错误", pr_doms.warn, pr_doms.input_code);
                    return
                }
                if (!p) {
                    UI.showError("密码不能为空", pr_doms.warn2, pr_doms.input_password);
                    return
                }
                if (p.length < 6) {
                    UI.showError("密码长度不能少于6位", pr_doms.warn2, pr_doms.input_password);
                    return
                }
                if (p.length > 16) {
                    UI.showError("密码长度不能大于16位", pr_doms.warn2, pr_doms.input_password);
                    return
                }
                is_requesting = true;
                Util.text(pr_doms.button_submit, CONFIG.REGISTER_BUTTON_TEXT[1]);
                request("mobileregisterpwd", {mobile: m, code: c, password: p}, function (json) {
                    if (json.result == 200 && json.sessionid) {
                        request("sessionlogin", {sessionid: json.sessionid}, function (code, msg) {
                            is_requesting = false;
                            Util.text(pr_doms.button_submit, CONFIG.REGISTER_BUTTON_TEXT[0]);
                            if (code === 0) {
                                setAutoLogin()
                            }
                            registerSuccess()
                        })
                    } else {
                        is_requesting = false;
                        Util.text(pr_doms.button_submit, CONFIG.REGISTER_BUTTON_TEXT[0]);
                        if (json.result == 702 || json.result == 700) {
                            UI.showError("该手机已被注册", pr_doms.warn, pr_doms.input_mobile)
                        } else {
                            if (json.result == 600) {
                                pr_doms.input_code.value = "";
                                UI.showError("短信验证码错误", pr_doms.warn, pr_doms.input_code)
                            } else {
                                UI.showError("服务器内部错误，请重试", pr_doms.warn)
                            }
                        }
                    }
                })
            };
            Util.bind(pr_doms.button_submit, "click.x", pr_submit)
        }

        function initAccountRegisterForm() {
            if (ar_inited) {
                return
            }
            ar_inited = true;
            var i, need_captcha = false;
            var accountCheck = {account: "", check: 0};
            ar_doms = UI.accountRegisterDoms();
            request("isNeedValidate", {}, function (json) {
                if (json.result == 200 && json.need == 1) {
                    var k;
                    _fresh_captch();
                    for (k in ar_doms.captcha_container) {
                        ar_doms.captcha_container[k].style.display = ""
                    }
                    need_captcha = true
                }
                if (CONFIG.LOGIN_ID == "103") {
                    try {
                        parent.niuCaptchaNotify(1)
                    } catch (err) {
                    }
                } else {
                    if (!need_captcha) {
                        try {
                            parent.niuCaptchaNotify(0)
                        } catch (err) {
                        }
                    }
                }
                notify_code = !need_captcha ? 0 : 1;
                try {
                    parent.xlQuickLogin.changeSizeFunc(notify_code)
                } catch (err) {
                    Util.errlog(err)
                }
            });
            for (i = ar_doms.captcha_fresh.length - 1; i >= 0; --i) {
                Util.bind(ar_doms.captcha_fresh[i], "click.x", _fresh_captch)
            }
            function _fresh_captch() {
                request("registerCaptcha", {img: ar_doms.captcha_img}, function (code, msg) {
                    if (code !== 0) {
                        UI.showError(msg, ar_doms.warn)
                    }
                });
                return false
            }

            Util.bind(ar_doms.input_account, "blur.x", _check_account);
            function _check_account() {
                UI.hideError(ar_doms.warn);
                var a = Util.trim(ar_doms.input_account.value);
                if (!a) {
                    return
                }
                if (a.length < 6) {
                    UI.showError("帐号不能小于6个字符", ar_doms.warn);
                    return
                }
                if (a.length > 16) {
                    UI.showError("帐号不能大于16个字符", ar_doms.warn);
                    return
                }
                var reg = /^[a-z0-9A-Z_]{6,16}$/;
                if (!reg.test(a)) {
                    UI.showError("帐号格式错误，仅支持字母、数字和下划线的组合", ar_doms.warn);
                    return
                }
                reg = /[a-zA-Z]+/;
                if (!reg.test(a)) {
                    UI.showError("帐号格式错误，帐号必须包含字母", ar_doms.warn);
                    return
                }
                if (accountCheck.account === a) {
                    if (accountCheck.check === 1) {
                        return
                    }
                    if (accountCheck.check === -1) {
                        UI.showError("该账号已被注册", ar_doms.warn);
                        return
                    }
                }
                request("checkbind", {account: a, type: "account"}, function (json) {
                    if (json.result == 200) {
                        accountCheck.account = a;
                        if (json.binded == 1) {
                            accountCheck.check = -1;
                            UI.showError("该账号已被注册", ar_doms.warn, ar_doms.input_account)
                        } else {
                            if (json.binded == 0) {
                                accountCheck.check = 1
                            }
                        }
                    }
                })
            }

            ar_submit = function () {
                if (is_requesting) {
                    return
                }
                UI.hideError(ar_doms.warn);
                var a = Util.trim(ar_doms.input_account.value), p = Util.trim(ar_doms.input_password.value), p2 = Util.trim(ar_doms.input_password2.value), c = Util.trim(ar_doms.input_captcha.value);
                if (!a) {
                    UI.showError("帐号不能为空", ar_doms.warn, ar_doms.input_account);
                    return
                }
                if (a.length < 6) {
                    UI.showError("帐号不能小于6个字符", ar_doms.warn, ar_doms.input_account);
                    return
                }
                if (a.length > 16) {
                    UI.showError("帐号不能大于16个字符", ar_doms.warn, ar_doms.input_account);
                    return
                }
                var reg = /^[a-z0-9A-Z_]{6,16}$/;
                if (!reg.test(a)) {
                    UI.showError("帐号格式错误，仅支持字母、数字和下划线的组合", ar_doms.warn, ar_doms.input_account);
                    return
                }
                reg = /[a-zA-Z]+/;
                if (!reg.test(a)) {
                    UI.showError("帐号格式错误，帐号必须包含字母", ar_doms.warn, ar_doms.input_account);
                    return
                }
                if (accountCheck.account === a && accountCheck.check === -1) {
                    UI.showError("该账号已被注册", ar_doms.warn);
                    return
                }
                if (!p) {
                    UI.showError("密码不能为空", ar_doms.warn, ar_doms.input_password);
                    return
                }
                if (p.length < 6) {
                    UI.showError("密码长度不能少于6位", ar_doms.warn, ar_doms.input_password);
                    return
                }
                if (p.length > 16) {
                    UI.showError("密码长度不能大于16位", ar_doms.warn, ar_doms.input_password);
                    return
                }
                if (p !== p2) {
                    UI.showError("两次输入密码不一致，请重新输入", ar_doms.warn, ar_doms.input_password);
                    return
                }
                if (need_captcha) {
                    if (!c) {
                        UI.showError("验证码不能为空", ar_doms.warn, ar_doms.input_captcha);
                        return
                    }
                    if (c.length < 4 || c.length > 6) {
                        ar_doms.input_captcha.value = "";
                        UI.showError("图片验证码错误", ar_doms.warn, ar_doms.input_captcha);
                        return
                    }
                }
                is_requesting = true;
                Util.text(ar_doms.button_submit, CONFIG.REGISTER_BUTTON_TEXT[1]);
                request("accountregister", {account: a, password: p, code: c}, function (json) {
                    if (json.result == 200 && json.sessionid) {
                        request("sessionlogin", {sessionid: json.sessionid}, function (code, msg) {
                            is_requesting = false;
                            if (code === 0) {
                                setAutoLogin()
                            }
                            registerSuccess()
                        })
                    } else {
                        is_requesting = false;
                        Util.text(ar_doms.button_submit, CONFIG.REGISTER_BUTTON_TEXT[0]);
                        if (json.result == 301) {
                            UI.showError("帐号格式错误，仅支持字母、数字和下划线的组合", ar_doms.warn, ar_doms.input_account)
                        } else {
                            if (json.result == 704) {
                                UI.showError("该帐号已被注册", ar_doms.warn, ar_doms.input_account)
                            } else {
                                if (json.result == 701) {
                                    ar_doms.input_password.value = "";
                                    UI.showError("您的密码过于简单，请尝试字母、数字、符号的组合", ar_doms.warn, ar_doms.input_password)
                                } else {
                                    if (json.result == 600) {
                                        ar_doms.input_captcha.value = "";
                                        UI.showError("图片验证码错误", ar_doms.warn, ar_doms.input_captcha);
                                        _fresh_captch()
                                    } else {
                                        UI.showError("服务器内部错误，请重试", ar_doms.warn, ar_doms.input_account)
                                    }
                                }
                            }
                        }
                    }
                })
            };
            Util.bind(ar_doms.button_submit, "click.x", ar_submit)
        }

        function initBindMobileForm() {
            var bm_inited, bm_doms, get_sms_code, get_verify_code, check_verify_code, submit, requesting = false, verify_ok = false, dynamic_aq_domain = "http://dynamic.aq.xunlei.com/";
            if (bm_inited) {
                return
            }
            bm_doms = UI.getDoms();
            err_msgs = UI.getErrorMsg();
            bm_inited = true;
            get_verify_code = function () {
                bm_doms.verifyCode.value = "";
                bm_doms.verifyCode.parentNode.className = "";
                request("captcha", {img: bm_doms.getVerifyCode, t: "MEA"}, function (code, msg) {
                });
                return false
            };
            get_verify_code();
            Util.bind(bm_doms.getVerifyCode, "click.x", get_verify_code);
            check_verify_code = function () {
                if (bm_doms.verifyCode.value === "") {
                    verify_ok = false;
                    UI.showError("请输入正确的图形验证码", bm_doms.verifyCode.nextSibling.nextSibling);
                    bm_doms.verifyCode.parentNode.className = "";
                    return false
                }
                request("checkRegisterCaptcha", {code: bm_doms.verifyCode.value}, function (json) {
                    if (json.result == 200 && json.data == 200) {
                        verify_ok = true;
                        UI.hideError(bm_doms.verifyCode.nextSibling.nextSibling);
                        bm_doms.verifyCode.parentNode.className = "correct";
                        return true
                    } else {
                        verify_ok = false;
                        UI.showError("请输入正确的图形验证码", bm_doms.verifyCode.nextSibling.nextSibling);
                        get_verify_code();
                        bm_doms.verifyCode.parentNode.className = "";
                        return false
                    }
                });
                return
            };
            Util.bind(bm_doms.verifyCode, "blur.x", check_verify_code);
            get_sms_code = function () {
                if (!UI.checkMobile()) {
                    return
                }
                if (!verify_ok) {
                    UI.showError("请输入正确的图形验证码", bm_doms.verifyCode.nextSibling.nextSibling);
                    return
                }
                var data = {m: "set_iframe_send", mobile: bm_doms.mobile.value, verifycode: bm_doms.verifyCode.value};
                if (requesting) {
                    return
                }
                requesting = true;
                Util.getJson(dynamic_aq_domain + "interface/sms", data, function (data) {
                    requesting = false;
                    if (data.result != 0) {
                        get_verify_code();
                        UI.showError(err_msgs[data.result], bm_doms.mobileCode.nextSibling.nextSibling);
                        return
                    } else {
                        UI.setSmsButtonGrey(bm_doms.getMobileCode);
                        return
                    }
                }, "jsoncallback")
            };
            Util.bind(bm_doms.getMobileCode, "click.x", get_sms_code);
            submit = function () {
                if (!UI.checkMobile()) {
                    return
                }
                if (!verify_ok) {
                    UI.showError("请输入正确的图形验证码", bm_doms.verifyCode.nextSibling.nextSibling, bm_doms.verifyCode);
                    return
                }
                if (!UI.checkMobileCode()) {
                    return
                }
                var data = {m: "set_iframe_check", mobile: bm_doms.mobile.value, code: bm_doms.mobileCode.value};
                if (requesting) {
                    return
                }
                requesting = true;
                Util.getJson(dynamic_aq_domain + "interface/sms", data, function (data) {
                    requesting = false;
                    if (data.result != 0) {
                        bm_doms.mobileCode.value = "";
                        verify_ok = false;
                        get_verify_code();
                        UI.showError(err_msgs[data.result], bm_doms.mobileCode.nextSibling.nextSibling);
                        return
                    } else {
                        bm_doms.bindForm.style.display = "none";
                        bm_doms.succForm.style.display = "";
                        return
                    }
                }, "jsoncallback")
            };
            Util.bind(bm_doms.submit, "click.x", submit);
            Util.bind(bm_doms.closeBtn, "click.x", function () {
                parent.xlQuickLogin.closeBindFunc()
            })
        }

        function config(opts, isinit) {
            if (typeof opts !== "object") {
                return
            }
            var k, _k, v, _v, _t, dfts = {}, key_map = {}, not_allowd = {
                XL_CLIENT_PATH: 1,
                DEBUG: 1,
                DOMAIN: 1,
                DOMAIN_ALLOWED: 1
            };
            for (k in CONFIG) {
                if (!CONFIG.hasOwnProperty(k) || k in not_allowd) {
                    continue
                }
                _k = k.toLowerCase().replace(/[_\-]/g, "");
                dfts[_k] = CONFIG[k];
                key_map[_k] = k
            }
            var except = ",AUTO_LOGIN_EXPIRE_TIME,UI_STYLE,LOGIN_ID,", setloginid = false;
            for (k in opts) {
                if (!opts.hasOwnProperty(k)) {
                    continue
                }
                _k = k.toLowerCase().replace(/[_\-]/g, "");
                if (_k in dfts) {
                    _v = dfts[_k];
                    v = opts[k];
                    _t = typeof _v;
                    _k = key_map[_k];
                    if (_t === "boolean" && except.indexOf(_k) === -1) {
                        v = !!v
                    }
                    if (v === _v && _k !== "LOGIN_ID") {
                        continue
                    }
                    if (_t === typeof v || except.indexOf(_k) >= 0) {
                        if (_k === "LOGIN_ID") {
                            setloginid = true
                        }
                        CONFIG[_k] = v
                    } else {
                        throw new Error("config key(" + k + ") error, type not match")
                    }
                } else {
                    throw new Error("config key(" + k + ") not exists")
                }
            }
            if (isinit && !setloginid) {
                throw new Error("not init loginID， please init it")
            }
        }

        function init(opts) {
            if (inited) {
                return
            }
            var i, flag, host = Util.getDomain(document.referrer), p, domain;
            inited = true;
            p = host.split(".");
            domain = p.pop();
            domain = (p.pop() + "." + domain).toLowerCase();
            for (i = CONFIG.DOMAIN_ALLOWED.length; i > 0; --i) {
                if (domain === CONFIG.DOMAIN_ALLOWED[i - 1]) {
                    flag = true;
                    break
                }
            }
            if (!flag) {
                throw new Error("你的域名不支持此快速登录")
            }
            CONFIG.DOMAIN = domain;
            config(opts, true);
            if (CONFIG.SET_ROOT_DOMAIN) {
                document.domain = CONFIG.DOMAIN
            }
            var c = Util.getCookie(), login_key, done = "0", lt_k = CONFIG.LOGIN_TYPE_COOKIE_NAME;
            login_key = store.enabled && store.get(CONFIG.LOGIN_KEY_NAME) || c[CONFIG.LOGIN_KEY_NAME];
            loginType = c[lt_k];
            if (typeof loginType === "string") {
                loginType = loginType.split("_");
                done = loginType[1] === "1" ? "1" : "0"
            }
            loginType = loginType === undef ? -1 : parseInt(loginType);
            if (loginType === undef || loginType !== loginType || loginType < -1 || loginType > 5) {
                loginType = -1
            }
            if (!Util.isSessionid(c.sessionid)) {
                if (CONFIG.AUTO_LOGIN_EXPIRE_TIME > 0 && c.userid && c.userid > 0 && login_key && (store.enabled && store.get("xl_autologin") || c.xl_autologin)) {
                    var ali = "xl_autologin_info";
                    if (store.enabled && store.get(ali)) {
                        var now = (new Date).getTime(), t_last;
                        t_last = Number(store.get(ali).split("|")[0]);
                        if (now - t_last < 5 * 60 * 1e3) {
                            store.set(ali, now + "|" + "1");
                            return
                        }
                    }
                    is_requesting = true;
                    request("loginkeylogin", {loginkey: login_key, userid: c.userid}, function (code, msg) {
                        is_requesting = false;
                        if (code !== 0) {
                            store.enabled && store.remove(CONFIG.LOGIN_KEY_NAME);
                            Util.delCookie(CONFIG.LOGIN_KEY_NAME);
                            return
                        } else {
                            if (store.enabled) {
                                store.set(ali, (new Date).getTime() + "|" + "0")
                            }
                            loginSuccess(LOGIN_TYPE.AUTOWEB)
                        }
                    })
                }
                if (loginType !== -1) {
                    Util.setCookie(lt_k, "0", 0, CONFIG.DOMAIN)
                }
            }
        }

        self = {
            register: function (k, t) {
                UIS[k] = t
            }, getLoginBox: function () {
                load(function () {
                    if (container) {
                        container.style.display = ""
                    }
                    var ui_type = "";
                    if (CONFIG.LOGIN_TYPES !== "" && CONFIG.DEFUALT_UI === "login") {
                        if (CONFIG.LOGIN_TYPES.indexOf("1") >= 0) {
                            ui_type = "accountLogin";
                            initAccountLoginForm()
                        } else {
                            if (CONFIG.LOGIN_TYPES.indexOf("2") >= 0) {
                                ui_type = "mobileLogin";
                                initMobileLoginForm()
                            }
                        }
                        if (CONFIG.THIRD_LOGIN_DISPLAY) {
                            initThirdLogin()
                        }
                    } else {
                        if (CONFIG.REGISTER_TYPES !== "" && CONFIG.DEFUALT_UI === "register") {
                            if (CONFIG.REGISTER_TYPES.indexOf("2") >= 0) {
                                ui_type = "mobileRegister";
                                initMobileRegisterForm()
                            } else {
                                if (CONFIG.REGISTER_TYPES.indexOf("1") >= 0) {
                                    ui_type = "mailRegister";
                                    initMailRegisterForm()
                                } else {
                                    if (CONFIG.REGISTER_TYPES.indexOf("3") >= 0) {
                                        ui_type = "accountRegister";
                                        initAccountRegisterForm()
                                    }
                                }
                            }
                        } else {
                            throw new Error("请配置登录和注册方式")
                        }
                    }
                    if (CONFIG.SET_ROOT_DOMAIN) {
                        parent.xlQuickLogin.uiChangeFunc(ui_type)
                    }
                    UI.onShowUI = function (type) {
                        ui_type = type;
                        switch (type) {
                            case"accountLogin":
                                initAccountLoginForm();
                                break;
                            case"mobileLogin":
                                initMobileLoginForm();
                                break;
                            case"mailRegister":
                                initMailRegisterForm();
                                break;
                            case"mobileRegister":
                                initMobileRegisterForm();
                                break;
                            case"accountRegister":
                                initAccountRegisterForm();
                                break
                        }
                        if (CONFIG.SET_ROOT_DOMAIN) {
                            parent.xlQuickLogin.uiChangeFunc(ui_type)
                        }
                    };
                    Util.setCookie(CONFIG.LOGIN_TYPE_COOKIE_NAME, LOGIN_TYPE.INITED + "", 0, CONFIG.DOMAIN);
                    Util.bind(document, "keypress.xl", function (e) {
                        if (e.keyCode == "13") {
                            switch (ui_type) {
                                case"accountLogin":
                                    al_submit();
                                    break;
                                case"mobileLogin":
                                    ml_submit();
                                    break;
                                case"mailRegister":
                                    mr_submit();
                                    break;
                                case"mobileRegister":
                                    pr_submit();
                                    break;
                                case"mobileRegister2":
                                    pr_finish();
                                    break;
                                case"accountRegister":
                                    ar_submit();
                                    break
                            }
                        } else {
                            if (e.keyCode == "27") {
                                if (CONFIG.UI_THEME === "popup" && CONFIG.SET_ROOT_DOMAIN === true) {
                                    parent.xlQuickLogin.closeFunc()
                                }
                            }
                        }
                    });
                    if (CONFIG.UI_THEME === "popup" && CONFIG.DEFUALT_BACKGROUND) {
                        if (CONFIG.USE_CDN) {
                            CONFIG.DEFUALT_BACKGROUND = CONFIG.DEFUALT_BACKGROUND.replace(/http:\/\/i\.xunlei\.com\//, CONFIG.CDN_PATH)
                        }
                        UI.setBackground(CONFIG.DEFUALT_BACKGROUND)
                    }
                    if (CONFIG.UI_THEME === "popup" || CONFIG.UI_THEME === "embed") {
                        UI.setAlertError(CONFIG.ALERT_ERROR)
                    }
                })
            }, getBindBox: function () {
                load(function () {
                    if (container) {
                        container.style.display = ""
                    }
                    initBindMobileForm()
                })
            }, setBackgroud: function (url) {
                if (CONFIG.UI_THEME === "embed") {
                    throw new Error("内嵌主题暂时不支持设置背景图片")
                }
                if (!url) {
                    url = (CONFIG.ALL_HTTPS ? "https://" : "http://") + "i." + CONFIG.DOMAIN + "/login/theme/popup/images/layer_bg.jpg"
                }
                UI.setBackground(url)
            }, showUI: function (ui) {
                var ui_type = "";
                if (ui === "login") {
                    if (CONFIG.LOGIN_TYPES.indexOf("1") >= 0) {
                        ui_type = "accountLogin"
                    } else {
                        if (CONFIG.LOGIN_TYPES.indexOf("2") >= 0) {
                            ui_type = "mobileLogin"
                        }
                    }
                } else {
                    if (ui === "register") {
                        if (CONFIG.REGISTER_TYPES.indexOf("2") >= 0) {
                            ui_type = "mobileRegister"
                        } else {
                            if (CONFIG.REGISTER_TYPES.indexOf("1") >= 0) {
                                ui_type = "mailRegister"
                            } else {
                                if (CONFIG.REGISTER_TYPES.indexOf("3") >= 0) {
                                    ui_type = "accountRegister"
                                }
                            }
                        }
                    }
                }
                if (ui_type) {
                    UI.showUI(ui_type);
                    parent.xlQuickLogin.uiChangeFunc(ui_type)
                }
            }, config: config, init: init
        };
        return self
    }();
    (function () {
        var inited = false, self, k;
        self = {
            Util: Util,
            registerUI: UIManager.register,
            init: function (opts) {
                if (inited === true) {
                    return
                }
                inited = true;
                UIManager.init(opts)
            },
            config: UIManager.config,
            getLoginBox: UIManager.getLoginBox,
            getBindBox: UIManager.getBindBox,
            setBackgroud: UIManager.setBackgroud,
            showUI: UIManager.showUI
        };
        for (k in self) {
            if (self.hasOwnProperty(k)) {
                xlQuickLogin[k] = self[k]
            }
        }
    })()
})();
(function (f) {
    if (typeof exports === "object" && typeof module !== "undefined") {
        module.exports = f()
    } else if (typeof define === "function" && define.amd) {
        define([], f)
    } else {
        var g;
        if (typeof window !== "undefined") {
            g = window
        } else if (typeof global !== "undefined") {
            g = global
        } else if (typeof self !== "undefined") {
            g = self
        } else {
            g = this
        }
        g.store = f()
    }
})(function () {
    var define, module, exports;
    return function e(t, n, r) {
        function s(o, u) {
            if (!n[o]) {
                if (!t[o]) {
                    var a = typeof require == "function" && require;
                    if (!u && a)return a(o, !0);
                    if (i)return i(o, !0);
                    var f = new Error("Cannot find module '" + o + "'");
                    throw f.code = "MODULE_NOT_FOUND", f
                }
                var l = n[o] = {exports: {}};
                t[o][0].call(l.exports, function (e) {
                    var n = t[o][1][e];
                    return s(n ? n : e)
                }, l, l.exports, e, t, n, r)
            }
            return n[o].exports
        }

        var i = typeof require == "function" && require;
        for (var o = 0; o < r.length; o++)s(r[o]);
        return s
    }({
        1: [function (require, module, exports) {
            (function (global) {
                "use strict";
                module.exports = function () {
                    function e() {
                        try {
                            return o in n && n[o]
                        } catch (e) {
                            return !1
                        }
                    }

                    var t, r = {}, n = "undefined" != typeof window ? window : global, i = n.document, o = "localStorage", a = "script";
                    if (r.disabled = !1, r.version = "1.3.20", r.set = function (e, t) {
                        }, r.get = function (e, t) {
                        }, r.has = function (e) {
                            return void 0 !== r.get(e)
                        }, r.remove = function (e) {
                        }, r.clear = function () {
                        }, r.transact = function (e, t, n) {
                            null == n && (n = t, t = null), null == t && (t = {});
                            var i = r.get(e, t);
                            n(i), r.set(e, i)
                        }, r.getAll = function () {
                        }, r.forEach = function () {
                        }, r.serialize = function (e) {
                            return JSON.stringify(e)
                        }, r.deserialize = function (e) {
                            if ("string" == typeof e)try {
                                return JSON.parse(e)
                            } catch (t) {
                                return e || void 0
                            }
                        }, e()) t = n[o], r.set = function (e, n) {
                        return void 0 === n ? r.remove(e) : (t.setItem(e, r.serialize(n)), n)
                    }, r.get = function (e, n) {
                        var i = r.deserialize(t.getItem(e));
                        return void 0 === i ? n : i
                    }, r.remove = function (e) {
                        t.removeItem(e)
                    }, r.clear = function () {
                        t.clear()
                    }, r.getAll = function () {
                        var e = {};
                        return r.forEach(function (t, r) {
                            e[t] = r
                        }), e
                    }, r.forEach = function (e) {
                        for (var n = 0; n < t.length; n++) {
                            var i = t.key(n);
                            e(i, r.get(i))
                        }
                    }; else if (i && i.documentElement.addBehavior) {
                        var c, u;
                        try {
                            u = new ActiveXObject("htmlfile"), u.open(), u.write("<" + a + ">document.w=window</" + a + '><iframe src="/favicon.ico"></iframe>'), u.close(), c = u.w.frames[0].document, t = c.createElement("div")
                        } catch (l) {
                            t = i.createElement("div"), c = i.body
                        }
                        var f = function (e) {
                            return function () {
                                var n = Array.prototype.slice.call(arguments, 0);
                                n.unshift(t), c.appendChild(t), t.addBehavior("#default#userData"), t.load(o);
                                var i = e.apply(r, n);
                                return c.removeChild(t), i
                            }
                        }, d = new RegExp("[!\"#$%&'()*+,/\\\\:;<=>?@[\\]^`{|}~]", "g"), s = function (e) {
                            return e.replace(/^d/, "___$&").replace(d, "___")
                        };
                        r.set = f(function (e, t, n) {
                            return t = s(t), void 0 === n ? r.remove(t) : (e.setAttribute(t, r.serialize(n)), e.save(o), n)
                        }), r.get = f(function (e, t, n) {
                            t = s(t);
                            var i = r.deserialize(e.getAttribute(t));
                            return void 0 === i ? n : i
                        }), r.remove = f(function (e, t) {
                            t = s(t), e.removeAttribute(t), e.save(o)
                        }), r.clear = f(function (e) {
                            var t = e.XMLDocument.documentElement.attributes;
                            e.load(o);
                            for (var r = t.length - 1; r >= 0; r--)e.removeAttribute(t[r].name);
                            e.save(o)
                        }), r.getAll = function (e) {
                            var t = {};
                            return r.forEach(function (e, r) {
                                t[e] = r
                            }), t
                        }, r.forEach = f(function (e, t) {
                            for (var n, i = e.XMLDocument.documentElement.attributes, o = 0; n = i[o]; ++o)t(n.name, r.deserialize(e.getAttribute(n.name)))
                        })
                    }
                    try {
                        var v = "__storejs__";
                        r.set(v, v), r.get(v) != v && (r.disabled = !0), r.remove(v)
                    } catch (l) {
                        r.disabled = !0
                    }
                    return r.enabled = !r.disabled, r
                }()
            }).call(this, typeof global !== "undefined" ? global : typeof self !== "undefined" ? self : typeof window !== "undefined" ? window : {})
        }, {}]
    }, {}, [1])(1)
});