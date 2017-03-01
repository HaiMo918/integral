package com.integral.service.video.thunder;

import com.integral.tools.MD5;
import com.integral.utils.*;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kris on 2017/3/1.
 */
public class ThunderService implements IQueryIntegral{
    private MyX509TrustManager xtm = new MyX509TrustManager();
    private MyHostNameVerify mhv = new MyHostNameVerify();
    private SSLContext sslContext = null;
    private final String refer = "http://i.xunlei.com/login/?r_d=1&use_cdn=0&timestamp="
            +System.currentTimeMillis()
            +"&refurl=http%3A%2F%2Fjifen.xunlei.com%2Fmyinfo%2F";
    public ThunderService() throws Exception{
        sslContext = SSLContext.getInstance("SSL");
        X509TrustManager[] xtmArray = new X509TrustManager[]{xtm};
        sslContext.init(null, xtmArray, new java.security.SecureRandom());
    }

    @Override
    public JfResult requestVerifyCode(JfRequest request) throws Exception {
        return null;
    }

    @Override
    public JfResult queryIntegral(JfRequest request) throws Exception {
        JfResult result = new JfResult();
        Map<String,String> thunderCookie = new HashMap<>();
        riskReport(thunderCookie);
        String response = doPreLoginThunder(request.getAccount(),thunderCookie);
        if (response.contains("limit")){
            result.setMessage("[迅雷]预处理验证失败");
            return result;
        }
        return result;
    }

    private void riskReport(Map<String,String> cookie) throws Exception{
        final String param =
                "xl_fp_raw=TW96aWxsYS81LjAgKFdpbmRvd3MgTlQgNi4xOyBXT1c2NCkgQXBwbGVXZWJLaXQvNTM3LjM2IChLSFRNTCwgbGlrZSB" +
                        "HZWNrbykgQ2hyb21lLzU1LjAuMjg4My44NyBTYWZhcmkvNTM3LjM2IyMjemgtQ04jIyMyNCMjIzkwMHgxNDQwIyMjLTQ4M" +
                        "CMjI3RydWUjIyN0cnVlIyMjdHJ1ZSMjI3VuZGVmaW5lZCMjI2Z1bmN0aW9uIyMjIyMjV2luMzIjIyMjIyNXaWRldmluZSB" +
                        "Db250ZW50IERlY3J5cHRpb24gTW9kdWxlOjpFbmFibGVzIFdpZGV2aW5lIGxpY2Vuc2VzIGZvciBwbGF5YmFjayBvZiBIV" +
                        "E1MIGF1ZGlvL3ZpZGVvIGNvbnRlbnQuICh2ZXJzaW9uOiAxLjQuOC45MDMpOjphcHBsaWNhdGlvbi94LXBwYXBpLXdpZGV" +
                        "2aW5lLWNkbX47Q2hyb21lIFBERiBWaWV3ZXI6Ojo6YXBwbGljYXRpb24vcGRmfjtTaG9ja3dhdmUgRmxhc2g6OlNob2Nrd" +
                        "2F2ZSBGbGFzaCAyMy4wIHIwOjphcHBsaWNhdGlvbi94LXNob2Nrd2F2ZS1mbGFzaH5zd2YsYXBwbGljYXRpb24vZnV0dXJ" +
                        "lc3BsYXNofnNwbDtOYXRpdmUgQ2xpZW50Ojo6OmFwcGxpY2F0aW9uL3gtbmFjbH4sYXBwbGljYXRpb24veC1wbmFjbH47Q" +
                        "2hyb21lIFBERiBWaWV3ZXI6OlBvcnRhYmxlIERvY3VtZW50IEZvcm1hdDo6YXBwbGljYXRpb24veC1nb29nbGUtY2hyb21l" +
                        "LXBkZn5wZGYjIyMyMmI0NjkyZjk4ZjBkN2E0OGVlMzc3Mjg5ODFjZDFjNQ==&xl_fp=0fc1d5aefd60ede98252c02" +
                        "cec676c34&xl_fp_sign=6340aec69e9a352a87b790bc93d7e3a3&cachetime="+System.currentTimeMillis();
        final String url = "https://login.xunlei.com/risk?cmd=report";
        byte[] data = param.getBytes();
        final String length=String.valueOf(data.length);

        HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setHostnameVerifier(mhv);
        connection.setSSLSocketFactory(sslContext.getSocketFactory());
        connection.setRequestProperty(Constants.HttpHeaders.USER_AGENT,Constants.DEFAULT_UA);
        connection.setRequestProperty(Constants.HttpHeaders.CONTENT_TYPE,"application/x-www-form-urlencoded");
        connection.setRequestProperty(Constants.HttpHeaders.CONTENT_LENGTH,length);
        connection.setRequestProperty(Constants.HttpHeaders.REFERER,refer);

        OutputStream os = connection.getOutputStream();
        os.write(data);
        os.flush();
        os.close();

        Common.updateCookie(connection,cookie);
        cookie.put("_x_t_","0");
    }
    private String doPreLoginThunder(String user,Map<String,String> cookie) throws Exception{
        final long t1 = System.currentTimeMillis();
        //final String csrf_token=new MD5().bytesToMD5(cookie.get("deviceid").substring(0,32).getBytes());
        ThunderPreLoginData thunderPreLoginData = new ThunderPreLoginData();
        thunderPreLoginData.u=user;
        thunderPreLoginData.csrf_token="693ba5d5847621b344a5b36c95ff1471";
        thunderPreLoginData.cachetime=t1;

        final String url = "https://login.xunlei.com/check/?"+thunderPreLoginData.toString();
        HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
        connection.setHostnameVerifier(mhv);
        connection.setSSLSocketFactory(sslContext.getSocketFactory());
        connection.setRequestProperty(Constants.HttpHeaders.USER_AGENT,Constants.DEFAULT_UA);
        connection.setRequestProperty(Constants.HttpHeaders.ACCEPT,"image/webp,image/*,*/*;q=0.8");
        connection.setRequestProperty(Constants.HttpHeaders.REFERER,refer);

        Common.updateCookie(connection,cookie);
        return Common.inputStreamToString(connection.getInputStream());
    }

    public static void main(String[] args){
        try{
            JfRequest request = new JfRequest();
            request.setAccount("515091847@qq.com");
            request.setPassword("lqh_1985t");

            ThunderService service = new ThunderService();
            service.queryIntegral(request);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
