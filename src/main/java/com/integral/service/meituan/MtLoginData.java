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
}
