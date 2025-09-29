package com.tencent.tcmpp.demo.open.payment;

import android.content.DialogInterface;

public interface ICustomPayCallback {
    void onPayResult(int retCode, String msg, DialogInterface dialogInterface);
}
