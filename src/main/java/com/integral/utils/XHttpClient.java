package com.integral.utils;

import org.apache.commons.io.IOUtils;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.sync.CloseableHttpClient;
import org.apache.hc.client5.http.impl.sync.HttpClientBuilder;
import org.apache.hc.client5.http.methods.CloseableHttpResponse;
import org.apache.hc.client5.http.methods.HttpGet;
import org.apache.hc.client5.http.methods.HttpPost;
import org.apache.hc.client5.http.socket.ConnectionSocketFactory;
import org.apache.hc.client5.http.socket.LayeredConnectionSocketFactory;
import org.apache.hc.client5.http.socket.PlainConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLInitializationException;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.config.Registry;
import org.apache.hc.core5.http.config.RegistryBuilder;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by xiongzicheng on 2016/11/1.
 */
public class XHttpClient {
    final static PoolingHttpClientConnectionManager CONNMGR;
    final static CloseableHttpClient CLIENT;


    static Pattern pattern = Pattern.compile("charset=(\\S+)");

    static {
        LayeredConnectionSocketFactory ssl = null;
        try {
            ssl = SSLConnectionSocketFactory.getSystemSocketFactory();
        } catch (final SSLInitializationException ex) {
            final SSLContext sslcontext;
            try {
                sslcontext = SSLContext.getInstance(SSLConnectionSocketFactory.TLS);
                sslcontext.init(null, null, null);
                ssl = new SSLConnectionSocketFactory(sslcontext);
            } catch (final SecurityException | NoSuchAlgorithmException | KeyManagementException ignore) {
            }
        }

        final Registry<ConnectionSocketFactory> sfr = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https", ssl != null ? ssl : SSLConnectionSocketFactory.getSocketFactory())
                .build();

        CONNMGR = new PoolingHttpClientConnectionManager(sfr);
        CONNMGR.setDefaultMaxPerRoute(100);
        CONNMGR.setMaxTotal(200);
        CONNMGR.setValidateAfterInactivity(1000);
        CLIENT = HttpClientBuilder.create()
                .setConnectionManager(CONNMGR)
                .setDefaultCookieStore(new XCookieStore())
                .build();
    }

    public static String get(HttpGet httpget) throws IOException {
        return HttpEntityToString(XHttpClient.CLIENT.execute(httpget).getEntity());
    }

    public static InputStream getInputStream(String url) throws IOException {
        HttpGet httpget = new HttpGet(url);
        return XHttpClient.CLIENT.execute(httpget).getEntity().getContent();
    }

    public static String post(String url,Map<String,Object> params) throws IOException {
        MultipartEntityBuilder  builder= MultipartEntityBuilder.create();
        for (Map.Entry<String,Object> entry: params.entrySet()){
            builder.addTextBody(entry.getKey(),entry.getValue()+"");
        }
        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(builder.build());
        CloseableHttpResponse response= XHttpClient.CLIENT.execute(httpPost);
        return HttpEntityToString(response.getEntity());
    }

    public static String post(HttpPost httpPost, Map<String, Object> params) throws IOException {
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            builder.addTextBody(entry.getKey(), entry.getValue() + "");
        }
        httpPost.setEntity(builder.build());
        CloseableHttpResponse response = XHttpClient.CLIENT.execute(httpPost);
        return HttpEntityToString(response.getEntity());
    }

    private static String HttpEntityToString(HttpEntity httpEntity) throws IOException {
        try (InputStream inputStream = httpEntity.getContent()) {
            Matcher matcher = pattern.matcher(httpEntity.getContentType());
            String charset = "UTF-8";
            if (matcher.find()) {
                charset = matcher.group(1);
            }
            String htmlContent = IOUtils.toString(inputStream, charset);
            IOUtils.closeQuietly(inputStream);
            return htmlContent;
        }
    }

}
