package com.tencent.tcmpp.demo;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tencent.tcmpp.demo.utils.SoftKeyboardUtil;
import com.tencent.tmf.mini.api.TmfMiniSDK;
import com.tencent.tmf.mini.api.bean.MiniApp;
import com.tencent.tmf.mini.api.bean.MiniCode;
import com.tencent.tmf.mini.api.bean.MiniScene;
import com.tencent.tmf.mini.api.bean.MiniStartOptions;
import com.tencent.tmf.mini.api.bean.SearchOptions;
import com.tencent.tmf.mini.api.callback.MiniCallback;

import java.util.List;

import qiu.niorgai.StatusBarCompat;
import xiao.framework.adapter.XGCOnRVItemClickListener;
import xiao.framework.adapter.XGCOnRVItemLongClickListener;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener, XGCOnRVItemClickListener,
        XGCOnRVItemLongClickListener, TextView.OnEditorActionListener {

    private static final int ITEMS_PER_ROW = 4;
    private AppAdapter mAppAdapter;
    private Toolbar mToolbar;
    //    private ImageView mBackImg;
    private CardView mCardView;
    private RecyclerView mRecyclerView;
    private TextView mHintText;
    private EditText mSearchEdit;
    private ResultReceiver mResultReceiver = new ResultReceiver(new Handler()) {
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            if (resultCode != MiniCode.CODE_OK) {
                String errMsg = resultData.getString("errMsg");
                Toast.makeText(SearchActivity.this, errMsg + resultCode, Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.applet_activity_search);

        StatusBarCompat.setStatusBarColor(this,
                getResources().getColor(R.color.applet_color_primary_dark));

        initView();
        initRecyclerView();
    }

    private void initView() {
        mToolbar = findViewById(R.id.toolbar);
        mCardView = findViewById(R.id.cardView);
        mRecyclerView = findViewById(R.id.recyclerView1);
        mHintText = findViewById(R.id.hint_text);
        mSearchEdit = findViewById(R.id.search_edit);
        findViewById(R.id.cancel_text).setOnClickListener(this);
        mSearchEdit.setOnEditorActionListener(this);
        mSearchEdit.requestFocus();
        SoftKeyboardUtil.showInputMethod(SearchActivity.this, mSearchEdit);
    }

    private void initRecyclerView() {
        mAppAdapter = new AppAdapter(this);
        mAppAdapter.setOnRVItemClickListener(this);
        mAppAdapter.setOnRVItemLongClickListener(this);
        mRecyclerView.setAdapter(mAppAdapter);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, ITEMS_PER_ROW));
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.cancel_text) {
            finish();
        } else if (id == R.id.back_img) {
        }
    }

    private void search() {
        SearchOptions searchOptions = new SearchOptions(mSearchEdit.getText().toString());
        TmfMiniSDK.searchMiniApp(searchOptions, new MiniCallback<List<MiniApp>>() {
            @Override
            public void value(int code, String msg, List<MiniApp> data) {
                if (code == MiniCode.CODE_OK && data != null) {
                    mHintText.setVisibility(View.GONE);
                    mCardView.setVisibility(View.VISIBLE);
                    mAppAdapter.setDatas(data);
                } else {
                    mHintText.setVisibility(View.VISIBLE);
                    mHintText.setText(R.string.applet_search_no_ava_mini);
                    mCardView.setVisibility(View.GONE);
                    mAppAdapter.clear();
                    Toast.makeText(SearchActivity.this, msg + code, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onRVItemClick(ViewGroup parent, View itemView, int position) {
        MiniApp item = mAppAdapter.getItem(position);
        MiniStartOptions miniStartOptions = new MiniStartOptions();
        miniStartOptions.resultReceiver = mResultReceiver;
        TmfMiniSDK.startMiniApp(this, item.appId, MiniScene.LAUNCH_SCENE_SEARCH, MiniApp.TYPE_ONLINE, miniStartOptions);
    }

    @Override
    public boolean onRVItemLongClick(ViewGroup parent, View itemView, int position) {
        return false;
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//            InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context
//            .INPUT_METHOD_SERVICE);
            search();
            return true;
        }
        return false;
    }
}
