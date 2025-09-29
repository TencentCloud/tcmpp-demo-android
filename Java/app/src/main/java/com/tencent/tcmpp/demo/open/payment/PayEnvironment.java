package com.tencent.tcmpp.demo.open.payment;

public class PayEnvironment {

    public static final String BASE_URL = "https://openapi-sg.tcmpp.com/superappv2/";

    public static final String API_CHECK_APP_ORDER = BASE_URL + "checkout-counter/open";
    public static final String API_CHECK_GAME_ORDER = BASE_URL + "checkout-counter/game/open";

    public static final String API_PAY_ORDER = BASE_URL + "checkout-counter/pay/confirm";

}
