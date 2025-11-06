package com.tencent.tcmpp.demo.jsplugin;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.tencent.tcmpp.demo.activity.MainTaskStackActivity;
import com.tencent.tcmpp.demo.fragment.LoginDialogFragment;
import com.tencent.tcmpp.demo.open.payment.PaymentManager;
import com.tencent.tmfmini.sdk.annotation.JsEvent;
import com.tencent.tmfmini.sdk.annotation.JsPlugin;
import com.tencent.tmfmini.sdk.launcher.core.model.RequestEvent;
import com.tencent.tmfmini.sdk.launcher.core.plugins.BaseJsPlugin;
import com.tencent.tmfmini.sdk.launcher.log.QMLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@JsPlugin(secondary = true)
public class CustomPagePlugin extends BaseJsPlugin {

    @JsEvent("mockLogin")
    public void mockLogin(final RequestEvent req) {
        Activity activity = mMiniAppContext.getAttachedActivity();
        if (activity.isFinishing()) {
            req.fail("app activity already finished");
            return;
        }
        try {
            JSONObject jsonObject = new JSONObject(req.jsonParams).optJSONObject("data");
            if (jsonObject == null) {
                req.fail("ill params");
                return;
            }
            String miniAppName = jsonObject.optString("miniAppName");
            LoginDialogFragment loginDialogFragment = LoginDialogFragment.newInstance(miniAppName);
            loginDialogFragment.setAuthListener((isAuth, userName) -> {
                JSONObject result = new JSONObject();
                try {
                    result.put("isAuth", isAuth);
                    result.put("userName", userName);
                } catch (JSONException e) {
                    req.fail("auth result illegal params");
                    return;
                }
                req.ok(result);
            });
            loginDialogFragment.show(activity.getFragmentManager(), "login");
        } catch (JSONException e) {
            req.fail("illegal params");
        }
    }

    @JsEvent("mockPayment")
    public void mockPayment(final RequestEvent req) {
        Activity activity = mMiniAppContext.getAttachedActivity();
        if (activity.isFinishing()) {
            req.fail("app activity already finished");
            return;
        }
        try {
            //mock payment default test password 666666
            JSONObject mockPayData = mockPayData();
            PaymentManager.g().gotoPayPage(activity, mMiniAppContext, mockPayData, (res, data) -> {
                QMLog.d("mockPayment", "result: " + data + "; success: " + res);
                if (res) {
                    req.ok(data);
                }else {
                    req.fail("pay failed");
                }
            });
        } catch (JSONException e) {
            req.fail("create mock pay data failed");
        }
    }

    @NonNull
    private JSONObject mockPayData() throws JSONException {
        //Simulate payment order data
        JSONObject mockPayData = new JSONObject();
        mockPayData.put("isMock", true);
        mockPayData.put("payId", "pid_17605202016339a8ua0jw4a");
        mockPayData.put("actualAmount", 1580000);
        mockPayData.put("total_fee", 1580000);
        JSONArray payModelList = new JSONArray();
        JSONObject payModel = new JSONObject();
        payModel.put("payModel", "Wallet");
        payModel.put("payModelName", "Wallet Pay");
        payModel.put("payModelIcon", "https://tcmpp-dev-renter-1258344699.cos.ap-guangzhou.tencentcos.cn/u9l3j82wdqc8zmsj/tcmpp/1cce9d3c-e91c-4c5b-b603-b63b1891ec8e.png");
        payModel.put("payModelId", "wallet_ws78gmvw52qrrc5ou3do");
        payModel.put("balance", "5451890000");
        payModelList.put(payModel);
        mockPayData.put("payModelList", payModelList);
        return mockPayData;
    }

    @JsEvent("mockRunMainAppPage")
    public void mockRunMainAppPage(final RequestEvent req) {
        Activity activity = mMiniAppContext.getAttachedActivity();
        if (activity.isFinishing()) {
            req.fail("app activity already finished");
            return;
        }
        try {
            JSONObject params = new JSONObject(req.jsonParams);
            String data = params.optString("data");
            MainTaskStackActivity.start(activity, data, mMiniAppInfo.appId, mMiniAppInfo.verType);
        } catch (JSONException e) {
            req.fail("illegal params");
            return;
        }
        req.ok();
    }
}
