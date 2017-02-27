package com.integral.controller;

import com.integral.service.creditcard.jiaotongyinhang.JiaohangService;
import com.integral.tools.Decrypter;
import com.integral.utils.JfResult;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * Created by liuqinghai on 2016/11/18.
 */
@Controller
public class JiaohangController {
    @Resource
    JiaohangService mJiaoHang;

    @ResponseBody
    @RequestMapping(value = "/request_jtyh_jf",method = RequestMethod.POST)
    public JfResult requestJiaohangJf(@RequestParam("data") String data){
        JfResult jfResult = new JfResult();
        try{
            return mJiaoHang.queryIntegral(Decrypter.getInstance().decrypt(data));
        }catch (Exception e){
            jfResult.setMessage("获取交行积分出现异常");
            jfResult.setReason(e.getLocalizedMessage());
        }
        return jfResult;
    }
}
