package com.integral.service.suning;

import com.alibaba.fastjson.JSONObject;
import com.integral.utils.*;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;

import javax.net.ssl.HttpsURLConnection;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by kris on 2017/1/15.
 */
public class SuningService implements IQueryIntegral {
    private static ScriptEngineManager sem;
    private static ScriptEngine se;
    private static boolean IS_DEBUG=true;
    private static String jscriptCode;
    private static Map<String,Map<String,String>> mCookie;
    private static String mLoginPBK;
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
        boolean needVerifyCode = checkNeedVerifyCode(cookie);
        if (needVerifyCode){
            String vCodeUrl = readVcode(cookie);
            result.setData(vCodeUrl);
        }else{
            result.setData("");
        }

        return result;
    }

    private String readVcode(Map<String, String> cookie) throws Exception{
        return null;
    }

    private boolean checkNeedVerifyCode(Map<String, String> cookie) throws Exception{

        return false;
    }

    private String openLoginPage(Map<String, String> cookie) throws Exception{
        final String url = "https://passport.suning.com/ids/login?service=https%3A%2F%2Fssl.suning.com%2Fwebapp%2Fwcs%2Fstores%2Fauth%3FtargetUrl%3Dhttp%253A%252F%252Fsearch.suning.com%252F%2525E7%2525A7%2525AF%2525E5%252588%252586%2525E5%252595%252586%2525E5%25259F%25258E%252F%253Fsrc%253Dssds_%2525E7%2525A7%2525AF%2525E5%252588%252586%2525E5%252585%252591%2525E6%25258D%2525A2_recreword_1-2_c_0000000000_%2525E7%2525A7%2525AF%2525E5%252588%252586%2525E5%252595%252586%2525E5%25259F%25258E_0&method=GET&loginTheme=b2c";
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

    @Override
    public JfResult queryIntegral(JfRequest request) throws Exception {
        JfResult result = new JfResult();

        return null;
    }




    private String encryptPwd(String pwd,String pbk) throws Exception{
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
            suningService.requestVerifyCode(request);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
