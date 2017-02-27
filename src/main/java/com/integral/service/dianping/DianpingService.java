package com.integral.service.dianping;

import com.alibaba.fastjson.JSONObject;
import com.integral.utils.*;
import org.springframework.stereotype.Service;

import javax.net.ssl.HttpsURLConnection;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by liuqinghai on 2017/1/3.
 */
@Service
public class DianpingService implements IQueryIntegral{
    private static Map<String,Map<String,String>> cookies = new HashMap<String,Map<String,String>>();

    private final String agent="Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36";

    @Override
    public JfResult requestVerifyCode(JfRequest request) throws Exception {
        JfResult result = new JfResult();
        Map<String,String> cookie=new HashMap<String,String>();;
        String id = request.getAccount();

        openHomePage(cookie);
        openCityListPage(cookie);
        String uuid = captchaShow(id,cookie);
        result = sendVerifyMessage(uuid,id,cookie);
        result.setId(id);
        cookies.put(id,cookie);
        return result;
    }


    @Override
    public JfResult queryIntegral(JfRequest request) throws Exception {
        JfResult result = new JfResult();

        Map<String,String > cookie = cookies.get(request.getId());
        if (!doLogin(cookie,request.getAccount(),request.getCode())){
            result.setCode(Constants.ErrorCode.LOGIN_FAILED+"");
            result.setMessage("login failed");
            return result;
        }

        String jf = getDianpingJf(cookie);
        result.setPoints(jf);
        return result;
    }

    private void openHomePage(Map<String,String> cookie) throws Exception{
        URL url = new URL("http://www.dianping.com/");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        connection.setRequestProperty("Accept-Encoding","gzip, deflate, sdch");
        connection.setRequestProperty("User-Agent",agent);
        connection.setRequestProperty("Host","www.dianping.com");
        connection.setRequestProperty("Accept-Language","zh-CN,zh;q=0.8");
        Common.updateCookie(connection,cookie);
    }

    private void openCityListPage(Map<String,String> cookie) throws Exception{
        URL url = new URL("http://www.dianping.com/citylist");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        connection.setRequestProperty("Accept-Encoding","gzip, deflate, sdch");
        connection.setRequestProperty("User-Agent",agent);
        connection.setRequestProperty("Host","www.dianping.com");
        connection.setRequestProperty("Accept-Language","zh-CN,zh;q=0.8");
        connection.setRequestProperty("Cookie",Common.buildCookieString(cookie));
        Common.updateCookie(connection,cookie);
    }

    private String captchaShow(String phone,Map<String,String > cookie) throws Exception{
        URL url = new URL("https://www.dianping.com/account/ajax/captchaShow");
        String param = "captchaChannel=202&params=%22"+phone+"%22";
        byte[] buffer = param.getBytes();
        int len = buffer.length;

        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        connection.setRequestProperty("Accept-Encoding","gzip, deflate, sdch");
        connection.setRequestProperty("User-Agent",agent);
        connection.setRequestProperty("Host","www.dianping.com");
        connection.setRequestProperty("Accept-Language","zh-CN,zh;q=0.8");
        connection.setRequestProperty("Cookie",Common.buildCookieString(cookie));
        connection.setRequestProperty("Content-Length",len+"");
        connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");
        connection.setRequestProperty("Referer","https://www.dianping.com/account/iframeLogin?callback=EasyLogin_frame_callback0&wide=false&redir=http%3A%2F%2Fwww.dianping.com%2Fcitylist");

        OutputStream os = connection.getOutputStream();
        os.write(buffer);
        os.flush();
        os.close();

        String page = Common.inputStreamToString(connection.getInputStream());
        JSONObject object = JSONObject.parseObject(page);
        if (object.getIntValue("code")!=200){
            return null;
        }
        return object.getJSONObject("msg").getString("uuid");
    }
    private JfResult sendVerifyMessage(String uuid,String phone,Map<String,String> cookie) throws Exception{
        JfResult result = new JfResult();
        URL url = new URL("https://www.dianping.com/account/ajax/mobileVerifySend");
        String param = "mobileNo="+phone+"&uuid="+uuid+"&type=304";
        byte[] buffer = param.getBytes();
        int length = buffer.length;

        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        connection.setRequestProperty("Accept-Encoding","gzip, deflate, sdch");
        connection.setRequestProperty("User-Agent",agent);
        connection.setRequestProperty("Host","www.dianping.com");
        connection.setRequestProperty("Accept-Language","zh-CN,zh;q=0.8");
        connection.setRequestProperty("Cookie",Common.buildCookieString(cookie));
        connection.setRequestProperty("Content-Length",length+"");
        connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");
        connection.setRequestProperty("Referer","https://www.dianping.com/account/iframeLogin?callback=EasyLogin_frame_callback0&wide=false&redir=http%3A%2F%2Fwww.dianping.com%2Fcitylist");

        OutputStream os = connection.getOutputStream();
        os.write(buffer);
        os.flush();
        os.close();

        String page = Common.inputStreamToString(connection.getInputStream());
        JSONObject object = JSONObject.parseObject(page);
        if (object.getIntValue("code")==200){
            result.setMessage(object.getJSONObject("msg").getString("info"));
            result.setCode(Constants.ErrorCode.ERROR_SUCCESS+"");
            return result;
        }
        result.setMessage(object.getJSONObject("msg").getString("info"));
        result.setCode(Constants.ErrorCode.OTHER_EXCEPTION+"");
        return result;
    }

    private boolean doLogin(Map<String,String > cookie,String phone,String code) throws Exception{
        URL url = new URL("https://www.dianping.com/account/ajax/mfastlogin");
        String param = "mobile="+phone+"&vcode="+code+"&channel=13&type=304&keepMobile=off";
        byte[] buffer = param.getBytes();
        int length = buffer.length;
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Accept","*/*");
        connection.setRequestProperty("Accept-Encoding","gzip, deflate, sdch");
        connection.setRequestProperty("User-Agent",agent);
        connection.setRequestProperty("Host","www.dianping.com");
        connection.setRequestProperty("Accept-Language","zh-CN,zh;q=0.8");
        connection.setRequestProperty("Cookie",Common.buildCookieString(cookie));
        connection.setRequestProperty("Content-Length",length+"");
        connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");
        connection.setRequestProperty("Referer","https://www.dianping.com/account/iframeLogin?callback=EasyLogin_frame_callback0&wide=false&redir=http%3A%2F%2Fwww.dianping.com%2Fcitylist");

        OutputStream os = connection.getOutputStream();
        os.write(buffer);
        os.flush();
        os.close();
        Common.updateCookie(connection,cookie);
        String page = Common.inputStreamToString(connection.getInputStream());
        JSONObject object = JSONObject.parseObject(page);
        if (object.getIntValue("code")==200){
            return true;
        }
        return false;
    }

    private String getDianpingJf(Map<String,String> cookie) throws Exception{
        URL url = new URL("http://www.dianping.com/member/myinfo");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        connection.setRequestProperty("Accept-Encoding","gzip, deflate, sdch");
        connection.setRequestProperty("User-Agent",agent);
        connection.setRequestProperty("Host","www.dianping.com");
        connection.setRequestProperty("Accept-Language","zh-CN,zh;q=0.8");
        connection.setRequestProperty("Cookie",Common.buildCookieString(cookie));
        connection.setRequestProperty("Referer","http://www.dianping.com/citylist");
        String page = new String(GZipUtils.decompress(GZipUtils.input2byte(connection.getInputStream())));
        Pattern pattern = Pattern.compile("'dp_myinfo_dcash'\\);\".>\\d{1,5}<");
        Matcher matcher = pattern.matcher(page);
        if (matcher.find()){
            String temp = matcher.group(0);
            if (temp!=null) {
                pattern = Pattern.compile("\\d{1,5}");
                matcher = pattern.matcher(temp);
                if (matcher.find()){
                    return matcher.group(0);
                }
            }
        }
        return null;
    }

    public static void main(String[] args) throws Exception{
        DianpingService service = new DianpingService();
        JfRequest request = new JfRequest();
        request.setAccount("18621299168");
        service.requestVerifyCode(request);
        System.out.println("请输入验证码：");
        Scanner scanner = new Scanner(System.in);
        String code = scanner.nextLine();
        request.setPassword(code);
        request.setId("18621299168");
        JfResult result = service.queryIntegral(request);
        System.out.println(result.getPoints());
    }
}
