package com.integral.controller;//Created by xiacheng on 16/10/9.

import com.integral.service.airline.juneyair.JuneyAir;
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
public class JuneyAirController {
    @Resource
    JuneyAir mJuneyAir;

    @Deprecated
    @ResponseBody
    @RequestMapping(value = "/request_jx_code",method = RequestMethod.POST)
    public JfResult requestJuneyCode(){
        JfResult jfResult = new JfResult();
        try{
            jfResult = mJuneyAir.requestVerifyCode(null);
        }catch (Exception e){
            jfResult.setMessage("获取吉祥航空验证码发生异常");
            jfResult.setReason(e.getMessage());
            jfResult.setPoints("-1");
        }
        return jfResult;
    }

    @ResponseBody
    @RequestMapping(value = "/request_jx_jf",method = RequestMethod.POST)
    public JfResult requestJuneyJF(@RequestParam("data") String data){
        JfResult jfResult = new JfResult();
        try{
            Decrypter decrypter = Decrypter.getInstance();
            JfRequest request = decrypter.decrypt(data);
            jfResult = mJuneyAir.queryIntegral(request);
        }catch (Exception e){
            jfResult.setMessage("获取吉祥航空积分发生异常");
            jfResult.setReason(e.getMessage());
            jfResult.setPoints("-1");
        }
        return jfResult;
    }
}
