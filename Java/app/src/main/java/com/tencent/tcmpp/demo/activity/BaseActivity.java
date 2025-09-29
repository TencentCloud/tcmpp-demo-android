package com.tencent.tcmpp.demo.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.tencent.tcmpp.demo.utils.LocalUtil;
import com.tencent.tcmpp.demo.utils.LocaleContextWrapper;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleContextWrapper.create(newBase, LocalUtil.current()));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        setupSystemBars();
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        setupSystemBars();
    }

    /**
     * Setup system bars with edge-to-edge display and transparent navigation bar
     */
    private void setupSystemBars() {
        Window window = getWindow();
        
        androidx.core.view.WindowCompat.setDecorFitsSystemWindows(window, false);
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | 
                             WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }
        
        WindowInsetsControllerCompat controllerCompat = ViewCompat.getWindowInsetsController(window.getDecorView());
        if (controllerCompat != null) {
            controllerCompat.show(WindowInsetsCompat.Type.statusBars());
            controllerCompat.show(WindowInsetsCompat.Type.navigationBars());
            controllerCompat.setAppearanceLightNavigationBars(true);
            controllerCompat.setAppearanceLightStatusBars(true);
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.setStatusBarContrastEnforced(false);
            window.setNavigationBarContrastEnforced(false);
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            window.setNavigationBarDividerColor(Color.TRANSPARENT);
        }
        
        setupContentInsets();
    }

    /**
     * Setup content area insets handling
     * No manual padding needed as layout uses fitsSystemWindows="true"
     */
    private void setupContentInsets() {
        // System automatically handles content spacing with system bars
    }
}