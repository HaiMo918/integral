package com.integral.service.yihaodian;

import com.alibaba.fastjson.JSONObject;
import com.integral.utils.*;
import org.apache.hc.client5.http.methods.HttpRequestBase;
import org.springframework.stereotype.Service;

import javax.net.ssl.HttpsURLConnection;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by liuqinghai on 2016/12/22.
 *
 */
@Service
public class YihaodianService implements IQueryIntegral{
    private static ScriptEngineManager sem;
    private static ScriptEngine se;
    private PlatForm pf=PlatForm.WINDOWS;
    private static String jscode;
    private Map<String, String> cookies;

    public YihaodianService(){
        sem = new ScriptEngineManager();
        se = sem.getEngineByName("javascript");
        cookies = new HashMap<String, String>();
    }

    @Override
    public JfResult requestVerifyCode(JfRequest request) throws Exception {
        return null;
    }

    @Override
    public JfResult queryIntegral(JfRequest request) throws Exception {
        JfResult result = new JfResult();
        String redirectURL = openHomePage();
        openRTPage(redirectURL);
        String referer = redirectURL.substring(0, redirectURL.indexOf('&'));
        openLoginPage(referer);

        readJSCode();
        String username = executeJSEncryptCall(request.getAccount());
        String password = executeJSEncryptCall(request.getPassword());
        String token = executeJSEncryptCall("");
        String param = String.format("credentials.username=%s&credentials.password=%s&validCode=验证码&sig=&captchaToken=%s&loginSource=1%returnUrl=%s&isAutoLogin=0",
                username,
                password,
                token,
                redirectURL);
        if (!doLogin(param)) {
            result.setCode(Constants.ErrorCode.LOGIN_FAILED + "");
            result.setMessage("login failed");
            return result;
        }

        String jf = getJF(referer);
        result.setData(jf);
        return null;
    }

    private String openHomePage() throws Exception {
        URL url = new URL("http://www.yhd.com/");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.107 Safari/537.36");
        connection.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8");
        connection.setRequestProperty("Accept-Encoding", "gzip, deflate, sdch");
        connection.setRequestProperty("Host", "www.yhd.com");
        connection.setInstanceFollowRedirects(false);
        updateCookie(connection);
        return connection.getHeaderField("Location");
    }

    private void openRTPage(String page) throws Exception {
        URL url = new URL(page);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.107 Safari/537.36");
        connection.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8");
        connection.setRequestProperty("Accept-Encoding", "gzip, deflate, sdch");
        connection.setRequestProperty("Host", "www.yhd.com");
        connection.setRequestProperty("Cookie", buildCookieString(cookies));
        connection.setInstanceFollowRedirects(false);
        updateCookie(connection);
    }


    private void openLoginPage(String referer) throws Exception {
        URL url = new URL("https://passport.yhd.com/passport/login_input.do");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.107 Safari/537.36");
        connection.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8");
        connection.setRequestProperty("Accept-Encoding", "gzip, deflate, sdch");
        connection.setRequestProperty("Host", "passport.yhd.com");
        connection.setRequestProperty("Referer", referer);
        connection.setRequestProperty("Cookie", buildCookieString(cookies));

    }

    private boolean doLogin(String param) throws Exception {
        byte[] buffer = param.getBytes();
        int len = buffer.length;
        URL url = new URL("https://passport.yhd.com/publicPassport/login.do");
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Accept", "*/*");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        connection.setRequestProperty("Origin", "https://passport.yhd.com");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.107 Safari/537.36");
        connection.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8");
        connection.setRequestProperty("Accept-Encoding", "gzip, deflate, sdch");
        connection.setRequestProperty("Host", "passport.yhd.com");
        connection.setRequestProperty("Referer", "https://passport.yhd.com/passport/login_input.do");
        connection.setRequestProperty("Cookie", buildCookieString(cookies));
        connection.setRequestProperty("Content-Length", len + "");

        OutputStream os = connection.getOutputStream();
        os.write(buffer);
        os.flush();
        os.close();
        updateCookie(connection);
        String response = Common.inputStreamToString(connection.getInputStream());
        JSONObject obj = JSONObject.parseObject(response);
        if (obj.getIntValue("errorCode") == 0) {
            return true;
        }
        return false;
    }

    private String getJF(String referer) throws Exception {
        long t = System.currentTimeMillis();
        String provinceId = cookies.get("provinceId");
        String userID = cookies.get("yihaodian_uid");
        String callback = "jQuery11130017935971962288022_" + t;
        String link = "http://www.yhd.com/homepage/ajaxFindPrismMemberUserInfo.do?callback=" + callback
                + "&userId=" + userID + "&currSiteId=1&currSiteType=1&provinceId=" + provinceId + "&_=" + System.currentTimeMillis();
        URL url = new URL(link);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Accept", "text/javascript, application/javascript, application/ecmascript, application/x-ecmascript, */*; q=0.01");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.107 Safari/537.36");
        connection.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8");
        connection.setRequestProperty("Accept-Encoding", "gzip, deflate, sdch");
        connection.setRequestProperty("Host", "www.yhd.com");
        connection.setRequestProperty("Referer", referer);
        connection.setRequestProperty("Cookie", buildCookieString(cookies));

        String data = Common.inputStreamToString(connection.getInputStream());
        System.out.println(data);
        return null;
    }

    private void readJSCode() throws Exception {
        File jsFile = null;
        if (pf == PlatForm.WINDOWS) {
            jsFile = new File("D:\\projects\\Integral\\js\\pc_login_new.js");
        }
        if (pf == PlatForm.LINUX) {
            jsFile = new File("/usr/local/etc/tomcat8/webapps/jquery/pc_login_new.js");
        }


        StringBuilder sb = new StringBuilder();
        String line;
        BufferedReader br = new BufferedReader(new FileReader(jsFile));
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();
        jscode = sb.toString();

    }
    private String executeJSEncryptCall(String param) throws Exception{
        if (jscode==null||jscode.length()==0){
            readJSCode();
        }
        se.eval(jscode);
        Invocable inv2 = (Invocable) se;
        String key = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDXQG8rnxhslm+2f7Epu3bB0inrnCaTHhUQCYE+2X+qWQgcpn+Hvwyks3A67mvkIcyvV0ED3HFDf+ANoMWV1Ex56dKqOmSUmjrk7s5cjQeiIsxX7Q3hSzO61/kLpKNH+NE6iAPpm96Fg15rCjbm+5rR96DhLNG7zt2JgOd2o1wXkQIDAQAB";
        String data=inv2.invokeFunction("my_encrypt",param, key).toString();
        return data;
    }

    private enum PlatForm{
        WINDOWS(1),LINUX(2);
        int pt;
        PlatForm(int p){
            this.pt=p;
        }
    }


    private synchronized void updateCookie(HttpURLConnection connection) throws Exception {
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
                        cookies.put(key, keyvalue);
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
    public static void main(String[] args) throws Exception {
        YihaodianService service = new YihaodianService();
        JfRequest request = new JfRequest();
        request.setAccount("15202823217");
        request.setPassword("lqh_1985t");
        service.queryIntegral(request);
    }
}
