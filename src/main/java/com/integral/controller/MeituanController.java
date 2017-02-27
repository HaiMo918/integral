package com.integral.controller;

import com.integral.service.meituan.MeituanService;
import com.integral.tools.Decrypter;
import com.integral.utils.JfResult;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * Created by kris on 2017/1/3.
 *18621793235
 * liu123
 */
@Controller
public class MeituanController {
    @Resource
    MeituanService meituan;

    @ResponseBody
    @RequestMapping(value = "/request_mt_code",method = RequestMethod.POST)
    public JfResult queryMeituanCode(){
        JfResult result = new JfResult();
        try{
            result = meituan.requestVerifyCode(null);
        }catch (Exception e){
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/request_mt_jf",method = RequestMethod.POST)
    public JfResult queryMeituanPoints(@RequestParam("data") String data){
        JfResult result = new JfResult();
        try{
            result = meituan.queryIntegral(Decrypter.getInstance().decrypt(data));
        }catch (Exception e){
            result.setMessage(e.getMessage());
        }
        return result;
    }
}
