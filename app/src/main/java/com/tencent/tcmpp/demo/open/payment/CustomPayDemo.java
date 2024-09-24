package com.tencent.tcmpp.demo.open.payment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.tencent.tcmpp.demo.R;

public class CustomPayDemo {

    public static final String TAG = "CustomPayDemo";

    public static void requestPay(Activity activity, double money, ICustomPayCallback callback) {
        if (activity == null) {
            callback.onPayResult(-1, "param activity can not be null");
        }
        showPwdDialog(activity, money, dialog -> {
            String pwd = ((CustomPayDialog) dialog).getInputText();
            Log.e(TAG, "onDismiss isComplete=" + pwd);
            if (!TextUtils.isEmpty(pwd)) {
                simulatePay(activity, pwd, money, callback);
            } else {
                callback.onPayResult(-2, "canceled by user");
            }
        });
    }

    private static void showPwdDialog(Activity activity, double money, OnDismissListener onDismissListener) {
        CustomPayDialog customPayDialog = new CustomPayDialog(activity, money, R.style.MyAlertDialog);
        customPayDialog.show();
        customPayDialog.setOnDismissListener(onDismissListener);
    }

    private static void simulatePay(Activity activity, String pwd, double money, ICustomPayCallback callback) {
        ProgressDialog loadingDialog = ProgressDialog.show(activity, "",
                "Please wait...", true);
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadingDialog.dismiss();
                if (TextUtils.equals(pwd, "666666")){
                    callback.onPayResult(0, "ok");
                }else{
                    callback.onPayResult(-3, "wrong pwd");
                }
            }
        }, 1000);
    }

    public static class CustomPayDialog extends AlertDialog implements View.OnClickListener {

        PwdEditText mPwdInputView;
        ImageView mCloseBtn;
        TextView mTvForgetPwd;

        TextView mTvCount;

        double mCount;

        String mInputText;

        public String getInputText() {
            return mInputText;
        }

        public CustomPayDialog(Context context, double count, int themeId) {
            super(context, themeId);
            mCount = count;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.applet_dialog_pay);
            mTvCount = findViewById(R.id.tvCount);
            mTvCount.setText(String.valueOf(mCount));
            mPwdInputView = findViewById(R.id.pwd);
            mPwdInputView.setOnInputFinishListener(text -> {
                mInputText = text;
                dismiss();
            });
            mCloseBtn = findViewById(R.id.close);
            mCloseBtn.setOnClickListener(this);
            mTvForgetPwd = findViewById(R.id.tvForgetPwd);
            mTvForgetPwd.setOnClickListener(this);

            getWindow().clearFlags(
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.close:
                    cancel();
                    break;
                case R.id.tvForgetPwd:
                    Toast.makeText(v.getContext(), "Current password:666666", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}
