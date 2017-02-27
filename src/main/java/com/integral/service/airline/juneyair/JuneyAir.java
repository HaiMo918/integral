package com.integral.service.airline.juneyair;//Created by xiacheng on 16/9/11.

import com.alibaba.fastjson.JSONObject;
import com.integral.utils.IQueryIntegral;
import com.integral.utils.JfRequest;
import com.integral.utils.JfResult;
import org.springframework.stereotype.Service;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class JuneyAir implements IQueryIntegral{
    private String mAccount;
    private String mPassword;
    private String mVerifyCode;
    private Map<String, String> mCookies = new HashMap<String,String>();
    private MyX509TrustManager xtm = new MyX509TrustManager();
    private MyHostNameVerify mhv = new MyHostNameVerify();
    private SSLContext sslContext = null;
    private String id;//每个对象都持有一个id,与之对应的是JuneyResults
    public static Map<String,JuneyResults> idCache = new HashMap<String,JuneyResults>();//id cache,最多1024个
    public static Map<String,String> globalCookieCache = new HashMap<String, String>();

    @Override
    public JfResult requestVerifyCode(JfRequest request) throws Exception {
        JfResult jfResult = new JfResult();
        JuneyResults juneyResults = getVerifyCodeData();
        jfResult.setCode(juneyResults.code+"");
        jfResult.setData(juneyResults.picurl);
        jfResult.setMessage(juneyResults.message);
        jfResult.setId(juneyResults.id);
        return jfResult;
    }

    @Override
    public JfResult queryIntegral(JfRequest request) throws Exception {
        JfResult jfResult = new JfResult();
        JuneyResults juneyResults = getIntegral(request);
        jfResult.setPoints(juneyResults.points);
        jfResult.setMessage(juneyResults.message);
        jfResult.setCode(juneyResults.code+"");

        return jfResult;
    }

    class MyX509TrustManager implements X509TrustManager {

        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s)
                throws CertificateException {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s)
                throws CertificateException {

        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;//new X509Certificate[0];
        }
    }

    /**
     * MyHostNameVerify
     */
    class MyHostNameVerify implements HostnameVerifier {
        @Override
        public boolean verify(String s, SSLSession sslSession) {
            return true;
        }
    }


    public JuneyAir() {
        try {
            sslContext = SSLContext.getInstance("TLS");
            X509TrustManager[] xtmArray = new X509TrustManager[]{xtm};
            sslContext.init(null, xtmArray, new java.security.SecureRandom());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 打开登录页面,并生成验证码的下载地址
     * @return
     */
    private synchronized JuneyResults getVerifyCodeData() {
        JuneyResults results = null;
        try {
            results = openLoginPage();
            assert results!=null;
            System.out.println(JSONObject.toJSONString(results));

            idCache.put(mCookies.get("JSESSIONID"),results);
            id=mCookies.get("JSESSIONID");

            URL url = new URL(JuneyConsts.VERIFYCODE_LINK);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setSSLSocketFactory(sslContext.getSocketFactory());
            conn.setHostnameVerifier(mhv);
            conn.setRequestProperty("User-Agent", JuneyConsts.JUNEY_UA);
            conn.setRequestProperty("Cookie",map2String(mCookies));
            long curMillons = System.currentTimeMillis();
            String picLocalPath="/usr/local/etc/tomcat8/webapps/images/"+curMillons+".jpeg";
            File jpeg = new File(picLocalPath);
            FileOutputStream fw = new FileOutputStream(jpeg);
            InputStream is = conn.getInputStream();
            byte[] buffer = new byte[1024];
            int count;
            if (is != null) {
                while ((count=is.read(buffer))!=-1){
                    fw.write(buffer,0,count);
                }
                fw.flush();
                fw.close();
            }
            is.close();


            results.picurl="http://114.55.133.156:8080/images/"+curMillons+".jpeg";
            results.message=conn.getResponseMessage();
            results.code=conn.getResponseCode();
            results.id=id;
        } catch (Exception e) {
            results.exception=e.getMessage();
        }
        return results;
    }

    private synchronized JuneyResults getIntegral(JfRequest request) throws Exception {
        JuneyResults results = null;
        this.mAccount = request.getAccount();
        this.mPassword = request.getPassword();
        this.mVerifyCode = request.getCode();
        results = idCache.get(request.getId());
        String param = buildLoginParam(results.getExtraInfo(JuneyResults.ExtraInfo.LT));
        results = login(param);
        results = openTicketPage(results.location);
        results = openIndexPage(results.location);
        return results;
    }


    /**
     * 打开登录页面,返回一个真正的登陆页
     * @return
     */
    private JuneyResults openLoginPage() {
        JuneyResults results = new JuneyResults();
        try {
            URL url = new URL(JuneyConsts.LOGIN_PAGE);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setSSLSocketFactory(sslContext.getSocketFactory());
            conn.setHostnameVerifier(mhv);
            conn.setRequestProperty("User-Agent", JuneyConsts.JUNEY_UA);
            //conn.setRequestProperty("Host", "sso.juneyaoair.com");
            //conn.setRequestProperty("Referer","http://www.juneyaoair.com/index.aspx");
            conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            conn.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
            conn.setRequestProperty("Accept-Encoding","gzip, deflate, br");
            //conn.setRequestProperty("Connection","keep-alive");
            //conn.setRequestProperty("Upgrade-Insecure-Requests", "1");
            updateJuneyAirCookie(conn);
            InputStream is = conn.getInputStream();
            int pageSize = is.available();
            byte[] pageContent = new byte[pageSize];
            is.read(pageContent);
            is.close();

            String content = new String(pageContent).trim();
            Pattern pattern = Pattern.compile("LT-\\d{1,6}-\\S{16,32}-cas.juneyaoair.com");
            Matcher matcher = pattern.matcher(content);
            if (matcher.find()) {
                results.putExtraInfo(JuneyResults.ExtraInfo.LT, matcher.group(0));
            }
            results.message=conn.getResponseMessage();
            results.code=conn.getResponseCode();
        } catch (Exception e) {
            results.exception=e.getMessage();
        }
        return results;
    }


    private JuneyResults login(String param) {
        JuneyResults results = new JuneyResults();
        HttpsURLConnection conn = null;
        int contentLength = param.getBytes().length;
        try {
            URL requested = new URL(JuneyConsts.LOGIN_PAGE);
            conn = (HttpsURLConnection) requested.openConnection();
            conn.setSSLSocketFactory(sslContext.getSocketFactory());
            conn.setHostnameVerifier(mhv);
            conn.setInstanceFollowRedirects(false);
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            //conn.setRequestProperty("Host","sso.juneyaoair.com");
            conn.setRequestProperty("User-Agent", JuneyConsts.JUNEY_UA);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Cookie", map2String(mCookies));
            conn.setRequestProperty("Content-Length", String.valueOf(contentLength));

            DataOutputStream os = new DataOutputStream(conn.getOutputStream());
            os.write(param.getBytes());
            os.flush();
            os.close();
            switch (conn.getResponseCode()) {
                case HttpURLConnection.HTTP_MOVED_TEMP:
                    updateJuneyAirCookie(conn);
                    results.location=conn.getHeaderField("Location");
                    results.code=conn.getResponseCode();
                    results.message=conn.getResponseMessage();
                    break;
                default:
                    results.code=conn.getResponseCode();
                    results.message=conn.getResponseMessage();
                    break;
            }
        } catch (Exception e) {
            results.exception=e.getMessage();
        }
        return results;
    }

    private JuneyResults openTicketPage(String url) {
        JuneyResults results = new JuneyResults();
        try {
            URL urlLink = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) urlLink.openConnection();
            conn.setInstanceFollowRedirects(false);
            conn.setRequestProperty("User-Agent", JuneyConsts.JUNEY_UA);
            //conn.setRequestProperty("Host", "juneyaoair.com");
            conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            conn.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
            conn.setRequestProperty("Accept-Encoding","gzip, deflate, br");
            //conn.setRequestProperty("Connection","keep-alive");
            //conn.setRequestProperty("Upgrade-Insecure-Requests", "1");
            switch (conn.getResponseCode()) {
                case HttpURLConnection.HTTP_MOVED_TEMP:
                    updateJuneyAirCookie(conn);
                    results.location=conn.getHeaderField("Location");
                    results.code=conn.getResponseCode();
                    results.message=conn.getResponseMessage();
                    break;
                default:
                    results.code=conn.getResponseCode();
                    results.message=conn.getResponseMessage();
                    break;
            }
        } catch (Exception e) {
            results.exception=e.getMessage();
        }
        return results;
    }

    private JuneyResults openIndexPage(String pageUrl) {
        JuneyResults results = new JuneyResults();
        try {
            URL url = new URL(pageUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Host","juneyaoair.com");
            conn.setRequestProperty("User-Agent", JuneyConsts.JUNEY_UA);
            conn.setRequestProperty("Cookie", map2String(mCookies));

            updateJuneyAirCookie(conn);
            results.code=conn.getResponseCode();
            results.message=conn.getResponseMessage();

            InputStream is = conn.getInputStream();
            StringBuilder sb = new StringBuilder();
            String line;
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            while ((line=br.readLine())!=null){
                sb.append(line);
            }
            br.close();

            String currentPoints = null;
            Pattern pattern = Pattern.compile("目前积分：<span>\\d{1,6}点</span>");
            String webContent = sb.toString();
            Matcher matcher = pattern.matcher(webContent);
            if (matcher.find()) {
                currentPoints = matcher.group(0);
            }
            if (currentPoints != null) {
                Pattern pattern1 = Pattern.compile("\\d{1,6}");
                Matcher matcher1 = pattern1.matcher(currentPoints);
                if (matcher1.find()) {
                    currentPoints = matcher1.group(0);
                    results.points=currentPoints;
                }
            }
        } catch (Exception e) {
            results.exception=e.getMessage();
        }
        return results;
    }




    /**
     * 更新cookie
     *
     * @param connection
     */
    private void updateJuneyAirCookie(HttpURLConnection connection) {
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

    private String buildLoginParam(String lt) {
        return "username=" + this.mAccount + "&password=" + this.mPassword +
                "&authcode=" + this.mVerifyCode + "&lt=" + lt + "&execution=e1s1&_eventId=submit";
    }

    public static void main(String[] args) throws Exception{
        JuneyAir air = new JuneyAir();
        JfResult results = air.requestVerifyCode(null);
        Scanner sc = new Scanner(System.in);
        System.out.println("输入验证码:");
        String code = sc.nextLine();

        JfRequest request = new JfRequest();
        request.setAccount("18621299168");
        request.setPassword("lisa007");
        request.setCode(code);
        results = air.queryIntegral(request);
        System.out.println(results.getPoints());
    }
}
