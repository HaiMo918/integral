package com.integral.controller;

import com.integral.service.qunar.QunarService;
import com.integral.tools.Decrypter;
import com.integral.utils.JfRequest;
import com.integral.utils.JfResult;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * Created by liuqinghai on 2016/12/12.
 */
@Controller
public class QunarController {
    @Resource
    private QunarService mQunarService;

    @ResponseBody
    @RequestMapping(value = "/request_qunar_code",method = RequestMethod.POST)
    public JfResult requestQunarCode(@RequestParam("data") String data){
        JfResult result = new JfResult();
        try{
            result=mQunarService.requestVerifyCode(Decrypter.getInstance().decrypt(data));
        }catch (Exception e){
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/query_qunar_jf",method = RequestMethod.POST)
    public JfResult requestQunarJf(@RequestParam("data") String data){
        JfResult result = new JfResult();
        try{
            result=mQunarService.queryIntegral(Decrypter.getInstance().decrypt(data));
        }catch (Exception e){
            result.setMessage(e.getMessage());
        }
        return result;
    }
}
