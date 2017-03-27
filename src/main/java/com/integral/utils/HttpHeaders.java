package com.integral.utils;

import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.methods.HttpRequestBase;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by kris on 2017/3/27.
 */
public class HttpHeaders {
    public static final String USER_AGENT = "User-Agent";
    public static final String COOKIE = "Cookie";
    public static final String ACCEPT = "Accept";
    public static final String ACCEPT_LANGUAGE = "Accept-Language";
    public static final String ACCEPT_ENCODING = "Accept-Encoding";
    public static final String REFERER = "Referer";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String CONTENT_LENGTH = "Content-Length";
    public static final String ORIGIN = "Origin";
    public static final String CONNECTION = "Connection";
    public static final String HOST = "Host";
    public static final String CONTENT_TYPE_XFORMED = "application/x-www-form-urlencoded; charset=UTF-8";

    public static final List<String> defaultAgentList = new ArrayList<String>() {
        {
            add("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36");
            add("Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; rv:11.0) like Gecko");
        }
    };


    public static String getADefaultUserAgent() {
        int size = defaultAgentList.size();
        Random rand = new Random();
        int randNum = rand.nextInt(size);
        return defaultAgentList.get(randNum);
    }

    public static String defaultAgent = getADefaultUserAgent();

    public static synchronized void addGetHttpHeaders(HttpRequestBase base,
                                                      String accept,
                                                      boolean followInstance
    ) {
        base.addHeader(USER_AGENT, defaultAgent);
        base.addHeader(ACCEPT, accept);

        RequestConfig.Builder builder = RequestConfig.custom();
        builder.setRedirectsEnabled(followInstance);
        base.setConfig(builder.build());
    }

    public static synchronized void addPostHttpHeaders(HttpRequestBase base,
                                                       String accept,
                                                       boolean followInstance,
                                                       String cookie,
                                                       String type,
                                                       String len
    ) {
        base.addHeader(USER_AGENT, defaultAgent);
        base.addHeader(ACCEPT, accept);
        base.addHeader(COOKIE,cookie);
        base.addHeader(CONTENT_TYPE,type);
        base.addHeader(CONTENT_LENGTH,len);
        RequestConfig.Builder builder = RequestConfig.custom();
        builder.setRedirectsEnabled(followInstance);
        base.setConfig(builder.build());
    }

}
