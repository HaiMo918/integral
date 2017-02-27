package com.integral.controller;

import com.integral.service.eleme.ElemeService;
import com.integral.tools.Decrypter;
import com.integral.utils.JfResult;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * Created by liuqinghai on 2017/1/3.
 */
@Controller
public class ElemeController {
    @Resource
    ElemeService elemeService;

    @ResponseBody
    @RequestMapping(value = "/request_eleme_pic",method = RequestMethod.POST)
    public JfResult queryElemePic(){
        JfResult result=new JfResult();
        try{
            result = elemeService.requestVerifyCode(null);
        }catch (Exception e){
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/request_eleme_jf",method = RequestMethod.POST)
    public JfResult queryElemeJf(@RequestParam("data")String data){
        JfResult result=new JfResult();
        try{
            result = elemeService.queryIntegral(Decrypter.getInstance().decrypt(data));
        }catch (Exception e){
            result.setMessage(e.getMessage());
        }
        return result;
    }
}
