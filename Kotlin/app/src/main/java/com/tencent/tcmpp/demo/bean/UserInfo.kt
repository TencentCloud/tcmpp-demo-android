package com.tencent.tcmpp.demo.bean

import com.google.gson.Gson
import com.tencent.tcmpp.demo.TCMPPDemoApplication
import java.io.Serializable

class UserInfo private constructor() : Serializable {

    var sdkAppId: Int = 0
    private var zone: String? = null
    private var phone: String? = null
    private var token: String? = null
    private var userId: String? = null
    private var userSig: String? = null
    private var name: String? = null
    private var avatar: String? = null
    private var isAutoLogin: Boolean = false
    private var isDebugLogin: Boolean = false

    // Getter methods for private properties
    fun getZone(): String? = zone
    fun getPhone(): String? = phone
    fun getToken(): String? = token
    fun getUserId(): String? = userId
    fun getUserSig(): String? = userSig
    fun getName(): String? = name
    fun getAvatar(): String? = avatar
    fun isAutoLogin(): Boolean = isAutoLogin
    fun isDebugLogin(): Boolean = isDebugLogin

    companion object {
        private const val PER_USER_MODEL = "per_user_model"
        const val LOGOUT = "logout"
        const val USERINFO = "userInfo"

        @Volatile
        private var sUserInfo: UserInfo? = null

        @Synchronized
        fun getInstance(): UserInfo {
            if (sUserInfo == null) {
                val shareInfo = TCMPPDemoApplication.sApp?.getSharedPreferences(USERINFO, 0)
                val json = shareInfo?.getString(PER_USER_MODEL, "") ?: ""
                sUserInfo = if (json.isNotEmpty()) {
                    Gson().fromJson(json, UserInfo::class.java)
                } else {
                    UserInfo()
                }
                if (sUserInfo == null) {
                    sUserInfo = UserInfo()
                }
            }
            return sUserInfo!!
        }
    }

    private fun setUserInfo(info: UserInfo) {
        val shareInfo = TCMPPDemoApplication.sApp?.getSharedPreferences(USERINFO, 0)
        val editor = shareInfo?.edit()
        editor?.putString(PER_USER_MODEL, Gson().toJson(info))
        editor?.apply()
    }

    fun setUserSig(userSig: String?) {
        this.userSig = userSig
        setUserInfo(this)
    }

    fun setName(name: String?) {
        this.name = name
        setUserInfo(this)
    }

    fun setUserId(userId: String?) {
        this.userId = userId
        setUserInfo(this)
    }

    fun setToken(token: String?) {
        this.token = token
        setUserInfo(this)
    }

    fun setZone(zone: String?) {
        this.zone = zone
        setUserInfo(this)
    }

    fun setPhone(userPhone: String?) {
        this.phone = userPhone
        setUserInfo(this)
    }

    fun setAutoLogin(autoLogin: Boolean) {
        this.isAutoLogin = autoLogin
        setUserInfo(this)
    }

    fun setAvatar(url: String?) {
        this.avatar = url
        setUserInfo(this)
    }

    fun setDebugLogin(debugLogin: Boolean) {
        this.isDebugLogin = debugLogin
        setUserInfo(this)
    }

    fun cleanUserInfo() {
        sdkAppId = 0
        zone = ""
        token = ""
        userId = ""
        userSig = ""
        name = ""
        avatar = ""
        isAutoLogin = false
        setUserInfo(this)
    }
}