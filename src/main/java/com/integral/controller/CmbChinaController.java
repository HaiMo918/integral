package com.integral.controller;//Created by xiacheng on 16/10/9.

import com.integral.service.cmbchina.CmbChina;
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
public class CmbChinaController {
    @Resource
    CmbChina cmbChina;

    @ResponseBody
    @RequestMapping(value = "/request_cmb_code", method = RequestMethod.POST)
    public JfResult requestCmbCode(){
        JfResult jfResult = new JfResult();
        try{
            jfResult=cmbChina.requestVerifyCode(null);
        }catch (Exception e){
            jfResult.setMessage("获取招行验证码发生异常");
            jfResult.setReason(e.getMessage());
            jfResult.setPoints("-1");
        }
        return jfResult;
    }

    @ResponseBody
    @RequestMapping(value = "/request_cmb_points",method = RequestMethod.POST)
    public JfResult queryCmbPoints(@RequestParam String data){
        JfResult jfResult = new JfResult();
        try{
            Decrypter decrypter = Decrypter.getInstance();
            JfRequest request = decrypter.decrypt(data);
            jfResult=cmbChina.queryIntegral(request);
        }catch (Exception e){
            jfResult.setMessage("获取招行积分发生异常");
            jfResult.setReason(e.getMessage());
            jfResult.setPoints("-1");
        }
        return jfResult;
    }
}
