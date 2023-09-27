package com.tencent.tcmpp.demo.proxy;

import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import com.tencent.tcmpp.demo.BuildConfig;
import com.tencent.tcmpp.demo.Constants;
import com.tencent.tcmpp.demo.utils.UniversalDrawable;
import com.tencent.tmf.mini.api.bean.MiniConfigData;
import com.tencent.tmfmini.sdk.annotation.ProxyService;
import com.tencent.tmfmini.sdk.launcher.core.IMiniAppContext;
import com.tencent.tmfmini.sdk.launcher.core.proxy.AsyncResult;
import com.tencent.tmfmini.sdk.launcher.core.proxy.BaseMiniAppProxyImpl;
import com.tencent.tmfmini.sdk.launcher.core.proxy.MiniAppProxy;
import com.tencent.tmfmini.sdk.launcher.ui.OnMoreItemSelectedListener;
import com.tencent.tmfmini.sdk.ui.DefaultMoreItemSelectedListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

@ProxyService(proxy = MiniAppProxy.class)
public class MiniAppProxyImpl extends BaseMiniAppProxyImpl {

    @Override
    public void getUserInfo(String s, AsyncResult asyncResult) {
        JSONObject jsonObject = new JSONObject();
        try {
            //返回昵称
            jsonObject.put("nickName", Constants.USER_INFO_NAME);
            //返回头像url
            jsonObject.put("avatarUrl", Constants.USER_INFO_AVATAR_URL);
            asyncResult.onReceiveResult(true, jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getAppVersion() {
        return "1.0.0";
    }

    @Override
    public String getAppName() {
        return "TCMPPDemo";
    }

    @Override
    public boolean isDebugVersion() {
        return BuildConfig.DEBUG;
    }

    @Override
    public Drawable getDrawable(Context context, String source, int width, int height, Drawable defaultDrawable) {
        //接入方接入自己的ImageLoader
        //demo里使用开源的universalimageloader
        UniversalDrawable drawable = new UniversalDrawable();
        if (TextUtils.isEmpty(source)) {
            return drawable;
        }
        drawable.loadImage(context, source);
        return drawable;
    }

    @Override
    public boolean openChoosePhotoActivity(Context context, int i, IChoosePhotoListner iChoosePhotoListner) {
        return false;
    }

    @Override
    public boolean openImagePreview(Context context, int i, List<String> list) {
        return false;
    }

    @Override
    public boolean onCapsuleButtonMoreClick(IMiniAppContext iMiniAppContext) {
        return false;
    }

    @Override
    public boolean onCapsuleButtonCloseClick(IMiniAppContext iMiniAppContext, OnClickListener onClickListener) {
        return false;
    }

    @Override
    public Map<String, Integer> getCustomShare() {
        return null;
    }

    @Override
    public boolean uploadUserLog(String s, String s1) {
        return false;
    }

    @Override
    public MiniConfigData configData(Context context, int configType, JSONObject params) {
        if(configType == MiniConfigData.TYPE_CUSTOM_JSAPI) {
            //自定义JsApi配置
            MiniConfigData.CustomJsApiConfig customJsApiConfig = new MiniConfigData.CustomJsApiConfig();
            customJsApiConfig.jsApiConfigPath = "tcmpp/custom-config.json";

            return new MiniConfigData
                    .Builder()
                    .customJsApiConfig(customJsApiConfig)
                    .build();
        }

        if(configType == MiniConfigData.TYPE_LIVE) {
            //Live直播配置
            MiniConfigData.LiveConfig liveConfig = new MiniConfigData.LiveConfig();
            //下面的key和url仅可用于demo
            liveConfig.licenseKey = "6ae463dfe484853eef22052ca122623b";
            liveConfig.licenseUrl = "https://license.vod2.myqcloud.com/license/v2/1314481471_1/v_cube.license";

            return new MiniConfigData
                    .Builder()
                    .liveConfig(liveConfig)
                    .build();
        }

//        if(configType == MiniConfigData.TYPE_DOMAIN) {
//            //虚拟域名配置
//            MiniConfigData.DomainConfig domainConfig = new MiniConfigData.DomainConfig();
//            domainConfig.domain = "test.com";
//
//            return new MiniConfigData
//                    .Builder()
//                    .domainConfig(domainConfig)
//                    .build();
//        }

        if(configType == MiniConfigData.TYPE_WEBVIEW) {
            //webView userAgent
            String ua = params.optString(MiniConfigData.WebViewConfig.WEBVIEW_CONFIG_UA);
            //设置新的userAgent
            MiniConfigData.WebViewConfig webViewConfig = new MiniConfigData.WebViewConfig();
            webViewConfig.userAgent = "xxxxxxxxxxxx";

            return new MiniConfigData
                    .Builder()
                    .webViewConfig(webViewConfig)
                    .build();
        }

        return new MiniConfigData
                .Builder()
                .build();
    }

    @Override
    public OnMoreItemSelectedListener getMoreItemSelectedListener() {
        return new DefaultMoreItemSelectedListener();
    }
}
