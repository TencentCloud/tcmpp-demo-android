package com.tencent.tcmpp.demo.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.tencent.tcmpp.demo.Constants
import com.tencent.tcmpp.demo.R
import com.tencent.tcmpp.demo.open.login.Login
import com.tencent.tcmpp.demo.open.login.LoginApi
import com.tencent.tcmpp.demo.sp.impl.CommonSp
import com.tencent.tcmpp.demo.utils.GlobalConfigureUtil
import com.tencent.tcmpp.demo.utils.LocalUtil
import com.tencent.tcmpp.demo.utils.LocaleContextWrapper
import com.tencent.tmf.mini.api.TmfMiniSDK
import com.tencent.tmf.mini.api.callback.MiniCallback

class WelcomeActivity : BaseActivity() {

    companion object {
        private const val TAG = "LOGIN"
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.applet_activity_welcome)



        addLoginBtnListener()
        checkPrivacy()
        loadUIByGlobalConfigure()
    }

    private fun isPrivacyAuth(context: Context): Boolean {
        return CommonSp.getInstance().isPrivacyAuth(context)
    }

    private fun isLogin(context: Context): Boolean {
        return Login.g(context).getUserInfo() != null
    }

    private fun agreePrivacyAuth(context: Context) {
        CommonSp.getInstance().putPrivacyAuth(context, true)
        // Note: Calling any TmfMiniSDK external interface will trigger the initialization of the mini program container,
        // so if privacy policy is involved, make sure to call the TmfMiniSDK related interface after agreeing
        TmfMiniSDK.setLocation(Constants.COUNTRY, Constants.PROVINCE, Constants.CITY)
        TmfMiniSDK.preloadMiniApp(context, null)
    }

    private fun showPrivateAuth() {
        AlertDialog.Builder(this)
            .setTitle(R.string.applet_main_privacy_auth)
            .setMessage(R.string.applet_main_privacy_auth_content)
            .setPositiveButton(R.string.applet_main_act_delete_msg_confirm) { _, _ ->
                agreePrivacyAuth(this@WelcomeActivity)
                checkLogin()
            }
            .setNegativeButton(R.string.applet_main_act_delete_msg_cancal) { dialogInterface, _ ->
                dialogInterface.dismiss()
                android.os.Process.killProcess(android.os.Process.myPid())
            }
            .setCancelable(false)
            .create()
            .show()
    }

    private fun startMain() {
        startActivity(Intent(this@WelcomeActivity, MainContentActivity::class.java))
        finish()
    }

    private fun addLoginBtnListener() {
        findViewById<View>(R.id.btn_tcmpp_login_confirm).setOnClickListener {
            loginWithUserName()
        }
    }

    private fun checkLogin() {
        if (isLogin(this)) {
            startMain()
        }
    }

    private fun loginWithUserName() {
        if (!isPrivacyAuth(this)) {
            Toast.makeText(this, "agree privacy first", Toast.LENGTH_SHORT).show()
            return
        }

        val userName = findViewById<EditText>(R.id.et_tcmpp_login_username).text.toString().trim()
        if (TextUtils.isEmpty(userName)) {
            Toast.makeText(this, "user name is empty", Toast.LENGTH_SHORT).show()
            return
        }

        Login.g(this).login(userName, "123456", object : MiniCallback<LoginApi.UserInfo> {
            override fun value(code: Int, message: String?, loginUserInfo: LoginApi.UserInfo?) {
                Log.d(TAG, "login return $code message $message userInfo $loginUserInfo")
                if (code == LoginApi.LOGIN_SUCCESS) {
                    startMain()
                } else {
                    runOnUiThread {
                        Toast.makeText(this@WelcomeActivity, "login failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private fun fillUpUserName() {
        val userInfo = Login.g(applicationContext).getUserInfo()
        userInfo?.let {
            val editText = findViewById<EditText>(R.id.et_tcmpp_login_username)
            editText.setText(it.userName)
        }
    }

    private fun checkPrivacy() {
        if (isPrivacyAuth(this)) {
            fillUpUserName()
            if (isLogin(this)) {
                startMain()
            }
        } else {
            Handler(Looper.getMainLooper()).postDelayed({
                showPrivateAuth()
            }, 1000)
        }
    }

    private fun loadUIByGlobalConfigure() {
        val globalConfigure = GlobalConfigureUtil.getGlobalConfig(applicationContext)
        globalConfigure.icon?.let {
            val iconView = findViewById<ImageView>(R.id.iv_tcmpp_login_icon)
            iconView.setImageBitmap(it)
        }

        if (!TextUtils.isEmpty(globalConfigure.appName)) {
            val appNameTextView = findViewById<TextView>(R.id.tv_tcmpp_login_title)
            appNameTextView.text = globalConfigure.appName
        }

        if (!TextUtils.isEmpty(globalConfigure.description)) {
            val appNameTextView = findViewById<TextView>(R.id.tv_tcmpp_login_title_desc)
            appNameTextView.text = globalConfigure.description
        }
    }
}