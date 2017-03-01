package com.integral.controller;

import com.integral.service.video.pptv.PPTVService;
import com.integral.tools.Decrypter;
import com.integral.utils.JfResult;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * Created by liuqinghai on 2017/1/23.
 */
@Controller
public class PPTVController {
    @Resource
    PPTVService pptvService;

    @ResponseBody
    @RequestMapping(value = "/query_pptv_jf",method = RequestMethod.POST)
    public JfResult queryPPTVJf(@RequestParam("data") String data){
        JfResult result = new JfResult();
        try{
            result = pptvService.queryIntegral(Decrypter.getInstance().decrypt(data));
        }catch (Exception e){
            result.setMessage(e.getMessage());
        }
        return result;
    }
}
