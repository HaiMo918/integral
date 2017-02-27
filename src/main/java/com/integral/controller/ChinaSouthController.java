package com.integral.controller;//Created by xiacheng on 16/10/9.

import com.integral.service.airline.chinasouth.ChinaSouth;
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
public class ChinaSouthController {
    @Resource
    ChinaSouth mChinaSouth;

    @ResponseBody
    @RequestMapping(value = "/request_nh_code", method = RequestMethod.POST)
    public JfResult requestNHCode() {
        return null;
    }

    @ResponseBody
    @RequestMapping(value = "/request_nh_jf", method = RequestMethod.POST)
    public JfResult requestNHJF(@RequestParam("data") String data) {
        JfResult jfResult = new JfResult();
        try{
            Decrypter decrypter = Decrypter.getInstance();
            JfRequest request = decrypter.decrypt(data);
            jfResult = mChinaSouth.queryIntegral(request);
        }catch (Exception e){
            jfResult.setMessage("南航积分查询发生异常");
            jfResult.setReason(e.getMessage());
            jfResult.setPoints("-1");
        }
        return jfResult;
    }
}
