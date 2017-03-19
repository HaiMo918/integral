package com.integral.service.miui;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.integral.tools.Decrypter;
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

        doStaticLogin(cookie,"https://account.xiaomi.com"+var.login_cn_html);

        final long t = System.currentTimeMillis();
        final String loginURL = "https://account.xiaomi.com/pass/serviceLoginAuth2?_dc="+t;
        final String callback=URLEncoder.encode(var.callback,"UTF-8");
        final String sid = URLEncoder.encode(var.sid,"UTF-8");
        final String qs = URLEncoder.encode(var.qs,"UTF-8");
        final String sign = URLEncoder.encode(var._sign,"UTF-8");
        final String sParam = URLEncoder.encode(var.serviceParam,"UTF-8");
        final String loginParam = "_json=true"+"&callback="+callback +"&sid="+sid +"&qs="+qs+"&_sign="+sign +"&serviceParam="+sParam +"&user="+request.getAccount() +"&hash="+new MD5().bytesToMD5(request.getPassword().getBytes()).toUpperCase();
        MiuiLoginResponse response = doMiuiLogin(cookie,loginURL,loginParam);
        if (response.code!=0){
            result.setMessage("[小米]"+response.desc);
            return result;
        }

        turnToExtraPage(cookie,response.location);
        String points = searchPoints(cookie);
        if (points==null){
            result.setMessage("[小米]获取积分失败");
            return result;
        }
        result.setPoints(points);
        result.setMessage("[小米]获取积分成功");
        logout(cookie);
        return result;
    }

    private void logout(Map<String, String> cookie) throws Exception{
        final String url = "https://account.xiaomi.com/pass/logout?&sid=miuibbs&callback=http%3A%2F%2Fwww.miui.com%2Findex.html";
        HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
        connection.setRequestProperty(Constants.HttpHeaders.USER_AGENT,Constants.DEFAULT_UA);
        connection.setRequestProperty(Constants.HttpHeaders.COOKIE,Common.buildCookieString(cookie));
        connection.setRequestProperty(Constants.HttpHeaders.ACCEPT,"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
    }

    private void turnToExtraPage(Map<String, String> cookie, String location) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL(location).openConnection();
        connection.setRequestProperty(Constants.HttpHeaders.USER_AGENT,Constants.DEFAULT_UA);
        connection.setRequestProperty(Constants.HttpHeaders.COOKIE,Common.buildCookieString(cookie));
        connection.setRequestProperty(Constants.HttpHeaders.ACCEPT,"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        connection.setInstanceFollowRedirects(false);
        Common.updateCookie(connection,cookie);
    }

    private String searchPoints(Map<String, String> cookie) throws Exception{
        final String url = "http://www.miui.com/index.html";
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestProperty(Constants.HttpHeaders.USER_AGENT,Constants.DEFAULT_UA);
        connection.setRequestProperty(Constants.HttpHeaders.COOKIE,Common.buildCookieString(cookie));
        connection.setRequestProperty(Constants.HttpHeaders.ACCEPT,"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        Common.updateCookie(connection,cookie);
        String body =Common.inputStreamToString(connection.getInputStream());
        Document doc = Jsoup.parse(body);
        Element e = doc.getElementsByClass("uinfo_t_box_other").get(0);
        Element e1 = e.child(1);
        Pattern pattern = Pattern.compile("\\d{1,6}");
        Matcher matcher = pattern.matcher(e1.toString());
        return matcher.find()?matcher.group(0):null;
    }

    private void doStaticLogin(Map<String, String> cookie, String staticLoginURL) throws Exception{
        HttpsURLConnection connection = (HttpsURLConnection) new URL(staticLoginURL).openConnection();
        connection.setRequestProperty(Constants.HttpHeaders.USER_AGENT,Constants.DEFAULT_UA);
        connection.setRequestProperty(Constants.HttpHeaders.ACCEPT,"*/*");
        connection.setRequestProperty(Constants.HttpHeaders.COOKIE,Common.buildCookieString(cookie));
    }

    private MiuiLoginResponse doMiuiLogin(Map<String, String> cookie, String loginURL, String loginParam) throws Exception{
        byte[] data = loginParam.getBytes();
        int len = data.length;

        HttpsURLConnection connection = (HttpsURLConnection) new URL(loginURL).openConnection();
        connection.setRequestProperty(Constants.HttpHeaders.USER_AGENT,Constants.DEFAULT_UA);
        connection.setRequestProperty(Constants.HttpHeaders.ACCEPT,"*/*");
        //connection.setHostnameVerifier(mhv);
        //connection.setSSLSocketFactory(sslContext.getSocketFactory());
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

        final String body = Common.inputStreamToString(connection.getInputStream()).replace("&&&START&&&","");
        return JSONObject.parseObject(body,MiuiLoginResponse.class);
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
        //connection.setRequestProperty(Constants.HttpHeaders.COOKIE,Common.buildCookieString(cookie));
        connection.setSSLSocketFactory(sslContext.getSocketFactory());
        connection.setHostnameVerifier(mhv);
        Common.updateCookie(connection,cookie);
        String body = Common.inputStreamToString(connection.getInputStream());
        Pattern pattern = Pattern.compile("var JSP_VAR=\\{  .*\\};var PAGE_VAR");
        Matcher matcher = pattern.matcher(body);
        if (matcher.find()){
            var = new MiuiJspVar();
            var.deviceType="PC";
            var.locale="zh_CN";
            var.region="CN";
            var.showActiveXControl=false;
            var.serviceParam="{\"checkSafePhone\":false}";
            var.sid="miuibbs";
            String text = matcher.group(0);
            text=text.substring(text.indexOf('{')+1,text.lastIndexOf('}'));
            String [] data = text.split(" ");
            String eachLine;
            for (String t : data){
                if (t.isEmpty()) continue;
                if (t.startsWith("callback")){
                    var.callback=t.substring(t.indexOf(':')+2,t.length()-2);
                }else if (t.startsWith("qs")){
                    eachLine = t.split(":")[1];
                    var.qs=eachLine.substring(1,eachLine.length()-2);
                }else if(t.startsWith("hidden")){
                    eachLine = t.split(":")[1];
                    var.hidden=eachLine.substring(1,eachLine.length()-1);
                }else if(t.startsWith("\"_sign\"")){
                    eachLine = t.split(":")[1];
                    var._sign=eachLine.substring(1,eachLine.length()-2);
                }else if(t.startsWith("privacyLink")){
                    var.privacyLink=t.substring(t.indexOf(':')+1,t.length()-1);
                }else if(t.startsWith("dataCenter")){
                    var.dataCenter = t.substring(t.indexOf('\'')+1,t.length()-1);
                }
            }
        }

        pattern = Pattern.compile("/static/res/.*login-cn\\.html");
        matcher = pattern.matcher(body);
        if (matcher.find()){
            var.login_cn_html=matcher.group(0);
        }
        return var;
    }
//
//    public static void main(String[] args){
//        try{
//            JfRequest request = new JfRequest();
//            request.setAccount("13366183868");
//            request.setPassword("666xiaomi");
//
//
//            MiuiService service = new MiuiService();
//            JfResult result = service.queryIntegral(request);
//            System.out.println(result.getPoints());
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }

}
