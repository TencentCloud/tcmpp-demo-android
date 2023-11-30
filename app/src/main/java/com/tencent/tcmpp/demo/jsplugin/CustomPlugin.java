package com.tencent.tcmpp.demo.jsplugin;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.tencent.tcmpp.demo.Constants;
import com.tencent.tcmpp.demo.TransActivity;
import com.tencent.tmf.mini.api.TmfMiniSDK;
import com.tencent.tmfmini.sdk.annotation.JsEvent;
import com.tencent.tmfmini.sdk.annotation.JsPlugin;
import com.tencent.tmfmini.sdk.launcher.core.model.RequestEvent;
import com.tencent.tmfmini.sdk.launcher.core.plugins.BaseJsPlugin;
import com.tencent.tmfmini.sdk.launcher.shell.IActivityResultListener;

import org.json.JSONException;
import org.json.JSONObject;

@JsPlugin(secondary = true)
public class CustomPlugin extends BaseJsPlugin {
    private static final String TAG = Constants.LOG_TAG;

    @JsEvent("testState")
    public void testState(final RequestEvent req) {

        try {
            //回调中间状态
            req.sendState(req, new JSONObject().put("progress", 1));
            req.sendState(req, new JSONObject().put("progress", 30));
            req.sendState(req, new JSONObject().put("progress", 60));
            req.sendState(req, new JSONObject().put("progress", 100));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("key", "test");
            req.ok(jsonObject);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @JsEvent("customAsyncEvent")
    public void custom(final RequestEvent req) {
        //获取参数
        //req.jsonParams
        //异步返回数据
        //req.fail();
        //req.ok();
        Log.d(TAG, "custom_async_event=" + req.jsonParams);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("key", "test");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        req.ok(jsonObject);
    }

    @JsEvent("customSyncEvent")
    public String custom1(final RequestEvent req) {
        //获取参数
        //req.jsonParams
        Log.d(TAG, "custom_sync_event=" + req.jsonParams);
        //同步返回数据(必须返回json数据)
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("key", "value");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return req.failSync(jsonObject, "aaaaaaaa");
    }

    /**
     * 测试覆盖系统API
     *
     * @param req
     */
    @JsEvent("getAppBaseInfo")
    public void getLocation(final RequestEvent req) {
        //获取参数
        //req.jsonParams
        //异步返回数据
        //req.fail();
        //req.ok();
        Log.d(TAG, "getAppBaseInfo=" + req.jsonParams);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("key", "test");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        req.ok(jsonObject);
    }

    /**
     * 测试小程序调气第三方APP完成分享、支付等功能后，直接返回到小程序，而不是返回到 APP
     *
     * @param req
     */
    @JsEvent("testStartActivityForResult")
    public void testStartActivityForResult(final RequestEvent req) {
        Activity activity = req.activityRef.get();
        TmfMiniSDK.addActivityResultListener(new IActivityResultListener() {
            @Override
            public boolean doOnActivityResult(int requestCode, int resultCode, Intent data) {
                TmfMiniSDK.removeActivityResultListener(this);

                Log.i(TAG, data.getStringExtra("key"));
                req.ok();
                return true;
            }
        });

        //注意：requestCode必须>=1000000
        activity.startActivityForResult(new Intent(activity, TransActivity.class), 1000000);
    }
}
