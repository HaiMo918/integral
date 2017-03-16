package com.integral.service.meituan;

/**
 * Created by liuqinghai on 2017/3/7.
 */
public class MtLoginData {
    public String email;
    public String password;
    public String captcha;
    public String origin="account-login";
    public String fingerprint="1-3-1-24g|4c|5g|5z|5i|7j|5k|61|5i|54|6g|8d|s1|57|2g|7k|6b|59|5c";
    public String csrf;

    @Override
    public String toString() {
        return "email="+email+"&password="+password+"&captcha="+captcha+"&origin="+origin+"&fingerprint="+fingerprint+"&csrf="+csrf;
    }

    public static final String MT_LOGIN_URL = "https://passport.meituan.com/account/unitivelogin?service=www&continue=http%3A%2F%2Fwww.meituan.com%2Faccount%2Fsettoken%3Fcontinue%3Dhttp%253A%252F%252Fbj.meituan.com%252F";
}
