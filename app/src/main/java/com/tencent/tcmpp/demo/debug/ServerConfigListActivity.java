package com.tencent.tcmpp.demo.debug;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tencent.tcmpp.demo.Constants;
import com.tencent.tcmpp.demo.R;
import xiao.framework.adapter.XGCOnRVItemClickListener;
import xiao.framework.adapter.XGCOnRVItemLongClickListener;
import com.tencent.tcmpp.demo.sp.impl.CommonSp;
import com.tencent.tcmpp.demo.ui.SpaceItemDecoration;
import com.tencent.tmf.base.api.utils.FileUtil;
import com.tencent.tmf.mini.api.TmfMiniSDK;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class ServerConfigListActivity extends AppCompatActivity implements XGCOnRVItemClickListener, OnClickListener,
        XGCOnRVItemLongClickListener {

    private Toolbar mToolbar;
    private ImageView mBackImg;
    private ImageView mAddImg;
    private RecyclerView mRecyclerView;
    private ServerConfigAdapter mAdapter;
    //当前选择的配置文件
    private File mCurrentSelectConfigFile = null;
    private String oldConfigPath = null;

    public void killMyself() {
        //这里可以重启你的应用程序，我的app中有service，所以我只要杀死进程就自动重启了。
        TmfMiniSDK.stopAllMiniApp(this);
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.applet_activity_server_config);
//        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.applet_color_primary_dark));
        initView();
    }

    private void initView() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mBackImg = (ImageView) findViewById(R.id.back_img);
        mAddImg = (ImageView) findViewById(R.id.add_img);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView1);

        mBackImg.setOnClickListener(this);
        mAddImg.setOnClickListener(this);
        findViewById(R.id.save_img).setOnClickListener(this);

        mAdapter = new ServerConfigAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(0, 0, 0, 1, 1));
        mAdapter.setOnRVItemClickListener(this);
        mAdapter.setOnRVItemLongClickListener(this);

        oldConfigPath = CommonSp.getInstance().getConfigFilePath();

        copyAsset();
    }

    private void copyAsset() {
        File configDir = AddServerConfigActivity.getConfigDirPath(this);
        try {
            String[] assets = this.getAssets().list("server");
            if (assets != null) {
                for (String s : assets) {
                    File file = new File(configDir, s);
                    if (s.endsWith(".json") && !file.exists()) {
                        FileUtil.copyFilesFromAssets(this, "server/" + s, file.getAbsolutePath());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    private void refresh() {
        ArrayList<ServerConfigEntity> serverConfigEntities = getConfigList();
        mAdapter.setDatas(serverConfigEntities);
    }

    /**
     * 配置文件列表
     *
     * @return
     */
    private ArrayList<ServerConfigEntity> getConfigList() {
        ArrayList<ServerConfigEntity> serverConfigEntities = new ArrayList<>();
        //如果配置文件已经不存在了则移除配置
        File configFile = new File(CommonSp.getInstance().getConfigFilePath());
        if (!configFile.exists()) {
            CommonSp.getInstance().removeConfigFilePath();
        }
        boolean isFind = false;

        File configDir = AddServerConfigActivity.getConfigDirPath(this);
        if (configDir.exists()) {
            for (File f : configDir.listFiles()) {
                String s = FileUtil.readFileContent(f.getAbsolutePath());
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    String productId = jsonObject.optString("productId");
                    jsonObject = jsonObject.optJSONObject("shark");
                    if(jsonObject != null) {
                        String tcpHost = jsonObject.optString("tcpHost");
                        String httpUrl = jsonObject.optString("httpUrl");
                        if (f.getAbsolutePath().equalsIgnoreCase(configFile.getAbsolutePath())) {
                            isFind = true;
                            mCurrentSelectConfigFile = f;
                            serverConfigEntities.add(new ServerConfigEntity(f.getName(), tcpHost, httpUrl, productId, f, true));
                        } else {
                            serverConfigEntities.add(new ServerConfigEntity(f.getName(), tcpHost, httpUrl, productId, f, false));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        try {
            InputStream in = this.getAssets().open(Constants.TCMPP_CONFIG_FILE);
            String s = FileUtil.readFileContent(in);
            JSONObject jsonObject = new JSONObject(s);
            String productId = jsonObject.optString("productId");
            jsonObject = jsonObject.optJSONObject("shark");
            String tcpHost = jsonObject.optString("tcpHost");
            String httpUrl = jsonObject.optString("httpUrl");
            String text = getString(R.string.applet_inner_app_config);
            serverConfigEntities.add(0, new ServerConfigEntity(text, tcpHost, httpUrl, productId, null, !isFind));
            if (!isFind) {
                mCurrentSelectConfigFile = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return serverConfigEntities;
    }

    @Override
    public void onRVItemClick(ViewGroup parent, View itemView, int position) {
        for (int i = 0; i < mAdapter.getDatas().size(); i++) {
            if (position == i) {
                mAdapter.getItem(i).isSelect = true;
            } else {
                mAdapter.getItem(i).isSelect = false;
            }
        }
        mAdapter.notifyDataSetChanged();
        if (position == 0) {
            mCurrentSelectConfigFile = null;
        } else {
            mCurrentSelectConfigFile = mAdapter.getItem(position).file;
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.add_img) {
            startActivity(new Intent(this, AddServerConfigActivity.class));
        } else if (id == R.id.back_img) {
            finish();
        } else if (id == R.id.save_img) {//保存现在的配置
            if (mCurrentSelectConfigFile == null) {
                CommonSp.getInstance().removeConfigFilePath();
            } else {
                CommonSp.getInstance().putConfigFilePath(mCurrentSelectConfigFile.getAbsolutePath());
            }

            //判断配置是否发生的变化
            boolean isChange = true;
            if (TextUtils.isEmpty(oldConfigPath)) {
                if (mCurrentSelectConfigFile == null) {
                    isChange = false;
                }
            } else {
                if (mCurrentSelectConfigFile != null && oldConfigPath
                        .equals(mCurrentSelectConfigFile.getAbsolutePath())) {
                    isChange = false;
                }
            }

            if (isChange) {
                AlertDialog alertDialog = new AlertDialog.Builder(this)
                        .setTitle(R.string.applet_save_success)//标题
                        .setMessage(R.string.applet_restart_to_take_effective)//内容
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                killMyself();
                            }
                        })
                        .setCancelable(false)
                        .create();
                alertDialog.show();
            } else {
                finish();
            }
        }
    }

    @Override
    public boolean onRVItemLongClick(ViewGroup parent, View itemView, final int position) {
        if (position == 0) {
            return false;
        }

        final AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.applet_restart_to_take_effective_delete)//标题
                .setMessage(R.string.applet_restart_to_take_effective_delete_config)//内容
                .setPositiveButton(R.string.applet_tmf_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FileUtil.deleteFileOrDir(mAdapter.getItem(position).file);
                        if (mAdapter.getItem(position).isSelect) {
                            CommonSp.getInstance().removeConfigFilePath();
                            refresh();
                        } else {
                            mAdapter.removeItem(position);
                        }
                    }
                })
                .setNegativeButton(R.string.applet_tmf_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setCancelable(false)
                .create();
        alertDialog.show();
        return false;
    }

}
