package com.tencent.tcmpp.demo.ipcplugin;

import android.os.Bundle;

import com.tencent.tcmpp.demo.bean.UserInfo;
import com.tencent.tmf.mini.api.bean.IpcRequestEvent;
import com.tencent.tmf.mini.api.callback.BaseIpcPlugin;
import com.tencent.tmfmini.sdk.annotation.IpcEvent;
import com.tencent.tmfmini.sdk.annotation.IpcMainPlugin;
import com.tencent.tmfmini.sdk.core.proxy.ProxyManager;
import com.tencent.tmfmini.sdk.launcher.core.proxy.MiniAppProxy;

@IpcMainPlugin
public class OpenDataIPC extends BaseIpcPlugin {
    public static final String OPEN_DATA_IPC_EVENT_GET_USER_ID = "getUserInfo";
    @Override
    @IpcEvent(OPEN_DATA_IPC_EVENT_GET_USER_ID)
    public void invoke(IpcRequestEvent ipcRequestEvent) {
        Bundle resp = new Bundle();
        resp.putString("userId", UserInfo.getInstance().getUserId());
        MiniAppProxy proxy = ProxyManager.get(MiniAppProxy.class);
        resp.putString("nickName", proxy.getNickName());
        resp.putString("avatarUrl", proxy.getAvatarUrl());
        ipcRequestEvent.ok(resp);
    }
}
