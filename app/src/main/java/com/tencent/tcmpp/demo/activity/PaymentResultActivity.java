package com.tencent.tcmpp.demo.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.tencent.tcmpp.demo.R;
import com.tencent.tcmpp.demo.open.login.Login;
import com.tencent.tcmpp.demo.open.login.LoginApi;

public class PaymentResultActivity extends AppCompatActivity {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_result);
    }

    @Override
    protected void onStart() {
        super.onStart();
        boolean success = getIntent().getBooleanExtra("success", true);

        String totalFee = getIntent().getStringExtra("total");
        if (!TextUtils.isEmpty(totalFee)) {
            float value = Integer.parseInt(totalFee) / 100.0f;
            TextView textView = findViewById(R.id.tv_pay_sum);
            textView.setText(value+"");
        }
        LoginApi.UserInfo userInfo = Login.g(this).getUserInfo();
        if (null != userInfo) {
            TextView textView = findViewById(R.id.tv_pay_user_name);
            textView.setText(userInfo.userId);
        }
        findViewById(R.id.btn_pay_finish).setOnClickListener(v -> finish());
    }
}
