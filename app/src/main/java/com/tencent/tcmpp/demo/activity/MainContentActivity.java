package com.tencent.tcmpp.demo.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.tencent.tcmpp.demo.bean.GlobalConfigure;
import com.tencent.tcmpp.demo.fragment.MiniAppListFragment;
import com.tencent.tcmpp.demo.R;
import com.tencent.tcmpp.demo.open.login.Login;
import com.tencent.tcmpp.demo.utils.DynamicLanguageUtil;
import com.tencent.tcmpp.demo.utils.GlobalConfigureUtil;
import com.tencent.tcmpp.demo.utils.LocalUtil;
import com.tencent.tcmpp.demo.utils.LocaleContextWrapper;
import com.tencent.tcmpp.demo.utils.ScreenUtil;
import com.tencent.tmf.mini.api.TmfMiniSDK;
import com.tencent.tmf.mini.api.bean.MiniCode;
import com.tencent.tmf.mini.api.bean.MiniStartLinkOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainContentActivity extends AppCompatActivity {
    public static final int REQ_CODE_OF_LANGUAGE_LIST = 10098;
    private final List<Fragment> fragments = new ArrayList<>();
    private final ResultReceiver mResultReceiver = new ResultReceiver(new Handler()) {
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            if (resultCode != MiniCode.CODE_OK) {
                //小程序启动错误
                //mini program startup error
                String errMsg = resultData.getString(MiniCode.KEY_ERR_MSG);
                Toast.makeText(MainContentActivity.this, errMsg + resultCode, Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleContextWrapper.create(newBase, LocalUtil.current()));
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_v2);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        decorView.setSystemUiVisibility(uiOptions);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        fragments.add(MiniAppListFragment.newInstance(MiniAppListFragment.TYPE_MY));
        fragments.add(MiniAppListFragment.newInstance(MiniAppListFragment.TYPE_RECENT));
        initViewPager();
        loadUIByGlobalConfigure();
        showToast(getString(R.string.applet_login_success));
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == REQ_CODE_OF_LANGUAGE_LIST) {
            boolean isLanguageChange = data.getBooleanExtra("isLanguageChange", false);
            if (isLanguageChange) {
                changeLanguage();
            }
            return;
        }
        JSONObject scanResult = TmfMiniSDK.getScanResult(requestCode, data);

        if (scanResult != null) {
            // Obtain the scanning result of the qrcode
            String result = scanResult.optString("result");
            if (!TextUtils.isEmpty(result)) {
                // Process the scan result
                MiniStartLinkOptions options = new MiniStartLinkOptions();
                if (GlobalConfigureUtil.getGlobalConfig(getApplicationContext()).mockApi) {
                    options.params = "noServer=1";
                }
                options.resultReceiver = mResultReceiver;
                TmfMiniSDK.startMiniAppByLink(this, result, options);
            }
        }
    }

    private void changeLanguage() {
        DynamicLanguageUtil.setAppLanguage(this, LocalUtil.current().getLanguage());
        TmfMiniSDK.stopAllMiniApp(this);
        Log.e("TAG", "language change is  " + LocalUtil.current().getLanguage());
        recreate();
    }


    private void initViewPager() {

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ViewPager viewPager2 = findViewById(R.id.vp_main_mini_list);
        viewPager2.setAdapter(new MainContentPageAdapter(getSupportFragmentManager()));
        tabLayout.setupWithViewPager(viewPager2);
        findViewById(R.id.iv_tool_trigger).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToolPopupDialogFragment toolPopupDialogFragment = new ToolPopupDialogFragment();
                toolPopupDialogFragment.show(getSupportFragmentManager(), "tool");
            }
        });
    }

    public void showToast(String msg) {
        TextView textView = findViewById(R.id.tv_tcmpp_toast_info);
        textView.setText(msg);

        findViewById(R.id.cv_toast_of_success).setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.cv_toast_of_success).setVisibility(View.GONE);
            }
        }, 2000);
    }


    class MainContentPageAdapter extends FragmentPagerAdapter {

        public MainContentPageAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) {
                return getResources().getString(R.string.applet_main_tab_my);
            } else if (position == 1) {
                return getResources().getString(R.string.applet_main_tab_recent);
            }
            return super.getPageTitle(position);
        }
    }

    public static class ToolPopupDialogFragment extends DialogFragment {

        public ToolPopupDialogFragment() {

        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.layout_main_tool_dialog, container, false);
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            setUpToolMenuEventHandle(view);
        }

        @Override
        public void onStart() {
            super.onStart();
            setUpWindowLocation();
        }

        private void setUpToolMenuEventHandle(View view) {
            view.findViewById(R.id.ll_tool_scan).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismissAllowingStateLoss();
                    TmfMiniSDK.scan(getActivity());
                }
            });
            view.findViewById(R.id.ll_tool_language).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismissAllowingStateLoss();
                    Intent intent = new Intent(getContext(), LanguageListActivity.class);
                    startActivityForResult(intent, REQ_CODE_OF_LANGUAGE_LIST);
                }
            });
            view.findViewById(R.id.ll_tool_logout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismissAllowingStateLoss();
                    Login.g(getActivity()).deleteUserInfo();
                    startActivity(new Intent(getActivity(), WelcomeActivity.class));
                    getActivity().finish();
                    TmfMiniSDK.stopAllMiniApp(getActivity());
                }
            });
        }

        private void setUpWindowLocation() {
            WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();

            params.gravity = Gravity.TOP | Gravity.END;
            params.dimAmount = 0f;
            params.y = ScreenUtil.dp2px(75, getContext());
            params.x = ScreenUtil.dp2px(8, getContext());
            params.width = ScreenUtil.dp2px(134, getActivity());
            params.height = ScreenUtil.dp2px(162, getContext());
            getDialog().getWindow().getDecorView().setBackgroundResource(R.drawable.applet_bg_main_tool_corner);
            getDialog().getWindow().setAttributes(params);
        }


    }

    private void loadUIByGlobalConfigure() {
        GlobalConfigure globalConfigure = GlobalConfigureUtil.getGlobalConfig(this);
        if (null != globalConfigure) {
            if (null != globalConfigure.icon) {
                ImageView iconView = findViewById(R.id.iv_tcmpp_main_icon);
                iconView.setImageBitmap(globalConfigure.icon);
            }
            if (!TextUtils.isEmpty(globalConfigure.appName)) {
                TextView appNameTextView = findViewById(R.id.tv_tcmpp_main_title);
                appNameTextView.setText(globalConfigure.appName);
            }
            if (!TextUtils.isEmpty(globalConfigure.description)) {
                TextView appNameTextView = findViewById(R.id.tv_tcmpp_main_title_desc);
                appNameTextView.setText(globalConfigure.description);
            }

            if (!TextUtils.isEmpty(globalConfigure.mainTitle)) {
                TextView mainTitleView = findViewById(R.id.tv_main_title);
                mainTitleView.setText(globalConfigure.mainTitle);
            }
        }
    }

}
