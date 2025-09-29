package com.tencent.tcmpp.demo.utils

import android.content.Context
import android.util.DisplayMetrics
import android.view.WindowManager

object ScreenUtil {
    fun dp2px(dpValue: Float, context: Context?): Int {
        return (dpValue * (context?.resources?.displayMetrics?.density ?: 1f) + 0.5f).toInt()
    }

    fun getScreenHeight(context: Context): Int {
        val metric = DisplayMetrics()
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        wm.defaultDisplay.getMetrics(metric)
        return metric.heightPixels
    }

    fun getScreenWidth(context: Context): Int {
        val metric = DisplayMetrics()
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        wm.defaultDisplay.getMetrics(metric)
        return metric.widthPixels
    }
}