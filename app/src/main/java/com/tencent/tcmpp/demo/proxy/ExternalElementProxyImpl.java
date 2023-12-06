package com.tencent.tcmpp.demo.proxy;

import android.content.Context;
import android.graphics.Color;
import android.util.LongSparseArray;
import android.util.TypedValue;
import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatTextView;

import com.tencent.tmfmini.sdk.annotation.ProxyService;
import com.tencent.tmfmini.sdk.launcher.core.proxy.ExternalElementProxy;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by MadCat Yi on 2023/10/25.
 */
@ProxyService(proxy = ExternalElementProxy.class)
public class ExternalElementProxyImpl extends ExternalElementProxy {

    private LongSparseArray<TestEmbeddedWidget> mEmbeddedWidgets = new LongSparseArray<>();

    @Override
    public void handleInsertElement(long widgetId, ExternalWidgetContext widgetContext, String type, ViewGroup parent, JSONObject params) {
        TestEmbeddedWidget widget = new TestEmbeddedWidget(parent.getContext());
        mEmbeddedWidgets.put(widgetId, widget);
        parent.post(new Runnable() {
            @Override
            public void run() {
                ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                parent.addView(widget, lp);
            }
        });
    }

    @Override
    public void handleUpdateElement(long widgetId, ExternalWidgetContext widgetContext, JSONObject params) {

    }

    @Override
    public void handleOperateElement(long widgetId, ExternalWidgetContext widgetContext, JSONObject params) {
        TestEmbeddedWidget widget = mEmbeddedWidgets.get(widgetId);
        if (widget != null) {
            widget.changeColor();
            try {
                JSONObject callbackData = new JSONObject();
                callbackData.put("result", "change color success!");
                widgetContext.callbackSuccess(callbackData);

                JSONObject event = new JSONObject();
                event.put("data", "this is an event!");
                widgetContext.onExternalElementEvent(event);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void handleRemoveElement(long widgetId, ExternalWidgetContext widgetContext) {

    }

    private static class TestEmbeddedWidget extends AppCompatTextView {

        private Context mContext;
        private boolean mIsBlack = false;

        public TestEmbeddedWidget(Context context) {
            super(context);
            this.mContext = context;
            setPadding(60, 60, 0, 0);
            setText("Hello world");
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
            setBackgroundColor(Color.BLACK);
            setTextColor(Color.WHITE);
        }

        public void changeColor() {
            mIsBlack = !mIsBlack;
            setBackgroundColor(mIsBlack ? Color.WHITE : Color.BLACK);
            setTextColor(mIsBlack ? Color.BLACK : Color.WHITE);
        }
    }
}
