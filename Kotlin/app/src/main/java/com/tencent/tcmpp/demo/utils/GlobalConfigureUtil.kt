package com.tencent.tcmpp.demo.utils

import android.content.Context
import android.graphics.BitmapFactory
import com.tencent.tcmpp.demo.R
import com.tencent.tcmpp.demo.bean.GlobalConfigure

object GlobalConfigureUtil {
    private var sCustomConfig: GlobalConfigure? = null

    fun setCustomConfig(configure: GlobalConfigure?) {
        sCustomConfig = configure
    }

    fun getGlobalConfig(context: Context): GlobalConfigure {
        return sCustomConfig ?: getDefaultGlobalConfig(context)
    }

    private fun getDefaultGlobalConfig(context: Context): GlobalConfigure {
        val bitmap = BitmapFactory.decodeResource(context.resources, R.mipmap.applet_ic_tcmpp_login)
        return GlobalConfigure.Builder()
            .appName(context.getString(R.string.applet_login_title)) // main page app name
            .icon(bitmap) // bitmap for main page icon
            .description(context.getString(R.string.applet_login_title_desc)) // main page description
            .mainTitle(context.getString(R.string.applet_main_title)) // main page title
            .mockApi(false) // not use mock api by default
            .build()
    }
}