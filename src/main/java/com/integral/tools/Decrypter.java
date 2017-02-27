package com.integral.tools;//Created by xiacheng on 16/10/9.

import com.alibaba.fastjson.JSONObject;
import com.integral.utils.Constants;
import com.integral.utils.JfRequest;

public class Decrypter {
    public static Decrypter instance=null;
    private static final Object object = new Object();
    public static Decrypter getInstance(){
        synchronized (object){
            if (instance==null){
                instance = new Decrypter();
            }
        }
        return instance;
    }

    private Decrypter(){

    }

    /**
     * 解密
     *
     * @param mixedData
     * @return
     * @throws Exception
     */
    public JfRequest decrypt(String mixedData) throws Exception{
        if (mixedData==null||"".equals(mixedData)){
            throw new Exception("请求的参数无效");
        }
        String base64PlainText = Base64Util.decode(mixedData);
        String aesPlainText = AesCrypter.decrypt(Constants.AES_KEY,base64PlainText);
        JSONObject object = JSONObject.parseObject(aesPlainText);
        JSONObject user = object.getJSONObject("data");
        String userStr = JSONObject.toJSONString(user);
        JfRequest request = JSONObject.parseObject(userStr,JfRequest.class);
        return request;
    }

    /**
     * 加密
     *
     * @param request
     * @return
     * @throws Exception
     */
    public  String encrypt(JfRequest request) throws Exception{
        if (request==null){
            throw  new NullPointerException("请求的参数无效NULL");
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data", request);
        String jsonText = JSONObject.toJSONString(jsonObject);

        String cipherTextAes = AesCrypter.AES_Encrypt(Constants.AES_KEY, jsonText);
        String base64Text = Base64Util.encode(cipherTextAes);
        return base64Text;
    }


    public static void main(String[] args) throws Exception {
        JfRequest request = new JfRequest();
        request.setCode("11111");
        Decrypter decrypter = new Decrypter();
        String mw = decrypter.encrypt(request);
        JfRequest planObject = decrypter.decrypt(mw);
        System.out.println(planObject);
    }

}