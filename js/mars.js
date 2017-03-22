/**
 * Created by kris on 2017/3/23.
 */
/*! mars-1.0.1 2017-03-13 02:03:54 */
!function () {
    if (!window.Mar) {
        window.T0 = (new Date).getTime(), window.Mar = function () {
        }, Mar.Cookie = {
            get: function (a) {
                var b, c = document.cookie, d = c.indexOf(a + "=");
                if (d !== -1)return d += a.length + 1, b = c.indexOf(";", d), unescape(c.substring(d, b === -1 ? c.length : b))
            }, set: function (a, b, c) {
                var d = Mar.Util.getDomain();
                0 === c ? document.cookie = a + "=" + escape(b) + ";path=/;domain=." + d : (date = new Date, date.setTime(date.getTime() + 24 * c * 3600 * 1e3), document.cookie = a + "=" + escape(b) + ";expires=" + date.toGMTString() + ";path=/;domain=." + d)
            }, del: function (a, b) {
                var b = Mar.Util.getDomain();
                document.cookie = a + "=; expires=Mon, 26 Jul 1997 05:00:00 GMT; path=/;" + (b ? "domain=" + b : "")
            }
        }, Mar.guid = function () {
            for (var a = 0, b = []; a < 8;)b.push((65536 * (1 + Math.random()) | 0).toString(16).substring(1)), a++;
            return b.join("").toUpperCase()
        }, Mar.guid2 = function () {
            function a() {
                return (65536 * (1 + Math.random()) | 0).toString(16).substring(1)
            }

            return a() + a() + "-" + a() + "-" + a() + "-" + a() + "-" + a() + a() + a()
        }, Mar.guid3 = function () {
            function a(a, c, d) {
                var e = b(a, 16), f = new Array, g = "", h = 0;
                for (h = 0; h < e.length; h++)f.push(e.substring(h, h + 1));
                for (h = Math.floor(c / 4); h <= Math.floor(d / 4); h++)g += f[h] && "" != f[h] ? f[h] : "0";
                return g
            }

            function b(a, b) {
                return a.toString(b)
            }

            function c(a) {
                return Math.floor(Math.random() * (a + 1))
            }

            var d = new Date(1582, 10, 15, 0, 0, 0, 0), e = new Date, f = e.getTime() - d.getTime(), g = a(f, 0, 31), h = a(f, 32, 47), i = a(f, 48, 59) + "1", j = a(c(4095), 0, 7), k = a(c(4095), 0, 7), l = a(c(8191), 0, 7) + a(c(8191), 8, 15) + a(c(8191), 0, 7) + a(c(8191), 8, 15) + a(c(8191), 0, 15);
            return g + h + i + j + k + l
        }, Mar.rand = function (a) {
            var b = "0123456789abcdef", c = "", d = 0;
            for (a = a || 32; d < a; d++)c += b.charAt(Math.ceil(1e8 * Math.random()) % b.length);
            return c
        }, Mar.IE = function () {
            for (var a = 3, b = document.createElement("DIV"), c = b.getElementsByTagName("I"); b.innerHTML = "<!--[if gt IE " + ++a + "]><i></i><![endif]-->", c[0];);
            return a > 4 ? a : 0
        }, Mar.browser = function () {
            var a = navigator.userAgent.toLowerCase(), b = Mar.IE();
            return {
                ie: b && ["ie", b],
                firefox: a.match(/firefox\/([\d.]+)/),
                chrome: a.match(/chrome\/([\d.]+)/),
                opera: a.match(/opera.([\d.]+)/),
                safari: window.openDatabase ? a.match(/version\/([\d.]+)/) : void 0
            }
        }, Mar.DOMReady = function (a) {
            document.addEventListener ? document.addEventListener("DOMContentLoaded", function () {
                    console.log("DOMContentLoaded"), a()
                }, !1) : document.attachEvent && document.attachEvent("onreadystatechange", function () {
                    "complete" == document.readyState && (document.detachEvent("onreadystatechange", arguments.callee), a())
                })
        }, Mar.protocal = function () {
            return document.location.href.toLowerCase().indexOf("https://") !== -1 ? "https://" : "http://"
        }, Mar.stringify = function (a) {
            if (window.JSON && window.JSON.stringify)return window.JSON.stringify(a);
            var b, c, d, e = [], f = typeof a, g = 0;
            if ("string" === f)return '"' + a + '"';
            if ("undefined" === f || "boolean" === f || "number" === f || null === a)return a;
            if ("[object Array]" == Object.prototype.toString.call(a)) {
                for (b = a.length, e.push("["); g < b; g++)e.push(Mar.stringify(a[g]) + ",");
                e.push("]")
            } else {
                e.push("{");
                for (c in a)a.hasOwnProperty(c) && e.push('"' + c + '":' + Mar.stringify(a[c]) + ",");
                d = e.length - 1, e[d] = e[d].replace(/,$/, ""), e.push("}")
            }
            return e.join("")
        };
        var a;
        Mar.Base = {}, a = Mar.Base, a.local = document.location, a.domain = document.domain, a.docEle = document.documentElement, a.context = "css1compat" === document.compatMode.toLowerCase() ? document.body : a.docEle, a.monitor = window.screen, a.href = a.local.href, a.url = escape(a.href), a.pn = a.local.pathname.toLowerCase(), a.hn = a.local.hostname.toLowerCase(), a.ref = escape(document.referrer), a.cw = a.docEle.clientWidth, a.ch = a.docEle.clientHeight, a.res = a.monitor.width + "*" + a.monitor.height, a.col = a.monitor.colorDepth, a.w = a.context.width, a.h = a.context.height, a.nav = escape(navigator.userAgent.toLowerCase()), a.ce = navigator.cookieEnabled ? 1 : 0, a.title = escape(document.title)
    }
}(), Mar.Util = {
    pad: function (a, b) {
        for (var c = a.toString().length; c < b;)a = "0" + a, c++;
        return a
    }, getDomain: function () {
        var a, b = document.domain.toLowerCase();
        return a = b.indexOf("vipshop.com") !== -1 ? "vipshop.com" : b.indexOf("appvipshop.com") !== -1 ? "appvipshop.com" : b.indexOf("vipglobal.hk") !== -1 ? "vipglobal.hk" : "vip.com"
    }, parseURL: function (a) {
        var b = document.createElement("a");
        return b.href = a, {
            source: a,
            protocol: b.protocol.replace(":", ""),
            host: b.hostname,
            port: b.port,
            query: b.search,
            params: function () {
                for (var a, c = {}, d = b.search.replace(/^\?/, "").split("&"), e = d.length, f = 0; f < e; f++)d[f] && (a = d[f].split("="), c[a[0]] = a[1]);
                return c
            }(),
            file: (b.pathname.match(/\/([^\/?#]+)$/i) || [, ""])[1],
            hash: b.hash.replace("#", ""),
            path: b.pathname.replace(/^([^\/])/, "/$1"),
            relative: (b.href.match(/tps?:\/\/[^\/]+(.+)/) || [, ""])[1],
            segments: b.pathname.replace(/^\//, "").split("/")
        }
    }, getTimeSpan: function () {
        return parseInt(Mar.Util.getTimeSpanMillisecond() / 1e3)
    }, getTimeSpanMillisecond: function () {
        return (new Date).getTime()
    }, Debounce: function (a, b, c) {
        var d, e, f, g, h, i = Date.now || function () {
                return (new Date).getTime()
            }, j = function () {
            var k = i() - g;
            k < b && k >= 0 ? d = setTimeout(j, b - k) : (d = null, c || (h = a.apply(f, e), d || (f = e = null)))
        };
        return function () {
            f = this, e = arguments, g = i();
            var k = c && !d;
            return d || (d = setTimeout(j, b)), k && (h = a.apply(f, e), f = e = null), h
        }
    }, getQueryStringByName: function (a, b) {
        var c = b.match(new RegExp("[?&]" + a + "=([^&^#]+)", "i"));
        return null == c || c.length < 1 ? "" : c[1]
    }, getMetaByName: function (a) {
        var b = document.getElementsByTagName("meta");
        for (i in b)if ("undefined" != typeof b[i].name && b[i].name.toLowerCase() == a)return b[i].content;
        return null
    }, isDetailPage: function (a) {
        var b = !1;
        return !a || a.indexOf("shop.vipshop.com/detail") === -1 && a.indexOf("www.vip.com/detail") === -1 && a.indexOf("detail.vipglobal.hk") === -1 || (b = !0), b
    }
}, function (a) {
    Mar.Request = function (a, b, c) {
        var d = Mar.Base, e = Mar.Biz(), f = Mar.Util.getDomain();
        c && (e.mvar = Mar.stringify(c)), a = Mar.protocal() + "mar." + f + a + "&mars_cid=" + e.cid + "&mars_sid=" + e.sid + "&pi=" + e.pid + "&mars_vid=" + e.vid + "&mars_var=" + e.mvar + "&lg=" + e.isLog + "&wh=" + e.wh + "&in=" + e.newbie + "&sn=" + e.orderId + "&url=" + d.url + "&sr=" + d.res + "&rf=" + d.ref + "&bw=" + d.cw + "&bh=" + d.ch + "&sc=" + d.col + "&bv=" + d.nav + "&ce=" + d.ce + "&vs=&title=" + d.title + "&tab_page_id=" + e.pageId + "&vip_qe=" + e.vip_qe + "&vip_qt=" + e.vip_qt + "&vip_xe=" + e.vip_xe + "&vip_xt=" + e.vip_xt, b && (a += "&wap_ln=" + e.wapln + "&wap_vs=" + e.wapvs + "&wap_pwh=" + e.wappwh + "&wap_wh=" + e.wapwh + "&wap_id=" + e.wapid + "&wap_from=" + e.wapfrom + "&cps_u=" + e.cpsu + "&m_vipruid=" + e.ruid), a += "&r=" + Math.random();
        var g = new Image(1, 1);
        return g.onload = g.onerror = g.onabort = function () {
            g.onload = g.onerror = g.onabort = null, g = null
        }, g.src = a, Mar
    }, Mar.PageIdIndex = 0, Mar.PageId = Mar.PageId ? Mar.PageId + "_" + ++Mar.PageIdIndex : (new Date).getTime() + "_" + Mar.guid2(), Mar.Biz = function (b) {
        function c(a) {
            var b = a.split("_"), c = b[0], d = b[1];
            if (!c || !d)return a;
            for (var e = 0, f = c.length, g = 0; g < f; g++)e += parseInt(c[g]);
            for (var h = e % 32, i = e, j = d.length, g = 0; g < j; g++)g !== h && (i += parseInt(d[g], 16));
            var k = (i % 16).toString(16);
            return c + "_" + d.substr(0, h) + k.toString() + d.substr(h + 1, j)
        }

        var d = Mar.Cookie, e = d.get, f = d.set, g = d.del, h = Mar.Base, i = 0, j = 0, k = "", l = e("VipLID"), m = e("PHPSESSID"), n = e("mars_cid") || e("cookie_id"), o = e("mars_pid") || e("page_id"), p = e("mars_sid") || Mar.rand(), q = e("visit_id") || Mar.guid(), r = e("tmp_mars_cid"), s = e("vip_wh"), t = window.mars_var ? Mar.stringify(window.mars_var) : "-", u = e("vip_qe"), v = e("vip_qt"), w = window.vip_xe || "", x = window.vip_xt || "";
        if (l && m && unescape(l).split("|")[0] === m && (i = 1), !n && r ? n = r : (!n || 32 !== (n + "").length && 46 !== (n + "").length) && (n = c(Mar.Util.pad((new Date).getTime(), 13) + "_" + Mar.rand()), j = 1), o = o || 0, b && o++, g("cookie_id"), g("page_id"), g("mars_cid"), f("mars_pid", o, 732), f("mars_cid", n, 732), f("mars_sid", p, 0), f("visit_id", q, .5 / 24), "/shop/shop_pay.php" === h.pn) k = a("#orid").html(); else if ("checkout.vipshop.com" === h.hn && "order.php" === h.pn.substring(6, 15))try {
            k = a("body").html().match(/您的订单号为：(\d+)/)[1]
        } catch (a) {
        }
        return {
            cid: n,
            sid: p,
            pid: o,
            vid: q,
            wh: s,
            mvar: t,
            newbie: j,
            isLog: i,
            orderId: k,
            pageId: Mar.PageId,
            vip_qe: u,
            vip_qt: v,
            vip_xe: w,
            vip_xt: x
        }
    }, Mar.PV = function () {
        return Mar.Request("/p?1=1"), Mar
    }
}(jQuery), Mar.Seed = function () {
    function a(a) {
        return /.*\.\w*$/.test(a) && $.inArray(a.match(/\.(\w*)$/i)[1], ["rar", "zip", "exe", "doc", "ppt", "xls", "docx", "xlsx", "pptx", "sisx", "apk"]) !== -1 ? "download" : ""
    }

    function b(b, d) {
        var e, f, g, h = $(this), i = this.tagName.toLowerCase();
        d = d || "click", "a" === i ? (f = h.attr("href"), g = c[i + (f ? a(f.toLowerCase()) : "")]) : (f = h.attr("type"), e = c[i + (f ? f.toLowerCase() : "")], g = e ? e : i), Mar.Seed.request(g, d, "hover" !== d.toLowerCase() ? h.attr("mars_sead") : h.attr("mars_sead_hover"))
    }

    var c = {
        inputbutton: "button",
        inputsubmit: "button",
        inputtext: "inputText",
        inputinput: "inputText",
        inputradio: "radio",
        inputcheckbox: "checkbox",
        adownload: "download",
        a: "link",
        span: "span",
        button: "button"
    };
    return $("body").delegate("[mars_sead]:not(select)", "click", function () {
        b.call(this)
    }).delegate("select[mars_sead]", "change", function () {
        Mar.Seed.request("select", "change", $(this).attr("mars_sead"))
    }).delegate("[mars_sead_hover]", "mouseenter", function (a) {
        b.call(this, a, "hover")
    }), Mar
}, Mar.Seed.request = function (a, b, c, d) {
    var e = (new Date).getTime() - window.T0;
    Mar.Request("/c?at=" + e + "&et=" + a + "&ed=" + b + "&one=" + encodeURIComponent(c), "", d)
}, Mar.Report = function () {
    return window.MARS_EVENT_TYPE && window.MARS_EVENT_VALUE && Mar.Seed.request(MARS_EVENT_TYPE, escape(MARS_EVENT_VALUE), "", ""), Mar
}, Mar.Screen = function () {
    var a, b, c, d, e, f, g, h, i, j, k, l, m, n, o = Mar.Biz(), p = o.cid;
    p.substring(p.length - 1, p.length);
    return p.lastIndexOf("0") !== p.length - 1 ? Mar : (a = Mar.Base, b = [], c = (new Date).getTime(), e = $(window), f = e.width(), g = e.height(), h = a.context.scrollHeight, n = function (a, b) {
            Mar.Request("/s?psn=" + a + "&pt=" + b + "&ph=" + e.scrollTop() + "&pl=" + h)
        }, e.bind("scroll", function () {
            m = setTimeout(function () {
                clearTimeout(m), i = e.scrollTop(), j = Math.ceil(i / g), i >= (j - 1) * g + .2 * g && (a.href.indexOf("shop.vipshop.com/detail") !== -1 || a.href.indexOf("www.vip.com/detail") !== -1 || a.href.indexOf("detail.vipglobal.hk") !== -1 ? (k !== j && (clearInterval(l), l = setInterval(function () {
                        n(j, 3)
                    }, 3e3)), k = j) : $.inArray(j, b) === -1 && (d = (new Date).getTime(), n(j, (d - c) / 1e3), c = d, b.push(j)))
            }, 500)
        }).bind("close", function () {
            i = e.scrollTop(), j = Math.ceil(i / g), d = (new Date).getTime(), n(j, (d - c) / 1e3)
        }).bind("resize", function () {
            e = $(this), f = e.width(), g = e.height(), srollHeight = a.context.scrollHeight
        }), Mar)
}, Mar.Links = function () {
    var a = function (a, b) {
        var c = $(this).hasOwnProperty("href"), d = c ? $(this).attr("href") : "";
        d = d ? d.replace("#", "%26") : d, void 0 === b ? Mar.Request("/m?mx=" + a.pageX + "&my=" + a.pageY + "&href=" + d) : Mar.Request("/m?mx=" + a.pageX + "&my=" + a.pageY + "&clk_depth=" + b + "&href=" + d)
    };
    return $(document.body).click(function (b) {
        var c = [], d = $(b.target);
        if (7 != Mar.IE())for (; d && d[0] && d[0].tagName && "body" !== d[0].tagName.toLowerCase() && "!" !== d[0].tagName.toLowerCase();)8 !== d[0].nodeType && (c.push(d[0].tagName.toLowerCase() + (void 0 === d.attr("class") ? "" : "." + d.attr("class")) + ":" + d.prevAll(d[0].tagName).length), d = d.parent());
        a(b, c.reverse().join(" "))
    }), Mar
}, Mar.Performance = function () {
    var a, b, c, d, e = window.performance;
    return e && (a = e.timing, b = e.navigation, d = setInterval(function () {
        0 !== a.loadEventEnd && (clearInterval(d), c = {
            navigation: a.loadEventEnd - a.navigationStart,
            unloadEvent: a.unloadEventEnd - a.unloadEventStart,
            redirect: a.redirectEnd - a.redirectStart,
            domainLookup: a.domainLookupEnd - a.domainLookupStart,
            connect: a.connectEnd - a.connectStart,
            request: a.responseEnd - a.requestStart,
            response: a.responseEnd - a.responseStart,
            domLoading: a.domInteractive - a.domLoading,
            domInteractive: a.domContentLoadedEventEnd - a.domInteractive,
            domContentLoaded: a.domContentLoadedEventEnd - a.domContentLoadedEventStart,
            domComplete: a.domComplete - a.domLoading,
            loadEvent: a.loadEventEnd - a.loadEventStart,
            fetch: a.responseEnd - a.fetchStart,
            type: b.type,
            timeTofirstByte: a.responseStart - a.navigationStart
        }, Mar.Request("/a?ps_nav=" + c.navigation + "&ps_ule=" + c.unloadEvent + "&ps_rd=" + c.redirect + "&ps_dlu=" + c.domainLookup + "&ps_con=" + c.connect + "&ps_req=" + c.request + "&ps_resp=" + c.response + "&ps_dl=" + c.domLoading + "&ps_di=" + c.domInteractive + "&ps_dcl=" + c.domContentLoaded + "&ps_dc=" + c.domComplete + "&ps_le=" + c.loadEvent + "&ps_ft=" + c.fetch + "&ps_ty=" + c.type + "&ps_ttfb=" + c.timeTofirstByte))
    }, 100)), Mar
}, Mar.Var = function () {
    if (document.location.href.indexOf("//www.vip.com/detail-") !== -1 || document.location.href.indexOf("//list.vip.com/") !== -1 || document.location.href.indexOf("//detail.vipglobal.hk/") !== -1 || document.location.href.indexOf("//list.vipglobal.hk/") !== -1)var a, b = (window.mars_var ? Mar.stringify(window.mars_var) : "-", 0), c = function () {
        return b < 1 ? void b++ : (clearInterval(a), void Mar.Request("/o?iso=1"))
    }, a = window.setInterval(c, 1e3);
    return Mar
}, Mar.Banner = function () {
    function a(a) {
        var b = $(window).scrollTop(), c = $(window).height(), d = a.height() / 2, e = a.offset().top, f = e + d;
        return b <= f && b + c >= f
    }

    function b(a, b, e, f) {
        var g = +new Date, h = "";
        f && (h = "&one=" + f), (g - c > 500 || e) && Mar.Request("/y?page=index&adv=-99_" + a + "_" + b + "_1_1" + h), d = !1
    }

    var c = +new Date, d = !0;
    return $.Listeners && $.Listeners.sub("index.switchable.ads").onsuccess(function (e) {
        var f = e.adIdx + 1, g = e.adId, h = e.panelCls, i = e.onlyOneAd, j = e.adExpose, k = a(h);
        i ? ($(window).on("scroll", function () {
                var e = a(h);
                e && b(g, f, d, j), c = +new Date
            }).trigger("scroll"), $.Listeners.unsub("index.switchable.ads").success()) : k && (b(g, f, d, j), c = +new Date)
    }), Mar
}, Mar.Recommend = function () {
    function a(a, b) {
        var c = d.scrollTop(), g = d.scrollLeft(), h = c + d.height(), i = g + d.width(), j = [];
        $("[mars-exposure-module]:visible").each(function (a, b) {
            var d = $(b), e = d.offset(), k = e.top, l = d.height() / 2, m = k + l;
            h >= m && m >= c && f(i, g, d, e) && (j[j.length] = d.attr("mars-exposure-module"))
        }), j.length > 0 && (e[e.length] = {into_time: a, leave_time: b, sec: b - a, brands: j})
    }

    function b() {
        var a, b, c, d, f, g, h, i, j, k = e.length;
        if (0 !== k) {
            for (a = 0; a < k; a++) {
                for (g = {}, d = e[a], f = d.brands, c = f.length, b = 0; b < c; b++)h = f[b].split("_"), j = h[1], i = g[j] = g[j] || [], i[i.length] = {
                    brand_id: h[2],
                    brand_rank: h[3]
                };
                for (a in g)Mar.Request("/y?" + ["into_time=" + d.into_time, "leave_time=" + d.leave_time, "sec=" + d.sec, "brand_expose=" + encodeURIComponent(Mar.stringify({
                        page: h[0],
                        module: a,
                        brands: g[a]
                    }))].join("&"))
            }
            e = []
        }
    }

    var c = window, d = $(c), e = [], f = c.marOptions && marOptions.tCjudge ? function (a, b, c, d) {
            var e = d.left, f = c.width() / 2, g = e + f;
            return a >= g && g >= b
        } : function () {
            return !0
        };
    return $(document).ready(function () {
        function c() {
            var b = +new Date;
            b - e > 500 && a(e, b)
        }

        var e = +new Date;
        d.on("scroll", function () {
            c(), e = +new Date
        }).on("unload", function () {
            c(), b()
        }), setInterval(function () {
            b()
        }, 2e3)
    }), Mar
}, Mar.Track = function () {
    var a = this, b = Mar.Util.getMetaByName("mars_root"), c = Mar.Util.getQueryStringByName("_mars_ref", location.href), d = Math.round((new Date).getTime() / 1e3);
    return this.Track.handleUrl = function (a, b) {
        var c = "", d = "", e = "", f = [];
        return f = a.split("#"), a = f[0], hash = f[1], c = a.indexOf("?") == -1 ? "?" : "", e = c ? "_mars_ref=" : "&_mars_ref=", b = b ? c + e + b : "", d = a + b + (hash ? "#" + hash : "")
    }, this.Track.setUrl = function (e) {
        var f = "", g = "", h = "";
        return null != b ? (g = e.indexOf("ff=") != -1 ? Mar.Util.getQueryStringByName("ff", e) : Mar.Util.getQueryStringByName("f", e), f = b + "|" + g + "|" + d) : f = c, h = a.Track.handleUrl(e, f)
    }, this.Track.setElementTrack = function (b) {
        var c = b.attr("href");
        return b.attr("data-track", "done").attr("href", a.Track.setUrl(c)), b
    }, this.Track.setTrackInVisable = function (b, c, d) {
        return b.offset().top > c && b.offset().top < d && a.Track.setElementTrack(b), b
    }, this.Track.set = function () {
        for (var b = $(window).scrollTop(), c = b + $(window).height(), d = $("a"), e = 0, f = d.length; e < f; e++) {
            var g = d.eq(e), h = g.attr("href");
            "" !== h && void 0 !== h && h.indexOf("f=") > -1 && (g.attr("data-track") || a.Track.setTrackInVisable(g, b, c)), g.attr("data-track") || Mar.Util.isDetailPage(h) && a.Track.setTrackInVisable(g, b, c)
        }
    }, this.Track.set(), $(window).on("scroll", Mar.Util.Debounce(a.Track.set, 300)), $.Listeners && ($.Listeners.sub("mars.track").onsuccess(function (b) {
        a.Track.set()
    }), window.mars_var || (window.mars_var = {}), $.Listeners.sub("cart.add.success").onsuccess(function (a) {
        window.mars_var.mars_ref = c || "", window.mars_var.size_id = a.param.size_id || "", Mar.Seed.request("a", "click", "add_goods_to_cart_success", "")
    }), $.Listeners.sub("cart.add.mars").onsuccess(function (a) {
        a && Mar.Seed.request("a", "click", "te_buy_cart_btn", {
            brand_id: a.brand_id,
            goods_id: a.goods_id,
            scene_id: a.scene_id
        })
    })), Mar
}, function () {
    Mar.PV().Report().Screen().Links().Track().Performance().Var().Seed().Banner().Recommend()
}();