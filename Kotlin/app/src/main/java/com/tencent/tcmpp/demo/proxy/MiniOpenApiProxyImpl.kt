package com.tencent.tcmpp.demo.proxy

import android.os.Bundle
import com.tencent.tcmpp.demo.ipcplugin.OpenDataIPC
import com.tencent.tcmpp.demo.open.login.Login
import com.tencent.tcmpp.demo.open.payment.PaymentManager
import com.tencent.tcmpp.demo.utils.GlobalConfigureUtil
import com.tencent.tmf.mini.api.TmfMiniSDK
import com.tencent.tmf.mini.api.callback.IpcCallback
import com.tencent.tmfmini.sdk.annotation.ProxyService
import com.tencent.tmfmini.sdk.launcher.core.IMiniAppContext
import com.tencent.tmfmini.sdk.launcher.core.proxy.AsyncResult
import com.tencent.tmfmini.sdk.launcher.core.proxy.MiniOpenApiProxy
import com.tencent.tmfmini.sdk.launcher.log.QMLog
import org.json.JSONException
import org.json.JSONObject

@ProxyService(proxy = MiniOpenApiProxy::class)
class MiniOpenApiProxyImpl : MiniOpenApiProxy() {

    companion object {
        private const val TAG = "MiniOpenApiProxyImpl"
    }

    private fun callBackError(code: Int, message: String, result: AsyncResult) {
        val retData = JSONObject()
        try {
            retData.put("errno", code)
            retData.put("errMsg", message)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        result.onReceiveResult(false, retData)
    }

    override fun checkSession(miniAppContext: IMiniAppContext, params: JSONObject, result: AsyncResult) {
        QMLog.d(TAG, "checkSession:$params")

        // mock api
        if (GlobalConfigureUtil.getGlobalConfig(miniAppContext.context).mockApi) {
            val retData = JSONObject()
            result.onReceiveResult(true, retData)
            return
        }

        // real api
        val jsonObject = JSONObject()
        val login = Login.g(miniAppContext.context)
        val userInfo = login.getUserInfo()
        result.onReceiveResult(userInfo != null, jsonObject)
    }

    override fun getUserInfo(miniAppContext: IMiniAppContext, params: JSONObject, result: AsyncResult) {
        QMLog.d(TAG, "getUserInfo:$params")

        // mock api
        if (GlobalConfigureUtil.getGlobalConfig(miniAppContext.context).mockApi) {
            val retData = JSONObject()
            val userInfo = JSONObject()
            try {
                userInfo.put("nickName", "mockUser")
                userInfo.put("avatarUrl", "")
                userInfo.put("gender", 0)
                userInfo.put("country", "CN")
                userInfo.put("province", "BeiJing")
                userInfo.put("city", "BeiJing")
                userInfo.put("language", "en")
                retData.put("userInfo", userInfo)
            } catch (e: JSONException) {
                // Handle exception
            }
            result.onReceiveResult(true, retData)
            return
        }

        // real api call
        val jsonObject = JSONObject()
        try {
            val userInfo = JSONObject()
            TmfMiniSDK.callMainProcessPlugin(
                OpenDataIPC.OPEN_DATA_IPC_EVENT_GET_USER_ID,
                Bundle(),
                object : IpcCallback {
                    override fun result(b: Boolean, bundle: Bundle) {
                        try {
                            userInfo.put("nickName", bundle.getString("userId"))
                            userInfo.put("avatarUrl", bundle.getString("avatarUrl"))
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                }
            )
            userInfo.put("gender", 0)
            userInfo.put("country", "CN")
            userInfo.put("province", "BeiJing")
            userInfo.put("city", "BeiJing")
            userInfo.put("language", "en")
            jsonObject.put("userInfo", userInfo)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        result.onReceiveResult(true, jsonObject)
    }

    override fun getUserProfile(miniAppContext: IMiniAppContext, params: JSONObject, result: AsyncResult) {
        QMLog.d(TAG, "getUserProfile:$params")

        // mock api
        if (GlobalConfigureUtil.getGlobalConfig(miniAppContext.context).mockApi) {
            val retData = JSONObject()
            val userInfo = JSONObject()
            try {
                userInfo.put("nickName", "mockUser")
                userInfo.put("avatarUrl", "")
                userInfo.put("gender", 0)
                userInfo.put("country", "CN")
                userInfo.put("province", "BeiJing")
                userInfo.put("city", "BeiJing")
                userInfo.put("language", "en")
                retData.put("userInfo", userInfo)
            } catch (e: JSONException) {
                // Handle exception
            }
            result.onReceiveResult(true, retData)
            return
        }

        // real api
        val jsonObject = JSONObject()
        try {
            val userInfo = JSONObject()
            TmfMiniSDK.callMainProcessPlugin(
                OpenDataIPC.OPEN_DATA_IPC_EVENT_GET_USER_ID,
                Bundle(),
                object : IpcCallback {
                    override fun result(b: Boolean, bundle: Bundle) {
                        try {
                            userInfo.put("nickName", bundle.getString("userId"))
                            userInfo.put("avatarUrl", bundle.getString("avatarUrl"))
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                }
            )
            userInfo.put("gender", 0)
            userInfo.put("country", "CN")
            userInfo.put("province", "BeiJing")
            userInfo.put("city", "BeiJing")
            userInfo.put("language", "en")
            jsonObject.put("userInfo", userInfo)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        result.onReceiveResult(true, jsonObject)
    }

    override fun requestPayment(miniAppContext: IMiniAppContext, params: JSONObject, result: AsyncResult) {
        QMLog.d(TAG, "requestPayment:$params")
        PaymentManager.g().startPayment(miniAppContext, params, result)
    }
}