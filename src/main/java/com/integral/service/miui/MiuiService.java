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
    private static Map<String,Map<String,String>> muuiCookie;
    private static boolean debug=true;
    private static Map<String,Map<String,String>> extraData;
    private static Map<String,String > referers;
    private final String agent="Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36";
    public MiuiService() throws Exception{
        if(muuiCookie==null){
            muuiCookie=new HashMap<>();
        }
        if (extraData==null){
            extraData = new HashMap<>();
        }
        if (referers==null){
            referers = new HashMap<>();
        }
        sslContext = SSLContext.getInstance("SSL");
        X509TrustManager[] xtmArray = new X509TrustManager[]{xtm};
        sslContext.init(null, xtmArray, new java.security.SecureRandom());
    }

    @Override
    public JfResult requestVerifyCode(JfRequest request) throws Exception {
//        JfResult result = new JfResult();
//        result.setMessage("获取验证码失败");
//        Map<String,String> cookie = new HashMap<>();
//        String id = Common.createUUID();
//        result.setId(id);
//
//        String loginPage = "http://www.miui.com/member.php?mod=logging&action=miuilogin";
//        String redirectUrl = openLoginPage(loginPage,cookie);
//        referers.put(id,redirectUrl);
//        Map<String,String> keys = pass_service_login(cookie,redirectUrl);
//        extraData.put(id,keys);
//        pass_service_loginauth(cookie,redirectUrl,keys,request.getAccount(),request.getPassword());
//        String picurl=setPicUrl(cookie,redirectUrl);
//        result.setData(picurl);
//        muuiCookie.put(id,cookie);
        return null;
    }



    @Override
    public JfResult queryIntegral(JfRequest request) throws Exception {
        JfResult result = new JfResult();
        Map<String,String> cookie = new HashMap<>();
        String loginPage = "http://www.miui.com/member.php?mod=logging&action=miuilogin";
        String redirectUrl = openLoginPage(loginPage,cookie);
        cookie.put("uLocale","zh_CN");
        Map<String,String> keys = pass_service_login(cookie,redirectUrl);
        MiOkData md = pass_service_loginauth(cookie,redirectUrl,keys,request.getAccount(),request.getPassword());
        if (md.code!=0){
            result.setMessage(md.desc);
            return result;
        }

        extra_php_page(md.location,cookie);

        String jf = getJf(cookie);
        result.setPoints(jf);
        return result;
    }

    private String getJf(Map<String, String> cookie) throws Exception{
        HttpURLConnection connection = (HttpURLConnection) new URL("http://www.miui.com/index.html").openConnection();
        connection.setRequestProperty("User-Agent",agent);
        connection.setRequestProperty("Cookie",Common.buildCookieString(cookie));

        String html = Common.inputStreamToString(connection.getInputStream());
        Document document = Jsoup.parse(html);
        Elements other = document.getElementsByClass("uinfo_t_box_other");
        if (other==null || other.size()==0){
            return null;
        }

        Node e = other.get(0).childNode(3);
        String line = e.toString();
        Pattern pattern=Pattern.compile("\\d{1,7}");
        Matcher matcher = pattern.matcher(line);
        return matcher.find()?matcher.group(0):null;
    }

    private void extra_php_page(String location, Map<String, String> cookie) throws Exception{
        HttpURLConnection connection = (HttpURLConnection) new URL(location).openConnection();
        connection.setRequestProperty("User-Agent",agent);
        connection.setRequestProperty("Cookie",Common.buildCookieString(cookie));
        connection.setRequestProperty("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        connection.setInstanceFollowRedirects(false);
        Common.updateCookie(connection,cookie);
    }

    private MiOkData pass_service_loginauth(Map<String, String> cookie, String url,Map<String,String> keys,String user,String pwd)
            throws Exception{
        String param ="_json=true"
                +"&callback="+ URLEncoder.encode(keys.get("callback"),"UTF-8")
                +"&sid="+keys.get("sid")
                +"&qs="+URLEncoder.encode(keys.get("qs"),"UTF-8")
                +"&_sign="+URLEncoder.encode(keys.get("_sign"),"UTF-8")
                +"&serviceParam={\"checkSafePhone\":false}"
                +"&user="+user
                +"&hash="+new MD5().bytesToMD5(pwd.getBytes()).toUpperCase();
        byte[] buffer = param.getBytes();
        int len = buffer.length;
        String url_ = "https://account.xiaomi.com/pass/serviceLoginAuth2?_dc="+System.currentTimeMillis();
        HttpsURLConnection connection = (HttpsURLConnection) new URL(url_).openConnection();
        //connection.setSSLSocketFactory(sslContext.getSocketFactory());
        //connection.setHostnameVerifier(mhv);
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Length",len+"");
        connection.setRequestProperty("User-Agent",agent);
        connection.setRequestProperty("Accept","*/*");
        connection.setRequestProperty("Referer",url);
        connection.setRequestProperty("Origin","https://account.xiaomi.com");
        connection.setRequestProperty("Accept-Encoding","gzip, deflate, sdch");
        connection.setRequestProperty("Accept-Language","zh-CN,zh;q=0.8");
        connection.setRequestProperty("Cookie",Common.buildCookieString(cookie));
        connection.setRequestProperty("Content-type","application/x-www-form-urlencoded");


        OutputStream os = connection.getOutputStream();
        os.write(buffer);
        os.close();

        Common.updateCookie(connection,cookie);
        String body = new String(GZipUtils.decompress(GZipUtils.input2byte(connection.getInputStream())));

        String jsontext=body.substring(body.indexOf('{'),body.lastIndexOf('}')+1);
        MiOkData md = JSONObject.parseObject(jsontext,MiOkData.class, Feature.IgnoreNotMatch);

        return md;
    }

    private Map<String,String> pass_service_login(Map<String,String> cooke,String url) throws Exception{
        Map<String,String > data = new HashMap<>();
        HttpsURLConnection connection= (HttpsURLConnection) new URL(url).openConnection();
        connection.setSSLSocketFactory(sslContext.getSocketFactory());
        connection.setRequestProperty("User-Agent",agent);
        connection.setRequestProperty("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        connection.setRequestProperty("Accept-Encoding","gzip, deflate, sdch");
        connection.setRequestProperty("Cookie",Common.buildCookieString(cooke));
        Common.updateCookie(connection,cooke);

        String page = new String(GZipUtils.decompress(GZipUtils.input2byte(connection.getInputStream())));
        Document document = Jsoup.parse(page);
        Elements scripts = document.getElementsByTag("script");
        if (scripts.size()==0){
            return data;
        }

        Element weNeed = scripts.get(1);
        String codes = weNeed.toString();

        data.put("appId",getPatternedText("appId:\"\\S*\"",codes));
        data.put("appkey",getPatternedText("appKey:\"\\S*\"",codes));
        data.put("deviceType",getPatternedText("deviceType:'\\S*'",codes));
        data.put("dataCenter",getPatternedText("dataCenter:'\\S*'",codes));
        data.put("locale",getPatternedText("locale:\"\\S*\"",codes));
        data.put("region",getPatternedText("region:\"\\S*\"",codes));
        data.put("callback",patternCallback("callback:\"\\S*\"",codes));
        data.put("sid",getPatternedText("sid:\"\\S*\"",codes));
        data.put("qs",getPatternedText("qs:\"\\S*\"",codes));
        data.put("_sign",getPatternedText("\"_sign\":\"\\S*\"",codes));
        data.put("serviceParam",getPatternedText("serviceParam :'\\S*'",codes));
        data.put("privacyLink",patternCallback("privacyLink:'\\S*'",codes));
        return data;
    }

    private String getPatternedText(String patterntext,String searchFrom) {
        Pattern pattern = Pattern.compile(patterntext);
        Matcher matcher = pattern.matcher(searchFrom);

        if (matcher.find()) {
            String text = matcher.group(0);
            if (text.contains(":")) {
                String[] d = text.split(":");
                String suffix = d[1];
                try {
                    suffix = suffix.substring(suffix.indexOf("\"") + 1, suffix.lastIndexOf("\""));
                } catch (Exception e) {
                    suffix = suffix.substring(suffix.indexOf("'") + 1, suffix.lastIndexOf("'"));
                }
                if (suffix != null && !"".equals(suffix)) {
                    return suffix;
                }
            }
        }
        return null;
    }

    private String patternCallback(String p,String t){
        Pattern pattern = Pattern.compile(p);
        Matcher matcher = pattern.matcher(t);
        if (matcher.find()) {
            String text = matcher.group(0);
            try {
                text = text.substring(text.indexOf("\"") + 1, text.lastIndexOf("\""));
            }catch (Exception e){
                text = text.substring(text.indexOf("'") + 1, text.lastIndexOf("'"));
            }
            if (text != null && !"".equals(text)) {
                return text;
            }
        }
        return null;
    }
    private String openLoginPage(String url, Map<String,String> cookie) throws Exception{
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestProperty("User-Agent",agent);
        connection.setRequestProperty("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        connection.setRequestProperty("Accept-Encoding","gzip, deflate, sdch");
        connection.setInstanceFollowRedirects(false);

        Common.updateCookie(connection,cookie);
        return connection.getHeaderField("Location");
    }
}
