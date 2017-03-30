package com.integral.utils;

public interface Constants {
    String DEFAULT_UA="Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36";
    String BK_UA = "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36";
    String AES_KEY = "oi4fVNvyITEHT6qH";
    enum ErrorCode{
        ERROR_SUCCESS(0),
        SEND_SMS_FAILED(-1),
        GET_INTEGRAL_FAILED(-2),
        OTHER_EXCEPTION(-3),
        LOGIN_FAILED(-4);
        private int code;
        ErrorCode(int code){
            this.code = code;
        }
        public void setCode(int code){
            this.code=code;
        }
        public int getCode(){
            return this.code;
        }
    }

    class HttpHeaders{
        public static final String USER_AGENT="User-Agent";
        public static final String COOKIE="Cookie";
        public static final String CONTENT_TYPE="Content-Type";
        public static final String CONTENT_LENGTH="Content-Length";
        public static final String HOST="Host";
        public static final String ACCEPT="Accept";
        public static final String REFERER="Referer";
        public static final String ACCEPT_ENCODING="Accept-Encoding";
        public static final String ACCEPT_LANGUAGE="Accept-Language";
    }

    enum PlatForm{
        WINDOWS(0),LINUX(1);
        private int platform;
        PlatForm(int platform){
            this.platform=platform;
        }
    }

    PlatForm platform=PlatForm.WINDOWS;

    String JS_FOLDER = "/usr/local/etc/tomcat8/webapps/jquery/";
    String REQUEST_METHOD_POST = "POST";
}
