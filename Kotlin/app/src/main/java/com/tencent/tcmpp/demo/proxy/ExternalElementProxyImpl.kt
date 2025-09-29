package com.tencent.tcmpp.demo.proxy

import android.content.Context
import android.graphics.Color
import android.util.LongSparseArray
import android.util.TypedValue
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import com.tencent.tmfmini.sdk.annotation.ProxyService
import com.tencent.tmfmini.sdk.launcher.core.proxy.ExternalElementProxy
import org.json.JSONException
import org.json.JSONObject

/**
 * Created by MadCat Yi on 2023/10/25.
 */
@ProxyService(proxy = ExternalElementProxy::class)
class ExternalElementProxyImpl : ExternalElementProxy() {

    private val mEmbeddedWidgets = LongSparseArray<TestEmbeddedWidget>()

    override fun handleInsertElement(
        widgetId: Long,
        widgetContext: ExternalWidgetContext,
        type: String,
        parent: ViewGroup,
        params: JSONObject
    ) {
        val widget = TestEmbeddedWidget(parent.context)
        mEmbeddedWidgets.put(widgetId, widget)
        parent.post {
            val lp = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            parent.addView(widget, lp)
        }
    }

    override fun handleUpdateElement(
        widgetId: Long,
        widgetContext: ExternalWidgetContext,
        params: JSONObject
    ) {
        // Implementation for update element
    }

    override fun handleOperateElement(
        widgetId: Long,
        widgetContext: ExternalWidgetContext,
        params: JSONObject
    ) {
        val widget = mEmbeddedWidgets.get(widgetId)
        widget?.let {
            it.changeColor()
            try {
                val callbackData = JSONObject()
                callbackData.put("result", "change color success!")
                widgetContext.callbackSuccess(callbackData)

                val event = JSONObject()
                event.put("data", "this is an event!")
                widgetContext.onExternalElementEvent(event)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }

    override fun handleRemoveElement(widgetId: Long, widgetContext: ExternalWidgetContext) {
        // Implementation for remove element
    }

    private class TestEmbeddedWidget(context: Context) : AppCompatTextView(context) {
        private var mIsBlack = false

        init {
            setPadding(60, 60, 0, 0)
            text = "Hello world"
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20f)
            setBackgroundColor(Color.BLACK)
            setTextColor(Color.WHITE)
        }

        fun changeColor() {
            mIsBlack = !mIsBlack
            setBackgroundColor(if (mIsBlack) Color.WHITE else Color.BLACK)
            setTextColor(if (mIsBlack) Color.BLACK else Color.WHITE)
        }
    }
}