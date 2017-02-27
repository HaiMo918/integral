package com.integral.controller;//Created by xiacheng on 16/10/9.

import com.integral.service.airline.eastairline.EastAirline;
import com.integral.tools.Decrypter;
import com.integral.utils.JfRequest;
import com.integral.utils.JfResult;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Controller
public class EastAirLineController {
    @Resource
    EastAirline mEastAirLine;

    @ResponseBody
    @RequestMapping(value = "/request_dh_code", method = RequestMethod.POST)
    public JfResult requestDHCode() {
        JfResult jfResult = new JfResult();
        try {
            jfResult = mEastAirLine.requestVerifyCode(null);
        } catch (Exception e) {
            jfResult.setMessage("获取东航验证码发生异常");
            jfResult.setReason(e.getMessage());
            jfResult.setPoints("-1");
        }
        return jfResult;
    }

    @ResponseBody
    @RequestMapping(value = "/request_dh_jf", method = RequestMethod.POST)
    public JfResult requestDHJF(@RequestParam("data") String data) {
        JfResult jfResult = new JfResult();
        try {
            Decrypter decrypter = Decrypter.getInstance();
            JfRequest request = decrypter.decrypt(data);
            jfResult = mEastAirLine.queryIntegral(request);
        } catch (Exception e) {
            jfResult.setMessage("获取东航积分发生异常");
            jfResult.setReason(e.getMessage());
            jfResult.setPoints("-1");
        }
        return jfResult;
    }
}
