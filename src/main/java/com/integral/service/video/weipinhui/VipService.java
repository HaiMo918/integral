package com.integral.service.video.weipinhui;

import com.alibaba.fastjson.JSONObject;
import com.integral.utils.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by kris on 2017/3/22.
 */

// <li func="total" class="vipmoney-list-li vip-money-total">0</li>
@Service
public class VipService implements IQueryIntegral{
    private static MyX509TrustManager xtm = new MyX509TrustManager();
    private static MyHostNameVerify mhv = new MyHostNameVerify();
    private static SSLContext sslContext = null;
    private static Map<String,Map<String,String>> mVipCookie = new HashMap<>();
    private final String mVipc = "Tm9yDAXVB8MbWgTt3nLIhGiR1jSjPaTOoVbNcK4lDTWbA%2F6x6PRtFB64E3j3CjO%2BR7lnt4KXfU45OLqzA1kQfZiS9hTMJKjQufaWy1rqBi4SqJ2xaDYx9A%3D%3D";

    private VipService() throws Exception{
        sslContext = SSLContext.getInstance("SSL");
        X509TrustManager[] xtmArray = new X509TrustManager[]{xtm};
        sslContext.init(null, xtmArray, new java.security.SecureRandom());
    }

    @Override
    public JfResult requestVerifyCode(JfRequest request) throws Exception {
        JfResult result = new JfResult();
        Map<String,String> cookie = new HashMap<>();

        openLoginPage(cookie);
        VipLoginData data = firstLoginWithOutCaptcha(cookie,request.getAccount(),request.getPassword());
        cookie.put("vipc",data.vipc);
        final String captcha = "https://passport.vip.com/captcha/getCaptcha?type=0&vipc="+URLEncoder.encode(data.vipc,"UTF-8")+"&v="+System.currentTimeMillis();
        String picURL = getPicURL(captcha,cookie);
        result.setData(picURL);
        mVipCookie.put(request.getAccount(),cookie);
        return result;
    }

    private VipLoginData firstLoginWithOutCaptcha(Map<String, String> cookie,String user,String password) throws Exception{
        final String url = "https://passport.vip.com/login";
        final String param = "loginName="+user+"&password="+password+"&remUser=0&vipc=&captcha=";
        byte[] data = param.getBytes();
        String len = String.valueOf(data.length);
        HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
        connection.setSSLSocketFactory(sslContext.getSocketFactory());
        connection.setHostnameVerifier(mhv);
        connection.setRequestMethod(Constants.REQUEST_METHOD_POST);
        connection.setDoOutput(true);
        connection.setRequestProperty(Constants.HttpHeaders.USER_AGENT,Constants.DEFAULT_UA);
        connection.setRequestProperty(Constants.HttpHeaders.ACCEPT,"application/json, text/javascript, */*; q=0.01");
        connection.setRequestProperty(Constants.HttpHeaders.REFERER,"https://passport.vip.com/login?src=http%3A%2F%2Fwww.vip.com%2F");
        connection.setRequestProperty(Constants.HttpHeaders.COOKIE,Common.buildCookieString(cookie));
        connection.setRequestProperty(Constants.HttpHeaders.CONTENT_LENGTH,len);
        connection.setRequestProperty(Constants.HttpHeaders.CONTENT_TYPE,"application/x-www-form-urlencoded; charset=UTF-8");
        OutputStream os = connection.getOutputStream();
        os.write(data);
        os.flush();
        os.close();

        String body = Common.inputStreamToString(connection.getInputStream());
        VipLoginResponse object = JSONObject.parseObject(body,VipLoginResponse.class);
        VipLoginData data1 = JSONObject.parseObject(object.data,VipLoginData.class);
        return data1;
    }

    @Override
    public JfResult queryIntegral(JfRequest request) throws Exception {
        JfResult result = new JfResult();
        Map<String,String> cookie = mVipCookie.get(request.getAccount());
        JSONObject validCaptchaCode = checkValidCaptcha(cookie,request.getCode());
        if (!validCaptchaCode.getBooleanValue("result")){
            result.setMessage("[唯品会]验证码错误");
            return result;
        }

        final String url = "https://passport.vip.com/login";
        final String param = createVipLoginParam(request.getAccount(),request.getPassword(),request.getCode(),cookie.get("vipc"));
        VipLoginResponse response = tryLogin(cookie,url,param);
        if ("error".equals(response.result)){
            result.setMessage("[唯品会]登录失败");
            return result;
        }

        VipLoginData data = JSONObject.parseObject(response.data,VipLoginData.class);
        boolean isIn = doRealAccess(data.signedApiUrl,cookie);
        if (!isIn){
            result.setMessage("[唯品会]登录失败");
            return result;
        }
        String points = getPoints(cookie);
        if (points==null){
            result.setMessage("[唯品会]获取积分失败");
            return result;
        }
        result.setPoints(points);
        result.setMessage("[唯品会]获取积分成功");
        return result;
    }

    private String getPoints(Map<String, String> cookie) throws Exception{
        String points = null;
        final String url = "http://myi.vip.com/vipmoney.html";
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestProperty(Constants.HttpHeaders.USER_AGENT,Constants.DEFAULT_UA);
        connection.setRequestProperty(Constants.HttpHeaders.ACCEPT,"*/*");
        connection.setRequestProperty(Constants.HttpHeaders.COOKIE,Common.buildCookieString(cookie));

        String html = Common.inputStreamToString(connection.getInputStream());
        Document doc = Jsoup.parse(html);
        Elements elements = doc.select("vipmoney-list-li vip-money-total");
        points = elements.get(0).val();
        return points;
    }

    private boolean doRealAccess(String signedApiUrl, Map<String, String> cookie) throws Exception{
        HttpsURLConnection connection = (HttpsURLConnection) new URL(signedApiUrl).openConnection();
        connection.setSSLSocketFactory(sslContext.getSocketFactory());
        connection.setHostnameVerifier(mhv);
        connection.setRequestProperty(Constants.HttpHeaders.USER_AGENT,Constants.DEFAULT_UA);
        connection.setRequestProperty(Constants.HttpHeaders.ACCEPT,"*/*");
        connection.setRequestProperty(Constants.HttpHeaders.COOKIE,Common.buildCookieString(cookie));
        Common.updateCookie(connection,cookie);

        String body = Common.inputStreamToString(connection.getInputStream());
        String bodyJson = body.substring(body.indexOf('{'),body.indexOf('}')+1);
        return JSONObject.parseObject(bodyJson).getIntValue("status")==1;
    }


    private JSONObject checkValidCaptcha(Map<String,String> cookie, String captcha) throws Exception{
        String originVipc = cookie.get("vipc");
        cookie.remove("vipc");
        final String url = "https://passport.vip.com/captcha/ajaxCheckCaptcha";
        HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
        connection.setSSLSocketFactory(sslContext.getSocketFactory());
        connection.setHostnameVerifier(mhv);
        connection.setRequestMethod(Constants.REQUEST_METHOD_POST);
        connection.setDoOutput(true);
        connection.setRequestProperty(Constants.HttpHeaders.USER_AGENT,Constants.DEFAULT_UA);
        connection.setRequestProperty(Constants.HttpHeaders.ACCEPT,"application/json, text/javascript, */*; q=0.01");
        connection.setRequestProperty(Constants.HttpHeaders.ACCEPT_ENCODING,"gzip, deflate, br");
        connection.setRequestProperty(Constants.HttpHeaders.ACCEPT_LANGUAGE,"zh-CN,zh;q=0.8");

        connection.setRequestProperty(Constants.HttpHeaders.REFERER,"https://passport.vip.com/login?src=http%3A%2F%2Fwww.vip.com%2F");

        String param = "captcha="+captcha+"&vipc="+originVipc+"&anticache="+System.currentTimeMillis()+"&type=0";
        byte[] data = param.getBytes();
        String len = String.valueOf(data.length);
        connection.setRequestProperty(Constants.HttpHeaders.CONTENT_LENGTH,len);
        connection.setRequestProperty(Constants.HttpHeaders.CONTENT_TYPE,"application/x-www-form-urlencoded; charset=UTF-8");
        connection.setRequestProperty(Constants.HttpHeaders.COOKIE,Common.buildCookieString(cookie));

        OutputStream os = connection.getOutputStream();
        os.write(data);
        os.flush();
        os.close();

        cookie.put("vipc",originVipc);
        String body = new String(GZipUtils.decompress(GZipUtils.input2byte(connection.getInputStream())));
        JSONObject object = JSONObject.parseObject(body);
        return object;
    }

    private void openLoginPage(Map<String, String> cookie) throws Exception{
        final String url = "https://passport.vip.com/login?src=http%3A%2F%2Fwww.vip.com%2F";
        HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
        connection.setSSLSocketFactory(sslContext.getSocketFactory());
        connection.setHostnameVerifier(mhv);
        connection.setRequestProperty(Constants.HttpHeaders.USER_AGENT,Constants.DEFAULT_UA);
        connection.setRequestProperty(Constants.HttpHeaders.ACCEPT,"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        Common.updateCookie(connection,cookie);
    }

    private String getPicURL(String url ,Map<String, String> cookie) throws Exception{
        final long t = System.currentTimeMillis();
        final String onlieURL = "http://114.55.133.156:8080/images/"+t+".png";

        String picLink;
        if (System.getProperty("os.name").startsWith("Win")){
            picLink="d:\\"+t+".jpeg";
        }else{
            picLink="/usr/local/etc/tomcat8/webapps/images/"+t+".jpeg";
        }
        FileOutputStream fos = new FileOutputStream(new File(picLink));
        int count = -1;
        byte[] buffer = new byte[512];

        HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
        connection.setSSLSocketFactory(sslContext.getSocketFactory());
        connection.setHostnameVerifier(mhv);
        connection.setRequestProperty(Constants.HttpHeaders.USER_AGENT,Constants.DEFAULT_UA);
        connection.setRequestProperty(Constants.HttpHeaders.ACCEPT,"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        connection.setRequestProperty(Constants.HttpHeaders.REFERER,"https://passport.vip.com/login?src=http%3A%2F%2Fwww.vip.com%2F");
        //connection.setRequestProperty(Constants.HttpHeaders.COOKIE,Common.buildCookieString(cookie));
        int code = connection.getResponseCode();
        InputStream is = connection.getInputStream();
        while ((count = is.read(buffer)) != -1) {
            fos.write(buffer, 0, count);
        }
        is.close();
        fos.close();
        return onlieURL;
    }

    private VipLoginResponse tryLogin(Map<String, String> cookie, String url, String param) throws Exception{
        cookie.remove("vipc");
        byte[] data = param.getBytes();
        String len = String.valueOf(data.length);
        HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
        connection.setSSLSocketFactory(sslContext.getSocketFactory());
        connection.setHostnameVerifier(mhv);
        connection.setRequestMethod(Constants.REQUEST_METHOD_POST);
        connection.setDoOutput(true);
        connection.setRequestProperty(Constants.HttpHeaders.USER_AGENT,Constants.DEFAULT_UA);
        connection.setRequestProperty(Constants.HttpHeaders.ACCEPT,"application/json, text/javascript, */*; q=0.01");
        connection.setRequestProperty(Constants.HttpHeaders.REFERER,"https://passport.vip.com/login?src=http%3A%2F%2Fwww.vip.com%2F");
        connection.setRequestProperty(Constants.HttpHeaders.COOKIE,Common.buildCookieString(cookie));
        connection.setRequestProperty(Constants.HttpHeaders.CONTENT_LENGTH,len);
        connection.setRequestProperty(Constants.HttpHeaders.CONTENT_TYPE,"application/x-www-form-urlencoded; charset=UTF-8");
        OutputStream os = connection.getOutputStream();
        os.write(data);
        os.flush();
        os.close();

        Common.updateCookie(connection,cookie);
        String body = Common.inputStreamToString(connection.getInputStream());
        return JSONObject.parseObject(body,VipLoginResponse.class);
    }

    private String createVipLoginParam(String account, String password, String code,String vipc) throws Exception{
        VipLoginParam param = new VipLoginParam();
        param.captcha=code;
        param.loginName=account;
        param.password=password;
        param.remUser="1";
        param.vipc=vipc;
        return param.toString();
    }


    public static void main(String[] args) throws Exception{
        JfRequest request = new JfRequest();
        JfResult result;
        request.setAccount("15202823217");
        request.setPassword("testjf123");
        VipService service = new VipService();
        result = service.requestVerifyCode(request);
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入验证码");
        String code = scanner.nextLine();
        request.setCode(code);
        result = service.queryIntegral(request);
        System.out.println(result.getPoints());
    }
}
