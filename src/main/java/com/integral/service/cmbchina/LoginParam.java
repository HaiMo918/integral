package com.integral.service.cmbchina;//Created by xiacheng on 16/10/10.

public class LoginParam {
    private String Token;
    private String ValidateCodeUrl;
    private String GuidKey;
    private String Timestamp;
    private String Code;
    private String Merchant="Point";
    private String MerchantData;

    public String getToken() {
        return Token;
    }

    public void setToken(String token) {
        Token = token;
    }

    public String getValidateCodeUrl() {
        return ValidateCodeUrl;
    }

    public void setValidateCodeUrl(String validateCodeUrl) {
        ValidateCodeUrl = validateCodeUrl;
    }

    public String getGuidKey() {
        return GuidKey;
    }

    public void setGuidKey(String guidKey) {
        GuidKey = guidKey;
    }

    public String getTimestamp() {
        return Timestamp;
    }

    public void setTimestamp(String timestamp) {
        Timestamp = timestamp;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public String getMerchant() {
        return Merchant;
    }

    public void setMerchant(String merchant) {
        Merchant = merchant;
    }

    public String getMerchantData() {
        return MerchantData;
    }

    public void setMerchantData(String merchantData) {
        MerchantData = merchantData;
    }

    @Override
    public String toString() {
        return "LoginParam{" +
                "Token='" + Token + '\'' +
                ", ValidateCodeUrl='" + ValidateCodeUrl + '\'' +
                ", GuidKey='" + GuidKey + '\'' +
                ", Timestamp='" + Timestamp + '\'' +
                ", Code='" + Code + '\'' +
                ", Merchant='" + Merchant + '\'' +
                ", MerchantData='" + MerchantData + '\'' +
                '}';
    }
}
