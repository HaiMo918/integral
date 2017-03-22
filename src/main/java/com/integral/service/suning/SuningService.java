package com.integral.service.suning;

import com.alibaba.fastjson.JSONObject;
import com.integral.utils.*;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import org.springframework.stereotype.Service;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by kris on 2017/1/15.
 */
@Service
public class SuningService implements IQueryIntegral {
    private static ScriptEngineManager sem;
    private static ScriptEngine se;
    private static boolean IS_DEBUG=true;
    private static String jscriptCode;
    private static Map<String,Map<String,String>> mCookie;
    private static String mLoginPBK;
    private static MyX509TrustManager xtm = new MyX509TrustManager();
    private static MyHostNameVerify mhv = new MyHostNameVerify();
    private static SSLContext sslContext = null;
    private static Map<String,Map<String,String>> mSuningCookie;
    static {
        try {
            File file;
            if (IS_DEBUG) {
                file = new File("D:\\projects\\integral\\js\\suning.js");
            } else {
                file = new File("/usr/local/etc/tomcat8/webapps/jquery/suning.js");
            }
            FileReader fr = new FileReader(file);
            char[] buffer = new char[(int) file.length()];
            fr.read(buffer);
            fr.close();
            jscriptCode = new String(buffer);
            sslContext = SSLContext.getInstance("SSL");
            X509TrustManager[] xtmArray = new X509TrustManager[]{xtm};
            sslContext.init(null, xtmArray, new java.security.SecureRandom());
            mSuningCookie = new HashMap<>();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public SuningService(){
        sem = new ScriptEngineManager();
        se = sem.getEngineByName("javascript");
        mCookie = new HashMap<>();
    }

    @Override
    public JfResult requestVerifyCode(JfRequest request) throws Exception {
        JfResult result = new JfResult();
        Map<String,String> cookie = new HashMap<>();
        mLoginPBK = openLoginPage(cookie);
        boolean needVerifyCode = checkNeedVerifyCode(cookie,request.getAccount());
        if (needVerifyCode){
            String vCodeUrl = readVcode(cookie);
            result.setData(vCodeUrl);
        }else{
            result.setData("");
        }
        mSuningCookie.put(request.getAccount(),cookie);
        return result;
    }


    @Override
    public JfResult queryIntegral(JfRequest request) throws Exception {
        JfResult result = new JfResult();
        result.setMessage("[苏宁]获取积分失败");
        Map<String,String> cookie = mSuningCookie.get(request.getAccount());

        SuningLoginParam loginParam = new SuningLoginParam();
        loginParam.username=request.getAccount();
        loginParam.password2 = encryptPwd(request.getPassword(),mLoginPBK);
        boolean loginSuccess = loginSuning(cookie,loginParam.toString());
        if (!loginSuccess){
            return result;
        }

        String point = getSuningPoint(cookie);
        if (point==null || "".equals(point)){
            return result;
        }
        result.setMessage("[苏宁]获取积分成功");
        result.setPoints(point);

        logOutSuning(cookie);
        return result;
    }

    private void logOutSuning(Map<String, String> cookie) throws Exception{
        final String url = "https://passport.suning.com/ids/logout?service=https%3A%2F%2Fpassport.suning.com%2Fids%2Flogin%3Fmethod%3DGET%26loginTheme%3Db2c";
        HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
        connection.setHostnameVerifier(mhv);
        connection.setSSLSocketFactory(sslContext.getSocketFactory());
        connection.setRequestProperty(Constants.HttpHeaders.USER_AGENT,Constants.DEFAULT_UA);
        connection.setRequestProperty(Constants.HttpHeaders.ACCEPT,"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        connection.setRequestProperty(Constants.HttpHeaders.COOKIE,Common.buildCookieString(cookie));
    }

    private String getSuningPoint(Map<String, String> cookie) throws Exception{
        String point = null;
        final String url = "http://my.suning.com/pointDrill.do";
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestProperty(Constants.HttpHeaders.USER_AGENT,Constants.DEFAULT_UA);
        connection.setRequestProperty(Constants.HttpHeaders.ACCEPT,"application/json, text/javascript, */*; q=0.01");
        connection.setRequestProperty(Constants.HttpHeaders.COOKIE,Common.buildCookieString(cookie));
        String body = Common.inputStreamToString(connection.getInputStream());
        point = body.substring(1,body.lastIndexOf('"'));
        return point;
    }

    private boolean loginSuning(Map<String, String> cookie, String s) throws Exception{
        byte[] data = s.getBytes();
        String len = String.valueOf(data.length);
        final String url = "https://passport.suning.com/ids/login";
        HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
//        connection.setHostnameVerifier(mhv);
//        connection.setSSLSocketFactory(sslContext.getSocketFactory());
        connection.setDoOutput(true);
        connection.setRequestMethod(Constants.REQUEST_METHOD_POST);
        connection.setRequestProperty(Constants.HttpHeaders.USER_AGENT,Constants.DEFAULT_UA);
        connection.setRequestProperty(Constants.HttpHeaders.ACCEPT,"application/json, text/javascript, */*; q=0.01");
        connection.setRequestProperty(Constants.HttpHeaders.CONTENT_TYPE,"application/x-www-form-urlencoded; charset=UTF-8");
        connection.setRequestProperty(Constants.HttpHeaders.COOKIE,Common.buildCookieString(cookie));
        connection.setRequestProperty(Constants.HttpHeaders.CONTENT_LENGTH,len);
        connection.setRequestProperty(Constants.HttpHeaders.REFERER,"https://passport.suning.com/ids/login?service=https%3A%2F%2Fssl.suning.com%2Fwebapp%2Fwcs%2Fstores%2Fauth%3FtargetUrl%3Dhttp%253A%252F%252Fsearch.suning.com%252F%2525E7%2525A7%2525AF%2525E5%252588%252586%2525E5%252595%252586%2525E5%25259F%25258E%252F%253Fsrc%253Dssds_%2525E7%2525A7%2525AF%2525E5%252588%252586%2525E5%252585%252591%2525E6%25258D%2525A2_recreword_1-2_c_0000000000_%2525E7%2525A7%2525AF%2525E5%252588%252586%2525E5%252595%252586%2525E5%25259F%25258E_0&method=GET&loginTheme=b2c");

        OutputStream os = connection.getOutputStream();
        os.write(data);
        os.flush();
        os.close();

        Common.updateCookie(connection,cookie);
        String responseBody = Common.inputStreamToString(connection.getInputStream());
        if (responseBody.startsWith("<!DOCTYPE")){
            return false;
        }
        JSONObject bodyObj = JSONObject.parseObject(responseBody);
        return bodyObj.getBooleanValue("success");
    }


    private String readVcode(Map<String, String> cookie) throws Exception{
        final String url = "https://vcs.suning.com/vcs/imageCode.htm?uuid=f2526d6a-062f-4e9e-9709-13fee2d4b23d&amp;yys="
                +System.currentTimeMillis();
        HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
        connection.setRequestProperty(Constants.HttpHeaders.USER_AGENT,Constants.DEFAULT_UA);
        connection.setRequestProperty(Constants.HttpHeaders.COOKIE,Common.buildCookieString(cookie));
        Common.updateCookie(connection,cookie);
        final long t = System.currentTimeMillis();
        String picLink;
        if (System.getProperty("os.name").startsWith("Win")){
            picLink="d:\\"+t+".jpeg";
        }else{
            picLink="/usr/local/etc/tomcat8/webapps/images/"+t+".jpeg";
        }

        FileOutputStream fos = new FileOutputStream(new File(picLink));
        int count = -1;
        byte[] buffer = new byte[512];
        InputStream is = connection.getInputStream();
        while ((count = is.read(buffer)) != -1) {
            fos.write(buffer, 0, count);
        }
        is.close();
        fos.close();

        return "http://114.55.133.156:8080/images/"+t+".jpeg";
    }

    private boolean checkNeedVerifyCode(Map<String, String> cookie,String loginName) throws Exception{
        final String url = "https://passport.suning.com/ids/needVerifyCode";
        HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
        connection.setHostnameVerifier(mhv);
        connection.setSSLSocketFactory(sslContext.getSocketFactory());
        connection.setDoOutput(true);
        connection.setRequestMethod(Constants.REQUEST_METHOD_POST);
        connection.setRequestProperty(Constants.HttpHeaders.USER_AGENT,Constants.DEFAULT_UA);
        connection.setRequestProperty(Constants.HttpHeaders.ACCEPT,"application/json, text/javascript, */*; q=0.01");
        connection.setRequestProperty(Constants.HttpHeaders.CONTENT_TYPE,"application/x-www-form-urlencoded; charset=UTF-8");
        connection.setRequestProperty(Constants.HttpHeaders.COOKIE,Common.buildCookieString(cookie));

        final byte[] param = ("username="+loginName).getBytes();
        final int len = param.length;

        connection.setRequestProperty(Constants.HttpHeaders.CONTENT_LENGTH,String.valueOf(len));

        OutputStream os = connection.getOutputStream();
        os.write(param);
        os.flush();
        os.close();

        String body = Common.inputStreamToString(connection.getInputStream());
        return "true".equals(body);
    }

    private String openLoginPage(Map<String, String> cookie) throws Exception{
        final String url = "https://passport.suning.com/ids/login?service=https%3A%2F%2Fssl.suning.com%2Fwebapp%2Fwcs" +
                "%2Fstores%2Fauth%3FtargetUrl%3Dhttp%253A%252F%252Fsearch.suning.com%252F%2525E7%2525A7%2525AF%2525E5" +
                "%252588%252586%2525E5%252595%252586%2525E5%25259F%25258E%252F%253Fsrc%253Dssds_%2525E7%2525A7%2525AF" +
                "%2525E5%252588%252586%2525E5%252585%252591%2525E6%25258D%2525A2_recreword_1-2_c_0000000000_%2525E7%2525A7" +
                "%2525AF%2525E5%252588%252586%2525E5%252595%252586%2525E5%25259F%25258E_0&method=GET&loginTheme=b2c";
        HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
        connection.setRequestProperty(Constants.HttpHeaders.USER_AGENT,Constants.DEFAULT_UA);
        connection.setRequestProperty(Constants.HttpHeaders.ACCEPT,"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");

        Common.updateCookie(connection,cookie);

        String html = Common.inputStreamToString(connection.getInputStream()).substring(0,4096).trim();
        Pattern pattern = Pattern.compile("loginPBK=\\\".*\\\".*companycard_url");
        Matcher matcher = pattern.matcher(html);
        if (matcher.find()) {
            String text = matcher.group(0);
            return text==null? null :text.substring(text.indexOf('"')+1,text.lastIndexOf('"'));
        }
        return null;
    }

    private String encryptPwd(String pwd,String pbk) throws Exception{
        se.eval(jscriptCode);
        Invocable inv2 = (Invocable) se;
        String data=inv2.invokeFunction("suningEncrypt",pwd,pbk).toString();
        return data;
    }


//    public static void main(String[] args){
//        try{
//            JfRequest request = new JfRequest();
//            request.setAccount("18516318026");
//            request.setPassword("wgy123");
//            SuningService suningService = new SuningService();
//            JfResult result = suningService.requestVerifyCode(request);
//            if (!"".equals(result.getData())){
//                System.out.println("请输入验证码");
//                Scanner scanner = new Scanner(System.in);
//                request.setCode(scanner.nextLine());
//            }
//            result = suningService.queryIntegral(request);
//            System.out.println("[苏宁积分]："+result.getPoints());
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }
}
