package com.integral.controller;

import com.integral.service.creditcard.Jianhang.JianHangService;
import com.integral.tools.Decrypter;
import com.integral.utils.Constants;
import com.integral.utils.JfResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * Created by xiongzicheng on 2016/11/1.
 */
@RestController
public class JianHangController {

    @Resource
    JianHangService jianHangService;

    @RequestMapping(value = "/request_jh_code", method = RequestMethod.POST)
    public JfResult requestVerifyCode(@RequestParam("data") String data) {
        try {
            return jianHangService.requestVerifyCode(Decrypter.getInstance().decrypt(data));
        } catch (Exception e) {
            JfResult result = new JfResult();
            result.setCode(Constants.ErrorCode.OTHER_EXCEPTION + "");
            result.setMessage(e.getMessage());
            return result;
        }
    }

    @ResponseBody
    @RequestMapping(value = "/request_jh_jf", method = RequestMethod.POST)
    public JfResult queryIntegral(@RequestParam("data") String data) throws Exception {
        try {
            return jianHangService.queryIntegral(Decrypter.getInstance().decrypt(data));
        } catch (Exception e) {
            JfResult result = new JfResult();
            result.setCode(Constants.ErrorCode.OTHER_EXCEPTION + "");
            result.setMessage(e.getMessage());
            result.setPoints("-1");
            return result;
        }
    }

}
