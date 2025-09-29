package com.tencent.tcmpp.demo.open.payment

object PayEnvironment {
    
    const val BASE_URL = "https://openapi-sg.tcmpp.com/superappv2/"
    
    const val API_CHECK_APP_ORDER = "${BASE_URL}checkout-counter/open"
    const val API_CHECK_GAME_ORDER = "${BASE_URL}checkout-counter/game/open"
    
    const val API_PAY_ORDER = "${BASE_URL}checkout-counter/pay/confirm"
}