package com.integral.controller;

import com.integral.service.quanjia.Quanjia;
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
 *
 */
@Controller
public class QuanjiaContruller {
    @Resource
    Quanjia quanjia;


    @ResponseBody
    @RequestMapping(value = "/request_qj_code",method = RequestMethod.POST)
    public JfResult getQuanjiaCode(){
        JfResult result = new JfResult();
        try{
            result = quanjia.requestVerifyCode(null);
        }catch (Exception e){
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/request_qj_jf",method = RequestMethod.POST)
    public JfResult getQuanjiaPoinsts(@RequestParam("data")String data){
        JfResult result = new JfResult();
        try{
            result = quanjia.queryIntegral(Decrypter.getInstance().decrypt(data));
        }catch (Exception e){
            result.setMessage(e.getMessage());
        }
        return result;
    }

}
