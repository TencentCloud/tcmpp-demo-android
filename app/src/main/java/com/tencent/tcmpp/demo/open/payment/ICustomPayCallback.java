package com.tencent.tcmpp.demo.open.payment;

public interface ICustomPayCallback {
    void onPayResult(int retCode, String msg);
}
