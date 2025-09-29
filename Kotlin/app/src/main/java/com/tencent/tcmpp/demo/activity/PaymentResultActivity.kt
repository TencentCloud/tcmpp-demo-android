package com.tencent.tcmpp.demo.activity

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import com.tencent.tcmpp.demo.R
import com.tencent.tcmpp.demo.open.login.Login

class PaymentResultActivity : BaseActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_result)
    }

    override fun onStart() {
        super.onStart()
        val success = intent.getBooleanExtra("success", true)

        val totalFee = intent.getStringExtra("total")
        if (!TextUtils.isEmpty(totalFee)) {
            val value = totalFee!!.toInt() / 10000.0f
            val textView = findViewById<TextView>(R.id.tv_pay_sum)
            textView.text = value.toString()
        }
        
        val userInfo = Login.g(this).getUserInfo()
        userInfo?.let {
            val textView = findViewById<TextView>(R.id.tv_pay_user_name)
            textView.text = it.userId
        }
        
        findViewById<View>(R.id.btn_pay_finish).setOnClickListener { finish() }
    }
}