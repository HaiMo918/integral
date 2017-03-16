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
        return result;
    }


    //打开登录页，返回重定向的页面
    private String member_php(Map<String,String> cookie) throws Exception{
        final String url = "http://www.miui.com/member.php?mod=logging&action=miuilogin";
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestProperty(Constants.HttpHeaders.USER_AGENT,Constants.DEFAULT_UA);

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
        Pattern pattern = Pattern.compile("var JSP_VAR=.\\n.*\\n.*\\n.*\\n.*\\n.*\\n.*\\n.*\\n.*\\n.*\\n.*\\n.*\\n.*\\n.*};");
        Matcher matcher = pattern.matcher(body);
        if (matcher.find()){
            String text = matcher.group(0);
            text=text.substring(text.indexOf('{')+1,text.lastIndexOf('}')).trim();

        }

        return var;
    }


    /*
        var JSP_VAR={
  deviceType:'PC',
  dataCenter:'lg',
  locale:"zh_CN",
  region:"CN",
  callback:"http://www.miui.com/extra.php?mod=xiaomi/authcallback&followup=http%3A%2F%2Fwww.miui.com&sign=MjljNjQyMDlkYzVhM2Q2MWIyMTdlOWM1MDRlMGFjNTYzYTE5NzZlNw,,",
  sid:"miuibbs",
  qs:"%3Fcallback%3Dhttp%253A%252F%252Fwww.miui.com%252Fextra.php%253Fmod%253Dxiaomi%252Fauthcallback%2526followup%253Dhttp%25253A%25252F%25252Fwww.miui.com%2526sign%253DMjljNjQyMDlkYzVhM2Q2MWIyMTdlOWM1MDRlMGFjNTYzYTE5NzZlNw%252C%252C%26sid%3Dmiuibbs%26_locale%3Dzh_CN",
  hidden:"",
  "_sign":"AzkkvsPUGQuKHN3ShdTXaqq/L+U=",
  serviceParam :'{"checkSafePhone":false}',
  privacyLink:'http://www.miui.com/res/doc/privacy/cn.html',
  showActiveXControl:false
};

<ul class="uinfo_t_box_other">
                                <li>用户组：玩机小白</li>
                                <li>积&nbsp;&nbsp;&nbsp;分：21</li>
                                <li>帖&nbsp;&nbsp;&nbsp;子：0</li>
                                <li>好&nbsp;&nbsp;&nbsp;友：0</li>
                            </ul>
     */
    private Map<String,String> parseJspVar(String jspVarText) throws Exception{
        String[] elem = jspVarText.split("\n");
        String []eachData;
        Map<String,String> vars = new HashMap<>();
        for (String data : elem){
            eachData = data.split(":");

        }
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
