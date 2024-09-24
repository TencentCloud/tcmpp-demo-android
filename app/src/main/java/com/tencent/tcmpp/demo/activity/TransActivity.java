package com.tencent.tcmpp.demo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.tencent.tcmpp.demo.Constants;
import com.tencent.tcmpp.demo.R;
import com.tencent.tmfmini.sdk.launcher.utils.ProcessUtil;

public class TransActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.applet_activity_tran);

        Log.i(Constants.LOG_TAG, "TransActivity:" + ProcessUtil.getProcessName(this));

        Intent intent = new Intent();
        intent.setClassName("com.tencent.tmf.demo", "com.tencent.tmf.module.main.activtiy.TMFMainActivity");
        startActivityForResult(intent, 10000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i(Constants.LOG_TAG, data.getStringExtra("test"));
        Intent intent = new Intent();
        intent.putExtra("key", "value");
        setResult(RESULT_OK, intent);
        finish();
    }
}
