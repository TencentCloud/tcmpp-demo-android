package com.tencent.tcmpp.demo.proxy;

import android.app.Application;

import com.tencent.tcmpp.demo.TCMPPDemoApplication;
import com.tencent.tmf.mini.api.bean.MiniInitConfig;
import com.tencent.tmf.mini.api.proxy.MiniConfigProxy;
import com.tencent.tmfmini.sdk.annotation.ProxyService;

@ProxyService(proxy = MiniConfigProxy.class)
public class MiniConfigProxyImpl extends MiniConfigProxy {

    @Override
    public Application getApp() {
        return TCMPPDemoApplication.sApp;
    }

    @Override
    public MiniInitConfig buildConfig() {
        MiniInitConfig.Builder builder = new MiniInitConfig.Builder();
        return builder
                .configAssetName("tcmpp-android-configurations.json")
                .debug(true)  // optional
                .verifyPkg(false) //optional：是否校验配置文件包名，默认校验
                .build();
    }
}
