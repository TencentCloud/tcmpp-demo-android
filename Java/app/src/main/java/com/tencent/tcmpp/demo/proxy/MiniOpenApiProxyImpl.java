package com.tencent.tcmpp.demo.proxy;

import android.os.Bundle;

import com.tencent.tcmpp.demo.ipcplugin.OpenDataIPC;
import com.tencent.tcmpp.demo.open.login.Login;
import com.tencent.tcmpp.demo.open.login.LoginApi;
import com.tencent.tcmpp.demo.open.payment.PaymentManager;
import com.tencent.tcmpp.demo.utils.GlobalConfigureUtil;
import com.tencent.tmf.mini.api.TmfMiniSDK;
import com.tencent.tmf.mini.api.callback.IpcCallback;
import com.tencent.tmfmini.sdk.annotation.ProxyService;
import com.tencent.tmfmini.sdk.launcher.core.IMiniAppContext;
import com.tencent.tmfmini.sdk.launcher.core.proxy.AsyncResult;
import com.tencent.tmfmini.sdk.launcher.core.proxy.MiniOpenApiProxy;
import com.tencent.tmfmini.sdk.launcher.log.QMLog;

import org.json.JSONException;
import org.json.JSONObject;

@ProxyService(proxy = MiniOpenApiProxy.class)
public class MiniOpenApiProxyImpl extends MiniOpenApiProxy {
    private static final String TAG = "MiniOpenApiProxyImpl";

    private void callBackError(int code, String message, AsyncResult result) {
        JSONObject retData = new JSONObject();
        try {
            retData.put("errno", code);
            retData.put("errMsg", message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        result.onReceiveResult(false, retData);
    }

    @Override
    public void checkSession(IMiniAppContext miniAppContext, JSONObject params, AsyncResult result) {
        QMLog.d(TAG, "checkSession:" + params);
        //mock api
        if (GlobalConfigureUtil.getGlobalConfig(miniAppContext.getContext()).mockApi) {
            JSONObject retData = new JSONObject();
            result.onReceiveResult(true, retData);
            return;
        }
        //real api
        JSONObject jsonObject = new JSONObject();
        Login login = Login.g(miniAppContext.getContext());
        LoginApi.UserInfo userInfo = login.getUserInfo();
        result.onReceiveResult(userInfo != null, jsonObject);
    }

    @Override
    public void getUserInfo(IMiniAppContext miniAppContext, JSONObject params, AsyncResult result) {
        QMLog.d(TAG, "getUserInfo:" + params);
        //mock api
        if (GlobalConfigureUtil.getGlobalConfig(miniAppContext.getContext()).mockApi) {
            JSONObject retData = new JSONObject();
            final JSONObject userInfo = new JSONObject();
            try {
                userInfo.put("nickName", "mockUser");
                userInfo.put("avatarUrl", "");
                userInfo.put("gender", 0);
                userInfo.put("country", "CN");
                userInfo.put("province", "BeiJing");
                userInfo.put("city", "BeiJing");
                userInfo.put("language", "en");
                retData.put("userInfo", userInfo);
            } catch (JSONException e) {

            }
            result.onReceiveResult(true, retData);
            return;
        }
        //real api call
        JSONObject jsonObject = new JSONObject();
        try {
            final JSONObject userInfo = new JSONObject();
            TmfMiniSDK.callMainProcessPlugin(OpenDataIPC.OPEN_DATA_IPC_EVENT_GET_USER_ID, new Bundle(), new IpcCallback() {
                @Override
                public void result(boolean b, Bundle bundle) {
                    try {
                        userInfo.put("nickName", bundle.getString("userId"));
                        userInfo.put("avatarUrl", bundle.getString("avatarUrl"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            userInfo.put("gender", 0);
            userInfo.put("country", "CN");
            userInfo.put("province", "BeiJing");
            userInfo.put("city", "BeiJing");
            userInfo.put("language", "en");
            jsonObject.put("userInfo", userInfo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        result.onReceiveResult(true, jsonObject);
    }

    @Override
    public void getUserProfile(IMiniAppContext miniAppContext, JSONObject params, AsyncResult result) {
        QMLog.d(TAG, "getUserProfile:" + params);
        //mock api
        if (GlobalConfigureUtil.getGlobalConfig(miniAppContext.getContext()).mockApi) {
            JSONObject retData = new JSONObject();
            final JSONObject userInfo = new JSONObject();
            try {
                userInfo.put("nickName", "mockUser");
                userInfo.put("avatarUrl", "");
                userInfo.put("gender", 0);
                userInfo.put("country", "CN");
                userInfo.put("province", "BeiJing");
                userInfo.put("city", "BeiJing");
                userInfo.put("language", "en");
                retData.put("userInfo", userInfo);
            } catch (JSONException e) {

            }
            result.onReceiveResult(true, retData);
            return;
        }
        //real api
        JSONObject jsonObject = new JSONObject();
        try {
            final JSONObject userInfo = new JSONObject();
            TmfMiniSDK.callMainProcessPlugin(OpenDataIPC.OPEN_DATA_IPC_EVENT_GET_USER_ID, new Bundle(), new IpcCallback() {
                @Override
                public void result(boolean b, Bundle bundle) {
                    try {
                        userInfo.put("nickName", bundle.getString("userId"));
                        userInfo.put("avatarUrl", bundle.getString("avatarUrl"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            userInfo.put("gender", 0);
            userInfo.put("country", "CN");
            userInfo.put("province", "BeiJing");
            userInfo.put("city", "BeiJing");
            userInfo.put("language", "en");
            jsonObject.put("userInfo", userInfo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        result.onReceiveResult(true, jsonObject);
    }

    @Override
    public void requestPayment(IMiniAppContext miniAppContext, JSONObject params, AsyncResult result) {
        QMLog.d(TAG, "requestPayment:" + params);
        PaymentManager.g().startPayment(miniAppContext, params, result);
    }
}
