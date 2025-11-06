package com.tencent.tcmpp.demo.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.tencent.tcmpp.demo.R;
import com.tencent.tmf.mini.api.TmfMiniSDK;

public class MainTaskStackActivity extends BaseActivity {

    private static final String EXTRA_TITLE = "title";
    private static final String EXTRA_MINI_APP_ID = "miniAppId";
    private static final String EXTRA_VER_TYPE = "verType";

    public static void start(Activity activity, String data, String miniAppId, int verType) {
        Intent intent = new Intent(activity, MainTaskStackActivity.class);
        intent.putExtra(EXTRA_TITLE, data);
        intent.putExtra(EXTRA_MINI_APP_ID, miniAppId);
        intent.putExtra(EXTRA_VER_TYPE, verType);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    private String mMiniAppId;
    private int mVerType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_example);
        TextView titleView = findViewById(R.id.tv_title);
        String title = getIntent().getStringExtra(EXTRA_TITLE);
        mMiniAppId = getIntent().getStringExtra(EXTRA_MINI_APP_ID);
        mVerType = getIntent().getIntExtra(EXTRA_VER_TYPE, 0);
        if (TextUtils.isEmpty(title)) {
            title = "main app page";
        }
        titleView.setText(title);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //Monitor the back event and use the backToMiniApp interface to return to the mini program page
        //The miniAppId and verType parameters can be obtained through the MiniAppInfo object in BaseJsPlugin
        if (!TextUtils.isEmpty(mMiniAppId)) {
            TmfMiniSDK.backToMiniApp(mMiniAppId, mVerType);
        }
    }
}
