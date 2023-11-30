package com.tencent.tcmpp.demo.debug;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.tencent.tcmpp.demo.R;
import com.tencent.tcmpp.demo.utils.FileUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class AddServerConfigActivity extends AppCompatActivity implements OnClickListener {

    private EditText mTitleEdit;
    private EditText mContentEdit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.applet_activity_add_server);
        initView();
    }

    private void initView() {
        mTitleEdit = findViewById(R.id.title_edit);
        mContentEdit = findViewById(R.id.content_edit);
        Button mSaveBtn = findViewById(R.id.save_btn);

        findViewById(R.id.back_img).setOnClickListener(this);

        mSaveBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = mTitleEdit.getText().toString();
                String content = mContentEdit.getText().toString();
                if (TextUtils.isEmpty(title) || TextUtils.isEmpty(content)) {
                    Toast.makeText(AddServerConfigActivity.this, R.string.applet_content_can_not_be_empty, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (title.length() < 2 || title.length() > 10) {
                    Toast.makeText(AddServerConfigActivity.this, R.string.applet_server_name_must_be, Toast.LENGTH_SHORT).show();
                    return;
                }

                File[] files = getConfigDirPath(AddServerConfigActivity.this).listFiles();
                if (files != null) {
                    for (File f : files) {
                        if (f.getName().equalsIgnoreCase(title)) {
                            Toast.makeText(AddServerConfigActivity.this, getString(R.string.applet_config_already_exists ,title), Toast.LENGTH_SHORT)
                                    .show();
                            return;
                        }
                    }
                }

                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(content);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(AddServerConfigActivity.this, R.string.applet_config_must_be_json, Toast.LENGTH_SHORT).show();
                    return;
                }

//                String configPackageName = jsonObject.optString("packageName", "");
//                String packageName = getPackageName();
//                if (MiniSDK.getConfig().verifyPkg && !configPackageName.equalsIgnoreCase(packageName)) {
//                    DialogUtils.showDialog(AddServerConfigActivity.this, "bundleId不匹配", "配置文件包名: " + configPackageName + "\n当前应用包名: " + packageName);
//                    return;
//                }

                jsonObject = jsonObject.optJSONObject("shark");
                if (jsonObject == null) {
                    Toast.makeText(AddServerConfigActivity.this, "not found shark ", Toast.LENGTH_SHORT).show();
                    return;
                }
                String httpUrl = jsonObject.optString("httpUrl");
                if (TextUtils.isEmpty(httpUrl)) {
                    Toast.makeText(AddServerConfigActivity.this, "not found httpUrl", Toast.LENGTH_SHORT).show();
                    return;
                }

                File file = new File(getConfigDirPath(AddServerConfigActivity.this), title);
                if (file.exists()) {
                    FileUtil.deleteFileOrDir(file);
                }
                FileUtil.writeText(file.getAbsolutePath(), content, false);
                finish();
            }
        });
    }

    public static File getConfigDirPath(Context context) {
        File file = null;
//        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//            file = new File(Environment.getExternalStorageDirectory(), "TMFAppletConfig");
//        } else {
            file = context.getExternalFilesDir("TMFAppletConfig");
//        }

        if (!file.exists()) {
            file.mkdirs();
        }

        return file;
    }


    @Override
    public void onClick(View v) {
        finish();
    }
}
