package com.integral.controller;

import com.integral.service.video.thunder.ThunderService;
import com.integral.tools.Decrypter;
import com.integral.utils.JfResult;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * Created by kris on 2017/3/6.
 */
@Controller
public class ThunderController {
    @Resource
    ThunderService thunderService;

    @ResponseBody
    @RequestMapping(value = "/query_thunder_points",method = RequestMethod.POST)
    public JfResult queryThunderPoints(@RequestParam("data")String data) {
        JfResult result = new JfResult();
        try{
            result = thunderService.queryIntegral(Decrypter.getInstance().decrypt(data));
        }catch (Exception e){
            result.setMessage(e.getMessage());
        }
        return result;
    }
}
