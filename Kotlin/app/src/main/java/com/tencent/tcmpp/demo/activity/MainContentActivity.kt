package com.tencent.tcmpp.demo.activity

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.ResultReceiver
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.tencent.tcmpp.demo.BuildConfig
import com.tencent.tcmpp.demo.Constants
import com.tencent.tcmpp.demo.R
import com.tencent.tcmpp.demo.fragment.MiniAppListFragment
import com.tencent.tcmpp.demo.open.login.Login
import com.tencent.tcmpp.demo.utils.DynamicLanguageUtil
import com.tencent.tcmpp.demo.utils.GlobalConfigureUtil
import com.tencent.tcmpp.demo.utils.LocalUtil
import com.tencent.tcmpp.demo.utils.LocaleContextWrapper
import com.tencent.tcmpp.demo.utils.ScreenUtil
import com.tencent.tmf.mini.api.TmfMiniSDK
import com.tencent.tmf.mini.api.bean.MiniCode
import com.tencent.tmf.mini.api.bean.MiniStartLinkOptions
import org.json.JSONObject

class MainContentActivity : BaseActivity() {

    companion object {
        const val REQ_CODE_OF_LANGUAGE_LIST = 10098
    }

    private val fragments = ArrayList<Fragment>()
    private val mResultReceiver = object : ResultReceiver(Handler(Looper.getMainLooper())) {
        override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {
            if (resultCode != MiniCode.CODE_OK) {
                // Mini program startup error
                val errMsg = resultData?.getString(MiniCode.KEY_ERR_MSG)
                Toast.makeText(this@MainContentActivity, "$errMsg$resultCode", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleContextWrapper.create(newBase, LocalUtil.current()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_v2)
        
        initAndroid15Compatibility()


        fragments.add(MiniAppListFragment.newInstance(MiniAppListFragment.TYPE_MY))
        fragments.add(MiniAppListFragment.newInstance(MiniAppListFragment.TYPE_RECENT))
        initViewPager()
        loadUIByGlobalConfigure()
        showToast(getString(R.string.applet_login_success))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == REQ_CODE_OF_LANGUAGE_LIST) {
            val isLanguageChange = data?.getBooleanExtra("isLanguageChange", false) ?: false
            if (isLanguageChange) {
                changeLanguage()
            }
            return
        }

        val scanResult = TmfMiniSDK.getScanResult(requestCode, data)
        scanResult?.let {
            // Obtain the scanning result of the qrcode
            val result = it.optString("result")
            if (!TextUtils.isEmpty(result)) {
                // Process the scan result
                val options = MiniStartLinkOptions().apply {
                    if (GlobalConfigureUtil.getGlobalConfig(applicationContext).mockApi) {
                        params = "noServer=1"
                    }
                    resultReceiver = mResultReceiver
                }
                TmfMiniSDK.startMiniAppByLink(this, result, options)
            }
        }
    }

    private fun changeLanguage() {
        DynamicLanguageUtil.setAppLanguage(this, LocalUtil.current().language)
        TmfMiniSDK.stopAllMiniApp(this)
        Log.e("TAG", "language change is ${LocalUtil.current().language}")
        recreate()
    }

    private fun initViewPager() {
        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)
        val viewPager2 = findViewById<ViewPager>(R.id.vp_main_mini_list)
        viewPager2.adapter = MainContentPageAdapter(supportFragmentManager)
        tabLayout.setupWithViewPager(viewPager2)

        findViewById<View>(R.id.iv_tool_trigger).setOnClickListener {
            val toolPopupDialogFragment = ToolPopupDialogFragment()
            toolPopupDialogFragment.show(supportFragmentManager, "tool")
        }
    }

    fun showToast(msg: String) {
        val textView = findViewById<TextView>(R.id.tv_tcmpp_toast_info)
        textView.text = msg

        findViewById<View>(R.id.cv_toast_of_success).visibility = View.VISIBLE
        Handler(Looper.getMainLooper()).postDelayed({
            findViewById<View>(R.id.cv_toast_of_success).visibility = View.GONE
        }, 2000)
    }

    inner class MainContentPageAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }

        override fun getCount(): Int {
            return fragments.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return when (position) {
                0 -> resources.getString(R.string.applet_main_tab_my)
                1 -> resources.getString(R.string.applet_main_tab_recent)
                else -> super.getPageTitle(position)
            }
        }
    }

    class ToolPopupDialogFragment : DialogFragment() {

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            return inflater.inflate(R.layout.layout_main_tool_dialog, container, false)
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            setUpToolMenuEventHandle(view)
        }

        override fun onStart() {
            super.onStart()
            setUpWindowLocation()
        }

        private fun setUpToolMenuEventHandle(view: View) {
            view.findViewById<View>(R.id.ll_tool_scan).setOnClickListener {
                dismissAllowingStateLoss()
                TmfMiniSDK.scan(activity)
            }

            view.findViewById<View>(R.id.ll_tool_language).setOnClickListener {
                dismissAllowingStateLoss()
                val intent = Intent(context, LanguageListActivity::class.java)
                startActivityForResult(intent, REQ_CODE_OF_LANGUAGE_LIST)
            }

            view.findViewById<View>(R.id.ll_tool_logout).setOnClickListener {
                dismissAllowingStateLoss()
                activity?.let { Login.g(it).deleteUserInfo() }
                activity?.let { startActivity(Intent(it, WelcomeActivity::class.java)) }
                activity?.finish()
                activity?.let { TmfMiniSDK.stopAllMiniApp(it) }
            }
        }

        private fun setUpWindowLocation() {
            val params = dialog?.window?.attributes
            params?.let {
                it.gravity = Gravity.TOP or Gravity.END
                it.dimAmount = 0f
                it.y = ScreenUtil.dp2px(75f, context)
                it.x = ScreenUtil.dp2px(8f, context)
                it.width = ScreenUtil.dp2px(134f, context)
                it.height = ScreenUtil.dp2px(162f, context)
                dialog?.window?.setBackgroundDrawableResource(R.drawable.applet_bg_main_tool_corner)
                dialog?.window?.attributes = it
            }
        }
    }

    private fun loadUIByGlobalConfigure() {
        val globalConfigure = GlobalConfigureUtil.getGlobalConfig(this)

        globalConfigure.icon?.let {
            val iconView = findViewById<ImageView>(R.id.iv_tcmpp_main_icon)
            iconView.setImageBitmap(it)
        }

        if (!TextUtils.isEmpty(globalConfigure.appName)) {
            val appNameTextView = findViewById<TextView>(R.id.tv_tcmpp_main_title)
            appNameTextView.text = globalConfigure.appName
        }

        if (!TextUtils.isEmpty(globalConfigure.description)) {
            val appNameTextView = findViewById<TextView>(R.id.tv_tcmpp_main_title_desc)
            appNameTextView.text = globalConfigure.description
        }

        if (!TextUtils.isEmpty(globalConfigure.mainTitle)) {
            val mainTitleView = findViewById<TextView>(R.id.tv_main_title)
            mainTitleView.text = globalConfigure.mainTitle
        }
    }
    

    
    /**
     * Android 15: 初始化兼容性适配
     */
    private fun initAndroid15Compatibility() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            Log.d(Constants.LOG_TAG, "Initializing Android 15 compatibility...")
            

        }
    }
}