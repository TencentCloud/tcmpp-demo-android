package com.tencent.tcmpp.demo.open.payment

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.tencent.tcmpp.demo.R

object CustomPayDemo {

    const val TAG = "CustomPayDemo"

    fun requestPay(activity: Activity?, money: Double, callback: ICustomPayCallback) {
        if (activity == null) {
            callback.onPayResult(-1, "param activity can not be null", null)
            return
        }
        showPwdDialog(activity, money) { dialog ->
            val pwd = (dialog as CustomPayDialog).inputText
            Log.e(TAG, "onDismiss isComplete=$pwd")
            if (!TextUtils.isEmpty(pwd)) {
                simulatePay(activity, pwd, money, callback)
            } else {
                callback.onPayResult(-2, "canceled by user", null)
            }
        }
    }

    private fun showPwdDialog(activity: Activity, money: Double, onDismissListener: DialogInterface.OnDismissListener) {
        val customPayDialog = CustomPayDialog(activity, money, R.style.MyAlertDialog)
        customPayDialog.show()
        customPayDialog.setOnDismissListener(onDismissListener)
    }

    private fun simulatePay(activity: Activity, pwd: String, money: Double, callback: ICustomPayCallback) {
        val loadingDialog = ProgressDialog.show(activity, "", "Please wait...", true)
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            loadingDialog.dismiss()
            if (TextUtils.equals(pwd, "666666")) {
                callback.onPayResult(0, "ok", null)
            } else {
                callback.onPayResult(-3, "wrong pwd", null)
            }
        }, 1000)
    }

    class CustomPayDialog : AlertDialog, View.OnClickListener {

        private lateinit var mPwdInputView: PwdEditText
        private lateinit var mCloseBtn: ImageView
        private lateinit var mTvForgetPwd: TextView
        private lateinit var mTvCount: TextView
        private lateinit var mPayType: TextView
        private lateinit var mPayTypeIv: ImageView

        private val mCount: Double
        var inputText: String = ""
            private set
        private var payCallback: ICustomPayCallback? = null
        private var payStatus = -1 // -1 Password not entered; 1. Enter password
        private val src: Int
        private val name: String?

        constructor(context: Context, count: Double, themeId: Int) : super(context, themeId) {
            mCount = count
            src = 0
            name = null
            setOnDismissListener { dialog ->
                payCallback?.onPayResult(payStatus, "", dialog)
            }
        }

        constructor(context: Context, count: Double, themeId: Int, src: Int, desc: String) : super(context, themeId) {
            mCount = count
            this.src = src
            this.name = desc
            setOnDismissListener { dialog ->
                payCallback?.onPayResult(payStatus, "", dialog)
            }
        }

        fun addPayResultListen(payCallback: ICustomPayCallback) {
            this.payCallback = payCallback
        }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.applet_dialog_pay)

            mTvCount = findViewById(R.id.tvCount)
            mTvCount.text = String.format("%.2f", mCount)

            mPwdInputView = findViewById(R.id.pwd)
            mPwdInputView.setOnInputFinishListener(object : PwdEditText.OnInputFinishListener {
                override fun onInputFinish(password: String) {
                    inputText = password
                    payStatus = 1
                    dismiss()
                }
            })

            mCloseBtn = findViewById(R.id.close)
            mCloseBtn.setOnClickListener(this)

            mTvForgetPwd = findViewById(R.id.tvForgetPwd)
            mTvForgetPwd.setOnClickListener(this)

            mPayType = findViewById(R.id.pay_dialog_type)
            mPayTypeIv = findViewById(R.id.pay_dialog_icon)

            if (src != 0) {
                mPayTypeIv.setImageResource(src)
            }
            if (!TextUtils.isEmpty(name)) {
                mPayType.text = name
            }

            window?.clearFlags(
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
            )
            window?.setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE or
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN
            )
        }

        override fun onClick(v: View) {
            when (v.id) {
                R.id.close -> cancel()
                R.id.tvForgetPwd -> Toast.makeText(v.context, "Current password:666666", Toast.LENGTH_SHORT).show()
            }
        }
    }
}