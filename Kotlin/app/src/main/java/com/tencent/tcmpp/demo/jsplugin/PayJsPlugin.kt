package com.tencent.tcmpp.demo.jsplugin

import android.content.DialogInterface
import com.tencent.tcmpp.demo.open.payment.CustomPayDemo
import com.tencent.tcmpp.demo.open.payment.ICustomPayCallback
import com.tencent.tmfmini.sdk.annotation.JsEvent
import com.tencent.tmfmini.sdk.annotation.JsPlugin
import com.tencent.tmfmini.sdk.launcher.core.model.RequestEvent
import com.tencent.tmfmini.sdk.launcher.core.plugins.BaseJsPlugin
import org.json.JSONObject

@JsPlugin(secondary = true)
class PayJsPlugin : BaseJsPlugin() {

    @JsEvent("requestPayment")
    fun requestPayment(req: RequestEvent) {
        try {
            val paramsObject = JSONObject(req.jsonParams)
            val data = paramsObject.optJSONObject("data")
            val count = data?.optDouble("money") ?: 0.0

            if (!mIsContainer && !mIsDestroyed) {
                CustomPayDemo.requestPay(mMiniAppContext.attachedActivity, count, object : ICustomPayCallback {
                    override fun onPayResult(retCode: Int, msg: String, dialogInterface: DialogInterface?) {
                        if (retCode == 0) {
                            req.ok()
                        } else {
                            req.fail(msg)
                        }
                    }
                })
            }
        } catch (e: Throwable) {
            req.fail("invalid params")
        }
    }
}