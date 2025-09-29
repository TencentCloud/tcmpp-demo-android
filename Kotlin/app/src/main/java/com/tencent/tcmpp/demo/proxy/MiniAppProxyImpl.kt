package com.tencent.tcmpp.demo.proxy

import android.content.Context
import android.content.DialogInterface
import android.graphics.drawable.Drawable
import android.text.TextUtils
import com.tencent.tcmpp.demo.BuildConfig
import com.tencent.tcmpp.demo.Constants
import com.tencent.tcmpp.demo.TCMPPDemoApplication
import com.tencent.tcmpp.demo.open.login.Login
import com.tencent.tcmpp.demo.utils.LocalUtil
import com.tencent.tcmpp.demo.utils.UniversalDrawable
import com.tencent.tmf.mini.api.bean.MiniConfigData
import com.tencent.tmfmini.sdk.annotation.ProxyService
import com.tencent.tmfmini.sdk.launcher.core.IMiniAppContext
import com.tencent.tmfmini.sdk.launcher.core.proxy.AsyncResult
import com.tencent.tmfmini.sdk.launcher.core.proxy.BaseMiniAppProxyImpl
import com.tencent.tmfmini.sdk.launcher.core.proxy.MiniAppProxy
import com.tencent.tmfmini.sdk.launcher.ui.OnMoreItemSelectedListener
import com.tencent.tmfmini.sdk.ui.DefaultMoreItemSelectedListener
import org.json.JSONException
import org.json.JSONObject
import java.util.*

@ProxyService(proxy = MiniAppProxy::class)
class MiniAppProxyImpl : BaseMiniAppProxyImpl() {

    override fun getUserInfo(s: String, asyncResult: AsyncResult) {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("nickName", Constants.USER_INFO_NAME)
            jsonObject.put("avatarUrl", Constants.USER_INFO_AVATAR_URL)
            asyncResult.onReceiveResult(true, jsonObject)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    override fun getAppVersion(): String {
        return "1.0.0"
    }

    override fun getAppName(): String {
        return "TCMPPDemo"
    }

    override fun isDebugVersion(): Boolean {
        return BuildConfig.DEBUG
    }

    override fun getDrawable(context: Context, source: String, width: Int, height: Int, defaultDrawable: Drawable?): Drawable {
        val drawable = UniversalDrawable()
        if (TextUtils.isEmpty(source)) {
            return drawable
        }
        drawable.loadImage(context, source)
        return drawable
    }

    override fun openChoosePhotoActivity(context: Context, i: Int, iChoosePhotoListner: IChoosePhotoListner): Boolean {
        return false
    }

    override fun openImagePreview(context: Context, i: Int, list: List<String>): Boolean {
        return false
    }

    override fun onCapsuleButtonMoreClick(iMiniAppContext: IMiniAppContext): Boolean {
        return false
    }

    override fun onCapsuleButtonCloseClick(iMiniAppContext: IMiniAppContext, onClickListener: DialogInterface.OnClickListener): Boolean {
        return false
    }

    override fun getCustomShare(): Map<String, Int>? {
        return null
    }

    override fun uploadUserLog(s: String, s1: String): Boolean {
        return false
    }

    override fun configData(context: Context, configType: Int, params: JSONObject): MiniConfigData {
        when (configType) {
            MiniConfigData.TYPE_CUSTOM_JSAPI -> {
                // Custom JsApi configuration
                val customJsApiConfig = MiniConfigData.CustomJsApiConfig().apply {
                    jsApiConfigPath = "tcmpp/custom-config.json"
                }

                return MiniConfigData.Builder()
                    .customJsApiConfig(customJsApiConfig)
                    .build()
            }

            MiniConfigData.TYPE_LIVE -> {
                // Live broadcast configuration
                val liveConfig = MiniConfigData.LiveConfig().apply {
                    // The following key and url can only be used for demo
                    licenseKey = "6ae463dfe484853eef22052ca122623b"
                    licenseUrl = "https://license.vod2.myqcloud.com/license/v2/1314481471_1/v_cube.license"
                }

                return MiniConfigData.Builder()
                    .liveConfig(liveConfig)
                    .build()
            }

            MiniConfigData.TYPE_WEBVIEW -> {
                val ua = params.optString(MiniConfigData.WebViewConfig.WEBVIEW_CONFIG_UA)
                // Set new userAgent
                val webViewConfig = MiniConfigData.WebViewConfig().apply {
                    userAgent = "UATest"
                }

                return MiniConfigData.Builder()
                    .webViewConfig(webViewConfig)
                    .build()
            }

            else -> {
                return MiniConfigData.Builder().build()
            }
        }
    }

    override fun getMoreItemSelectedListener(): OnMoreItemSelectedListener {
        return DefaultMoreItemSelectedListener()
    }

    override fun getLocale(): Locale {
        return LocalUtil.current()
    }

    override fun getAccount(): String {
        val userInfo = TCMPPDemoApplication.sApp?.let { Login.g(it).getUserInfo() }
        return userInfo?.userId ?: "unknown"
    }
}