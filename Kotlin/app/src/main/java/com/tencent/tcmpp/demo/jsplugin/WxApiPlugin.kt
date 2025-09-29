package com.tencent.tcmpp.demo.jsplugin

import com.tencent.tcmpp.demo.Constants
import com.tencent.tmfmini.sdk.annotation.JsEvent
import com.tencent.tmfmini.sdk.annotation.JsPlugin
import com.tencent.tmfmini.sdk.launcher.core.model.RequestEvent
import com.tencent.tmfmini.sdk.launcher.core.plugins.BaseJsPlugin
import org.json.JSONException
import org.json.JSONObject

@JsPlugin(secondary = true)
class WxApiPlugin : BaseJsPlugin() {

    @JsEvent("login")
    fun login(req: RequestEvent) {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("key", "wx.login${mMiniAppInfo.appId}")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        req.ok(jsonObject)
    }

    @JsEvent("getUserInfo")
    fun getUserInfo(req: RequestEvent) {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("key", "wx.getUserInfo")
            val userInfo = JSONObject()
            userInfo.put("nickName", Constants.USER_INFO_NAME)
            userInfo.put("avatarUrl", Constants.USER_INFO_AVATAR_URL)
            jsonObject.put("userInfo", userInfo)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        req.ok(jsonObject)
    }

    @JsEvent("getUserProfile")
    fun getUserProfile(req: RequestEvent) {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("key", "wx.getUserProfile")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        req.ok(jsonObject)
    }
}