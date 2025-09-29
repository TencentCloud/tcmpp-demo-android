package com.tencent.tcmpp.demo.sp

import android.content.SharedPreferences

abstract class BaseSp {
    protected var mSharedPreferences: SharedPreferences? = null
    protected var mEditor: SharedPreferences.Editor? = null

    protected fun getString(sp: SharedPreferences?, key: String, defaultValue: String): String {
        return sp?.getString(key, defaultValue) ?: defaultValue
    }

    protected fun putString(editor: SharedPreferences.Editor?, key: String, value: String) {
        editor?.putString(key, value)?.apply()
    }

    protected fun getBoolean(sp: SharedPreferences?, key: String, defaultValue: Boolean): Boolean {
        return sp?.getBoolean(key, defaultValue) ?: defaultValue
    }

    protected fun putBoolean(editor: SharedPreferences.Editor?, key: String, value: Boolean) {
        editor?.putBoolean(key, value)?.apply()
    }

    protected fun getInt(sp: SharedPreferences?, key: String, defaultValue: Int): Int {
        return sp?.getInt(key, defaultValue) ?: defaultValue
    }

    protected fun putInt(editor: SharedPreferences.Editor?, key: String, value: Int) {
        editor?.putInt(key, value)?.apply()
    }

    protected fun remove(editor: SharedPreferences.Editor?, key: String) {
        editor?.remove(key)?.apply()
    }

    protected fun clear(editor: SharedPreferences.Editor?) {
        editor?.clear()?.apply()
    }
}