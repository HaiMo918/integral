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

function getMd5(text){
    var fp = md5(text);
    return fp;
}
