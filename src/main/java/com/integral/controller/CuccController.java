package com.integral.controller;//Created by xiacheng on 16/10/9.

import com.integral.service.communite.chinaunicom.ChinaUnicom;
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
public class CuccController {
    @Resource
    ChinaUnicom mChinaUnicom;

    @ResponseBody
    @RequestMapping(value = "/request_lt_jf", method = RequestMethod.POST)
    public JfResult requestLTJF(@RequestParam("data") String data) {
        JfResult jfResult = new JfResult();
        try{
            Decrypter decrypter = Decrypter.getInstance();
            JfRequest request = decrypter.decrypt(data);
            jfResult = mChinaUnicom.queryIntegral(request);
        }catch (Exception e){
            jfResult.setMessage("查询联通积分发生异常");
            jfResult.setPoints("-1");
            jfResult.setCode(Constants.ErrorCode.GET_INTEGRAL_FAILED+"");
            jfResult.setReason(e.getMessage());
        }
        return jfResult;
    }
}
