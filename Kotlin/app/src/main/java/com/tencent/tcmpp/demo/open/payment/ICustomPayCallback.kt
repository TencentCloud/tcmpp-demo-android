package com.tencent.tcmpp.demo.open.payment

import android.content.DialogInterface

interface ICustomPayCallback {
    fun onPayResult(retCode: Int, msg: String, dialogInterface: DialogInterface?)
}