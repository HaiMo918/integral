package com.integral.service.jf360;

import com.alibaba.fastjson.JSONObject;
import com.integral.tools.MD5;
import com.integral.utils.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import javax.net.ssl.*;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by liuqinghai on 2017/1/16.
 */
@Service
public class Jf360Service implements IQueryIntegral{
    private MyX509TrustManager xtm = new MyX509TrustManager();
    private MyHostNameVerify mhv = new MyHostNameVerify();
    private SSLContext sslContext = null;

    public Jf360Service() throws Exception{
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
        Map<String,String> cookie = new HashMap<>();
        JfResult result = new JfResult();
        String user=request.getAccount();
        String password=new MD5().bytesToMD5(request.getPassword().getBytes());
        String token=getToken(cookie,user);
        openJifenPage(cookie);
        get_user_info(cookie);
        login(cookie,user,password,token);
        String apptoken=getAppToken(cookie);
        if ("".equals(apptoken)){
            result.setMessage("登录失败");
            return result;
        }
        String jfurl="http://jifen.360.cn/ajax_signin_count.html?token="+apptoken;
        String jf = getJf(jfurl,cookie);
        result.setPoints(jf);
        return result;
    }

    private void get_user_info(Map<String, String> cookie) throws Exception{
        HttpURLConnection connection = (HttpURLConnection) new URL("http://wan.360.cn/getuserinfo.html").openConnection();
        connection.setRequestProperty("User-Agent",Constants.DEFAULT_UA);
        Common.updateCookie(connection,cookie);
    }

    private void openJifenPage(Map<String, String> cookie) throws Exception{
        HttpURLConnection connection = (HttpURLConnection) new URL("http://jifen.360.cn/").openConnection();
        connection.setRequestProperty("User-Agent",Constants.DEFAULT_UA);
        Common.updateCookie(connection,cookie);
    }


    private String getAppToken(Map<String, String> cookie) throws Exception{
        HttpURLConnection connection = (HttpURLConnection) new URL("http://jifen.360.cn/").openConnection();
        connection.setRequestProperty("User-Agent",Constants.DEFAULT_UA);
        connection.setRequestProperty("Referer","http://jifen.360.cn/");
        connection.setRequestProperty("Cookie",Common.buildCookieString(cookie));
        Common.updateCookie(connection,cookie);

        //String page = new String(GZipUtils.decompress(GZipUtils.input2byte(connection.getInputStream())));
        String page = Common.inputStreamToString(connection.getInputStream());
        Document document = Jsoup.parse(page);
        Elements elements = document.getElementsByTag("script");
        String el = elements.get(0).toString();
        String text=el.substring(el.indexOf('{'),el.lastIndexOf('}')+1);
        JSONObject object = JSONObject.parseObject(text);
        return object.getString("token");
    }


    private String getJf(String url,Map<String,String> cookie) throws Exception{
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("User-Agent",Constants.DEFAULT_UA);
        connection.setRequestProperty("Accept","application/json, text/javascript, */*; q=0.01");
        connection.setRequestProperty("Referer","http://jifen.360.cn/");
        connection.setRequestProperty("Content-Length","0");
        connection.setRequestProperty("Cookie",Common.buildCookieString(cookie));

        OutputStream os = connection.getOutputStream();
        os.write("".getBytes());
        os.close();


        Common.updateCookie(connection,cookie);

        String text=new String(GZipUtils.decompress(GZipUtils.input2byte(connection.getInputStream())));
        JSONObject object = JSONObject.parseObject(text);
        if (object.getIntValue("errno")==0){
            JSONObject dataObj = object.getJSONObject("data");
            if (dataObj.getBooleanValue("ifsignin")){
                int active_days=dataObj.getIntValue("active_days");
                int score=dataObj.getIntValue("score");
                return (active_days*score)+"";
            }
        }
        return null;
    }


    private String getToken(Map<String,String> cookie,String account) throws Exception{
        String url="https://login.360.cn/?func=jQuery11130021198072886692287_1484555554733&src=pcw_wan&from=pcw_wan&charset=UTF-8&requestScema=https&o=sso&m=getToken"
                +"&userName="+account+"&_="+System.currentTimeMillis();
        HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
        connection.setSSLSocketFactory(sslContext.getSocketFactory());
        connection.setHostnameVerifier(mhv);
        connection.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
        connection.setRequestProperty("Cookie",Common.buildCookieString(cookie));
        String text= Common.inputStreamToString(connection.getInputStream());
        String textjson=text.substring(text.indexOf('{'),text.lastIndexOf('}')+1);
        JSONObject object = JSONObject.parseObject(textjson);
        return object.getString("token");
    }

    private void login(Map<String,String> cookie,String accout,String password,String token) throws Exception{
        String param = buildLoginParam(accout,password,token);
        byte[] data = param.getBytes();
        int len = data.length;
        String url = "https://login.360.cn/";
        HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
        connection.setSSLSocketFactory(sslContext.getSocketFactory());
        connection.setHostnameVerifier(mhv);
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("User-Agent",Constants.DEFAULT_UA);
        connection.setRequestProperty("Cookie",Common.buildCookieString(cookie));
        connection.setRequestProperty("Content-Length",len+"");
        connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
        OutputStream os = connection.getOutputStream();
        os.write(data);
        os.close();
        Common.updateCookie(connection,cookie);

        String body = Common.inputStreamToString(connection.getInputStream());
        System.out.println(body);
    }


    private String buildLoginParam(String user,String password,String token) throws Exception{
        StringBuilder sb = new StringBuilder();
        sb.append("src=pcw_wan&from=pcw_wan&charset=UTF-8&requestScema=https")
                .append("&o=sso&m=login&lm=0&captFlag=1&rtype=data&validatelm=0&isKeepAlive=0&captchaApp=i360")
                .append("&userName=").append(user)
                .append("&type=normal")
                .append("&account=").append(user)
                .append("&password=").append(password)
                .append("&captcha=")
                .append("&token=").append(token)
                .append("&proxy=http://wan.360.cn/psp_jump.html").append("&callback=QiUserJsonp81244946&func=QiUserJsonp81244946");
        return sb.toString();
    }


    public static void main(String[] args){
        try {
            Jf360Service service = new Jf360Service();
            JfRequest request = new JfRequest();
            request.setAccount("testjifen");
            request.setPassword("wgy123");
            service.queryIntegral(request);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
