package com.tencent.tcmpp.demo.open.payment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.tencent.tcmpp.demo.R;
import com.tencent.tcmpp.demo.activity.PaymentResultActivity;
import com.tencent.tcmpp.demo.utils.GlobalConfigureUtil;
import com.tencent.tmfmini.sdk.launcher.core.IMiniAppContext;
import com.tencent.tmfmini.sdk.launcher.core.proxy.AsyncResult;

import org.json.JSONException;
import org.json.JSONObject;

public class PaymentManager {
    private static final String TAG = "PaymentManager";
    private static PaymentManager instance;
    private final PayApi payApi = new PayApi();

    public static PaymentManager g(Context context) {
        if (instance == null) {
            instance = new PaymentManager();
        }
        return instance;
    }

    public void startPayment(IMiniAppContext miniAppContext, JSONObject params, AsyncResult result) {
        //do params check
        if (params.has("prepayId")) {
            if (isMock(miniAppContext.getContext())) {//mock check order
                JSONObject mockData = new JSONObject();
                try {
                    mockData.put("total_fee", 9999);
                } catch (JSONException e) {
                }
                checkOrderMock(miniAppContext, mockData, result);
            } else {//real check order
                checkOrder(miniAppContext, params, result);
            }
        } else {
            JSONObject failRet = new JSONObject();
            try {
                failRet.put("success", false);
            } catch (JSONException e) {
            }
            result.onReceiveResult(false, failRet);
        }
    }

    /**
     * STEP 1: check order status
     *
     * @param miniAppContext
     * @param params
     * @param result
     */
    private void checkOrder(IMiniAppContext miniAppContext, JSONObject params, AsyncResult result) {
        String appId = miniAppContext.getMiniAppInfo().appId;
        Activity activity = miniAppContext.getAttachedActivity();
        payApi.checkOrder(appId, params, new PayApi.PayCallBack() {
            @Override
            public void onSuccess(JSONObject checkRet) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String fee = checkRet.optString("total_fee");
                        double paymentValue = Double.parseDouble(fee) / 100;
                        showPwdDialog(activity, paymentValue, dialog -> {
                            String pwd = ((CustomPayDemo.CustomPayDialog) dialog).getInputText();
                            Log.e(TAG, "onDismiss isComplete=" + pwd);
                            if (!TextUtils.isEmpty(pwd)) {
                                checkPwdAndPay(activity, pwd, checkRet, result);
                            } else {
                                Log.e(TAG, "empty pwd ~");
                                result.onReceiveResult(false, new JSONObject());
                            }
                        });
                    }
                });

            }

            @Override
            public void onFailed(int errCode, String msg) {
                Log.e(TAG, "pay failed " + msg);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(miniAppContext.getContext(), "failed", Toast.LENGTH_SHORT).show();
                    }
                });
                result.onReceiveResult(false, new JSONObject());
            }
        });
    }

    /**
     * STEP 2: show pwd dialog
     *
     * @param activity
     * @param money
     * @param onDismissListener
     */
    private void showPwdDialog(Activity activity, double money, DialogInterface.OnDismissListener onDismissListener) {
        CustomPayDemo.CustomPayDialog customPayDialog = new CustomPayDemo.CustomPayDialog(activity, money, R.style.MyAlertDialog);
        customPayDialog.show();
        customPayDialog.setOnDismissListener(onDismissListener);
    }

    /**
     * STEP 3: confirm pwd input and check pwd
     *
     * @param activity
     * @param pwd
     * @param data
     * @param result
     */
    private void checkPwdAndPay(Activity activity, String pwd, JSONObject data, AsyncResult result) {
        if (checkPassWord(pwd)) {
            if (isMock(activity)) {//mock payment
                requestPaymentByMock(activity, data, result);
            } else {//real payment
                requestPayment(activity, data, result);
            }
        } else {
            result.onReceiveResult(false, new JSONObject());
        }
    }

    private boolean checkPassWord(String pwd) {
        return "666666".equals(pwd);
    }


    /**
     * STEP 4: request payment
     *
     * @param activity
     * @param data
     * @param asyncResult
     */
    private void requestPayment(Activity activity, JSONObject data, AsyncResult asyncResult) {
        //real payment
        payApi.payOrder(data, new PayApi.PayCallBack() {
            @Override
            public void onSuccess(JSONObject result) {
                String totalFee = data.optString("total_fee");
                showPayResult(true, activity, totalFee);
                asyncResult.onReceiveResult(true, new JSONObject());
            }

            @Override
            public void onFailed(int errCode, String msg) {
                asyncResult.onReceiveResult(false, new JSONObject());

            }
        });
    }


    private void checkOrderMock(IMiniAppContext miniAppContext, JSONObject params, AsyncResult result) {
        Activity activity = miniAppContext.getAttachedActivity();
        String fee = params.optString("total_fee");
        double paymentValue = Double.parseDouble(fee) / 100;
        showPwdDialog(activity, paymentValue, dialog -> {
            String pwd = ((CustomPayDemo.CustomPayDialog) dialog).getInputText();
            Log.e(TAG, "onDismiss isComplete=" + pwd);
            if (!TextUtils.isEmpty(pwd)) {
                checkPwdAndPay(activity, pwd, params, result);
            } else {
                Log.e(TAG, "empty pwd ~");
                result.onReceiveResult(false, new JSONObject());
            }
        });
    }

    private void requestPaymentByMock(Activity activity, JSONObject data, AsyncResult asyncResult) {
        String totalFee = data.optString("total_fee");
        showPayResult(true, activity, totalFee);
        asyncResult.onReceiveResult(true, new JSONObject());
    }

    private boolean isMock(Context context) {
        return GlobalConfigureUtil.getGlobalConfig(context).mockApi;
    }

    private void showPayResult(boolean success, Activity activity, String total) {
        Intent intent = new Intent(activity, PaymentResultActivity.class);
        intent.putExtra("success", success);
        intent.putExtra("total", total);
        activity.startActivity(intent);
    }
}
