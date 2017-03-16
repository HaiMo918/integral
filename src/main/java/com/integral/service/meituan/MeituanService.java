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
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;
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
    private static Map<String, Map<String, String>> theCookie = new HashMap<>();
    private static Map<String, String> mLoginURLs = new HashMap<>();
    private MyX509TrustManager xtm = new MyX509TrustManager();
    private MyHostNameVerify mhv = new MyHostNameVerify();
    private SSLContext sslContext = null;


    MeituanService() throws Exception{
        sslContext = SSLContext.getInstance("SSL");
        X509TrustManager[] xtmArray = new X509TrustManager[]{xtm};
        sslContext.init(null, xtmArray, new java.security.SecureRandom());
    }

    @Override
    public JfResult requestVerifyCode(JfRequest request) throws Exception {
        JfResult result = new JfResult();
        Map<String, String> mtCookie = new HashMap<>();
        //打开首页，并获取到登录页面
//        String defaultLoginPage = openOringinalHomePage(mtCookie);
//        this.mLoginUrl=defaultLoginPage;
        String mtt = Long.toString(System.currentTimeMillis(), 36);
//        String loginUrl = defaultLoginPage + "&mtt=1.index%2Fchangecity.0.0." + mtt;
//        mLoginURLs.put(request.getAccount(), loginUrl);
        //打开登录页面
        String defaultLoginPage = MtLoginData.MT_LOGIN_URL+"&mtt=1.index/changecity.0.0."+mtt;
        mLoginURLs.put(request.getAccount(),defaultLoginPage);
        String csrf = doAccessLoginPage(defaultLoginPage, mtCookie);
        if (csrf == null) {
            result.setMessage("[美团]访问登录页异常，获取CSRF失败");
            return result;
        }

        //获取验证码
        String picLink = requestVerifyCode(mtCookie);
        result.setData(picLink);
        result.setMessage("[美团]已经获取到验证码");
        mtCookie.put("csrf", csrf);
        theCookie.put(request.getAccount(), mtCookie);
        return result;
    }

    @Override
    public JfResult queryIntegral(JfRequest request) throws Exception {
        JfResult result = new JfResult();
        Map<String, String> mtCookie = theCookie.get(request.getAccount());

        MtLoginData data = new MtLoginData();
        data.captcha = request.getCode();
        //data.captcha="";
        data.email = request.getAccount();
        data.password = request.getPassword();
        data.csrf = mtCookie.get("csrf");

        //开始登录
        JSONObject loginBodyObj = doLoginMt(mtCookie, data);
        if (loginBodyObj == null) {
            result.setMessage("[美团]登录验证失败");
            return result;
        }

        String setTokenParm = "token=" + loginBodyObj.getString("token")
                + "&expire=" + loginBodyObj.getIntValue("expire")
                + "&isdialog=0"
                + "&autologin=" + loginBodyObj.getIntValue("autologin")
                + "&logintype=normal";

        String homePage = accountSetToken(mtCookie, loginBodyObj.getString("continue"), setTokenParm);
        if (homePage == null) {
            result.setMessage("[美团]登录失败");
            return result;
        }
        homePage = "http://www.meituan.com/account/growth?mtt=1.index%2Ffloornew.0.0."+Long.toString(System.currentTimeMillis(),36);
        String points = openHomePageAndGetPoints(homePage, mtCookie);
        if (points == null) {
            result.setMessage("[美团]获取积分失败");
            return result;
        }

        result.setMessage("[美团]获取积分失败");
        result.setPoints(points);
        return result;
    }

    //访问登录页，返回csrf
    private String doAccessLoginPage(String url, Map<String, String> cookie) throws Exception {
        HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
        connection.setSSLSocketFactory(sslContext.getSocketFactory());
        connection.setHostnameVerifier(mhv);
        connection.setRequestProperty(Constants.HttpHeaders.USER_AGENT, Constants.BK_UA);
        connection.setRequestProperty(Constants.HttpHeaders.ACCEPT, "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        connection.setRequestProperty(Constants.HttpHeaders.ACCEPT_ENCODING,"gzip, deflate, sdch, br");
        connection.setRequestProperty(Constants.HttpHeaders.ACCEPT_LANGUAGE,"zh-CN,zh;q=0.8");
        //connection.setRequestProperty(Constants.HttpHeaders.COOKIE, Common.buildCookieString(cookie));
        Common.updateCookie(connection, cookie);

        String html;
        if ("gzip".equals(connection.getHeaderField("Content-Encoding"))){
            html = new String(GZipUtils.decompress(GZipUtils.input2byte(connection.getInputStream())));
        }else{
            html = Common.inputStreamToString(connection.getInputStream());
        }
        Document doc = Jsoup.parse(html);
        Elements elements = doc.getElementsByClass("form-field form-field--ops");
        Element element = elements.get(0);
        List<Node> nodes = element.childNodes();
        for (Node node : nodes) {
            if (node.attributes() == null)
                continue;
            if (node.attr("name").equals("csrf")) {
                return node.attr("value");
            }
        }
        return null;
    }

    //获取验证码，备用
    private String requestVerifyCode(Map<String, String> cookie) throws Exception {
        final String url = "https://passport.meituan.com/account/captcha";
        HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
        connection.setRequestProperty(Constants.HttpHeaders.USER_AGENT, Constants.BK_UA);
        connection.setRequestProperty(Constants.HttpHeaders.ACCEPT, "image/webp,image/*,*/*;q=0.8");
        connection.setRequestProperty(Constants.HttpHeaders.COOKIE, Common.buildCookieString(cookie));

        String contentlen = connection.getHeaderField("Content-Length");
        int picSize = Integer.valueOf(contentlen);

        String contentType = connection.getHeaderField("Content-Type");
        int pos = contentType.indexOf('/');
        contentType = contentType.substring(pos + 1);

        String picFolderPath;
        String os = System.getProperty("os.name");
        if (os.startsWith("Win")) {//windows
            picFolderPath = "d:\\";
        } else {//linux
            picFolderPath = "/usr/local/etc/tomcat8/webapps/images/";
        }
        long t = System.currentTimeMillis();
        String picPath = picFolderPath + t + "." + contentType;

        FileOutputStream fos = new FileOutputStream(new File(picPath));
        int count = -1;
        byte[] buffer = new byte[512];
        InputStream is = connection.getInputStream();
        while ((count = is.read(buffer)) != -1) {
            fos.write(buffer, 0, count);
        }
        is.close();
        fos.close();
        return picPath;
    }

    private JSONObject doLoginMt(Map<String, String> cookie, MtLoginData loginData) throws Exception {
        JSONObject dataObj = new JSONObject();
        String param = loginData.toString();
        byte[] data = param.getBytes();
        String len = String.valueOf(data.length);
        HttpsURLConnection connection = (HttpsURLConnection) new URL(MtLoginData.MT_LOGIN_URL).openConnection();
        connection.setHostnameVerifier(mhv);
        connection.setSSLSocketFactory(sslContext.getSocketFactory());
        connection.setRequestProperty(Constants.HttpHeaders.USER_AGENT, Constants.BK_UA);
        connection.setRequestProperty(Constants.HttpHeaders.COOKIE, Common.buildCookieString(cookie));
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty(Constants.HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded; charset=UTF-8");
        connection.setRequestProperty(Constants.HttpHeaders.CONTENT_LENGTH, len);
        connection.setRequestProperty(Constants.HttpHeaders.REFERER,this.mLoginURLs.get(loginData.email));
        connection.setRequestProperty("X-Client", "javascript");
        connection.setRequestProperty("X-CSRF-Token", cookie.get("csrf"));

        OutputStream os = connection.getOutputStream();
        os.write(data);
        os.flush();
        os.close();

        Common.updateCookie(connection, cookie);

        String body = Common.inputStreamToString(connection.getInputStream());
        if (body.startsWith("<!DOCTYPE")) {
            Document document = Jsoup.parse(body);
            Element e = document.getElementsByClass("J-token").get(0);
            String value = e.val();
            dataObj.put("token",value);

            e = document.getElementsByClass("J-expire").get(0);
            value = e.val();
            dataObj.put("expire",value);

            e = document.getElementsByClass("J-isdialog").get(0);
            value = e.val();
            dataObj.put("isdialog",value);

            e = document.getElementsByClass("J-autologin").get(0);
            value = e.val();
            dataObj.put("autologin",value);

            //J-form mainbox__content
            e = document.getElementsByClass("J-form mainbox__content").get(0);
            value = e.attr("action");
            dataObj.put("continue",value);
            return dataObj;
        }
        dataObj = JSONObject.parseObject(body);
        return dataObj.getJSONObject("data");
    }

    //这里不需要跳转，获取到首页数据 POST
    private String accountSetToken(Map<String, String> cookie, String url, String param) throws Exception {
        byte[] data = param.getBytes();
        String len = String.valueOf(data.length);

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty(Constants.HttpHeaders.USER_AGENT, Constants.BK_UA);
        connection.setRequestProperty(Constants.HttpHeaders.COOKIE, Common.buildCookieString(cookie));
        connection.setInstanceFollowRedirects(false);
        connection.setRequestProperty(Constants.HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");
        connection.setRequestProperty(Constants.HttpHeaders.CONTENT_LENGTH, len);

        OutputStream os = connection.getOutputStream();
        os.write(data);
        os.flush();
        os.close();
        Common.updateCookie(connection, cookie);

        return connection.getHeaderField("Location");
    }

    private String openHomePageAndGetPoints(String url, Map<String, String> cookie) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestProperty(Constants.HttpHeaders.USER_AGENT, Constants.BK_UA);
        connection.setRequestProperty(Constants.HttpHeaders.COOKIE, Common.buildCookieString(cookie));
        connection.setRequestProperty(Constants.HttpHeaders.ACCEPT, "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");

        Common.updateCookie(connection, cookie);

        String body;
        if ("gzip".equals(connection.getHeaderField("Content-Encoding"))) {
            body = new String(GZipUtils.decompress(GZipUtils.input2byte(connection.getInputStream())));
        } else {
            body = Common.inputStreamToString(connection.getInputStream());
        }
        Document html = Jsoup.parse(body);
        Elements elements = html.getElementsByClass("item item__point");
        String element = elements.first().toString();
        Pattern pattern = Pattern.compile("\\d{1,8}");
        Matcher matcher = pattern.matcher(element);
        if (matcher.find()) {
            String jf = matcher.group(0);
            return jf;
        }
        return null;
    }
//
//    private String openOringinalHomePage(Map<String, String> cookie) throws Exception {
//        final String url = "http://www.meituan.com/";
//        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
//        connection.setRequestProperty(Constants.HttpHeaders.ACCEPT, "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
//        Common.updateCookie(connection, cookie);
//
//        String body = Common.inputStreamToString(connection.getInputStream());
//        Document document = Jsoup.parse(body);
//        Element e = document.getElementById("J-login");
//        String href = e.attr("href");
//        return href;
//    }

    public static void main(String[] args) throws Exception {
        MeituanService service = new MeituanService();
        JfResult result;
        JfRequest request = new JfRequest();
        request.setAccount("18621793235");
        //request.setAccount("15202823217");
        result = service.requestVerifyCode(request);
        System.out.println(result.getData());
        request.setPassword("liu123");
        //request.setPassword("lqh1985");
        System.out.println("input code:");
        Scanner scanner = new Scanner(System.in);
        String code = scanner.nextLine();
        request.setCode(code);
        request.setId(result.getId());
        result = service.queryIntegral(request);
        System.out.println(result.getPoints());
    }
}
