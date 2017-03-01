/**
 * Created by kris on 2017/3/1.
 */
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
    return (function e(t, n, r) {
        function s(o, u) {
            if (!n[o]) {
                if (!t[o]) {
                    var a = typeof require == "function" && require;
                    if (!u && a) return a(o, !0);
                    if (i) return i(o, !0);
                    var f = new Error("Cannot find module '" + o + "'");
                    throw f.code = "MODULE_NOT_FOUND", f
                }
                var l = n[o] = {
                    exports: {}
                };
                t[o][0].call(l.exports, function (e) {
                    var n = t[o][1][e];
                    return s(n ? n : e)
                }, l, l.exports, e, t, n, r)
            }
            return n[o].exports
        }
        var i = typeof require == "function" && require;
        for (var o = 0; o < r.length; o++) s(r[o]);
        return s
    })({
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
                    var t, r = {}, n = "undefined" != typeof window ? window : global,
                        i = n.document,
                        o = "localStorage",
                        a = "script";
                    if (r.disabled = !1, r.version = "1.3.20", r.set = function (e, t) {}, r.get = function (e, t) {},
                            r.has = function (e) {
                                return void 0 !== r.get(e)
                            }, r.remove = function (e) {}, r.clear = function () {}, r.transact = function (e, t, n) {
                            null == n && (n = t, t = null), null == t && (t = {});
                            var i = r.get(e, t);
                            n(i), r.set(e, i)
                        }, r.getAll = function () {}, r.forEach = function () {}, r.serialize = function (e) {
                            return JSON.stringify(e)
                        }, r.deserialize = function (e) {
                            if ("string" == typeof e) try {
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
                    };
                    else if (i && i.documentElement.addBehavior) {
                        var c, u;
                        try {
                            u = new ActiveXObject("htmlfile"), u.open(), u.write("<" + a + ">document.w=window</" +
                                a + '><iframe src="/favicon.ico"></iframe>'), u.close(), c = u.w.frames[0].document,
                                t = c.createElement("div")
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
                            }, d = new RegExp("[!\"#$%&'()*+,/\\\\:;<=>?@[\\]^`{|}~]", "g"),
                            s = function (e) {
                                return e.replace(/^d/, "___$&").replace(d, "___")
                            };
                        r.set = f(function (e, t, n) {
                            return t = s(t), void 0 === n ? r.remove(t) : (e.setAttribute(t, r.serialize(n)), e.save(
                                    o), n)
                        }), r.get = f(function (e, t, n) {
                            t = s(t);
                            var i = r.deserialize(e.getAttribute(t));
                            return void 0 === i ? n : i
                        }), r.remove = f(function (e, t) {
                            t = s(t), e.removeAttribute(t), e.save(o)
                        }), r.clear = f(function (e) {
                            var t = e.XMLDocument.documentElement.attributes;
                            e.load(o);
                            for (var r = t.length - 1; r >= 0; r--) e.removeAttribute(t[r].name);
                            e.save(o)
                        }), r.getAll = function (e) {
                            var t = {};
                            return r.forEach(function (e, r) {
                                t[e] = r
                            }), t
                        }, r.forEach = f(function (e, t) {
                            for (var n, i = e.XMLDocument.documentElement.attributes, o = 0; n = i[o]; ++o) t(n.name,
                                r.deserialize(e.getAttribute(n.name)))
                        })
                    }
                    try {
                        var v = "__storejs__";
                        r.set(v, v), r.get(v) != v && (r.disabled = !0), r.remove(v)
                    } catch (l) {
                        r.disabled = !0
                    }
                    return r.enabled = !r.disabled, r
                }();
            }).call(this, typeof global !== "undefined" ? global : typeof self !== "undefined" ? self : typeof window !==
                    "undefined" ? window : {})
        }, {}]
    }, {}, [1])(1)
});
var md5;
md5 || (md5 = function () {
    function i(a, b) {
        a[b >> 5] |= 128 << b % 32, a[(b + 64 >>> 9 << 4) + 14] = b;
        for (var c = 1732584193, d = -271733879, e = -1732584194, f = 271733878, g = 0; a.length > g; g += 16) {
            var h = c,
                i = d,
                j = e,
                o = f;
            c = k(c, d, e, f, a[g + 0], 7, -680876936), f = k(f, c, d, e, a[g + 1], 12, -389564586), e = k(e, f, c, d,
                a[g + 2], 17, 606105819), d = k(d, e, f, c, a[g + 3], 22, -1044525330), c = k(c, d, e, f, a[g + 4], 7, -
                176418897), f = k(f, c, d, e, a[g + 5], 12, 1200080426), e = k(e, f, c, d, a[g + 6], 17, -1473231341),
                d = k(d, e, f, c, a[g + 7], 22, -45705983), c = k(c, d, e, f, a[g + 8], 7, 1770035416), f = k(f, c, d,
                e, a[g + 9], 12, -1958414417), e = k(e, f, c, d, a[g + 10], 17, -42063), d = k(d, e, f, c, a[g + 11],
                22, -1990404162), c = k(c, d, e, f, a[g + 12], 7, 1804603682), f = k(f, c, d, e, a[g + 13], 12, -
                40341101), e = k(e, f, c, d, a[g + 14], 17, -1502002290), d = k(d, e, f, c, a[g + 15], 22, 1236535329),
                c = l(c, d, e, f, a[g + 1], 5, -165796510), f = l(f, c, d, e, a[g + 6], 9, -1069501632), e = l(e, f, c,
                d, a[g + 11], 14, 643717713), d = l(d, e, f, c, a[g + 0], 20, -373897302), c = l(c, d, e, f, a[g + 5],
                5, -701558691), f = l(f, c, d, e, a[g + 10], 9, 38016083), e = l(e, f, c, d, a[g + 15], 14, -660478335),
                d = l(d, e, f, c, a[g + 4], 20, -405537848), c = l(c, d, e, f, a[g + 9], 5, 568446438), f = l(f, c, d,
                e, a[g + 14], 9, -1019803690), e = l(e, f, c, d, a[g + 3], 14, -187363961), d = l(d, e, f, c, a[g + 8],
                20, 1163531501), c = l(c, d, e, f, a[g + 13], 5, -1444681467), f = l(f, c, d, e, a[g + 2], 9, -51403784),
                e = l(e, f, c, d, a[g + 7], 14, 1735328473), d = l(d, e, f, c, a[g + 12], 20, -1926607734), c = m(c, d,
                e, f, a[g + 5], 4, -378558), f = m(f, c, d, e, a[g + 8], 11, -2022574463), e = m(e, f, c, d, a[g + 11],
                16, 1839030562), d = m(d, e, f, c, a[g + 14], 23, -35309556), c = m(c, d, e, f, a[g + 1], 4, -
                1530992060), f = m(f, c, d, e, a[g + 4], 11, 1272893353), e = m(e, f, c, d, a[g + 7], 16, -155497632),
                d = m(d, e, f, c, a[g + 10], 23, -1094730640), c = m(c, d, e, f, a[g + 13], 4, 681279174), f = m(f, c,
                d, e, a[g + 0], 11, -358537222), e = m(e, f, c, d, a[g + 3], 16, -722521979), d = m(d, e, f, c, a[g + 6],
                23, 76029189), c = m(c, d, e, f, a[g + 9], 4, -640364487), f = m(f, c, d, e, a[g + 12], 11, -421815835),
                e = m(e, f, c, d, a[g + 15], 16, 530742520), d = m(d, e, f, c, a[g + 2], 23, -995338651), c = n(c, d, e,
                f, a[g + 0], 6, -198630844), f = n(f, c, d, e, a[g + 7], 10, 1126891415), e = n(e, f, c, d, a[g + 14],
                15, -1416354905), d = n(d, e, f, c, a[g + 5], 21, -57434055), c = n(c, d, e, f, a[g + 12], 6,
                1700485571), f = n(f, c, d, e, a[g + 3], 10, -1894986606), e = n(e, f, c, d, a[g + 10], 15, -1051523),
                d = n(d, e, f, c, a[g + 1], 21, -2054922799), c = n(c, d, e, f, a[g + 8], 6, 1873313359), f = n(f, c, d,
                e, a[g + 15], 10, -30611744), e = n(e, f, c, d, a[g + 6], 15, -1560198380), d = n(d, e, f, c, a[g + 13],
                21, 1309151649), c = n(c, d, e, f, a[g + 4], 6, -145523070), f = n(f, c, d, e, a[g + 11], 10, -
                1120210379), e = n(e, f, c, d, a[g + 2], 15, 718787259), d = n(d, e, f, c, a[g + 9], 21, -343485551), c =
                p(c, h), d = p(d, i), e = p(e, j), f = p(f, o)
        }
        return [c, d, e, f]
    }
    function j(a, b, c, d, e, f) {
        return p(q(p(p(b, a), p(d, f)), e), c)
    }
    function k(a, b, c, d, e, f, g) {
        return j(b & c | ~b & d, a, b, e, f, g)
    }
    function l(a, b, c, d, e, f, g) {
        return j(b & d | c & ~d, a, b, e, f, g)
    }
    function m(a, b, c, d, e, f, g) {
        return j(b ^ c ^ d, a, b, e, f, g)
    }
    function n(a, b, c, d, e, f, g) {
        return j(c ^ (b | ~d), a, b, e, f, g)
    }
    function p(a, b) {
        var c = (a & 65535) + (b & 65535),
            d = (a >> 16) + (b >> 16) + (c >> 16);
        return d << 16 | c & 65535
    }
    function q(a, b) {
        return a << b | a >>> 32 - b
    }
    function r(a) {
        for (var b = [], d = (1 << c) - 1, e = 0; a.length * c > e; e += c) b[e >> 5] |= (a.charCodeAt(e / c) & d) << e %
            32;
        return b
    }
    function t(b) {
        for (var c = a ? "0123456789ABCDEF" : "0123456789abcdef", d = "", e = 0; b.length * 4 > e; e++) d += c.charAt(b[
                e >> 2] >> e % 4 * 8 + 4 & 15) + c.charAt(b[e >> 2] >> e % 4 * 8 & 15);
        return d
    }
    var a = 0,
        c = 8;
    return function (a) {
        return t(i(r(a), a.length * c))
    }
}());

(function(global) {
    'use strict';
    // existing version for noConflict()
    var _Base64 = global.Base64;
    var version = "2.1.9";
    // if node.js, we use Buffer
    var buffer;
    if (typeof module !== 'undefined' && module.exports) {
        try {
            buffer = require('buffer').Buffer;
        } catch (err) {}
    }
    // constants
    var b64chars
        = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/';
    var b64tab = function(bin) {
        var t = {};
        for (var i = 0, l = bin.length; i < l; i++) t[bin.charAt(i)] = i;
        return t;
    }(b64chars);
    var fromCharCode = String.fromCharCode;
    // encoder stuff
    var cb_utob = function(c) {
        if (c.length < 2) {
            var cc = c.charCodeAt(0);
            return cc < 0x80 ? c
                : cc < 0x800 ? (fromCharCode(0xc0 | (cc >>> 6))
                    + fromCharCode(0x80 | (cc & 0x3f)))
                    : (fromCharCode(0xe0 | ((cc >>> 12) & 0x0f))
                    + fromCharCode(0x80 | ((cc >>>  6) & 0x3f))
                    + fromCharCode(0x80 | ( cc         & 0x3f)));
        } else {
            var cc = 0x10000
                + (c.charCodeAt(0) - 0xD800) * 0x400
                + (c.charCodeAt(1) - 0xDC00);
            return (fromCharCode(0xf0 | ((cc >>> 18) & 0x07))
            + fromCharCode(0x80 | ((cc >>> 12) & 0x3f))
            + fromCharCode(0x80 | ((cc >>>  6) & 0x3f))
            + fromCharCode(0x80 | ( cc         & 0x3f)));
        }
    };
    var re_utob = /[\uD800-\uDBFF][\uDC00-\uDFFFF]|[^\x00-\x7F]/g;
    var utob = function(u) {
        return u.replace(re_utob, cb_utob);
    };
    var cb_encode = function(ccc) {
        var padlen = [0, 2, 1][ccc.length % 3],
            ord = ccc.charCodeAt(0) << 16
                | ((ccc.length > 1 ? ccc.charCodeAt(1) : 0) << 8)
                | ((ccc.length > 2 ? ccc.charCodeAt(2) : 0)),
            chars = [
                b64chars.charAt( ord >>> 18),
                b64chars.charAt((ord >>> 12) & 63),
                padlen >= 2 ? '=' : b64chars.charAt((ord >>> 6) & 63),
                padlen >= 1 ? '=' : b64chars.charAt(ord & 63)
            ];
        return chars.join('');
    };
    var btoa = global.btoa ? function(b) {
            return global.btoa(b);
        } : function(b) {
            return b.replace(/[\s\S]{1,3}/g, cb_encode);
        };
    var _encode = buffer ? function (u) {
                return (u.constructor === buffer.constructor ? u : new buffer(u))
                    .toString('base64')
            }
            : function (u) { return btoa(utob(u)) }
        ;
    var encode = function(u, urisafe) {
        return !urisafe
            ? _encode(String(u))
            : _encode(String(u)).replace(/[+\/]/g, function(m0) {
                return m0 == '+' ? '-' : '_';
            }).replace(/=/g, '');
    };
    var encodeURI = function(u) { return encode(u, true) };
    // decoder stuff
    var re_btou = new RegExp([
        '[\xC0-\xDF][\x80-\xBF]',
        '[\xE0-\xEF][\x80-\xBF]{2}',
        '[\xF0-\xF7][\x80-\xBF]{3}'
    ].join('|'), 'g');
    var cb_btou = function(cccc) {
        switch(cccc.length) {
            case 4:
                var cp = ((0x07 & cccc.charCodeAt(0)) << 18)
                        |    ((0x3f & cccc.charCodeAt(1)) << 12)
                        |    ((0x3f & cccc.charCodeAt(2)) <<  6)
                        |     (0x3f & cccc.charCodeAt(3)),
                    offset = cp - 0x10000;
                return (fromCharCode((offset  >>> 10) + 0xD800)
                + fromCharCode((offset & 0x3FF) + 0xDC00));
            case 3:
                return fromCharCode(
                    ((0x0f & cccc.charCodeAt(0)) << 12)
                    | ((0x3f & cccc.charCodeAt(1)) << 6)
                    |  (0x3f & cccc.charCodeAt(2))
                );
            default:
                return  fromCharCode(
                    ((0x1f & cccc.charCodeAt(0)) << 6)
                    |  (0x3f & cccc.charCodeAt(1))
                );
        }
    };
    var btou = function(b) {
        return b.replace(re_btou, cb_btou);
    };
    var cb_decode = function(cccc) {
        var len = cccc.length,
            padlen = len % 4,
            n = (len > 0 ? b64tab[cccc.charAt(0)] << 18 : 0)
                | (len > 1 ? b64tab[cccc.charAt(1)] << 12 : 0)
                | (len > 2 ? b64tab[cccc.charAt(2)] <<  6 : 0)
                | (len > 3 ? b64tab[cccc.charAt(3)]       : 0),
            chars = [
                fromCharCode( n >>> 16),
                fromCharCode((n >>>  8) & 0xff),
                fromCharCode( n         & 0xff)
            ];
        chars.length -= [0, 0, 2, 1][padlen];
        return chars.join('');
    };
    var atob = global.atob ? function(a) {
            return global.atob(a);
        } : function(a){
            return a.replace(/[\s\S]{1,4}/g, cb_decode);
        };
    var _decode = buffer ? function(a) {
            return (a.constructor === buffer.constructor
                ? a : new buffer(a, 'base64')).toString();
        }
        : function(a) { return btou(atob(a)) };
    var decode = function(a){
        return _decode(
            String(a).replace(/[-_]/g, function(m0) { return m0 == '-' ? '+' : '/' })
                .replace(/[^A-Za-z0-9\+\/]/g, '')
        );
    };
    var noConflict = function() {
        var Base64 = global.Base64;
        global.Base64 = _Base64;
        return Base64;
    };
    // export Base64
    global.Base64 = {
        VERSION: version,
        atob: atob,
        btoa: btoa,
        fromBase64: decode,
        toBase64: encode,
        utob: utob,
        encode: encode,
        encodeURI: encodeURI,
        btou: btou,
        decode: decode,
        noConflict: noConflict
    };
    // if ES5 is available, make Base64.extendString() available
    if (typeof Object.defineProperty === 'function') {
        var noEnum = function(v){
            return {value:v,enumerable:false,writable:true,configurable:true};
        };
        global.Base64.extendString = function () {
            Object.defineProperty(
                String.prototype, 'fromBase64', noEnum(function () {
                    return decode(this)
                }));
            Object.defineProperty(
                String.prototype, 'toBase64', noEnum(function (urisafe) {
                    return encode(this, urisafe)
                }));
            Object.defineProperty(
                String.prototype, 'toBase64URI', noEnum(function () {
                    return encode(this, true)
                }));
        };
    }
    // that's it!
    if (global['Meteor']) {
        Base64 = global.Base64; // for normal export in Meteor.js
    }
})(this);
/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  */
/*
 * fingerprintJS 0.5.4 - Fast browser fingerprint library
 * https://github.com/Valve/fingerprintjs
 * Copyright (c) 2013 Valentin Vasilyev (valentin.vasilyev@outlook.com)
 * Licensed under the MIT (http://www.opensource.org/licenses/mit-license.php) license.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

;(function (name, context, definition) {
    if (typeof module !== 'undefined' && module.exports) { module.exports = definition(); }
    else if (typeof define === 'function' && define.amd) { define(definition); }
    else { context[name] = definition(); }
})('Fingerprint', this, function () {
    'use strict';

    var Fingerprint = function (options) {
        var nativeForEach, nativeMap;
        nativeForEach = Array.prototype.forEach;
        nativeMap = Array.prototype.map;

        this.each = function (obj, iterator, context) {
            if (obj === null) {
                return;
            }
            if (nativeForEach && obj.forEach === nativeForEach) {
                obj.forEach(iterator, context);
            } else if (obj.length === +obj.length) {
                for (var i = 0, l = obj.length; i < l; i++) {
                    if (iterator.call(context, obj[i], i, obj) === {}) return;
                }
            } else {
                for (var key in obj) {
                    if (obj.hasOwnProperty(key)) {
                        if (iterator.call(context, obj[key], key, obj) === {}) return;
                    }
                }
            }
        };

        this.map = function(obj, iterator, context) {
            var results = [];
            // Not using strict equality so that this acts as a
            // shortcut to checking for `null` and `undefined`.
            if (obj == null) return results;
            if (nativeMap && obj.map === nativeMap) return obj.map(iterator, context);
            this.each(obj, function(value, index, list) {
                results[results.length] = iterator.call(context, value, index, list);
            });
            return results;
        };

        if (typeof options == 'object'){
            this.hasher = options.hasher;
            this.screen_resolution = options.screen_resolution;
            this.screen_orientation = options.screen_orientation;
            this.canvas = options.canvas;
            this.ie_activex = options.ie_activex;
        } else if(typeof options == 'function'){
            this.hasher = options;
        }
    };

    Fingerprint.prototype = {
        get: function(){
            var keys = [];
            keys.push(navigator.userAgent);
            keys.push(navigator.language);
            keys.push(screen.colorDepth);
            if (this.screen_resolution) {
                var resolution = this.getScreenResolution();
                if (typeof resolution !== 'undefined'){ // headless browsers, such as phantomjs
                    keys.push(resolution.join('x'));
                }
            }
            keys.push(new Date().getTimezoneOffset());
            keys.push(this.hasSessionStorage());
            keys.push(this.hasLocalStorage());
            keys.push(!!window.indexedDB);
            //body might not be defined at this point or removed programmatically
            if(document.body){
                keys.push(typeof(document.body.addBehavior));
            } else {
                keys.push(typeof undefined);
            }
            keys.push(typeof(window.openDatabase));
            keys.push(navigator.cpuClass);
            keys.push(navigator.platform);
            keys.push(navigator.doNotTrack);
            keys.push(this.getPluginsString());
            if(this.canvas && this.isCanvasSupported()){
                keys.push(this.getCanvasFingerprint());
            }
            return Base64.encode(keys.join('###'));
        },

        // https://bugzilla.mozilla.org/show_bug.cgi?id=781447
        hasLocalStorage: function () {
            try{
                return !!window.localStorage;
            } catch(e) {
                return true; // SecurityError when referencing it means it exists
            }
        },

        hasSessionStorage: function () {
            try{
                return !!window.sessionStorage;
            } catch(e) {
                return true; // SecurityError when referencing it means it exists
            }
        },

        isCanvasSupported: function () {
            var elem = document.createElement('canvas');
            return !!(elem.getContext && elem.getContext('2d'));
        },

        isIE: function () {
            if(navigator.appName === 'Microsoft Internet Explorer') {
                return true;
            } else if(navigator.appName === 'Netscape' && /Trident/.test(navigator.userAgent)){// IE 11
                return true;
            }
            return false;
        },

        getPluginsString: function () {
            if(this.isIE() && this.ie_activex){
                return this.getIEPluginsString();
            } else {
                return this.getRegularPluginsString();
            }
        },

        getRegularPluginsString: function () {
            return this.map(navigator.plugins, function (p) {
                var mimeTypes = this.map(p, function(mt){
                    return [mt.type, mt.suffixes].join('~');
                }).join(',');
                return [p.name, p.description, mimeTypes].join('::');
            }, this).join(';');
        },

        getIEPluginsString: function () {
            if(window.ActiveXObject){
                var names = ['ShockwaveFlash.ShockwaveFlash',//flash plugin
                    'AcroPDF.PDF', // Adobe PDF reader 7+
                    'PDF.PdfCtrl', // Adobe PDF reader 6 and earlier, brrr
                    'QuickTime.QuickTime', // QuickTime
                    // 5 versions of real players
                    'rmocx.RealPlayer G2 Control',
                    'rmocx.RealPlayer G2 Control.1',
                    'RealPlayer.RealPlayer(tm) ActiveX Control (32-bit)',
                    'RealVideo.RealVideo(tm) ActiveX Control (32-bit)',
                    'RealPlayer',
                    'SWCtl.SWCtl', // ShockWave player
                    'WMPlayer.OCX', // Windows media player
                    'AgControl.AgControl', // Silverlight
                    'Skype.Detection'];

                // starting to detect plugins in IE
                return this.map(names, function(name){
                    try{
                        new ActiveXObject(name);
                        return name;
                    } catch(e){
                        return null;
                    }
                }).join(';');
            } else {
                return ""; // behavior prior version 0.5.0, not breaking backwards compat.
            }
        },

        getScreenResolution: function () {
            var resolution;
            if(this.screen_orientation){
                resolution = (screen.height > screen.width) ? [screen.height, screen.width] : [screen.width, screen.height];
            }else{
                resolution = [screen.height, screen.width];
            }
            return resolution;
        },

        getCanvasFingerprint: function () {
            try{
                var canvas = document.createElement('canvas');
                var ctx = canvas.getContext('2d');
                var txt = 'thunder network';
                ctx.textBaseline = "top";
                ctx.font = "14px Arial";
                ctx.textBaseline = "alphabetic";
                ctx.fillStyle = "#f60";
                ctx.fillRect(125,1,62,20);
                ctx.fillStyle = "#069";
                ctx.fillText(txt, 2, 15);
                ctx.fillStyle = "rgba(102, 204, 0, 0.7)";
                ctx.fillText(txt, 4, 17);
                return md5(canvas.toDataURL());
            }
            catch(e){
                return '';
            }
        }
    };


    return Fingerprint;

});
var Util = (function(){
    var undef = void 0, self, DOMAIN = ".xunlei.com", SERVER_LOGIN = ["https://login", "https://login2", "https://login3"],
        SERVER_LOGIN_STATUS = [1, 1, 1];
    self = {
        randString: function(length, max) {
            var random_string_chars = "abcdefghijklmnopqrstuvwxyz0123456789",
                len = random_string_chars.length;
            max = max ? Math.min(max, len) : len;
            var i, ret = [];
            for (i = 0; i < length; i++) {
                ret.push(random_string_chars.charAt(Math.floor(Math.random() * max)))
            }
            return ret.join("")
        },
        getCookie: function(param, decode) {
            var c, cookie = document.cookie,
                t, i, l;
            decode = decode === undef ? true : decode;
            if (param) {
                c = cookie.match(new RegExp("(^| )" + param + "=([^;]*)")) == null ? undef : (RegExp.$2);
                if (decode && c !== undef) {
                    try {
                        c = decodeURIComponent(c)
                    } catch (e) {
                        c = unescape(c)
                    }
                }
                return c ? c : '';
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
        },
        setCookie: function(name, value, expire, domain, path, secure) {
            var cookie, expire = expire ? new Date(new Date().getTime() + expire).toGMTString() : false;
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
        },
        getServer: function(type, path) {
            var servers = SERVER_LOGIN,
                status, index, len = servers.length,
                count = 0,
                flag, tmp;
            tmp = self.getCookie("_s." + type + "_");
            if (tmp && (tmp = tmp.split(",")) && tmp.length === len) {
                status = tmp;
                SERVER_LOGIN_STATUS = status;
            } else {
                status = SERVER_LOGIN_STATUS;
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
                index = (new Date().getTime()) % len
            }
            return servers[index] + DOMAIN + (path ? path : "")
        },
        setServer: function(server, value, type) {
            var i, flag, servers = SERVER_LOGIN,
                status = SERVER_LOGIN_STATUS;
            server = server.substring(0, server.indexOf(DOMAIN))
            for (i = servers.length - 1; i >= 0; --i) {
                if (server === servers[i]) {
                    status[i] = value ? 1 : 0;
                    self.setCookie("_s." + type + "_", status.join(","));
                    flag = true;
                    break
                }
            }
            if (!flag) {
                throw new Error("not find your server: " + server)
            }
        },
        loadScript: function(path, cb, retry) {
            var script = document.createElement("script"),
                done = false;
            url = self.getServer("login", path);
            script.src = url;
            script.type = "text/javascript";
            script.language = "javascript";
            script.onload = script.onreadystatechange = function() {
                if (!done && (!this.readyState || this.readyState == "loaded" || this.readyState == "complete")) {
                    done = true;
                    if (typeof cb === "function") {
                        cb()
                    }
                    script.onload = script.onreadystatechange = null;
                    script.parentNode.removeChild(script)
                }
            };
            script.onerror = function() {
                self.setServer(url, 0, "login");
                script.onload = script.onreadystatechange = null;
                script.parentNode.removeChild(script)
                if (retry && retry === true) {
                    retry = 1;
                }
                if(retry && SERVER_LOGIN.length > retry){
                    self.loadScript(path, cb, retry+1);
                }
            };
            document.getElementsByTagName("head")[0].appendChild(script);
            return script
        },
        requestHelper: function(method, path, params, cb, timeout, retry) {
            var rtn = 0,
                url;
            url = self.getServer("login", path);
            self.loginRequest(method, url, params, function(msg) {
                if (msg === "TIMEOUT") {
                    rtn = -1;
                }
                if (retry && retry === true) {
                    retry = 1;
                }

                if (retry && SERVER_LOGIN.length > retry && rtn === -1) {
                    self.setServer(url, 0, "login");
                    self.requestHelper(method, path, params, cb, timeout, retry + 1)
                } else {
                    if (rtn !== -1) {
                        self.setServer(url, 1, "login");
                    } else {
                        self.setServer(url, 0, "login");
                    }
                    cb();
                }
            }, timeout);
        },
        loginRequest: function(method, url, data, cb, timeout) {
            if (!method || !url) {
                throw new Error("loginRequest can't accept empty method and url as param")
            }
            var k, iframe, id, area, form, params = [],
                hash = "",
                sid;
            method = method.toUpperCase();
            form = document.createElement("form");
            form.style.display = "none";
            form.style.position = "absolute";
            form.method = method;
            form.enctype = "application/x-www-form-urlencoded";
            form.acceptCharset = "UTF-8";
            data = data || {};
            data.cachetime = new Date().getTime();
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
            try{
                form.action = url;
            } catch(e) {
                form.setAttribute('action',url);
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
                setTimeout(function() {
                    iframe = null;
                    form = null
                }, 500);
                sid && clearTimeout(sid);
                e = typeof e === "string" ? e : undef;
                (typeof cb === "function") && cb(e)
            }
            if (timeout > 0) {
                sid = setTimeout(function() {
                    completed("TIMEOUT")
                }, timeout)
            }
            iframe.onerror = iframe.onload = completed;
            iframe.onreadystatechange = function(e) {
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
        }
    };
    return self;
})();

var fp = (function() {
    return {
        report : function() {
            var fp_raw = new Fingerprint({screen_resolution: true,canvas: true,ie_activex: true}).get();
            var fp = md5(fp_raw);
            var path = "/risk?cmd=report";
            var current = (new Date()).getTime();
            if(store.enabled){
                if(store.get('xl_fp')==fp && Util.getCookie('deviceid') && Util.getCookie('deviceid') == store.get('deviceid') && current-parseInt(store.get('xl_fp_rt'))<7*24*3600*1000 ){
                    return true;//上报指纹情况:1.指纹xl_fp发生变化 2.不存在deviceid 3.deviceid发生变化 4.xl_fp七天过期后
                }
            }else{
                if(Util.getCookie('xl_fp')==fp && Util.getCookie('deviceid')){
                    return true;
                }
            }

            Util.loadScript( '/risk?cmd=algorithm&t='+current, function(){
                var xl_fp_sign =  xl_al(fp_raw);
                Util.requestHelper("POST", path, {xl_fp_raw:fp_raw,xl_fp:fp,xl_fp_sign:xl_fp_sign}, function(){
                    if(store.enabled){
                        store.set('xl_fp',fp);
                        store.set('deviceid',Util.getCookie('deviceid'));
                        store.set('xl_fp_rt',current);
                    }else{
                        Util.setCookie('xl_fp',fp,7*24*3600*1000,'xunlei.com');
                    }
                }, 1000, true);
            }, true);
        }
    };
})();

fp.report();