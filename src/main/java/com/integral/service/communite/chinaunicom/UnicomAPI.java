package com.integral.service.communite.chinaunicom;

import java.util.Date;

/**
 * Created by qinghai on 16/7/23.
 */
public class UnicomAPI {

    //WT_FPC
    public static String dcsFPC(int offset) {////offset = 8
        Date dCur=new Date();
        String cur=String.valueOf(dCur.getTime());
        String co_f="2";
        for (int i=2;i<=(32-cur.length());i++){
            double d = Math.floor(Math.random()*16.0);
            co_f+=Long.toHexString((long)d);
        }
        co_f+=cur;
        Date dSec = new Date(dCur.getTime());
        return "id="+co_f+":lv="+System.currentTimeMillis()+":ss="+(dSec.getTime()+1800000);
    }

    //Hm_lvt_9208c8c641bfb0560ce7884c36938d9d
    public static String hmLvt(){
        return String.valueOf(System.currentTimeMillis()/1000);
    }

    //Hm_lpvt_9208c8c641bfb0560ce7884c36938d9d
    public static String hmLpvt(){
        return String.valueOf(System.currentTimeMillis()/1000);
    }
}
