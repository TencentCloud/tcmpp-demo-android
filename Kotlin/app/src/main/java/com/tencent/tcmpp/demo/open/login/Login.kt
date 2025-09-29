package com.tencent.tcmpp.demo.open.login

import android.content.Context
import android.text.TextUtils
import com.google.gson.Gson
import com.tencent.tcmpp.demo.TCMPPDemoApplication
import com.tencent.tcmpp.demo.ipcplugin.SaveUserIPC
import com.tencent.tcmpp.demo.sp.BaseSp
import com.tencent.tmf.base.api.config.ITMFConfigManager
import com.tencent.tmf.core.api.TMFServiceManager
import com.tencent.tmf.mini.api.TmfMiniSDK
import com.tencent.tmf.mini.api.callback.MiniCallback
import com.tencent.tmfmini.sdk.core.utils.GsonUtils
import com.tencent.tmfmini.sdk.launcher.AppLoaderFactory

class Login private constructor(context: Context) : BaseSp() {
    
    companion object {
        @Volatile
        private var instance: Login? = null
        
        fun g(context: Context): Login {
            return instance ?: synchronized(this) {
                instance ?: Login(context).also { instance = it }
            }
        }
    }
    
    private val mAppId: String = getEnvAppId()
    private var mUserInfo: LoginApi.UserInfo? = null

    init {
        mSharedPreferences = context.getSharedPreferences("tcmpp_auth_data_$mAppId", Context.MODE_PRIVATE)
        mEditor = mSharedPreferences?.edit()
        getUserInfo()
    }

    fun getUserInfo(): LoginApi.UserInfo? {
        if (mUserInfo == null) {
            val userInfo = getString(mSharedPreferences, "userInfo", "")
            if (!TextUtils.isEmpty(userInfo)) {
                mUserInfo = Gson().fromJson(userInfo, LoginApi.UserInfo::class.java)
            }
        }
        return mUserInfo
    }

    fun saveUserInfo(userInfo: LoginApi.UserInfo) {
        mUserInfo = userInfo
        if (AppLoaderFactory.g().isMainProcess) {
            putString(mEditor, "userInfo", GsonUtils.toJson(userInfo))
        } else {
            SaveUserIPC.saveUserInfo(userInfo)
        }
    }

    fun deleteUserInfo() {
        mUserInfo = null
        remove(mEditor, "userInfo")
    }

    fun login(userId: String, passwd: String, callback: MiniCallback<LoginApi.UserInfo>) {
        LoginApi.INSTANCE.login(mAppId, userId, passwd) { code, message, userInfo ->
            if (code == 0) {
                userInfo?.let { saveUserInfo(it) }
            }
            callback.value(code, message, userInfo)
        }
    }

    fun getAuthCode(miniAppId: String, callback: MiniCallback<String>) {
        mUserInfo?.let { userInfo ->
            LoginApi.INSTANCE.getAuthCode(mAppId, miniAppId, userInfo.token) { code, message, authCode ->
                if (code == LoginApi.ERROR_TOKEN) {
                    deleteUserInfo()
                }
                callback.value(code, message, authCode)
            }
        } ?: callback.value(-2, "login first", "")
    }

    private fun getEnvAppId(): String {
        if (!TmfMiniSDK.isMiniProcess(TCMPPDemoApplication.sApp)) {
            TmfMiniSDK.getDebugInfo()
        }
        val itmfConfigManager = TMFServiceManager.getDefaultServiceManager().getService(ITMFConfigManager::class.java)
        return itmfConfigManager.appKey
    }
}