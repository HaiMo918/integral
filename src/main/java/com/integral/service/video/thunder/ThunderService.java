package com.integral.service.video.thunder;

import com.alibaba.fastjson.JSONObject;
import com.integral.tools.Base64Util;
import com.integral.tools.MD5;
import com.integral.utils.*;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kris on 2017/3/1.
 */
public class ThunderService implements IQueryIntegral{
    private ScriptEngineManager engineManager;
    private ScriptEngine engine;
    private MyX509TrustManager xtm = new MyX509TrustManager();
    private MyHostNameVerify mhv = new MyHostNameVerify();
    private SSLContext sslContext = null;
    private String csrf_token;
    private final String refer = "http://i.xunlei.com/login/?r_d=1&use_cdn=0&timestamp=" +System.currentTimeMillis() +"&refurl=http%3A%2F%2Fjifen.xunlei.com%2Fmyinfo%2F";

    public ThunderService() throws Exception{
        sslContext = SSLContext.getInstance("SSL");
        X509TrustManager[] xtmArray = new X509TrustManager[]{xtm};
        sslContext.init(null, xtmArray, new java.security.SecureRandom());
        engineManager = new ScriptEngineManager();
        engine = engineManager.getEngineByName("javascript");
    }

    @Override
    public JfResult requestVerifyCode(JfRequest request) throws Exception {
        return null;
    }

    @Override
    public JfResult queryIntegral(JfRequest request) throws Exception {
        JfResult result = new JfResult();
        Map<String,String> thunderCookie = new HashMap<>();
        doReferAccess(thunderCookie);
        riskReport(thunderCookie);
        String response = doPreLoginThunder(request.getAccount(),thunderCookie);
        if (response.contains("limit")){
            result.setMessage("[迅雷]预处理验证失败");
            return result;
        }


        String url = "https://login.xunlei.com/sec2login/?csrf_token="+this.csrf_token;
        response = doLoginThunder(thunderCookie,url,request.getPassword(),request.getAccount());
        if (!"".equals(response)){
            result.setMessage("[迅雷]登录失败");
            return result;
        }

        url = "http://jifen.xunlei.com//call?c=user&a=getJifenScore&callback=jQuery111205287585339271343_1488802126136&_="+System.currentTimeMillis();
        String jf = queryPoints(thunderCookie,url);
        result.setMessage("[迅雷]获取积分成功");
        result.setPoints(jf);
        return result;
    }

    private void riskReport(Map<String,String> cookie) throws Exception{
        /**
         * xl_fp的计算方式 md5(fp_raw)
         * xl_fp_sign 计算方式：xl_al(fp_raw)
         */
//        final String xl_fp_raw = Base64Util.encode(new ThunderConfig().toString());
        final String xl_fp_raw = ThunderConfig.fp_raw;
        final String xl_fp = CallJSMethod("D:\\projects\\integral\\src\\main\\java\\com\\integral\\service\\video\\thunder\\js\\thundermd5.js","toMd5String",xl_fp_raw);
        final String xl_fp_sign = CallJSMethod("D:\\projects\\integral\\src\\main\\java\\com\\integral\\service\\video\\thunder\\js\\algorithm.js","xl_al",xl_fp_raw);
        final String param = "xl_fp_raw="+xl_fp_raw+"&xl_fp="+ xl_fp +"&xl_fp_sign="+ xl_fp_sign+"&cachetime="+System.currentTimeMillis();
        final String url = "https://login.xunlei.com/risk?cmd=report";
        byte[] data = param.getBytes();
        final String length=String.valueOf(data.length);

        HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        //connection.setHostnameVerifier(mhv);
        //connection.setSSLSocketFactory(sslContext.getSocketFactory());
        connection.setRequestProperty(Constants.HttpHeaders.USER_AGENT,Constants.DEFAULT_UA);
        connection.setRequestProperty(Constants.HttpHeaders.CONTENT_TYPE,"application/x-www-form-urlencoded");
        connection.setRequestProperty(Constants.HttpHeaders.CONTENT_LENGTH,length);
        connection.setRequestProperty(Constants.HttpHeaders.REFERER,refer);

        OutputStream os = connection.getOutputStream();
        os.write(data);
        os.flush();
        os.close();

        Common.updateCookie(connection,cookie);
        cookie.put("_x_t_","0");
    }
    private String doPreLoginThunder(String user,Map<String,String> cookie) throws Exception{
        final long t1 = System.currentTimeMillis();
        final String deviceid = cookie.get("deviceid").substring(0,32);
        this.csrf_token=CallJSMethod("D:\\projects\\integral\\src\\main\\java\\com\\integral\\service\\video\\thunder\\js\\thundermd5.js","toMd5String",deviceid);
        ThunderPreLoginData thunderPreLoginData = new ThunderPreLoginData();
        thunderPreLoginData.u=user;
        thunderPreLoginData.csrf_token=this.csrf_token;
        thunderPreLoginData.cachetime=t1;

        final String url = "https://login.xunlei.com/check/?"+thunderPreLoginData.toString();
        HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
        connection.setRequestProperty(Constants.HttpHeaders.USER_AGENT,Constants.DEFAULT_UA);
        connection.setRequestProperty(Constants.HttpHeaders.ACCEPT,"image/webp,image/*,*/*;q=0.8");
        connection.setRequestProperty(Constants.HttpHeaders.REFERER,refer);
        connection.setRequestProperty(Constants.HttpHeaders.COOKIE,Common.buildCookieString(cookie));

        Common.updateCookie(connection,cookie);
        return Common.inputStreamToString(connection.getInputStream());
    }

    private void doReferAccess(Map<String,String> cookie) throws Exception
    {
        HttpURLConnection connection = (HttpURLConnection) new URL(refer).openConnection();
        connection.setRequestProperty(Constants.HttpHeaders.USER_AGENT,Constants.DEFAULT_UA);
        connection.setRequestProperty(Constants.HttpHeaders.ACCEPT,"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        Common.updateCookie(connection,cookie);
    }

    private String CallJSMethod(String js,String method,String param) throws Exception
    {
        File f = new File(js);
        int size = (int) f.length();
        byte[] code = new byte[size];
        FileInputStream fis = new FileInputStream(f);
        fis.read(code);
        String jscode = new String(code);
        String value = null;
        engine.eval(jscode);
        Invocable inv = (Invocable) engine;
        value = (String) inv.invokeFunction(method,param);
        return value;
    }

    private String doLoginThunder(Map<String,String> cookie,String url,String p,String u) throws Exception
    {
        ThunderLoginData thunderLoginData = new ThunderLoginData();
        thunderLoginData.p=p;
        thunderLoginData.u=u;
        thunderLoginData.cachetime = System.currentTimeMillis();
        String param = thunderLoginData.toString();
        byte[] buffer = param.getBytes();
        String len = String.valueOf(buffer.length);

        HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
        connection.setRequestProperty(Constants.HttpHeaders.USER_AGENT,Constants.DEFAULT_UA);
        connection.setRequestProperty(Constants.HttpHeaders.COOKIE,Common.buildCookieString(cookie));
        connection.setRequestProperty(Constants.HttpHeaders.CONTENT_TYPE,"application/x-www-form-urlencoded");
        connection.setRequestProperty(Constants.HttpHeaders.CONTENT_LENGTH,len);
        connection.setRequestProperty(Constants.HttpHeaders.REFERER,refer);
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");

        OutputStream os = connection.getOutputStream();
        os.write(buffer);
        os.flush();
        os.close();

        Common.updateCookie(connection,cookie);
        return Common.inputStreamToString(connection.getInputStream());
    }

    private String queryPoints(Map<String,String> cookie,String url) throws Exception{
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestProperty(Constants.HttpHeaders.USER_AGENT,Constants.DEFAULT_UA);
        connection.setRequestProperty(Constants.HttpHeaders.ACCEPT,"text/javascript, application/javascript, application/ecmascript, application/x-ecmascript");
        connection.setRequestProperty(Constants.HttpHeaders.COOKIE,Common.buildCookieString(cookie));

        String body = Common.inputStreamToString(connection.getInputStream());
        String jsonText = body.substring(body.indexOf('{'),body.lastIndexOf('}')+1);
        JSONObject obj = JSONObject.parseObject(jsonText);
        JSONObject data = obj.getJSONObject("data");
        int totalPoints = data.getIntValue("gold")+data.getIntValue("437");
        return String.valueOf(totalPoints);
    }
    public static void main(String[] args){
        try{
            JfRequest request = new JfRequest();
            request.setAccount("515091847@qq.com");
            request.setPassword("lqh_1985t");
            ThunderService service = new ThunderService();
            JfResult result = service.queryIntegral(request);
            System.out.println(result.getPoints());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
