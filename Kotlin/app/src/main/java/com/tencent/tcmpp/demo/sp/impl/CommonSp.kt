package com.tencent.tcmpp.demo.sp.impl

import android.content.Context
import android.text.TextUtils
import com.google.gson.annotations.SerializedName
import com.tencent.tcmpp.demo.TCMPPDemoApplication
import com.tencent.tcmpp.demo.sp.BaseSp
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class CommonSp private constructor(context: Context) : BaseSp() {
    
    companion object {
        const val FILE_NAME = "app_common"
        private const val KEY_CONFIG_FILE_PATH = "config_file_path"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_SKIP_LOGIN = "skip_login"
        private const val KEY_OPERATE_USER = "operate_user"
        private const val KEY_USER = "user"
        private const val KEY_LAST_USER = "last_user"
        private const val KEY_IS_PRIVACY = "is_privacy_auth"
        private const val KEY_MINI_LANGUAGE = "mini_language"

        @Volatile
        private var mInstance: CommonSp? = null

        fun getInstance(): CommonSp {
            return getInstance(TCMPPDemoApplication.sApp!!)
        }

        @Synchronized
        fun getInstance(context: Context): CommonSp {
            if (mInstance == null) {
                synchronized(CommonSp::class.java) {
                    if (mInstance == null) {
                        mInstance = CommonSp(context)
                    }
                }
            }
            return mInstance!!
        }
    }

    init {
        mSharedPreferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        mEditor = mSharedPreferences?.edit()
    }

    fun getConfigFilePath(): String {
        return getString(mSharedPreferences, KEY_CONFIG_FILE_PATH, "")
    }

    fun putConfigFilePath(path: String) {
        putString(mEditor, KEY_CONFIG_FILE_PATH, path)
    }

    fun removeConfigFilePath() {
        remove(mEditor, KEY_CONFIG_FILE_PATH)
    }

    fun getUserName(context: Context): String {
        return getString(
            context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE),
            KEY_USER_NAME,
            ""
        )
    }

    fun putUserName(context: Context, name: String) {
        putString(
            context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE).edit(),
            KEY_USER_NAME,
            name
        )
    }

    fun removeUserName() {
        remove(mEditor, KEY_USER_NAME)
    }

    fun putSkipLogin(isSkipLogin: Boolean) {
        putBoolean(mEditor, KEY_SKIP_LOGIN, isSkipLogin)
    }

    fun isSkipLogin(): Boolean {
        return getBoolean(mSharedPreferences, KEY_SKIP_LOGIN, false)
    }

    fun removeSkipLogin() {
        remove(mEditor, KEY_SKIP_LOGIN)
    }

    @Synchronized
    fun putUser(name: String, pwd: String) {
        val string = getString(mSharedPreferences, KEY_USER, "")
        try {
            var jsonArray = JSONArray()
            if (!TextUtils.isEmpty(string)) {
                jsonArray = JSONArray(string)
            }

            val length = jsonArray.length()
            var isFind = false
            for (i in 0 until length) {
                val jsonObject = jsonArray.optJSONObject(i)
                val name1 = jsonObject.optString("name")
                val pwd1 = jsonObject.optString("pwd")
                if (name1.equals(name, ignoreCase = true) && pwd1.equals(pwd, ignoreCase = true)) {
                    isFind = true
                    break
                }
            }

            if (!isFind) {
                val jsonObject = JSONObject()
                jsonObject.put("name", name.trim())
                jsonObject.put("pwd", pwd.trim())
                jsonArray.put(jsonObject)
            }

            putString(mEditor, KEY_USER, jsonArray.toString())

            val jsonObject = JSONObject()
            jsonObject.put("name", name.trim())
            jsonObject.put("pwd", pwd.trim())
            putString(mEditor, KEY_LAST_USER, jsonObject.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    @Synchronized
    fun getUsers(): List<String> {
        val string = getString(mSharedPreferences, KEY_USER, "")
        val strings = ArrayList<String>()
        try {
            val jsonArray = JSONArray(string)
            val length = jsonArray.length()
            for (i in 0 until length) {
                val jsonObject = jsonArray.optJSONObject(i)
                val name = jsonObject.optString("name")
                strings.add(name)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return strings
    }

    @Synchronized
    fun getPwd(name: String): String {
        val string = getString(mSharedPreferences, KEY_USER, "")
        try {
            val jsonArray = JSONArray(string)
            val length = jsonArray.length()
            for (i in 0 until length) {
                val jsonObject = jsonArray.optJSONObject(i)
                val n = jsonObject.optString("name")
                if (name.equals(n, ignoreCase = true)) {
                    return jsonObject.optString("pwd")
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return ""
    }

    @Synchronized
    fun getLastUser(): JSONObject? {
        val string = getString(mSharedPreferences, KEY_LAST_USER, "")
        if (TextUtils.isEmpty(string)) {
            return null
        }

        return try {
            JSONObject(string)
        } catch (e: JSONException) {
            e.printStackTrace()
            null
        }
    }

    fun clearAll() {
        clear(mEditor)
    }

    fun isPrivacyAuth(context: Context): Boolean {
        return getBoolean(
            context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE),
            KEY_IS_PRIVACY,
            false
        )
    }

    fun putPrivacyAuth(context: Context, value: Boolean) {
        putBoolean(
            context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE).edit(),
            KEY_IS_PRIVACY,
            value
        )
    }

    fun putMiniLanguage(languageIndex: Int) {
        putInt(mEditor, KEY_MINI_LANGUAGE, languageIndex)
    }

    val miniLanguage: Int
        get() = getInt(mSharedPreferences, KEY_MINI_LANGUAGE, 0)

    data class User(
        @SerializedName("name")
        val name: String,
        @SerializedName("pwd")
        val pwd: String
    )
}