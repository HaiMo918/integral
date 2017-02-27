package com.integral.service.airline.eastairline;//Created by xiacheng on 16/9/28.

import java.util.Map;

public class EaResult {
    public String points;//积分信息
    public String location;//重定位地址
    public String id;//请求的唯一标识
    public String code;//响应码
    public String message;//响应消息
    public String exception;//异常信息
    public String picurl;//图片验证码url
    public Map<String,String> cookie;
}
