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
public class MiniOpenApiProxyImpl implements MiniOpenApiProxy {
    private static final String TAG = "MiniOpenApiProxyImpl";

    @Override
    public void login(IMiniAppContext miniAppContext, JSONObject params, AsyncResult result) {
        QMLog.d(TAG, "login:" + params);
        //mock api
        if (GlobalConfigureUtil.getGlobalConfig(miniAppContext.getContext()).mockApi) {
            JSONObject retData = new JSONObject();
            try {
                retData.put("code", "9527");
            } catch (JSONException e) {
            }
            result.onReceiveResult(true, retData);
            return;
        }
        //real api
        Login login = Login.g(miniAppContext.getContext());
        LoginApi.UserInfo userInfo = login.getUserInfo();
        if (userInfo == null) {
            getAuthCodeWithLogin(miniAppContext, result);
        } else {
            login.getAuthCode(miniAppContext.getMiniAppInfo().appId, (i, s, s2) -> {
                if (i == 0) {
                    JSONObject retData = new JSONObject();
                    try {
                        retData.put("code", s2);
                        result.onReceiveResult(true, retData);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        callBackError(-1, "json exception:" + e, result);
                    }
                } else {
                    if (i == LoginApi.ERROR_TOKEN) {
                        getAuthCodeWithLogin(miniAppContext, result);
                    } else {
                        callBackError(i, s, result);
                    }
                }
            });
        }
    }

    private void getAuthCodeWithLogin(IMiniAppContext miniAppContext, AsyncResult result) {
        Login login = Login.g(miniAppContext.getContext());
        TmfMiniSDK.callMainProcessPlugin(OpenDataIPC.OPEN_DATA_IPC_EVENT_GET_USER_ID, new Bundle(), (b, bundle) -> {
            if (b) {
                String userId = bundle.getString("userId");
                login.login(userId, "123456", (i, s, userInfo1) -> {
                    if (i == 0) {
                        login.getAuthCode(miniAppContext.getMiniAppInfo().appId, (i1, s1, s2) -> {
                            if (i1 == 0) {
                                JSONObject retData = new JSONObject();
                                try {
                                    retData.put("code", s2);
                                    result.onReceiveResult(true, retData);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    callBackError(-1, "json exception:" + e, result);
                                }
                            }
                        });
                    } else {
                        callBackError(i, s, result);
                    }
                });
            } else {
                callBackError(-2, "get userId failed!", result);
            }
        });
    }

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
                userInfo.put("nickName","mockUser");
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
                userInfo.put("nickName","mockUser");
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
    public void getPhoneNumber(IMiniAppContext miniAppContext, JSONObject params, AsyncResult result) {
        QMLog.d(TAG, "getPhoneNumber:" + params);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("key", "wx.getPhoneNumber");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        result.onReceiveResult(true, jsonObject);
    }

    @Override
    public void requestPayment(IMiniAppContext miniAppContext, JSONObject params, AsyncResult result) {
        QMLog.d(TAG, "requestPayment:" + params);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("key", "wx.requestPayment");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        PaymentManager.g(miniAppContext.getContext()).startPayment(miniAppContext, params, result);
//        result.onReceiveResult(true, jsonObject);
    }
}
