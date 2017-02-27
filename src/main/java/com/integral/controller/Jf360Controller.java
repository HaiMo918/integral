package com.integral.controller;

import com.integral.service.jf360.Jf360Service;
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
 * Created by kris on 2017/1/16.
 */
@Controller
public class Jf360Controller {
    @Resource
    Jf360Service jf360Service;


    @RequestMapping(value = "/query_jf360_jf",method = RequestMethod.POST)
    @ResponseBody
    public JfResult queryJf360jf(@RequestParam("data") String data) {
        JfResult result = new JfResult();
        try{
            result = jf360Service.queryIntegral(Decrypter.getInstance().decrypt(data));
        }catch (Exception e){
            result.setMessage(e.getMessage());
        }
        return result;
    }
}
