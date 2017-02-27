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
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
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
    private static Map<String,Map<String,String>> theCookie = new HashMap<>();
    private static Map<String,Map<String,String>> important = new HashMap<>();
    @Override
    public JfResult requestVerifyCode(JfRequest request) throws Exception {
        JfResult result = new JfResult();
        String id = Common.createUUID();
        result.setId(id);
        Map<String,String> cookie = new HashMap<>();
        openHomePage(cookie);
        postDefault(cookie);
        Map<String,String> hiddenElements = openLoginPage(cookie);
        important.put(id,hiddenElements);
        String fileurl = getCaptcha(cookie);
        result.setData(fileurl);
        theCookie.put(id,cookie);
        return result;
    }

    @Override
    public JfResult queryIntegral(JfRequest request) throws Exception {
        JfResult result = new JfResult();
        Map<String,String> cookie = theCookie.get(request.getId());
        Map<String,String> hiddenElements=important.get(request.getId());
        String token = startLogin(cookie,hiddenElements,request.getAccount(),request.getPassword(),request.getCode());
        if (token==null){
            result.setCode(Constants.ErrorCode.LOGIN_FAILED+"");
            result.setMessage("login failure");
            return result;
        }
        tokenAccess(token,cookie);
        openDefault(cookie);
        String jf = getItems(cookie);
        if (jf==null){
            result.setCode(Constants.ErrorCode.OTHER_EXCEPTION+"");
            result.setMessage("query integral failure");
            return result;
        }
        result.setPoints(jf);
        return result;
    }

    private String getCaptcha(Map<String,String> cooke) throws Exception{
        URL url = new URL("https://passport.meituan.com/account/captcha");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Host","passport.meituan.com");
        connection.setRequestProperty("Referer","https://passport.meituan.com/account/unitivelogin?service=www&continue=http%3A%2F%2Fwww.meituan.com%2Faccount%2Fsettoken%3Fcontinue%3Dhttp%253A%252F%252Fwww.meituan.com%252F&mtt=1.index%2Fchangecity.0.0.ixj0xjry");
        connection.setRequestProperty("Accept","image/webp,*/*;q=0.8");
        connection.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.107 Safari/537.36");
        connection.setRequestProperty("Accept-Encoding","gzip, deflate, sdch");
        connection.setRequestProperty("Accept-Language","zh-CN,zh;q=0.8");
        connection.setRequestProperty("Cookie",Common.buildCookieString(cooke));
        InputStream inputStream = connection.getInputStream();
        long time = System.currentTimeMillis();
        String localeFile = "d:\\"+time+".jpeg";
        //String localeFile="/usr/local/etc/tomcat8/webapps/images/"+time+".jpeg";
        String fileURL = "http://114.55.133.156:8080/images/"+time+".jpeg";
        File jpeg = new File(localeFile);
        FileOutputStream fos = new FileOutputStream(jpeg);
        int count = -1;
        byte[] buffer = new byte[1024];
        while((count=inputStream.read(buffer))!=-1){
            fos.write(buffer,0,count);
        }
        fos.close();
        inputStream.close();
        return fileURL;
    }
    private void openHomePage(Map<String,String> cookie) throws Exception{
        URL url = new URL("http://www.meituan.com/");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Host","www.meituan.com");
        connection.setRequestProperty("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        connection.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.107 Safari/537.36");
        connection.setRequestProperty("Accept-Encoding","gzip, deflate, sdch");
        connection.setRequestProperty("Accept-Language","zh-CN,zh;q=0.8");
        connection.setInstanceFollowRedirects(false);
        Common.updateCookie(connection,cookie);
    }

    private void postDefault(Map<String,String> cookie) throws Exception{
        String param = "0=%7B%22_a%22%3A%22%2Fcombo%2Fuserinfo%22%7D&1=%7B%22_a%22%3A%22%2Findex%2Fmessage%22%7D&2=%7B%22_a%22%3A%22%2Findex%2Frvd%22%7D&3=%7B%22_a%22%3A%22%2Findex%2Fnavcart%22%7D&4=%7B%22ref%22%3A%22%2Findex%2Fchangecity%22%2C%22_a%22%3A%22%2Findex%2Fvipbubble%22%7D&5=%7B%22isshowshops%22%3Atrue%2C%22isshopspage%22%3Afalse%2C%22_a%22%3A%22%2Findex%2Fhotqueries%22%7D";
        URL url = new URL("http://www.meituan.com/multiact/default//");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.107 Safari/537.36");
        connection.setRequestProperty("Accept-Encoding","gzip, deflate");
        connection.setRequestProperty("Accept-Language","zh-CN,zh;q=0.8");
        connection.setRequestProperty("Accept","*/*");
        connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");
        connection.setRequestProperty("Content-Length",param.getBytes().length+"");
        connection.setRequestProperty("Cookie",Common.buildCookieString(cookie));
        connection.setRequestProperty("Referer","http://www.meituan.com/");
        connection.setRequestProperty("Host","www.meituan.com");
        connection.setRequestProperty("X-Requested-With","XMLHttpRequest");

        OutputStream os = connection.getOutputStream();
        os.write(param.getBytes());
        os.flush();
        os.close();
        Common.updateCookie(connection,cookie);
    }

    private Map<String,String> openLoginPage(Map<String,String> cookie) throws Exception{
        URL url = new URL("https://passport.meituan.com/account/unitivelogin?service=www&continue=http%3A%2F%2Fwww.meituan.com%2Faccount%2Fsettoken%3Fcontinue%3Dhttp%253A%252F%252Fwww.meituan.com%252F&mtt=1.index%2Fchangecity.0.0.iximme15");
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.107 Safari/537.36");
        connection.setRequestProperty("Accept-Encoding","gzip, deflate, sdch");
        connection.setRequestProperty("Accept-Language","zh-CN,zh;q=0.8");
        connection.setRequestProperty("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        connection.setRequestProperty("Referer","http://www.meituan.com/");
        connection.setRequestProperty("Cookie",Common.buildCookieString(cookie));
        connection.setRequestProperty("Host","passport.meituan.com");
        Common.updateCookie(connection,cookie);
        Map<String,String> data = new HashMap<>();
        String page = new String(GZipUtils.decompress(GZipUtils.input2byte(connection.getInputStream())));
        Document doc = Jsoup.parse(page);
        Elements elements = doc.getElementsByClass("form-field form-field--ops");
        Element element = elements.get(0);
        List<Node> nodes = element.childNodes();
        for (Node node:nodes){
            if(node.attributes()==null)
                continue;
            if (node.attr("name").equals("origin")){
                data.put("origin",node.attr("value"));
            }
            if (node.attr("name").equals("fingerprint")){
                data.put("fingerprint",node.attr("value"));
            }
            if (node.attr("name").equals("csrf")){
                data.put("csrf",node.attr("value"));
            }
        }
        return data;
    }


    private String startLogin(Map<String,String> cookie,Map<String,String> hd,String user,String pwd,String code) throws Exception{
        URL url = new URL("https://passport.meituan.com/account/unitivelogin?service=www&continue=http%3A%2F%2Fwww.meituan.com%2Faccount%2Fsettoken%3Fcontinue%3Dhttp%253A%252F%252Fcd.meituan.com%252F");
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.107 Safari/537.36");
        connection.setRequestProperty("Accept-Encoding","gzip, deflate, sdch");
        connection.setRequestProperty("Accept-Language","zh-CN,zh;q=0.8");
        connection.setRequestProperty("Accept","*/*");
        connection.setRequestProperty("Referer","https://passport.meituan.com/account/unitivelogin?service=www&continue=http%3A%2F%2Fwww.meituan.com%2Faccount%2Fsettoken%3Fcontinue%3Dhttp%253A%252F%252Fwww.meituan.com%252F&mtt=1.index%2Fchangecity.0.0.ixj0xjry");
        connection.setRequestProperty("Cookie",Common.buildCookieString(cookie));
        connection.setRequestProperty("X-CSRF-Token",hd.get("csrf"));
        connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");
        connection.setRequestProperty("Host","passport.meituan.com");
        connection.setRequestProperty("X-Requested-With","XMLHttpRequest");
        connection.setRequestProperty("X-Client","javascript");
        String param = "password="+pwd+"&origin="+hd.get("origin")+"&fingerprint=0-1-1-&email="+user+"&csrf="+hd.get("csrf")+"&captcha="+code;
        byte[] buffer = param.getBytes();
        int length = buffer.length;
        connection.setRequestProperty("Content-Length",length+"");

        OutputStream os = connection.getOutputStream();
        os.write(buffer);
        os.flush();
        os.close();

        Common.updateCookie(connection,cookie);
        String html = Common.inputStreamToString(connection.getInputStream());
        if (html==null){
            return null;
        }

        JSONObject object = JSONObject.parseObject(html);
        JSONObject dataObj = object.getJSONObject("data");
        return dataObj.getString("token");
    }

    private void tokenAccess(String token,Map<String,String> cookie)throws Exception{
        URL url = new URL("http://www.meituan.com/account/settoken?continue=http%3A%2F%2Fcd.meituan.com%2F");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.107 Safari/537.36");
        connection.setRequestProperty("Accept-Encoding","gzip, deflate, sdch");
        connection.setRequestProperty("Accept-Language","zh-CN,zh;q=0.8");
        connection.setRequestProperty("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        connection.setRequestProperty("Cookie",Common.buildCookieString(cookie));
        connection.setRequestProperty("Host","www.meituan.com");
        connection.setInstanceFollowRedirects(false);
        connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");

        String param = "token="+token+"&logintype=normal&isdialog=0&expire=0&autologin=0";
        byte[] buffer = param.getBytes();
        int length = buffer.length;
        connection.setRequestProperty("Content-Length",length+"");

        OutputStream os = connection.getOutputStream();
        os.write(buffer);
        os.flush();
        os.close();

        Common.updateCookie(connection,cookie);

    }
    private void openDefault(Map<String,String> cookie) throws Exception{
        String param = "0=%7B%22_a%22%3A%22%2Fcombo%2Fuserinfo%22%7D&1=%7B%22_a%22%3A%22%2Findex%2Fmessage%22%7D&2=%7B%22_a%22%3A%22%2Findex%2Frvd%22%7D&3=%7B%22_a%22%3A%22%2Findex%2Fnavcart%22%7D&4=%7B%22ref%22%3A%22%2Findex%2FfloornewV2%22%2C%22_a%22%3A%22%2Findex%2Fvipbubble%22%7D&5=%7B%22_a%22%3A%22%2Fcombo%2Funfoldbanner%22%2C%22_p%22%3A%22index%2FfloornewV2%22%7D&6=%7B%22isshowshops%22%3Atrue%2C%22isshopspage%22%3Afalse%2C%22_a%22%3A%22%2Findex%2Fhotqueries%22%7D&7=%7B%22sysmsg%22%3Atrue%2C%22commontips%22%3Atrue%2C%22page%22%3A%22index%22%2C%22_a%22%3A%22%2Findex%2Ftips%22%2C%22_p%22%3A%22index%2FfloornewV2%22%7D&8=%7B%22_a%22%3A%22%2Findex%2Fhotsandrec%22%7D";
        URL url = new URL("http://cd.meituan.com/multiact/default//");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.107 Safari/537.36");
        connection.setRequestProperty("Accept-Encoding","gzip, deflate, sdch");
        connection.setRequestProperty("Accept-Language","zh-CN,zh;q=0.8");
        connection.setRequestProperty("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        connection.setRequestProperty("Referer","http://cd.meituan.com/");
        connection.setRequestProperty("Cookie",Common.buildCookieString(cookie));
        connection.setRequestProperty("Host","cd.meituan.com");
        connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");
        byte[] buffer = param.getBytes();
        int length = buffer.length;
        connection.setRequestProperty("Content-Length",length+"");
        OutputStream os = connection.getOutputStream();
        os.write(buffer);
        os.flush();
        os.close();
        Common.updateCookie(connection,cookie);

    }

    private String getItems(Map<String,String> cookie)throws Exception{
        URL url = new URL("http://www.meituan.com/account/growth?mtt=1.index%2Ffloornew.0.0.ixixt497");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.107 Safari/537.36");
        connection.setRequestProperty("Accept-Encoding","gzip, deflate, sdch");
        connection.setRequestProperty("Accept-Language","zh-CN,zh;q=0.8");
        connection.setRequestProperty("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        connection.setRequestProperty("Cookie",Common.buildCookieString(cookie));
        connection.setRequestProperty("Referer","http://cd.meituan.com/");
        connection.setRequestProperty("Host","www.meituan.com");
        connection.setRequestProperty("Cookie",Common.buildCookieString(cookie));

        String page = new String(GZipUtils.decompress(GZipUtils.input2byte(connection.getInputStream())));
        Document html = Jsoup.parse(page);
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
//    public static void main(String[] args) throws Exception{
//        MeituanService service = new MeituanService();
//        JfResult result;
//        result = service.requestVerifyCode(null);
//        System.out.println(result.getId());
//        JfRequest request = new JfRequest();
//        request.setAccount("18621793235");
//        request.setPassword("liu123");
//        System.out.println("input code:");
//        Scanner scanner = new Scanner(System.in);
//        String code = scanner.nextLine();
//        request.setCode(code);
//        request.setId(result.getId());
//        result=service.queryIntegral(request);
//        System.out.println(result.getPoints());
//    }
}
