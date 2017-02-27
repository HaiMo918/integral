package com.integral.service.tuniu;

import com.alibaba.fastjson.JSONObject;
import com.integral.utils.*;
import org.springframework.stereotype.Service;

import javax.net.ssl.*;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.integral.utils.Common.buildCookieString;
import static com.integral.utils.Common.updateCookie;

/**
 * Created by liuqinghai on 2016/12/8.
 *
 */
@Service
public class Tuniu implements IQueryIntegral{
    private MyX509TrustManager xtm = new MyX509TrustManager();
    private MyHostNameVerify mhv = new MyHostNameVerify();
    private SSLContext sslContext = null;
    private PlatForm mPF=PlatForm.WINDOWS;

    private enum PlatForm{
        WINDOWS(1),LINUX(2);
        int pt;
        PlatForm(int p){
            this.pt=p;
        }
    }

    public Tuniu() throws Exception{
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
        JfResult result=new JfResult();
        Map<String,String> cookie = new HashMap<>();
        openHomePage(cookie);
        String ssoURL = openULoginPage(cookie);
        if (ssoURL==null){
            return result;
        }
        doOpenSSOConnectPage(ssoURL,cookie);
        openPassportPage(cookie);
        openPassportPage(cookie);
        String tokenUrl = doLoginTuniu(null,request.getAccount(),request.getPassword(),cookie);
        if (tokenUrl==null){
            result.setMessage("failed");
            return result;
        }
        String mainPage=null;
        if (tokenUrl.contains("token")){
            mainPage=openTokenURL(tokenUrl,cookie);
            if (mainPage==null){
                result.setMessage("failed");
                return result;
            }
        }else{
            mainPage=openTokenURL(tokenUrl,cookie);
        }
        openHomePage(mainPage,cookie);
        String ituniuPageURL = openPersonalPage(cookie);
        openITuniuPage(ituniuPageURL,cookie);
        String score = getUserPromotionInfo(cookie);
        result.setMessage("success");
        result.setPoints(score);
        return result;
    }

    private void openHomePage(String mainPage,Map<String,String> cookies) throws Exception{
        URL url = new URL(mainPage);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("User-Agent",Constants.DEFAULT_UA);
        connection.setRequestProperty("Cookie",buildCookieString(cookies));
        updateCookie(connection,cookies);
    }


    // TODO: 2016/12/12  open home page
    private void openHomePage(Map<String,String> cookie) throws Exception{
        URL url = new URL("http://www.tuniu.com");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("User-Agent",Constants.DEFAULT_UA);
        updateCookie(connection,cookie);
    }

    // TODO: 2016/12/12 open u-login page
    private String openULoginPage(Map<String,String> cookies) throws Exception{
        URL url = new URL("http://www.tuniu.com/u/login");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("User-Agent",Constants.DEFAULT_UA);
        connection.setRequestProperty("Cookie",buildCookieString(cookies));
        connection.setInstanceFollowRedirects(false);
        updateCookie(connection,cookies);
        if (connection.getHeaderFields().containsKey("Location")){
            return connection.getHeaderField("Location");
        }
        return null;
    }

    // TODO: 2016/12/12 open passport.tuniu.com
    private void openPassportPage(Map<String,String> cookies) throws Exception{
        URL url = new URL("https://passport.tuniu.com/");
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestProperty("User-Agent",Constants.DEFAULT_UA);
        connection.setRequestProperty("Cookie",buildCookieString(cookies));
        updateCookie(connection,cookies);
    }

    // TODO: 2016/12/12
    private String doOpenSSOConnectPage(String page,Map<String,String> cookies) throws Exception{
        URL url = new URL(page);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setInstanceFollowRedirects(false);
        connection.setRequestProperty("User-Agent",Constants.DEFAULT_UA);
        connection.setRequestProperty("Cookie",buildCookieString(cookies));
        updateCookie(connection,cookies);
        if (connection.getHeaderField("Location")!=null){
            return connection.getHeaderField("Location");
        }
        return null;
    }

    // TODO: 2016/12/12
    private String doLoginTuniu(String ssoUrl,String user,String pass,Map<String,String> cookies) throws Exception{
        String param = "login_type=P-N&intlCode=&username="+user+"&password="+caclPwd(pass);
        byte[] outData = param.getBytes("utf-8");
        int length = outData.length;

        URL url = new URL("https://passport.tuniu.com/login/post");
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setInstanceFollowRedirects(false);
        connection.setSSLSocketFactory(sslContext.getSocketFactory());
        connection.setHostnameVerifier(mhv);
        connection.setRequestProperty("User-Agent", Constants.DEFAULT_UA);
        connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
        connection.setRequestProperty("Content-Length",length+"");
        connection.setRequestProperty("Referer","https://passport.tuniu.com/login?origin=http://www.tuniu.com/ssoConnect");
        connection.setRequestProperty("Host","passport.tuniu.com");
        connection.setRequestProperty("Cookie",buildCookieString(cookies));
        connection.setRequestProperty("Accept","image/jpeg, application/x-ms-application, image/gif, application/xaml+xml, image/pjpeg, application/x-ms-xbap, */*");
        connection.setRequestProperty("Accept-Language","zh-CN");
        connection.setRequestProperty("Accept-Encoding","gzip, deflate");

        OutputStream os = connection.getOutputStream();
        os.write(outData);
        os.flush();
        os.close();

        if (connection.getHeaderField("Location")!=null){
            updateCookie(connection,cookies);
            return connection.getHeaderField("Location");
        }
        return null;
    }

    // TODO: 2016/12/12
    private String openTokenURL(String tokenUrl,Map<String,String> cookies) throws Exception{
        URL url = new URL(tokenUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("User-Agent",Constants.DEFAULT_UA);
        connection.setRequestProperty("Cookie",buildCookieString(cookies));
        connection.setInstanceFollowRedirects(false);
        updateCookie(connection,cookies);
        if (connection.getHeaderFields().containsKey("Location")){
            return connection.getHeaderField("Location");
        }
        return null;
    }

    private String openPersonalPage(Map<String,String> cookies) throws Exception{
        URL url = new URL("http://www.tuniu.com/u");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("User-Agent",Constants.DEFAULT_UA);
        connection.setRequestProperty("Cookie",buildCookieString(cookies));
        connection.setInstanceFollowRedirects(false);
        updateCookie(connection,cookies);
        if (connection.getHeaderFields().containsKey("Location")){
            return connection.getHeaderField("Location");
        }
        return null;
    }

    private void openITuniuPage(String page,Map<String,String> cookies) throws Exception{
        URL url = new URL(page);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("User-Agent",Constants.DEFAULT_UA);
        connection.setRequestProperty("Cookie",buildCookieString(cookies));
        updateCookie(connection,cookies);
    }

    private String getUserPromotionInfo(Map<String,String> cookies) throws Exception{
        URL url = new URL("https://i.tuniu.com/usercenter/usercenter/userPromotionInfo");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("User-Agent",Constants.DEFAULT_UA);
        connection.setRequestProperty("Cookie",buildCookieString(cookies));

        InputStream is = connection.getInputStream();
        StringBuilder sb = new StringBuilder();
        String line;
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        while ((line=br.readLine())!=null){
            sb.append(line);
        }
        br.close();
        is.close();

        JSONObject object = JSONObject.parseObject(sb.toString());
        if (object.getBoolean("success")){
            JSONObject dataObj = object.getJSONObject("data");
            if (dataObj!=null){
                return dataObj.getString("totalCredits");
            }
        }
        return "-1";
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
    private String caclPwd(String pwd) throws Exception{
        File f;
        if (mPF==PlatForm.WINDOWS) {
            f = new File("D:\\projects\\integral\\src\\main\\java\\com\\integral\\service\\tuniu\\md5.js");
        }else {
            f=new File("/usr/local/etc/tomcat8/webapps/jquery/md5.js");
        }
        ScriptEngineManager sem = new ScriptEngineManager();
        ScriptEngine se = sem.getEngineByName("js");
        StringBuilder sb = new StringBuilder();
        String line;
        BufferedReader br = new BufferedReader(new FileReader(f));
        while ((line=br.readLine())!=null){
            sb.append(line);
        }
        br.close();
        String script=sb.toString();
        se.eval(script);
        Invocable inv2 = (Invocable) se;
        String res=(String)inv2.invokeFunction("hex_md5",pwd);
        return res;
    }
    public static void main(String[] args){
        try{
            JfRequest request = new JfRequest();
            request.setPassword("ilikejf1");
            request.setAccount("18017108839");
            Tuniu tuniu = new Tuniu();
            JfResult result=tuniu.queryIntegral(request);
            System.out.println(result.getPoints());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
