package com.integral.service.communite.chinaunicom;

import com.alibaba.fastjson.JSONObject;
import com.integral.utils.Constants;
import com.integral.utils.IQueryIntegral;
import com.integral.utils.JfRequest;
import com.integral.utils.JfResult;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ChinaUnicom implements IQueryIntegral{
    private static final Object lock = new Object();
    private String callback = "jQuery17207180133164121163_";

    @Override
    public JfResult requestVerifyCode(JfRequest request) throws Exception {
        return null;
    }

    @Override
    public JfResult queryIntegral(JfRequest request) throws Exception {
        synchronized (lock) {
            JfResult jfResult = new JfResult();
            Map<String,String> thisCookie = new HashMap<String, String>();
            initCookie(thisCookie);
            requestNqr(thisCookie);
            String loginParam = buildLoginParam(request.getAccount(),request.getPassword());
            String loginResult = doLogin(loginParam,thisCookie);
            if ("".equals(loginResult)){
                jfResult.setMessage("登录失败");
                jfResult.setCode(Constants.ErrorCode.LOGIN_FAILED+"");
                return jfResult;
            }

            String points = queryPoints(thisCookie);
            if (points==null){
                jfResult.setMessage("获取联通积分失败");
            }else {
                jfResult.setMessage("获取联通积分成功");
                jfResult.setCode(Constants.ErrorCode.ERROR_SUCCESS+"");
                jfResult.setPoints(points);
            }
            return jfResult;
        }
    }

    private void requestNqr(Map<String,String> thisCookie) throws Exception {
        final String address = "https://uac.10010.com/oauth2/genqr?timestamp=" + System.currentTimeMillis();
        URL url = new URL(address);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Cookie", map2String(thisCookie));
        connection.setRequestProperty("User-Agent", Constants.DEFAULT_UA);
        updateChinaMobileCookie(connection, thisCookie);
    }

    private String buildLoginParam(String account,String password) {
        StringBuilder sb = new StringBuilder();
        sb.append("https://uac.10010.com/portal/Service/MallLogin?callback=").append(callback)
                .append(System.currentTimeMillis())
                .append("&req_time=").append(System.currentTimeMillis())
                .append("&redirectURL=http%3A%2F%2Fjf2.10010.com%2F")
                .append("&userName=").append(account)
                .append("&password=").append(password)
                .append("&pwdType=01&productType=01&redirectType=01&rememberMe=1")
                .append("&_=").append(System.currentTimeMillis());
        return sb.toString();
    }

    private String doLogin(String pageURL,Map<String,String> cookies)throws Exception {
        URL url = new URL(pageURL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Accept", "text/javascript, application/javascript, application/ecmascript, application/x-ecmascript, */*; q=0.01");
        connection.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
        connection.setRequestProperty("Cookie", map2String(cookies));
        connection.setRequestProperty("Referer", "https://uac.10010.com/portal/homeLogin");
        connection.setRequestProperty("User-Agent", Constants.DEFAULT_UA);
        updateChinaMobileCookie(connection, cookies);
        InputStream is = connection.getInputStream();
        int t = is.available();
        InputStreamReader isr = new InputStreamReader(is);
        char[] content = new char[t];
        isr.read(content);
        isr.close();
        is.close();

        String data = new String(content);
        String jsonStr = data.substring(data.indexOf('(') + 1, data.lastIndexOf(')'));
        JSONObject object = JSONObject.parseObject(jsonStr);
        return object.getString("resultCode");
    }

    private String queryPoints(Map<String,String> cookie) throws Exception {
        String url = "http://jf2.10010.com/jf-mall/availablePoints";
        URL myurl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) myurl.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Accept", "*/*");
        connection.setRequestProperty("Accept-Encoding", "gzip, deflate");
        connection.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
        connection.setRequestProperty("Cookie", map2String(cookie));
        connection.setRequestProperty("Host", "jf2.10010.com");
        connection.setRequestProperty("Referer", "http://jf2.10010.com/");
        connection.setRequestProperty("User-Agent", Constants.DEFAULT_UA);
        connection.setRequestProperty("X-Requested-With", "XMLHttpRequest");

        InputStream is = connection.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        char[] content = new char[256];
        isr.read(content);
        isr.close();
        is.close();

        String data = new String(content);
        data = data.trim();
        JSONObject object = JSONObject.parseObject(data);
        return object.getString("points");
    }

    private void updateChinaMobileCookie(HttpURLConnection connection,Map<String,String> theCookie) {
        Map<String, List<String>> headerFields = connection.getHeaderFields();
        if (!headerFields.containsKey("Set-Cookie")) {
            return;
        }

        List<String> values = headerFields.get("Set-Cookie");
        for (String value : values) {
            int firstSemicolon = value.indexOf(';');
            if (firstSemicolon != -1) {
                String cookie = value.substring(0, firstSemicolon);
                int firstColon = cookie.indexOf('=');
                String theKey = cookie.substring(0, firstColon);
                String theValue = cookie.substring(firstColon + 1);
                theCookie.put(theKey, theValue);
            }
        }
    }

    private String map2String(Map<String, String> mapData) throws Exception {
        StringBuilder data = new StringBuilder();
        for (String key : mapData.keySet()) {
            data.append(key).append("=");
            if (mapData.get(key) == null) {
                data.append("null");
            } else {
                data.append(mapData.get(key));
            }
            data.append(";");
        }
        String cookies = data.toString();
        return cookies.substring(0, cookies.length() - 1);
    }

    private void initCookie(Map<String,String> thisCookie) {
        thisCookie.put("WT_FPC", UnicomAPI.dcsFPC(8));
        thisCookie.put("Hm_lvt_9208c8c641bfb0560ce7884c36938d9d", UnicomAPI.hmLvt());
        thisCookie.put("Hm_lpvt_9208c8c641bfb0560ce7884c36938d9d", UnicomAPI.hmLpvt());
        thisCookie.put("_n3fa_cid","be5f5bf62a13424bead6ff8cf2767f09");
        thisCookie.put("_n3fa_ext","ft="+System.currentTimeMillis()/1000);
        thisCookie.put("_n3fa_lpvt_a9e72dfe4a54a20c3d6e671b3bad01d9",getTimeStamp());
        thisCookie.put("_n3fa_lvt_a9e72dfe4a54a20c3d6e671b3bad01d9",getTimeStamp()+","+getTimeStamp()+","+getTimeStamp()+","+getTimeStamp());
    }

    private String getTimeStamp(){
        return String.valueOf(System.currentTimeMillis()/1000);
    }
}
