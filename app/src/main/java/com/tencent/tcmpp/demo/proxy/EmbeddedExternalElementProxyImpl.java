package com.tencent.tcmpp.demo.proxy;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.util.LongSparseArray;
import android.view.MotionEvent;
import android.view.Surface;

import com.tencent.tmfmini.sdk.annotation.ProxyService;
import com.tencent.tmfmini.sdk.launcher.core.proxy.EmbeddedExternalElementProxy;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by MadCat Yi on 2023/10/25.
 */
@ProxyService(proxy = EmbeddedExternalElementProxy.class)
public class EmbeddedExternalElementProxyImpl extends EmbeddedExternalElementProxy {

    private LongSparseArray<TestEmbeddedWidget> mEmbeddedWidgets = new LongSparseArray<>();

    @Override
    public void onInit(Context context, long widgetId, String tagName, Map<String, String> attributes) {
        mEmbeddedWidgets.put(widgetId, new TestEmbeddedWidget(context));
    }

    @Override
    public void onSurfaceCreated(long widgetId, Surface surface) {
        TestEmbeddedWidget widget = mEmbeddedWidgets.get(widgetId);
        if (surface != null && widget != null) {
            widget.setSurface(surface);
        }
    }

    @Override
    public void onSurfaceDestroyed(long widgetId, Surface surface) {
        TestEmbeddedWidget widget = mEmbeddedWidgets.get(widgetId);
        if (widget != null) {
            widget.setSurface(null);
        }
    }

    @Override
    public boolean onTouchEvent(long widgetId, MotionEvent event) {
        return false;
    }

    @Override
    public void onRectChanged(long widgetId, Rect rect) {

    }

    @Override
    public void onRequestRedraw(long widgetId) {
        TestEmbeddedWidget widget = mEmbeddedWidgets.get(widgetId);
        if (widget != null) {
            widget.draw();
        }
    }

    @Override
    public void onVisibilityChanged(long widgetId, boolean visibility) {

    }

    @Override
    public void onActive(long widgetId) {

    }

    @Override
    public void onDeActive(long widgetId) {

    }

    @Override
    public void onDestroy(long widgetId) {
        TestEmbeddedWidget widget = mEmbeddedWidgets.get(widgetId);
        if (widget != null) {
            widget.setSurface(null);
            mEmbeddedWidgets.remove(widgetId);
        }
    }

    @Override
    public void webViewPause(long widgetId) {

    }

    @Override
    public void webViewResume(long widgetId) {

    }

    @Override
    public void webViewDestroy(long widgetId) {

    }

    @Override
    public void nativeResume(long widgetId) {

    }

    @Override
    public void nativePause(long widgetId) {

    }

    @Override
    public void nativeDestroy(long widgetId) {

    }

    @Override
    public void handleInsertXWebExternalElement(long widgetId, XWebExternalWidgetContext widgetContext, String type, JSONObject req) {

    }

    @Override
    public void handleUpdateXWebExternalElement(long widgetId, XWebExternalWidgetContext widgetContext, JSONObject req) {
        TestEmbeddedWidget widget = mEmbeddedWidgets.get(widgetId);
        if (widget != null && widget.getActivity() != null) {
            widget.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    widget.draw();
                }
            });
        }
    }

    @Override
    public void handleOperateXWebExternalElement(long widgetId, XWebExternalWidgetContext widgetContext, JSONObject req) {
        TestEmbeddedWidget widget = mEmbeddedWidgets.get(widgetId);
        if (widget != null && widget.getActivity() != null) {
            widget.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    widget.changeColor();
                    try {
                        JSONObject callbackData = new JSONObject();
                        callbackData.put("result", "change color success!");
                        widgetContext.callbackSuccess(callbackData);

                        JSONObject event = new JSONObject();
                        event.put("data", "this is an event!");
                        widgetContext.onXWebExternalElementEvent(event);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @Override
    public void handleRemoveXWebExternalElement(long widgetId, XWebExternalWidgetContext widgetContext) {

    }

    private static class TestEmbeddedWidget {

        private Context mContext;
        private Surface mSurface;
        private Paint mTextPaint = new Paint();
        private boolean mIsBlack = false;

        public TestEmbeddedWidget(Context context) {
            mContext = context;
        }

        public void setSurface(Surface surface) {
            this.mSurface = surface;
        }

        public void draw() {
            if (mSurface != null) {
                Canvas canvas = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    canvas = mSurface.lockHardwareCanvas();
                } else {
                    canvas = mSurface.lockCanvas(null);
                }
                canvas.drawColor(mIsBlack ? Color.WHITE : Color.BLACK, PorterDuff.Mode.SRC);

                mTextPaint.setColor(mIsBlack ? Color.BLACK : Color.WHITE);
                mTextPaint.setStyle(Paint.Style.FILL);
                mTextPaint.setTextSize(72);
                mTextPaint.setAntiAlias(true);
                canvas.drawText("Hello world", 50, 150, mTextPaint);

                mSurface.unlockCanvasAndPost(canvas);
            }
        }

        public void changeColor() {
            mIsBlack = !mIsBlack;
            draw();
        }

        public Activity getActivity() {
            if (mContext instanceof Activity) {
                return (Activity) mContext;
            }
            return null;
        }
    }
}
