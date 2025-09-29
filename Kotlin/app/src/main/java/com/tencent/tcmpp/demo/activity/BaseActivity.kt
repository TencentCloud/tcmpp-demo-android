package com.tencent.tcmpp.demo.activity

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.tencent.tcmpp.demo.utils.LocalUtil
import com.tencent.tcmpp.demo.utils.LocaleContextWrapper

abstract class BaseActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleContextWrapper.create(newBase, LocalUtil.current()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        setupSystemBars()
    }

    override fun setContentView(view: View) {
        super.setContentView(view)
        setupSystemBars()
    }

    /**
     * Setup system bars with edge-to-edge display and transparent navigation bar
     */
    private fun setupSystemBars() {
        val window: Window = window
        
        androidx.core.view.WindowCompat.setDecorFitsSystemWindows(window, false)
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or 
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
            )
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.TRANSPARENT
            window.navigationBarColor = Color.TRANSPARENT
        }
        
        val controllerCompat = ViewCompat.getWindowInsetsController(window.decorView)
        controllerCompat?.let {
            it.show(WindowInsetsCompat.Type.statusBars())
            it.show(WindowInsetsCompat.Type.navigationBars())
            it.isAppearanceLightNavigationBars = true
            it.isAppearanceLightStatusBars = true
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isStatusBarContrastEnforced = false
            window.isNavigationBarContrastEnforced = false
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            window.navigationBarDividerColor = Color.TRANSPARENT
        }
        
        setupContentInsets()
    }

    /**
     * Setup content area insets handling
     * No manual padding needed as layout uses fitsSystemWindows="true"
     */
    private fun setupContentInsets() {
        // System automatically handles content spacing with system bars
    }
}