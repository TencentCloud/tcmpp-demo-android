package com.tencent.tcmpp.demo;

import android.app.Application;
import android.content.Context;

import com.tencent.tcmpp.demo.utils.GlideUtil;


public class TCMPPDemoApplication extends Application {
    public static Application sApp;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        sApp = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        GlideUtil.init(this);
    }
}
