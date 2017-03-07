package com.integral.utils;

public interface Constants {
    String DEFAULT_UA="Mozilla/5.0 (Macintosh; Intel Mac OS X 10.11; rv:47.0) Gecko/20100101 Firefox/47.0";
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
}
