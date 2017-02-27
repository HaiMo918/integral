package com.integral.controller;

import com.integral.service.yihaodian.YihaodianService;
import com.integral.tools.Decrypter;
import com.integral.utils.JfResult;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * Created by liuqinghai on 2016/12/22.
 *
 */
@Controller
public class YihaodianController {
    @Resource
    YihaodianService yihaodian;

    @ResponseBody
    @RequestMapping(value = "/query_yihaodianjf",method = RequestMethod.POST)
    public JfResult queryYihaodianJf(@RequestParam("data") String data){
        JfResult result=new JfResult();
        try{
            result=yihaodian.queryIntegral(Decrypter.getInstance().decrypt(data));
        }catch (Exception e){
            result.setMessage(e.getMessage());
        }
        return result;
    }
}
