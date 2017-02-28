package com.integral.service.communite.chinamobile;

/**
 * Created by kris on 2017/2/28.
 */
public class LoginData {
    public String accountType="01";
    public String account;
    public String password;
    public String pwdType="02";
    public String smsPwd="";
    public String inputCode="";
    public String backUrl="http://jf.10086.cn/targetUrl=";
    public String rememberMe="0";
    public String channelID="12019";
    public String protocol="https:";
    public long timestamp;

    @Override
    public String toString() {
        return "accountType="+accountType
                +"&account="+account
                +"&password="+password
                +"&pwdType="+pwdType
                +"&smsPwd="+smsPwd
                +"&inputCode="+inputCode
                +"&backUrl="+backUrl
                +"&rememberMe="+rememberMe
                +"&channelID="+channelID
                +"&protocol="+protocol
                +"&timestamp="+timestamp;
    }
}
