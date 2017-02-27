package com.integral.controller;//Created by xiacheng on 16/10/9.

import com.integral.service.communite.chinamobile.ChinaMobile;
import com.integral.tools.Decrypter;
import com.integral.utils.Constants;
import com.integral.utils.JfRequest;
import com.integral.utils.JfResult;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Controller
public class CmccController {
    @Resource
    ChinaMobile mChinaMobile;

    @ResponseBody
    @RequestMapping(value = "/request_yd_code", method = RequestMethod.POST)
    public JfResult requestYDVerifyCode(@RequestParam("data") String data) {
        JfResult result = new JfResult();
        try {
            Decrypter decrypter = Decrypter.getInstance();
            JfRequest request = decrypter.decrypt(data);
            result = mChinaMobile.requestVerifyCode(request);
        }catch (Exception e){
            result.setCode(Constants.ErrorCode.OTHER_EXCEPTION+"");
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/request_yd_jf", method = RequestMethod.POST)
    public JfResult requestYDJF(@RequestParam("data") String data) {
        JfResult result = new JfResult();
        try{
            Decrypter decrypter = Decrypter.getInstance();
            JfRequest request = decrypter.decrypt(data);
            result = mChinaMobile.queryIntegral(request);
        }catch (Exception e){
            result.setCode(Constants.ErrorCode.OTHER_EXCEPTION+"");
            result.setMessage(e.getMessage());
            result.setPoints("-1");
        }
        return result;
    }
}
