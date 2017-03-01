package com.integral.service.video.pptv;

import com.alibaba.fastjson.JSONObject;
import com.integral.utils.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import javax.net.ssl.HttpsURLConnection;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by liuqinghai on 2017/1/23.
 */
@Service
public class PPTVService implements IQueryIntegral{
    @Override
    public JfResult requestVerifyCode(JfRequest request) throws Exception {
        return null;
    }

    @Override
    public JfResult queryIntegral(JfRequest request) throws Exception {
        JfResult result = new JfResult();
        Map<String,String> pptvCookie=new HashMap<>();
        String puid = getPUID(pptvCookie);
        pptvCookie.put("PUID",puid);
        pptvCookie.put("userAutoLogin","notAuto");
        LoginResponse loginResponse = login(pptvCookie,request.getAccount(),request.getPassword());
        if (loginResponse.errorCode!=0){
            result.setMessage(URLDecoder.decode(loginResponse.message,"UTF-8"));
            return result;
        }

        LoginResult loginResult = JSONObject.parseObject(loginResponse.result,LoginResult.class);
        QueryResult queryResult = queryCookie(pptvCookie,loginResult.token,request.getAccount());
        if (queryResult.errorCode!=0){
            result.setMessage(URLDecoder.decode(queryResult.message,"UTF-8"));
            return result;
        }
        QueryResponse queryResponse = JSONObject.parseObject(queryResult.result,QueryResponse.class);
        pptvCookie.put("blogBind",queryResponse.blogBind);
        pptvCookie.put("PPKey",queryResponse.PPKey);
        pptvCookie.put("PPName",queryResponse.PPName);
        pptvCookie.put("ppToken",queryResponse.ppToken);
        pptvCookie.put("UDI",queryResponse.UDI);

        String ppi = getPPI(pptvCookie,request.getAccount());
        pptvCookie.put("ppi",ppi);

        String jf = getjf(pptvCookie,loginResult.token,request.getAccount());
        result.setPoints(jf);
        return result;
    }

    private String getjf(Map<String, String> cookie, String token, String account) throws Exception{
        final String url = "http://api.usergrowth.pptv.com/getUserBilling?&cb=jQuery18309605722799663385_1485159734936"
                +"&username="+account+"&from=web&format=jsonp&token="+token;
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestProperty("User-Agent",Constants.DEFAULT_UA);
        connection.setRequestProperty("Cookie",Common.buildCookieString(cookie));

        String body=Common.inputStreamToString(connection.getInputStream());
        String jsontext=body.substring(body.indexOf('{'),body.lastIndexOf('}')+1);
        JSONObject object = JSONObject.parseObject(jsontext);
        JSONObject resultObj = object.getJSONObject("result");
        return resultObj.getIntValue("userAvailablePoint")+"'";
    }

    private String getPPI(Map<String, String> cookie, String account) throws Exception{
        final String url="http://tools.aplusapi.pptv.com/get_ppi?a="+account+"&cb=wn";
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestProperty("User-Agent",Constants.DEFAULT_UA);
        connection.setRequestProperty("Cookie",Common.buildCookieString(cookie));

        String body=Common.inputStreamToString(connection.getInputStream());
        String jsontext=body.substring(body.indexOf('{'),body.lastIndexOf('}')+1);
        JSONObject object = JSONObject.parseObject(jsontext);
        return object.getString("ppi");
    }

    private QueryResult queryCookie(Map<String, String> cookie, String token, String account) throws Exception{
        StringBuilder sburl = new StringBuilder();
        sburl.append("https://passport.pptv.com/v3/cookies/query.do?").append("&token=").append(token)
                .append("&username=").append(account).append("&from=web&version=1.0.0&format=jsonp&cb=pplive_callback_0");
        HttpsURLConnection connection = (HttpsURLConnection) new URL(sburl.toString()).openConnection();
        connection.setRequestProperty("User-Agent",Constants.DEFAULT_UA);
        connection.setRequestProperty("Cookie",Common.buildCookieString(cookie));
        Common.updateCookie(connection,cookie);

        String body=Common.inputStreamToString(connection.getInputStream());
        String jsontext=body.substring(body.indexOf('{'),body.lastIndexOf('}')+1);
        QueryResult result = JSONObject.parseObject(jsontext,QueryResult.class);
        return result;
    }

    private LoginResponse login(Map<String, String> cookie, String account,String password) throws Exception{
        StringBuilder sb = new StringBuilder();
        sb.append("https://passport.pptv.com/v3/login/login.do?")
                .append("format=jsonp&from=web&cb=jQuery183011041068164298506_1485159718421")
                .append("&username=").append(account)
                .append("&password=").append(password).append("&CheckboxSaveInfo=on&_=").append(System.currentTimeMillis());
        HttpsURLConnection connection = (HttpsURLConnection) new URL(sb.toString()).openConnection();
        connection.setRequestProperty("User-Agent",Constants.DEFAULT_UA);
        connection.setRequestProperty("Cookie",Common.buildCookieString(cookie));
        Common.updateCookie(connection,cookie);

        String body=Common.inputStreamToString(connection.getInputStream());
        String jsontext=body.substring(body.indexOf('{'),body.lastIndexOf('}')+1);
        LoginResponse response = JSONObject.parseObject(jsontext,LoginResponse.class);
        return response;
    }

    private String getPUID(Map<String, String> cookie) throws Exception{
        final String url = "http://api.passport.pptv.com/v3/checkcode/guid.do";
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestProperty("User-Agent", Constants.DEFAULT_UA);
        Common.updateCookie(connection,cookie);
        String html = Common.inputStreamToString(connection.getInputStream());
        Document doc = Jsoup.parse(html);
        Elements errorcode=doc.getElementsByTag("errorCode");
        if (errorcode.size()==0){
            return null;
        }
        String code=errorcode.get(0).childNodes().get(0).toString().replace("\n","");
        if (!"0".equals(code)){
            return null;
        }
        Elements puid = doc.getElementsByClass("string");
        String strPuid=puid.get(0).childNodes().get(0).toString().replace("\n","");
        return strPuid;
    }


    public static void main(String[] args){
        try{
            JfRequest request = new JfRequest();
            request.setAccount("18516318026");
            request.setPassword("wgy123");
            PPTVService service = new PPTVService();
            JfResult result = service.queryIntegral(request);
            System.out.println(result.getPoints());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
