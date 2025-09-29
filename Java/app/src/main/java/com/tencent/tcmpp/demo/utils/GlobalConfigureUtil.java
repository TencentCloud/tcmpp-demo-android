package com.tencent.tcmpp.demo.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.tencent.tcmpp.demo.bean.GlobalConfigure;
import com.tencent.tcmpp.demo.R;

public class GlobalConfigureUtil {
    private static GlobalConfigure sCustomConfig;

    public static void setCustomConfig(GlobalConfigure configure) {
        sCustomConfig = configure;
    }

    public static GlobalConfigure getGlobalConfig(Context context) {
        return null == sCustomConfig ? getDefaultGlobalConfig(context) : sCustomConfig;
    }

    public static GlobalConfigure getDefaultGlobalConfig(Context context) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.applet_ic_tcmpp_login);
        return new GlobalConfigure.Builder()
                .appName(context.getString(R.string.applet_login_title))//main page app name
                .icon(bitmap)//bitmap for main page icon
                .description(context.getString(R.string.applet_login_title_desc))//main page description
                .mainTitle(context.getString(R.string.applet_main_title))//main page title
                .mockApi(false)//not use mock api by default
                .build();
    }

}
