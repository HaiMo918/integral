package com.integral.service.airline.chinasouth;//Created by xiacheng on 16/9/28.

import com.alibaba.fastjson.JSONObject;
import com.integral.utils.*;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;

@Service
public class ChinaSouth implements IQueryIntegral{
    private Map<String,String> mCookie;
    @Override
    public JfResult requestVerifyCode(JfRequest request) throws Exception {
        return null;
    }

    @Override
    public JfResult queryIntegral(JfRequest request) throws Exception {
        JfResult jfResult = new JfResult();
        mCookie = new HashMap<String, String>();
        String loginResult = doLogin(request.getAccount(),request.getPassword());
        if (!"200".equals(loginResult)){
            jfResult.setCode(Constants.ErrorCode.LOGIN_FAILED+"");
            jfResult.setMessage("南航登录失败");
            return jfResult;
        }
        openOrderManagementFrame();
        String points = doCheckLogin();
        jfResult.setMessage("南航积分查询成功");
        jfResult.setPoints(points);
        return jfResult;
    }

    private String doLogin(String userID,String passWord) throws Exception {
        String param = buildLoginParam(userID,passWord);
        int length = param.getBytes().length;
        URL url = new URL(CSConstants.LOGIN_PAGE);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setRequestMethod("POST");
        httpConn.setDoOutput(true);
        httpConn.setRequestProperty("User-Agent",CSConstants.CS_USER_AGENT);
        httpConn.setRequestProperty("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");
        httpConn.setRequestProperty("Content-Length",String.valueOf(length));
        httpConn.setRequestProperty("Accept-Encoding","gzip, deflate");
        httpConn.setRequestProperty("Accept-Language","zh-CN,zh;q=0.8");

        OutputStream os = httpConn.getOutputStream();
        os.write(param.getBytes());
        os.close();
        updateCookie(httpConn);
        return httpConn.getResponseCode()+"";
    }


    private void openOrderManagementFrame() throws Exception{
        URL url = new URL(CSConstants.ORDER_MANAGE_FRAME);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setRequestMethod("GET");
        httpConn.setRequestProperty("User-Agent",CSConstants.CS_USER_AGENT);
        httpConn.setRequestProperty("Accept-Encoding","gzip, deflate, sdch");
        httpConn.setRequestProperty("Accept-Language","zh-CN,zh;q=0.8");
        httpConn.setRequestProperty("Cookie",buildCookieString(mCookie));
        updateCookie(httpConn);
    }


    private String doCheckLogin() throws Exception{
        URL url = new URL(CSConstants.CHECK_LOGIN);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setRequestMethod("POST");
        httpConn.setDoOutput(true);
        httpConn.setRequestProperty("User-Agent",CSConstants.CS_USER_AGENT);
        httpConn.setRequestProperty("Accept","application/json, text/javascript, */*; q=0.01");
        httpConn.setRequestProperty("Accept-Encoding","gzip, deflate");
        httpConn.setRequestProperty("Accept-Language","zh-CN,zh;q=0.8");
        httpConn.setRequestProperty("Cookie",buildCookieString(mCookie));
        httpConn.setRequestProperty("Content-Length","0");

        OutputStream os = httpConn.getOutputStream();
        os.write("".getBytes());
        os.close();

        updateCookie(httpConn);

        String data = null;
        Map<String,List<String>> fields = httpConn.getHeaderFields();
        if (fields.containsKey("Content-Encoding")){
            if (httpConn.getHeaderField("Content-Encoding").equals("gzip")) {
                data = unzipGZIP(httpConn.getInputStream());
            }
        }else {
            int length = Integer.parseInt(httpConn.getHeaderField("Content-Length"));
            byte[] buffer = new byte[length];
            InputStream is = httpConn.getInputStream();
            is.read(buffer);
            is.close();
            data=new String(buffer,"UTF-8");
        }
        JSONObject jsonObject = JSONObject.parseObject(data);
        String points = null;
        boolean success = jsonObject.getBooleanValue("success");
        if (success==true){
            JSONObject dataObj = jsonObject.getJSONObject("data");
            points = dataObj.getString("points");
        }
        return points;
    }

    private void updateCookie(HttpURLConnection conn) throws Exception {
        if (conn == null) {
            return;
        }
        Map<String, List<String>> headerFields = conn.getHeaderFields();
        Set<String> fields = headerFields.keySet();
        if (!fields.contains("Set-Cookie")) {
            return;
        }
        List<String> cookieValues = headerFields.get("Set-Cookie");
        for (String value : cookieValues) {
            int firstSemicolon = value.indexOf(';');
            if (firstSemicolon == -1) {
                continue;
            }
            String cookie = value.substring(0, firstSemicolon);
            int firstColon = cookie.indexOf('=');
            String theKey = cookie.substring(0, firstColon);
            String theValue = cookie.substring(firstColon + 1);
            mCookie.put(theKey, theValue);
        }
    }

    private String buildCookieString(Map<String,String> cookie) throws Exception {
        StringBuilder data = new StringBuilder();
        for (String key : cookie.keySet()) {
            data.append(key).append("=");
            if (cookie.get(key) != null) {
                data.append(cookie.get(key));
            }
            data.append(";");
        }
        String cookies = data.toString();
        return cookies.substring(0, cookies.length() - 1);
    }

    private String buildLoginParam(String userID,String password) {
        return "userId=" + userID + "&passWord=" + password + "&memberType=1&loginType=1&vcode=";
    }

    private String unzipGZIP(InputStream in) throws Exception {
        GZIPInputStream gInputStream = new GZIPInputStream(in);
        byte[] by = new byte[1024];
        StringBuffer strBuffer = new StringBuffer();
        int len = 0;
        while ((len = gInputStream.read(by)) != -1) {
            strBuffer.append(new String(by, 0, len));
        }
        return strBuffer.toString();
    }


}
