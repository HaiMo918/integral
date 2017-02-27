package com.integral.controller;

import com.integral.service.dianping.DianpingService;
import com.integral.tools.Decrypter;
import com.integral.utils.JfResult;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * Created by kris on 2017/1/3.
 * 大众点评
 */
@Controller
public class DianpingController {
    @Resource
    DianpingService dianpingService;

    @ResponseBody
    @RequestMapping(value = "/request_dp_code",method = RequestMethod.POST)
    public JfResult requestDianpingMessage(@RequestParam("data") String data){
        JfResult result = new JfResult();
        try{
            result=dianpingService.requestVerifyCode(Decrypter.getInstance().decrypt(data));
        }catch (Exception e){
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/request_dp_jf",method = RequestMethod.POST)
    public JfResult requestDianpingPoints(@RequestParam("data") String data){
        JfResult result = new JfResult();
        try{
            result=dianpingService.queryIntegral(Decrypter.getInstance().decrypt(data));
        }catch (Exception e){
            result.setMessage(e.getMessage());
        }
        return result;
    }
}
