package com.tencent.tcmpp.demo.utils

import android.content.Context
import android.content.res.Configuration
import android.util.Log
import com.tencent.tcmpp.demo.R
import java.util.*

object DynamicLanguageUtil {
    fun setAppLanguage(context: Context, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val resources = context.resources
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
        Log.e("TAG", "get test ${resources.getString(R.string.applet_system_language)}")
    }
}