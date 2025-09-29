package com.tencent.tcmpp.demo.proxy;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tencent.tmf.mini.api.bean.MiniAppWaterMarkPriority;
import com.tencent.tmfmini.sdk.annotation.ProxyService;
import com.tencent.tmfmini.sdk.launcher.core.proxy.IWaterMakerProxy;
import com.tencent.tmfmini.sdk.launcher.model.MiniAppInfo;

import org.json.JSONObject;

@ProxyService(proxy = IWaterMakerProxy.class)
public class MiniWaterMarkProxy implements IWaterMakerProxy {

    @Override
    public MiniAppWaterMarkPriority getWaterMarkPriority() {
        return MiniAppWaterMarkPriority.GLOBAL;
    }

    @Override
    public boolean enableWaterMark() {
        return false;
    }

    @Override
    public View createWatermarkView(@NonNull Context context, @NonNull LayoutParams layoutParams,
            @NonNull MiniAppInfo finAppInfo, @Nullable JSONObject jsonObject) {
        Log.d("TAG", "createWatermarkView ");
        TextView textView = new TextView(context);
        textView.setText("TCMPP Auth");
        textView.setTextColor(Color.RED);
        textView.setTextSize(30);
        layoutParams.topMargin = 600;
        layoutParams.width = 1000;
        layoutParams.height = 1000;
        layoutParams.leftMargin = 100;
        return textView;
    }
}
