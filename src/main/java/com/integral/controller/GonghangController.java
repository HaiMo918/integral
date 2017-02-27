package com.integral.controller;

import com.integral.service.creditcard.gonghang.GonghangService;
import com.integral.tools.Decrypter;
import com.integral.utils.JfResult;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * Created by liuqinghai on 2016/11/28.
 */
@Controller
public class GonghangController {
    @Resource
    GonghangService mGonghang;

    @ResponseBody
    @RequestMapping(value = "/request_gh_code")
    public JfResult requestGonghangCode(@RequestParam("data") String data){
        JfResult result = new JfResult();
        try{
            result = mGonghang.requestVerifyCode(Decrypter.getInstance().decrypt(data));
        }catch (Exception e){
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/request_gh_jf")
    public JfResult requestGonghangPoints(@RequestParam("data") String data){
        JfResult result = new JfResult();
        try{
            result = mGonghang.queryIntegral(Decrypter.getInstance().decrypt(data));
        }catch (Exception e){
            result.setMessage(e.getMessage());
            result.setPoints("-1");
        }
        return result;
    }
}
