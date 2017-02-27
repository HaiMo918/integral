!function () {
    var e = {
        Cookie: {
            set: function (e, t, a, s, i) {
                var n = "";
                if (a && "number" == typeof a) {
                    var r = new Date;
                    r.setTime(r.getTime() + 24 * a * 60 * 60 * 1e3), n = "; expires=" + r.toGMTString()
                }
                var s = s || "/", i = i || ".youku.com";
                document.cookie = [e, "=", t, n, ";domain=", i, ";path=", s].join("")
            }, get: function (e) {
                for (var t = e + "=", a = document.cookie ? document.cookie.split("; ") : [], s = 0; s < a.length; s++) {
                    for (var i = a[s]; " " == i.charAt(0);)i = i.substring(1, i.length);
                    if (0 == i.indexOf(t))return i.substring(t.length, i.length)
                }
                return null
            }, remove: function (e) {
                this.set(e, "", -1)
            }
        }, Common: {
            shichang: function (e) {
                return 30 * e * e - 20 * e
            }, decodeBase64: function (e) {
                if (!e)return "";
                var t, a, s, i, n, r, o, c = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=", l = "", d = 0;
                e = e.replace(/[^A-Za-z0-9\+\/\=]/g, "");
                do i = c.indexOf(e.charAt(d++)), n = c.indexOf(e.charAt(d++)), r = c.indexOf(e.charAt(d++)), o = c.indexOf(e.charAt(d++)), t = i << 2 | n >> 4, a = (15 & n) << 4 | r >> 2, s = (3 & r) << 6 | o, l += String.fromCharCode(t), 64 != r && (l += String.fromCharCode(a)), 64 != o && (l += String.fromCharCode(s)); while (d < e.length);
                return this.utf8To16(l)
            }, utf8To16: function (e) {
                var t, a, s, i, n, r = [];
                for (a = e.length, t = 0; t < a;)switch (s = e.charCodeAt(t++), s >> 4) {
                    case 0:
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                    case 7:
                        r.push(e.charAt(t - 1));
                        break;
                    case 12:
                    case 13:
                        i = e.charCodeAt(t++), r.push(String.fromCharCode((31 & s) << 6 | 63 & i));
                        break;
                    case 14:
                        i = e.charCodeAt(t++), n = e.charCodeAt(t++), r.push(String.fromCharCode((15 & s) << 12 | (63 & i) << 6 | (63 & n) << 0))
                }
                return r.join("")
            }, cutStr: function (e, t, s) {
                var e = e.replace(/</g, "&lt;").replace(/>/g, "&gt;"), i = this._strLen(e);
                if (i <= t)return e;
                var n = 0, r = 0;
                str_cut = new String, r = e.length;
                for (var o = 0; o < r; o++) {
                    if (n++, a = e.charAt(o), escape(a).length > 4 && n++, n > t)return s && (str_cut = str_cut.concat(s)), str_cut;
                    str_cut = str_cut.concat(a)
                }
            }, _strLen: function (e) {
                if (!e)return 0;
                var t = 0, s = 0;
                s = e.length;
                for (var i = 0; i < s; i++)t++, a = e.charAt(i), escape(a).length > 4 && t++;
                return t
            }, serialize: function (e) {
                return JSON.stringify(e)
            }, deserialize: function (e) {
                if ("string" == typeof e)try {
                    return JSON.parse(e)
                } catch (t) {
                    return e || void 0
                }
            }, getBrowser: function () {
                var e, t = {}, a = navigator.userAgent.toLowerCase();
                return (e = a.match(/rv:([\d.]+)\) like gecko/)) ? t.ie = e[1] : (e = a.match(/msie ([\d.]+)/)) ? t.ie = e[1] : (e = a.match(/firefox\/([\d.]+)/)) ? t.firefox = e[1] : (e = a.match(/chrome\/([\d.]+)/)) ? t.chrome = e[1] : (e = a.match(/opera.([\d.]+)/)) ? t.opera = e[1] : (e = a.match(/version\/([\d.]+).*safari/)) ? t.safari = e[1] : 0, t
            }, bind: function (e, t, a) {
                e.attachEvent ? e.attachEvent("on" + t, function () {
                        return function (t) {
                            window.event.cancelBubble = !0, e.attachEvent = [a.apply(e)]
                        }
                    }(e), !1) : e.addEventListener && e.addEventListener(t, function (e) {
                        e.stopPropagation(), a.apply(this)
                    }, !1)
            }, Md5: function (e) {
                var t, a, s, i, n, r, o, c, l, d = function (e, t) {
                    return e << t | e >>> 32 - t
                }, u = function (e, t) {
                    var a, s, i, n, r;
                    return i = 2147483648 & e, n = 2147483648 & t, a = 1073741824 & e, s = 1073741824 & t, r = (1073741823 & e) + (1073741823 & t), a & s ? 2147483648 ^ r ^ i ^ n : a | s ? 1073741824 & r ? 3221225472 ^ r ^ i ^ n : 1073741824 ^ r ^ i ^ n : r ^ i ^ n
                }, p = function (e, t, a) {
                    return e & t | ~e & a
                }, h = function (e, t, a) {
                    return e & a | t & ~a
                }, g = function (e, t, a) {
                    return e ^ t ^ a
                }, m = function (e, t, a) {
                    return t ^ (e | ~a)
                }, v = function (e, t, a, s, i, n, r) {
                    return e = u(e, u(u(p(t, a, s), i), r)), u(d(e, n), t)
                }, _ = function (e, t, a, s, i, n, r) {
                    return e = u(e, u(u(h(t, a, s), i), r)), u(d(e, n), t)
                }, f = function (e, t, a, s, i, n, r) {
                    return e = u(e, u(u(g(t, a, s), i), r)), u(d(e, n), t)
                }, y = function (e, t, a, s, i, n, r) {
                    return e = u(e, u(u(m(t, a, s), i), r)), u(d(e, n), t)
                }, k = function (e) {
                    for (var t, a = e.length, s = a + 8, i = (s - s % 64) / 64, n = 16 * (i + 1), r = Array(n - 1), o = 0, c = 0; c < a;)t = (c - c % 4) / 4, o = c % 4 * 8, r[t] = r[t] | e.charCodeAt(c) << o, c++;
                    return t = (c - c % 4) / 4, o = c % 4 * 8, r[t] = r[t] | 128 << o, r[n - 2] = a << 3, r[n - 1] = a >>> 29, r
                }, C = function (e) {
                    var t, a, s = "", i = "";
                    for (a = 0; a <= 3; a++)t = e >>> 8 * a & 255, i = "0" + t.toString(16), s += i.substr(i.length - 2, 2);
                    return s
                }, b = function (e) {
                    e = e.replace(/\x0d\x0a/g, "\n");
                    for (var t = "", a = 0; a < e.length; a++) {
                        var s = e.charCodeAt(a);
                        s < 128 ? t += String.fromCharCode(s) : s > 127 && s < 2048 ? (t += String.fromCharCode(s >> 6 | 192), t += String.fromCharCode(63 & s | 128)) : (t += String.fromCharCode(s >> 12 | 224), t += String.fromCharCode(s >> 6 & 63 | 128), t += String.fromCharCode(63 & s | 128))
                    }
                    return t
                }, w = Array(), A = 7, U = 12, I = 17, N = 22, D = 5, M = 9, S = 14, x = 20, E = 4, j = 11, T = 16, H = 23, z = 6, Q = 10, q = 15, L = 21;
                for (e = b(e), w = k(e), r = 1732584193, o = 4023233417, c = 2562383102, l = 271733878, t = 0; t < w.length; t += 16)a = r, s = o, i = c, n = l, r = v(r, o, c, l, w[t + 0], A, 3614090360), l = v(l, r, o, c, w[t + 1], U, 3905402710), c = v(c, l, r, o, w[t + 2], I, 606105819), o = v(o, c, l, r, w[t + 3], N, 3250441966), r = v(r, o, c, l, w[t + 4], A, 4118548399), l = v(l, r, o, c, w[t + 5], U, 1200080426), c = v(c, l, r, o, w[t + 6], I, 2821735955), o = v(o, c, l, r, w[t + 7], N, 4249261313), r = v(r, o, c, l, w[t + 8], A, 1770035416), l = v(l, r, o, c, w[t + 9], U, 2336552879), c = v(c, l, r, o, w[t + 10], I, 4294925233), o = v(o, c, l, r, w[t + 11], N, 2304563134), r = v(r, o, c, l, w[t + 12], A, 1804603682), l = v(l, r, o, c, w[t + 13], U, 4254626195), c = v(c, l, r, o, w[t + 14], I, 2792965006), o = v(o, c, l, r, w[t + 15], N, 1236535329), r = _(r, o, c, l, w[t + 1], D, 4129170786), l = _(l, r, o, c, w[t + 6], M, 3225465664), c = _(c, l, r, o, w[t + 11], S, 643717713), o = _(o, c, l, r, w[t + 0], x, 3921069994), r = _(r, o, c, l, w[t + 5], D, 3593408605), l = _(l, r, o, c, w[t + 10], M, 38016083), c = _(c, l, r, o, w[t + 15], S, 3634488961), o = _(o, c, l, r, w[t + 4], x, 3889429448), r = _(r, o, c, l, w[t + 9], D, 568446438), l = _(l, r, o, c, w[t + 14], M, 3275163606), c = _(c, l, r, o, w[t + 3], S, 4107603335), o = _(o, c, l, r, w[t + 8], x, 1163531501), r = _(r, o, c, l, w[t + 13], D, 2850285829), l = _(l, r, o, c, w[t + 2], M, 4243563512), c = _(c, l, r, o, w[t + 7], S, 1735328473), o = _(o, c, l, r, w[t + 12], x, 2368359562), r = f(r, o, c, l, w[t + 5], E, 4294588738), l = f(l, r, o, c, w[t + 8], j, 2272392833), c = f(c, l, r, o, w[t + 11], T, 1839030562), o = f(o, c, l, r, w[t + 14], H, 4259657740), r = f(r, o, c, l, w[t + 1], E, 2763975236), l = f(l, r, o, c, w[t + 4], j, 1272893353), c = f(c, l, r, o, w[t + 7], T, 4139469664), o = f(o, c, l, r, w[t + 10], H, 3200236656), r = f(r, o, c, l, w[t + 13], E, 681279174), l = f(l, r, o, c, w[t + 0], j, 3936430074), c = f(c, l, r, o, w[t + 3], T, 3572445317), o = f(o, c, l, r, w[t + 6], H, 76029189), r = f(r, o, c, l, w[t + 9], E, 3654602809), l = f(l, r, o, c, w[t + 12], j, 3873151461), c = f(c, l, r, o, w[t + 15], T, 530742520), o = f(o, c, l, r, w[t + 2], H, 3299628645), r = y(r, o, c, l, w[t + 0], z, 4096336452), l = y(l, r, o, c, w[t + 7], Q, 1126891415), c = y(c, l, r, o, w[t + 14], q, 2878612391), o = y(o, c, l, r, w[t + 5], L, 4237533241), r = y(r, o, c, l, w[t + 12], z, 1700485571), l = y(l, r, o, c, w[t + 3], Q, 2399980690), c = y(c, l, r, o, w[t + 10], q, 4293915773), o = y(o, c, l, r, w[t + 1], L, 2240044497), r = y(r, o, c, l, w[t + 8], z, 1873313359), l = y(l, r, o, c, w[t + 15], Q, 4264355552), c = y(c, l, r, o, w[t + 6], q, 2734768916), o = y(o, c, l, r, w[t + 13], L, 1309151649), r = y(r, o, c, l, w[t + 4], z, 4149444226), l = y(l, r, o, c, w[t + 11], Q, 3174756917), c = y(c, l, r, o, w[t + 2], q, 718787259), o = y(o, c, l, r, w[t + 9], L, 3951481745), r = u(r, a), o = u(o, s), c = u(c, i), l = u(l, n);
                var O = C(r) + C(o) + C(c) + C(l);
                return O.toLowerCase()
            }
        }, User: {
            getYKToken: function () {
                return e.Cookie.get("yktk")
            }, getUserName: function () {
                var t = this.getYKToken();
                if (t) {
                    var a = e.Common.decodeBase64(decodeURIComponent(t).split("|")[3]);
                    if (a.indexOf(",") > -1 && a.indexOf("nn:") > -1 && a.indexOf("id:") > -1)return a.split(",")[1].split(":")[1]
                }
                return 0
            }, getUID: function () {
                var t = this.getYKToken();
                if (t) {
                    var a = e.Common.decodeBase64(decodeURIComponent(t).split("|")[3]);
                    if (a.indexOf(",") > -1 && a.indexOf("nn:") > -1 && a.indexOf("id:") > -1)return parseInt(a.split(",")[0].split(":")[1])
                }
                return 0
            }, getYTID: function () {
                var t = this.getYKToken();
                if (t) {
                    var a = e.Common.decodeBase64(decodeURIComponent(t).split("|")[3]);
                    if (a.indexOf(",") > -1 && a.indexOf("ytid:") > -1)return parseInt(a.split(",")[3].split(":")[1])
                }
                return 0
            }, getisVIP: function () {
                var t = this.getYKToken();
                if (t) {
                    var a = e.Common.decodeBase64(decodeURIComponent(t).split("|")[3]);
                    if (a.indexOf(",") > -1 && a.indexOf("vip:") > -1)return "true" == a.split(",")[2].split(":")[1]
                }
                return !1
            }, getLoginStatus: function () {
                return 0 !== this.getUID()
            }
        }, Ajax: {
            getScript: function (e, t, a) {
                if ("string" == typeof arguments[0]) {
                    var t = "function" == typeof arguments[1] ? t : function () {
                        }, a = "boolean" == typeof arguments[2] && a, s = document.getElementsByTagName("HEAD")[0], i = document.createElement("SCRIPT");
                    i.type = "text/javascript", i.src = e, i.onload = i.onreadystatechange = function () {
                        this.readyState && "loaded" != this.readyState && "complete" != this.readyState || (t(), i.onload = i.onreadystatechange = null, a && this.parentNode.removeChild(this))
                    }, s.appendChild(i)
                }
            }
        }
    };
    window.CHUDA = e
}(), function () {
    if (void 0 == window.jQuery) {
        var e = [location.protocol, "//", "static.youku.com", "/js/jquery.js"].join("");
        CHUDA.Ajax.getScript(e, function () {
            window.jQuery = jQuery.noConflict()
        }, !0)
    }
    Number.prototype.toFixed = function (e) {
        var t = this + "";
        if (e || (e = 0), t.indexOf(".") == -1 && (t += "."), t += new Array(e + 1).join("0"), new RegExp("^(-|\\+)?(\\d+(\\.\\d{0," + (e + 1) + "})?)\\d*$").test(t)) {
            var t = "0" + RegExp.$2, a = RegExp.$1, s = RegExp.$3.length, i = !0;
            if (s == e + 2) {
                if (s = t.match(/\d/g), parseInt(s[s.length - 1]) > 4)for (var n = s.length - 2; n >= 0 && (s[n] = parseInt(s[n]) + 1, 10 == s[n]); n--)s[n] = 0, i = 1 != n;
                t = s.join("").replace(new RegExp("(\\d+)(\\d{" + e + "})\\d$"), "$1.$2")
            }
            return i && (t = t.substr(1)), (a + t).replace(/\.$/, "")
        }
        return this + ""
    }, UC_DOMAIN = window.UC_DOMAIN || "i.youku.com", API_DOMAIN_DEF = "www.youku.com", API_DOMAIN_Link = "http://lv.youku.com/page/grade/task", LINK_GRAGE_ICON = "http://cps.youku.com/redirect.html?id=000145df";
    var $ = function (e) {
        return document.getElementById(e)
    }, t = {
        targetURL: {
            vip: "http://cps.youku.com/redirect.html?id=000145de",
            lvip: "http://cps.youku.com/redirect.html?id=000145dd"
        },
        curpanel: "",
        userDefauleIcon: "http://static.youku.com/v1.0.1040/user/img/head/64/999.jpg",
        panel: {user: "qheader_username_panel", noticelist: "qheader_notice_info"},
        loading: '<div class="cd-hpanel-loading" id="hpanelloading"><span class="ico-loading-64"></span></div>',
        initHeaderUser: function () {
            jQuery("#" + t.panel.user).remove();
            var e = CHUDA.User.getUID();
            if (e) {
                window.USERINFO = void 0;
                var a = "http://lv.youku.com/api/grade/get_uinfo?callback=QheaderModule.upUserImg";
                CHUDA.Ajax.getScript(a, null, !0)
            }
        },
        renderUserInfo: function (e) {
            "20000" == e.code && (window.USERDATA = jQuery.extend(window.USERDATA, e.result));
            var a = window.USERDATA;
            $("qheader_userlog").className += " patch-user-info";
            var s = a.user_image ? a.user_image : t.userDefauleIcon, i = "";
            (a.is_vip || a.is_lvip) && 0 == a.vip_grade && (a.vip_grade = 1), 0 != a.vip_grade && a.icon && (i = '<span class="vip-level-icon" style="background:url(' + a.icon + ') no-repeat 0 0;height:22px;margin-top:1px;" title="' + a.name + "\uff1aVIP" + a.vip_grade + '" id="qheader_crown_icon" data-type="vip"></span>');
            var n = '<div class="yk-userlog-after-meta"><img class="yk-userlog-after-avatar" src="' + s + '"><span class="yk-userlog-after-name' + (a.is_vip || a.is_lvip ? " patch-red-name" : "") + '" title="' + a.user_name + '" id="qheaer_user_name">' + CHUDA.Common.cutStr(a.user_name, 10, null) + "</span>" + i + '<span class="user-grade-icon user-grade-lv' + a.grade + '" title="\u4f18\u9177\u7b49\u7ea7\uff1a' + a.grade + '" id="qheader_grade_icon"></span></div>';
            $("qheader_username_show").innerHTML = n, CHUDA.Common.bind($("qheaer_user_name"), "click", function () {
                return window.location.href = "http://user.youku.com/page/usc/index", !1
            });
            var r = this, o = $("qheader_crown_icon");
            void 0 != o && CHUDA.Common.bind(o, "click", function () {
                return window.location.href = r.targetURL[o.getAttribute("data-type")], !1
            }), CHUDA.Common.bind($("qheader_grade_icon"), "click", function () {
                return window.location.href = LINK_GRAGE_ICON, !1
            }), this.showUserMsgInit({
                init: !0,
                userIdEncode: "profile",
                isverified: !1,
                is_verify_email: 1,
                is_verify_mobile: !1
            })
        },
        upUserImg: function (e) {
            if ($("qheader_username")) {
                var t = null;
                if (void 0 != e.errno && e.errno === -300003504)return CHUDA.Cookie.remove("yktk"), void setTimeout(function () {
                    window.location.reload()
                }, 500);
                if (void 0 != e.errno && 0 === e.errno) t = e.data, window.localStorage && window.localStorage.setItem("USERDATA", CHUDA.Common.serialize(t)); else {
                    if (!window.localStorage || null == window.localStorage.getItem("USERDATA"))return;
                    t = CHUDA.Common.deserialize(window.localStorage.getItem("USERDATA"))
                }
                window.USERDATA = t;
                var a = "http://vip.youku.com/member/show_valid_member.jsonp?version=2&callback=QheaderModule.renderUserInfo";
                CHUDA.Ajax.getScript(a, null, !0)
            }
        },
        showUserMsg: function () {
            if ($(t.panel.user)) {
                t.curpanel = "user";
                var e = $(t.panel.user);
                if (!e)return !1;
                if (t.showsign(), window.USERZPD || t.showUserZpd(), void 0 == window.USERINFO) {
                    var a = "http://lvip.youku.com/api/user/get_user_info?uid=" + CHUDA.User.getUID() + "&access_token=" + encodeURIComponent(CHUDA.User.getYKToken()) + "&callback=QheaderModule.showUserMsgCallback";
                    CHUDA.Ajax.getScript(a, null, !0)
                } else t.showUserMsgInit(window.USERINFO)
            }
        },
        showsign: function () {
            var e = "http://actives.youku.com/task/show/user_is_sign?uid=" + CHUDA.User.getUID() + "&callback=QheaderModule.showsignCallback";
            CHUDA.Ajax.getScript(e, null, !0)
        },
        showsignCallback: function (e) {
            window.SIGNTAG = 0 === e.errno && 1 === e.data.is_sign ? 1 : 0
        },
        showUserMsgInit: function (e) {
            if ($(t.panel.user)) {
                $(t.panel.user).innerHTML = '<div class="cd-hpanel-ico ico-arrow-top-grey"></div>';
                var a = window.USERDATA, s = document.createElement("div");
                if (s.className = "cd-hpanel-user-info", CHUDA.User.getLoginStatus()) {
                    var i = a.user_image || t.userDefauleIcon, n = '<div class="cd-hpanel-user-avatar"><a href="http://' + UC_DOMAIN + "/u/" + e.userIdEncode + '" target="_blank"><img src="' + i + '"></a></div>', r = '<div class="cd-hpanel-user-profile"><div class="cd-hpanel-user-name"><a href="http://' + UC_DOMAIN + "/u/" + e.userIdEncode + '" target="_blank" title="' + a.user_name + '"' + (a.is_vip || a.is_lvip ? ' class="patch-red-name"' : "") + ">" + CHUDA.Common.cutStr(a.user_name, 12, "...") + "</a></div>";
                    r += '<div class="cd-hpanel-user-credit">', 0 != a.vip_grade && a.icon && (r += '<a href="' + this.targetURL.vip + '" target="_blank"><span class="vip-level-icon" style="background:url(' + a.icon + ') no-repeat 0 0;height:22px;margin-top:-3px;" title="' + a.name + "\uff1aVIP" + a.vip_grade + '"></span></a>'), r += '<a href="' + LINK_GRAGE_ICON + '" target="_blank"><span class="user-grade-icon user-grade-lv' + a.grade + '" title="\u4f18\u9177\u7b49\u7ea7\uff1a' + a.grade + '"></span></a>', e.isverified && (r += '<a href="http://' + UC_DOMAIN + '/u/rz" target="_blank"><i class="ico-cert" title="\u8ba4\u8bc1\u7528\u6237"></i></a>'), r += 0 != e.is_verify_email ? '<div class="cd-hpanel-user-valid cd-hpanel-user-valid-email"><a class="cd-hpanel-ico ico-valid-email" href="http://' + UC_DOMAIN + '/u/setting/sec_security/tab_mail.html" target="_blank"></a></div>' : '<div class="cd-hpanel-user-valid cd-hpanel-user-valid-email"><a class="cd-hpanel-ico ico-valid-email-done" href="http://' + UC_DOMAIN + '/u/setting/sec_security.html" target="_blank"></a></div>', 1 != e.is_verify_mobile ? (r += '<div class="cd-hpanel-user-valid cd-hpanel-user-valid-phone"><a class="cd-hpanel-ico ico-valid-phone" href="http://' + UC_DOMAIN + '/u/setting/sec_security/tab_mobile.html" target="_blank"></a>', e.init || (r += 0 == e.is_verify_email ? '<div class="qtips qtips_valid qtips_valid_email"><div class="cd-hpanel-ico ico-arrow-left"></div><div class="content"><span class="cd-hpanel-ico ico-min-close" onclick="this.parentNode.parentNode.style.display=\'none\'"></span><a href="http://' + UC_DOMAIN + '/u/setting/sec_security/tab_mobile.html" target="_blank">\u7ed1\u5b9a\u624b\u673a</a></div></div>' : '<div class="qtips qtips_valid qtips_valid_email"><div class="cd-hpanel-ico ico-arrow-left"></div><div class="content"><span class="cd-hpanel-ico ico-min-close" onclick="this.parentNode.parentNode.style.display=\'none\'"></span><a href="http://' + UC_DOMAIN + '/u/setting/sec_security/tab_mobile.html" target="_blank">\u53bb\u7ed1\u5b9a</a></div></div>'), r += "</div>") : r += '<div class="cd-hpanel-user-valid cd-hpanel-user-valid-phone-done"><a class="cd-hpanel-ico ico-valid-phone-done" href="http://' + UC_DOMAIN + '/u/setting/sec_security/tab_mobile.html" target="_blank"></a></div>', r += "</div>", r += '<div class="cd-hpanel-user-vip">', a.is_vip ? (r += '<div class="user-vip-item">' + a.tiny_name + "\u5269\u4f59<span>" + a.vip_last_day + '</span>\u5929</div><a class="user-vip-buy" href="http://cps.youku.com/redirect.html?id=00014639" target="_blank" title="\u7eed\u8d39' + a.tiny_name + '"><span>\u7eed\u8d39</span><i></i></a>', this.cpsStatistics("00014639")) : a.is_lvip ? (r += '<div class="user-vip-item">\u767d\u94f6\u4f1a\u5458\u5269\u4f59<span>' + a.lvip_last_day + '</span>\u5929</div><a class="user-vip-buy" href="http://cps.youku.com/redirect.html?id=0001463b" target="_blank" title="\u7eed\u8d39\u767d\u94f6\u4f1a\u5458"><span>\u7eed\u8d39</span><i></i></a>', this.cpsStatistics("0001463b")) : (r += '<div class="user-vip-item">\u52a0\u5165\u4f1a\u5458\u514d\u5e7f\u544a</div><a class="user-vip-buy" href="http://cps.youku.com/redirect.html?id=0001463a" target="_blank" title="\u5f00\u901a\u4f18\u9177\u571f\u8c46\u4f1a\u5458"><span>\u5f00\u901a</span><i></i></a>', this.cpsStatistics("0001463a")), r += "</div>", r += '<div class="cd-hpanel-user-extend"><a href="http://' + UC_DOMAIN + '/u/setting/base_profile.html" target="_blank">\u8bbe\u7f6e</a><span class="cd-hpanel-split">|</span><a href="javascript:;" onclick="setTimeout(function(){logout();},200);return false;">\u767b\u51fa</a></div></div>';
                    var o = '<div class="cd-hpanel-user-zpd" id="cdhpaneluserzpd"></div>', c = "";
                    s.innerHTML = n + r + o + c
                } else {
                    var l = '<div class="cd-hpanel-user-avatar"><a><img src="http://static.youku.com/index/img/toolbar/toolbar_avatar.jpg" alt=""></a></div><div class="cd-hpanel-user-profile"><div class="cd-hpanel-user-name">Hi, \u60a8\u597d\uff5e\uff5e</div><p>\u767b\u5f55\u53ef\u4ee5\u6512\u65f6\u957f\u73a9\u5347\u7ea7\u54e6~</p></div><div class="cd-hpanel-code"><div class="ykcode-flag" id="sidetoolqrcodepanelshow"><span></span></div><div class="ykcode-cover"><img class="ykcode-cover-img" src="http://static.youku.com/index/img/toolbar/codecover.png"></div></div>';
                    s.innerHTML = l
                }
                if ($(t.panel.user).appendChild(s), o && t.showUserZpdInit(), CHUDA.User.getLoginStatus()) {
                    var d = document.createElement("div");
                    d.className = "panel-user-grade";
                    var u = [];
                    Math.round(a.score / 6) / 10, Math.round(a.upgrade_score / 6) / 10;
                    if (u.push('<div class="panel-user-grade-title"><span>\u6211\u7684\u7b49\u7ea7</span><a class="panel-user-grade-reward-a" href="' + LINK_GRAGE_ICON + '" target="_blank">\u7acb\u5373\u9886\u53d6></a><div class="panel-user-grade-reward">\u4e13\u5c5e\u793c\u5305\uff0c</div></div>'), a.grade >= 100)var p = 99; else var p = a.grade;
                    u.push('<div class="panel-user-grade-line">'), u.push('<a class="panel-user-grade-left-new">' + p + '</a><a class="panel-user-grade-center-new"></a><a class="panel-user-grade-right-new">' + parseInt(p + 1) + "</a>"), u.push('<div class="panel-user-grade-time-new" id="panelusergradetimenew">');
                    var h = 223, g = Math.ceil((a.score - CHUDA.Common.shichang(p)) / (CHUDA.Common.shichang(p + 1) - CHUDA.Common.shichang(p)) * h);
                    (a.score / 60).toFixed(1) == (CHUDA.Common.shichang(p + 1) / 60).toFixed(1) && (g = 223);
                    var m = a.score - CHUDA.Common.shichang(p) <= 0 ? "" : a.score - CHUDA.Common.shichang(p) < 4 ? "0.1" : ((a.score / 60).toFixed(1) - (CHUDA.Common.shichang(p) / 60).toFixed(1)).toFixed(1);
                    u.push('<a target="_blank" href="' + LINK_GRAGE_ICON + '" class="panel-user-grade-left-new">' + p + "</a>"), a.score - CHUDA.Common.shichang(p) > 0 && (u.push('<a target="_blank" href="' + LINK_GRAGE_ICON + '" class="panel-user-grade-center-new" id="rank_todayDuraLine">' + m + "\u5c0f\u65f6</a>"), u.push('<a target="_blank" href="' + LINK_GRAGE_ICON + '" class="panel-user-grade-right-new"></a>')), u.push('<a target="_blank" href="' + LINK_GRAGE_ICON + '" class="panel-user-grade-next ' + (a.grade >= 100 ? "panel-user-grade-next-top" : "") + '">' + parseInt(p + 1) + "</a>"), u.push('<div class="panel-user-grade-line-hover" id="panelusergradelinehover"><div class="panel-user-grade-line-today"><i></i><em>\u4eca\u65e5\u5df2\u6210\u957f' + (a.today_score / 60).toFixed(1) + '\u5c0f\u65f6</em></div><div class="panel-user-grade-line-todaymore"><i></i><em>\u6700\u591a\u53ef\u518d\u589e\u52a0' + ((a.today_max_score * (a.is_vip ? 1.2 : a.is_lvip ? 1.1 : 1) / 60).toFixed(1) - (a.today_score / 60).toFixed(1)).toFixed(1) + "\u5c0f\u65f6</em></div></div>"), u.push("</div>"), u.push("</div>"), u.push('<div class="panel-user-grade-info-new">'), u.push('<a target="_blank" href="' + LINK_GRAGE_ICON + '" class="panel-user-grade-icon-new panel-grade-icon-nowlv">' + (CHUDA.Common.shichang(p) / 60).toFixed(1) + "\u5c0f\u65f6</a>"), u.push('<a target="_blank" href="' + LINK_GRAGE_ICON + '" class="panel-user-grade-icon-new panel-grade-icon-nextlv ' + (a.grade >= 100 ? "panel-grade-icon-nextlv-top" : "") + '">' + (CHUDA.Common.shichang(p + 1) / 60).toFixed(1) + "\u5c0f\u65f6</a>"), u.push("</div>"), d.innerHTML = u.join(""), $(t.panel.user).appendChild(d), !e.init && g > 0 && jQuery("#rank_todayDuraLine").animate({width: g + "px"}, "slow", "swing");
                    var v = document.createElement("div");
                    v.className = "cd-hpanel-u-task";
                    var _ = '<div class="u-task-title"><span>\u6211\u7684\u4efb\u52a1</span></div>';
                    v.innerHTML = _;
                    var f = document.createElement("div");
                    f.className = "u-task-list", f.setAttribute("id", "tasklistsidetool"), f.innerHTML = t.loading, v.appendChild(f), $(t.panel.user).appendChild(v), this.getUserTaskListNew();
                    var y = document.createElement("div");
                    y.className = "cd-hpanel-userservices", y.innerHTML = '<ul class="cd-userservices-list cd-userservices-list-topborder"><li class="u-service-chanel"><a href="http://' + UC_DOMAIN + "/u/" + e.userIdEncode + '" target="_blank"><i class="cd-hpanel-ico ico-myspace"></i>\u6211\u7684\u9891\u9053</a></li><li class="u-service-manage"><a href="http://' + UC_DOMAIN + '/u/creative_center" target="_blank"><i class="cd-hpanel-ico ico-videomanage"></i>\u521b\u4f5c\u4e2d\u5fc3</a></li><li class="u-service-favor"><a href="http://faxian.youku.com/" target="_blank"><i class="cd-hpanel-ico ico-yourfavor"></i>\u7cbe\u5f69\u53d1\u73b0</a></li><li class="u-service-subscribe"><a href="http://faxian.youku.com/favorite" target="_blank"><i class="cd-hpanel-ico ico-subscribe"></i>\u6211\u7684\u6536\u85cf</a></li></ul>', $(t.panel.user).appendChild(y)
                } else {
                    var k = document.createElement("div");
                    k.className = "cd-hpanel-oauth-login";
                    var C = '<button class="btn btn-large" href="jsvascript:;" onclick="login({type:\'toolbar\',callBack:\'QheaderModule.initHeaderUser\'});return false;">\u7acb\u5373\u767b\u5f55</button><a onclick="login({type:\'toolbar\',callBack:\'QheaderModule.initHeaderUser\'});return false;" class="cd-hpanel-ico ico-qq" title="QQ"><span>qq</span></a><a onclick="login({type:\'toolbar\',callBack:\'QheaderModule.initHeaderUser\'});return false;" class="cd-hpanel-ico ico-weibo" title="\u5fae\u535a"><span>\u5fae\u535a</span></a><a onclick="login({type:\'toolbar\',callBack:\'QheaderModule.initHeaderUser\'});return false;" class="cd-hpanel-ico ico-alipay" title="\u901a\u8fc7\u652f\u4ed8\u5b9d\u626b\u63cf\u53ef\u76f4\u63a5\u6ce8\u518c\u3001\u767b\u5f55\u4f18\u9177\u4e86"><span>\u652f\u4ed8\u5b9d</span></a><span class="extend">\u6ca1\u6709\u8d26\u53f7? <a href="https://account.youku.com/register.htm" target="_blank">\u514d\u8d39\u6ce8\u518c</a></span>';
                    k.innerHTML = C, $(t.panel.user).appendChild(k)
                }
                setTimeout(function () {
                    t.userMsgShowHover()
                }, 500), this.cpsStatistics("0001472a")
            }
        },
        showUserMsgCallback: function (e) {
            return void 0 != e.errno && e.errno === -300003504 ? (CHUDA.Cookie.remove("yktk"), void setTimeout(function () {
                    window.location.reload()
                }, 500)) : void(void 0 !== e.errno && 0 == e.errno && (window.USERINFO = e.data, this.showUserMsgInit(window.USERINFO)))
        },
        makeTasklistNewHtml: function (e) {
            var t = "";
            if (!e.web || e.web.length <= 0)return t = '<div class="u-task-meta u-task-meta-null"><p class="u-task-e-null-status">\u66f4\u591a\u7cbe\u5f69 \u656c\u8bf7\u671f\u5f85...</p></div>';
            for (var a = 0; a < e.web.length; a++) {
                var s = e.web[a];
                t += '<div class="sign1" style="overflow:hidden;margin-bottom:10px;position:relative;">', t += '<div class="s-img" style="float:left;width:234px;height:50px;"><img src="' + s.img + '" width="234" height="50"></div>', t += '<div class="s-link" style="position:absolute;right:0;width:54px;height:50px;" id="signLink">', t += '<a href="' + s.redirect_url + '" target="_blank" id="qheaderSignTag" style="display:block;width:54px;height:50px;"><img style="width:54px;height:50px;" src="' + ("number" == typeof window.SIGNTAG && 1 === window.SIGNTAG ? s.status_finish_img : s.status_unfinish_img) + '"></a>', t += "</div>", t += "</div>"
            }
            return t
        },
        makeTasklistHtml: function (e) {
            var t = "";
            if (!e.tasklist || e.tasklist.length <= 0)return t = '<div class="u-task-meta u-task-meta-null"><p class="u-task-e-null-status">\u66f4\u591a\u7cbe\u5f69 \u656c\u8bf7\u671f\u5f85...</p></div>';
            for (var a = !1, s = "", i = 0; i < e.tasklist.length; i++) {
                var n = "u-task-meta";
                2 == e.tasklist[i].status ? n = "u-task-meta u-task-meta-done" : 1 == e.tasklist[i].status && (n = "u-task-meta"), t += '<div class="' + n + '" id="task_list_' + e.tasklist[i].tid + '" name="tasklistidetool" tid="' + e.tasklist[i].tid + '" ttype="' + e.tasklist[i].type + '"><a class="u-task-m-link"><img src="' + e.tasklist[i].icon + '"><span class="u-task-link-icon-done"></span></a><div class="u-task-m-entry"><a class="u-task-m-e-title" title="' + e.tasklist[i].name + '">' + e.tasklist[i].name + '</a><span title="' + e.tasklist[i].desc + '">' + e.tasklist[i].desc + '</span><div class="u-task-m-e-reward"><span>\u5956\u52b1\uff1a</span>';
                for (var r = 0; r < e.tasklist[i].awards.length; r++)1 == e.tasklist[i].awards[r].type && 0 == a && (a = !0), e.tasklist[i].awards[r].detail && (t += '<span class="reward-entry">' + e.tasklist[i].awards[r].detail + "</span>");
                if (t += "</div></div>", 2 == e.tasklist[i].status) t += '<span class="u-task-m-status u-task-m-status-done" id="task_receive_' + e.tasklist[i].tid + '">\u9886\u5956\u52b1</span>'; else if (1 == e.tasklist[i].status)if (10 != e.tasklist[i].type) e.tasklist[i].progress && e.tasklist[i].progress.length > 0 ? t += "0/1" === e.tasklist[i].progress ? '<span class="u-task-m-status u-task-m-status-ing">\u8fdb\u884c\u4e2d\u2026</span>' : '<span class="u-task-m-status u-task-m-status-degree">' + e.tasklist[i].progress + "</span>" : (s = "", "string" == typeof e.tasklist[i].toUrl && e.tasklist[i].toUrl.length > 0 && (s = ' data-tourl="' + e.tasklist[i].toUrl + '"'), t += '<span class="u-task-m-status u-task-m-status-progress" id="toolbar_initiative_task_' + e.tasklist[i].tid + '"' + s + ">\u9886\u4efb\u52a1</span>"); else if (e.tasklist[i].special_task_type && 2 == e.tasklist[i].special_task_type) {
                    var o = e.tasklist[i].special_task_source ? e.tasklist[i].special_task_source : "http://www.youku.com";
                    t += '<span class="u-task-m-status u-task-m-status-progress"><a href="' + o + '" target="_blank">\u505a\u4efb\u52a1</a></span>'
                } else t += '<span class="u-task-m-status u-task-m-status-progress" id="do_task_' + e.tasklist[i].tid + '">\u505a\u4efb\u52a1</span>';
                t += "</div>"
            }
            if (a) {
                var c = window.USERDATA;
                t += c.is_vip ? '<div class="u-task-meta-hint"><i class="ico__vipsuper"></i><span>\u9ec4\u91d1\u4f1a\u5458\u53cc\u500d\u79ef\u5206</span></div>' : '<div class="u-task-meta-hint u-task-meta-hint-no"><i class="cd-hpanel-ico ico__vipsuper_no"></i><span>\u9ec4\u91d1\u4f1a\u5458\u53cc\u500d\u79ef\u5206</span></div>'
            }
            return t
        },
        userMsgShowHover: function () {
            $("sidetoolqrcodepanelshow") && ($("sidetoolqrcodepanelshow").onclick = function () {
                t.qrcodeShow()
            }), t.bindTastDivListEvent();
            var e = $("panelusergradetimenew");
            e && jQuery("#panelusergradetimenew").hover(function (e) {
                jQuery(e.target).hasClass("panel-user-grade-next") || jQuery(e.target).hasClass("panel-user-grade-left-new") || jQuery("#panelusergradelinehover").show()
            }, function () {
                jQuery("#panelusergradelinehover").hide()
            })
        },
        bindTastDivListEvent: function () {
            var e = t._getElementsByName("div", "tasklistidetool");
            if (e)for (var a = e.length, s = 0; s < a; s++) {
                var i = e[s].getAttribute("tid");
                !function (e) {
                    $("task_receive_" + e) && ($("task_receive_" + e).onclick = function () {
                        var t = "http://" + API_DOMAIN_DEF + "/index_QSideToolJSONP?function[]=userReceiveAward&callback[]=QheaderModule.userReceiveAwardCallback&tid=" + e;
                        CHUDA.Ajax.getScript(t, null, !0)
                    }), $("do_task_" + e) && ($("do_task_" + e).onclick = function () {
                        taskUpdateUserInfo(e, 0)
                    }), $("toolbar_initiative_task_" + e) && ($("toolbar_initiative_task_" + e).onclick = function () {
                        doInitiativeTask(e, 0)
                    })
                }(i)
            }
        },
        qrcodeShow: function () {
            var e = $(t.panel.user).select(".cd-hpanel-code")[0];
            qrcodeLogin.getObj("toolbar") && qrcodeLogin.getObj("toolbar").delInterval();
            var a = {
                size: 138, loginfrom: "toolbar", stfrom: "toolbar", hParentDom: e, isPageExist: function () {
                    return "user" == t.curpanel
                }, getQrcodeCallback: function () {
                    t.qrcodeShowAnimation()
                }, callback: function () {
                    window.toolbarQrloginObj.isQrloginSucc = !0, window.login_callback(), t.initHeaderUser()
                }
            };
            qrcodeLogin.init("toolbar", a), setTimeout(function () {
                window.toolbarQrloginObj = qrcodeLogin.getObj("toolbar")
            }, 500)
        },
        qrcodeShowAnimation: function () {
            setTimeout(function () {
                t.qrcodePanelShow.call($("sidetoolqrcodepanelshow"), t), setTimeout(function () {
                    $("sidetoolqrcodepanelclose") && ($("sidetoolqrcodepanelclose").onclick = function () {
                        t.qrcodePanelClose.call($("sidetoolqrcodepanelclose"), t), setTimeout(function () {
                            t.qrcodeHide()
                        }, 600)
                    })
                }, 100)
            }, 100)
        },
        qrcodeHide: function () {
            var e = $(t.panel.user).select(".cd-hpanel-code .ykcode-container")[0];
            e && e.remove(), qrcodeLogin.getObj("toolbar") && qrcodeLogin.getObj("toolbar").delInterval()
        },
        userReceiveAwardCallback: function (e) {
            e && e.ret && "OK" == e.ret && e.tid && $("task_list_" + e.tid) && t.userReceiveAward.call($("task_list_" + e.tid), t)
        },
        getUserTaskListNew: function () {
            if ($("tasklistsidetool")) {
                var e = "http://task.youku.com/task/task/get_config_v2?pl=web&callback=QheaderModule.getUserTaskListNewCallback";
                CHUDA.Ajax.getScript(e, null, !0)
            }
        },
        getUserTaskListNewCallback: function (e) {
            if (void 0 !== e.errno && 0 == e.errno) {
                var e = e.data;
                if (e.web && e.web.length > 0 && $("tasklistsidetool")) {
                    var a = t.makeTasklistNewHtml(e), s = jQuery("#tasklistsidetool");
                    s.html(a)
                } else $("tasklistsidetool") && ($("tasklistsidetool").innerHTML = '<div class="u-task-meta u-task-meta-null"><p class="u-task-e-null-status">\u66f4\u591a\u7cbe\u5f69 \u656c\u8bf7\u671f\u5f85...</p></div>')
            }
        },
        qrcodePanelShow: function (e) {
            var $ = jQuery, t = CHUDA.Common.getBrowser(), a = $(this).parent(), s = a.find(".ykcode-container"), i = a.find(".ykcode-cover"), n = i.find(".ykcode-cover-img"), r = 300;
            return $(this).hide(), s.css("display", "block"), t.ie && parseInt(t.ie) < 10 ? void a.css({
                    height: "330px",
                    width: "274px",
                    zIndex: "100"
                }) : (i.show(), void a.on("hover", function () {
                    return !1
                }).css({width: "32px", height: "32px", zIndex: "100", overflow: "hidden"}).animate({
                    width: "330px",
                    height: "274px"
                }, r, function () {
                    n.css({
                        transition: "transform 0.35s",
                        "-webkit-transition": "-webkit-transform 0.35s",
                        "-moz-transition": "-moz-transform 0.35s",
                        "-o-transition": "-o-transform 0.35s",
                        "-ms-transition": "-ms-transform 0.35s",
                        transform: "rotate(-55deg)",
                        "-webkit-transform": "rotate(-55deg)",
                        "-moz-transform": "rotate(-55deg)",
                        "-o-transform": "rotate(-55deg)",
                        "-ms-transform": "rotate(-55deg)"
                    }), i.delay(r + 350).hide(1)
                }))
        },
        qrcodePanelClose: function (e) {
            var $ = jQuery, t = CHUDA.Common.getBrowser(), a = $(this).parent().parent(), s = $(this).parent(), i = a.find(".ykcode-flag"), n = a.find(".ykcode-cover"), r = n.find(".ykcode-cover-img"), o = 300;
            return t.ie && parseInt(t.ie) < 10 ? (s.hide(), i.show(), void a.css({
                    height: "auto",
                    width: "auto",
                    overflow: ""
                })) : (n.show(), r.css("display"), r.css({
                    transition: "transform 0.25s",
                    "-webkit-transition": "-webkit-transform 0.25s",
                    "-moz-transition": "-moz-transform 0.25s",
                    "-o-transition": "-o-transform 0.25s",
                    "-ms-transition": "-ms-transform 0.25s",
                    transform: "rotate(0deg)",
                    "-webkit-transform": "rotate(0deg)",
                    "-moz-transform": "rotate(0deg)",
                    "-o-transform": "rotate(0deg)",
                    "-ms-transform": "rotate(0deg)"
                }), void a.delay(230).animate({width: "32px", height: "32px"}, o, function () {
                    s.hide(), n.hide(), i.show()
                }))
        },
        userReceiveAward: function (e) {
            var $ = jQuery, t = e, a = $(this).find("span.reward-entry"), s = ($(".cd-integral"), $('<div class="u-task-mask"><p>\u9886\u53d6\u6210\u529f</p></div>'));
            meta = $("<div class='task-reward-meta'></div>"), $.each(a, function (e) {
                $(a[e]).appendTo(meta)
            });
            var i = $(this).find("div.u-task-mask");
            i.length && i.remove(), $(this).fadeOut("fast", function () {
                $(this).addClass("u-task-meta-reward"), s.append(meta), $(this).append(s), $(this).removeClass("u-task-meta-done"), $(this).fadeIn(function () {
                    $(this).delay(1700).slideUp(300, function () {
                        $(this).remove(), t.getUserTaskListNew()
                    })
                })
            })
        },
        showUserZpd: function () {
            var e = "http://cvip.youku.com/zpd_api/get_medal_list?&callback=QheaderModule.showUserZpdCallback";
            CHUDA.Ajax.getScript(e, null, !0)
        },
        showUserZpdCallback: function (e) {
            return void 0 != e.errno && e.errno === -300003504 ? (CHUDA.Cookie.remove("yktk"), void setTimeout(function () {
                    window.location.reload()
                }, 500)) : void(void 0 !== e.errno && 0 == e.errno && 0 != e.data.length && (e.data = e.data.sort(function (e, t) {
                    return t.status ? 1 : -1
                }), window.USERZPD = e.data, this.showUserZpdInit(window.USERZPD)))
        },
        showUserZpdInit: function (e) {
            var t;
            if ((window.USERZPD || "" == e) && $("cdhpaneluserzpd") && (t = window.USERZPD), t && t.length > 0) {
                for (var a = t.length, s = '<div class="cd-hpanel-user-zpd-content"><ul class="cd-hpanel-user-zpd-list" id="cdhpaneluserzpdlist" style="width:' + 41 * a + 'px;left:0px;">', i = j = 0; j < a; j++)i = j, "\u7f57\u8f91\u601d\u7ef4\u9891\u9053" == t[i].name && (t[i].name = "\u7f57\u8f91\u601d\u7ef4"), s += '<li><a target="_blank" href="' + t[i].info_link + '/zpd" class="' + (t[i].status ? "cd-hpanel-user-zpd-follow" : "cd-hpanel-user-zpd-unfollow") + '" title="' + t[i].name + '\u9891\u9053\u4f1a\u5458"><img src="' + t[i].medal_icon_medium + '"></a></li>';
                s += "</ul>", a > 7 && (s += '</div><div class="cd-hpanel-user-zpd-nav"><a class="cd-hpanel-user-zpd-prv-none" id="cdhpaneluserzpdprv" title="\u4e0a\u4e00\u4e2a"></a><a class="cd-hpanel-user-zpd-pre" id="cdhpaneluserzpdpre" title="\u4e0b\u4e00\u4e2a"></a></div>'), $("cdhpaneluserzpd").style.height = "26px", $("cdhpaneluserzpd").innerHTML = s, a > 7 && ($("cdhpaneluserzpdprv").onclick = function () {
                    if ($("cdhpaneluserzpdprv").className.indexOf("cd-hpanel-user-zpd-prv-none") > -1); else {
                        var e = $("cdhpaneluserzpdlist").style ? parseInt($("cdhpaneluserzpdlist").style.left) : 0;
                        $("cdhpaneluserzpdlist").style.left = e + 41 + "px", $("cdhpaneluserzpdlist").style && 0 == parseInt($("cdhpaneluserzpdlist").style.left) ? ($("cdhpaneluserzpdpre").className = "cd-hpanel-user-zpd-pre", $("cdhpaneluserzpdprv").className = "cd-hpanel-user-zpd-prv cd-hpanel-user-zpd-prv-none") : $("cdhpaneluserzpdlist").style && parseInt($("cdhpaneluserzpdlist").style.left) < 0 && ($("cdhpaneluserzpdpre").className = "cd-hpanel-user-zpd-pre", $("cdhpaneluserzpdprv").className = "cd-hpanel-user-zpd-prv")
                    }
                }, $("cdhpaneluserzpdpre").onclick = function () {
                    if ($("cdhpaneluserzpdpre").className.indexOf("cd-hpanel-user-zpd-pre-none") > -1); else {
                        var e = $("cdhpaneluserzpdlist").style ? parseInt($("cdhpaneluserzpdlist").style.left) : 0;
                        $("cdhpaneluserzpdlist").style.left = e - 41 + "px", $("cdhpaneluserzpdlist").style && parseInt($("cdhpaneluserzpdlist").style.left) == 41 * (7 - t.length) ? ($("cdhpaneluserzpdpre").className = "cd-hpanel-user-zpd-pre cd-hpanel-user-zpd-pre-none", $("cdhpaneluserzpdprv").className = "cd-hpanel-user-zpd-prv") : parseInt($("cdhpaneluserzpdlist").style.left) > 41 * (7 - t.length) && ($("cdhpaneluserzpdpre").className = "cd-hpanel-user-zpd-pre", $("cdhpaneluserzpdprv").className = "cd-hpanel-user-zpd-prv")
                    }
                })
            }
        },
        _getElementsByName: function (e, t) {
            for (var a = [], s = document.getElementById("qheader_box"), i = s.getElementsByTagName(e), n = 0; n < i.length; n++)i[n].getAttribute("name") == t && (a[a.length] = i[n]);
            return a
        },
        MSGCENTER_LINK: "http://msg.youku.com/page/msg/index",
        MSGCENTER_API: "http://msg.youku.com/api/push/",
        noticePoll: function () {
            if (this.initNoticePannel(), this.resetNoticeTypeAndCount(), !this.timer) {
                this.updateNoticeNum();
                var e = this;
                this.timer = setInterval(function () {
                    e.updateNoticeNum()
                }, 3e4)
            }
        },
        initNoticePannel: function () {
            var e = document.getElementById("qheader_notice"), t = document.getElementById("qheader_notice_panel"), a = document.getElementById("qheader_notice_show"), s = document.getElementById("qheader_notice_num");
            e && t && (this.getChudaMessage(), s && (s.className = "chuda_qheader_spot"), a && (a.getElementsByTagName("a")[0].href = this.MSGCENTER_LINK), t.innerHTML = '<div class="chuda_panel_arrow"></div><div id="qheader_notice_info" class="chuda_notice_box"></div>')
        },
        updateNoticeNum: function () {
            var e = CHUDA.User.getUID();
            e && CHUDA.Ajax.getScript("http://notify.youku.com/notify/get.json?uid=" + e + '&types=["umc_notice","mvip.msg.state"]&callback=QheaderModule.updateNoticeNumCallback', null, !0)
        },
        resetNoticeTypeAndCount: function () {
            this.noticeCount = 0, this.noticeType = {
                privateMsg: {
                    notify: "umc_notice",
                    category: 3,
                    type: "private_msg",
                    parentCat: "privateMsg",
                    subCats: [6]
                },
                comments: {
                    notify: "umc_notice",
                    category: 4,
                    type: "comments",
                    parentCat: "privateMsg",
                    subCats: [7, 8, 9]
                },
                act_notice: {
                    notify: "mvip.msg.state",
                    category: 2,
                    type: "act_notice",
                    parentCat: "activityMsg",
                    subCats: [2]
                },
                renewal: {notify: "mvip.msg.state", category: 3, type: "renewal", parentCat: "systemMsg", subCats: [3]},
                view_coupons: {
                    notify: "mvip.msg.state",
                    category: 1e3,
                    type: "view_coupons",
                    parentCat: "systemMsg",
                    subCats: [1e3]
                },
                grow_up: {
                    notify: "mvip.msg.state",
                    category: 1001,
                    type: "grow_up",
                    parentCat: "systemMsg",
                    subCats: [1001]
                },
                video_publish: {
                    notify: "mvip.msg.state",
                    category: 1004,
                    type: "video_publish",
                    parentCat: "systemMsg",
                    subCats: [1004]
                },
                self_channel: {
                    notify: "mvip.msg.state",
                    category: 1002,
                    type: "self_channel",
                    parentCat: "systemMsg",
                    subCats: [1002]
                },
                level_up: {
                    notify: "mvip.msg.state",
                    category: 1009,
                    type: "level_up",
                    parentCat: "systemMsg",
                    subCats: [1009]
                },
                task_credits: {
                    notify: "mvip.msg.state",
                    category: 1010,
                    type: "task_credits",
                    parentCat: "systemMsg",
                    subCats: [1010]
                },
                weiku_zhibo: {
                    notify: "mvip.msg.state",
                    category: 1011,
                    type: "weiku_zhibo",
                    parentCat: "systemMsg",
                    subCats: [1011]
                },
                bofangliang: {
                    notify: "mvip.msg.state",
                    category: 1012,
                    type: "bofangliang",
                    parentCat: "systemMsg",
                    subCats: [1012]
                },
                crowdfunding: {
                    notify: "mvip.msg.state",
                    category: 1013,
                    type: "crowdfunding",
                    parentCat: "systemMsg",
                    subCats: [1013]
                },
                account_msg: {
                    notify: "mvip.msg.state",
                    category: 1014,
                    type: "account_msg",
                    parentCat: "systemMsg",
                    subCats: [1014]
                },
                topicnew_msg: {
                    notify: "mvip.msg.state",
                    category: 1015,
                    type: "topicnew_msg",
                    parentCat: "systemMsg",
                    subCats: [1015]
                },
                sys_ad: {
                    notify: "mvip.msg.state",
                    category: 2001,
                    type: "sys_ad",
                    parentCat: "systemMsg",
                    subCats: [2001]
                },
                sys_important: {
                    notify: "mvip.msg.state",
                    category: 2002,
                    type: "sys_important",
                    parentCat: "systemMsg",
                    subCats: [2002]
                },
                zpd_msg: {
                    notify: "mvip.msg.state",
                    category: 3002,
                    type: "zpd_msg",
                    parentCat: "systemMsg",
                    subCats: [3002]
                },
                nryypush_msg: {
                    notify: "mvip.msg.state",
                    category: 3003,
                    type: "nryypush_msg",
                    parentCat: "systemMsg",
                    subCats: [3003]
                },
                id_msg: {
                    notify: "mvip.msg.state",
                    category: 3004,
                    type: "id_msg",
                    parentCat: "systemMsg",
                    subCats: [3004]
                },
                loginerr_msg: {
                    notify: "mvip.msg.state",
                    category: 3011,
                    type: "loginerr_msg",
                    parentCat: "systemMsg",
                    subCats: [3011]
                },
                core_msg: {
                    notify: "mvip.msg.state",
                    category: 3012,
                    type: "core_msg",
                    parentCat: "systemMsg",
                    subCats: [3012]
                },
                videopass_msg: {
                    notify: "mvip.msg.state",
                    category: 3101,
                    type: "videopass_msg",
                    parentCat: "systemMsg",
                    subCats: [3101]
                },
                zpdfinance_msg: {
                    notify: "mvip.msg.state",
                    category: 3201,
                    type: "zpdfinance_msg",
                    parentCat: "systemMsg",
                    subCats: [3201]
                },
                topic_msg: {
                    notify: "mvip.msg.state",
                    category: 3202,
                    type: "topic_msg",
                    parentCat: "systemMsg",
                    subCats: [3202]
                },
                actives_msg: {
                    notify: "mvip.msg.state",
                    category: 4001,
                    type: "actives_msg",
                    parentCat: "systemMsg",
                    subCats: [4001]
                },
                live_msg: {
                    notify: "mvip.msg.state",
                    category: 4002,
                    type: "live_msg",
                    parentCat: "systemMsg",
                    subCats: [4002]
                },
                holiday_msg: {
                    notify: "mvip.msg.state",
                    category: 4003,
                    type: "holiday_msg",
                    parentCat: "systemMsg",
                    subCats: [4003]
                }
            }
        },
        updateNoticeNumCallback: function (e) {
            if (e && e.notify) {
                this.resetNoticeTypeAndCount();
                var t = 0;
                if (e.notify.umc_notice) {
                    var a = e.notify.umc_notice[0];
                    for (var s in this.noticeType)if ("umc_notice" == this.noticeType[s].notify) {
                        var i = this.noticeType[s].category, n = a[i] || 0;
                        this.noticeCount += n
                    }
                    t = a[1] || 0
                }
                if (e.notify["mvip.msg.state"]) {
                    var a = e.notify["mvip.msg.state"][0];
                    for (var s in this.noticeType)if ("mvip.msg.state" == this.noticeType[s].notify) {
                        var i = this.noticeType[s].category, n = a[i] || 0;
                        this.noticeCount += n
                    }
                }
                this.initNoticeSpot(), this.subscribeCount = t, this.initSubscribeSpot()
            }
        },
        initSubscribeSpot: function () {
            var e = document.getElementById("subscribe_lsidetooln_flag"), t = document.getElementById("subscribe_lsidetoolw_flag");
            this.subscribeCount > 0 ? (e && (e.innerHTML = this.subscribeCount > 50 ? "..." : this.subscribeCount, e.className = this.subscribeCount > 50 ? "sub-update-num sub-update-nonum" : "sub-update-num", e.style.display = "block"), t && (t.innerHTML = this.subscribeCount > 50 ? "..." : this.subscribeCount, t.className = this.subscribeCount > 50 ? "sub-update-num sub-update-nonum" : "sub-update-num", t.style.display = "block")) : (e && (e.style.display = "none"), t && (e.style.display = "none"))
        },
        initNoticeSpot: function () {
            return document.getElementById("qheader_notice_num") && (document.getElementById("qheader_notice_num").style.display = this.noticeCount > 0 ? "block" : "none"), document.getElementById("chuda_notice_clear") && (document.getElementById("chuda_notice_clear").style.display = this.noticeCount > 0 ? "block" : "none"), this.noticeCount
        },
        showNoticeList: function () {
            this.curpanel = "notice";
            var e = document.getElementById("qheader_notice_info");
            if (e)if (e.innerHTML = "", CHUDA.User.getLoginStatus()) {
                var t = !1;
                t = void 0 != window.USERDATA ? window.USERDATA.is_vip || window.USERDATA.is_lvip : CHUDA.User.getisVIP(), e.parentNode.className = "chuda_notice_panel";
                var a = [];
                a.push('<div class="chuda_notice_bd">'), a.push('<div class="chuda_notice_inner">' + (0 === this.noticeCount, '<div class="chuda_notice_loading"></div>') + "</div>"), a.push("</div>"), a.push('<div class="chuda_notice_ft"><a href="javascript:void(0)" class="chuda_notice_clear" id="chuda_notice_clear">\u5168\u90e8\u6807\u8bb0\u5df2\u8bfb</a><a class="chuda_notice_rightlink" href="' + this.MSGCENTER_LINK + '" target="_blank" style="display: none;">\u67e5\u770b\u5168\u90e8\u6d88\u606f></a></div>'), e.innerHTML = a.join(""), CHUDA.Ajax.getScript(this.MSGCENTER_API + "get_msg_box_list?" + this.getApiSignature() + "&uid=" + CHUDA.User.getUID() + "&page=1&page_size=12&callback=QheaderModule.showNoticeListCallback", null, !0), this.cpsStatistics("0001472b")
            } else {
                e.parentNode.className = "chuda_notice_panel chuda_notice_notlogin";
                var s = '<div class="chuda_notice_notlogin_hd"><h3 class="chuda_notice_notlogin_title">\u6211\u7684\u901a\u77e5</h3></div>';
                s += '<div class="chuda_notice_notlogin_bd"><div class="chuda_notice_notlogin_tips"><span class="chuda_notice_notlogin_icon"></span><p>\u767b\u5f55\u540e\u53ef\u4ee5\u770b\u5230\u6d77\u91cf\u4fe1\u606f</p><p>\u53c2\u4e0e\u7cbe\u5f69\u6d3b\u52a8\uff5e</p></div>', s += '<a class="chuda_notice_notlogin_btn" href="javscript:;" onclick="login({type:\'toolbar\',callBack:\'QheaderModule.initHeaderUser\'});return false;">\u9a6c\u4e0a\u767b\u5f55</a></div>', e.innerHTML = s
            }
        },
        showNoticeListCallback: function (e) {
            var t = this, a = 3, s = 4;
            if (void 0 === e.errno || 0 != e.errno)return jQuery(".chuda_notice_loading").html('<div class="chuda_notice_tips">\u83b7\u53d6\u6570\u636e\u8d85\u65f6\uff0c<a href="javascript:void(0)" class="chuda_notice_reload">\u91cd\u65b0\u52a0\u8f7d</a></div>'), void jQuery(".chuda_notice_reload").one("click", function () {
                t.showNoticeList()
            });
            var i = e.data.msg_list, n = jQuery("#qheader_notice_info .chuda_notice_inner"), r = [];
            for (var o in i) {
                var c = i[o], l = "notice_page_";
                c.type && (o = parseInt(o) + 1, l += Math.ceil(o / s), o > a * s || (new RegExp(/^[A-Za-z]+:\/\/[A-Za-z0-9-_]+.[A-Za-z0-9-_%&\?\/.:=]+$/).test(c.content.link) || (c.content.link = ""), r.push('<li data-msgid="' + c.msgid + '" data-catid="' + c.type + '" class="notice_juhe ' + (2 == c.status ? "notice_visited " : "") + l + '"><div class="notice_img"><img src="' + c.fromuid.img + '"></div><div class="notice_node"><div class="notice_juhe_title">' + c.fromuid.name + '<span class="notice_difftime">' + t._dateDiff(1e3 * c.access_time) + '</span></div><a title="\u67e5\u770b\u8be6\u60c5" target="_blank" href="' + c.content.link + '" class="notice_go"><span title="' + c.content.ifo.replace(/"/g, "'") + '">' + CHUDA.Common.cutStr(c.content.ifo.replace(/"/g, "'"), 36, "...") + "</span></a>" + (1 == c.juhe ? "" : '<a title="\u5220\u9664" class="notice_del"><i class="notice_del_i"></i></a>') + "</div></li>")))
            }
            var d = jQuery("#notice_page .on").html() ? parseInt(jQuery("#notice_page .on").html()) : 1;
            if (n.eq(0).html(r.length ? '<ul class="chuda_notice_list">' + r.join("") + "</ul>" + (e.data.total_pages > 1 ? '<div id="notice_page"></div>' : "") : '<div class="chuda_notice_empty"></div>').show(), e.data.total_pages > 1) {
                e.data.total_pages > 3 && (e.data.total_pages = 3);
                var u = function (e) {
                    var t = "notice_page_";
                    jQuery(".chuda_notice_list .notice_juhe").hide(), jQuery('<div class="chuda_notice_loading"></div>').insertBefore(jQuery(".chuda_notice_list")), setTimeout(function () {
                        jQuery(".chuda_notice_loading").remove(), jQuery(".chuda_notice_list ." + t + e).show()
                    }, 500)
                };
                u(1), t.setPage(document.getElementById("notice_page"), e.data.total_pages, d, u)
            }
            t.noticeCount > 0 ? (jQuery(".chuda_notice_clear").show(), t.initNoticeSpot()) : (jQuery(".chuda_notice_clear").hide(), t.noticeCount = 0, t.initNoticeSpot()), r.length > 4 && jQuery(".chuda_notice_rightlink").show(), jQuery(".chuda_notice_clear").click(function () {
                t.updateAllmsgs()
            }), jQuery(".chuda_notice_list li").hover(function () {
                jQuery(this).addClass("notice_hover")
            }, function () {
                jQuery(this).removeClass("notice_hover")
            }).one("click", function () {
                jQuery(this).hasClass("notice_visited") || (jQuery(this).addClass("notice_visited"), t.resetNoticeNum(jQuery(this).attr("data-msgid"), jQuery(this).attr("data-catid")), t.noticeCount -= 1, t.initNoticeSpot(), jQuery(this).parent().children("li.notice_visited").length === s && setTimeout(function () {
                    t.showNoticeList()
                }, 2e3))
            }), jQuery(".chuda_notice_list .notice_go").each(function () {
                jQuery(this).click(function (e) {
                    jQuery(this).parent().parent().hasClass("notice_visited") || (jQuery(this).parent().parent().addClass("notice_visited"), t.noticeCount -= 1, t.initNoticeSpot(), t.resetNoticeNum(jQuery(this).parent().parent().attr("data-msgid"), jQuery(this).parent().parent().attr("data-catid")));
                    var a = jQuery(this).attr("href");
                    if (e.stopPropagation(), "" == a)return !1
                })
            }), jQuery(".chuda_notice_list .notice_del").each(function () {
                jQuery(this).click(function (e) {
                    var a = jQuery(this).parent().parent(), s = CHUDA.User.getUID(), i = a.attr("data-msgid"), n = "http://msg.youku.com/api/push/deletemsg?callback=QheaderModule.deletemsgCallback&" + t.getApiSignature() + "&uid=" + s + "&msgid=" + i;
                    CHUDA.Ajax.getScript(n, null, !0), e.stopPropagation()
                })
            })
        },
        resetNoticeNum: function (e, t) {
            if (CHUDA.User.getUID() && t) {
                var a = new Image;
                a.src = this.MSGCENTER_API + "readcomfirm_msg?" + this.getApiSignature() + "&msgid=" + e + "&type=" + t
            }
        },
        updateAllmsgs: function () {
            var e = "http://msg.youku.com/api/push/update_all_status?callback=QheaderModule.updateAllmsgsCallback&" + this.getApiSignature() + "&uid=" + CHUDA.User.getUID() + "&status=2";
            CHUDA.Ajax.getScript(e, null, !0)
        },
        updateAllmsgsCallback: function (e) {
            0 == e.errno && (jQuery(".chuda_notice_list li").addClass("notice_visited"), this.noticeCount = 0, this.initNoticeSpot(), jQuery(".chuda_notice_clear").hide())
        },
        getApiSignature: function () {
            var e = Math.round((new Date).getTime() / 1e3), t = CHUDA.User.getYKToken(), a = CHUDA.User.getUID(), s = "1001";
            return "appid=" + s + "&ts=" + e + "&uid=" + a + "&access_token=" + t + "&sig=" + CHUDA.Common.Md5([s, e, a, decodeURIComponent(t)].join(""))
        },
        getChudaMessage: function () {
            var e = CHUDA.User.getLoginStatus() ? this.MSGCENTER_API + "getpopupmsg?" + this.getApiSignature() + "&page_no=1&callback=QheaderModule.getChudaMessageCallback" : "http://static.youku.com/lvip/msg/chuda_message.js";
            CHUDA.Ajax.getScript(e, null, !0)
        },
        getChudaMessageCallback: function (e) {
            if (void 0 !== e.errno && 0 == e.errno && e.data && e.data.msgs && e.data.msgs.length > 0) {
                Math.round((new Date).getTime() / 1e3);
                for (i = 0; i < e.data.msgs.length; i++) {
                    var t = e.data.msgs[i];
                    if (this.showChudaMessage(t, !0))break
                }
            }
        },
        initUnloginMessage: function (e) {
            if (e && e.length > 0) {
                var t = Math.round((new Date).getTime() / 1e3);
                for (i = 0; i < e.length; i++)if (("undefined" == typeof e[i].access_time || isNaN(parseInt(e[i].access_time)) || !(t < parseInt(e[i].access_time))) && ("undefined" == typeof e[i].expire_time || isNaN(parseInt(e[i].expire_time)) || !(t > parseInt(e[i].expire_time)))) {
                    var a = "msgid_" + e[i].msgid;
                    if (!CHUDA.Cookie.get(a) && this.showChudaMessage(e[i], !1)) {
                        CHUDA.Cookie.set(a, e[i].msgid, 1);
                        break
                    }
                }
            }
        },
        showChudaMessage: function (e, t) {
            if ("undefined" != typeof e.status && 0 !== e.status)return !1;
            var a = document.getElementById("qheader_msg_panel");
            if (a)return !1;
            var s = document.getElementById("qheader_notice");
            if (!s)return !1;
            a = document.createElement("div"), a.id = "qheader_msg_panel", a.className = "chuda_msg_panel";
            var i = '<div class="chuda_panel_arrow"></div><div class="chuda_msg_box" id="qheader_msg_box">', n = this;
            try {
                i += 1007 == e.type ? '<div class="chuda_msg_hd chuda_msg_hd_new"><div class="chuda_msg_icon"><img src="' + e.fromuid.img.replace(/</g, "&lt;").replace(/>/g, "&gt;") + '"><span>' + e.fromuid.name.replace(/</g, "&lt;").replace(/>/g, "&gt;") + '</span></div><h3 class="chuda_msg_title_new">' + e.content.title.replace(/</g, "&lt;").replace(/>/g, "&gt;") + "</h3></div>" : '<div class="chuda_msg_hd"><h3 class="chuda_msg_title">' + e.content.title.replace(/</g, "&lt;").replace(/>/g, "&gt;") + "</h3></div>", i += '<div class="chuda_msg_bd"><div class="chuda_msg_info">' + e.content.ifo.replace(/</g, "&lt;").replace(/>/g, "&gt;") + "</div>", e.content.btnname && (i += '<a class="chuda_msg_btn" href="' + (e.content.link && e.content.link.indexOf("http") >= 0 ? e.content.link + '" target="_blank"' : "javascript:void(0)") + '" id="qheader_msg_btn">' + e.content.btnname + "</a>"), i += '<a class="chuda_msg_close" href="#closeMsgBox" onclick="jQuery(\'#qheader_msg_panel\').remove(); return false;">\u5173\u95ed</a></div>'
            } catch (e) {
            }
            i += "</div>";
            var r = this.MSGCENTER_API + "updatemsgstatus?" + this.getApiSignature() + "&msgid=" + e.msgid + "&status=";
            if (t) {
                var o = !1;
                void 0 != window.USERDATA ? o = window.USERDATA.is_vip || window.USERDATA.is_lvip : CHUDA.User.getLoginStatus() && (o = CHUDA.User.getisVIP()), o && 1007 != e.type && 1006 != e.type && (a.className = "chuda_msg_panel chuda_msg_vip");
                var c = new Image;
                c.src = r + "1"
            } else if (e.msgid) {
                var l = new Image;
                l.src = "http://msg.youku.com/api/push/read_unlogin_msg?msgid=" + e.msgid
            }
            a.innerHTML = i, s.parentNode.appendChild(a), t && document.getElementById("qheader_msg_btn") && (document.getElementById("qheader_msg_btn").onclick = function () {
                var e = document.getElementById("qheader_msg_panel");
                e && e.remove(), n.updateChudaMessage(r + 2)
            }), !t && document.getElementById("qheader_msg_btn") && e.msgid && (document.getElementById("qheader_msg_btn").onclick = function () {
                var t = document.getElementById("qheader_msg_panel");
                t && t.remove();
                var a = new Image;
                a.src = "http://msg.youku.com/api/push/click_unlogin_msg?msgid=" + e.msgid
            }), !isNaN(parseInt(e.fade)) && parseInt(e.fade) > 0 && setTimeout(function () {
                var e = document.getElementById("qheader_msg_panel");
                e && e.remove()
            }, 1e3 * parseInt(e.fade));
            try {
                var d = "cps.youku.com/redirect.html?id=";
                if (e.content && e.content.link && e.content.link.indexOf(d) > 0) {
                    var u = e.content.link.substr(e.content.link.indexOf(d) + d.length, 8);
                    this.cpsStatistics(u)
                }
            } catch (e) {
            }
            return !0
        },
        cpsStatistics: function (e) {
            if (e) {
                var t = new Image;
                t.src = "http://p.l.youku.com/vippay?lt=2&uri=" + e + "&ext="
            }
        },
        setPage: function (e, t, a, s) {
            function i() {
                if (a == o)var e = '<a href="#" class="on">' + o; else var e = '<a href="#">' + o;
                o < t ? n[n.length] = e + ".</a>" : o == t && (n[n.length] = e + "</a>")
            }

            var e = e, t = t, a = a, s = s, n = [], r = this;
            if (t <= 6)for (var o = 1; o <= t; o++)i();
            e.innerHTML = n.join("");
            (function () {
                for (var i = e.getElementsByTagName("a"), n = a, o = 0; o < i.length; o++)i[o].onclick = function () {
                    return n = parseInt(this.innerHTML), s && s(n), r.setPage(e, t, n, s), !1
                }
            })()
        },
        deletemsgCallback: function (e) {
            void 0 !== e.errno && 0 == e.errno && e.data && e.data.msgid && (this.noticeCount -= 1, this.initNoticeSpot(), jQuery("#notice_page a").removeClass("on"), CHUDA.Ajax.getScript(this.MSGCENTER_API + "get_msg_box_list?" + this.getApiSignature() + "&uid=" + CHUDA.User.getUID() + "&page=1&page_size=12&callback=QheaderModule.showNoticeListCallback", null, !0))
        },
        _dateDiff: function (e) {
            var t = 6e4, a = 60 * t, s = 24 * a, i = 30 * s, n = (new Date).getTime(), r = n - e, o = r / i, c = r / (7 * s), l = r / s, d = r / a, u = r / t;
            return o >= 1 ? result = parseInt(o) + "\u4e2a\u6708\u524d" : c >= 1 ? result = parseInt(c) + "\u5468\u524d" : l >= 1 ? result = parseInt(l) + "\u5929\u524d" : d >= 1 ? result = parseInt(d) + "\u5c0f\u65f6\u524d" : u >= 1 ? result = parseInt(u) + "\u5206\u949f\u524d" : result = "\u521a\u521a", result
        },
        updateChudaMessage: function (e) {
            var t = e + "&callback=QheaderModule.updateChudaMessageCallback";
            CHUDA.Ajax.getScript(t, null, !0)
        },
        updateChudaMessageCallback: function (e) {
            void 0 !== e.errno && 0 == e.errno && e.data && e.data.msgid && this.updateNoticeNum()
        }
    }, a = "http://static.youku.com/lvip/css/chuda_qheader.css", s = document.createElement("link");
    s.setAttribute("rel", "stylesheet"), s.setAttribute("type", "text/css"), s.setAttribute("href", a), document.getElementsByTagName("head")[0].appendChild(s), window.QheaderModule = t
}();