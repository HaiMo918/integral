package com.integral.service.quanjia;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.integral.utils.*;
import org.springframework.stereotype.Service;

import javax.net.ssl.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by liuqinghai on 2017/1/3.
 */
@Service
public class Quanjia implements IQueryIntegral{
    static Map<String,Map<String,String>> cookies = new HashMap<String,Map<String,String>>();
    private MyX509TrustManager xtm = new MyX509TrustManager();
    private MyHostNameVerify mhv = new MyHostNameVerify();
    private SSLContext sslContext = null;

    public Quanjia() throws Exception{
        sslContext = SSLContext.getInstance("SSL");
        X509TrustManager[] xtmArray = new X509TrustManager[]{xtm};
        sslContext.init(null, xtmArray, new java.security.SecureRandom());
    }

    @Override
    public JfResult requestVerifyCode(JfRequest request) throws Exception {
        JfResult jfResult = new JfResult();
        String id = Common.createUUID();
        jfResult.setId(id);

        Map<String,String> thisCookie = new HashMap<String,String>();
        URL url = new URL("https://www.maxxipoint.com/getCaptcha.do?t=0.4588608755038548");
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setHostnameVerifier(mhv);
        connection.setSSLSocketFactory(sslContext.getSocketFactory());
        connection.setRequestProperty("Host","www.maxxipoint.com");
        connection.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
        connection.setRequestProperty("Accept","image/webp,image/*,*/*;q=0.8");
        connection.setRequestProperty("Referer","https://www.maxxipoint.com/");
        connection.setRequestProperty("Accept-Encoding","gzip, deflate, sdch, br");
        updateCookie(connection,thisCookie);

        InputStream is = connection.getInputStream();
        byte[] img = new byte[1024];
        int count=-1;
        long time= System.currentTimeMillis();
        File file = new File("/usr/local/etc/tomcat8/webapps/images/"+time+".jpeg");
        //File file = new File("d:\\"+time+".jpeg");
        FileOutputStream fos = new FileOutputStream(file);
        while ((count=is.read(img))!=-1){
            fos.write(img,0,count);
        }
        fos.close();
        is.close();
        jfResult.setData("http://114.55.133.156:8080/images/"+time+".jpeg");
        cookies.put(id,thisCookie);
        return jfResult;
    }

    @Override
    public JfResult queryIntegral(JfRequest request) throws Exception {
        JfResult result = new JfResult();
        Map<String ,String > cookie = cookies.get(request.getId());
        if (!checkVerifycode(request.getCode(),cookie)){
            result.setCode(Constants.ErrorCode.LOGIN_FAILED+"");
            result.setMessage("验证码错误");
            return result;
        }
        String param = String.format("mobileNumber=%s&password=%s&imgCode=%s&rememberMe=false",request.getAccount(),request.getPassword(),request.getCode());
        if (!doLogin(cookie,param)){
            result.setCode(Constants.ErrorCode.LOGIN_FAILED+"");
            result.setMessage("login failed");
            return result;
        }

        param = getQuanjiaJf(cookie);
        result.setPoints(param);
        return result;
    }

    private synchronized void updateCookie(HttpURLConnection connection, Map<String,String> thecookie) throws Exception {
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
                        thecookie.put(key, keyvalue);
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

    private boolean doLogin(Map<String,String> cookie,String param) throws Exception{
        URL url = new URL("https://www.maxxipoint.com/checkLogin.do");
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setHostnameVerifier(mhv);
        connection.setSSLSocketFactory(sslContext.getSocketFactory());
        connection.setRequestProperty("Host","www.maxxipoint.com");
        connection.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
        connection.setRequestProperty("Accept","application/json, text/javascript, */*; q=0.01");
        connection.setRequestProperty("Referer","https://www.maxxipoint.com/");
        connection.setRequestProperty("Accept-Encoding","gzip, deflate, sdch, br");
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");
        connection.setRequestProperty("Host","www.maxxipoint.com");
        connection.setRequestProperty("Cookie",buildCookieString(cookie));
        connection.setRequestProperty("Content-Length",param.getBytes().length+"");

        OutputStream os = connection.getOutputStream();
        os.write(param.getBytes());
        os.close();

        updateCookie(connection,cookie);

        String data = Common.inputStreamToString(connection.getInputStream());
        JSONObject object=JSONObject.parseObject(data);
        String status = object.getString("status");
        if ("success".equals(status)){
            return true;
        }
        return false;
    }

    private String getQuanjiaJf(Map<String,String > cookie) throws Exception{
        URL url = new URL("https://www.maxxipoint.com/member/memberCardDetail.do?_r=0.9355408035018711");
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setHostnameVerifier(mhv);
        connection.setSSLSocketFactory(sslContext.getSocketFactory());
        connection.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
        connection.setRequestProperty("Accept","application/json, text/javascript, */*; q=0.01");
        connection.setRequestProperty("Referer","https://www.maxxipoint.com/member/member.do");
        connection.setRequestProperty("Accept-Encoding","gzip, deflate, sdch, br");
        connection.setRequestProperty("Host","www.maxxipoint.com");
        connection.setRequestProperty("Cookie",buildCookieString(cookie));
        String page = Common.inputStreamToString(connection.getInputStream());
        JSONObject object = JSONObject.parseObject(page);
        JSONArray array = object.getJSONArray("cards");
        if (array.size()==0) {
            return "-1";
        }
        object = array.getJSONObject(0);
        return object.getString("points");
    }


    class MyX509TrustManager implements X509TrustManager {

        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s)
                throws CertificateException {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s)
                throws CertificateException {

        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;//new X509Certificate[0];
        }
    }

    /**
     * MyHostNameVerify
     */
    class MyHostNameVerify implements HostnameVerifier {
        @Override
        public boolean verify(String s, SSLSession sslSession) {
            return true;
        }
    }


    private boolean checkVerifycode(String code,Map<String,String > cookie) throws Exception{
        URL url = new URL("https://www.maxxipoint.com/validateCaptcha.do?imgCode="+code);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setHostnameVerifier(mhv);
        connection.setSSLSocketFactory(sslContext.getSocketFactory());
        connection.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
        connection.setRequestProperty("Accept","application/json, text/javascript, */*; q=0.01");
        connection.setRequestProperty("Referer","https://www.maxxipoint.com/");
        connection.setRequestProperty("Accept-Encoding","gzip, deflate, sdch, br");
        connection.setRequestProperty("Host","www.maxxipoint.com");
        connection.setRequestProperty("Cookie",buildCookieString(cookie));

        String result = Common.inputStreamToString(connection.getInputStream());
        if (result.equals("false")){
            return false;
        }
        return true;
    }
}
