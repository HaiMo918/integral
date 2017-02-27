package com.integral.controller;

import com.integral.service.ctrip.Ctrip;
import com.integral.tools.Decrypter;
import com.integral.utils.JfResult;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * Created by liuqinghai on 2016/12/7.
 */
@Controller
public class CtripController {
    @Resource
    Ctrip mCtrip;

    @ResponseBody
    @RequestMapping(value = "/query_xiecheng_jf",method = RequestMethod.POST)
    public JfResult queryXiechengJf(@RequestParam("data") String data){
        JfResult result = new JfResult();
        try{
            result = mCtrip.queryIntegral(Decrypter.getInstance().decrypt(data));
        }catch (Exception e){
            result.setMessage(e.getMessage());
        }
        return result;
    }
}
