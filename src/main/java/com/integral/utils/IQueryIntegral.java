package com.integral.utils;//Created by xiacheng on 16/10/9.

public interface IQueryIntegral {
    JfResult requestVerifyCode(JfRequest request) throws Exception;
    JfResult queryIntegral(JfRequest request) throws Exception;
}
