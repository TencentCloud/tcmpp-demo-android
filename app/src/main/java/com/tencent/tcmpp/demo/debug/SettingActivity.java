package com.tencent.tcmpp.demo.debug;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tencent.tcmpp.demo.Constants;
import com.tencent.tcmpp.demo.R;
import com.tencent.tcmpp.demo.sp.impl.CommonSp;
import com.tencent.tcmpp.demo.ui.SpaceItemDecoration;
import com.tencent.tmf.mini.api.TmfMiniSDK;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;


public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.applet_activity_debug);
        initView();
    }

    private void initView() {
        RecyclerView mRecyclerView = findViewById(R.id.recyclerView1);

        SettingAdapter mAdapter = new SettingAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(0, 0, 0, 1, 1));

        ArrayList<SettingEntity> serverConfigEntities = new ArrayList<>();
        serverConfigEntities.add(
                new SettingEntity("UserName", CommonSp.getInstance().getUserName(this), SettingEntity.TYPE_1));
        JSONObject debugInfo = TmfMiniSDK.getDebugInfo();
        serverConfigEntities
                .add(new SettingEntity("BaseLibVersion", debugInfo.optString("MiniBaseLibVersion"),
                        SettingEntity.TYPE_1));
        serverConfigEntities.add(new SettingEntity("IMEI", Constants.IMEI, SettingEntity.TYPE_1));
        serverConfigEntities.add(new SettingEntity("MODEL", Build.MODEL, SettingEntity.TYPE_1));
        serverConfigEntities.add(new SettingEntity("MANUFACTURER", Build.MANUFACTURER, SettingEntity.TYPE_1));
        serverConfigEntities.add(new SettingEntity("BOARD", Build.BOARD, SettingEntity.TYPE_1));
        serverConfigEntities
                .add(new SettingEntity("TcpHost", debugInfo.optString("TcpHost"), SettingEntity.TYPE_1));
        serverConfigEntities
                .add(new SettingEntity("HttpUrl", debugInfo.optString("HttpUrl"), SettingEntity.TYPE_1));
        serverConfigEntities.add(new SettingEntity("AreaInfo",
                getString(R.string.applet_mini_data_country) + " " + getString(R.string.applet_mini_proxy_province)
                        + " " + getString(R.string.applet_mini_proxy_city),
                SettingEntity.TYPE_1));
        serverConfigEntities.add(
                new SettingEntity("SdkVersion", debugInfo.optString("SdkVersion"), SettingEntity.TYPE_1));
        serverConfigEntities.add(new SettingEntity("GUID", debugInfo.optString("GUID"), SettingEntity.TYPE_1));
        serverConfigEntities
                .add(new SettingEntity("ProductId", debugInfo.optString("ProductId"),
                        SettingEntity.TYPE_1));
        serverConfigEntities.add(new SettingEntity("CheckPermission", "CheckPermission", SettingEntity.TYPE_2));
        mAdapter.setDebugEntityList(serverConfigEntities);

        findViewById(R.id.config_server_text).setOnClickListener(v ->
                startActivity(
                        new Intent(SettingActivity.this, ServerConfigListActivity.class)));
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
                requestPermissions(p, 9527);
            }
        }
    }

    private class SettingAdapter extends RecyclerView.Adapter<SettingAdapter.SettingItemViewHolder> {

        private List<SettingEntity> mSettingEntityList = new ArrayList<>();

        void setDebugEntityList(List<SettingEntity> debugEntities) {
            if (debugEntities != null) {
                mSettingEntityList = debugEntities;
            } else {
                mSettingEntityList.clear();
            }
        }

        @NonNull
        @Override
        public SettingItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.applet_debug_item, parent, false);
            return new SettingItemViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull SettingItemViewHolder holder, int position) {
            SettingEntity data = mSettingEntityList.get(position);
            if (data.type == SettingEntity.TYPE_1) {
                holder.mTitleText.setVisibility(View.VISIBLE);
                holder.mTitleText.setText(data.title);
                holder.mContentText.setText(data.content);
                holder.mContentText.setTextIsSelectable(true);
            } else if (data.type == SettingEntity.TYPE_2) {
                holder.mTitleText.setVisibility(View.GONE);
                holder.mContentText.setText(data.title);
                holder.mContentText.setTextIsSelectable(false);
            }
            holder.itemView.setOnClickListener(v -> {
                if ("CheckPermission".equals(data.title)) {
                    checkPermission();
                }
            });
        }

        @Override
        public int getItemCount() {
            return mSettingEntityList.size();
        }


        public class SettingItemViewHolder extends RecyclerView.ViewHolder {

            public TextView mTitleText;
            public TextView mContentText;

            public SettingItemViewHolder(View itemView) {
                super(itemView);
                mTitleText = itemView.findViewById(R.id.title_text);
                mContentText = itemView.findViewById(R.id.content_text);
            }
        }
    }

    private static class SettingEntity {

        public static final int TYPE_1 = 1;
        public static final int TYPE_2 = 2;
        public String title;
        public String content;
        public int type;

        public SettingEntity(String title, String content, int type) {
            this.title = title;
            this.content = content;
            this.type = type;
        }
    }

}
