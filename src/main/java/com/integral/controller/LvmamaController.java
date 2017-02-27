package com.integral.controller;

import com.integral.service.lvmama.Lvmama;
import com.integral.tools.Decrypter;
import com.integral.utils.JfResult;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * Created by kris on 2017/1/13.
 */
@Controller
public class LvmamaController {
    @Resource
    private Lvmama lvmama;

    @RequestMapping(value = "/query_lmm_code")
    @ResponseBody
    public JfResult queryLmmCode(){
        JfResult result = new JfResult();
        try{
            result=lvmama.requestVerifyCode(null);
        }catch (Exception e){
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @RequestMapping(value = "/query_lmm_jf")
    @ResponseBody
    public JfResult queryLmmPoints(@RequestParam("data")String data){
        JfResult result = new JfResult();
        try{
            result=lvmama.queryIntegral(Decrypter.getInstance().decrypt(data));
        }catch (Exception e){
            result.setMessage(e.getMessage());
        }
        return result;
    }
}
