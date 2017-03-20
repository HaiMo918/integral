package com.integral.controller;

import com.integral.service.miui.MiuiService;
import com.integral.tools.Decrypter;
import com.integral.utils.JfResult;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * Created by liuqinghai on 2017/1/22.
 */
@Controller
public class MiuiController {
    @Resource
    MiuiService miuiService;

    @ResponseBody
    @RequestMapping(value = "/query_miui_jf",method = RequestMethod.POST)
    public JfResult queryPoints(@RequestParam("data")String data){
        JfResult result = new JfResult();
        try{
            result=miuiService.queryIntegral(Decrypter.getInstance().decrypt(data));
        }catch (Exception e){
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/request_miui_pic",method = RequestMethod.POST)
    public JfResult queryPic(@RequestParam("data")String data){
        JfResult result = new JfResult();
        try{
            result=miuiService.requestVerifyCode(Decrypter.getInstance().decrypt(data));
        }catch (Exception e){
            result.setMessage(e.getMessage());
        }
        return result;
    }
}
