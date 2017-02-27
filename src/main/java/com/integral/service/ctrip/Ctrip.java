package com.integral.service.ctrip;

import com.integral.utils.*;
import org.apache.hc.client5.http.utils.URLEncodedUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

/**
 * Created by liuqinghai on 2016/12/7.
 */
@Service
public class Ctrip implements IQueryIntegral {
    private final String __EVENTTARGET = "__EVENTTARGET";
    private final String __EVENTARGUMENT="__EVENTARGUMENT";
    private final String __VIEWSTATE = "__VIEWSTATE";
    private final String __VIEWSTATEGENERATOR="__VIEWSTATEGENERATOR";
    private final String signin_logintype="signin_logintype";
    private final String done="done";
    private final String loginType="loginType";
    private final String hdnToken="hdnToken";
    private final String hidGohome="hidGohome";
    private final String hidVerifyCodeLevel="hidVerifyCodeLevel";
    private final String VerifyCodeFlagDy="VerifyCodeFlagDy";
    private final String hidMask="hidMask";
    private final String hidServerName="hidServerName";
    private final String hidImgCodeDatahash="hidImgCodeDatahash";
    private final String needCheckServerSession="needCheckServerSession";
    private final String hidToken="hidToken";
    private final String theOne="1";
    private final String txtUserName="txtUserName";
    private final String txtPwd="txtPwd";
    private final String txtCode="txtCode";
    private final String chkAutoLogin="chkAutoLogin";
    private final String btnSubmit="btnSubmit";
    private final String mobilePhone="mobilePhone";
    private final String txtCodePwd="txtCodePwd";
    private final String dyPwd="dyPwd";
    private final String chkAutoLoginDy="chkAutoLoginDy";
    private final String cardname="cardname";
    private final String hid_cardname="hid_cardname";
    private final String txtCUserName="txtCUserName";
    private final String txtCPwd="txtCPwd";
    private final String CSVerifyCode="CSVerifyCode";
    private final String txtVerifyCode="txtVerifyCode";
    private final String txtMPwd="txtMPwd";
    private final String txtReMPwd="txtReMPwd";
    private final String txtHPwd="txtHPwd";
    private final String txtReHPwd = "txtReHPwd";

    @Override
    public JfResult requestVerifyCode(JfRequest request) throws Exception {
        return null;
    }

    @Override
    public JfResult queryIntegral(JfRequest request) throws Exception {
        JfResult result = new JfResult();
        Map<String,String> cookie = new HashMap<String,String>();
        long time = System.currentTimeMillis();
        cookie.put("_bfa","1."+time+".3mpskm.1."+time+"."+time+".1.1");
        cookie.put("_bfs","1.1");
        cookie.put("_bfi","p1%3D100003%26p2%3D0%26v1%3D1%26v2%3D0");

        Map<String ,String> postData = openLoginPage(cookie);
        postData.put(txtUserName,request.getAccount());
        postData.put(txtPwd,request.getPassword());

        ajaxChxBwgAndVerifyCode(request.getAccount());
        ajaxCheckVerifyCodeAndIP(request.getAccount());
        String param = buildLoginParam(postData);
        System.out.println(param);
        if (doLoginCtrip(cookie,param)){
            System.out.println("登录成功");
        }else{
            System.out.println("登录失败");
            result.setMessage("登录失败");
        }

        String score = queryjf(cookie);
        result.setPoints(score);
        return result;
    }

    private String queryjf(Map<String, String> cookie) throws Exception{
        URL url=new URL("http://jifen.ctrip.com/rewards/SearchPrize.aspx?sso_lct=F ");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("User-Agent", Constants.DEFAULT_UA);
        connection.setRequestProperty("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        connection.setRequestProperty("Cookie",buildCookieString(cookie));
        connection.setRequestProperty("Accept-Language","zh-CN");
        connection.setRequestProperty("Accept-Encoding","gzip, deflate");
        connection.setRequestProperty("Host","jifen.ctrip.com");

        InputStream is = connection.getInputStream();
        byte[] data = GZipUtils.input2byte(is);
        byte[] pageData = GZipUtils.decompress(data);
        String sb = new String(pageData);
        Document document = Jsoup.parse(sb);
        Elements elements = document.getElementsByClass("sm-user-score");
        List<Node> nodes=elements.get(0).childNodes();
        Node e = nodes.get(1);
        String str = e.toString();
        Pattern pattern = Pattern.compile("\\d{1,10}");
        Matcher matcher = pattern.matcher(str);
        if (matcher.find()){
            return matcher.group(0);
        }
        return "-1";
    }


    private void ajaxCheckVerifyCodeAndIP(String account) throws Exception{
        //https://accounts.ctrip.com/member/ajax/AjaxCheckVerifyCodeAndIP.aspx?username=4014878@qq.com&tmp=182
        URL url = new URL("https://accounts.ctrip.com/member/ajax/AjaxCheckVerifyCodeAndIP.aspx?username="+account+"&tmp=182");
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestProperty("User-Agent", Constants.DEFAULT_UA);
        connection.setRequestProperty("Accept","*/*");
        connection.setRequestProperty("Accept-Language","zh-CN");
        connection.setRequestProperty("Accept-Encoding","gzip, deflate");
        connection.setRequestProperty("Host","crm.ws.ctrip.com");
        connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
    }

    private void ajaxChxBwgAndVerifyCode(String account) throws Exception{
        //https://accounts.ctrip.com/member/ajax/AjaxChkBWGAndVerifyCode.ashx?username=4014878@qq.com&tmp=419
        URL url = new URL("https://accounts.ctrip.com/member/ajax/AjaxChkBWGAndVerifyCode.ashx?username="+account+"&tmp=419");
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestProperty("User-Agent", Constants.DEFAULT_UA);
        connection.setRequestProperty("Accept","*/*");
        connection.setRequestProperty("Accept-Language","zh-CN");
        connection.setRequestProperty("Accept-Encoding","gzip, deflate");
        connection.setRequestProperty("Host","crm.ws.ctrip.com");
        connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
    }

    private void openDeviceGuidPage(Map<String, String> cookie) throws Exception{
        URL url = new URL("https://cdid.c-ctrip.com/Payment-CardRisk-DeviceWebSite/DeviceGuid.aspx?EncryptID=OnlineLogin");
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestProperty("User-Agent", Constants.DEFAULT_UA);
        connection.setRequestProperty("Accept","*/*");
        connection.setRequestProperty("Accept-Language","zh-CN");
        connection.setRequestProperty("Accept-Encoding","gzip, deflate");
        connection.setRequestProperty("Host","crm.ws.ctrip.com");
        updateCookie(connection,cookie);
    }

    private void openAdCallProxyV2(Map<String,String> cookie) throws Exception{
        URL url = new URL("https://crm.ws.ctrip.com/Customer-Market-Proxy/AdCallProxyV2.aspx?biztype=1100&adlist=[{\"pagecode\":\"2\",\"domid\":\"adpic\",\"type\":0}]");
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestProperty("User-Agent", Constants.DEFAULT_UA);
        connection.setRequestProperty("Accept","*/*");
        connection.setRequestProperty("Cookie",buildCookieString(cookie));
        connection.setRequestProperty("Accept-Language","zh-CN");
        connection.setRequestProperty("Accept-Encoding","gzip, deflate");
        connection.setRequestProperty("Host","crm.ws.ctrip.com");
        connection.setRequestProperty("Referer","https://accounts.ctrip.com/member/login.aspx?BackUrl=http%3A%2F%2Fjifen.ctrip.com%2Frewards%2FSearchPrize.aspx&responsemethod=get");
        updateCookie(connection,cookie);
    }

    private boolean doLoginCtrip(Map<String,String> cookie,String data) throws Exception{
        byte[] buffer = data.getBytes();
        int length = buffer.length;

        URL url = new URL("https://accounts.ctrip.com/member/login.aspx?BackUrl=http://jifen.ctrip.com/rewards/SearchPrize.aspx&responsemethod=get");
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Referer","https://accounts.ctrip.com/member/login.aspx?BackUrl=http://jifen.ctrip.com/rewards/SearchPrize.aspx&responsemethod=get");
        connection.setRequestProperty("Accept","image/jpeg, application/x-ms-application, image/gif, application/xaml+xml, image/pjpeg, application/x-ms-xbap, */*");
        connection.setRequestProperty("Accept-Language","zh-CN");
        connection.setRequestProperty("Accept-Encoding","gzip, deflate");
        connection.setRequestProperty("Host","accounts.ctrip.com");
        connection.setRequestProperty("User-Agent", Constants.DEFAULT_UA);
        connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
        connection.setRequestProperty("Content-Length",String.valueOf(length));
        connection.setRequestProperty("Cookie",buildCookieString(cookie));

        OutputStream os = connection.getOutputStream();
        os.write(buffer);
        os.flush();
        os.close();
        if (200==connection.getResponseCode()){
            updateCookie(connection,cookie);
            return true;
        }else {
            System.out.println(connection.getResponseMessage());
        }
        return false;
    }

    private Map<String,String> openLoginPage(Map<String,String> cookie) throws Exception{
        URL url = new URL("https://accounts.ctrip.com/member/login.aspx?BackUrl=http%3A%2F%2Fjifen.ctrip.com%2Frewards%2FSearchPrize.aspx&responsemethod=get");
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestProperty("User-Agent", Constants.DEFAULT_UA);
        connection.setRequestProperty("Cookie",cookie.get("_bfi"));
        updateCookie(connection,cookie);
        InputStream is = connection.getInputStream();
        StringBuilder sb = new StringBuilder();
        String line = null;
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        while ((line=br.readLine())!=null){
            sb.append(line);
        }
        br.close();
        is.close();

        Document document;
        Element element;
        Attributes attributes;

        Map<String,String> postData = new HashMap<String,String>();
        document  = Jsoup.parse(sb.toString());

        element=document.getElementById("__EVENTTARGET");
        attributes=element.attributes();
        postData.put(__EVENTTARGET,attributes.get("value"));

        element=document.getElementById("__EVENTARGUMENT");
        attributes=element.attributes();
        postData.put(__EVENTARGUMENT,attributes.get("value"));

        element=document.getElementById("__VIEWSTATE");
        attributes=element.attributes();
        postData.put(__VIEWSTATE,URLEncoder.encode(attributes.get("value"),"UTF-8"));

        element=document.getElementById("__VIEWSTATEGENERATOR");
        attributes=element.attributes();
        postData.put(__VIEWSTATEGENERATOR,attributes.get("value"));

        element=document.getElementById("loginType");
        attributes=element.attributes();
        postData.put(loginType,attributes.get("value"));

        element=document.getElementById("hdnToken");
        attributes=element.attributes();
        postData.put(hdnToken,attributes.get("value"));

        element=document.getElementById("hidGohome");
        attributes=element.attributes();
        postData.put(hidGohome,postData.get("hdnToken"));

        element=document.getElementById("hidVerifyCodeLevel");
        attributes=element.attributes();
        postData.put(hidVerifyCodeLevel,attributes.get("value"));

        element=document.getElementById("VerifyCodeFlagDy");
        attributes=element.attributes();
        postData.put(VerifyCodeFlagDy,attributes.get("value"));

        element=document.getElementById("hidMask");
        attributes=element.attributes();
        postData.put(hidMask,attributes.get("value"));

        element=document.getElementById("hidToken");
        attributes=element.attributes();
        postData.put(hidToken,attributes.get("value"));

        element=document.getElementById("hidServerName");
        attributes=element.attributes();
        postData.put(hidServerName, URLEncoder.encode(attributes.get("value"),"utf-8"));

        element=document.getElementById("hidImgCodeDatahash");
        attributes=element.attributes();
        postData.put(hidImgCodeDatahash,attributes.get("value"));

        element=document.getElementById("needCheckServerSession");
        attributes=element.attributes();
        postData.put(needCheckServerSession,attributes.get("value"));

        postData.put(signin_logintype,"");
        postData.put(done,"");
        postData.put(chkAutoLoginDy,"on");
        postData.put(cardname,"");
        postData.put(hid_cardname,"0");
        postData.put(txtCUserName,"");
        postData.put(txtCPwd,"");
        postData.put(CSVerifyCode,"");
        postData.put(txtVerifyCode,"");
        postData.put(txtMPwd,"");
        postData.put(txtReMPwd,"");
        postData.put(txtHPwd,"");
        postData.put(txtReHPwd,"");
        postData.put(btnSubmit,URLEncoder.encode("登录","utf-8"));
        postData.put(mobilePhone,"");
        postData.put(txtCodePwd,"");
        postData.put(dyPwd,"");
        postData.put(chkAutoLogin,"on");
        postData.put(theOne,"on");
        postData.put(txtCode,"");
        return postData;
    }
    private synchronized void updateCookie(HttpURLConnection connection, Map<String,String>mCookie) throws Exception{
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

    private String buildLoginParam(Map<String,String> data) throws Exception{
        if (data==null||data.isEmpty()){
            throw new NullPointerException("invalid parameters");
        }

        StringBuilder sb = new StringBuilder();
        for (String key : data.keySet()){
            sb.append(key).append("=").append(data.get(key)).append("&");
        }
        String text = sb.toString();
        return text.substring(0,text.length()-1);
    }

    public static void main(String[] args){
        try{
            Ctrip ctrip = new Ctrip();
            JfRequest request = new JfRequest();
            request.setAccount("4014878@qq.com");
            request.setPassword("lqh_1985t");
            JfResult result = ctrip.queryIntegral(request);
            System.out.println(result.getPoints());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
