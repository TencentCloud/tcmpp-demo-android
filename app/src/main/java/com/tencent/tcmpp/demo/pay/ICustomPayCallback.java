package com.tencent.tcmpp.demo.pay;

public interface ICustomPayCallback {
    void onPayResult(int retCode, String msg);
}
