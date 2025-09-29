package com.tencent.tcmpp.demo.ipcplugin

import android.os.Bundle
import com.tencent.tcmpp.demo.open.login.Login
import com.tencent.tcmpp.demo.open.login.LoginApi
import com.tencent.tmf.mini.api.TmfMiniSDK
import com.tencent.tmf.mini.api.bean.IpcRequestEvent
import com.tencent.tmf.mini.api.callback.BaseIpcPlugin
import com.tencent.tmfmini.sdk.annotation.IpcEvent
import com.tencent.tmfmini.sdk.annotation.IpcMainPlugin
import com.tencent.tmfmini.sdk.core.utils.GsonUtils
import com.tencent.tmfmini.sdk.launcher.log.QMLog

@IpcMainPlugin
class SaveUserIPC : BaseIpcPlugin() {
    
    companion object {
        const val OPEN_DATA_IPC_EVENT_SAVE_USER = "saveUser"
        
        fun saveUserInfo(userInfo: LoginApi.UserInfo?) {
            val data = Bundle()
            userInfo?.let {
                data.putString("userInfo", GsonUtils.toJson(it))
                TmfMiniSDK.callMainProcessPlugin(OPEN_DATA_IPC_EVENT_SAVE_USER, data) { _, _ ->
                    QMLog.d("SaveUserIPC", "save ok.")
                }
            }
        }
    }

    @IpcEvent(OPEN_DATA_IPC_EVENT_SAVE_USER)
    override fun invoke(ipcRequestEvent: IpcRequestEvent) {
        val userInfoStr = ipcRequestEvent.data.getString("userInfo")
        QMLog.d("SaveUserIPC", "userInfo=$userInfoStr")
        val userInfo = GsonUtils.fromJson(userInfoStr, LoginApi.UserInfo::class.java)
        Login.g(ipcRequestEvent.context).saveUserInfo(userInfo)
    }
}