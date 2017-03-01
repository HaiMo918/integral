package com.integral.service.jf360;

import com.integral.tools.MD5;
import com.integral.utils.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.cookie.CookieStore;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.sync.CloseableHttpClient;
import org.apache.hc.client5.http.impl.sync.HttpClients;
import org.apache.hc.client5.http.methods.CloseableHttpResponse;
import org.apache.hc.client5.http.methods.HttpGet;
import org.apache.hc.client5.http.methods.HttpPost;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.entity.EntityUtils;
import org.apache.hc.core5.http.message.BasicNameValuePair;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by liuqinghai on 2017/1/16.
 */
@Service
public class Jf360Service implements IQueryIntegral{

    @Override
    public JfResult requestVerifyCode(JfRequest request) throws Exception {
        String userName=request.getAccount();
        HttpGet httpGet = new HttpGet("http://login.360.cn/?callback=jQuery"+System.currentTimeMillis()+"&src=pcw_i360&from=pcw_i360&charset=UTF-8&requestScema=http&o=sso&m=checkNeedCaptcha&account="+userName+"&captchaApp=i360&_="+System.currentTimeMillis());
        String text = XHttpClient.get(httpGet);
        if(StringUtils.contains(text, "\"captchaFlag\":true")){
            String url= getJsonValue(text,"captchaUrl").replace("\\/","/");
            JfResult result=new JfResult();
            result.setData(url);
            return result;
        }
        return null;
    }

    @Override
    public JfResult queryIntegral(JfRequest request) throws Exception {
        String userName=request.getAccount();
        String pwd=request.getPassword();
        String token = getToken(userName);
        String code=request.getCode();
        Map<String, String> params=new HashMap<>();
        params.put("src","pcw_home");
        params.put("from","pcw_home");
        params.put("charset","UTF-8");
        params.put("requestScema","https");
        params.put("o","sso");
        params.put("m","login");
        params.put("lm","0");
        params.put("captFlag","1");
        params.put("rtype","data");
        params.put("validatelm","0");
        params.put("isKeepAlive","1");
        params.put("captchaApp","i360");
        params.put("userName",userName);
        params.put("type","normal");
        params.put("account",userName);
        MD5 md5=new MD5();
        params.put("password", md5.bytesToMD5(pwd.getBytes()).toLowerCase());
        params.put("captcha",code);
        params.put("token",token);
        params.put("proxy","http://i.360.cn/psp_jump.html");
        params.put("callback","QiUserJsonp"+System.currentTimeMillis());
        params.put("func","QiUserJsonp"+System.currentTimeMillis());

        // 创建http POST请求
        HttpPost httpPost = new HttpPost("https://login.360.cn/");
//        httpPost.setConfig(this.requestConfig);
        if (params != null) {
            // 设置2个post参数，一个是scope、一个是q
            List<BasicNameValuePair> parameters = new ArrayList<>();
            for (String key : params.keySet()) {
                parameters.add(new BasicNameValuePair(key, params.get(key)));
            }
            // 构造一个form表单式的实体
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(parameters, Charset.forName("utf-8"));
            formEntity.setContentType("application/x-www-form-urlencoded");
            // 将请求实体设置到httpPost对象中
            httpPost.setEntity(formEntity);
        }
        httpPost.addHeader("Host","login.360.cn");
        httpPost.addHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64; rv:51.0) Gecko/20100101 Firefox/51.0");
        httpPost.addHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        httpPost.addHeader("Accept-Language","zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
        httpPost.addHeader("Accept-Encoding","gzip, deflate, br");
        httpPost.addHeader("Referer","http://i.360.cn/login/?src=pcw_home&destUrl=http%3A%2F%2Fwww.360.com%2F");
        httpPost.addHeader("Connection","keep-alive");
        httpPost.addHeader("Upgrade-Insecure-Requests","1");
        CookieStore cookieStore = new BasicCookieStore();
        CloseableHttpClient httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
        CloseableHttpResponse response = null;
        HttpClientContext context = null;
        context = HttpClientContext.create();
        context.setCookieStore(cookieStore);
        response = httpClient.execute(httpPost,context);
        String text = EntityUtils.toString(response.getEntity(), Charset.forName("UTF-8"));
        String regLoginMsg="errmsg=([^&]+)";
        Pattern patternLogin = Pattern.compile(regLoginMsg);
        // 忽略大小写的写法
        Matcher matcherLogin = patternLogin.matcher(text);
        String logonErr=null;
        if (matcherLogin.find()){
            logonErr= matcherLogin.group(1);
            String msg = URLDecoder.decode(logonErr, "utf-8");
            if(StringUtils.isNotBlank(msg)){
                JfResult result=new JfResult();
                result.setReason(msg);
                return result;
            }
        }

        //获取积分
        HttpGet httpGet = new HttpGet("http://jifen.360.cn/index/orderlist");
        HttpResponse httpResponse =  httpClient.execute(httpGet,context);
        String jfPage = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
        String regEx="<div class=\"tit\">我的积分余额：<span class=\"textb\">([\\d]+)</span>";
        Pattern pattern = Pattern.compile(regEx);
        // 忽略大小写的写法
        Matcher matcher = pattern.matcher(jfPage);
        String jifen=null;
        if (matcher.find()){
            jifen= matcher.group(1);
        }
        JfResult result=new JfResult();
        result.setPoints(jifen);
        return result;
    }

    private String getToken(String userName) throws IOException {
        HttpGet httpGet = new HttpGet("https://login.360.cn/?func=jQuery"+System.currentTimeMillis()+"&src=pcw_i360&from=pcw_i360&charset=UTF-8&requestScema=https&o=sso&m=getToken&userName="+userName+"&_="+System.currentTimeMillis());
        String text = XHttpClient.get(httpGet);
        return getJsonValue(text,"token");
    }
    public static String getJsonValue(String text,String key){
        String regEx="\""+key+"\":\"([^\"]*)\"";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()){
            return matcher.group(1);
        }
        return "";
    }

    public static void main(String[] args) throws Exception {
        Jf360Service jf360Service=new Jf360Service();
        JfRequest request=new JfRequest();
        request.setAccount("testjifen");
        request.setPassword("wgy123");
//        JfResult result = jf360Service.requestVerifyCode(request);
//        if(result!=null){
//            System.out.println(result.getData());
//            Scanner scanner = new Scanner(System.in);
//            String captcha = scanner.next();
//            request.setCode(captcha);
//        }
        JfResult jfResult = jf360Service.queryIntegral(request);
        System.out.println("积分："+jfResult.getPoints()+" 错误信息:"+jfResult.getReason());
    }
}
