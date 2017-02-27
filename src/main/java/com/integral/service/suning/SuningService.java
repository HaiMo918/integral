package com.integral.service.suning;

import com.alibaba.fastjson.JSONObject;
import com.integral.utils.*;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.File;
import java.io.FileReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kris on 2017/1/15.
 */
public class SuningService implements IQueryIntegral {
    private static ScriptEngineManager sem;
    private static ScriptEngine se;
    private final String agent ="Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36";
    private final String encoding="gzip, deflate, sdch";
    private final String language="zh-CN,zh;q=0.8";
    private final String accept="text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8";
    private static boolean IS_DEBUG=true;
    private static String jscriptCode;
    static {
        try {
            File file;
            if (IS_DEBUG) {
                file = new File("D:\\projects\\Integral\\js\\suning_login.js");
            } else {
                file = new File("/usr/local/etc/tomcat8/webapps/jquery/suning_login.js");
            }
            FileReader fr = new FileReader(file);
            char[] buffer = new char[(int) file.length()];
            fr.read(buffer);
            fr.close();
            jscriptCode = new String(buffer);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public SuningService(){
        sem = new ScriptEngineManager();
        se = sem.getEngineByName("javascript");
    }

    @Override
    public JfResult requestVerifyCode(JfRequest request) throws Exception {
        return null;
    }

    @Override
    public JfResult queryIntegral(JfRequest request) throws Exception {
        JfResult result = new JfResult();
        result.setMessage("获取苏宁积分失败");

        Map<String,String> cookie = new HashMap<>();
        openHomePage(cookie);
        boolean need = needVerifyCode(cookie,request.getAccount());
        System.out.println("是否需要验证码："+need);
        boolean success = loginSuning(cookie,request.getAccount(),request.getPassword());
        return null;
    }

    private boolean loginSuning(Map<String,String> cookie,String user,String pwd) throws Exception{
        String pwd2=encryptPwd(pwd);
        StringBuilder param = new StringBuilder();
        param.append("jsonViewType=true")
                .append("&username=").append(user)
                .append("&password=")
                .append("&password2=").append(pwd2)
                .append("&loginTheme=b2c")
                .append("&service=https://aq.suning.com/asc/auth?targetUrl=http%3A%2F%2Fmy.suning.com%2F")
                .append("&rememberMe=false")
                .append("&client=app");
        byte[] data = param.toString().getBytes();
        int len = data.length;
        HttpURLConnection connection = (HttpURLConnection) new URL("https://passport.suning.com/ids/login").openConnection();
        connection.setRequestProperty("Accept","application/json, text/javascript, */*; q=0.01");
        connection.setRequestProperty("Origin","https://passport.suning.com");
        addHeader(connection,
                true,
                "application/x-www-form-urlencoded; charset=UTF-8",
                len+"",
                "https://passport.suning.com/ids/login?service=https%3A%2F%2Faq.suning.com%2Fasc%2Fauth%3FtargetUrl%3Dhttp%253A%252F%252Fmy.suning.com%252F&loginTheme=b2c",
                Common.buildCookieString(cookie));

        OutputStream os = connection.getOutputStream();
        os.write(data);
        os.flush();
        os.close();

        Common.updateCookie(connection,cookie);

        String page = Common.inputStreamToString(connection.getInputStream());
        JSONObject object = JSONObject.parseObject(page);
        return object.getBooleanValue("success");
    }

    private void openHomePage(Map<String,String> cookie) throws Exception{
        final String loginurl="https://passport.suning.com/ids/login?service=https%3A%2F%2Faq.suning.com%2Fasc%2Fauth%3FtargetUrl%3Dhttp%253A%252F%252Fmy.suning.com%252F&loginTheme=b2c";
        HttpURLConnection connection = (HttpURLConnection) new URL(loginurl).openConnection();
        addHeader(connection,false,null,null,null, Common.buildCookieString(cookie));
        Common.updateCookie(connection,cookie);
    }

    private boolean needVerifyCode(Map<String,String> cookie,String user) throws Exception{
        String param = "username="+user;
        byte[] data = param.getBytes();
        int len = data.length;
        HttpURLConnection connection = (HttpURLConnection) new URL("https://passport.suning.com/ids/needVerifyCode").openConnection();
        connection.setRequestProperty("Accept","application/json, text/javascript, */*; q=0.01");
        addHeader(connection,
                true,
                "application/x-www-form-urlencoded; charset=UTF-8",
                String.valueOf(len),
                " https://passport.suning.com/ids/login?service=https%3A%2F%2Faq.suning.com%2Fasc%2Fauth%3FtargetUrl%3Dhttp%253A%252F%252Fmy.suning.com%252F&loginTheme=b2c",
                Common.buildCookieString(cookie));
        OutputStream os = connection.getOutputStream();
        os.write(data);
        os.flush();
        os.close();

        Common.updateCookie(connection,cookie);

        String page = new String(GZipUtils.decompress(GZipUtils.input2byte(connection.getInputStream())));
        return "true".equals(page);
    }
    private void addHeader(HttpURLConnection connection,boolean isPost,String type,String len,String refer,String cookie) throws Exception{
        connection.setRequestProperty("User-Agent", agent);
        connection.setRequestProperty("Accept-Encoding",encoding);
        connection.setRequestProperty("Accept-Language",language);
        if (isPost){
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type",type);
            connection.setRequestProperty("Content-Length",len);
            connection.setRequestProperty("Referer",refer);
        }
        connection.setRequestProperty("Cookie",cookie);
    }


    private String encryptPwd(String pwd) throws Exception{
        se.eval(jscriptCode);
        Invocable inv2 = (Invocable) se;
        String data=inv2.invokeFunction("suningEncrypt",pwd).toString();
        return data;
    }


    public static void main(String[] args){
        try{
            JfRequest request = new JfRequest();
            request.setAccount("18516318026");
            request.setPassword("wgy123");
            SuningService suningService = new SuningService();
            suningService.queryIntegral(request);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
