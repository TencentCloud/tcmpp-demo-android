package com.tencent.tcmpp.demo.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.tencent.tcmpp.demo.Constants;
import com.tencent.tcmpp.demo.bean.GlobalConfigure;
import com.tencent.tcmpp.demo.R;
import com.tencent.tcmpp.demo.open.login.Login;
import com.tencent.tcmpp.demo.open.login.LoginApi;
import com.tencent.tcmpp.demo.sp.impl.CommonSp;
import com.tencent.tcmpp.demo.utils.GlobalConfigureUtil;
import com.tencent.tcmpp.demo.utils.LocalUtil;
import com.tencent.tcmpp.demo.utils.LocaleContextWrapper;
import com.tencent.tmf.mini.api.TmfMiniSDK;
import com.tencent.tmf.mini.api.callback.MiniCallback;


public class WelcomeActivity extends AppCompatActivity {
    private static final String TAG = "LOGIN";

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleContextWrapper.create(newBase, LocalUtil.current()));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        decorView.setSystemUiVisibility(uiOptions);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        setContentView(R.layout.applet_activity_welcome);

        addLoginBtnListener();
        checkPrivacy();
        loadUIByGlobalConfigure();
    }


    private boolean isPrivacyAuth(Context context) {
        return CommonSp.getInstance().isPrivacyAuth(context);
    }

    private boolean isLogin(Context context) {
        return Login.g(context).getUserInfo() != null;
    }

    private void agreePrivacyAuth(Context context) {
        CommonSp.getInstance().putPrivacyAuth(context, true);
        // Note: Calling any TmfMiniSDK external interface will trigger the initialization of the mini program container,
        // so if privacy policy is involved, make sure to call the TmfMiniSDK related interface after agreeing
        TmfMiniSDK.setLocation(Constants.COUNTRY, Constants.PROVINCE, Constants.CITY);
        TmfMiniSDK.preloadMiniApp(context, null);
    }

    private void showPrivateAuth() {
        final AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.applet_main_privacy_auth) // 标题
                .setMessage(R.string.applet_main_privacy_auth_content) // 内容
                .setPositiveButton(R.string.applet_main_act_delete_msg_confirm, (dialogInterface, i) -> {
                    //同意隐私授权
                    agreePrivacyAuth(WelcomeActivity.this);
                    checkLogin();
                })
                .setNegativeButton(R.string.applet_main_act_delete_msg_cancal, (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    android.os.Process.killProcess(android.os.Process.myPid());
                })
                .setCancelable(false)
                .create();
        alertDialog.show();
    }

    private void startMain() {
        startActivity(new Intent(WelcomeActivity.this, MainContentActivity.class));
        finish();
    }

    private void addLoginBtnListener() {
        findViewById(R.id.btn_tcmpp_login_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginWithUserName();
            }
        });
    }

    private void checkLogin() {
        if (isLogin(this)) {
            startMain();
        }
    }

    private void loginWithUserName() {
        if (!isPrivacyAuth(this)) {
            Toast.makeText(this, "agree privacy first", Toast.LENGTH_SHORT).show();
            return;
        }
        //
        String userName = ((EditText) findViewById(R.id.et_tcmpp_login_username)).getText().toString().trim();
        if (TextUtils.isEmpty(userName)) {
            Toast.makeText(this, "user name is empty", Toast.LENGTH_SHORT).show();
            return;
        }
        Login.g(this).login(userName, "123456", new MiniCallback<LoginApi.UserInfo>() {
            @Override
            public void value(int i, String s, LoginApi.UserInfo userInfo) {
                Log.d(TAG, "login return " + i + " s " + s + " userInfo " + userInfo);
                if (i == LoginApi.LOGIN_SUCCESS) {
                    startMain();
                } else {
                    runOnUiThread(() -> Toast.makeText(WelcomeActivity.this, "login failed", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void fillUpUserName() {
        LoginApi.UserInfo userInfo = Login.g(getApplicationContext()).getUserInfo();
        if (null != userInfo) {
            EditText editText = findViewById(R.id.et_tcmpp_login_username);
            editText.setText(userInfo.userName);
        }
    }

    private void checkPrivacy() {
        if (isPrivacyAuth(this)) {
            fillUpUserName();
            if (isLogin(this)) {
                startMain();
            }
        } else {
            new Handler().postDelayed(this::showPrivateAuth, 1000);
        }
    }

    private void loadUIByGlobalConfigure() {
        GlobalConfigure globalConfigure = GlobalConfigureUtil.getGlobalConfig(getApplicationContext());
        if (null != globalConfigure) {
            if (null != globalConfigure.icon) {
                ImageView iconView = findViewById(R.id.iv_tcmpp_login_icon);
                iconView.setImageBitmap(globalConfigure.icon);
            }
            if (!TextUtils.isEmpty(globalConfigure.appName)) {
                TextView appNameTextView = findViewById(R.id.tv_tcmpp_login_title);
                appNameTextView.setText(globalConfigure.appName);
            }
            if (!TextUtils.isEmpty(globalConfigure.description)) {
                TextView appNameTextView = findViewById(R.id.tv_tcmpp_login_title_desc);
                appNameTextView.setText(globalConfigure.description);
            }
        }
    }

}















