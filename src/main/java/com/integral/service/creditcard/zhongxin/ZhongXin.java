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
        String mID = request.getAccount();
        Map<String,String> mCookie = new HashMap<String,String>();
        openLoginPage(mCookie,mID);
        ZxResponse response = doLogin(request.getAccount(),request.getPassword(),mCookie,mID);
        if (!"0000000".equals(response.getRtnCode())){
            result.setMessage(response.getRetMsg());
            result.setCode(response.getRtnCode());
            result.setId(mID);
            return result;
        }
        sendSms01(mCookie);
        SmsResponse smsResponse = sendSms02(mCookie,mID);
        if (!"0000000".equals(smsResponse.getRtnCode())){
            result.setMessage(response.getRetMsg());
            result.setCode(response.getRtnCode());
            result.setId(mID);
            return result;
        }
        mPageAndUcseqs.put("mainpage",response.getRedirectUrl());
        result.setMessage(response.getRetMsg());
        result.setCode(response.getRtnCode());
        result.setId(mID);
        System.out.println(smsResponse.getRtnMsg());
        return result;
    }

    @Override
    public synchronized JfResult queryIntegral(JfRequest request) throws Exception {
        JfResult result = new JfResult();
        Map<String,String> cookie = mCookies.get(request.getAccount());
        SmsCheckResponse response = checkSms(request.getCode(),cookie,request.getAccount());
        if (!"0000000".equals(response.getRtnCode())){
            result.setMessage(response.getRtnMsg());
            return result;
        }

        String ucseq = response.getUcseq();
        cookie.put("ucseq",ucseq);
        // TODO: 16-11-29 open main page
        String mainpage = mPageAndUcseqs.get("mainpage");
        String integralPage = openMainPage(mainpage,cookie,request.getAccount());
        String baseUrl = "https://creditcard.ecitic.com/citiccard/newonline/";
        // TODO: 16-11-29 get integral
        result=getIntegralScore(cookie);
        return result;
    }
    private JfResult getIntegralScore(Map<String,String> cookie) throws Exception{
        String url = "https://creditcard.ecitic.com/citiccard/newonline/score.do?func=integralQuery";
        URL url1=new URL(url);
        HttpsURLConnection connection = (HttpsURLConnection) url1.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Cookie",buildCookieString(cookie));
        connection.setRequestProperty("User-Agent",Constants.DEFAULT_UA);
        connection.setRequestProperty("Content-Length","0");
        connection.setRequestProperty("Accept","application/xml, text/xml, */*; q=0.01");
        connection.setRequestProperty("Accept-Language","zh-cn");
        connection.setRequestProperty("Referer","https://creditcard.ecitic.com/citiccard/newonline/score.do?func=scoreQuery&menuId=a6db812a872b46beb546b48882fe7d9d");
        connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded;charset=GBK");

        OutputStream os = connection.getOutputStream();
        os.write("".getBytes());
        os.flush();
        os.close();

        InputStream is = connection.getInputStream();
        String line;
        StringBuilder content = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        while ((line=br.readLine())!=null){
            content.append(line);
        }
        br.close();
        is.close();

        JfResult result = new JfResult();
        String data = content.toString();
        String regRtnCode="retcode=\"\\d{1,5}\"";
        Pattern pattern=Pattern.compile(regRtnCode);
        Matcher matcher = pattern.matcher(data);
        if (matcher.find()){
            String rtnCodeText=matcher.group(0);
            if (rtnCodeText!=null){
                String code = rtnCodeText.substring(rtnCodeText.indexOf('\"')+1,rtnCodeText.lastIndexOf("\""));
                if ("0".equals(code)){
                    String regScorePat = "total_integral=\"\\d{1,10}\"";
                    pattern = Pattern.compile(regScorePat);
                    matcher = pattern.matcher(data.replace(",",""));
                    if (matcher.find()){
                        String text = matcher.group(0);
                        if (text!=null){
                            String score = text.substring(text.indexOf("\"")+1,text.lastIndexOf("\""));
                            result.setPoints(score);
                            result.setMessage("中信信用卡积分查询成功");
                        }
                    }
                }else {
                    result.setPoints("-1");
                    result.setMessage("中信信用卡积分查询失败");
                }
            }
        }
        return result;
    }

    private String openMainPage(String page,Map<String,String > cookie,String id) throws Exception{
        URL url = new URL(page);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestProperty("User-Agent",Constants.DEFAULT_UA);
        connection.setRequestProperty("Accept-Encoding","gzip, deflate, br");
        connection.setRequestProperty("Accept-Language","en-US,en;q=0.5");
        connection.setRequestProperty("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        connection.setRequestProperty("Referer","https://creditcard.ecitic.com/citiccard/ucweb/sendSmsInit.do");
        connection.setRequestProperty("Cookie",buildCookieString(cookie));
        connection.setRequestProperty("Host","creditcard.ecitic.com");
        updateCookie(connection,cookie,id);

        InputStream is = connection.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line;
        StringBuilder sb = new StringBuilder();
        while ((line=br.readLine())!=null){
            sb.append(line);
        }
        br.close();
        is.close();
        String regPattern = "settingManage\\.do\\?func=baseSetting&menuId=.{32}";
        Pattern pattern = Pattern.compile(regPattern);
        Matcher matcher = pattern.matcher(sb.toString());
        if (matcher.find()){
            int groupconout = matcher.groupCount();
            String group =  matcher.group(groupconout);
            return group;
        }
        return null;
    }
    private void openLoginPage(Map<String,String> mCookie,String id) throws Exception{
        String url = "https://creditcard.ecitic.com/citiccard/ucweb/entry.do";
        URL page = new URL(url);
        HttpsURLConnection connection = (HttpsURLConnection) page.openConnection();
        connection.setRequestProperty("User-Agent",Constants.DEFAULT_UA);
        connection.setRequestProperty("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        connection.setRequestProperty("Accept-Encoding","gzip, deflate, br");
        connection.setRequestProperty("Accept-Language","en-US,en;q=0.5");
        updateCookie(connection,mCookie,id);
    }

    private ZxResponse doLogin(String phone,String pwd,Map<String,String> cookie,String id) throws Exception{
        String page = "https://creditcard.ecitic.com/citiccard/ucweb/login.do?date="+System.currentTimeMillis();
        ZxRequest request = new ZxRequest();
        request.setPhone(phone);
        MD5 md5 = new MD5();
        request.setMemcode(md5.bytesToMD5(pwd.getBytes()));
        String param = JSONObject.toJSONString(request);
        URL url = new URL(page);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("User-Agen",Constants.DEFAULT_UA);
        connection.setRequestProperty("Accept","application/json, text/javascript, */*; q=0.01");
        connection.setRequestProperty("Content-Type","application/json");
        connection.setRequestProperty("Accept-Language","en-US,en;q=0.5");
        connection.setRequestProperty("Host","creditcard.ecitic.com");
        connection.setRequestProperty("Referer","https://creditcard.ecitic.com/citiccard/ucweb/entry.do");
        connection.setRequestProperty("Content-Length",param.getBytes().length+"");
        connection.setRequestProperty("Cookie",buildCookieString(cookie));

        OutputStream os = connection.getOutputStream();
        os.write(param.getBytes());
        os.flush();
        os.close();

        updateCookie(connection,cookie,id);

        InputStream is = connection.getInputStream();
        StringBuilder sb = new StringBuilder();
        String line;
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        while ((line=br.readLine())!=null){
            sb.append(line);
        }
        br.close();
        is.close();

        ZxResponse response = JSONObject.parseObject(sb.toString(),ZxResponse.class);
        return response;
    }

    private void sendSms01(Map<String,String> cookie) throws Exception{
        URL url = new URL("https://creditcard.ecitic.com/citiccard/ucweb/sendSmsInit.do");
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestProperty("User-Agent",Constants.DEFAULT_UA);
        connection.setRequestProperty("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        connection.setRequestProperty("Accept-Encoding","gzip, deflate, br");
        connection.setRequestProperty("Accept-Language","en-US,en;q=0.5");
        connection.setRequestProperty("Cookie",buildCookieString(cookie));
        connection.setRequestProperty("Referer","https://creditcard.ecitic.com/citiccard/ucweb/entry.do");
    }

    private SmsResponse sendSms02(Map<String,String > cookie,String id) throws Exception{
        SmsResponse response = null;
        URL url = new URL("https://creditcard.ecitic.com/citiccard/ucweb/sendSms.do?date="+System.currentTimeMillis());
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("User-Agent",Constants.DEFAULT_UA);
        connection.setRequestProperty("Accept","application/json, text/javascript, */*; q=0.01");
        connection.setRequestProperty("Accept-Encoding","gzip, deflate, br");
        connection.setRequestProperty("Accept-Language","en-US,en;q=0.5");
        connection.setRequestProperty("Cookie",buildCookieString(cookie));
        connection.setRequestProperty("Referer","https://creditcard.ecitic.com/citiccard/ucweb/sendSmsInit.do");
        connection.setRequestProperty("Content-Type","multipart/form-data");
        connection.setRequestProperty("Content-Length","0");

        OutputStream os = connection.getOutputStream();
        os.write("".getBytes());
        os.flush();
        os.close();

        updateCookie(connection,cookie,id);
        InputStream is = connection.getInputStream();
        StringBuilder sb = new StringBuilder();
        String line;
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        while ((line=br.readLine())!=null){
            sb.append(line);
        }
        br.close();
        is.close();
        response = JSONObject.parseObject(sb.toString(),SmsResponse.class);
        return response;
    }

    private SmsCheckResponse checkSms(String sms,Map<String,String> cookie,String id) throws Exception{
        String param = String.format(Locale.getDefault(),"{\"smsCode\":\"%s\"}",sms);
        int length = param.getBytes().length;
        URL url = new URL("https://creditcard.ecitic.com/citiccard/ucweb/checkSms.do?date="+System.currentTimeMillis());
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Host","creditcard.ecitic.com");
        connection.setRequestProperty("User-Agent",Constants.DEFAULT_UA);
        connection.setRequestProperty("Accept","application/json, text/javascript, */*; q=0.01");
        connection.setRequestProperty("Content-Type","application/json");
        connection.setRequestProperty("Accept-Language","en-US,en;q=0.5");
        connection.setRequestProperty("Accept-Encoding","gzip, deflate, br");
        connection.setRequestProperty("Referer","https://creditcard.ecitic.com/citiccard/ucweb/sendSmsInit.do");
        connection.setRequestProperty("Content-Length",length+"");
        connection.setRequestProperty("Cookie",buildCookieString(cookie));

        OutputStream os = connection.getOutputStream();
        os.write(param.getBytes());
        os.flush();
        os.close();

        updateCookie(connection,cookie,id);

        InputStream is = connection.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line=br.readLine())!=null){
            sb.append(line);
        }
        br.close();
        is.close();
        return JSONObject.parseObject(sb.toString(),SmsCheckResponse.class);
    }

    private synchronized void updateCookie(HttpsURLConnection connection,Map<String,String>mCookie,String id) throws Exception{
        if (connection != null) {
            Map<String, List<String>> headerFields = connection.getHeaderFields();
            Set<String> fields = headerFields.keySet();
            if (fields.contains("Set-Cookie")) {
                List<String> cookieValues = headerFields.get("Set-Cookie");
                for (String value : cookieValues) {
                    int firstSemicolon = value.indexOf(';');
                    if (firstSemicolon != -1) {
                        String cookie = value.substring(0, firstSemicolon);
                        int firstColon = cookie.indexOf('=');
                        String key = cookie.substring(0, firstColon);
                        String keyvalue = cookie.substring(firstColon + 1);
                        mCookie.put(key, keyvalue);
                    }
                }
            }
            mCookies.put(id,mCookie);
        }
    }


    private synchronized String buildCookieString(Map<String, String> cookie) throws Exception {
        StringBuilder data = new StringBuilder();
        for (String key : cookie.keySet()) {
            data.append(key).append("=");
            if (cookie.get(key) != null) {
                data.append(cookie.get(key));
            }
            data.append(";");
        }
        return data.toString();
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
