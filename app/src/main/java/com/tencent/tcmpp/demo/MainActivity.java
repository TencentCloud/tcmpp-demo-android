package com.tencent.tcmpp.demo;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tencent.tcmpp.demo.debug.SettingActivity;
import com.tencent.tcmpp.demo.utils.FileUtil;
import com.tencent.tmf.mini.api.TmfMiniSDK;
import com.tencent.tmf.mini.api.bean.MiniApp;
import com.tencent.tmf.mini.api.bean.MiniCode;
import com.tencent.tmf.mini.api.bean.MiniScene;
import com.tencent.tmf.mini.api.bean.MiniStartLinkOptions;
import com.tencent.tmf.mini.api.bean.MiniStartOptions;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    RecyclerView mRvMiniAppListView;
    MiniAppRecyclerViewAdapter miniAppRecyclerViewAdapter;

    List<ItemBean> mExampleList;
    List<ItemBean> mItemList = new ArrayList<>();

    private final ResultReceiver mResultReceiver = new ResultReceiver(new Handler()) {
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            if (resultCode != MiniCode.CODE_OK) {
                //小程序启动错误
                //mini program startup error
                String errMsg = resultData.getString(MiniCode.KEY_ERR_MSG);
                Toast.makeText(MainActivity.this, errMsg + resultCode, Toast.LENGTH_SHORT).show();
            } else {
                refreshData();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        decorView.setSystemUiVisibility(uiOptions);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        mRvMiniAppListView = findViewById(R.id.recyclerView);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 4);
        layoutManager.setSpanSizeLookup(new SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                ItemBean bean = mItemList.get(position);
                if (bean.mType == ItemBean.ITEM_TYPE_VERT) {
                    return 1;
                } else {
                    return 4;
                }
            }
        });
        miniAppRecyclerViewAdapter = new MiniAppRecyclerViewAdapter();
        mRvMiniAppListView.setLayoutManager(layoutManager);
        mRvMiniAppListView.setAdapter(miniAppRecyclerViewAdapter);

        findViewById(R.id.btn_scan).setOnClickListener(v -> TmfMiniSDK.scan(MainActivity.this));

        findViewById(R.id.title).setOnClickListener(
                v -> startActivity(new Intent(MainActivity.this, SettingActivity.class)));

        findViewById(R.id.search).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, SearchActivity.class));
        });

        checkPermission();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshData();
    }

    private void refreshData() {
        mItemList.clear();
        mExampleList = loadExampleList();
        if (!mExampleList.isEmpty()) {
            mItemList.add(new ItemBean(ItemBean.ITEM_TYPE_GROUP, getString(R.string.main_title_example_apps), null));
            mItemList.addAll(mExampleList);
        }
        mItemList.add(new ItemBean(ItemBean.ITEM_TYPE_GROUP2, getString(R.string.main_title_recent_apps), null));
        loadRecentList();
    }

    private void loadRecentList() {
        TmfMiniSDK.getRecentList(recentList -> runOnUiThread(() -> {
            if (recentList != null && recentList.size() > 0) {
                for (MiniApp miniApp : recentList) {
                    mItemList.add(new ItemBean(ItemBean.ITEM_TYPE_HORZ, null, miniApp));
                }
            } else {
                mItemList.add(new ItemBean(ItemBean.ITEM_TYPE_EMPTY, null, null));
            }
            miniAppRecyclerViewAdapter.notifyDataSetChanged();
        }));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        JSONObject scanResult = TmfMiniSDK.getScanResult(requestCode, data);

        if (scanResult != null) {
            // 获取扫码结果
            // Obtain the scanning result of the qrcode
            String result = scanResult.optString("result");
            if (!TextUtils.isEmpty(result)) {
                // 处理扫码结果
                // Process the scan result
                MiniStartLinkOptions options = new MiniStartLinkOptions();
                options.resultReceiver = mResultReceiver;
                TmfMiniSDK.startMiniAppByLink(this, result, options);
            }
        }
    }

    private List<ItemBean> loadExampleList() {
        List<ItemBean> result = new ArrayList<>();
        try {
            InputStream in = getAssets().open("default_mini_apps.json");
            String exJson = FileUtil.readFileContent(in);
            List<MiniApp> examples = new Gson().fromJson(exJson, new TypeToken<List<MiniApp>>() {
            }.getType());
            for (MiniApp miniApp : examples) {
                result.add(new ItemBean(ItemBean.ITEM_TYPE_VERT, null, miniApp));
            }
        } catch (IOException ignored) {

        }
        return result;
    }

    private static class ItemBean {

        static final int ITEM_TYPE_VERT = 0;
        static final int ITEM_TYPE_HORZ = 1;
        static final int ITEM_TYPE_GROUP = 2;
        static final int ITEM_TYPE_EMPTY = 3;
        static final int ITEM_TYPE_GROUP2 = 4;

        final int mType;
        final MiniApp mAppInfo;
        final String mTitle;

        ItemBean(int type, String title, MiniApp app) {
            this.mType = type;
            this.mTitle = title;
            this.mAppInfo = app;
        }
    }

    private void startMiniApp(MiniApp app) {
        MiniStartOptions miniStartOptions = new MiniStartOptions();
        miniStartOptions.resultReceiver = mResultReceiver;
        TmfMiniSDK.startMiniApp(this,
                app.appId,
                MiniScene.LAUNCH_SCENE_MAIN_ENTRY,
                app.appVerType,
                miniStartOptions);
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> noPermissionList = new ArrayList<>();
            for (String permission : Constants.perms) {
                if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    noPermissionList.add(permission);
                }
            }
            if (noPermissionList.size() > 0) {
                String[] p = new String[noPermissionList.size()];
                p = noPermissionList.toArray(p);
                requestPermissions(p, 4000);
            }
        }
    }

    private class MiniAppRecyclerViewAdapter extends
            RecyclerView.Adapter<MiniAppRecyclerViewAdapter.MiniAppViewHolder> {

        @Override
        public int getItemViewType(int position) {
            ItemBean bean = mItemList.get(position);
            return bean.mType;
        }

        @NonNull
        @Override
        public MiniAppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            int layoutId = R.layout.mini_app_group_item;

            switch (viewType) {
                case ItemBean.ITEM_TYPE_EMPTY:
                    layoutId = R.layout.mini_app_recent_list_empty;
                    break;
                case ItemBean.ITEM_TYPE_HORZ:
                    layoutId = R.layout.mini_app_item_horz;
                    break;
                case ItemBean.ITEM_TYPE_VERT:
                    layoutId = R.layout.mini_app_item_vert;
                    break;
                case ItemBean.ITEM_TYPE_GROUP2:
                    layoutId = R.layout.mini_app_group_item2;
                    break;
            }

            View itemView = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
            return new MiniAppViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MiniAppViewHolder holder, int position) {
            ItemBean itemBean = mItemList.get(position);
            holder.bindView(itemBean);

        }

        @Override
        public int getItemCount() {
            return mItemList.size();
        }

        class MiniAppViewHolder extends RecyclerView.ViewHolder {

            ImageView img;
            TextView title;
            TextView versionType;

            MiniAppViewHolder(@NonNull View itemView) {
                super(itemView);
                img = itemView.findViewById(R.id.ivLogo);
                title = itemView.findViewById(R.id.title);
                versionType = itemView.findViewById(R.id.tvVerType);
            }

            void bindView(ItemBean bean) {
                if (bean.mType == ItemBean.ITEM_TYPE_GROUP || bean.mType == ItemBean.ITEM_TYPE_GROUP2) {
                    title.setText(bean.mTitle);
                } else if (bean.mType == ItemBean.ITEM_TYPE_VERT || bean.mType == ItemBean.ITEM_TYPE_HORZ) {
                    title.setText(bean.mAppInfo.name);
                    if (versionType != null) {
                        switch (bean.mAppInfo.appVerType) {
                            case MiniApp.TYPE_DEVELOP:
                                versionType.setVisibility(View.VISIBLE);
                                versionType.setText(R.string.version_dev);
                                break;
                            case MiniApp.TYPE_PREVIEW:
                                versionType.setVisibility(View.VISIBLE);
                                versionType.setText(R.string.version_pre);
                                break;
                            case MiniApp.TYPE_EXPERIENCE:
                                versionType.setVisibility(View.VISIBLE);
                                versionType.setText(R.string.version_exp);
                                break;
                            default:
                                versionType.setVisibility(View.GONE);
                                break;
                        }
                    }
                    Glide.with(itemView).load(bean.mAppInfo.iconUrl).placeholder(R.mipmap.ic_launcher).into(img);
                    itemView.setOnClickListener(v -> startMiniApp(bean.mAppInfo));
                }
            }
        }
    }
}