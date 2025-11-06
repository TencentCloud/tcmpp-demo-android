package com.tencent.tcmpp.demo.open.payment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.tencent.tcmpp.demo.R;
import com.tencent.tcmpp.demo.activity.PaymentResultActivity;
import com.tencent.tcmpp.demo.open.login.Login;
import com.tencent.tcmpp.demo.open.login.LoginApi;
import com.tencent.tcmpp.demo.utils.GlobalConfigureUtil;
import com.tencent.tmfmini.sdk.launcher.core.IMiniAppContext;
import com.tencent.tmfmini.sdk.launcher.core.proxy.AsyncResult;
import com.tencent.tmfmini.sdk.tmf.SharkUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class PaymentManager {
    private static final String TAG = "PaymentManager";
    private static PaymentManager instance;
    private final PayApi payApi = new PayApi();
    private PaymentRequest paymentRequest;
    private CustomPayDemo.CustomPayDialog mCustomPayDialog;

    public static PaymentManager g() {
        if (instance == null) {
            instance = new PaymentManager();
        }
        return instance;
    }

    public void startPayment(IMiniAppContext miniAppContext, JSONObject params, AsyncResult result) {
        //do params check
        if (!params.has("prepayId")) {
            //ill params
            JSONObject failRet = new JSONObject();
            try {
                failRet.put("success", false);
            } catch (JSONException ignored) {
            }
            result.onReceiveResult(false, failRet);
            return;
        }
        //mock check order
        if (isMock(miniAppContext.getContext())) {
            JSONObject mockData = new JSONObject();
            try {
                mockData.put("total_fee", 9999);
            } catch (JSONException ignored) {
            }
            checkOrderMock(miniAppContext, mockData, result);
            return;
        }
        //real check order
        checkOrder(miniAppContext, params, result, PayApi.PAY_TYPE_APP);
    }

    public void notifyPaymentCancel() {
        if (null != paymentRequest) {
            JSONObject ret = new JSONObject();
            try {
                ret.put("errMsg", "cancel");
            } catch (JSONException ignored) {
            }
            paymentRequest.result.onReceiveResult(false, ret);
            paymentRequest = null;
        }
    }

    public void showPwdConfirm(Activity activity, int iconSrc, String desc, String model, String modelId) {
        if (null != paymentRequest) {
            AsyncResult result = paymentRequest.result;
            JSONObject checkRet = paymentRequest.params;
            try {
                checkRet.put("appId", paymentRequest.miniAppContext.getMiniAppInfo().appId);
            } catch (JSONException | NullPointerException ignored) {
            }
            showPwdDialog(activity, paymentRequest.payValue, (retCode, msg, dialogInterface) -> {
                if (retCode == 1) {
                    String pwd = ((CustomPayDemo.CustomPayDialog) dialogInterface).getInputText();
                    Log.e(TAG, "onDismiss isComplete=" + pwd);
                    if (!TextUtils.isEmpty(pwd)) {
                        checkPwdAndPay(activity, pwd, checkRet, result, model, modelId);
                    } else {
                        Log.e(TAG, "empty pwd ~");
                        JSONObject ret = new JSONObject();
                        try {
                            ret.put("errMsg", "empty pws");
                        } catch (JSONException ignored) {
                        }
                        result.onReceiveResult(false, ret);
                        dismissPwdDialog();
                        activity.finish();
                    }
                    paymentRequest = null;
                }
            }, iconSrc, desc);
        }
    }

    /**
     * STEP 1: check order status
     *
     * @param miniAppContext
     * @param params
     * @param result
     * @param type           0 mini app,1 mini game
     */
    private void checkOrder(IMiniAppContext miniAppContext, JSONObject params, AsyncResult result, int type) {
        Activity activity = miniAppContext.getAttachedActivity();
        if (type == PayApi.PAY_TYPE_GAME) {
            dealPayTypeGame(miniAppContext, params);
        }
        payApi.checkOrder(params, type, new PayApi.PayCallBack() {
            @Override
            public void onSuccess(JSONObject checkRet) {
                gotoPayPage(activity, miniAppContext, checkRet, result);
            }

            @Override
            public void onFailed(int errCode, String msg) {
                Log.e(TAG, "pay failed " + msg);
                activity.runOnUiThread(() -> Toast.makeText(miniAppContext.getContext(), msg, Toast.LENGTH_SHORT).show());
                result.onReceiveResult(false, new JSONObject());
            }
        });
    }

    public void gotoPayPage(Activity activity, IMiniAppContext miniAppContext, JSONObject checkRet, AsyncResult result) {
        activity.runOnUiThread(() -> {
            String fee = checkRet.optString("actualAmount");
            double paymentValue = Double.parseDouble(fee) / 10000;
            Intent intent = new Intent(activity, PaymentMethodActivity.class);
            intent.setAction(Intent.ACTION_VIEW);
            intent.putExtra("totalFee", paymentValue);
            intent.putExtra("rawData", checkRet.toString());
            activity.startActivity(intent);
            paymentRequest = new PaymentRequest(miniAppContext, checkRet, result, paymentValue);
            Log.e("TAG", "payment request " + this.hashCode());
        });
    }

    private void dealPayTypeGame(IMiniAppContext miniAppContext, JSONObject params) {
        if (null == params) {
            Log.e(TAG, "dealPayTypeGame:ill params");
            return;
        }
        LoginApi.UserInfo userInfo = Login.g(miniAppContext.getContext()).getUserInfo();
        if (null == userInfo) {
            Log.e(TAG, "dealPayTypeGame:get user token failed");
            return;
        }
        try {
            params.put("token", userInfo.token);
            String appKey = SharkUtils.getAppKey();
            params.put("appId", appKey);
            Log.e("TAG", "put token for game " + userInfo.token);
        } catch (JSONException ignored) {
        }
    }

    /**
     * STEP 2: show pwd dialog
     *
     * @param activity
     * @param money
     */
    private void showPwdDialog(Activity activity, double money, ICustomPayCallback payCallback, int iconSrc, String desc) {
        mCustomPayDialog = new CustomPayDemo.CustomPayDialog(activity, money, R.style.MyAlertDialog, iconSrc, desc);
        mCustomPayDialog.addPayResultListen(payCallback);
        mCustomPayDialog.show();
    }

    private void dismissPwdDialog() {
        if (null != mCustomPayDialog) {
            mCustomPayDialog.dismiss();
            mCustomPayDialog = null;
        }
    }

    /**
     * STEP 3: confirm pwd input and check pwd
     *
     * @param activity
     * @param pwd
     * @param data
     * @param result
     */
    private void checkPwdAndPay(Activity activity, String pwd, JSONObject data, AsyncResult result, String payModel, String payModelId) {
        if (checkPassWord(pwd)) {
            if (isMock(activity) || data.optBoolean("isMock")) {//mock payment
                requestPaymentByMock(activity, data, result);
            } else {//real payment
                requestPayment(activity, data, result, payModel, payModelId);
            }
        } else {
            JSONObject ret = new JSONObject();
            try {
                ret.put("errMsg", "bad pwd ");
            } catch (JSONException ignored) {
            }
            result.onReceiveResult(false, ret);
            dismissPwdDialog();
            activity.finish();
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
    private void requestPayment(Activity activity, JSONObject data, AsyncResult asyncResult, String payModel, String paymdelId) {
        //real payment
        payApi.payOrder(data, new PayApi.PayCallBack() {
            @Override
            public void onSuccess(JSONObject result) {
                String totalFee = result.optString("paymentAmount");
                showPayResult(true, activity, totalFee);
                JSONObject resultToJs = result.optJSONObject("data");
                try {
                    resultToJs.put("errCode", "0");
                } catch (JSONException ignored) {
                }
                asyncResult.onReceiveResult(true, resultToJs);
            }

            @Override
            public void onFailed(int errCode, String msg) {
                JSONObject ret = new JSONObject();
                try {
                    ret.put("errMsg", msg);
                    ret.put("errCode", errCode);
                } catch (JSONException ignored) {
                }
                asyncResult.onReceiveResult(false, ret);
                dismissPwdDialog();
                activity.finish();

            }
        }, payModel, paymdelId);
    }


    private void checkOrderMock(IMiniAppContext miniAppContext, JSONObject params, AsyncResult result) {
        Activity activity = miniAppContext.getAttachedActivity();
        String fee = params.optString("total_fee");
        double paymentValue = Double.parseDouble(fee) / 10000;
        showPwdDialog(activity, paymentValue, (retCode, msg, dialogInterface) -> {
            String pwd = ((CustomPayDemo.CustomPayDialog) dialogInterface).getInputText();
            Log.e(TAG, "onDismiss isComplete=" + pwd);
            if (!TextUtils.isEmpty(pwd)) {
                checkPwdAndPay(activity, pwd, params, result, "wechat", "wechat pay");
            } else {
                Log.e(TAG, "empty pwd ~");
                result.onReceiveResult(false, new JSONObject());
            }
        }, R.drawable.pay_method_wechat, miniAppContext.getContext().getString(R.string.tcmpp_payment_we_chat_pay));
    }

    private void requestPaymentByMock(Activity activity, JSONObject data, AsyncResult asyncResult) {
        String totalFee = data.optString("total_fee");
        showPayResult(true, activity, totalFee);
        asyncResult.onReceiveResult(true, data);
    }

    private boolean isMock(Context context) {
        return GlobalConfigureUtil.getGlobalConfig(context).mockApi;
    }

    private void showPayResult(boolean success, Activity activity, String total) {
        Intent intent = new Intent(activity, PaymentResultActivity.class);
        intent.setAction(Intent.ACTION_VIEW);
        intent.putExtra("success", success);
        intent.putExtra("total", total);
        dismissPwdDialog();
        activity.startActivity(intent);
        activity.finish();
    }

    private static class PaymentRequest {
        public IMiniAppContext miniAppContext;
        public JSONObject params;
        public AsyncResult result;
        public double payValue;

        public PaymentRequest(IMiniAppContext miniAppContext, JSONObject params, AsyncResult result, double payValue) {
            this.miniAppContext = miniAppContext;
            this.params = params;
            this.result = result;
            this.payValue = payValue;

        }
    }
}
