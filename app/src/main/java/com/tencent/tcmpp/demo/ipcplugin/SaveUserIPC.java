package com.tencent.tcmpp.demo.ipcplugin;

import android.os.Bundle;


import com.tencent.tcmpp.demo.open.login.Login;
import com.tencent.tcmpp.demo.open.login.LoginApi;
import com.tencent.tmf.mini.api.TmfMiniSDK;
import com.tencent.tmf.mini.api.bean.IpcRequestEvent;
import com.tencent.tmf.mini.api.callback.BaseIpcPlugin;
import com.tencent.tmfmini.sdk.annotation.IpcEvent;
import com.tencent.tmfmini.sdk.annotation.IpcMainPlugin;
import com.tencent.tmfmini.sdk.core.utils.GsonUtils;
import com.tencent.tmfmini.sdk.launcher.log.QMLog;

@IpcMainPlugin
public class SaveUserIPC extends BaseIpcPlugin {
    public static final String OPEN_DATA_IPC_EVENT_SAVE_USER = "saveUser";

    @Override
    @IpcEvent(OPEN_DATA_IPC_EVENT_SAVE_USER)
    public void invoke(IpcRequestEvent ipcRequestEvent) {
        String userInfoStr = ipcRequestEvent.data.getString("userInfo");
        QMLog.d("SaveUserIPC", "userInfo=" + userInfoStr);
        LoginApi.UserInfo userInfo = GsonUtils.fromJson(userInfoStr, LoginApi.UserInfo.class);
        Login.g(ipcRequestEvent.context).saveUserInfo(userInfo);
    }

    public static void saveUserInfo(LoginApi.UserInfo userInfo){
        Bundle data = new Bundle();
        if (userInfo != null) {
            data.putString("userInfo", GsonUtils.toJson(userInfo));
            TmfMiniSDK.callMainProcessPlugin(OPEN_DATA_IPC_EVENT_SAVE_USER, data, (b, bundle) -> QMLog.d("SaveUserIPC", "save ok."));
        }
    }
}
