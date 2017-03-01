package com.integral.service.creditcard.zhongxin;//Created by xiacheng on 16/10/10.

import com.alibaba.fastjson.JSONObject;
import com.integral.tools.MD5;
import com.integral.utils.*;

import org.springframework.stereotype.Service;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ZhongXin implements IQueryIntegral {
    private static Map<String,Map<String,String>> mCookies= new HashMap<String,Map<String,String>>();
    private static Map<String,String> mPageAndUcseqs = new HashMap<String,String>();
    @Override
    public synchronized JfResult requestVerifyCode(JfRequest request) throws Exception {
        JfResult result = new JfResult();

        return result;
    }

    @Override
    public synchronized JfResult queryIntegral(JfRequest request) throws Exception {
        JfResult result = new JfResult();

        return result;
    }





    public static void main(String[] args){
        try{
            ZhongXin zhongXin = new ZhongXin();
            JfRequest request=new JfRequest();
            request.setAccount("13761187007");
            request.setPassword("abc123");

            JfResult result = zhongXin.requestVerifyCode(request);
            System.out.println(result.getMessage());
            System.out.println("请输入动态验证码：");
            Scanner scanner = new Scanner(System.in);
            String code = scanner.nextLine();
            request.setCode(code);
            result = zhongXin.queryIntegral(request);
            System.out.println(result.getPoints());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
