package com.tencent.tcmpp.demo.proxy;

import android.app.Application;
import android.text.TextUtils;

import com.tencent.tcmpp.demo.BuildConfig;
import com.tencent.tcmpp.demo.Constants;
import com.tencent.tcmpp.demo.TCMPPDemoApplication;
import com.tencent.tcmpp.demo.sp.impl.CommonSp;
import com.tencent.tmf.mini.api.bean.MiniInitConfig;
import com.tencent.tmf.mini.api.proxy.MiniConfigProxy;
import com.tencent.tmfmini.sdk.annotation.ProxyService;

import java.io.File;

@ProxyService(proxy = MiniConfigProxy.class)
public class MiniConfigProxyImpl extends MiniConfigProxy {

    @Override
    public Application getApp() {
        return TCMPPDemoApplication.sApp;
    }

    @Override
    public MiniInitConfig buildConfig() {
        MiniInitConfig.Builder builder = new MiniInitConfig.Builder();
        String configFile = CommonSp.getInstance().getConfigFilePath();
        if (!TextUtils.isEmpty(configFile) && new File(configFile).exists()) {
            builder.configFilePath(configFile);
        } else {
            builder.configAssetName(Constants.TCMPP_CONFIG_FILE);
        }

        return builder
                // optional: Whether to enable logs
                .debug(true)
                //optional：Whether to verify the package name
                .verifyPkg(false)
                .build();
    }
}
