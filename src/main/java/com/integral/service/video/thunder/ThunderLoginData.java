package com.integral.service.video.thunder;

/**
 * Created by kris on 2017/3/1.
 */
public class ThunderLoginData {
    public String p;
    public String u;
    public String verifycode="----";
    public String login_enable="0";
    public String business_type="106";
    public String v="101";
    public long cachetime;

    @Override
    public String toString() {
        return "p="+p+"&u="+u+"&verifycode="+verifycode+"&login_enable="+login_enable+"&business_type="
                +business_type+"&v="+v+"&cachetime="+cachetime;
    }
}
