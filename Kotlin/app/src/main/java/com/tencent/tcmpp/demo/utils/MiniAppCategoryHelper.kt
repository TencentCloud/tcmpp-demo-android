package com.tencent.tcmpp.demo.utils

import android.text.TextUtils

object MiniAppCategoryHelper {

    fun getCategoryFromString(categoryStrings: String?): List<MiniAppCategory> {
        val ret = ArrayList<MiniAppCategory>()
        if (!TextUtils.isEmpty(categoryStrings)) {
            val categories = categoryStrings!!.split(",")
            for (category in categories) {
                val levels = category.split("->")
                if (levels.size == 2) {
                    val firstCategory = levels[0]
                    val second = levels[1]
                    if (second.contains("_")) {
                        val secondCatIdAndName = second.split("_")
                        ret.add(MiniAppCategory(firstCategory, secondCatIdAndName[1], secondCatIdAndName[0].toInt()))
                    } else {
                        ret.add(MiniAppCategory(levels[0], levels[1]))
                    }
                }
            }
        }
        return ret
    }

    data class MiniAppCategory(
        val firstLevelCategory: String,
        val secondLevelCategory: String,
        val cateId: Int = 0
    )
}