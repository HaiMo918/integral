package com.integral.service.suning;

import java.net.URLEncoder;

/**
 * Created by kris on 2017/3/21.
 */
public class SuningLoginParam {
    public boolean jsonViewType=true;
    public String username;
    public String password="";
    public String password2;
    public String loginTheme="b2c";
    public String service="https://ssl.suning.com/webapp/wcs/stores/auth?targetUrl=http%3A%2F%2Fsearch.suning.com%2F%25E7%25A7%25AF%25E5%2588%2586%25E5%2595%2586%25E5%259F%258E%2F%3Fsrc%3Dssds_%25E7%25A7%25AF%25E5%2588%2586%25E5%2585%2591%25E6%258D%25A2_recreword_1-2_c_0000000000_%25E7%25A7%25AF%25E5%2588%2586%25E5%2595%2586%25E5%259F%258E_0";
    public boolean rememberMe=false;
    public String client="app";


    @Override
    public String toString(){
        String s = null;
        try{
            s= "jsonViewType="+jsonViewType
                    +"&username="+username
                    +"&password="+password
                    +"&password2="+ URLEncoder.encode(password2,"UTF-8")
                    +"&loginTheme="+loginTheme
                    +"&service="+URLEncoder.encode(service,"UTF-8")
                    +"&rememberMe="+rememberMe
                    +"&client="+client;
        }catch (Exception e){
            e.printStackTrace();
        }
        return s;
    }
}
