package com.tencent.tcmpp.demo.open.payment

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.tencent.tcmpp.demo.R
import com.tencent.tcmpp.demo.activity.PaymentResultActivity
import com.tencent.tcmpp.demo.open.login.Login
import com.tencent.tcmpp.demo.utils.GlobalConfigureUtil
import com.tencent.tmf.base.api.config.ITMFConfigManager
import com.tencent.tmf.core.api.TMFServiceManager
import com.tencent.tmfmini.sdk.launcher.core.IMiniAppContext
import com.tencent.tmfmini.sdk.launcher.core.proxy.AsyncResult
import org.json.JSONException
import org.json.JSONObject

class PaymentManager private constructor() {

    companion object {
        private const val TAG = "PaymentManager"

        @Volatile
        private var instance: PaymentManager? = null

        fun g(): PaymentManager {
            return instance ?: synchronized(this) {
                instance ?: PaymentManager().also { instance = it }
            }
        }
    }

    private val payApi = PayApi()
    private var paymentRequest: PaymentRequest? = null

    fun startPayment(miniAppContext: IMiniAppContext, params: JSONObject, result: AsyncResult) {
        // do params check
        if (!params.has("prepayId")) {
            // ill params
            val failRet = JSONObject().apply {
                try {
                    put("success", false)
                } catch (ignored: JSONException) {
                }
            }
            result.onReceiveResult(false, failRet)
            return
        }

        // mock check order
        if (isMock(miniAppContext.context)) {
            val mockData = JSONObject().apply {
                try {
                    put("total_fee", 9999)
                } catch (ignored: JSONException) {
                }
            }
            checkOrderMock(miniAppContext, mockData, result)
            return
        }

        // real check order
        checkOrder(miniAppContext, params, result, PayApi.PAY_TYPE_APP)
    }

    fun notifyPaymentCancel() {
        paymentRequest?.let { request ->
            val ret = JSONObject().apply {
                try {
                    put("errMsg", "cancel")
                } catch (ignored: JSONException) {
                }
            }
            request.result.onReceiveResult(false, ret)
            paymentRequest = null
        }
    }

    fun showPwdConfirm(activity: Activity, iconSrc: Int, desc: String, model: String, modelId: String) {
        paymentRequest?.let { request ->
            val result = request.result
            val checkRet = request.params
            try {
                checkRet.put("appId", request.miniAppContext.miniAppInfo.appId)
            } catch (ignored: JSONException) {
            } catch (ignored: NullPointerException) {
            }

            showPwdDialog(activity, request.payValue, object : ICustomPayCallback {
                override fun onPayResult(retCode: Int, msg: String, dialogInterface: DialogInterface?) {
                    if (retCode == 1) {
                        val pwd = (dialogInterface as CustomPayDemo.CustomPayDialog).inputText
                        Log.e(TAG, "onDismiss isComplete=$pwd")
                        if (!TextUtils.isEmpty(pwd)) {
                            checkPwdAndPay(activity, pwd, checkRet, result, model, modelId)
                        } else {
                            Log.e(TAG, "empty pwd ~")
                            val ret = JSONObject().apply {
                                try {
                                    put("errMsg", "empty pws")
                                } catch (ignored: JSONException) {
                                }
                            }
                            result.onReceiveResult(false, ret)
                            activity.finish()
                        }
                        paymentRequest = null
                    }
                }
            }, iconSrc, desc)
        }
    }

    /**
     * STEP 1: check order status
     */
    private fun checkOrder(miniAppContext: IMiniAppContext, params: JSONObject, result: AsyncResult, type: Int) {
        val activity = miniAppContext.attachedActivity
        if (type == PayApi.PAY_TYPE_GAME) {
            dealPayTypeGame(miniAppContext, params)
        }

        payApi.checkOrder(params, type, object : PayApi.PayCallBack {
            override fun onSuccess(checkRet: JSONObject) {
                gotoPayPage(activity, miniAppContext, checkRet, result)
            }

            override fun onFailed(errCode: Int, msg: String) {
                Log.e(TAG, "pay failed $msg")
                activity.runOnUiThread {
                    Toast.makeText(miniAppContext.context, msg, Toast.LENGTH_SHORT).show()
                }
                result.onReceiveResult(false, JSONObject())
            }
        })
    }

    private fun gotoPayPage(activity: Activity, miniAppContext: IMiniAppContext, checkRet: JSONObject, result: AsyncResult) {
        activity.runOnUiThread {
            val fee = checkRet.optString("actualAmount")
            val paymentValue = fee.toDouble() / 10000
            val intent = Intent(activity, PaymentMethodActivity::class.java).apply {
                putExtra("totalFee", paymentValue)
                putExtra("rawData", checkRet.toString())
            }
            activity.startActivity(intent)
            paymentRequest = PaymentRequest(miniAppContext, checkRet, result, paymentValue)
            Log.e("TAG", "payment request ${this.hashCode()}")
        }
    }

    private fun dealPayTypeGame(miniAppContext: IMiniAppContext, params: JSONObject?) {
        if (params == null) {
            Log.e(TAG, "dealPayTypeGame:ill params")
            return
        }

        val userInfo = Login.g(miniAppContext.context).getUserInfo()
        if (userInfo == null) {
            Log.e(TAG, "dealPayTypeGame:get user token failed")
            return
        }

        try {
            params.put("token", userInfo.token)
            val itmfConfigManager = TMFServiceManager.getDefaultServiceManager().getService(ITMFConfigManager::class.java)
            val appKey = itmfConfigManager.appKey
            params.put("appId", appKey)
            Log.e("TAG", "put token for game ${userInfo.token}")
        } catch (ignored: JSONException) {
        }
    }

    /**
     * STEP 2: show pwd dialog
     */
    private fun showPwdDialog(activity: Activity, money: Double, payCallback: ICustomPayCallback, iconSrc: Int, desc: String) {
        val customPayDialog = CustomPayDemo.CustomPayDialog(activity, money, R.style.MyAlertDialog, iconSrc, desc)
        customPayDialog.addPayResultListen(payCallback)
        customPayDialog.show()
    }

    /**
     * STEP 3: confirm pwd input and check pwd
     */
    private fun checkPwdAndPay(activity: Activity, pwd: String, data: JSONObject, result: AsyncResult, payModel: String, payModelId: String) {
        if (checkPassWord(pwd)) {
            if (isMock(activity)) { // mock payment
                requestPaymentByMock(activity, data, result)
            } else { // real payment
                requestPayment(activity, data, result, payModel, payModelId)
            }
        } else {
            val ret = JSONObject().apply {
                try {
                    put("errMsg", "bad pwd ")
                } catch (ignored: JSONException) {
                }
            }
            result.onReceiveResult(false, ret)
            activity.finish()
        }
    }

    private fun checkPassWord(pwd: String): Boolean {
        return "666666" == pwd
    }

    /**
     * STEP 4: request payment
     */
    private fun requestPayment(activity: Activity, data: JSONObject, asyncResult: AsyncResult, payModel: String, payModelId: String) {
        // real payment
        payApi.payOrder(data, object : PayApi.PayCallBack {
            override fun onSuccess(result: JSONObject) {
                val totalFee = result.optString("paymentAmount")
                showPayResult(true, activity, totalFee)
                val resultToJs = result.optJSONObject("data")
                try {
                    resultToJs?.put("errCode", "0")
                } catch (ignored: JSONException) {
                }
                asyncResult.onReceiveResult(true, resultToJs ?: JSONObject())
            }

            override fun onFailed(errCode: Int, msg: String) {
                val ret = JSONObject().apply {
                    try {
                        put("errMsg", msg)
                        put("errCode", errCode)
                    } catch (ignored: JSONException) {
                    }
                }
                asyncResult.onReceiveResult(false, ret)
                activity.finish()
            }
        }, payModel, payModelId)
    }

    private fun checkOrderMock(miniAppContext: IMiniAppContext, params: JSONObject, result: AsyncResult) {
        val activity = miniAppContext.attachedActivity
        val fee = params.optString("total_fee")
        val paymentValue = fee.toDouble() / 10000

        showPwdDialog(activity, paymentValue, object : ICustomPayCallback {
            override fun onPayResult(retCode: Int, msg: String, dialogInterface: DialogInterface?) {
                val pwd = (dialogInterface as CustomPayDemo.CustomPayDialog).inputText
                Log.e(TAG, "onDismiss isComplete=$pwd")
                if (!TextUtils.isEmpty(pwd)) {
                    checkPwdAndPay(activity, pwd, params, result, "wechat", "wechat pay")
                } else {
                    Log.e(TAG, "empty pwd ~")
                    result.onReceiveResult(false, JSONObject())
                }
            }
        }, R.drawable.pay_method_wechat, miniAppContext.context.getString(R.string.tcmpp_payment_we_chat_pay))
    }

    private fun requestPaymentByMock(activity: Activity, data: JSONObject, asyncResult: AsyncResult) {
        val totalFee = data.optString("total_fee")
        showPayResult(true, activity, totalFee)
        asyncResult.onReceiveResult(true, JSONObject())
    }

    private fun isMock(context: Context): Boolean {
        return GlobalConfigureUtil.getGlobalConfig(context).mockApi
    }

    private fun showPayResult(success: Boolean, activity: Activity, total: String) {
        val intent = Intent(activity, PaymentResultActivity::class.java).apply {
            putExtra("success", success)
            putExtra("total", total)
        }
        activity.startActivity(intent)
        activity.finish()
    }

    private data class PaymentRequest(
        val miniAppContext: IMiniAppContext,
        val params: JSONObject,
        val result: AsyncResult,
        val payValue: Double
    )
}