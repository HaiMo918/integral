package com.integral.servicetest;

import com.alibaba.fastjson.JSONObject;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.integral.tools.Decrypter;
import com.integral.tools.FileUtils;
import com.integral.utils.*;
import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.client5.http.methods.HttpGet;
import org.apache.hc.client5.http.methods.HttpPost;
import org.apache.hc.client5.http.methods.HttpRequestBase;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * Created by jianfeng on 2016/11/1.
 */
@Service
public class PuFaServer implements IQueryIntegral {

    private static Cache<String, Map<String, Object>> paramsCache = CacheBuilder.newBuilder()
            .maximumSize(10000)
            .expireAfterAccess(15, TimeUnit.MINUTES)//多久时间没被访问就清除掉
            .build();

    public JfResult requestVerifyPic(JfRequest request) throws Exception {
        String card = request.getAccount();

        XCookieStore.threadLocal.set(card);
        XCookieStore.getCookieSet().clear();
        /**
         * 请求页面
         */
        String url = "https://cardsonline.spdbccc.com.cn/icard/icardlogin.do?_locale=zh_CN";
        HttpGet httpGet = new HttpGet(url);
        addHead(httpGet, "");

        String htmlContent = XHttpClient.get(httpGet);
        Document doc = Jsoup.parse(htmlContent);

        httpGet = new HttpGet("https://cardsonline.spdbccc.com.cn/icard/templates/blank.html");
        addHead(httpGet, url);
        XHttpClient.get(httpGet);//

        /**
         * 获取验证码图片路径
         */
        Elements elements = doc.select("#CaptchaImg");
        String img = "https://cardsonline.spdbccc.com.cn" + elements.get(0).attr("src");

        /**
         * 获取下一次调用需要的参数，放入缓存中
         */
        Map<String, Object> formParams = getHitInputFromForm(doc.select("#form1").first());
        paramsCache.put(card, formParams);

        JfResult result = new JfResult();
        result.setCode(Constants.ErrorCode.ERROR_SUCCESS.toString());
        result.setData("/forward?url=" + img + "&user=" + card);
        return result;
    }

    @Override
    public JfResult requestVerifyCode(JfRequest request) throws Exception {
        JfResult result = new JfResult();
        String card = request.getAccount();
        String password = request.getPassword();
        String code = request.getCode();//
        Map<String, Object> params = paramsCache.getIfPresent(card);
        params.put("Token", code);
        params.put("navigator", "msie");
        params.put("IdType", "01");
        params.put("Passwordkeytype", "IcardPublicKey");
        params.put("IdNo", card);
        params.put("Password", password);

        XCookieStore.threadLocal.set(card);
        /**
         * 请求页面
         */
        String url = "https://cardsonline.spdbccc.com.cn/icard/login.do";

        HttpPost httpPost = new HttpPost(url);
        addHead(httpPost, "https://cardsonline.spdbccc.com.cn/icard/icardlogin.do?_locale=zh_CN");

        String htmlContent = XHttpClient.post(httpPost, params);

        HttpGet httpGet = new HttpGet("https://cardsonline.spdbccc.com.cn/icard/templates/blank.html");
        addHead(httpGet, url);
        XHttpClient.get(httpGet);//

        if (htmlContent.contains("校验码输入不正确")) {
            result.setCode(Constants.ErrorCode.OTHER_EXCEPTION + "");
            result.setMessage("校验码输入不正确");
        } else if (htmlContent.contains("目前我们无法完成您的请求")) {
            result.setCode(Constants.ErrorCode.OTHER_EXCEPTION + "");
            result.setMessage("请求无法完成");
        } else if (htmlContent.contains("手机动态密码")) {
            result.setCode(Constants.ErrorCode.ERROR_SUCCESS + "");
            result.setMessage("验证码下发成功.");

            Map<String, Object> formParams = getHitInputFromForm(Jsoup.parse(htmlContent).select("#form1").first());
            paramsCache.put(card, formParams);
        } else {
            result.setCode(Constants.ErrorCode.OTHER_EXCEPTION + "");
            result.setMessage("未知错误");
        }

        return result;
    }

    @Override
    public JfResult queryIntegral(JfRequest request) throws Exception {
        JfResult result = new JfResult();
        String card = request.getAccount();
        String mobilePasswd = request.getCode();
        XCookieStore.threadLocal.set(card);

        Map<String, Object> params = paramsCache.getIfPresent(card);
        params.put("MobilePasswd", mobilePasswd);

        String checkMobilePwdUrl = "https://cardsonline.spdbccc.com.cn/icard/checkMobilePwd.do";
        HttpPost httpPost = new HttpPost(checkMobilePwdUrl);
        addHead(httpPost, "https://cardsonline.spdbccc.com.cn/icard/login.do");
        String htmlContent = XHttpClient.post(httpPost, params);

        if (htmlContent.contains("动态密码输入错误")) {
            result.setCode(Constants.ErrorCode.OTHER_EXCEPTION + "");
            result.setMessage("动态密码输入错误");
        } else if (htmlContent.contains("页面已失效，请重新登录")) {
            result.setCode(Constants.ErrorCode.OTHER_EXCEPTION + "");
            result.setMessage("页面已失效，请重新登录");
        } else {
            Document doc = Jsoup.parse(htmlContent);

            params = getHitInputFromForm(doc.select("form[name='menuForm']").first());
            params.put("_viewReferer", "defaultError");
            params.put("SelectedMenuId", "menu6_1_1_0");

            String rewardPointsQueryUrl = "https://cardsonline.spdbccc.com.cn/icard/rewardPointsQuery.do";
            httpPost = new HttpPost(rewardPointsQueryUrl);
            addHead(httpPost, checkMobilePwdUrl);
            htmlContent = XHttpClient.post(httpPost, params);
            doc = Jsoup.parse(htmlContent);
            String score = doc.select(".td20ce").get(3).text();

            result.setCode(Constants.ErrorCode.ERROR_SUCCESS + "");
            result.setPoints(score);
        }

        return result;
    }

    private static Map<String, Object> getHitInputFromForm(Element element) {
        Elements hiddens = element.select("input[type='hidden']");
        Map<String, Object> map = new HashMap<>();
        for (int i = 0; i < hiddens.size(); i++) {
            Element input = hiddens.get(i);
            map.put(input.attr("name"), input.attr("value"));
        }
        return map;
    }

    private static void addHead(HttpRequestBase requestBase, String referer) {
        requestBase.addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36");
        requestBase.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        requestBase.addHeader("Accept-Encoding", "gzip, deflate, sdch, br");
        requestBase.addHeader("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6");
        requestBase.addHeader("Cache-Control", "max-age=0");
        requestBase.addHeader("Connection", "keep-alive");
        requestBase.addHeader("Referer", referer);
    }

    public static void main(String[] args) throws Exception {
        //final String base_url="http://114.55.133.156:8080/test";
        final String BASE_URL="http://localhost:8080";
        while (true) {
            JfRequest request = new JfRequest();
            request.setPassword("110425");
            request.setAccount("350125198803130343");
            NameValuePair nameValuePair = new BasicNameValuePair("data", Decrypter.getInstance().encrypt(request));

            String result = Request.Post(BASE_URL + "/request_pf_pic").bodyForm(nameValuePair).execute().returnContent().asString();
            JSONObject jsonObject = JSONObject.parseObject(result);
            String url = jsonObject.getString("data");
            System.out.println(BASE_URL + url);

            String thePic = BASE_URL + url;
            File file = FileUtils.getTmpFile("1.jpg");
            Request.Get(thePic).execute().saveContent(file);

            Desktop.getDesktop().open(file);//打开文件
            Desktop.getDesktop().browse(new URI(thePic));//直接用默认浏览器打开图片验证码 2种方式都可以


            System.out.println("请输入验证码：");
            Scanner scanner = new Scanner(System.in);
            String code = scanner.nextLine();

            JfRequest request1 = new JfRequest();
            request.setPassword("110425");
            request.setAccount("350125198803130343");
            request.setCode(code);
            NameValuePair nameValueair = new BasicNameValuePair("data", Decrypter.getInstance().encrypt(request1));
            String result1 = Request.Post(BASE_URL + "/request_pf_code").bodyForm(nameValueair).execute().returnContent().asString();
            System.out.println(result1);

            System.out.println("请输入动态验证码：");
            Scanner scanner1 = new Scanner(System.in);
            String code1 = scanner1.nextLine();

            JfRequest request2 = new JfRequest();
            request.setAccount("350125198803130343");
            request.setCode(code1);
            NameValuePair nameValuePair1 = new BasicNameValuePair("data", Decrypter.getInstance().encrypt(request2));
            String result2 = Request.Post(BASE_URL + "/request_pf_jf").bodyForm(nameValuePair1).execute().returnContent().asString();
            System.out.println(result2);
        }
    }

}
