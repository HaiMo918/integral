package com.integral.service.video.weipinhui;

/**
 * Created by kris on 2017/3/22.
 */
public class VipLoginParam {
    public String loginName;
    public String password;
    public String remUser;
    public String vipc;
    public String captcha;

    //loginName=15202823217&password=testjf123&remUser=1&vipc=bNZudRL1Vz0UabH4ugW79hX73uAMCx8udXyy9YzmAvELUIFPbQAUm0GWM5wAagGOC%2FVrRhvfXluH9f4GFbnCx3arqL9JwCwOgT1stXIHhN8%3D&captcha=nqx8
    @Override
    public String toString() {
        return "loginName="+loginName+"&password="+password+"&remUser="+remUser+"&vipc="+vipc+"&captcha="+captcha;
    }
}
