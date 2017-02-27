package com.integral.utils;//Created by xiacheng on 16/10/9.

public class JfRequest {
    /**
     * 用户名/账号
     */
    private String account=null;
    /**
     * 服务密码/密码
     */
    private String password=null;
    /**
     * 验证码(图片验证码/短信验证码)
     */
    private String code =null;
    /**
     * session id,客户端的每次请求,服务端都会填充这个字段
     * 一般来说只是在请求验证码的时候会填充该字段
     */
    private String id=null;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
