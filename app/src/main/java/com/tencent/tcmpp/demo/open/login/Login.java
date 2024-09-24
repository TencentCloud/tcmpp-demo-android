package com.tencent.tcmpp.demo.open.login;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.tencent.tcmpp.demo.ipcplugin.SaveUserIPC;
import com.tencent.tcmpp.demo.sp.BaseSp;
import com.tencent.tmf.base.api.config.ITMFConfigManager;
import com.tencent.tmf.core.api.TMFServiceManager;
import com.tencent.tmf.mini.api.TmfMiniSDK;
import com.tencent.tmf.mini.api.callback.MiniCallback;
import com.tencent.tmfmini.sdk.core.utils.GsonUtils;
import com.tencent.tmfmini.sdk.launcher.AppLoaderFactory;

public class Login extends BaseSp {

    private static Login instance;

    public static Login g(Context context) {
        if (instance == null) {
            instance = new Login(context);
        }
        return instance;
    }

    private final String mAppId;
    LoginApi.UserInfo mUserInfo;

    public Login(Context context) {
        mAppId = getEnvAppId();
        mSharedPreferences = context.getSharedPreferences("tcmpp_auth_data_" + mAppId, MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        getUserInfo();
    }

    public LoginApi.UserInfo getUserInfo() {
        if (mUserInfo == null) {
            String userInfo = getString(mSharedPreferences, "userInfo", "");
            if (!TextUtils.isEmpty(userInfo)) {
                mUserInfo = new Gson().fromJson(userInfo, LoginApi.UserInfo.class);
            }
        }
        return mUserInfo;
    }

    public void saveUserInfo(LoginApi.UserInfo userInfo) {
        mUserInfo = userInfo;
        if (AppLoaderFactory.g().isMainProcess()) {
            putString(mEditor, "userInfo", GsonUtils.toJson(userInfo));
        }else{
            SaveUserIPC.saveUserInfo(userInfo);
        }
    }

    public void deleteUserInfo() {
        mUserInfo = null;
        remove(mEditor, "userInfo");
    }

    public void login(String userId, String passwd, MiniCallback<LoginApi.UserInfo> callback) {
        LoginApi.INSTANCE.login(mAppId, userId, passwd, (i, s, userInfo) -> {
            if (i == 0) {
                saveUserInfo(userInfo);
            }
            callback.value(i, s, userInfo);
        });
    }

    public void getAuthCode(String miniAppId, MiniCallback<String> callback) {
        if (mUserInfo != null) {
            LoginApi.INSTANCE.getAuthCode(mAppId, miniAppId, mUserInfo.token, (i, s, s2) -> {
                if (i == LoginApi.ERROR_TOKEN) {
                    deleteUserInfo();
                }
                callback.value(i, s, s2);
            });
        } else {
            callback.value(-2, "login first", "");
        }
    }

    private String getEnvAppId(){
        TmfMiniSDK.getDebugInfo();
        ITMFConfigManager itmfConfigManager = TMFServiceManager.getDefaultServiceManager().getService(ITMFConfigManager.class);
        return itmfConfigManager.getAppKey();
    }
}
