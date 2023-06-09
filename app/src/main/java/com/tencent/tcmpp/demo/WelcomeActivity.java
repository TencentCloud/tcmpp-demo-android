package com.tencent.tcmpp.demo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.tencent.tcmpp.demo.sp.impl.CommonSp;
import com.tencent.tmf.mini.api.TmfMiniSDK;


public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        decorView.setSystemUiVisibility(uiOptions); // 将decorView设置为全屏显示
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        setContentView(R.layout.applet_activity_welcome);

        if (isPrivacyAuth(this)) {
            new Handler().postDelayed(() -> {
                startMain();
                finish();
            }, 1000);
        } else {
            showPrivateAuth();
        }
    }

    public boolean isPrivacyAuth(Context context) {
        return CommonSp.getInstance().isPrivacyAuth(context);
    }

    public void agreePrivacyAuth(Context context) {
        CommonSp.getInstance().putPrivacyAuth(context, true);
        // 同意隐私授权
        TmfMiniSDK.agreePrivacyAuth();
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
                    startMain();
                    finish();
                })
                .setNegativeButton(R.string.applet_main_act_delete_msg_cancal, (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    android.os.Process.killProcess(android.os.Process.myPid());
                })
                .setCancelable(false)
                .create();
        alertDialog.show();
    }

    private void startMain(){
//        TmfMiniSDK.loginTmf("jemmy", "iLoveTMF!", true, new MiniCallback<Void>() {
//            @Override
//            public void value(int i, String s, Void unused) {
//                Log.e("TmfMiniSDK", "login code=" + i + ", msg=" + s);
//                CommonSp.getInstance().putUser("jemmy", "iLoveTMF!");
//            }
//        });
        startActivity(new Intent(this, MainActivity.class));
    }
}















