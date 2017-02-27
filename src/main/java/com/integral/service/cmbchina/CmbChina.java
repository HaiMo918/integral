package com.integral.service.cmbchina;//Created by xiacheng on 16/10/8.

import com.integral.service.airline.eastairline.EastAirline;
import com.integral.utils.*;
import org.springframework.stereotype.Service;

import javax.net.ssl.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

@Service
public class CmbChina implements IQueryIntegral {
    private MyX509TrustManager xtm = new MyX509TrustManager();
    private MyHostNameVerify mhv = new MyHostNameVerify();
    private SSLContext sslContext = null;
    public static Boolean DEBUG = false;
    public static Map<String, Map<String, String>> sessions = new HashMap<String, Map<String, String>>();
    public static Map<String, LoginParam> loginParamMap = new HashMap<String, LoginParam>();

    public CmbChina(){
        try {
            sslContext = SSLContext.getInstance("TLS");
            X509TrustManager[] xtmArray = new X509TrustManager[]{xtm};
            sslContext.init(null, xtmArray, new java.security.SecureRandom());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    class MyX509TrustManager implements X509TrustManager {

        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }

    class MyHostNameVerify implements HostnameVerifier {
        @Override
        public boolean verify(String s, SSLSession sslSession) {
            return true;
        }
    }
    @Override
    public JfResult requestVerifyCode(JfRequest request) throws Exception {
        JfResult jfResult = new JfResult();
        CmbResult cmbResult = getVerifyCode();
        jfResult.setCode(cmbResult.code);
        jfResult.setId(cmbResult.id);
        jfResult.setData(cmbResult.picurl);
        jfResult.setMessage(cmbResult.message);
        return jfResult;
    }

    @Override
    public JfResult queryIntegral(JfRequest request) throws Exception {
        JfResult jfResult = new JfResult();
        CmbReqData data = new CmbReqData();
        data.account = request.getAccount();
        data.password = request.getPassword();
        data.code = request.getCode();
        data.id = request.getId();
        CmbResult cmbResult = getCmbChinaIntegral(data);
        jfResult.setMessage(cmbResult.message);
        jfResult.setCode(cmbResult.code);
        jfResult.setPoints(cmbResult.points);
        return jfResult;
    }

    private synchronized CmbResult getVerifyCode() throws Exception {
        String sid = Common.createUUID();
        CmbResult result = new CmbResult();
        Map<String, String> cookies = new HashMap<String, String>();
        String redirectUrl= openHttpDefaultAspxPage(cookies);
        String eWinLoginPage = openLoginAspxPage(redirectUrl,cookies);
        if (eWinLoginPage == null) {
            result.message = "打开登陆首页失败";
            return result;
        }

        HashMap<String, String> elem = openEWinLoginPage(eWinLoginPage, cookies);
        String gtmpID=getTempWtid();
        String picurl = openShowPage(elem,cookies);
        long time = System.currentTimeMillis();
        String localPath;
        if (DEBUG) {
            localPath = time + ".gif";
        } else {
            localPath = "/usr/local/etc/tomcat8/webapps/images/" + time + ".gif";
        }
        downloadPicture(picurl, localPath, false);
        result.picurl = "http://114.55.133.156:8080/images/" + time + ".gif";
        result.message = "获取验证码成功";
        result.id = sid;

        openDcsGif(gtmpID,cookies);
        cookies.put("WTFPC",createWTFPC_WT_COF(gtmpID));
        sessions.put(sid, cookies);
        backupLoginParam(sid, picurl, elem);
        return result;
    }

    private synchronized CmbResult getCmbChinaIntegral(CmbReqData data) throws Exception {
        CmbResult result = new CmbResult();
        Map<String, String> thisCookie = sessions.get(data.id);

        LoginParam loginParam = loginParamMap.get(data.id);
        String param = buildLoginParam(loginParam, data);
        Map<String,String> meta= doLogin(CmbConstants.REAL_LOGIN_URL, param, thisCookie);

        loginParam.setToken(meta.get("token"));
        loginParam.setMerchantData(meta.get("sslsite"));
        String msg=openEWinCallback(loginParam.getToken(), loginParam.getMerchantData(), thisCookie);
        if (msg==null){
            sessions.remove(data.id);
            result.points="-1";
            result.message="账号错误";
            return result;
        }
        System.out.println(msg);
        msg=openDefaultAspxPage(thisCookie);
        System.out.println(msg);
        msg=openPersonalInfoPage(thisCookie);
        System.out.println(msg);
        msg=openImageValidatorPage(thisCookie);
        System.out.println(msg);
        msg=openMyCreditCardManager(thisCookie);
        System.out.println(msg);
        result.points = ajaxPointQuery(thisCookie);
        sessions.remove(data.id);
        return result;
    }

    private String openHttpDefaultAspxPage(Map<String, String> cookies) throws Exception{
        URL url = new URL(CmbConstants.HTTP_DEFAUL_APX);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("User-Agent",CmbConstants.DEFAULT_UA);
        connection.setRequestProperty("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        connection.setRequestProperty("Accept-Encoding","gzip, deflate, sdch");
        connection.setRequestProperty("Accept-Language","zh-CN,zh;q=0.8");
        connection.setRequestProperty("Host","jf.cmbchina.com");
        connection.setRequestProperty("Upgrade-Insecure-Requests","1");
        connection.setInstanceFollowRedirects(false);
        if (connection.getHeaderFields().containsKey("Location")){
            updateCmbChinaCookie(connection,cookies);
            return connection.getHeaderField("Location");
        }
        return null;
    }

    private String openDcsGif(String tmpID,Map<String, String> cookies) throws Exception{
        String param = buildSdcParam(tmpID);
        String DCS_GIF="https://sdc.cmbchina.com/dcs5w0txb10000wocrvqy1nqm_6n1p/dcs.gif?"+param;
        URL url = new URL(DCS_GIF);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setInstanceFollowRedirects(false);
        connection.setSSLSocketFactory(sslContext.getSocketFactory());
        connection.setRequestProperty("User-Agent",CmbConstants.DEFAULT_UA);
        connection.setRequestProperty("Accept","*/*");
        connection.setRequestProperty("Accept-Language","zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
        connection.setRequestProperty("Accept-Encoding","gzip, deflate, br");
        connection.setRequestProperty("Referer","https://ssl.jf.cmbchina.com/Customer/Login.aspx");
        updateCmbChinaCookie(connection,cookies);

        String location = connection.getHeaderField("Location");
        if (location!=null) {
            url = new URL("https://sdc.cmbchina.com"+location);
            connection = (HttpsURLConnection) url.openConnection();
        }
        return connection.getResponseMessage();
    }

    private String buildSdcParam(String tempWtid) {
        StringBuffer sb = new StringBuffer();
        sb.append("&dcsdat=").append(System.currentTimeMillis())
                .append("&dcssip=ssl.jf.cmbchina.com")
                .append("&dcsuri=/Customer/Login.aspx")
                .append("&WT.co_f=").append(tempWtid)
                .append("&WT.vt_sid=").append(tempWtid).append("."+System.currentTimeMillis())
                .append("&WT.vt_f_tlv=0").append("&WT.tz=8").append("&WT.bh=10").append("&WT.ul=zh-CN").append("&WT.cd=24")
                .append("&WT.sr=1920x1080").append("&WT.jo=No")
                .append("%25E7%2594%25A8%25E6%2588%25B7%25E7%2599%25BB%25E5%25BD%2595%2520-%2520%25E6%258B%259B%25E5%2595%2586%25E9%2593%25B6%25E8%25A1%258C%25E4%25BF%25A1%25E7%2594%25A8%25E5%258D%25A1%25E9%25A2%2586%25E5%2585%2588%25E7%25A7%25AF%25E5%2588%2586%25E8%25AE%25A1%25E5%2588%2592")
                .append("&WT.js=Yes").append("&WT.bs=1920x950").append("&WT.fi=Yes").append("&WT.fv=23.0").append("&WT.em=uri")
                .append("&WT.le=GBK").append("&WT.tv=8.0.2").append("&WT.mle=gb2312").append("&WT.vt_f_tlh=0")
                .append("&WT.vt_f_d=1").append("&WT.vt_f_s=1").append("&WT.vt_f_a=1").append("&WT.vt_f=1");
        return sb.toString();
    }

    private String ajaxPointQuery(Map<String, String> cookie) throws Exception {
        byte[] osData = "".getBytes();
        URL url = new URL(CmbConstants.AJAX_POINT_QUERY);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setSSLSocketFactory(sslContext.getSocketFactory());
        connection.setRequestProperty("User-Agent", CmbConstants.DEFAULT_UA);
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        connection.setRequestProperty("Referer", "https://trip.cmbchina.com/SSO/Common/Login/Show.html");
        connection.setRequestProperty("ccept-Encoding", "gzip, deflate, br");
        connection.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8");
        connection.setRequestProperty("Cookie", buildCookieString(cookie));
        connection.setRequestProperty("Content-Length", "0");
        OutputStream outputStream = connection.getOutputStream();
        outputStream.write(osData);
        outputStream.flush();
        outputStream.close();

        String pageContent = readInputStream(connection.getInputStream(), false).trim();
        String pointRegExp = "<td>\\d{1,8} </td>";
        String text = stringFind(pageContent, pointRegExp);
        if (text != null) {
            return text.substring(text.indexOf('>') + 1, text.lastIndexOf('<'));
        }
        return null;
    }

    private String openMyCreditCardManager(Map<String, String> cookie) throws Exception {
        URL url = new URL(CmbConstants.MYCREDITCARDMGR);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestProperty("User-Agent", CmbConstants.DEFAULT_UA);
        connection.setSSLSocketFactory(sslContext.getSocketFactory());
        connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        connection.setRequestProperty("Referer", "https://trip.cmbchina.com/SSO/Common/Login/Show.html");
        connection.setRequestProperty("ccept-Encoding", "gzip, deflate, br");
        connection.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8");
        connection.setRequestProperty("Cookie", buildCookieString(cookie));
        updateCmbChinaCookie(connection, cookie);
        return connection.getResponseMessage();
    }

    private String openImageValidatorPage(Map<String, String> cookie) throws Exception {
        URL url = new URL(CmbConstants.IMAGE_VALIDATOR);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestProperty("User-Agent", CmbConstants.DEFAULT_UA);
        connection.setSSLSocketFactory(sslContext.getSocketFactory());
        connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        connection.setRequestProperty("Referer", "https://trip.cmbchina.com/SSO/Common/Login/Show.html");
        connection.setRequestProperty("ccept-Encoding", "gzip, deflate, br");
        connection.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8");
        connection.setRequestProperty("Cookie", buildCookieString(cookie));
        updateCmbChinaCookie(connection, cookie);
        return connection.getResponseMessage();
    }


    private String openPersonalInfoPage(Map<String, String> cookie) throws Exception {
        URL url = new URL(CmbConstants.PERSONAL_INFO);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestProperty("User-Agent", CmbConstants.DEFAULT_UA);
        connection.setSSLSocketFactory(sslContext.getSocketFactory());
        connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        connection.setRequestProperty("Referer", "https://trip.cmbchina.com/SSO/Common/Login/Show.html");
        connection.setRequestProperty("ccept-Encoding", "gzip, deflate, br");
        connection.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8");
        connection.setRequestProperty("Cookie", buildCookieString(cookie));
        updateCmbChinaCookie(connection, cookie);
        return connection.getResponseMessage();
    }

    private Map<String,String> doLogin(String loginUrl, String param, Map<String, String> cookie) throws Exception {
        Map<String, String> loginCookie=new HashMap<String, String>();
        loginCookie.put("BIGipServerPool_B2C_to_TRIP",cookie.get("BIGipServerPool_B2C_to_TRIP"));
        loginCookie.put("WEBTRENDS_ID",cookie.get("WEBTRENDS_ID"));

        byte[] osData = param.getBytes();
        URL url = new URL(loginUrl);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setSSLSocketFactory(sslContext.getSocketFactory());
        connection.setRequestProperty("User-Agent", CmbConstants.DEFAULT_UA);
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("Accept", "application/x-ms-application, image/jpeg, application/xaml+xml, image/gif, image/pjpeg, application/x-ms-xbap, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*");
        connection.setRequestProperty("Referer", "https://trip.cmbchina.com/SSO/Common/Login/Show.html");
        connection.setRequestProperty("ccept-Encoding", "gzip, deflate, br");
        connection.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8");
        connection.setRequestProperty("Cookie", buildCookieString(loginCookie));
        connection.setRequestProperty("Content-Length", String.valueOf(osData.length));
        OutputStream outputStream = connection.getOutputStream();
        outputStream.write(osData);
        outputStream.flush();
        outputStream.close();

        Map<String,String> postDataMap=new HashMap<String, String>();
        String text=readInputStream(connection.getInputStream(),false);
        Pattern pattern = Pattern.compile("\"\\S{32}\"");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()){
            String token = matcher.group(0);
            if (token!=null){
                String t = token.substring(1,token.lastIndexOf('\"'));
                postDataMap.put("token",t);
            }
        }

        pattern = Pattern.compile("\\S{32}\\|SSLSite\\|");
        matcher = pattern.matcher(text);
        if (matcher.find()){
            String sslsite=matcher.group(0);
            if (sslsite!=null){
                postDataMap.put("sslsite",sslsite);
            }
        }

        return postDataMap;
    }

    private String openEWinCallback(String token, String merchantData, Map<String, String> cookie) throws Exception {
        Map<String,String > tmpCookie=new HashMap<String, String>();
        tmpCookie.put("ASP.NET_SessionId",cookie.get("ASP.NET_SessionId"));
        tmpCookie.put("TSfcdae4",cookie.get("TSfcdae4"));
        tmpCookie.put("WTFPC",cookie.get("WTFPC"));
        tmpCookie.put("WEBTRENDS_ID",cookie.get("WEBTRENDS_ID"));
        tmpCookie.put("BIGipServerPOOL_jf_web",cookie.get("BIGipServerPOOL_jf_web"));
        String cookiestring=buildCookieString(tmpCookie);
        String param = "Token=" + token + "&MerchantData=" + merchantData;
        byte[] data = param.getBytes();
        URL url = new URL(CmbConstants.EWINCALLBACK);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setSSLSocketFactory(sslContext.getSocketFactory());
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setRequestProperty("Accept", "application/x-ms-application, image/jpeg, application/xaml+xml, image/gif, image/pjpeg, application/x-ms-xbap, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*");
        connection.setRequestProperty("Referer", "https://trip.cmbchina.com/SSO/Common/Login/Login.html");
        connection.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8");
        connection.setRequestProperty("User-Agent", CmbConstants.DEFAULT_UA);
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
        connection.setRequestProperty("Host","ssl.jf.cmbchina.com");
        connection.setRequestProperty("Connection","Keep-Alive");
        connection.setRequestProperty("Cache-Control","no-cache");
        connection.setRequestProperty("Cookie", cookiestring);
        connection.setRequestProperty("Content-Length", String.valueOf(data.length));

        OutputStream outputStream = connection.getOutputStream();
        outputStream.write(data);
        outputStream.flush();
        outputStream.close();

        String text=readInputStream(connection.getInputStream(),false);
        if (text.contains("isAccountError=1")){
            return null;
        }
        updateCmbChinaCookie(connection, cookie);
        return connection.getResponseMessage();
    }

    private String openDefaultAspxPage(Map<String, String> cookie) throws Exception {
        URL url = new URL(CmbConstants.DEFAULT_ASPX);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("User-Agent", CmbConstants.DEFAULT_UA);
        connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        connection.setRequestProperty("Referer", "https://trip.cmbchina.com/SSO/Common/Login/Login.html");
        connection.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
        connection.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8");
        connection.setRequestProperty("Cookie", buildCookieString(cookie));
        updateCmbChinaCookie(connection, cookie);
        return connection.getResponseMessage();
    }

    private void backupLoginParam(String id, String url, Map<String, String> elem) throws Exception {
        LoginParam lp = new LoginParam();
        lp.setCode(elem.get("Code"));
        lp.setGuidKey(elem.get("GuidKey"));
        lp.setMerchant(elem.get("Merchant"));
        lp.setTimestamp(elem.get("Timestamp"));
        lp.setToken(getTokenValue(stringFind(url, "Token=.{32}")));
        lp.setMerchantData(elem.get("MerchantData"));
        lp.setValidateCodeUrl(url);
        loginParamMap.put(id, lp);
    }

    private String buildLoginParam(LoginParam param, CmbReqData reqData) throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Account=").append(reqData.account)
                .append("&Password=").append(reqData.password)
                .append("&ValidateCode=").append(reqData.code)
                .append("&Token=").append(param.getToken())
                .append("&ValidateCodeUrl=").append(URLEncoder.encode(param.getValidateCodeUrl(),"utf-8"))
                .append("&GuidKey=").append(param.getGuidKey())
                .append("&Timestamp=").append(param.getTimestamp())
                .append("&Code=").append(param.getCode())
                .append("&Merchant=").append(param.getMerchant())
                .append("&MerchantData=").append(URLEncoder.encode(param.getMerchantData(), "utf-8"));
        return stringBuilder.toString();
    }

    /**
     * 打开登陆页,返回下一次需要访问的页面参数
     *
     * @throws Exception
     */
    private String openLoginAspxPage(String redirectUrl, Map<String, String> cookies) throws Exception {
        URL url = new URL(redirectUrl);
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setRequestProperty(CmbConstants.UA, CmbConstants.DEFAULT_UA);
        conn.setSSLSocketFactory(sslContext.getSocketFactory());
        conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        conn.setRequestProperty("Accept-Encoding", "gzip, deflate, sdch, br");
        conn.setRequestProperty("Host","ssl.jf.cmbchina.com");
        conn.setRequestProperty("Upgrade-Insecure-Requests","1");
        conn.setRequestProperty("Accept-Encoding","gzip, deflate, sdch, br");
        updateCmbChinaCookie(conn, cookies);
        String pageContent = readInputStream(conn.getInputStream(), true);
        final String reg = "guidkey=.{32}&MerchantData=.{32}%7cSSLSite%7c";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(pageContent);
        if (matcher.find()) {
            return matcher.group(0);
        }
        return null;
    }

    private HashMap<String, String> openEWinLoginPage(String param, Map<String, String> cookies) throws Exception {
        HashMap<String, String> data = new HashMap<String, String>();
        final String link = CmbConstants.EWINLOGIN + param;
        URL url = new URL(link);
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setRequestProperty(CmbConstants.UA, CmbConstants.DEFAULT_UA);
        conn.setSSLSocketFactory(sslContext.getSocketFactory());
        conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        conn.setRequestProperty("Accept-Encoding", "gzip, deflate, sdch, br");
        conn.setRequestProperty(CmbConstants.COOKIE, buildCookieString(cookies));
        updateCmbChinaCookie(conn, cookies);
        String pageContent = readInputStream(conn.getInputStream(), true);
        data.put("Merchant", "Point");
        data.put("Account", "");
        data.put("IsAccountError", "");

        final String guidkeyReg = "\"GuidKey\" value=\".{32}";
        final String timeStampReg = "\"Timestamp\" value=\".{17}";
        final String codeReg = "\"Code\" value=\".{32}";
        final String merchantDataReg = "\"MerchantData\" value=\".{32}\\|SSLSite\\|";

        int lastSemicolonPos = -1;
        String line = stringFind(pageContent, guidkeyReg);
        if (line != null) {
            lastSemicolonPos = line.lastIndexOf("\"");
            data.put("GuidKey", line.substring(lastSemicolonPos + 1));
        }

        line = stringFind(pageContent, timeStampReg);
        if (line != null) {
            lastSemicolonPos = line.lastIndexOf("\"");
            data.put("Timestamp", line.substring(lastSemicolonPos + 1));
        }

        line = stringFind(pageContent, codeReg);
        if (line != null) {
            lastSemicolonPos = line.lastIndexOf("\"");
            data.put("Code", line.substring(lastSemicolonPos + 1));
        }

        line = stringFind(pageContent, merchantDataReg);
        if (line != null) {
            lastSemicolonPos = line.lastIndexOf("\"");
            data.put("MerchantData", line.substring(lastSemicolonPos + 1));
        }
        return data;
    }

    private String getTempWtid() throws Exception{
        URL url = new URL(CmbConstants.WTID_PAGE);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setSSLSocketFactory(sslContext.getSocketFactory());
        connection.setRequestProperty("Host","sdc.cmbchina.com");
        connection.setRequestProperty(CmbConstants.UA,CmbConstants.DEFAULT_UA);
        connection.setRequestProperty("Accept","*/*");
        connection.setRequestProperty("Referer","https://ssl.jf.cmbchina.com/Customer/Login.aspx");
        String pageData = readInputStream(connection.getInputStream(),false);
        if (pageData==null){
            return null;
        }

        final String reg="\".*\"";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(pageData);
        if (matcher.find()){
            String text = matcher.group(0);
            return text.substring(text.indexOf('\"')+1,text.lastIndexOf('\"'));
        }
        return null;
    }

    /**
     * 可以获取到图片的URL地址
     *
     * @param param
     * @throws Exception
     */
    private String openShowPage(HashMap<String, String> param,Map<String,String> cookie) throws Exception {
        URL url = new URL(CmbConstants.SHOW_PAGE);
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setSSLSocketFactory(sslContext.getSocketFactory());
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setSSLSocketFactory(sslContext.getSocketFactory());
        conn.setRequestProperty(CmbConstants.UA, CmbConstants.DEFAULT_UA);
        conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        conn.setRequestProperty("Accept-Encoding", "gzip, deflate, sdch, br");
        conn.setRequestProperty("Referer", "https://ssl.jf.cmbchina.com/Customer/");
        //conn.setRequestProperty(CmbConstants.COOKIE, buildCookieString(cookies));
        String p = map2String(param);
        conn.setRequestProperty(CmbConstants.CONTENT_LENGTH, p.getBytes().length + "");
        conn.setRequestProperty(CmbConstants.CONTENT_TYPE, "application/x-www-form-urlencoded");
        OutputStream os = conn.getOutputStream();
        os.write(p.getBytes());
        os.close();
        updateCmbChinaCookie(conn,cookie);
        String pageContent = readInputStream(conn.getInputStream(), false);
        final String tokenReg = "https://trip\\.cmbchina\\.com/SSO/Common/Login/ValidateCode\\.html\\?Token=.{32}";
        return stringFind(pageContent, tokenReg);
    }

    /**
     * 图片保存在本地
     *
     * @param url
     * @param localPath
     * @throws Exception
     */
    private void downloadPicture(String url, String localPath, boolean isGZIP) throws Exception {
        URL page = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) page.openConnection();
        connection.setRequestProperty(CmbConstants.UA, CmbConstants.DEFAULT_UA);

        byte[] buffer = new byte[1024];
        int byteReaded = 0;

        InputStream is = connection.getInputStream();
        File picture = new File(localPath);
        FileOutputStream fos = new FileOutputStream(picture);
        if (!isGZIP) {
            while ((byteReaded = is.read(buffer)) != -1) {
                fos.write(buffer, 0, byteReaded);
            }
        } else {
            GZIPInputStream gis = new GZIPInputStream(is);
            while ((byteReaded = gis.read(buffer)) != -1) {
                fos.write(buffer, 0, byteReaded);
            }
        }
        fos.close();
        is.close();
    }


    private void updateCmbChinaCookie(HttpURLConnection conn, Map<String, String> cookie) {
        if (conn == null) {
            return;
        }
        Map<String, List<String>> headerFields = conn.getHeaderFields();
        Set<String> fields = headerFields.keySet();
        if (!fields.contains("Set-Cookie")) {
            return;
        }
        List<String> cookieValues = headerFields.get("Set-Cookie");
        for (String value : cookieValues) {
            int firstSemicolon = value.indexOf(';');
            if (firstSemicolon == -1) {
                continue;
            }
            String thecookie = value.substring(0, firstSemicolon);
            int firstColon = thecookie.indexOf('=');
            String theKey = thecookie.substring(0, firstColon);
            String theValue = thecookie.substring(firstColon + 1);
            cookie.put(theKey, theValue);
        }
    }

    private void updateCmbChinaCookie(HttpsURLConnection conn, Map<String, String> cookie) {
        if (conn == null) {
            return;
        }
        Map<String, List<String>> headerFields = conn.getHeaderFields();
        Set<String> fields = headerFields.keySet();
        if (!fields.contains("Set-Cookie")) {
            return;
        }
        List<String> cookieValues = headerFields.get("Set-Cookie");
        for (String value : cookieValues) {
            int firstSemicolon = value.indexOf(';');
            if (firstSemicolon == -1) {
                continue;
            }
            String thecookie = value.substring(0, firstSemicolon);
            int firstColon = thecookie.indexOf('=');
            String theKey = thecookie.substring(0, firstColon);
            String theValue = thecookie.substring(firstColon + 1);
            cookie.put(theKey, theValue);
        }
    }

    private String readInputStream(InputStream is, boolean isGzip) throws Exception {
        String data = null;
        if (isGzip) {
            return unzipGZIP(is);
        }

        byte[] by = new byte[1024];
        StringBuilder stringBuffer = new StringBuilder();
        int len = 0;
        while ((len = is.read(by)) != -1) {
            stringBuffer.append(new String(by, 0, len));
        }
        data = stringBuffer.toString();
        return data;
    }

    /**
     * 解压gzip格式的数据
     *
     * @param in
     * @return
     * @throws Exception
     */
    private String unzipGZIP(InputStream in) throws Exception {
        GZIPInputStream gInputStream = new GZIPInputStream(in);
        byte[] by = new byte[1024];
        StringBuffer strBuffer = new StringBuffer();
        int len = 0;
        while ((len = gInputStream.read(by)) != -1) {
            strBuffer.append(new String(by, 0, len));
        }
        return strBuffer.toString();
    }


    private String buildCookieString(Map<String, String> cookie) throws Exception {
        StringBuilder data = new StringBuilder();
        for (String key : cookie.keySet()) {
            data.append(key).append("=");
            if (cookie.get(key) != null) {
                data.append(cookie.get(key));
            }
            data.append(";");
        }
        return data.toString();
    }

    private String map2String(HashMap<String, String> map) throws Exception {
        if (map == null || map.isEmpty()) {
            throw new Exception("传入了无效的参数");
        }
        StringBuilder string = new StringBuilder();
        for (String key : map.keySet()) {
            string.append(key).append("=").append(map.get(key)).append("&");
        }
        String tmp = string.toString();
        return tmp.substring(0, tmp.length() - 1);
    }

    private String stringFind(String plain, String regExp) throws Exception {
        if (plain == null||"".equals(plain)) {
            return null;
        }
        Pattern pattern = Pattern.compile(regExp);
        Matcher matcher = pattern.matcher(plain);
        if (matcher.find()) {
            return matcher.group(0);
        }
        return null;
    }

    private String getTokenValue(String text) {
        if (text == null) {
            return null;
        }

        return text.substring(text.indexOf('=') + 1);
    }

    private  String createWTFPC_WT_COF(String tmpid) throws Exception{
        String time = String.valueOf(System.currentTimeMillis());
        return "id="+tmpid+":lv="+time+":ss="+time;
    }

    private String rebuildWTFPC(Map<String,String> cookie){
        String originWtfpc=cookie.get("WTFPC");
        String suffix=originWtfpc.substring(originWtfpc.indexOf(':'));
        String wid = cookie.get("WEBTRENDS_ID");
        return String.format("id=%s%s",wid,suffix);
    }

    public static void main(String[] argv) {
        try {

            CmbChina.DEBUG = true;
            CmbChina cmbChina = new CmbChina();
            CmbResult result = cmbChina.getVerifyCode();
            JfRequest request = new JfRequest();
            request.setId(result.id);
            request.setAccount("13456875762");
            request.setPassword("985628");
            Scanner scanner = new Scanner(System.in);
            System.out.println("请输入验证码:");
            request.setCode(scanner.nextLine());
            JfResult jfResult = cmbChina.queryIntegral(request);
            System.out.println(jfResult.getPoints());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
