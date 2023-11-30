package com.tencent.tcmpp.demo.proxy;

import android.app.Application;
import android.text.TextUtils;

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
                .debug(true)  // optional
                .verifyPkg(false) //optional：是否校验配置文件包名，默认校验
                .x5LicenseKey("请联系商务咨询")
                // 动态内核下载地址仅在集成动态X5内核时有效, 私有化部署请咨询管理员
                .coreUrl32("https://tmf-pkg-1314481471.cos.ap-shanghai.myqcloud.com/x5/32/46471/tbs_core_046471_20230809095840_nolog_fs_obfs_armeabi_release.tbs")
                .coreUrl64("https://tmf-pkg-1314481471.cos.ap-shanghai.myqcloud.com/x5/64/46471/tbs_core_046471_20230809100104_nolog_fs_obfs_arm64-v8a_release.tbs")
                .build();
    }
}
