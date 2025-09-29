package com.tencent.tcmpp.demo.proxy

import android.app.Activity
import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.LongSparseArray
import android.view.MotionEvent
import android.view.Surface
import com.tencent.tmfmini.sdk.annotation.ProxyService
import com.tencent.tmfmini.sdk.launcher.core.proxy.EmbeddedExternalElementProxy
import org.json.JSONException
import org.json.JSONObject

/**
 * Created by MadCat Yi on 2023/10/25.
 */
@ProxyService(proxy = EmbeddedExternalElementProxy::class)
class EmbeddedExternalElementProxyImpl : EmbeddedExternalElementProxy() {

    private val mEmbeddedWidgets = LongSparseArray<TestEmbeddedWidget>()

    override fun onInit(context: Context, widgetId: Long, tagName: String, attributes: Map<String, String>) {
        mEmbeddedWidgets.put(widgetId, TestEmbeddedWidget(context))
    }

    override fun onSurfaceCreated(widgetId: Long, surface: Surface) {
        val widget = mEmbeddedWidgets.get(widgetId)
        if (widget != null) {
            widget.setSurface(surface)
        }
    }

    override fun onSurfaceDestroyed(widgetId: Long, surface: Surface) {
        val widget = mEmbeddedWidgets.get(widgetId)
        widget?.setSurface(null)
    }

    override fun onTouchEvent(widgetId: Long, event: MotionEvent): Boolean {
        return false
    }

    override fun onRectChanged(widgetId: Long, rect: Rect) {
        // Implementation for rect changed
    }

    override fun onRequestRedraw(widgetId: Long) {
        val widget = mEmbeddedWidgets.get(widgetId)
        widget?.draw()
    }

    override fun onVisibilityChanged(widgetId: Long, visibility: Boolean) {
        // Implementation for visibility changed
    }

    override fun onActive(widgetId: Long) {
        // Implementation for active
    }

    override fun onDeActive(widgetId: Long) {
        // Implementation for deactive
    }

    override fun onDestroy(widgetId: Long) {
        val widget = mEmbeddedWidgets.get(widgetId)
        if (widget != null) {
            widget.setSurface(null)
            mEmbeddedWidgets.remove(widgetId)
        }
    }

    override fun webViewPause(widgetId: Long) {
        // Implementation for webview pause
    }

    override fun webViewResume(widgetId: Long) {
        // Implementation for webview resume
    }

    override fun webViewDestroy(widgetId: Long) {
        // Implementation for webview destroy
    }

    override fun nativeResume(widgetId: Long) {
        // Implementation for native resume
    }

    override fun nativePause(widgetId: Long) {
        // Implementation for native pause
    }

    override fun nativeDestroy(widgetId: Long) {
        // Implementation for native destroy
    }

    override fun handleInsertXWebExternalElement(
        widgetId: Long,
        widgetContext: XWebExternalWidgetContext,
        type: String,
        req: JSONObject
    ) {
        // Implementation for insert xweb external element
    }

    override fun handleUpdateXWebExternalElement(
        widgetId: Long,
        widgetContext: XWebExternalWidgetContext,
        req: JSONObject
    ) {
        val widget = mEmbeddedWidgets.get(widgetId)
        if (widget != null && widget.activity != null) {
            widget.activity!!.runOnUiThread {
                widget.draw()
            }
        }
    }

    override fun handleOperateXWebExternalElement(
        widgetId: Long,
        widgetContext: XWebExternalWidgetContext,
        req: JSONObject
    ) {
        val widget = mEmbeddedWidgets.get(widgetId)
        if (widget != null && widget.activity != null) {
            widget.activity!!.runOnUiThread {
                widget.changeColor()
                try {
                    val callbackData = JSONObject()
                    callbackData.put("result", "change color success!")
                    widgetContext.callbackSuccess(callbackData)

                    val event = JSONObject()
                    event.put("data", "this is an event!")
                    widgetContext.onXWebExternalElementEvent(event)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun handleRemoveXWebExternalElement(
        widgetId: Long,
        widgetContext: XWebExternalWidgetContext
    ) {
        // Implementation for remove xweb external element
    }

    private class TestEmbeddedWidget(private val mContext: Context) {
        private var mSurface: Surface? = null
        private val mTextPaint = Paint()
        private var mIsBlack = false

        fun setSurface(surface: Surface?) {
            this.mSurface = surface
        }

        fun draw() {
            mSurface?.let { surface ->
                val canvas = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    surface.lockHardwareCanvas()
                } else {
                    surface.lockCanvas(null)
                }
                
                canvas.drawColor(
                    if (mIsBlack) Color.WHITE else Color.BLACK,
                    PorterDuff.Mode.SRC
                )

                mTextPaint.apply {
                    color = if (mIsBlack) Color.BLACK else Color.WHITE
                    style = Paint.Style.FILL
                    textSize = 72f
                    isAntiAlias = true
                }
                canvas.drawText("Hello world", 50f, 150f, mTextPaint)

                surface.unlockCanvasAndPost(canvas)
            }
        }

        fun changeColor() {
            mIsBlack = !mIsBlack
            draw()
        }

        val activity: Activity?
            get() = if (mContext is Activity) mContext else null
    }
}