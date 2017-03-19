package com.integral.service.miui;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.integral.tools.MD5;
import com.integral.utils.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by kris on 2017/1/16.
 */
@Service
public class MiuiService implements IQueryIntegral {
    private MyX509TrustManager xtm = new MyX509TrustManager();
    private MyHostNameVerify mhv = new MyHostNameVerify();
    private SSLContext sslContext = null;
    public MiuiService() throws Exception{
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
        JfResult result = new JfResult();
        Map<String,String> cookie = new HashMap<>();
        String url = member_php(cookie);
        if (url == null){
            result.setMessage("[小米]获取重定向页面失败");
            return result;
        }

        MiuiJspVar var = extractMiuiJspVar(cookie,url);
        if (var == null){
            result.setMessage("[小米]解析预登陆JSON数据失败");
            return result;
        }

        final long t = System.currentTimeMillis();
        final String loginURL = "https://account.xiaomi.com/pass/serviceLoginAuth2?_dc="+t;
        final String loginParam = "_json=true"+"&callback="+var.callback
                +"&sid="+var.sid
                +"&qs="+var.qs
                +"&_sign="+var._sign
                +"&serviceParam="+var.serviceParam
                +"&user="+request.getAccount()
                +"&hash="+new MD5().bytesToMD5(request.getPassword().getBytes()).toUpperCase();
        MiuiLoginResponse response = doMiuiLogin(cookie,loginURL,loginParam);
        return result;
    }

    private MiuiLoginResponse doMiuiLogin(Map<String, String> cookie, String loginURL, String loginParam) throws Exception{
        byte[] data = loginParam.getBytes();
        int len = data.length;

        HttpsURLConnection connection = (HttpsURLConnection) new URL(loginURL).openConnection();
        connection.setRequestProperty(Constants.HttpHeaders.USER_AGENT,Constants.DEFAULT_UA);
        connection.setRequestProperty(Constants.HttpHeaders.ACCEPT,"*/*");
        connection.setHostnameVerifier(mhv);
        connection.setSSLSocketFactory(sslContext.getSocketFactory());
        connection.setDoOutput(true);
        connection.setRequestMethod(Constants.REQUEST_METHOD_POST);
        connection.setRequestProperty(Constants.HttpHeaders.CONTENT_TYPE,"application/x-www-form-urlencoded");
        connection.setRequestProperty(Constants.HttpHeaders.CONTENT_LENGTH,String.valueOf(len));
        connection.setRequestProperty(Constants.HttpHeaders.COOKIE,Common.buildCookieString(cookie));

        OutputStream os = connection.getOutputStream();
        os.write(data);
        os.flush();
        os.close();

        Common.updateCookie(connection,cookie);

        final String body = Common.inputStreamToString(connection.getInputStream());
        return null;
    }


    //打开登录页，返回重定向的页面
    private String member_php(Map<String,String> cookie) throws Exception{
        final String url = "http://www.miui.com/member.php?mod=logging&action=miuilogin";
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestProperty(Constants.HttpHeaders.USER_AGENT,Constants.DEFAULT_UA);
        connection.setInstanceFollowRedirects(false);
        Common.updateCookie(connection,cookie);
        return connection.getHeaderField("Location");
    }

    private MiuiJspVar extractMiuiJspVar(Map<String,String> cookie,String url) throws Exception{
        MiuiJspVar var = null;
        HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
        connection.setRequestProperty(Constants.HttpHeaders.USER_AGENT,Constants.DEFAULT_UA);
        connection.setRequestProperty(Constants.HttpHeaders.COOKIE,Common.buildCookieString(cookie));
        connection.setSSLSocketFactory(sslContext.getSocketFactory());
        connection.setHostnameVerifier(mhv);

        String body = Common.inputStreamToString(connection.getInputStream());
        Pattern pattern = Pattern.compile("var JSP_VAR=\\{  .*\\};var PAGE_VAR");
        Matcher matcher = pattern.matcher(body);
        if (matcher.find()){
            var = new MiuiJspVar();
            var.deviceType="PC";
            var.dataCenter="lg";
            var.locale="zh_CN";
            var.region="CN";
            var.showActiveXControl=false;
            var.serviceParam="{\"checkSafePhone\":false}";
            var.sid="miuibbs";
            String text = matcher.group(0);
            text=text.substring(text.indexOf('{')+1,text.lastIndexOf('}')).replaceAll(" ","");
            String [] data = text.split(",");
            String eachLine;
            for (String t : data){
                if (t.startsWith("callback")){
                    eachLine = t.split(":")[1];
                    var.callback=eachLine.substring(1,eachLine.length()-1);
                }else if (t.startsWith("qs")){
                    eachLine = t.split(":")[1];
                    var.qs=eachLine.substring(1,eachLine.length()-1);
                }else if(t.startsWith("hidden")){
                    eachLine = t.split(":")[1];
                    var.hidden=eachLine.substring(1,eachLine.length()-1);
                }else if(t.startsWith("_sign")){
                    eachLine = t.split(":")[1];
                    var._sign=eachLine.substring(1,eachLine.length()-1);
                }else if(t.startsWith("privacyLink")){
                    eachLine = t.split(":")[1];
                    var.privacyLink=eachLine.substring(1,eachLine.length()-1);
                }
            }
        }
        return var;
    }

    public static void main(String[] args){
        try{
            JfRequest request = new JfRequest();
            request.setAccount("13366183868");
            request.setPassword("666xiaomi");


            MiuiService service = new MiuiService();
            JfResult result = service.queryIntegral(request);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
