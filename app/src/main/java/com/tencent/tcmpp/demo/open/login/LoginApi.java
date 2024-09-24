package com.tencent.tcmpp.demo.open.login;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.tencent.tmf.mini.api.callback.MiniCallback;
import com.tencent.tmfmini.sdk.annotation.MiniKeep;
import com.tencent.tmfmini.sdk.launcher.log.QMLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginApi {

    public static LoginApi INSTANCE = new LoginApi();
    private static final String TAG = LoginApi.class.getSimpleName();

    private static final String API_LOGIN = LoginEnvironment.API_LOGIN;

    private static final String API_AUTH_CODE = LoginEnvironment.API_AUTH_CODE;

    public static int ERROR_TOKEN = 100006;
    public static int LOGIN_SUCCESS = 0;

    private OkHttpClient mRequestClient;

    @MiniKeep
    public static class UserInfo{
        public String userName;
        public String userId;
        public String avatarUrl;
        public String token;

        public UserInfo(String name, String id, String url, String token) {
            this.userId = id;
            this.userName = name;
            this.avatarUrl = url;
            this.token = token;
        }
    }

    public void login(String appId, String userId, String passwd, MiniCallback<UserInfo> callback) {
        JSONObject body = new JSONObject();
        try {
            body.put("appId", appId);
            body.put("userAccount", userId);
            body.put("userPassword", passwd);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        request(API_LOGIN, body, (i, s, jsonObject) -> {
            if (i == LOGIN_SUCCESS) {
                String name = jsonObject.optString("userName");
                String url = jsonObject.optString("iconUrl");
                String token = jsonObject.optString("token");
                if (!TextUtils.isEmpty(token)){
                    callback.value(0, "", new UserInfo(name, userId, url, token));

                }else{
                    callback.value(100, "empty token", null);
                }
            }else {
                callback.value(i, s, null);
            }
        });
    }

    public void getAuthCode(String appId, String miniAppId, String token, MiniCallback<String> callback) {
        JSONObject body = new JSONObject();
        try {
            body.put("appId", appId);
            body.put("miniAppId", miniAppId);
            body.put("token", token);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        request(API_AUTH_CODE, body, (i, s, jsonObject) -> {
            if (i == 0) {
                String code = jsonObject.optString("code");
                if (!TextUtils.isEmpty(code)){
                    callback.value(0, "", code);

                }else{
                    callback.value(100, "empty auth code", null);
                }
            }else {
                callback.value(i, s, null);
            }
        });
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

    private void request(String apiUrl, JSONObject body, MiniCallback<JSONObject> callback) {
        getRequestClient().newCall(new Request.Builder()
                .addHeader("Content-Type", "application/json")
                .url(apiUrl)
                .post(RequestBody.create(body.toString().getBytes()))
                .build()).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                QMLog.e(TAG, "onFailure:" + e);
                callback.value(-1, "failed:" + e, null);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String result = response.body().string();
                    QMLog.d(TAG, "onResponse:" + result);
                    try {
                        JSONObject body = new JSONObject(result);
                        String code = body.optString("returnCode");
                        int returnCode = Integer.valueOf(code);
                        String message = body.optString("returnMessage");
                        if (returnCode == 0){
                            callback.value(0, message, body.optJSONObject("data"));
                        }else{
                            callback.value(returnCode, "code=" + code + ",msg=" + message, body.optJSONObject("data"));
                        }
                    } catch (JSONException e) {
                        callback.value(-1, "json exception:" + e, null);
                    }
                } else {
                    callback.value(-1, "empty body", null);
                }
            }
        });
    }
}
