package com.tencent.tcmpp.demo.utils

import android.content.Context
import com.tencent.tcmpp.demo.sp.impl.CommonSp
import java.util.*

object LocalUtil {
    val SUPPORTED_LOCALES = arrayOf(
        Locale.SIMPLIFIED_CHINESE,
        Locale.US,
        Locale.FRANCE,
        Locale("id", "ID")
    )

    fun nextLocale(context: Context): String {
        val nextIndex = (CommonSp.getInstance().miniLanguage + 1) % SUPPORTED_LOCALES.size
        CommonSp.getInstance().putMiniLanguage(nextIndex)
        return SUPPORTED_LOCALES[nextIndex].toString()
    }

    fun current(): Locale {
        val index = CommonSp.getInstance().miniLanguage % SUPPORTED_LOCALES.size
        return (SUPPORTED_LOCALES[index] ?: Locale.getDefault()).clone() as Locale
    }

    fun setCurrentLocale(tag: String) {
        for (i in SUPPORTED_LOCALES.indices) {
            if (SUPPORTED_LOCALES[i].toLanguageTag() == tag) {
                CommonSp.getInstance().putMiniLanguage(i)
                break
            }
        }
    }
}