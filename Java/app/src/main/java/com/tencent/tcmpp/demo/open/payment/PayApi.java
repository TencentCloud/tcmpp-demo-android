package com.tencent.tcmpp.demo.open.payment;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PayApi {
    private static final String TAG = "PayApi";
    private final int ERR_OKHTTP_ERROR = -1;
    private final int ERR_CHECK_ORDER_FAILED = -10;
    private OkHttpClient mRequestClient;
    public static final int PAY_TYPE_APP = 0;
    public static final int PAY_TYPE_GAME = 1;


    public void checkOrder(JSONObject params, int type, PayCallBack payCallBack) {
        String checkOrderApi = type == PAY_TYPE_GAME ? PayEnvironment.API_CHECK_GAME_ORDER : PayEnvironment.API_CHECK_APP_ORDER;
        request(checkOrderApi, params.toString(), new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                payCallBack.onFailed(ERR_OKHTTP_ERROR, e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && null != response.body()) {
                    try {
                        JSONObject raw = new JSONObject(response.body().string());
                        String retCode = raw.optString("returnCode");
                        String errMst = raw.optString("returnMessage");
                        if (!"0".equals(retCode)) {
                            payCallBack.onFailed(ERR_CHECK_ORDER_FAILED, errMst);
                            return;
                        }
                        JSONObject data = raw.optJSONObject("data");
                        if (null != data) {
                            payCallBack.onSuccess(data);
                        } else {
                            payCallBack.onFailed(ERR_CHECK_ORDER_FAILED, "failed data error");
                        }

                    } catch (JSONException e) {
                        payCallBack.onFailed(ERR_CHECK_ORDER_FAILED, "failed http error");
                    }
                } else {
                    payCallBack.onFailed(response.code(), "failed http error");
                }
            }
        });
    }

    public void payOrder(JSONObject params, PayCallBack payCallBack, String payModel, String payModelId) {
        int actualAmount = params.optInt("actualAmount");
        String payId = params.optString("payId");
        JSONObject reqParam = new JSONObject();
        try {
            reqParam.put("payId", payId);
            reqParam.put("payAmount", actualAmount);
            reqParam.put("payModel", payModel);
            reqParam.put("payModelId", payModelId);
            reqParam.put("cardId", "");
        } catch (JSONException ignored) {
        }
        request(PayEnvironment.API_PAY_ORDER, reqParam.toString(), new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                payCallBack.onFailed(ERR_OKHTTP_ERROR, e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful() || null == response.body()) {
                    payCallBack.onFailed(response.code(), "failed http error");
                    return;
                }
                String respBody = response.body().string();
                try {
                    JSONObject ret = new JSONObject(respBody);
                    String retCode = ret.optString("returnCode");
                    String errMst = ret.optString("returnMessage");
                    if (!"0".equals(retCode)) {
                        payCallBack.onFailed(ERR_CHECK_ORDER_FAILED, errMst);
                        return;
                    }
                    ret.put("paymentAmount", actualAmount + "");
                    payCallBack.onSuccess(ret);
                } catch (JSONException e) {
                    payCallBack.onFailed(ERR_CHECK_ORDER_FAILED, e.getMessage());
                }
            }
        });


    }


    private void request(String apiUrl, String xmlData, Callback callback) {
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(xmlData, mediaType);
        getRequestClient().newCall(new Request.Builder()
                .addHeader("Content-Type", "application/json")
                .addHeader("TC-SUPER-APP-VERSION", "2.0")
                .url(apiUrl)
                .post(requestBody)
                .build()).enqueue(callback);
    }


    private OkHttpClient getRequestClient() {
        if (mRequestClient == null) {
            mRequestClient = new OkHttpClient.Builder()
                    .protocols(Arrays.asList(Protocol.HTTP_2, Protocol.HTTP_1_1))
                    .connectTimeout(5000, TimeUnit.MILLISECONDS)
                    .readTimeout(30000, TimeUnit.MILLISECONDS).build();
        }
        return mRequestClient;
    }


    private String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            char randomChar = characters.charAt(index);
            stringBuilder.append(randomChar);
        }
        return stringBuilder.toString();
    }

    public interface PayCallBack {
        void onSuccess(JSONObject result);

        void onFailed(int errCode, String msg);
    }

}
