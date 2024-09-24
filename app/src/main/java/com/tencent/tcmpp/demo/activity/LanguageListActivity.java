package com.tencent.tcmpp.demo.activity;

import static com.tencent.tcmpp.demo.activity.MainContentActivity.REQ_CODE_OF_LANGUAGE_LIST;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tencent.tcmpp.demo.R;
import com.tencent.tcmpp.demo.adapter.LanguageAdapter;
import com.tencent.tcmpp.demo.sp.impl.CommonSp;
import com.tencent.tcmpp.demo.utils.DynamicLanguageUtil;
import com.tencent.tcmpp.demo.utils.LocalUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LanguageListActivity extends AppCompatActivity {
    private String mCurrentLocale;
    private String mSelectedLocale;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_list);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        decorView.setSystemUiVisibility(uiOptions);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        DynamicLanguageUtil.setAppLanguage(this, LocalUtil.current().getLanguage());

        mCurrentLocale = LocalUtil.SUPPORTED_LOCALES[CommonSp.getInstance().getMiniLanguage()].getLanguage();
        mSelectedLocale = mCurrentLocale;
        setLanguageList();
        addClickListen();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setLanguageList();
    }

    private void addClickListen() {
        findViewById(R.id.iv_language_back_img).setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.putExtra("isLanguageChange", !mCurrentLocale.equals(mSelectedLocale));
            setResult(REQ_CODE_OF_LANGUAGE_LIST, intent);
            finish();
        });
    }

    private void setLanguageList() {
        RecyclerView recyclerView = findViewById(R.id.rv_language_list);

        LanguageAdapter adapter = new LanguageAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        adapter.setOnLanguageChangeListener((old, newLocale) -> mSelectedLocale = newLocale);
        recyclerView.setAdapter(adapter);
        adapter.update(getLanguageItems());

        TextView title = findViewById(R.id.tv_language_title);
        title.setText(getResources().getText(R.string.applet_main_tool_language));

    }

    private List<LanguageItem> getLanguageItems() {
        int current = CommonSp.getInstance().getMiniLanguage();
        ArrayList<LanguageItem> items = new ArrayList<>();
        for (int i = 0; i < LocalUtil.SUPPORTED_LOCALES.length; i++) {
            Locale locale = LocalUtil.SUPPORTED_LOCALES[i];
            LanguageItem item = new LanguageItem();
            item.locale = locale;
            item.selected = current == i;
            item.name = getResources().getStringArray(R.array.applet_language_name)[i];
            items.add(item);
        }
        return items;
    }

    public static class LanguageItem {
        public String name;
        public boolean selected = false;
        public Locale locale = null;
    }

}
