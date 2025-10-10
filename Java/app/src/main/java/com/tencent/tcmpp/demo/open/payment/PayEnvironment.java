package com.tencent.tcmpp.demo.open.payment;

import com.tencent.tcmpp.demo.open.login.LoginEnvironment;

public class PayEnvironment {

    public static final String BASE_URL = LoginEnvironment.BASE_URL;

    public static final String API_CHECK_APP_ORDER = BASE_URL + "checkout-counter/open";
    public static final String API_CHECK_GAME_ORDER = BASE_URL + "checkout-counter/game/open";

    public static final String API_PAY_ORDER = BASE_URL + "checkout-counter/pay/confirm";

}
