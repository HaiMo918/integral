package com.integral.tools;


import com.sun.org.apache.xml.internal.security.utils.Base64;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;


public class AesCrypter {
    private static final String AESTYPE = "AES/ECB/PKCS5Padding";

    public static String AES_Encrypt(String keyStr, String plainText) {
        byte[] encrypt = null;
        try {
            Key key = generateKey(keyStr);
            Cipher cipher = Cipher.getInstance(AESTYPE);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            encrypt = cipher.doFinal(plainText.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new String(Base64.encode(encrypt));
    }

    public static String decrypt(String keyStr, String encryptData) {
        byte[] decrypt = null;
        try {
            Key key = generateKey(keyStr);
            Cipher cipher = Cipher.getInstance(AESTYPE);
            cipher.init(Cipher.DECRYPT_MODE, key);
            decrypt = cipher.doFinal(Base64.decode(encryptData));
        } catch (Exception e) {
            e.printStackTrace();
        }
        assert decrypt != null;
        return new String(decrypt).trim();
    }

    private static Key generateKey(String key) {
        try {
            return new SecretKeySpec(key.getBytes(), "AES");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {

        String keyStr = "39984cbca380a91b";
        String plainText = "this is a string will be AES_Encrypt";

        //String encText = AES_Encrypt(keyStr, plainText);
        //System.out.println(encText);
        String decString = decrypt(keyStr, "LommqrACSUMowvrSVpR3ZgkagXtzu91JtwUifGaOcGc8LXAccgJBrj+0BKJUxGCG");
        System.out.println(decString);
    }
}