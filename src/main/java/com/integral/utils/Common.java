package com.integral.utils;//Created by xiacheng on 16/10/9.

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class Common {
    public static String createUUID(){
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    public static String inputStreamToString(InputStream is) throws Exception{
        StringBuilder sb = new StringBuilder();
        String line;
        BufferedReader br = new BufferedReader(new InputStreamReader(is,"utf-8"));
        while ((line=br.readLine())!=null){
            sb.append(line);
        }
        br.close();
        return sb.toString();
    }

    public static synchronized void updateCookie(HttpURLConnection connection, Map<String,String> thecookie) throws Exception {
        if (connection != null) {
            Map<String, List<String>> headerFields = connection.getHeaderFields();
            Set<String> fields = headerFields.keySet();
            if (fields.contains("Set-Cookie")) {
                List<String> cookieValues = headerFields.get("Set-Cookie");
                for (String value : cookieValues) {
                    int firstSemicolon = value.indexOf(';');
                    if (firstSemicolon != -1) {
                        String cookie = value.substring(0, firstSemicolon);
                        int firstColon = cookie.indexOf('=');
                        String key = cookie.substring(0, firstColon);
                        String keyvalue = cookie.substring(firstColon + 1);
                        thecookie.put(key, keyvalue);
                    }
                }
            }
        }
    }

    public static synchronized String buildCookieString(Map<String, String> cookie) throws Exception {
        StringBuilder data = new StringBuilder();
        for (String key : cookie.keySet()) {
            data.append(key).append("=");
            if (cookie.get(key) != null) {
                data.append(cookie.get(key));
            }
            data.append(";");
        }
        return data.toString();
    }

}
