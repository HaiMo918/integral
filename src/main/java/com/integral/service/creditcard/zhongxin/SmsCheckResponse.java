package com.integral.service.creditcard.zhongxin;

/**
 * Created by krisliu on 16-11-29.
 */
public class SmsCheckResponse {
    private String loginType;
    private String rtnCode;
    private String rtnMsg;
    private String ucseq;

    public String getLoginType() {
        return loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }

    public String getRtnCode() {
        return rtnCode;
    }

    public void setRtnCode(String rtnCode) {
        this.rtnCode = rtnCode;
    }

    public String getRtnMsg() {
        return rtnMsg;
    }

    public void setRtnMsg(String rtnMsg) {
        this.rtnMsg = rtnMsg;
    }

    public String getUcseq() {
        return ucseq;
    }

    public void setUcseq(String ucseq) {
        this.ucseq = ucseq;
    }
}
