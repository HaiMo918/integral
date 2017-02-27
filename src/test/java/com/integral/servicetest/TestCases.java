package com.integral.servicetest;

import com.alibaba.fastjson.JSONObject;
import com.integral.tools.Decrypter;
import com.integral.utils.JfRequest;
import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileReader;

/**
 * Created by liuqinghai on 2017/1/5.
 */
public class TestCases {
    public static final String BASE_URL = "http://localhost:8080";


    //浦发银行
    @Test
    public void testRequestVerifyPic() throws Exception {
        JfRequest request = new JfRequest();
        request.setPassword("110425");
        request.setAccount("350125198803130343");
        NameValuePair nameValuePair = new BasicNameValuePair("data", Decrypter.getInstance().encrypt(request));

        String result = Request.Post(BASE_URL + "/request_pf_pic").bodyForm(nameValuePair).execute().returnContent().asString();
        JSONObject jsonObject = JSONObject.parseObject(result);
        String url = jsonObject.getString("data");
        System.out.println(BASE_URL + url);
    }

    @Test
    public void testRequestVerifyCode() throws Exception {
        JfRequest request = new JfRequest();
        request.setPassword("110425");
        request.setAccount("350125198803130343");
        request.setCode("p6sbp4");
        NameValuePair nameValueair = new BasicNameValuePair("data", Decrypter.getInstance().encrypt(request));
        String result = Request.Post(BASE_URL + "/request_pf_code").bodyForm(nameValueair).execute().returnContent().asString();
        System.out.println(result);
    }

    @Test
    public void testQueryIntegral() throws Exception {
        JfRequest request = new JfRequest();
        request.setAccount("350125198803130343");
        request.setCode("mziuds");
        NameValuePair nameValuePair = new BasicNameValuePair("data", Decrypter.getInstance().encrypt(request));
        String result = Request.Post(BASE_URL + "/request_pf_jf").bodyForm(nameValuePair).execute().returnContent().asString();
        System.out.println(result);
    }

    //去哪儿
    @Test
    public void testRequestPic() throws Exception{
        JfRequest request = new JfRequest();
        request.setAccount("13456875762");
        NameValuePair nameValuePair = new BasicNameValuePair("data", Decrypter.getInstance().encrypt(request));
        String res = Request.Post(BASE_URL + "/request_qunar_code").bodyForm(nameValuePair).execute().returnContent().asString();
        JSONObject jsonObject = JSONObject.parseObject(res);
        String url = jsonObject.getString("data");
        System.out.println(jsonObject.getString("id"));
        System.out.println(url);
    }

    @Test
    public void testRequestJf() throws Exception{
        JfRequest request = new JfRequest();
        request.setPassword("lqh_1985t");
        request.setAccount("13456875762");
        request.setCode("189896");

        NameValuePair nameValuePair = new BasicNameValuePair("data", Decrypter.getInstance().encrypt(request));
        String res = Request.Post(BASE_URL + "/query_qunar_jf").bodyForm(nameValuePair).execute().returnContent().asString();
        System.out.println(res);
    }
    //建设银行
    @Test
    public void testRequestSmsCode() throws Exception {
        JfRequest request = new JfRequest();
        request.setPassword("0661");
        request.setAccount("4367 4800 0004 3086");
        NameValuePair nameValuePair = new BasicNameValuePair("data", Decrypter.getInstance().encrypt(request));
        String result = Request.Post(BASE_URL + "/request_jh_code").bodyForm(nameValuePair).execute().returnContent().asString();
        System.out.println(result);
    }

    @Test
    public void testGetScore() throws Exception {
        JfRequest request = new JfRequest();
        request.setPassword("0661");
        request.setAccount("4367 4800 0004 3086");
        request.setCode("222256");
        NameValuePair nameValuePair = new BasicNameValuePair("data", Decrypter.getInstance().encrypt(request));
        String result = Request.Post(BASE_URL + "/request_jh_jf").bodyForm(nameValuePair).execute().returnContent().asString();
        System.out.println(result);
    }
    //饿了么
    @Test
    public void testGetElemePic() throws Exception{
        JfRequest request = new JfRequest();
        request.setAccount("13456875762");
        NameValuePair nameValuePair = new BasicNameValuePair("data", Decrypter.getInstance().encrypt(request));

        String result = Request.Post(BASE_URL + "/request_eleme_pic").bodyForm(nameValuePair).execute().returnContent().asString();
        JSONObject jsonObject = JSONObject.parseObject(result);
        String url = jsonObject.getString("data");
        System.out.println(url);
    }


    @Test
    public void testQueryElemeJf() throws Exception{
        JfRequest request = new JfRequest();
        request.setPassword("lqh_1985t");
        request.setAccount("13456875762");
        request.setCode("56px");
        NameValuePair nameValuePair = new BasicNameValuePair("data", Decrypter.getInstance().encrypt(request));

        String result = Request.Post(BASE_URL + "/request_eleme_jf").bodyForm(nameValuePair).execute().returnContent().asString();
        JSONObject jsonObject = JSONObject.parseObject(result);
        String url = jsonObject.getString("data");
        System.out.println(url);
    }
    //全家
    @Test
    public void testGetQJPicCode() throws Exception{
        JfRequest request = new JfRequest();
        NameValuePair nameValuePair = new BasicNameValuePair("data", Decrypter.getInstance().encrypt(request));

        String result = Request.Post(BASE_URL + "/request_qj_code").bodyForm(nameValuePair).execute().returnContent().asString();
        JSONObject jsonObject = JSONObject.parseObject(result);
        String url = jsonObject.getString("data");
        System.out.println(url);
        String id = jsonObject.getString("id");
        System.out.println(id);
    }

    @Test
    public void tstGetQJPoints() throws Exception{
        JfRequest request = new JfRequest();
        request.setAccount("15618837152");
        request.setPassword("dunianhui672326");
        request.setId("4af5b476-85bc-44c8-a4b6-e41168556d5e");
        request.setCode("ENRR");

        NameValuePair nameValuePair = new BasicNameValuePair("data", Decrypter.getInstance().encrypt(request));
        String result = Request.Post(BASE_URL + "/request_qj_jf").bodyForm(nameValuePair).execute().returnContent().asString();
        JSONObject jsonObject = JSONObject.parseObject(result);
        System.out.println(jsonObject.toJSONString());
    }
    //大众点评
    @Test
    public void testDianpingCode() throws Exception{
        JfRequest request = new JfRequest();
        request.setAccount("15202823217");
        NameValuePair nameValuePair = new BasicNameValuePair("data", Decrypter.getInstance().encrypt(request));

        String result = Request.Post(BASE_URL + "/request_dp_code").bodyForm(nameValuePair).execute().returnContent().asString();
        JSONObject jsonObject = JSONObject.parseObject(result);
        String url = jsonObject.getString("data");
        System.out.println(url);
        String id = jsonObject.getString("id");
        System.out.println(id);
    }

    @Test
    public void testDianpingPoints() throws Exception{
        JfRequest request = new JfRequest();
        request.setAccount("15202823217");
        request.setPassword("968492");
        request.setId("15202823217");
        NameValuePair nameValuePair = new BasicNameValuePair("data", Decrypter.getInstance().encrypt(request));
        String result = Request.Post(BASE_URL + "/request_dp_jf").bodyForm(nameValuePair).execute().returnContent().asString();
        JSONObject jsonObject = JSONObject.parseObject(result);
        System.out.println(jsonObject.toJSONString());
    }

    //美团
    @Test
    public void testGetMeituanCode() throws Exception{
        JfRequest request = new JfRequest();
        NameValuePair nameValuePair = new BasicNameValuePair("data", Decrypter.getInstance().encrypt(request));
        String result = Request.Post(BASE_URL + "/request_mt_code").bodyForm(nameValuePair).execute().returnContent().asString();
        JSONObject jsonObject = JSONObject.parseObject(result);
        System.out.println(jsonObject.toJSONString());
    }
    @Test
    public void testGetMeituanPoints() throws Exception{
        JfRequest request = new JfRequest();
        request.setAccount("18621793235");
        request.setPassword("liu123");
        request.setId("46c242a8-3d29-4648-a3b8-116bb9476177");
        request.setCode("8fam");
        NameValuePair nameValuePair = new BasicNameValuePair("data", Decrypter.getInstance().encrypt(request));
        String result = Request.Post(BASE_URL + "/request_mt_jf").bodyForm(nameValuePair).execute().returnContent().asString();
        JSONObject jsonObject = JSONObject.parseObject(result);
        System.out.println(jsonObject.toJSONString());
    }

    @Test
    public void testDianxin() throws Exception{
        JfRequest request = new JfRequest();
        request.setAccount("18121100508");
        request.setPassword("081341");
        NameValuePair nameValuePair = new BasicNameValuePair("data", Decrypter.getInstance().encrypt(request));
        String result = Request.Post(BASE_URL + "/request_dx_jf").bodyForm(nameValuePair).execute().returnContent().asString();
        JSONObject jsonObject = JSONObject.parseObject(result);
        System.out.println(jsonObject.toJSONString());
    }

    @Test
    public void test360jifen() throws Exception{
        JfRequest request = new JfRequest();
        request.setAccount("testjifen");
        request.setPassword("wgy123");
        NameValuePair valuePair = new BasicNameValuePair("data",Decrypter.getInstance().encrypt(request));
        String result = Request.Post(BASE_URL+"/query_jf360_jf").bodyForm(valuePair).execute().returnContent().asString();
        JSONObject object = JSONObject.parseObject(result);
        System.out.println(object.toJSONString());
    }

    @Test
    public void testMIUIjifen() throws Exception{
        JfRequest request = new JfRequest();
        request.setAccount("13366183868");
        request.setPassword("666xiaomi");
        NameValuePair valuePair = new BasicNameValuePair("data",Decrypter.getInstance().encrypt(request));
        String result = Request.Post(BASE_URL+"/query_miui_jf").bodyForm(valuePair).execute().returnContent().asString();
        JSONObject object = JSONObject.parseObject(result);
        System.out.println(object.toJSONString());
    }
}
