package com.integral.utils;

/**
 * Created by kris on 2017/1/16.
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.pkcs.RSAPrivateKeyStructure;
/**
 * @author
 * @date 2014-11-1
 * @time 下午12:02:35
 *
 */
public class RSAHelper {
    public static final String DEFAULT_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQChDzcjw/rWgFwnxunbKp7/4e8w" + "\r"
            + "/UmXx2jk6qEEn69t6N2R1i/LmcyDT1xr/T2AHGOiXNQ5V8W4iCaaeNawi7aJaRht" + "\r"
            + "Vx1uOH/2U378fscEESEG8XDqll0GCfB1/TjKI2aitVSzXOtRs8kYgGU78f7VmDNg" + "\r" + "XIlk3gdhnzh+uoEQywIDAQAB" + "\r";
    /**
     * 私钥
     */
    private RSAPrivateKey privateKey;
    /**
     * 公钥
     */
    private RSAPublicKey publicKey;
    public RSAPrivateKey getPrivateKey() {
        return privateKey;
    }
    public RSAPublicKey getPublicKey() {
        return publicKey;
    }
    /**
     * 随机生成密钥对
     */
    public void genKeyPair() {
        KeyPairGenerator keyPairGen = null;
        try {
            keyPairGen = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        keyPairGen.initialize(1024, new SecureRandom());
        KeyPair keyPair = keyPairGen.generateKeyPair();
        this.privateKey = (RSAPrivateKey) keyPair.getPrivate();
        this.publicKey = (RSAPublicKey) keyPair.getPublic();
    }
    /**
     * 从文件中输入流中加载公钥
     *
     * @param in
     *            公钥输入流
     * @throws Exception
     *             加载公钥时产生的异常
     */
    public void loadPublicKey(InputStream in) throws Exception {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String readLine = null;
            StringBuilder sb = new StringBuilder();
            while ((readLine = br.readLine()) != null) {
                if (readLine.charAt(0) == '-') {
                    continue;
                } else {
                    sb.append(readLine);
                    sb.append('\r');
                }
            }
            loadPublicKey(sb.toString());
        } catch (IOException e) {
            throw new Exception("公钥数据流读取错误");
        } catch (NullPointerException e) {
            throw new Exception("公钥输入流为空");
        }
    }
    /**
     * 从字符串中加载公钥
     *
     * @param publicKeyStr
     *            公钥数据字符串
     * @throws Exception
     *             加载公钥时产生的异常
     */
    public void loadPublicKey(String publicKeyStr) throws Exception {
        byte[] buffer = Base64.decodeBase64(publicKeyStr);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
        this.publicKey = (RSAPublicKey) keyFactory.generatePublic(keySpec);
    }

    public void loadPrivateKeyPKCS1(InputStream in) throws Exception{
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String readLine = null;
            StringBuilder sb = new StringBuilder();
            while ((readLine = br.readLine()) != null) {
                if (readLine.charAt(0) == '-') {
                    continue;
                } else {
                    sb.append(readLine);
                    sb.append('\r');
                }
            }
            loadPrivateKeyPKCS1(sb.toString());
        } catch (IOException e) {
            throw new Exception("私钥数据读取错误");
        } catch (NullPointerException e) {
            throw new Exception("私钥输入流为空");
        }
    }

    public void loadPrivateKeyPKCS1(String privateKeyStr)  throws Exception{
        System.out.println("load-privateKey-PKCS#1");
        byte[] buffer = Base64.decodeBase64(privateKeyStr);
        RSAPrivateKeyStructure asn1PrivKey = new RSAPrivateKeyStructure((ASN1Sequence) ASN1Sequence.fromByteArray(buffer));
        RSAPrivateKeySpec rsaPrivKeySpec = new RSAPrivateKeySpec(asn1PrivKey.getModulus(), asn1PrivKey.getPrivateExponent());
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        this.privateKey = (RSAPrivateKey) keyFactory.generatePrivate(rsaPrivKeySpec);
    }
    /**
     * 从文件中加载私钥
     *
     * @param keyFileName
     *            私钥文件名
     * @return 是否成功
     * @throws Exception
     */
    public void loadPrivateKey(InputStream in) throws Exception {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String readLine = null;
            StringBuilder sb = new StringBuilder();
            while ((readLine = br.readLine()) != null) {
                if (readLine.charAt(0) == '-') {
                    continue;
                } else {
                    sb.append(readLine);
                    sb.append('\r');
                }
            }
            loadPrivateKey(sb.toString());
        } catch (IOException e) {
            throw new Exception("私钥数据读取错误");
        } catch (NullPointerException e) {
            throw new Exception("私钥输入流为空");
        }
    }
    public void loadPrivateKey(String privateKeyStr) throws Exception {
        System.out.println("load-privateKey-PKCS#8");
        byte[] buffer = Base64.decodeBase64(privateKeyStr);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        this.privateKey = (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
    }
    /**
     * @Description: RAS-publicKey-加密
     * @author
     * @date 2014-11-1
     * @time 下午12:00:34
     * @param publicKey
     * @param plainTextData
     * @return 返回字节数组
     * @throws Exception
     */
    public byte[] encrypt(RSAPublicKey publicKey, byte[] plainTextData) throws Exception {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] enBytes = cipher.doFinal(plainTextData);
            return enBytes;
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("RSA-publicKey-加密异常");
        } catch (NoSuchPaddingException e) {
            throw new Exception("RSA-publicKey-加密异常");
        }
    }
    /**
     * @Description: RAS-publicKey-加密
     * @author
     * @date 2014-11-1
     * @time 下午12:00:55
     * @param publicKey
     * @param plainTextDataBase64
     *            【明文数据-base64编码字符串】
     * @return 返回base64编码字符串
     * @throws Exception
     */
    public String encryptBase64(RSAPublicKey publicKey, String plainTextDataBase64) throws Exception {
        byte[] plainTextData = Base64.decodeBase64(plainTextDataBase64);
        byte[] enBytes = this.encrypt(publicKey, plainTextData);
        return Base64.encodeBase64String(enBytes);
    }
    /**
     * @Description: RSA-privateKey-解密
     * @author
     * @date 2014-11-1
     * @time 下午12:01:31
     * @param privateKey
     * @param cipherTextData
     * @return 返回字节数组
     * @throws Exception
     */
    public byte[] decrypt(RSAPrivateKey privateKey, byte[] cipherTextData) throws Exception {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] deBytes = cipher.doFinal(cipherTextData);
            return deBytes;
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("RSA-privateKey-解密异常");
        } catch (NoSuchPaddingException e) {
            throw new Exception("RSA-privateKey-解密异常");
        }
    }
    /**
     * @Description: RSA-privateKey-解密
     * @author
     * @date 2014-11-1
     * @time 下午12:01:57
     * @param privateKey
     * @param cipherTextDataBase64
     * @return 返回base64字符串
     * @throws Exception
     */
    public String decryptBase64(RSAPrivateKey privateKey, String cipherTextDataBase64) throws Exception {
        byte[] cipherTextData = Base64.decodeBase64(cipherTextDataBase64);
        byte[] deBytes = this.decrypt(privateKey, cipherTextData);
        return Base64.encodeBase64String(deBytes);
    }
    /**
     * @Description: RSA-privateKey-签名
     * @author
     * @date 2014-11-1
     * @time 下午12:07:39
     * @param privateKey
     * @param content
     * @return 返回字节数组
     * @throws Exception
     */
    public byte[] sign(RSAPrivateKey privateKey, byte[] content) throws Exception {
        try {
            Signature signature = Signature.getInstance("SHA1WithRSA");
            signature.initSign(privateKey);
            signature.update(content);
            byte[] signResult = signature.sign();
            return signResult;
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("RSA-privateKey-签名异常");
        }
    }
    /**
     * @Description: RSA-privateKey-签名
     * @author
     * @date 2014-11-1
     * @time 下午12:09:12
     * @param privateKey
     * @param contentBase64
     *            【验证签名原文-base64编码字符串】
     * @return 返回base64字符串
     * @throws Exception
     */
    public String signBase64(RSAPrivateKey privateKey, String contentBase64) throws Exception {
        byte[] content = Base64.decodeBase64(contentBase64);
        byte[] signResult = this.sign(privateKey, content);
        return Base64.encodeBase64String(signResult);
    }
    /**
     * @Description: RSA-publicKey-验证签名
     * @author
     * @date 2014-11-1
     * @time 下午12:15:52
     * @param publicKey
     * @param content
     *            【签名原文-字节数组】
     * @param sign
     *            【待验证签名-字节数组】
     * @return 签名结果
     * @throws Exception
     */
    public boolean verify(RSAPublicKey publicKey, byte[] content, byte[] sign) throws Exception {
        try {
            Signature signature = Signature.getInstance("SHA1WithRSA");
            signature.initVerify(publicKey);
            signature.update(content);
            return signature.verify(sign);
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("RSA-publicKey-验证签名异常");
        }
    }
    /**
     * @Description: RSA-publicKey-验证签名
     * @author
     * @date 2014-11-1
     * @time 下午12:18:52
     * @param publicKey
     * @param contentBase64
     *            【签名原文-base64编码字符串】
     * @param signBase64
     *            【待验证签名-base64编码字符串】
     * @return 签名结果
     * @throws Exception
     */
    public boolean verifyBase64(RSAPublicKey publicKey, String contentBase64, String signBase64) throws Exception {
        byte[] content = Base64.decodeBase64(contentBase64);
        byte[] sign = Base64.decodeBase64(signBase64);
        return this.verify(publicKey, content, sign);
    }


}
