package com.tencent.tcmpp.demo.open.payment

import com.tencent.tcmpp.demo.open.login.LoginEnvironment

object PayEnvironment {
    
    const val BASE_URL = LoginEnvironment.BASE_URL
    
    const val API_CHECK_APP_ORDER = "${BASE_URL}checkout-counter/open"
    const val API_CHECK_GAME_ORDER = "${BASE_URL}checkout-counter/game/open"
    
    const val API_PAY_ORDER = "${BASE_URL}checkout-counter/pay/confirm"
}