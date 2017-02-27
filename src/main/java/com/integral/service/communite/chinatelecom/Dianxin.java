package com.integral.service.communite.chinatelecom;

import com.alibaba.fastjson.JSONObject;
import com.integral.utils.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by kris on 2017/1/10.
 *
 */
@Service
public class Dianxin implements IQueryIntegral {
    private PhoneInfo phoneInfo;
    private PointInfo pointInfo;
    private final String agent = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36";
    private final String accept = "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8";
    private final String encoding = "gzip, deflate, sdch";
    private final String language = "zh-CN,zh;q=0.8";
    private static ScriptEngineManager manager;
    private static ScriptEngine engine;
    private static boolean debug = false;
    private static String jscode;
    private final String type="application/x-www-form-urlencoded; charset=UTF-8";

    static {
        manager = new ScriptEngineManager();
        engine = manager.getEngineByName("javascript");
        try {
            File file;
            if (debug) {
                file = new File("D:\\projects\\Integral\\js\\jquerydx.js");
            } else {
                file = new File("/usr/local/etc/tomcat8/webapps/jquery/jquerydx.js");
            }
            int length = (int) file.length();
            byte[] buffer = new byte[length];
            FileInputStream fis = new FileInputStream(file);
            int count = fis.read(buffer);
            if (count != -1) {
                jscode = new String(buffer);
            } else {
                jscode = null;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public JfResult requestVerifyCode(JfRequest request) throws Exception {
        return null;
    }

    @Override
    public JfResult queryIntegral(JfRequest request) throws Exception {
        JfResult result = new JfResult();
        Map<String, String> cookie = new HashMap<>();
        openLoginPage(cookie);
        phoneInfo = checkPhone(cookie, request.getAccount());
        String dqmh_UamT0_do = login(cookie, request.getAccount(), request.getPassword());
        assert dqmh_UamT0_do==null;
        String login_ecs_do = open_dqmh_UamT0_do(cookie,dqmh_UamT0_do);
        String rootPage = open_login_ecs_do(cookie,login_ecs_do);
        assert rootPage==null;
        String citycode=rootPage.substring(rootPage.lastIndexOf('/')+1);
        cookie.put("cityCode",citycode);
        String realHomePage = openSHRootPage(cookie,rootPage);
        assert realHomePage==null;

        boolean entered=isUserLoginSuccess(cookie,realHomePage);
        if (!entered){
            result.setMessage("user login failed");
            return result;
        }

        /*
            这是几层跳转
         */
        String dqmh_ssoLink_do_url="http://www.189.cn/dqmh/ssoLink.do?method=skip&platNo=93501&toStUrl=http://y.jf.189.cn/";
        String login_skip_ecs_do_url=open_dqmh_sslLink_do(dqmh_ssoLink_do_url,cookie,realHomePage);
        assert login_skip_ecs_do_url==null;

        String login_sso_ecs_do_url = open_login_skip_ecs_do(login_skip_ecs_do_url,cookie,realHomePage);
        assert login_sso_ecs_do_url==null;

        String redirect_ECSSSOTransit_url=open_login_sso_ecs_do(login_sso_ecs_do_url,cookie,realHomePage);
        assert redirect_ECSSSOTransit_url==null;

        String ssov2_sso_ecs2club_aspx_url=open_redirect_ecsssotransit(redirect_ECSSSOTransit_url,cookie,realHomePage);
        assert ssov2_sso_ecs2club_aspx_url==null;

        String ticketurl1=open_ssov2_sso_ecs2club_aspx(ssov2_sso_ecs2club_aspx_url,cookie,realHomePage);
        assert ticketurl1==null;
        String ticketValue=ticketurl1.substring(ticketurl1.indexOf('?')+1);
        String home_index_aspx = "http://y.jf.189.cn/home/index.aspx?"+ticketValue;


        String jsoncallback="http://y.jf.189.cn/home/ajax.ashx?jsoncallback=jQuery18306437714549101394_1484201336729";
        long t1=System.currentTimeMillis();
        jsoncallback=jsoncallback+"&t="+t1+"&g=nocachelogin"+"&"+ticketValue+"&uri="+home_index_aspx+"&_="+(t1+5);
        String ssov2_sso_login_aspx=open_jsoncallback(jsoncallback,cookie,home_index_aspx);
        if (ssov2_sso_login_aspx!=null && !ssov2_sso_login_aspx.contains("http")){
            result.setPoints(ssov2_sso_login_aspx);
            result.setMessage("ok");
        }
        return result;
    }

    private boolean isUserLoginSuccess(Map<String, String> cookie, String refer) throws Exception{
        HttpURLConnection connection = (HttpURLConnection) new URL("http://www.189.cn/login/index.do").openConnection();
        addHeader(connection,refer,null,null,null);
        connection.setRequestProperty("Cookie", Common.buildCookieString(cookie));
        String pageContent = Common.inputStreamToString(connection.getInputStream());
        JSONObject object=JSONObject.parseObject(pageContent);
        return "0".equals(object.getString("code"));
    }


    private String open_jsoncallback(String url, Map<String, String> cookie, String refer) throws Exception{
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        addHeader(connection,refer,null,null,null);
        connection.setRequestProperty("Cookie", Common.buildCookieString(cookie));
        Common.updateCookie(connection,cookie);
        String pageDoc = Common.inputStreamToString(connection.getInputStream());
        String jsonText=pageDoc.substring(pageDoc.indexOf('{'),pageDoc.lastIndexOf(')'));
        JSONObject object = JSONObject.parseObject(jsonText);
        if (object.containsKey("Redirect")) {
            return object.getString("Redirect");
        }
        if (object.containsKey("CustID")){
            return object.getString("Integral");
        }
        return null;
    }

    private String open_ssov2_sso_ecs2club_aspx(String url, Map<String, String> cookie, String refer) throws Exception{
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        addHeader(connection,refer,null,null,null);
        connection.setInstanceFollowRedirects(false);
        connection.setRequestProperty("Cookie", Common.buildCookieString(cookie));
        String pageDoc = Common.inputStreamToString(connection.getInputStream());
        Pattern pattern=Pattern.compile("http://y.jf.189.cn/\\?ticket=telefen-ST-.{32}");
        Matcher matcher = pattern.matcher(pageDoc);
        if (matcher.find()){
            return matcher.group(0);
        }
        return null;

    }

    private String open_redirect_ecsssotransit(String url, Map<String, String> cookie, String refer) throws Exception{
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        addHeader(connection,refer,null,null,null);
        connection.setInstanceFollowRedirects(false);
        connection.setRequestProperty("Cookie", Common.buildCookieString(cookie));
        return getRedirectURL(connection);
    }

    private String open_login_sso_ecs_do(String url, Map<String, String> cookie, String refer) throws Exception{
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        addHeader(connection,refer,null,null,null);
        connection.setInstanceFollowRedirects(false);
        connection.setRequestProperty("Cookie", Common.buildCookieString(cookie));
        return getRedirectURL(connection);
    }

    private String open_login_skip_ecs_do(String url, Map<String, String> cookie, String refer) throws Exception{
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        addHeader(connection,refer,null,null,null);
        connection.setInstanceFollowRedirects(false);
        connection.setRequestProperty("Cookie", Common.buildCookieString(cookie));
        return getRedirectURL(connection);
    }

    private String open_dqmh_sslLink_do(String url,Map<String, String> cookie, String refer) throws Exception{
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        addHeader(connection,refer,null,null,null);
        connection.setInstanceFollowRedirects(false);
        connection.setRequestProperty("Cookie", Common.buildCookieString(cookie));
        return getRedirectURL(connection);
    }




    private void openLoginPage(Map<String, String> cookie) throws Exception {
        URL url = new URL("http://login.189.cn/login");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        addHeader(urlConnection,"","","","");
        Common.updateCookie(urlConnection, cookie);
    }

    //POST
    private PhoneInfo checkPhone(Map<String, String> cooke, String phone) throws Exception {
        String param = "m=checkphone&phone=" + phone;
        byte[] data = param.getBytes();
        int len = data.length;
        URL url = new URL("http://login.189.cn/login/ajax");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        addHeader(connection,"http://login.189.cn/login","POST",type,len+"");
        connection.setRequestProperty("Cookie", Common.buildCookieString(cooke));
        postWrite(connection.getOutputStream(),data);
        String response = Common.inputStreamToString(connection.getInputStream());
        PhoneInfo phoneInfo = JSONObject.parseObject(response, PhoneInfo.class);
        return phoneInfo;
    }

    private String login(Map<String, String> cookie, String account, String password) throws Exception {
        String encryptedPwd = encryptPassword("myEncrypt",password);
        if (encryptedPwd == null) {
            throw new NullPointerException("加密密码时出现错误");
        }
        String param = buildLoginParameter(account,encryptedPwd,phoneInfo.ProvinceID);
        byte[] buffer = param.getBytes();
        int len = buffer.length;
        URL url = new URL("http://login.189.cn/login");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        addHeader(urlConnection,"http://login.189.cn/login","POST",type,len+"");
        urlConnection.setInstanceFollowRedirects(false);
        urlConnection.setRequestProperty("Cookie", Common.buildCookieString(cookie));
        postWrite(urlConnection.getOutputStream(),buffer);
        Common.updateCookie(urlConnection,cookie);
        return getRedirectURL(urlConnection);
    }

    private String encryptPassword(String function,String param) throws Exception {
        engine.eval(jscode);
        Invocable invocable = (Invocable) engine;
        Object result = invocable.invokeFunction(function, param);
        if (result != null) {
            return result.toString();
        }
        return null;
    }

    private String open_dqmh_UamT0_do(Map<String, String> cookie, String redirectURL) throws Exception{
        URL url = new URL(redirectURL);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setInstanceFollowRedirects(false);
        addHeader(urlConnection,"http://login.189.cn/login",null,null,null);
        urlConnection.setRequestProperty("Cookie", Common.buildCookieString(cookie));
        return getRedirectURL(urlConnection);
    }

    private String open_login_ecs_do(Map<String, String> cookie, String redirectURL) throws Exception{
        URL url = new URL(redirectURL);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setInstanceFollowRedirects(false);
        addHeader(urlConnection,"http://login.189.cn/login",null,null,null);
        urlConnection.setRequestProperty("Cookie", Common.buildCookieString(cookie));
        Common.updateCookie(urlConnection,cookie);
        if (cookie.get("isLogin").equals("logined")){
           cookie.put("loginStatus","logined");
       }
        return getRedirectURL(urlConnection);
    }

    private String openSHRootPage(Map<String, String> cookie, String redirectURL) throws Exception{
        URL url = new URL(redirectURL);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setInstanceFollowRedirects(false);
        addHeader(urlConnection,"http://login.189.cn/login",null,null,null);
        urlConnection.setRequestProperty("Cookie", Common.buildCookieString(cookie));
        return getRedirectURL(urlConnection);
    }


    private String getRedirectURL(HttpURLConnection connection) throws Exception{
        Map<String,List<String>> headerFields = connection.getHeaderFields();
        if (headerFields.containsKey("Location")){
            List<String> values = headerFields.get("Location");
            if (values.size()>0){
                return values.get(0);
            }
        }
        return null;
    }

    private void addHeader(HttpURLConnection connection,String refer,String method,String type,String len)
            throws Exception{
        connection.setRequestProperty("User-Agent", agent);
        connection.setRequestProperty("Accept-Encoding", encoding);
        connection.setRequestProperty("Accept-Language", language);
        connection.setRequestProperty("Referer", refer);
        if (method!=null&&method.equals("POST")){
            connection.setRequestMethod(method);
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type",type);
            connection.setRequestProperty("Content-Length",len);
        }
    }

    private void postWrite(OutputStream os, byte[] data) throws Exception{
        os.write(data);
        os.flush();
        os.close();
    }

    private String buildLoginParameter(String account,String password,String cityNumber) throws Exception{
        StringBuilder sb = new StringBuilder();
        sb.append("Account=").append(account)
                .append("&UType=201")
                .append("&ProvinceID=").append(cityNumber)
                .append("&AreaCode=")
                .append("&CityNo=")
                .append("&RandomFlag=0")
                .append("&Password=").append(URLEncoder.encode(password,"UTF-8"))
                .append("&Captcha=");
        return sb.toString();
    }
}
