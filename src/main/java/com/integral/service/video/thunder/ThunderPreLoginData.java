package com.integral.service.video.thunder;

/**
 * Created by kris on 2017/3/1.
 */
public class ThunderPreLoginData {
    public String u;
    public String business_type="106";
    public String v="101";
    public String csrf_token;
    public long cachetime;

    @Override
    public String toString() {
        return "u="+u+"&business_type="+business_type+"&v="+v+"&csrf_token="+csrf_token+"&cachetime="+cachetime+"&";
    }
}
