package com.integral.service.eleme;

import com.alibaba.fastjson.JSONObject;
import com.integral.utils.*;
import org.springframework.stereotype.Service;

import javax.net.ssl.HttpsURLConnection;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by liuqinghai on 2017/1/3.
 */
@Service
public class ElemeService implements IQueryIntegral{
    private static Map<String,Map<String,String>> cookies;

    public ElemeService(){
        cookies = new HashMap<String,Map<String,String>>();
    }

    @Override
    public JfResult requestVerifyCode(JfRequest request) throws Exception {
        JfResult result = new JfResult();
        String id = Common.createUUID();
        result.setId(id);
        /*
        请求图片验证码
         */
        Map<String,String> cookie = new HashMap<>();

        URL url = new URL("https://mainsite-restapi.ele.me/v1/captchas");
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("accept","application/json, text/plain, */*");
        connection.setRequestProperty("accept-encoding","gzip, deflate, br");
        connection.setRequestProperty("accept-language","zh-CN,zh;q=0.8");
        connection.setRequestProperty("content-length","0");
        connection.setRequestProperty("referer","https://account.ele.me/login");
        connection.setRequestProperty("user-agent","Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");

        OutputStream os = connection.getOutputStream();
        os.write("".getBytes());
        os.flush();
        os.close();

        updateCookie(connection,cookie);
        String data = new String(GZipUtils.decompress(GZipUtils.input2byte(connection.getInputStream())));
        JSONObject obj = JSONObject.parseObject(data);
        result.setData("https://mainsite-restapi.ele.me/v1/captchas/"+obj.getString("code"));
        cookies.put(id,cookie);
        return result;
    }

    @Override
    public JfResult queryIntegral(JfRequest request) throws Exception {
        JfResult jfResult=new JfResult();
        Map<String,String> cookie = cookies.get(request.getId());
        ElemeLoginData data=new ElemeLoginData();
        data.captcha_code=request.getCode();
        data.username=request.getAccount();
        data.password=request.getPassword();
        String param = JSONObject.toJSONString(data);
        String result = doLogin(param,cookie);
        if (result==null){
            jfResult.setCode(Constants.ErrorCode.LOGIN_FAILED+"");
            jfResult.setMessage("login failed");
            return jfResult;
        }

        String jf = getPoints(cookie);
        jfResult.setPoints(jf);
        return jfResult;
    }


    private synchronized void updateCookie(HttpURLConnection connection,Map<String,String> thecookie) throws Exception {
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

    private String doLogin(String param,Map<String,String> cookie) throws Exception{
        URL url = new URL("https://mainsite-restapi.ele.me/v1/login");
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("accept","application/json, text/plain, */*");
        connection.setRequestProperty("accept-encoding","gzip, deflate, br");
        connection.setRequestProperty("accept-language","zh-CN,zh;q=0.8");
        connection.setRequestProperty("content-length","0");
        connection.setRequestProperty("referer","https://account.ele.me/login");
        connection.setRequestProperty("user-agent","Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
        connection.setRequestProperty("Cookie",buildCookieString(cookie));
        connection.setRequestProperty("Content-Length",param.getBytes().length+"");
        connection.setRequestProperty("Content-Type","application/json");
        OutputStream so = connection.getOutputStream();
        so.write(param.getBytes());
        so.flush();
        so.close();

        updateCookie(connection,cookie);

        String data = new String(GZipUtils.decompress(GZipUtils.input2byte(connection.getInputStream())));
        JSONObject object = JSONObject.parseObject(data);
        return object.getString("user_id");
    }

    private String getPoints(Map<String,String> cookie) throws Exception{
        URL url = new URL("https://mainsite-restapi.ele.me/v1/user?extras%5B%5D=premium_vip&extras%5B%5D=is_auto_generated");
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestProperty("accept","application/json, text/plain, */*");
        connection.setRequestProperty("accept-encoding","gzip, deflate, br");
        connection.setRequestProperty("accept-language","zh-CN,zh;q=0.8");
        connection.setRequestProperty("referer","https://www.ele.me/");
        connection.setRequestProperty("user-agent","Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
        connection.setRequestProperty("Cookie",buildCookieString(cookie));

        String content = new String(GZipUtils.decompress(GZipUtils.input2byte(connection.getInputStream())));
        JSONObject jsonObject = JSONObject.parseObject(content);
        return jsonObject.getString("point");
    }

}
