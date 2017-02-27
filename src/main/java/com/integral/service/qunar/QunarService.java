package com.integral.service.qunar;

import com.alibaba.fastjson.JSONObject;
import com.integral.utils.*;
import org.springframework.stereotype.Service;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

/**
 * Created by liuqinghai on 2016/12/12.
 */

@Service
public class QunarService implements IQueryIntegral {
    private Platform mPlatfom = Platform.WINDOWS;
    private static Map<String, Map<String, Object>> mCookie = new HashMap<String, Map<String, Object>>();
    private static final Object lock = new Object();

    @Override
    public JfResult requestVerifyCode(JfRequest request) throws Exception {
        JfResult result;

        String ssloginPage = "https://user.qunar.com/passport/login.jsp?ret=https%3A%2F%2Fwww.qunar.com%2F";
        requestLoginPage(ssloginPage, request.getAccount());

        //发送短信
        String smsUrl = "https://user.qunar.com/passport/getLoginCode.jsp";
        Map<String, Object> cookie;
        synchronized (lock) {
            cookie = mCookie.get(request.getAccount());
            cookie.put("csrfToken",plantCsrfToken());
        }

        String xparams = "mobile="+request.getAccount()+"&vcode=&type=3";
        byte[] content = xparams.getBytes();
        int len = content.length;
        result = sendSms(smsUrl,
                xparams.getBytes(),
                "application/x-www-form-urlencoded; charset=UTF-8",
                len,
                cookie,
                "https://user.qunar.com/passport/login.jsp?ret=https%3A%2F%2Fwww.qunar.com%2F"
        );

        if (!result.getCode().equals(Constants.ErrorCode.ERROR_SUCCESS+"")){
            result.setMessage("login failed");
            result.setPoints("-1");
            return result;
        }
        return result;
    }

    @Override
    public JfResult queryIntegral(JfRequest request) throws Exception {
        Map<String,Object> cookie=mCookie.get(request.getAccount());

        String loginUrl="https://user.qunar.com/passport/loginx.jsp";
        String xparams="loginType=1&ret=https%3A%2F%2Fwww.qunar.com%2F&mobile="+request.getAccount()+"&randcode="+request.getCode()+"&remember=0";
        byte[] content = xparams.getBytes();
        int len = content.length;
        String contentType="application/x-www-form-urlencoded; charset=UTF-8";
        String referer="https://user.qunar.com/passport/login.jsp?ret=https%3A%2F%2Fwww.qunar.com%2F";
        JfResult result=startLogin(loginUrl, xparams.getBytes(), contentType, len, cookie, referer);

        Object csrfToken=cookie.get("csrfToken");
        long time1=System.currentTimeMillis();
        long time2=time1+3;
        String jfurl="http://tips.qunar.com/user/points.json?csrfToken="+csrfToken.toString()+"&callback=jQuery1910045617268602198724_"+time1+"&_="+time2;
        String points = queryPoints(jfurl,cookie);
        result.setPoints(points);
        return result;
    }

    private JfResult startLogin(String link,byte[] bytes, String s, int len, Map<String, Object> cookie, String s1) throws Exception {
        URL url1 = new URL(link);
        HttpsURLConnection connection = (HttpsURLConnection) url1.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        setHeaderProperties(connection);
        setHeaderCookie(connection, buildCookieString(cookie));
        setPostHeaderProperties(connection, s, len + "", s1);

        OutputStream os = connection.getOutputStream();
        os.write(bytes);
        os.close();

        Map<String, Object> newCookie = getConnectionCookie(connection);
        for (String key : newCookie.keySet()){
            cookie.put(key,newCookie.get(key));
        }
        JfResult result=new JfResult();
        String responseData=getPageDocument(connection.getInputStream(),false);
        JSONObject object=JSONObject.parseObject(responseData);
        if (!object.getBooleanValue("ret")){
            result.setCode(Constants.ErrorCode.LOGIN_FAILED+"");
        }else {
            result.setCode(Constants.ErrorCode.ERROR_SUCCESS+"");
        }
        return result;
    }

    /**
     * 请求登陆页面，返回内面内容
     *
     * @param link
     * @return
     * @throws Exception
     */
    private void requestLoginPage(String link, String id) throws Exception {
        URL url = new URL(link);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        setHeaderProperties(connection);
        Map<String, Object> cookie = getConnectionCookie(connection);
        synchronized (lock) {
            mCookie.put(id, cookie);
        }
    }

    /**
     * 开始登陆
     */
    private JfResult sendSms(String url, byte[] content, String contentType, int length, Map<String, Object> cookie, String referer) throws Exception {
        URL url1 = new URL(url);
        HttpsURLConnection connection = (HttpsURLConnection) url1.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        setHeaderProperties(connection);
        setHeaderCookie(connection, buildCookieString(cookie));
        setPostHeaderProperties(connection, contentType, length + "", referer);

        OutputStream os = connection.getOutputStream();
        os.write(content);
        os.close();

        Map<String, Object> newCookie = getConnectionCookie(connection);
        for (String key : newCookie.keySet()){
            cookie.put(key,newCookie.get(key));
        }

        JfResult result=new JfResult();
        String responseData=getPageDocument(connection.getInputStream(),false);
        JSONObject object=JSONObject.parseObject(responseData);
        if (!object.getBooleanValue("ret")){
            result.setCode(Constants.ErrorCode.LOGIN_FAILED+"");
        }else {
            result.setCode(Constants.ErrorCode.ERROR_SUCCESS+"");
        }
        return result;
    }

    /*
    查询积分
     */
    private String queryPoints(String url,Map<String,Object> cookie) throws Exception{
        URL url1=new URL(url);
        HttpURLConnection connection= (HttpURLConnection) url1.openConnection();
        setHeaderProperties(connection);
        setHeaderCookie(connection,buildCookieString(cookie));
        String document=getPageDocument(connection.getInputStream(),false);
        JSONObject object=JSONObject.parseObject(document.substring(document.indexOf('(')+1,document.indexOf(')')));
        if (object.getBooleanValue("ret")){
            JSONObject dataObj=object.getJSONObject("data");
            return dataObj.getIntValue("points")+"";
        }
        return "-1";
    }

    private void setHeaderProperties(HttpURLConnection connection) {
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
        connection.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8");
        connection.setRequestProperty("Accept-Encoding", "gzip, deflate, sdch, br");
        connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
    }

    private void setPostHeaderProperties(HttpURLConnection connection, String contentType, String contentLendth, String referer) {
        connection.setRequestProperty("Content-Length", contentLendth);
        connection.setRequestProperty("Content-Type", contentType);
        connection.setRequestProperty("Referer", referer);
    }

    private void setHeaderCookie(HttpURLConnection connection, String cookie) {
        connection.setRequestProperty("Cookie", cookie);
    }

    private Map<String, Object> getConnectionCookie(HttpURLConnection connection) throws Exception {
        Map<String, Object> cookieObject = new HashMap<String, Object>();
        if (connection == null) return cookieObject;
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
                    cookieObject.put(key, keyvalue);
                }
            }
        }
        return cookieObject;
    }

    private synchronized String buildCookieString(Map<String, Object> cookie) throws Exception {
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

    private String getPageDocument(InputStream inputStream, boolean isGzip) throws Exception {
        if (isGzip) {
            return new String(GZipUtils.decompress(GZipUtils.input2byte(inputStream)));
        }
        return Common.inputStreamToString(inputStream);
    }

    private String plantCsrfToken() {
        String t = "123456789poiuytrewqasdfghjklmnbvcxzQWERTYUIPLKJHGFDSAZXCVBNM";
        String n = "";
        for (int r = 0; r < 31; r++) {
            n += t.charAt((int) Math.ceil(Math.random() * 1e8) % t.length());
        }
        return n;
    }

    private enum Platform {
        WINDOWS(1), LINUX(2);
        int p;

        Platform(int _p) {
            this.p = _p;
        }
    }

    private String makeUUID() {
        return UUID.randomUUID().toString().substring(0,6);
    }
}
