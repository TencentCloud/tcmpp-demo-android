package com.tencent.tcmpp.demo.open.payment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import java.util.Locale;

public class CustomPayDemo {

    public static final String TAG = "CustomPayDemo";

    public static void requestPay(Activity activity, double money, ICustomPayCallback callback) {
        if (activity == null) {
            callback.onPayResult(-1, "param activity can not be null", null);
        }
        showPwdDialog(activity, money, dialog -> {
            String pwd = ((CustomPayDialog) dialog).getInputText();
            Log.e(TAG, "onDismiss isComplete=" + pwd);
            if (!TextUtils.isEmpty(pwd)) {
                simulatePay(activity, pwd, money, callback);
            } else {
                callback.onPayResult(-2, "canceled by user", null);
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
                if (TextUtils.equals(pwd, "666666")) {
                    callback.onPayResult(0, "ok", null);
                } else {
                    callback.onPayResult(-3, "wrong pwd", null);
                }
            }
        }, 1000);
    }

    public static class CustomPayDialog extends AlertDialog implements View.OnClickListener {

        PwdEditText mPwdInputView;
        ImageView mCloseBtn;
        TextView mTvForgetPwd;

        TextView mTvCount;
        TextView mPayType;
        ImageView mPayTypeIv;

        double mCount;

        String mInputText;
        private ICustomPayCallback payCallback;
        private int payStatus = -1;//-1 Password not entered; 1. Enter password
        private int src;
        private String name;

        public String getInputText() {
            return mInputText;
        }


        public CustomPayDialog(Context context, double count, int themeId) {
            super(context, themeId);
            mCount = count;
            setOnDismissListener(new OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    if (null != payCallback) {
                        payCallback.onPayResult(payStatus, "", dialog);
                    }
                }
            });
        }

        public CustomPayDialog(Context context, double count, int themeId, int src, String desc) {
            super(context, themeId);
            mCount = count;
            setOnDismissListener(new OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    if (null != payCallback) {
                        payCallback.onPayResult(payStatus, "", dialog);
                    }
                }
            });
            this.name = desc;
            this.src = src;
        }


        public void addPayResultListen(ICustomPayCallback payCallback) {
            this.payCallback = payCallback;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.applet_dialog_pay);
            mTvCount = findViewById(R.id.tvCount);
            mTvCount.setText(String.format(Locale.getDefault(), "%.2f", mCount));
            mPwdInputView = findViewById(R.id.pwd);
            mPwdInputView.setOnInputFinishListener(text -> {
                mInputText = text;
                payStatus = 1;
                dismiss();
            });
            mCloseBtn = findViewById(R.id.close);
            mCloseBtn.setOnClickListener(this);
            mTvForgetPwd = findViewById(R.id.tvForgetPwd);
            mTvForgetPwd.setOnClickListener(this);
            mPayType = findViewById(R.id.pay_dialog_type);
            mPayTypeIv = findViewById(R.id.pay_dialog_icon);
            if (src != 0) {
                mPayTypeIv.setImageResource(src);
            }
            if (!TextUtils.isEmpty(name)) {
                mPayType.setText(name);
            }

            getWindow().clearFlags(
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        }

        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.close) {
                cancel();
            } else if (id == R.id.tvForgetPwd) {
                Toast.makeText(v.getContext(), "Current password:666666", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
