package com.integral.service.creditcard.zhongxin;

/**
 * Created by krisliu on 16-11-28.
 */
public class SmsResponse {
    String phone;
    String rtnCode;
    String rtnMsg;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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
}
