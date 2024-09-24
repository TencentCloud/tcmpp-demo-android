package com.tencent.tcmpp.demo.open.payment;

import android.util.Log;

import androidx.annotation.NonNull;

import com.tencent.tcmpp.demo.utils.XmlConverter;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
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


    public void checkOrder(String appId, JSONObject params, PayCallBack payCallBack) {
        String nonce = generateRandomString(32);
        String prepay_ID = params.optString("prepayId");
        Map<String, String> map = new HashMap<>();
        map.put("appid", appId);
        map.put("nonce_str", nonce);
        map.put("prepay_id", prepay_ID);

        String xmlData = XmlConverter.mapToXml(map, "xml");
        String checkOrderApi = PayEnvironment.API_CHECK_ORDER;
        request(checkOrderApi, xmlData, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                payCallBack.onFailed(ERR_OKHTTP_ERROR, e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && null != response.body()) {
                    String respBody = response.body().string();
                    try {
                        Map<String, String> data = XmlConverter.xmlStringToMap(respBody);
                        String return_code = data.get("return_code");
                        if ("SUCCESS".equals(return_code)) {
                            JSONObject jsonObject = new JSONObject();

                            jsonObject.put("prepay_id", data.get("prepay_id"));
                            jsonObject.put("out_trade_no", data.get("out_trade_no"));
                            jsonObject.put("total_fee", data.get("total_fee"));
                            Log.e(TAG, "check order result " + jsonObject);
                            payCallBack.onSuccess(jsonObject);
                        } else {
                            payCallBack.onFailed(ERR_CHECK_ORDER_FAILED, "check order failed");
                        }
                    } catch (Exception e) {
                        payCallBack.onFailed(ERR_CHECK_ORDER_FAILED, "internal failed");
                    }
                } else {
                    payCallBack.onFailed(response.code(), "failed http error");
                }
            }
        });

    }

    public void payOrder(JSONObject params, PayCallBack payCallBack) {
        String prepayId = params.optString("prepay_id");
        String outTradeNo = params.optString("out_trade_no");
        String totalFee = params.optString("total_fee");

        Map<String, String> map = new HashMap<>();
        map.put("prepay_id", prepayId);
        map.put("out_trade_no", outTradeNo);
        map.put("total_fee", totalFee);

        String xmlData = XmlConverter.mapToXml(map, "xml");
        String payApi = PayEnvironment.API_PAY_ORDER;
        request(payApi, xmlData, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                payCallBack.onFailed(ERR_OKHTTP_ERROR, e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && null != response.body()) {
                    String respBody = response.body().string();
                    try {
                        Map<String, String> data = XmlConverter.xmlStringToMap(respBody);
                        String return_code = data.get("return_code");
                        if ("SUCCESS".equals(return_code)) {
                            JSONObject jsonObject = new JSONObject();
                            Log.e(TAG, "pay  order result " + jsonObject);
                            payCallBack.onSuccess(jsonObject);
                        } else {
                            payCallBack.onFailed(ERR_CHECK_ORDER_FAILED, "check order failed");
                        }
                    } catch (Exception e) {
                        payCallBack.onFailed(ERR_CHECK_ORDER_FAILED, "internal failed");
                    }
                } else {
                    payCallBack.onFailed(response.code(), "failed http error");
                }
            }
        });

    }


    private void request(String apiUrl, String xmlData, Callback callback) {
        MediaType mediaType = MediaType.parse("application/xml; charset=utf-8");
        RequestBody requestBody = RequestBody.create(xmlData, mediaType);
        getRequestClient().newCall(new Request.Builder()
                .addHeader("Content-Type", "application/xml")
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
