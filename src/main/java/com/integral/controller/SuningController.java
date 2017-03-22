package com.integral.controller;

import com.integral.service.suning.SuningService;
import com.integral.tools.Decrypter;
import com.integral.utils.JfResult;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * Created by liuqinghai on 2017/3/22.
 */
@Controller
public class SuningController {
    @Resource
    SuningService mSuningService;

    @ResponseBody
    @RequestMapping(value = "/request_suning_code")
    public JfResult requestSuningVerifyCode(@RequestParam("data")String data){
        JfResult result = null;
        try{
            result = mSuningService.requestVerifyCode(Decrypter.getInstance().decrypt(data));
        }catch (Exception e){

        }
        return result;
    }

    @RequestMapping(value = "/request_suning_jf",method = RequestMethod.POST)
    public JfResult querySuningPoints(@RequestParam("data")String data){
        JfResult result = null;
        try{
            result = mSuningService.queryIntegral(Decrypter.getInstance().decrypt(data));
        }catch (Exception e){

        }
        return result;
    }
}
