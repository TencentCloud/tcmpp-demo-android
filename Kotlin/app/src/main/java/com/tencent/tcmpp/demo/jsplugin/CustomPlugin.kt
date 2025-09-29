package com.tencent.tcmpp.demo.jsplugin

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.tencent.tcmpp.demo.Constants
import com.tencent.tcmpp.demo.activity.TransActivity
import com.tencent.tmf.mini.api.TmfMiniSDK
import com.tencent.tmfmini.sdk.annotation.JsEvent
import com.tencent.tmfmini.sdk.annotation.JsPlugin
import com.tencent.tmfmini.sdk.launcher.core.model.RequestEvent
import com.tencent.tmfmini.sdk.launcher.core.plugins.BaseJsPlugin
import com.tencent.tmfmini.sdk.launcher.shell.IActivityResultListener
import org.json.JSONException
import org.json.JSONObject

@JsPlugin(secondary = true)
class CustomPlugin : BaseJsPlugin() {
    
    companion object {
        private const val TAG = Constants.LOG_TAG
    }

    @JsEvent("testState")
    fun testState(req: RequestEvent) {
        try {
            // Call back the intermediate state to JS
            req.sendState(req, JSONObject().put("progress", 1))
            req.sendState(req, JSONObject().put("progress", 30))
            req.sendState(req, JSONObject().put("progress", 60))
            req.sendState(req, JSONObject().put("progress", 100))
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val jsonObject = JSONObject()
        try {
            jsonObject.put("key", "test")
            req.ok(jsonObject)
        } catch (e: JSONException) {
            throw RuntimeException(e)
        }
    }

    @JsEvent("customAsyncEvent")
    fun custom(req: RequestEvent) {
        // Get parameters and return data asynchronously

        Log.d(TAG, "custom_async_event=${req.jsonParams}")
        val jsonObject = JSONObject()
        try {
            jsonObject.put("key", "test")
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        req.ok(jsonObject)
    }

    @JsEvent("customSyncEvent")
    fun custom1(req: RequestEvent): String {
        Log.d(TAG, "custom_sync_event=${req.jsonParams}")

        // Synchronous return data (json data must be returned)
        val jsonObject = JSONObject()
        try {
            jsonObject.put("key", "value")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return req.failSync(jsonObject, "aaaaaaaa")
    }

    /**
     * Test for Override system API
     */
    @JsEvent("getAppBaseInfo")
    fun getLocation(req: RequestEvent) {
        // Get parameters and return data asynchronously
        Log.d(TAG, "getAppBaseInfo=${req.jsonParams}")
        val jsonObject = JSONObject()
        try {
            jsonObject.put("key", "test")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        req.ok(jsonObject)
    }

    /**
     * The mini program calls a third-party APP to complete sharing, payment and other functions,
     * and returns directly to the mini program instead of returning to the APP.
     */
    @JsEvent("testStartActivityForResult")
    fun testStartActivityForResult(req: RequestEvent) {
        val activity = req.activityRef.get()
        TmfMiniSDK.addActivityResultListener(object : IActivityResultListener {
            override fun doOnActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
                TmfMiniSDK.removeActivityResultListener(this)

                data?.getStringExtra("key")?.let { 
                    Log.i(TAG, it) 
                }
                req.ok()
                return true
            }
        })

        // Note: requestCode must be >=1000000
        activity?.startActivityForResult(Intent(activity, TransActivity::class.java), 1000000)
    }
}