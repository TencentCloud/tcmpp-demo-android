package com.tencent.tcmpp.demo.jsplugin;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.tencent.tcmpp.demo.Constants;
import com.tencent.tcmpp.demo.activity.TransActivity;
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
            // Call back the intermediate state to JS
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
        // Get parameters and return data asynchronously
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
        Log.d(TAG, "custom_sync_event=" + req.jsonParams);

        // Synchronous return data (json data must be returned)
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("key", "value");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return req.failSync(jsonObject, "aaaaaaaa");
    }

    /**
     * Test for Override system API
     * @param req
     */
    @JsEvent("getAppBaseInfo")
    public void getLocation(final RequestEvent req) {
        // Get parameters and return data asynchronously
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
     * The mini program calls a third-party APP to complete sharing, payment and other functions,
     * and returns directly to the mini program instead of returning to the APP.
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

        // Note: requestCode must be >=1000000
        Intent intent = new Intent(activity, TransActivity.class);
        intent.setAction(Intent.ACTION_VIEW);
        activity.startActivityForResult(intent, 1000000);
    }
}
