package com.integral.service.qunar;

/**
 * Created by liuqinghai on 2016/12/19.
 */
public class QunarLoginData {
    public int loginType=0;
    public String ret="https%3A%2F%2Fwww.qunar.com%2F";
    public String username;
    public String password;
    public String vcode;
    public int remember=0;

    public String toFormedString(){
        return "loginType="+loginType
                +"&ret="+ret
                +"&username="+username
                +"&password="+password
                +"&vcode="+vcode
                +"&remember="+remember;
    }
}
