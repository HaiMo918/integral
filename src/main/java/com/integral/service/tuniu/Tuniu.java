package com.integral.service.tuniu;

import com.alibaba.fastjson.JSONObject;
import com.integral.tools.MD5;
import com.integral.utils.*;
import org.springframework.stereotype.Service;

import javax.net.ssl.*;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by liuqinghai on 2016/12/8.
 *
 *
 *
 */
@Service
public class Tuniu implements IQueryIntegral{
    private MyX509TrustManager xtm = new MyX509TrustManager();
    private MyHostNameVerify mhv = new MyHostNameVerify();
    private SSLContext sslContext = null;
    private PlatForm mPF=PlatForm.WINDOWS;

    private enum PlatForm{
        WINDOWS(1),LINUX(2);
        int pt;
        PlatForm(int p){
            this.pt=p;
        }
    }

    public Tuniu() throws Exception{
        sslContext = SSLContext.getInstance("SSL");
        X509TrustManager[] xtmArray = new X509TrustManager[]{xtm};
        sslContext.init(null, xtmArray, new java.security.SecureRandom());
    }

    @Override
    public JfResult requestVerifyCode(JfRequest request) throws Exception {
        return null;
    }

    @Override
    public JfResult queryIntegral(JfRequest request) throws Exception {
        JfResult result=new JfResult();
        Map<String,String> cookie = new HashMap<>();
        openLoginPage(cookie);
        String ssoContentUrl = doLoginTuniu(request.getAccount(),request.getPassword(),cookie);
        if (ssoContentUrl==null||!ssoContentUrl.contains("token=")){
            result.setMessage("[途牛]登录失败");
            return result;
        }

        accessSSOConnect(ssoContentUrl,cookie);
        if (!cookie.containsKey("tuniuuser_id")){
            result.setMessage("[途牛]登录失败");
            return result;
        }

        String points = queryTuniuIntegral(cookie);
        if (points==null){
            result.setMessage("[途牛]获取积分失败");
            return result;
        }

        result.setMessage("[途牛]获取积分成功");
        result.setPoints(points);

        logOut(cookie);

        return result;
    }


    private void openLoginPage(Map<String,String> cookie) throws Exception{
        final String url = "https://passport.tuniu.com/login?origin=http://www.tuniu.com/ssoConnect";
        HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
        connection.setHostnameVerifier(mhv);
        connection.setSSLSocketFactory(sslContext.getSocketFactory());
        connection.setRequestProperty(Constants.HttpHeaders.USER_AGENT,Constants.DEFAULT_UA);
        connection.setRequestProperty(Constants.HttpHeaders.ACCEPT,"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        connection.setRequestProperty(Constants.HttpHeaders.ACCEPT_ENCODING,"gzip, deflate, sdch, br");
        Common.updateCookie(connection,cookie);
    }


    private String doLoginTuniu(String name,String pwd,Map<String,String> cookie) throws Exception{
        final String passwordMD5 = new MD5().bytesToMD5(pwd.getBytes());
        final String requestBody = "login_type=P-N&intlCode=&username="+name+"&password="+passwordMD5;
        byte[] bodyBuffer = requestBody.getBytes();
        final String length = String.valueOf(bodyBuffer.length);

        final String url = "https://passport.tuniu.com/login/post";
        HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
        connection.setSSLSocketFactory(sslContext.getSocketFactory());
        connection.setHostnameVerifier(mhv);
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty(Constants.HttpHeaders.USER_AGENT,Constants.DEFAULT_UA);
        connection.setRequestProperty(Constants.HttpHeaders.CONTENT_TYPE,"application/x-www-form-urlencoded");
        connection.setRequestProperty(Constants.HttpHeaders.CONTENT_LENGTH,length);
        connection.setRequestProperty(Constants.HttpHeaders.COOKIE,Common.buildCookieString(cookie));
        connection.setRequestProperty(Constants.HttpHeaders.REFERER,"https://passport.tuniu.com/login?origin=http://www.tuniu.com/ssoConnect");
        connection.setInstanceFollowRedirects(false);

        OutputStream os = connection.getOutputStream();
        os.write(bodyBuffer);
        os.flush();
        os.close();

        Common.updateCookie(connection,cookie);

        return connection.getHeaderField("Location");
    }

    private void accessSSOConnect(String url,Map<String ,String> cookie) throws Exception{
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setInstanceFollowRedirects(false);
        connection.setRequestProperty(Constants.HttpHeaders.USER_AGENT,Constants.DEFAULT_UA);
        connection.setRequestProperty(Constants.HttpHeaders.COOKIE,Common.buildCookieString(cookie));
        Common.updateCookie(connection,cookie);
    }


    private String queryTuniuIntegral(Map<String,String> cookie) throws Exception{
        final String url = "https://i.tuniu.com/usercenter/usercenter/userPromotionInfo";
        HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
        connection.setHostnameVerifier(mhv);
        connection.setSSLSocketFactory(sslContext.getSocketFactory());
        connection.setRequestProperty(Constants.HttpHeaders.COOKIE,Common.buildCookieString(cookie));
        Common.updateCookie(connection,cookie);

        String responseBody = Common.inputStreamToString(connection.getInputStream());
        //connection.setRequestProperty(Constants.HttpHeaders.COOKIE,Common.buildCookieString(cookie));
        JSONObject object = JSONObject.parseObject(responseBody);
        if (object.getBooleanValue("success")){
            JSONObject data = object.getJSONObject("data");
            return data.getIntValue("totalCredits")+"";
        }
        return null;
    }

    private void logOut(Map<String,String> cookie) throws Exception{
        final String url = "http://www.tuniu.com/u/logout";
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestProperty(Constants.HttpHeaders.USER_AGENT,Constants.DEFAULT_UA);
        connection.setRequestProperty(Constants.HttpHeaders.COOKIE,Common.buildCookieString(cookie));
    }


    public static void main(String[] args){
        try{
            JfRequest request = new JfRequest();
            request.setPassword("ilikejf1");
            request.setAccount("18017108839");
            Tuniu tuniu = new Tuniu();
            JfResult result=tuniu.queryIntegral(request);
            System.out.println(result.getPoints());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
