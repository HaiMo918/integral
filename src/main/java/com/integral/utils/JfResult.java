package com.integral.utils;//Created by xiacheng on 16/10/9.

public class JfResult {
    /**
     * 对于图片验证码来说,该字段表示一个图片的URL地址
     */
    private String data=null;

    /**
     * 积分数据
     */
    private String points=null;

    /**
     * 操作结果消息
     */
    private String message=null;

    /**
     * 服务端返回给客户端的session id
     */
    private String id=null;

    /**
     * 操作结果代码
     */
    private String code=null;

    /**
     * 如果有错误,则表示错误原因
     */
    private String reason = null;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
