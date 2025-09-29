package com.tencent.tcmpp.demo.bean

import com.tencent.tmf.mini.api.bean.MiniApp

data class ItemBean(
    val mType: Int,
    val mTitle: String,
    val mAppInfo: MiniApp
) {
    companion object {
        const val ITEM_TYPE_VERT = 0
        const val ITEM_TYPE_HORZ = 1
        const val ITEM_TYPE_GROUP = 2
        const val ITEM_TYPE_EMPTY = 3
        const val ITEM_TYPE_GROUP2 = 4
    }
}