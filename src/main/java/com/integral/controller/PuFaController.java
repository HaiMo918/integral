package com.integral.controller;

import com.integral.service.pufa.PuFaServer;
import com.integral.tools.Decrypter;
import com.integral.utils.Constants;
import com.integral.utils.JfResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * Created by jianfeng on 2016/11/1.
 */
@RestController
public class PuFaController {
    @Resource
    PuFaServer puFaServer;

    @RequestMapping(value = "/request_pf_pic")
    public JfResult requestVerifyPic(@RequestParam("data") String data) {
        try {
            return puFaServer.requestVerifyPic(Decrypter.getInstance().decrypt(data));
        } catch (Exception e) {
            return createExceptonResult(e);
        }
    }

    @RequestMapping(value = "/request_pf_code")
    public JfResult requestVerifyCode(@RequestParam("data") String data){
        try {
            return puFaServer.requestVerifyCode(Decrypter.getInstance().decrypt(data));
        } catch (Exception e) {
            return createExceptonResult(e);
        }
    }

    @RequestMapping(value = "/request_pf_jf")
    public JfResult queryIntegral(@RequestParam("data") String data){
        try {
            return puFaServer.queryIntegral(Decrypter.getInstance().decrypt(data));
        } catch (Exception e) {
            return createExceptonResult(e);
        }
    }

    private JfResult createExceptonResult(Exception e) {
        JfResult result = new JfResult();
        result.setCode(Constants.ErrorCode.OTHER_EXCEPTION + "");
        result.setMessage(e.getMessage());
        return result;
    }
}
