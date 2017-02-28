package com.integral.service.airline.juneyair;//Created by xiacheng on 16/9/11.

import com.integral.utils.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import javax.net.ssl.*;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 18621299168 lisa007
 */

@Service
public class JuneyAir implements IQueryIntegral{
    private MyX509TrustManager xtm = new MyX509TrustManager();
    private MyHostNameVerify mhv = new MyHostNameVerify();
    private SSLContext sslContext = null;


    @Override
    public JfResult requestVerifyCode(JfRequest request) throws Exception {
        return null;
    }

    @Override
    public JfResult queryIntegral(JfRequest request) throws Exception {
        JfResult jfResult = new JfResult();
        Map<String,String> cookie = new HashMap<>();
        String executeData = openLoginPageAndGetExecutionData(cookie);
        if ("".equals(executeData)){
            jfResult.setMessage("[吉祥航空]获取execute数据出错");
            return jfResult;
        }

        String tokenid = buildTokenID();
        String location = doLoginJuneyAir(request.getAccount(),request.getPassword(),executeData,cookie,tokenid);
        location = openIndexPage(location,cookie);
        String integral = openRealIndexPageAndFindIntegral(location,cookie);
        if ("".equals(integral)){
            jfResult.setMessage("[吉祥航空]获取积分失败");
            jfResult.setPoints(null);
        }else {
            jfResult.setMessage("[吉祥航空]获取积分成功");
            jfResult.setPoints(integral);
        }
        logout(cookie);
        return jfResult;
    }

    public JuneyAir() {
        try {
            sslContext = SSLContext.getInstance("TLS");
            X509TrustManager[] xtmArray = new X509TrustManager[]{xtm};
            sslContext.init(null, xtmArray, new java.security.SecureRandom());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String openLoginPageAndGetExecutionData(Map<String,String> cookie) throws Exception{
        String executionData = "";
        final String loginURL = "https://sso.juneyaoair.com/cas/login?service=http://www.juneyaoair.com/index.aspx";
        HttpsURLConnection connection = (HttpsURLConnection) new URL(loginURL).openConnection();
        connection.setSSLSocketFactory(sslContext.getSocketFactory());
        connection.setHostnameVerifier(mhv);
        connection.setRequestProperty(Constants.HttpHeaders.USER_AGENT,Constants.DEFAULT_UA);
        Common.updateCookie(connection,cookie);

        String html = Common.inputStreamToString(connection.getInputStream());
        Document pageDoc = Jsoup.parse(html);
        Elements elements = pageDoc.getElementsByAttributeValue("name","execution");
        if (elements.size()==0){
            return "";
        }
        Element e = elements.get(0);
        executionData = e.val();
        return executionData;
    }

    private String doLoginJuneyAir(String user,String pass,String executeData,Map<String,String> cookie,String tokenid)
            throws Exception{
        String redirectURL = null;
        final String loginURL = "https://sso.juneyaoair.com/cas/login?service=http://www.juneyaoair.com/index.aspx";
        final String requestParam = "username="+user+"&password="+pass+"&lt=&execution="+executeData+"&_eventId=submit&tokenId="+tokenid;
        byte[] bufferData = requestParam.getBytes();
        String length = String.valueOf(bufferData.length);

        HttpsURLConnection connection = (HttpsURLConnection) new URL(loginURL).openConnection();
        connection.setHostnameVerifier(mhv);
        connection.setSSLSocketFactory(sslContext.getSocketFactory());
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setInstanceFollowRedirects(false);
        connection.setRequestProperty(Constants.HttpHeaders.USER_AGENT,Constants.DEFAULT_UA);
        connection.setRequestProperty(Constants.HttpHeaders.COOKIE,Common.buildCookieString(cookie));
        connection.setRequestProperty(Constants.HttpHeaders.CONTENT_TYPE,"application/x-www-form-urlencoded");
        connection.setRequestProperty(Constants.HttpHeaders.CONTENT_LENGTH,length);

        OutputStream os = connection.getOutputStream();
        os.write(bufferData);
        os.flush();
        os.close();

        Common.updateCookie(connection,cookie);
        redirectURL = connection.getHeaderField("Location");
        return  redirectURL;
    }

    private String buildTokenID() {
        StringBuilder sb = new StringBuilder();
        sb.append("jxhk-").append(System.currentTimeMillis());
        double randData = Math.random();
        String hexString=Double.toHexString(randData).substring(2);
        sb.append("-").append(hexString);
        return sb.toString();
    }

    /*
        打开由doLoginJuneyAir返回的重定向URL
        http://www.juneyaoair.com/index.aspx?ticket=ST-34086-pDFgiLKaljcIo6LFyTTf-cas01.example.org
     */
    private String openIndexPage(String location,Map<String,String> cookie) throws Exception{
        HttpURLConnection connection = (HttpURLConnection) new URL(location).openConnection();
        connection.setRequestProperty(Constants.HttpHeaders.USER_AGENT,Constants.DEFAULT_UA);
        connection.setRequestProperty(Constants.HttpHeaders.ACCEPT,"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        connection.setRequestProperty(Constants.HttpHeaders.ACCEPT_ENCODING,"gzip, deflate, sdch");
        connection.setRequestProperty(Constants.HttpHeaders.ACCEPT_LANGUAGE,"zh-CN,zh;q=0.8");
        connection.setInstanceFollowRedirects(false);
        Common.updateCookie(connection,cookie);
        return connection.getHeaderField("Location");
    }

    private String openRealIndexPageAndFindIntegral(String indexpage,Map<String,String> cookie) throws Exception{
        String integral = "";
        HttpURLConnection connection = (HttpURLConnection) new URL(indexpage).openConnection();
        connection.setRequestProperty(Constants.HttpHeaders.USER_AGENT,Constants.DEFAULT_UA);
        connection.setRequestProperty(Constants.HttpHeaders.COOKIE,Common.buildCookieString(cookie));
        Common.updateCookie(connection,cookie);

        String html = Common.inputStreamToString(connection.getInputStream());
        Document document = Jsoup.parse(html);
        Elements els = document.getElementsByClass("nav_reserveLeftDescribe");
        if (els.size()==0){
            return integral;
        }
        Element e = els.get(5);
        String value = e.toString();
        Pattern pattern = Pattern.compile("\\d{1,10}");
        Matcher matcher = pattern.matcher(value);
        if (matcher.find()){
            integral = matcher.group(0);
        }
        return integral;
    }

    private void logout(Map<String,String> cookie) throws Exception{
        final String url = "http://www.juneyaoair.com/pages/logout.aspx";
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestProperty(Constants.HttpHeaders.USER_AGENT,Constants.DEFAULT_UA);
        connection.setRequestProperty(Constants.HttpHeaders.COOKIE,Common.buildCookieString(cookie));
    }

    public static void main(String[] args){
        try{
            JuneyAir air = new JuneyAir();
            JfRequest request = new JfRequest();
            request.setAccount("18621299168");
            request.setPassword("lisa007");
            air.queryIntegral(request);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
