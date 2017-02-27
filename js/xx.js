/**
 * Created by kris on 2017/1/15.
 */

function geRandom() {
    var a8 = new Uint32Array(256);
    window.crypto.getRandomValues(a8);
    return a8.toString();
}


function t(e) {
    var t = 0, n = 0, r = e.length - 1;
    for (r; r >= 0; r--) {
        var i = parseInt(e.charCodeAt(r), 10);
        t = (t << 6 & 268435455) + i + (i << 14), (n = t & 266338304) != 0 && (t ^= n >> 21)
    }
    return t
}

function n() {
    var n = [a.appName, a.version, a.language || a.browserLanguage, a.platform, a.userAgent, f.width, "x", f.height, f.colorDepth, u.referrer].join(""), r = n.length, i = e.history.length;
    while (i)n += i-- ^ r++;
    return (Math.round(Math.random() * 2147483647) ^ t(n)) * 2147483647
}

var i = "__guid", s = m.get(i);
if (!s) {
    s = [t(o ? "" : u.domain), n(), +(new Date) + Math.random() + Math.random()].join(".");
    var c = {expires: 2592e7, path: "/"};
    if (r) {
        var h = "." + r;
        if (l.indexOf(h) > 0 && l.lastIndexOf(h) == l.length - h.length || l == h) c.domain = h
    }
    m.set(i, s, c)
}
return function () {
    return s
}