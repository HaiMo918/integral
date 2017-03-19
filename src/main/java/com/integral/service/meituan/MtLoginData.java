package com.integral.service.meituan;

import java.util.ArrayList;

/**
 * Created by liuqinghai on 2017/3/7.
 */
public class MtLoginData {
    public String mobile;
    public String code="";
    public String captcha;
    public String origin="account-login";
    public String fingerprint="1-3-1-24g|4c|5g|5z|5i|7j|5k|61|5i|54|6g|8d|s1|57|2g|7k|6b|59|5c";
    public String csrf;

    @Override
    public String toString() {
        return "mobile="+mobile
                +"&code="+code
                +"&login-captcha="
                +"&origin="+origin+"&fingerprint="+fingerprint+"&csrf="+csrf;
    }

    public static final String MT_LOGIN_URL = "https://passport.meituan.com/account/unitivelogin?service=www&continue=http%3A%2F%2Fwww.meituan.com%2Faccount%2Fsettoken%3Fcontinue%3Dhttp%253A%252F%252Fbj.meituan.com%252F";
    public static ArrayList<String> MT_AGENT = new ArrayList<String>()
    {
        {
            add("Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; rv:11.0) like Gecko");
            add("Mozilla/5.0 (Windows NT 6.1; Trident/7.0; rv:11.0) like Gecko");
            add("Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
            add("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");
            add("Mozilla/5.0 (Windows NT 6.1; rv:52.0) Gecko/20100101 Firefox/52.0");
        }
    };

    //mobile=15202823217&login-captcha=&code=408821&origin=account-login&fingerprint=0-5-1-283%7C2p%7C4n%7C4x%7C4n%7C6d%7C5v%7C5q%7C4y%7C4o%7C54%7Chhr%7C75%7C9z%7C4g%7C61%7C5b&csrf=QYORFohd-4-ecMb8g7FQC5C9ZLrG06L9sYIE
}
