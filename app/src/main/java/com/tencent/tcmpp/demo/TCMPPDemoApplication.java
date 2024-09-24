package com.tencent.tcmpp.demo;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.tencent.tcmpp.demo.bean.GlobalConfigure;
import com.tencent.tcmpp.demo.utils.GlideUtil;
import com.tencent.tcmpp.demo.utils.GlobalConfigureUtil;


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
        configureApp();
    }

    /**
     * config your app main page here
     */
    private void configureApp() {
        //custom app config with your own UI infos
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.applet_ic_tcmpp_login);
        GlobalConfigure customConfig = new GlobalConfigure.Builder()
                .appName(getString(R.string.applet_login_title))//main page app name
                .icon(bitmap)//bitmap for main page icon
                .description(getString(R.string.applet_login_title_desc))//main page description
                .mainTitle(getString(R.string.applet_main_title))//main page title
                .mockApi(false)//once set true , it will use local mock login and payment data ,otherwise online
                .build();
        //once set custom config,app will use custom config instead of default ones
        GlobalConfigureUtil.setCustomConfig(customConfig);
    }
}
