package com.integral.service.creditcard.jiaotongyinhang;

import com.integral.tools.Base64Util;
import com.integral.utils.Constants;
import com.integral.utils.IQueryIntegral;
import com.integral.utils.JfRequest;
import com.integral.utils.JfResult;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by liuqinghai on 2016/11/18.
 */
@Service
public class JiaohangService implements IQueryIntegral {
    private Map<String,String> mCookies;
    private String mLT;
    public JiaohangService(){
        mCookies=new HashMap<String,String>();
    }
    @Override
    public JfResult requestVerifyCode(JfRequest request) throws Exception {
        return null;
    }

    @Override
    public JfResult queryIntegral(JfRequest request) throws Exception {
        JfResult result = new JfResult();

        String redirectUrl = openHomePage();
        if (redirectUrl==null){
            result.setMessage("打开交行积分首页失败");
            return result;
        }

        openLoginPage(redirectUrl,request.getAccount());
        String page = openAuthHtml(request.getAccount(),request.getPassword());
        if (page==null){
            result.setMessage("登录认证失败");
            return result;
        }
        page = openSecurityCheckPage(page);
        if (page==null){
            result.setMessage("登录认证失败");
            return result;
        }
        openCustomerPage(page);
        openIndexPage(JhConstants.INDEX_HTML);
        openIndexHtm();
        result = queryJf();
        return result;
    }

    private JfResult queryJf() throws Exception{
        JfResult result = new JfResult();
        URL url = new URL(JhConstants.MEMBER_CCARD_LIST);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestProperty("User-Agent", Constants.DEFAULT_UA);
        connection.setRequestProperty("Cookie",map2String(mCookies));
        InputStream is = connection.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String line;
        StringBuffer sb = new StringBuffer();
        while ((line=br.readLine())!=null){
            sb.append(line);
        }
        br.close();isr.close();is.close();
        String pageContent =sb.toString();

        List<Integer> jfs = new ArrayList<Integer>();
        Document document = Jsoup.parse(pageContent);
        Elements tables = document.select("table");
        Elements theTable = tables.get(2).select("tr");
        Elements tds = theTable.get(0).select("td");
        String text = tds.get(3).text();
        if (text!=null && !"".equals(text)) {
            result.setPoints(String.valueOf(text));
            result.setMessage("交行获取积分成功");
        }
        return result;
    }

    private void openIndexHtm() throws Exception{
        URL url = new URL(JhConstants.INDEX_HTM);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("User-Agent", Constants.DEFAULT_UA);
        connection.setRequestProperty("Cookie",map2String(mCookies));
    }

    private void openIndexPage(String page) throws Exception{
        URL url = new URL(page);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("User-Agent", Constants.DEFAULT_UA);
        connection.setRequestProperty("Cookie",map2String(mCookies));
        updateCookie(connection);
    }

    private String openCustomerPage(String page) throws Exception{
        URL url = new URL(page);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setInstanceFollowRedirects(false);
        connection.setRequestProperty("User-Agent", Constants.DEFAULT_UA);
        connection.setRequestProperty("Cookie",map2String(mCookies));
        updateCookie(connection);
        return connection.getHeaderField("Location");
    }

    private String openSecurityCheckPage(String page) throws Exception{
        URL url = new URL(page);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setInstanceFollowRedirects(false);
        connection.setRequestProperty("User-Agent", Constants.DEFAULT_UA);
        connection.setRequestProperty("Cookie",map2String(mCookies));
        updateCookie(connection);
        return connection.getHeaderField("Location");
    }

    private String openAuthHtml(String user,String password) throws Exception{
        String param = buildAuthParam(user,password);
        int length = param.getBytes().length;
        URL url = new URL(JhConstants.AUTH_PAGE);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setInstanceFollowRedirects(false);
        connection.setRequestProperty("User-Agent", Constants.DEFAULT_UA);
        connection.setRequestProperty("Cookie",map2String(mCookies));
        connection.setRequestProperty("Content-Length",String.valueOf(length));
        connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");

        OutputStream os = connection.getOutputStream();
        os.write(param.getBytes());
        os.close();

        updateCookie(connection);
        return connection.getHeaderField("Location");
    }

    private void openLoginPage(String page,String phone) throws Exception{
        URL url=new URL(page);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestProperty("User-Agent", Constants.DEFAULT_UA);
        updateCookie(connection);
        mCookies.put("username", Base64Util.encode(phone));
        mCookies.put("latelyLoginType","nocard");
        Map headers = connection.getHeaderFields();
        Set<String> keys = headers.keySet();
        int contentLength = 0;
        if (keys.contains("Content-Length")){
            contentLength = Integer.parseInt(connection.getHeaderField("Content-Length"));
            InputStream is = connection.getInputStream();
            byte[] content = new byte[contentLength];
            is.read(content);
            String data = new String(content).trim();
            Pattern pattern = Pattern.compile("LT-\\d{7}-\\w*");
            Matcher matcher = pattern.matcher(data);
            if (matcher.find()){
                mLT = matcher.group();
            }
        }else{
            String line;
            StringBuffer sb = new StringBuffer();
            InputStream is = connection.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            while ((line=br.readLine())!=null){
                sb.append(line);
            }
            br.close();
            isr.close();
            is.close();

            String pageData = sb.toString().trim();
            Pattern pattern = Pattern.compile("LT-\\d{7}-\\w*");
            Matcher matcher = pattern.matcher(pageData);
            if (matcher.find()){
                mLT = matcher.group();
            }
        }
    }

    private String openHomePage() throws Exception{
        URL url = new URL(JhConstants.HOME_PAGE);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setInstanceFollowRedirects(false);
        connection.setRequestProperty("User-Agent", Constants.DEFAULT_UA);
        updateCookie(connection);
        return connection.getHeaderField("Location");
    }
    private void updateCookie(HttpURLConnection connection) {
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
                        mCookies.put(key, keyvalue);
                    }
                }
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


    private String buildAuthParam(String user,String password){
        StringBuilder sb = new StringBuilder();
        sb.append("accept=1")
                .append("&conflictmobile=")
                .append("&lt=").append(mLT)
                .append("&mobileOrEmail=").append(user)
                .append("&password=").append(password)
                .append("&username=").append(user)
                .append("&usernametype=PHONE");
        return sb.toString();
    }


    public static void main(String[] args) throws Exception{
        JiaohangService jiaohang = new JiaohangService();
        JfRequest request = new JfRequest();
        request.setPassword("htk123456");
        request.setAccount("13641690661");
        JfResult result = jiaohang.queryIntegral(request);
        System.out.println(result.getPoints());
    }
}
