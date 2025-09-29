package com.tencent.tcmpp.demo.open.login

import android.text.TextUtils
import com.tencent.tmf.mini.api.callback.MiniCallback
import com.tencent.tmfmini.sdk.annotation.MiniKeep
import com.tencent.tmfmini.sdk.launcher.log.QMLog
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class LoginApi {
    
    companion object {
        val INSTANCE = LoginApi()
        private val TAG = LoginApi::class.java.simpleName
        
        private const val API_LOGIN = LoginEnvironment.API_LOGIN
        private const val API_AUTH_CODE = LoginEnvironment.API_AUTH_CODE
        
        const val ERROR_TOKEN = 100006
        const val LOGIN_SUCCESS = 0
    }
    
    private var mRequestClient: OkHttpClient? = null

    @MiniKeep
    data class UserInfo(
        var userName: String? = null,
        var userId: String? = null,
        var avatarUrl: String? = null,
        var token: String? = null,
        var email: String? = null,
        var phone: String? = null
    ) {
        constructor(name: String?, id: String?, url: String?, token: String?) : this(name, id, url, token, null, null)
    }

    fun login(appId: String, userId: String, passwd: String, callback: MiniCallback<UserInfo>) {
        val body = JSONObject().apply {
            try {
                put("appId", appId)
                put("userAccount", userId)
                put("userPassword", passwd)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        
        request(API_LOGIN, body) { code, message, jsonObject ->
            if (code == LOGIN_SUCCESS) {
                val name = jsonObject?.optString("userName")
                val url = jsonObject?.optString("iconUrl")
                val token = jsonObject?.optString("token")
                val uid = jsonObject?.optString("userId")
                val phone = jsonObject?.optString("phoneNumber")
                val email = jsonObject?.optString("email")
                
                if (!TextUtils.isEmpty(token)) {
                    callback.value(0, "", UserInfo(userId, uid, url, token, email, phone))
                } else {
                    callback.value(100, "empty token", null)
                }
            } else {
                callback.value(code, message, null)
            }
        }
    }

    fun getAuthCode(appId: String, miniAppId: String, token: String?, callback: MiniCallback<String>) {
        val body = JSONObject().apply {
            try {
                put("appId", appId)
                put("miniAppId", miniAppId)
                put("token", token)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        
        request(API_AUTH_CODE, body) { code, message, jsonObject ->
            if (code == 0) {
                val authCode = jsonObject?.optString("code")
                if (!TextUtils.isEmpty(authCode)) {
                    callback.value(0, "", authCode ?: "")
                } else {
                    callback.value(100, "empty auth code", null)
                }
            } else {
                callback.value(code, message, null)
            }
        }
    }

    private fun getRequestClient(): OkHttpClient {
        if (mRequestClient == null) {
            mRequestClient = OkHttpClient.Builder()
                .protocols(listOf(Protocol.HTTP_2, Protocol.HTTP_1_1))
                .connectTimeout(5000, TimeUnit.MILLISECONDS)
                .readTimeout(30000, TimeUnit.MILLISECONDS)
                .build()
        }
        return mRequestClient!!
    }

    private fun request(apiUrl: String, body: JSONObject, callback: MiniCallback<JSONObject>) {
        val request = Request.Builder()
            .addHeader("Content-Type", "application/json")
            .addHeader("TC-SUPER-APP-VERSION", "2.0")
            .url(apiUrl)
            .post(RequestBody.create(null, body.toString().toByteArray()))
            .build()
            
        getRequestClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                QMLog.e(TAG, "onFailure:$e")
                callback.value(-1, "failed:$e", null)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful && response.body != null) {
                    val result = response.body!!.string()
                    QMLog.d(TAG, "onResponse:$result")
                    try {
                        val responseBody = JSONObject(result)
                        val code = responseBody.optString("returnCode")
                        val returnCode = code.toInt()
                        val message = responseBody.optString("returnMessage")
                        
                        if (returnCode == 0) {
                            callback.value(0, message, responseBody.optJSONObject("data"))
                        } else {
                            callback.value(returnCode, "code=$code,msg=$message", responseBody.optJSONObject("data"))
                        }
                    } catch (e: JSONException) {
                        callback.value(-1, "json exception:$e", null)
                    }
                } else {
                    callback.value(-1, "empty body", null)
                }
            }
        })
    }
}