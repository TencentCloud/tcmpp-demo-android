package com.tencent.tcmpp.demo.open.payment

import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.tencent.tcmpp.demo.R
import com.tencent.tcmpp.demo.activity.BaseActivity

class PaymentMethodActivity : BaseActivity() {
    
    private var mPayFragment: PaymentListFragment? = null
    private var mBindCardFragment: BindCardFragment? = null
    private lateinit var titleTextView: TextView
    private var totalFee: Double = 0.0
    private var rawData: String? = null
    private var isShowList = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_method)
        
        totalFee = intent.getDoubleExtra("totalFee", 88.0)
        rawData = intent.getStringExtra("rawData")
        
        initToolbarClick()
        initChildFragment()
    }

    private fun initToolbarClick() {
        titleTextView = findViewById(R.id.tv_pay_title_info)
        titleTextView.setText(R.string.tcmpp_payment_choose)
        
        findViewById<View>(R.id.iv_pay_back).setOnClickListener {
            if (isShowList) {
                PaymentManager.g().notifyPaymentCancel()
                finish()
            } else {
                showPayList()
                isShowList = true
            }
        }
    }

    private fun initChildFragment() {
        mPayFragment = PaymentListFragment().apply {
            arguments = Bundle().apply {
                putDouble("total", totalFee)
                putString("rawData", rawData)
            }
        }
        
        supportFragmentManager.beginTransaction()
            .add(R.id.frag_container_pay_method, mPayFragment!!, "pay_list")
            .commit()
    }

    fun showBindCard() {
        if (mBindCardFragment == null) {
            mBindCardFragment = BindCardFragment()
            supportFragmentManager.beginTransaction()
                .add(R.id.frag_container_pay_method, mBindCardFragment!!, "bind_card")
                .commitAllowingStateLoss()
        }
        
        titleTextView.setText(R.string.tcmpp_payment_add_card)
        supportFragmentManager.beginTransaction()
            .hide(mPayFragment!!)
            .show(mBindCardFragment!!)
            .commitAllowingStateLoss()
        isShowList = false
    }

    fun showPayList() {
        titleTextView.setText(R.string.tcmpp_payment_choose)
        
        supportFragmentManager.beginTransaction()
            .hide(mBindCardFragment!!)
            .show(mPayFragment!!)
            .commitAllowingStateLoss()
        isShowList = true
    }

    override fun onBackPressed() {
        if (isShowList) {
            PaymentManager.g().notifyPaymentCancel()
        }
        super.onBackPressed()
    }
}