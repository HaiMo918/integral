package com.integral.service.lvmama;

import com.alibaba.fastjson.JSONObject;
import com.integral.utils.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import javax.net.ssl.HttpsURLConnection;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by kris on 2017/1/13.
 */
@Service
public class Lvmama implements IQueryIntegral{
    final String agent="Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36";
    final String defaultAcceptLanguage="zh-CN,zh;q=0.8";
    final String defaultAcceptEncoding="gzip, deflate, sdch";
    private static boolean DEBUG=false;
    private static Map<String,Map<String,String>> lvmamaCookie=new HashMap<>();
    private final Object lock=new Object();

    @Override
    public JfResult requestVerifyCode(JfRequest request) throws Exception {
        JfResult result = new JfResult();
        String id = Common.createUUID();
        result.setId(id);
        Map<String ,String> thisCookie = new HashMap<>();
        openPointsPage(thisCookie);
        String imageURL = getImageURL(thisCookie);
        result.setData(imageURL);
        synchronized (lock) {
            lvmamaCookie.put(id, thisCookie);
        }
        return result;
    }

    private String getImageURL(Map<String, String> cookie) throws Exception{
        String url="http://login.lvmama.com/nsso/account/checkcode.htm";
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        addHeader(connection,"http://www.lvmama.com/points",null,null,null);
        Common.updateCookie(connection,cookie);
        long t = System.currentTimeMillis();
        File img;
        String localPath;
        String server = "http://114.55.133.156:8080/images/"+t+".jpeg";
        if (DEBUG){
            localPath="D:\\"+t+".jpeg";
        }else{
            localPath="/usr/local/etc/tomcat8/webapps/images/"+t+".jpeg";
        }
        img=new File(localPath);
        FileOutputStream fos = new FileOutputStream(img);
        InputStream is = connection.getInputStream();
        int count;
        byte[] buffer = new byte[10240];
        while ((count=is.read(buffer))!=-1){
            fos.write(buffer,0,count);
        }
        fos.close();
        is.close();
        return server;
    }

    private void openPointsPage(Map<String, String> cookie) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL("http://www.lvmama.com/points").openConnection();
        addHeader(connection,"","","","");
        Common.updateCookie(connection,cookie);
    }

    @Override
    public JfResult queryIntegral(JfRequest request) throws Exception {
        JfResult result = new JfResult();
        Map<String ,String> cookie;
        synchronized (lock){
            cookie=lvmamaCookie.get(request.getId());
        }
        long t1 = 1484237422128L;
        String callbackPrefix="jQuery1720659725120639459_";
        String needCaptchUrl = "http://login.lvmama.com/nsso/ajax/captcha/checkIsNeedCaptcha.do?";
        needCaptchUrl=needCaptchUrl
                +"jsoncallback="+callbackPrefix+t1
                +"&userName="+request.getAccount()
                +"&_="+System.currentTimeMillis();
        boolean need = checkIsNeedCaptch(needCaptchUrl,cookie);
        if (need){
            System.out.println("need captch");
        }else {
            System.out.println("don't need captch");
        }

        String validateNormalLoginUrl="http://login.lvmama.com/nsso/geetest/login/validateNormalLogin.do?";
        validateNormalLoginUrl=validateNormalLoginUrl
                +"jsoncallback="+callbackPrefix+(t1+1)
                +"&userName="+request.getAccount()
                +"&password="+request.getPassword()
                +"&verifyCode="+request.getCode()
                +"&_="+System.currentTimeMillis();
        String securityCode = validateNormalLogin(validateNormalLoginUrl,cookie);
        if (securityCode==null){
            result.setMessage("图形验证码输入错误，获取安全码失败");
            result.setId(request.getId());
            return result;
        }
        String rapidLoginURL="https://login.lvmama.com/nsso/ajax/login/rapidLogin.do?";
        rapidLoginURL = rapidLoginURL
                +"jsoncallback="+callbackPrefix+(t1+2)
                +"&mobileOrEMail="+request.getAccount()
                +"&password="+request.getPassword()
                +"&verifycode="+request.getCode()
                +"&loginType=L-N"
                +"&securityCode="+securityCode
                +"&normal_geetest_challenge="
                +"&normal_geetest_validate="
                +"&_="+System.currentTimeMillis();
        boolean success = rapidLogin(rapidLoginURL,cookie);
        if (!success){
            result.setMessage("login failed");
            return result;
        }

        String jf = getPoints(cookie);
        if (jf!=null){
            result.setMessage("ok");
            result.setPoints(jf);
        }else{
            result.setMessage("获取积分失败");
        }
        return result;
    }

    private String getPoints(Map<String, String> cookie) throws Exception{
        HttpURLConnection connection = (HttpURLConnection) new URL("http://www.lvmama.com/points").openConnection();
        connection.setRequestProperty("Cookie",Common.buildCookieString(cookie));
        addHeader(connection,"http://www.lvmama.com/points",null,null,null);
        String body=new String(GZipUtils.decompress(GZipUtils.input2byte(connection.getInputStream())));
        Document doc = Jsoup.parse(body);
        Elements elements = doc.getElementsByClass("gray");
        Element myjf = elements.first();
        List<Node> nodeList = myjf.childNodes();
        String node = nodeList.get(1).toString();
        Pattern pattern = Pattern.compile("\\d{1,9}");
        Matcher matcher = pattern.matcher(node);
        if (matcher.find()){
            return matcher.group(0);
        }
        return null;
    }

    private boolean rapidLogin(String url, Map<String, String> cookie) throws Exception{
        HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
        connection.setRequestProperty("Cookie",Common.buildCookieString(cookie));
        addHeader(connection,"http://www.lvmama.com/points",null,null,null);
        Common.updateCookie(connection,cookie);
        String responseBody = Common.inputStreamToString(connection.getInputStream());
        String jsonText = responseBody.substring(responseBody.indexOf('{'),responseBody.lastIndexOf(')'));
        JSONObject object = JSONObject.parseObject(jsonText);
        return object.getBooleanValue("success");
    }

    private String validateNormalLogin(String url, Map<String, String> cookie) throws Exception{
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestProperty("Cookie",Common.buildCookieString(cookie));
        addHeader(connection,"http://www.lvmama.com/points",null,null,null);
        String responseBody = new String(GZipUtils.decompress(GZipUtils.input2byte(connection.getInputStream())));
        String jsonText = responseBody.substring(responseBody.indexOf('{'),responseBody.lastIndexOf(')'));
        JSONObject object = JSONObject.parseObject(jsonText);
        if (object.getBooleanValue("success")){
            return object.getString("securityCode");
        }
        return null;
    }

    private boolean checkIsNeedCaptch(String url, Map<String, String> cookie) throws Exception{
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestProperty("Cookie",Common.buildCookieString(cookie));
        addHeader(connection,"http://www.lvmama.com/points",null,null,null);
        String responseBody = Common.inputStreamToString(connection.getInputStream());
        String jsonText = responseBody.substring(responseBody.indexOf('{'),responseBody.lastIndexOf(')'));
        JSONObject object = JSONObject.parseObject(jsonText);
        return object.getBooleanValue("result");
    }


    private void addHeader(HttpURLConnection connection,String refer,String method,String type,String len)
            throws Exception{
        connection.setRequestProperty("User-Agent", agent);
        connection.setRequestProperty("Accept-Encoding", defaultAcceptEncoding);
        connection.setRequestProperty("Accept-Language", defaultAcceptLanguage);
        connection.setRequestProperty("Referer", refer);
        if (method!=null&&method.equals("POST")){
            connection.setRequestMethod(method);
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type",type);
            connection.setRequestProperty("Content-Length",len);
        }
    }


//    public static void main(String[] agrv){
//        try {
//            Lvmama lvmama = new Lvmama();
//            JfRequest request=new JfRequest();
//            request.setAccount("18516318026");
//            request.setPassword("wgy12345");
//
//            JfResult result = new JfResult();
//            result = lvmama.requestVerifyCode(request);
//            System.out.println(result.getId());
//
//            JfRequest anotherRequest = new JfRequest();
//            anotherRequest.setAccount("18516318026");
//            anotherRequest.setPassword("wgy12345");
//            anotherRequest.setId(result.getId());
//            Scanner scanner = new Scanner(System.in);
//            System.out.println("请输入计算结果：");
//            String score=scanner.nextLine();
//            anotherRequest.setCode(score);
//            result = lvmama.queryIntegral(anotherRequest);
//            System.out.println(result.getPoints());
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }
}
