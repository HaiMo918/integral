package com.integral.controller;//Created by xiacheng on 16/10/9.

import com.integral.service.communite.chinatelecom.Dianxin;
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
public class CtccController {
    @Resource
    private Dianxin mChinaTeleCom;

    @ResponseBody
    @RequestMapping(value = "/request_dx_jf", method = RequestMethod.POST)
    public JfResult requestDXJF(@RequestParam("data") String data) {
        JfResult jfResult = new JfResult();
        try {
            Decrypter decrypter = Decrypter.getInstance();
            JfRequest request = decrypter.decrypt(data);
            jfResult=mChinaTeleCom.queryIntegral(request);
        } catch (Exception e) {
            jfResult.setCode(Constants.ErrorCode.OTHER_EXCEPTION+"");
            jfResult.setMessage("查询电信积分发生异常");
            jfResult.setReason(e.getMessage());
            jfResult.setPoints("-1");
        }
        return jfResult;
    }
}
