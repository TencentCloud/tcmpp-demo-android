package com.tencent.tcmpp.demo.ipcplugin

import android.os.Bundle
import com.tencent.tcmpp.demo.TCMPPDemoApplication
import com.tencent.tcmpp.demo.open.login.Login
import com.tencent.tmf.mini.api.bean.IpcRequestEvent
import com.tencent.tmf.mini.api.callback.BaseIpcPlugin
import com.tencent.tmfmini.sdk.annotation.IpcEvent
import com.tencent.tmfmini.sdk.annotation.IpcMainPlugin
import com.tencent.tmfmini.sdk.core.proxy.ProxyManager
import com.tencent.tmfmini.sdk.launcher.core.proxy.MiniAppProxy

@IpcMainPlugin
class OpenDataIPC : BaseIpcPlugin() {

    companion object {
        const val OPEN_DATA_IPC_EVENT_GET_USER_ID = "getUserInfo"
    }

    @IpcEvent(OPEN_DATA_IPC_EVENT_GET_USER_ID)
    override fun invoke(ipcRequestEvent: IpcRequestEvent) {
        val resp = Bundle()
        val userInfo = Login.g(TCMPPDemoApplication.sApp!!).getUserInfo()
        resp.putString("userId", userInfo?.userId)

        val proxy = ProxyManager.get(MiniAppProxy::class.java)
        resp.putString("nickName", proxy.nickName)
        resp.putString("avatarUrl", proxy.avatarUrl)
        ipcRequestEvent.ok(resp)
    }
}