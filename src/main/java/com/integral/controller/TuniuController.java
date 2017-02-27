package com.integral.controller;

import com.integral.service.tuniu.Tuniu;
import com.integral.tools.Decrypter;
import com.integral.utils.JfResult;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * Created by liuqinghai on 2016/12/8.
 */
@Controller
public class TuniuController {
    @Resource
    Tuniu mTuniu;
    @ResponseBody
    @RequestMapping(value = "/query_tuniu_jf",method = RequestMethod.POST)
    public JfResult queryTuniuJf(@RequestParam("data")String data){
        JfResult result = new JfResult();
        try{
            return mTuniu.queryIntegral(Decrypter.getInstance().decrypt(data));
        }catch (Exception e){
            result.setMessage(e.getMessage());
        }
        return result;
    }
}
