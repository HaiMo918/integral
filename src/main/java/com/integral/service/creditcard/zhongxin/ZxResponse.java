package com.integral.service.creditcard.zhongxin;

/**
 * Created by liuqinghai on 2016/11/28.
 */
public class ZxResponse {
    String loginChannel;
    Boolean needValiCode;
    String redirectUrl;
    String retMsg;
    String rtnCode;

    public String getRtnCode() {
        return rtnCode;
    }

    public void setRtnCode(String rtnCode) {
        this.rtnCode = rtnCode;
    }

    public String getLoginChannel() {
        return loginChannel;
    }

    public void setLoginChannel(String loginChannel) {
        this.loginChannel = loginChannel;
    }

    public Boolean getNeedValiCode() {
        return needValiCode;
    }

    public void setNeedValiCode(Boolean needValiCode) {
        this.needValiCode = needValiCode;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public String getRetMsg() {
        return retMsg;
    }

    public void setRetMsg(String retMsg) {
        this.retMsg = retMsg;
    }
}
