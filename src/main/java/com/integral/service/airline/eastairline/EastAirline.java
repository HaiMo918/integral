package com.integral.service.airline.eastairline;

import com.alibaba.fastjson.JSONObject;
import com.integral.utils.Constants;
import com.integral.utils.IQueryIntegral;
import com.integral.utils.JfRequest;
import com.integral.utils.JfResult;
import org.springframework.stereotype.Service;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;

@Service
public class EastAirline implements IQueryIntegral{
    private MyX509TrustManager xtm = new MyX509TrustManager();
    private MyHostNameVerify mhv = new MyHostNameVerify();
    private SSLContext sslContext = null;
    private static Map<String,Map<String,String>> cookieCache = new HashMap<String,Map<String,String>>();

    @Override
    public JfResult requestVerifyCode(JfRequest request) throws Exception {
        JfResult jfResult = new JfResult();
        EaResult eaResult = requestVerifyCode();
        jfResult.setMessage(eaResult.message);
        jfResult.setData(eaResult.picurl);
        jfResult.setId(eaResult.id);
        return jfResult;
    }

    @Override
    public JfResult queryIntegral(JfRequest request) throws Exception {
        JfResult jfResult = new JfResult();
        EaResult eaResult = getEastAirlineIntegral(request);
        jfResult.setPoints(eaResult.points);
        jfResult.setMessage("获取东航积分成功");
        jfResult.setCode(Constants.ErrorCode.ERROR_SUCCESS+"");
        cookieCache.remove(request.getId());
        return jfResult;
    }

    public EastAirline() {
        try {
            sslContext = SSLContext.getInstance("TLS");
            X509TrustManager[] xtmArray = new X509TrustManager[]{xtm};
            sslContext.init(null, xtmArray, new java.security.SecureRandom());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 请求图片验证码,并返回给客户端图片地址
     *
     * @return EaResult
     * 填充其中的图片URL地址及错误和异常信息
     */
    private synchronized EaResult requestVerifyCode() throws Exception {
        EaResult result = null;
        result = openLoginHomePage();
        result.id = result.cookie.get("JSESSIONCESSOID");
        result.picurl = getVerifyCode(EAConstants.VERIFY_CODE_PAGE, map2String(result.cookie));
        result.cookie.put("isCookieOn", "testCookie");
        cookieCache.put(result.id, result.cookie);
        return result;
    }


    /**
     * 从输入流中读取图片数据,保存在本地
     * @return
     * 返回图片的URL链接
     * @throws Exception
     */
    private String getVerifyCode(String page,String cookie) throws Exception {
        URL url = new URL(page);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setSSLSocketFactory(sslContext.getSocketFactory());
        connection.setHostnameVerifier(mhv);
        connection.setRequestProperty("User-Agent", EAConstants.EA_AGENT);
        connection.setRequestProperty("Cookie", cookie);

        long time = System.currentTimeMillis();
        String serverLocalPath = "/usr/local/etc/tomcat8/webapps/images/"+time+".jpeg";
        //System.out.println("图片在服务器的本地路径:"+serverLocalPath);
        //String debugLocalPath = time+".jpeg";
        //System.out.println("图片在本地的路径:"+new File(debugLocalPath).getAbsolutePath());
        String pictureUrlPath="http://114.55.133.156:8080/images/"+time+".jpeg";
        //System.out.println("图片下载地址:"+pictureUrlPath);

        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
           saveVerifyCodePicture(serverLocalPath,connection.getInputStream());
        }
        return pictureUrlPath;
    }

    /**
     * 获取东航积分
     * @param reqData
     * @return
     */
    private EaResult getEastAirlineIntegral(JfRequest reqData) {
        EaResult score = new EaResult();
        score.points="-1";//for default
        try {
            EaResult result = loginEastAir(reqData,cookieCache.get(reqData.getId()));
            openIndexPage(result.location, result.cookie);
            result=openShopCartTotal(result.cookie);
            result =  getUserInfo(result.cookie);
            score.points = result.points;
        } catch (Exception e) {
            e.printStackTrace();
        }
        // delete the value we don't use anymore
        cookieCache.remove(reqData.getId());
        return score;
    }

    /**
     * 打开登录首页,获取到如下两个cookie
     * 1.Webtrends
     * 2.JSESSIONCESSOID
     */
    private EaResult openLoginHomePage() throws Exception {
        EaResult result = new EaResult();
        URL url = new URL(EAConstants.LOGIN_PAGE);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setHostnameVerifier(mhv);
        connection.setSSLSocketFactory(sslContext.getSocketFactory());
        connection.setRequestProperty("User-Agent", EAConstants.EA_AGENT);
        Map<String,String> cookie = new HashMap<String,String>();
        updateEaCookies(connection, cookie);
        result.cookie=cookie;
        result.code=connection.getResponseCode()+"";
        result.message=connection.getResponseMessage();
        return result;
    }

    private EaResult loginEastAir(JfRequest data,Map<String,String> cookie) throws Exception {
        EaResult result = new EaResult();

        String param = generateLoginParam(data.getAccount(),data.getPassword(),data.getCode());
        int length = param.getBytes().length;
        URL url = new URL(EAConstants.LOGIN_URL);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setHostnameVerifier(mhv);
        connection.setSSLSocketFactory(sslContext.getSocketFactory());
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setInstanceFollowRedirects(false);
        connection.setRequestProperty("User-Agent", EAConstants.EA_AGENT);
        connection.setRequestProperty("Cookie",map2String(cookieCache.get(data.getId())));
        connection.setRequestProperty("Content-Length", String.valueOf(length));
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        OutputStream os = connection.getOutputStream();
        os.write(param.getBytes());
        os.flush();
        os.close();

        switch (connection.getResponseCode()) {
            case HttpsURLConnection.HTTP_MOVED_PERM:
            case HttpsURLConnection.HTTP_MOVED_TEMP:
                updateEaCookies(connection, cookie);
                result.cookie=cookie;
                result.code = String.valueOf(connection.getResponseCode());
                result.message = connection.getResponseMessage();
                result.location = connection.getHeaderField("Location");
                break;
            default:
                result.cookie=cookie;
                result.code = String.valueOf(connection.getResponseCode());
                result.message = connection.getResponseMessage();
                break;
        }
        return result;
    }

    /**
     * 打开登录成功之后的页面
     * @param page
     * @param cookie
     * @throws Exception
     */
    private void openIndexPage(String page,Map<String,String> cookie)throws Exception{
        if (page==null||page.isEmpty()){
            return;
        }
        URL url = new URL(page);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setRequestProperty("User-Agent",EAConstants.EA_AGENT);
        httpConn.setRequestProperty("Cookie",map2String(cookie));
    }

    /**
     * 打开购物车页面
     * @param cookie
     */
    private EaResult openShopCartTotal(Map<String,String > cookie) throws Exception {
        EaResult result = new EaResult();
        URL url = new URL(EAConstants.SHOP_CART_TOTAL_PAGE);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("User-Agent",EAConstants.EA_AGENT);
        connection.setRequestProperty("Cookie", map2String(cookie));
        connection.setRequestProperty("Content-Length","0");
        connection.setRequestProperty("Accept","application/json, text/javascript, */*; q=0.01");
        connection.setRequestProperty("Accept-Language","zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
        connection.setRequestProperty("Accept-Encoding", "gzip, deflate");
        connection.setRequestProperty("DNT","1");
        connection.setRequestProperty("X-Requested-With","XMLHttpRequest");
        connection.setRequestProperty("Referer","http://shopping.ceair.com/Home/Index");
        connection.setRequestProperty("Connection","keep-alive");
        connection.setRequestProperty("Host", "shopping.ceair.com");
        OutputStream os = connection.getOutputStream();
        os.write("".getBytes());
        os.close();
        updateEaCookies(connection, cookie);
        result.cookie = cookie;
        result.message = connection.getResponseMessage();
        result.code = connection.getResponseCode() + "";
        return result;
    }

    /**
     * 查询积分
     *
     * @return
     */
    private EaResult getUserInfo(Map<String, String> cookie) throws Exception {
        EaResult result = new EaResult();
        URL url = new URL(EAConstants.USER_INFO_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("User-Agent", EAConstants.EA_AGENT);
        connection.setRequestProperty("Cookie", map2String(cookie));
        connection.setRequestProperty("Content-Length", "0");
        OutputStream os = connection.getOutputStream();
        os.write("".getBytes());
        os.close();
        InputStream inputStream = connection.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();
        JSONObject object = JSONObject.parseObject(sb.toString());
        if (object.getString("Code").equals("0")) {
            JSONObject dataObj = object.getJSONObject("Data");
            if (dataObj != null) {
                result.points = dataObj.getString("Score");
            }
        }
        return result;
    }

    /**
     * 构造登录参数
     *
     * @return
     */
    private String generateLoginParam(String account,String password,String code) throws UnsupportedEncodingException {
        StringBuilder param = new StringBuilder();
        param.append("user=").append(account)
                .append("&password=").append(password)
                .append("&validcode=").append(code)
                .append("&login=").append(URLEncoder.encode("登录", "utf-8"))
                .append("&redirectUrl=").append(URLEncoder.encode("http://shopping.ceair.com/Home/Index", "utf-8"))
                .append("&ltv=1").append("&at=1");
        return param.toString();
    }

    /**
     * 更新Cookies数据
     *
     * @param connection
     * @param cookies
     */
    private void updateEaCookies(HttpURLConnection connection, Map<String, String> cookies) {
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
                        String theKey = cookie.substring(0, firstColon);
                        String theValue = cookie.substring(firstColon + 1);
                        cookies.put(theKey, theValue);
                    }
                }
            }
        }
    }

    /**
     * 把map转换为String
     *
     * @param mapData
     * @return
     */
    private String map2String(Map<String, String> mapData) {
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

    /**
     * 保存验证码图片到本地
     * @param localPath
     * @param is
     * @throws Exception
     */
    private void saveVerifyCodePicture(String localPath, InputStream is) throws Exception {
        File picture = new File(localPath);
        FileOutputStream fos = new FileOutputStream(picture);
        int bytesCount = 0;
        byte[] buffer = new byte[1024];
        while ((bytesCount=is.read(buffer))!=-1){
            fos.write(buffer,0,bytesCount);
        }
        fos.close();
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


    public static void main(String[] argvs) {
        try {
            EastAirline eastAirline = new EastAirline();
            EaResult result = eastAirline.requestVerifyCode();

            JfRequest  reqData = new JfRequest();
            System.out.print("请输入验证码:");
            Scanner scanner = new Scanner(System.in);
            reqData.setCode(scanner.next());
            reqData.setAccount("650263236130");
            reqData.setPassword("12345678");
            reqData.setId(result.id);

            String score = eastAirline.getEastAirlineIntegral(reqData).points;
            System.out.println("当前用户积分:"+score);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
