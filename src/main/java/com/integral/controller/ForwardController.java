package com.integral.controller;

/**
 * Created by xiongzicheng on 2016/11/2.
 */

import com.integral.utils.XCookieStore;
import com.integral.utils.XHttpClient;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


@Controller
public class ForwardController {

    /**
     * 只用这个跳转获取图片验证码
     *
     * @param url
     * @param response
     * @throws IOException
     */
    @RequestMapping("/forward")
    public void forward(String url, String user, HttpServletResponse response) throws IOException {
        if (user == null || url == null) return;
        XCookieStore.threadLocal.set(user);
        OutputStream output = response.getOutputStream();
        response.setContentType("image/png");
        try (InputStream stream = XHttpClient.getInputStream(url)) {
            int len = 0;
            byte[] buffer = new byte[4000];
            while ((len = stream.read(buffer)) != -1) {
                output.write(buffer, 0, len);
            }
        }
    }
}
