package com.tencent.tcmpp.demo.proxy

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import com.tencent.tmf.mini.api.bean.MiniAppWaterMarkPriority
import com.tencent.tmfmini.sdk.annotation.ProxyService
import com.tencent.tmfmini.sdk.launcher.core.proxy.IWaterMakerProxy
import com.tencent.tmfmini.sdk.launcher.model.MiniAppInfo
import org.json.JSONObject

@ProxyService(proxy = IWaterMakerProxy::class)
class MiniWaterMarkProxy : IWaterMakerProxy {

    override fun getWaterMarkPriority(): MiniAppWaterMarkPriority {
        return MiniAppWaterMarkPriority.GLOBAL
    }

    override fun enableWaterMark(): Boolean {
        return false
    }

    override fun createWatermarkView(
        context: Context,
        layoutParams: RelativeLayout.LayoutParams,
        finAppInfo: MiniAppInfo,
        jsonObject: JSONObject?
    ): View {
        Log.d("TAG", "createWatermarkView ")
        val textView = TextView(context)
        textView.text = "TCMPP Auth"
        textView.setTextColor(Color.RED)
        textView.textSize = 30f
        layoutParams.topMargin = 600
        layoutParams.width = 1000
        layoutParams.height = 1000
        layoutParams.leftMargin = 100
        return textView
    }
}