package com.integral.service.creditcard.zhongxin;

/**
 * Created by liuqinghai on 2016/11/28.
 */
public class ZxRequest {
    String loginType="01";
    String memcode;
    boolean isBord=false;
    String phone;
    String source="PC";
    String page="new";

    public String getLoginType() {
        return loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }

    public String getMemcode() {
        return memcode;
    }

    public void setMemcode(String memcode) {
        this.memcode = memcode;
    }

    public boolean isBord() {
        return isBord;
    }

    public void setBord(boolean bord) {
        isBord = bord;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }
}
