package com.hbh.cl.smsvalidatedemo;

import android.app.Application;

import cn.smssdk.SMSSDK;

/**
 * Created by hbh on 2017/2/7.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SMSSDK.initSDK(this, "appkey", "appsecret");
    }
}
