package com.integral.service.meituan;

import com.alibaba.fastjson.JSONObject;
import com.integral.utils.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by kris on 2017/1/3.
 * 美团网
 */
@Service
public class MeituanService implements IQueryIntegral {
    private final String mtLoginHomePage ="https://passport.meituan.com/account/unitivelogin?service=www&amp;continue=http%3A%2F%2Fwww.meituan.com%2Faccount%2Fsettoken%3Fcontinue%3Dhttp%253A%252F%252Fcd.meituan.com%252F";
    private final String mtLoginURL ="https://passport.meituan.com/account/unitivelogin?service=www&continue=http%3A%2F%2Fwww.meituan.com%2Faccount%2Fsettoken%3Fcontinue%3Dhttp%253A%252F%252Fwww.meituan.com";
    private final String refer = this.mtLoginHomePage;

    private static Map<String,Map<String,String>> theCookie = new HashMap<>();


    @Override
    public JfResult requestVerifyCode(JfRequest request) throws Exception {
        JfResult result = new JfResult();
        Map<String,String> mtCookie = new HashMap<>();
        String defaultLoginPage = openOringinalHomePage(mtCookie);
        String csrf = doAccessLoginPage(defaultLoginPage,mtCookie);
        if (csrf==null){
            result.setMessage("[美团]访问登录页异常，获取CSRF失败");
            return result;
        }

        String picLink = requestVerifyCode(mtCookie);
        result.setData(picLink);
        result.setMessage("[美团]已经获取到验证码");
        mtCookie.put("csrf",csrf);
        theCookie.put(request.getAccount(),mtCookie);
        return result;
    }

    @Override
    public JfResult queryIntegral(JfRequest request) throws Exception {
        JfResult result = new JfResult();
        Map<String,String> mtCookie = theCookie.get(request.getAccount());

        MtLoginData data = new MtLoginData();
        data.captcha=request.getCode();
        //data.captcha="";
        data.email=request.getAccount();
        data.password=request.getPassword();
        data.csrf = mtCookie.get("csrf");

        JSONObject loginBodyObj = doLoginMt(mtCookie,data);
        if (loginBodyObj == null){
            result.setMessage("[美团]登录验证失败");
            return result;
        }

        String setTokenParm = "token="+loginBodyObj.getString("token")
                +"&expire="+loginBodyObj.getIntValue("expire")
                +"&isdialog=0"
                +"&autologin="+loginBodyObj.getIntValue("autologin")
                +"&logintype=normal";

        String homePage = accountSetToken(mtCookie,loginBodyObj.getString("continue"),setTokenParm);
        if (homePage==null){
            result.setMessage("[美团]登录失败");
            return result;
        }

        String points = openHomePageAndGetPoints(homePage,mtCookie);
        if (points==null){
            result.setMessage("[美团]获取积分失败");
            return result;
        }

        result.setMessage("[美团]获取积分失败");
        result.setPoints(points);
        return result;
    }

    //访问登录页，返回csrf
    private String doAccessLoginPage(String url,Map<String,String> cookie) throws Exception{
        HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
        connection.setRequestProperty(Constants.HttpHeaders.USER_AGENT,Constants.DEFAULT_UA);
        connection.setRequestProperty(Constants.HttpHeaders.ACCEPT,"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        Common.updateCookie(connection,cookie);

        String html = Common.inputStreamToString(connection.getInputStream());
        Document doc = Jsoup.parse(html);
        Elements elements = doc.getElementsByClass("form-field form-field--ops");
        Element element = elements.get(0);
        List<Node> nodes = element.childNodes();
        for (Node node:nodes){
            if(node.attributes()==null)
                continue;
            if (node.attr("name").equals("csrf")){
                return node.attr("value");
            }
        }
        return null;
    }

    //获取验证码，备用
    private String requestVerifyCode(Map<String,String> cookie) throws Exception{
        final String url = "https://passport.meituan.com/account/captcha";
        HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
        connection.setRequestProperty(Constants.HttpHeaders.USER_AGENT,Constants.DEFAULT_UA);
        connection.setRequestProperty(Constants.HttpHeaders.ACCEPT,"image/webp,image/*,*/*;q=0.8");
        connection.setRequestProperty(Constants.HttpHeaders.COOKIE,Common.buildCookieString(cookie));

        String contentlen = connection.getHeaderField("Content-Length");
        int picSize = Integer.valueOf(contentlen);

        String contentType = connection.getHeaderField("Content-Type");
        int pos = contentType.indexOf('/');
        contentType = contentType.substring(pos+1);

        String picFolderPath;
        String os = System.getProperty("os.name");
        if (os.startsWith("Win")){//windows
            picFolderPath = "d:\\";
        }else{//linux
            picFolderPath = "/usr/local/etc/tomcat8/webapps/images/";
        }
        long t = System.currentTimeMillis();
        String picPath = picFolderPath+t+"."+contentType;

        FileOutputStream fos = new FileOutputStream(new File(picPath));
        int count = -1;
        byte[] buffer = new byte[512];
        InputStream is = connection.getInputStream();
        while((count=is.read(buffer))!=-1){
            fos.write(buffer,0,count);
        }
        is.close();
        fos.close();
        return picPath;
    }

    private JSONObject doLoginMt(Map<String,String> cookie,MtLoginData loginData) throws Exception{
        String param = loginData.toString();
        byte[] data = param.getBytes();
        String len = String.valueOf(data.length);
        HttpsURLConnection connection = (HttpsURLConnection) new URL(this.mtLoginURL).openConnection();
        connection.setRequestProperty(Constants.HttpHeaders.USER_AGENT,Constants.DEFAULT_UA);
        connection.setRequestProperty(Constants.HttpHeaders.COOKIE,Common.buildCookieString(cookie));
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty(Constants.HttpHeaders.CONTENT_TYPE,"application/x-www-form-urlencoded; charset=UTF-8");
        connection.setRequestProperty(Constants.HttpHeaders.CONTENT_LENGTH,len);
        connection.setRequestProperty(Constants.HttpHeaders.REFERER,this.refer);
        connection.setRequestProperty("X-Client","javascript");
        connection.setRequestProperty("X-CSRF-Token",cookie.get("csrf"));

        OutputStream os = connection.getOutputStream();
        os.write(data);
        os.flush();
        os.close();

        Common.updateCookie(connection,cookie);

        String body = Common.inputStreamToString(connection.getInputStream());
        if (body.startsWith("<!DOCTYPE")){
            return null;
        }
        JSONObject dataObj = JSONObject.parseObject(body);
        return dataObj.getJSONObject("data");
    }

    //这里不需要跳转，获取到首页数据 POST
    private String accountSetToken(Map<String,String> cookie,String url,String param) throws Exception{
        byte[] data = param.getBytes();
        String len = String.valueOf(data.length);

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty(Constants.HttpHeaders.USER_AGENT,Constants.DEFAULT_UA);
        connection.setRequestProperty(Constants.HttpHeaders.COOKIE,Common.buildCookieString(cookie));
        connection.setInstanceFollowRedirects(false);
        connection.setRequestProperty(Constants.HttpHeaders.CONTENT_TYPE,"application/x-www-form-urlencoded");
        connection.setRequestProperty(Constants.HttpHeaders.CONTENT_LENGTH,len);

        OutputStream os = connection.getOutputStream();
        os.write(data);
        os.flush();
        os.close();
        Common.updateCookie(connection,cookie);

        return connection.getHeaderField("Location");
    }

    private String openHomePageAndGetPoints(String url,Map<String,String> cookie) throws Exception{
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestProperty(Constants.HttpHeaders.USER_AGENT,Constants.DEFAULT_UA);
        connection.setRequestProperty(Constants.HttpHeaders.COOKIE,Common.buildCookieString(cookie));
        connection.setRequestProperty(Constants.HttpHeaders.ACCEPT,"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");

        Common.updateCookie(connection,cookie);

        String body;
        if("gzip".equals(connection.getHeaderField("Content-Encoding"))){
            body = new String(GZipUtils.decompress(GZipUtils.input2byte(connection.getInputStream())));
        }else{
            body = Common.inputStreamToString(connection.getInputStream());
        }
        Document html = Jsoup.parse(body);
        Elements elements=html.getElementsByClass("item item__point");
        String element = elements.first().toString();
        Pattern pattern = Pattern.compile("\\d{1,8}");
        Matcher matcher = pattern.matcher(element);
        if (matcher.find()){
            String jf = matcher.group(0);
            return jf;
        }
        return null;
    }

    private String openOringinalHomePage(Map<String,String> cookie) throws Exception{

    }

    public static void main(String[] args) throws Exception{
        MeituanService service = new MeituanService();
        JfResult result;
        JfRequest request = new JfRequest();
        //request.setAccount("18621793235");
        request.setAccount("15202823217");
        result = service.requestVerifyCode(request);
        System.out.println(result.getData());
        //request.setPassword("liu123");
        request.setPassword("lqh1985");
        System.out.println("input code:");
        Scanner scanner = new Scanner(System.in);
        String code = scanner.nextLine();
        request.setCode(code);
        request.setId(result.getId());
        result=service.queryIntegral(request);
        System.out.println(result.getPoints());
    }
}
