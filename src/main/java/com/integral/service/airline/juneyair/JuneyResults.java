package com.integral.service.airline.juneyair;//Created by xiacheng on 16/9/13.

import java.util.HashMap;
import java.util.Map;

public class JuneyResults {
    public String id;
    public String location;//重定位URL
    public String exception;//异常消息
    public Map<String,String> extraInfo;
    public int code;//响应码
    public String message;//响应消息
    public String points;//积分
    public String picurl;//图片验证码的URL链接,需要返回给客户端

    public String getExtraInfo(String key){
        if (extraInfo!=null && extraInfo.containsKey(key)){
            return extraInfo.get(key);
        }
        return null;
    }

    public void putExtraInfo(String key,String value){
        if (extraInfo==null){
            extraInfo = new HashMap<String,String >();
            extraInfo.put(key,value);
        }
    }

    public class ExtraInfo{
        public static final String LT = "lt";
    }
}
