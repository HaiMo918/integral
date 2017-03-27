package com.integral.service.lvmama;

import com.alibaba.fastjson.JSONObject;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.integral.utils.*;
import org.apache.hc.client5.http.methods.HttpGet;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import javax.net.ssl.HttpsURLConnection;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by kris on 2017/1/13.
 * 18516318026
 * wgy12345
 */
@Service
public class Lvmama implements IQueryIntegral{
    private static Cache<String, Map<String, String>> paramsCache = CacheBuilder.newBuilder().maximumSize(10000).expireAfterAccess(15, TimeUnit.MINUTES).build();

    @Override
    public JfResult requestVerifyCode(JfRequest request) throws Exception {
        JfResult result = new JfResult();
        String phone = request.getAccount();

        XCookieStore.threadLocal.set(phone);
        XCookieStore.getCookieSet().clear();

        //打开登录页
        Map<String,String> ltToken = new HashMap<>();
        HttpGet httpGet = new HttpGet("https://login.lvmama.com/nsso/login?service=http%3A%2F%2Fwww.lvmama.com%2F");
        HttpHeaders.addGetHttpHeaders(httpGet,"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8",false);
        String html = XHttpClient.get(httpGet);
        Document doc = Jsoup.parse(html);
        Elements lte = doc.select("input[name=lt]");
        String lt = lte.get(0).attr("value");
        ltToken.put("lt",lt);
        Elements tokene = doc.select("input[name=token]");
        String token = tokene.get(0).attr("value");
        ltToken.put("token",token);
        paramsCache.put(phone,ltToken);
        result.setData("https://login.lvmama.com/captcha/account/checkcode/login_web.htm?secureLevel=primary");
        result.setData1("https://login.lvmama.com/captcha/account/checkcode/login.htm");
        return result;
    }

    @Override
    public JfResult queryIntegral(JfRequest request) throws Exception {
        JfResult result = new JfResult();
        return result;
    }

    public static void main(String[] args) throws Exception{
        Lvmama lvmama = new Lvmama();
        JfRequest request = new JfRequest();
        request.setAccount("18516318026");
        lvmama.requestVerifyCode(request);
    }
}
