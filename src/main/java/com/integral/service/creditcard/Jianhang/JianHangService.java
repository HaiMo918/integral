package com.integral.service.creditcard.Jianhang;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.integral.utils.*;
import org.apache.hc.client5.http.methods.HttpGet;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by xiongzicheng on 2016/11/1.
 */
@Service
public class JianHangService implements IQueryIntegral {

    @Override
    public JfResult requestVerifyCode(JfRequest request) throws Exception {
        String card = request.getAccount();
        String phone = request.getPassword();
        XCookieStore.threadLocal.set(card);

        String htmlContent = XHttpClient.get(new HttpGet("http://jf.ccb.com/exchangecenter/account/viewscore.jhtml"));

        Elements elements = Jsoup.parse(htmlContent).select("input[name='accessCode']");
        String accessCode = elements.get(0).val();

        Map<String, Object> params = new HashMap<>();
        params.put("accoumt", card);
        params.put("mobilelastNum", phone);
        params.put("accessCode", accessCode);
        params.put("state", 1);

        String codeResult = XHttpClient.post("http://jf.ccb.com/exchangecenter/account/viewScoreVerifyCode.jhtml", params);
        JSONObject jsonObject = JSON.parseObject(codeResult);
        int code = jsonObject.getIntValue("code");
        JfResult result = new JfResult();
        if (code == 0) {
            result.setCode(Constants.ErrorCode.ERROR_SUCCESS + "");
        } else {
            result.setCode(Constants.ErrorCode.SEND_SMS_FAILED + "");
            result.setMessage(jsonObject.getString("message"));
        }
        return result;
    }

    @Override
    public JfResult queryIntegral(JfRequest request) throws Exception {
        String card = request.getAccount();
        String phone = request.getPassword();
        String code = request.getCode();
        XCookieStore.threadLocal.set(card);
        String viewScoreUrl = "http://jf.ccb.com/exchangecenter/account/viewScoreResult.jhtml";
        Map<String, Object> viewScoreParams = new HashMap<>();
        viewScoreParams.put("hidden_mcode", code);
        viewScoreParams.put("hidden_bankCard", card);
        viewScoreParams.put("mobilelastNum", phone);
        String viewScoreResult = XHttpClient.post(viewScoreUrl, viewScoreParams);
        String score = Jsoup.parse(viewScoreResult).select("#score1_0100000000").get(0).text();
        JfResult result = new JfResult();
        result.setCode(Constants.ErrorCode.ERROR_SUCCESS + "");
        result.setPoints(score);
        return result;
    }
}
