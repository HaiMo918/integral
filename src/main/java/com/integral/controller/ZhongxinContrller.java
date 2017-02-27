package com.integral.controller;

import com.integral.service.creditcard.zhongxin.ZhongXin;
import com.integral.tools.Decrypter;
import com.integral.utils.JfRequest;
import com.integral.utils.JfResult;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * Created by liuqinghai on 2016/11/30.
 */
@Controller
public class ZhongxinContrller {
    @Resource
    ZhongXin mZhongxin;

    @ResponseBody
    @RequestMapping(value = "/request_zhx_code")
    public JfResult queryZhongxinTelCode(@RequestParam("data") String data){
        JfResult result = new JfResult();
        try {
            JfRequest request = Decrypter.getInstance().decrypt(data);
            result = mZhongxin.requestVerifyCode(request);
        }catch (Exception e){

        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/request_zhx_jf")
    public JfResult queryZhongxinPoints(@RequestParam("data") String data){
        JfResult result = new JfResult();
        try {
            JfRequest request = Decrypter.getInstance().decrypt(data);
            result = mZhongxin.queryIntegral(request);
        }catch (Exception e){

        }
        return result;
    }
}
