package com.tencent.tcmpp.demo.open.payment

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class PayApi {
    
    companion object {
        private const val TAG = "PayApi"
        const val PAY_TYPE_APP = 0
        const val PAY_TYPE_GAME = 1
    }
    
    private val ERR_OKHTTP_ERROR = -1
    private val ERR_CHECK_ORDER_FAILED = -10
    private var mRequestClient: OkHttpClient? = null

    fun checkOrder(params: JSONObject, type: Int, payCallBack: PayCallBack) {
        val checkOrderApi = if (type == PAY_TYPE_GAME) {
            PayEnvironment.API_CHECK_GAME_ORDER
        } else {
            PayEnvironment.API_CHECK_APP_ORDER
        }
        
        request(checkOrderApi, params.toString(), object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                payCallBack.onFailed(ERR_OKHTTP_ERROR, e.message ?: "Network error")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful && response.body != null) {
                    try {
                        val raw = JSONObject(response.body!!.string())
                        val retCode = raw.optString("returnCode")
                        val errMsg = raw.optString("returnMessage")
                        
                        if ("0" != retCode) {
                            payCallBack.onFailed(ERR_CHECK_ORDER_FAILED, errMsg)
                            return
                        }
                        
                        val data = raw.optJSONObject("data")
                        if (data != null) {
                            payCallBack.onSuccess(data)
                        } else {
                            payCallBack.onFailed(ERR_CHECK_ORDER_FAILED, "failed data error")
                        }
                    } catch (e: JSONException) {
                        payCallBack.onFailed(ERR_CHECK_ORDER_FAILED, "failed http error")
                    }
                } else {
                    payCallBack.onFailed(response.code, "failed http error")
                }
            }
        })
    }

    fun payOrder(params: JSONObject, payCallBack: PayCallBack, payModel: String, payModelId: String) {
        val actualAmount = params.optInt("actualAmount")
        val payId = params.optString("payId")
        val reqParam = JSONObject().apply {
            try {
                put("payId", payId)
                put("payAmount", actualAmount)
                put("payModel", payModel)
                put("payModelId", payModelId)
                put("cardId", "")
            } catch (ignored: JSONException) {
            }
        }
        
        request(PayEnvironment.API_PAY_ORDER, reqParam.toString(), object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                payCallBack.onFailed(ERR_OKHTTP_ERROR, e.message ?: "Network error")
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful || response.body == null) {
                    payCallBack.onFailed(response.code, "failed http error")
                    return
                }
                
                val respBody = response.body!!.string()
                try {
                    val ret = JSONObject(respBody)
                    val retCode = ret.optString("returnCode")
                    val errMsg = ret.optString("returnMessage")
                    
                    if ("0" != retCode) {
                        payCallBack.onFailed(ERR_CHECK_ORDER_FAILED, errMsg)
                        return
                    }
                    
                    ret.put("paymentAmount", actualAmount.toString())
                    payCallBack.onSuccess(ret)
                } catch (e: JSONException) {
                    payCallBack.onFailed(ERR_CHECK_ORDER_FAILED, e.message ?: "JSON error")
                }
            }
        })
    }

    private fun request(apiUrl: String, xmlData: String, callback: Callback) {
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = xmlData.toRequestBody(mediaType)
        
        getRequestClient().newCall(
            Request.Builder()
                .addHeader("Content-Type", "application/json")
                .addHeader("TC-SUPER-APP-VERSION", "2.0")
                .url(apiUrl)
                .post(requestBody)
                .build()
        ).enqueue(callback)
    }

    private fun getRequestClient(): OkHttpClient {
        if (mRequestClient == null) {
            mRequestClient = OkHttpClient.Builder()
                .protocols(listOf(Protocol.HTTP_2, Protocol.HTTP_1_1))
                .connectTimeout(5000, TimeUnit.MILLISECONDS)
                .readTimeout(30000, TimeUnit.MILLISECONDS)
                .build()
        }
        return mRequestClient!!
    }

    private fun generateRandomString(length: Int): String {
        val characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        return (1..length)
            .map { characters[Random.nextInt(characters.length)] }
            .joinToString("")
    }

    interface PayCallBack {
        fun onSuccess(result: JSONObject)
        fun onFailed(errCode: Int, msg: String)
    }
}