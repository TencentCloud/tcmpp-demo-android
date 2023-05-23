package com.tencent.tcmpp.demo;

import android.app.Application;
import android.content.Context;
import com.tencent.tcmpp.demo.sp.impl.CommonSp;
import com.tencent.tmf.mini.api.TmfMiniSDK;
import com.tencent.tmf.mini.api.bean.MiniInitConfig;


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
        initTCMPP();
    }

    private void initTCMPP(){
        boolean privacyAuth = CommonSp.getInstance().isPrivacyAuth(this);

        MiniInitConfig.Builder builder = new MiniInitConfig.Builder();
        MiniInitConfig config = builder
                .configAssetName("tcmpp-android-configurations.json")
                .debug(true)  // optional
                .privacyAuth(privacyAuth) // optional
                .verifyPkg(false) //optional：是否校验配置文件包名，默认校验
                .build();
        TmfMiniSDK.init(this, config);

        Constants.COUNTRY = getResources().getString(R.string.applet_mini_data_country);
        Constants.CITY = getResources().getString(R.string.applet_mini_proxy_city);
        Constants.PROVINCE = getResources().getString(R.string.applet_mini_proxy_province);

        if (privacyAuth) {
            //只有隐私授权后才能调用TmfMiniSDK相关API
            TmfMiniSDK.setLocation(Constants.COUNTRY, Constants.PROVINCE, Constants.CITY);
            TmfMiniSDK.preloadMiniApp(this, null);
        }
    }
}
