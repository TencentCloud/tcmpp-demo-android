package com.tencent.tcmpp.demo.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tencent.tcmpp.demo.R;
import com.tencent.tcmpp.demo.activity.LanguageListActivity;
import com.tencent.tcmpp.demo.utils.LocalUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LanguageAdapter extends RecyclerView.Adapter<LanguageAdapter.VH> {
    private List<LanguageListActivity.LanguageItem> items = new ArrayList<>();
    private Locale currentLocale = null;
    private OnLanguageChange languageChange;

    public void update(List<LanguageListActivity.LanguageItem> items) {
        this.items = items;

        for (LanguageListActivity.LanguageItem item : items) {
            if (item.selected) {
                currentLocale = item.locale;
            }
        }
    }

    public void setOnLanguageChangeListener(OnLanguageChange onLanguageChange) {
        this.languageChange = onLanguageChange;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mini_language_item, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class VH extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView imageView;

        public VH(@NonNull View itemView) {
            super(itemView);
        }

        public void bind(int pos) {
            LanguageListActivity.LanguageItem item = items.get(pos);
            textView = itemView.findViewById(R.id.iv_language_name);
            imageView = itemView.findViewById(R.id.iv_language_selection);
            textView.setText(item.name);
            if (item.selected) {
                imageView.setVisibility(View.VISIBLE);
            } else {
                imageView.setVisibility(View.INVISIBLE);
            }

            itemView.setOnClickListener(v -> {
                if (!item.selected) {
                    for (LanguageListActivity.LanguageItem item1 : items) {
                        item1.selected = false;
                    }
                    item.selected = true;
                    currentLocale = item.locale;

                    if (null != languageChange) {
                        languageChange.onLanguageChange(currentLocale.getLanguage(), item.locale.getLanguage());
                    }
                    LocalUtil.setCurrentLocale(item.locale.toLanguageTag());
                    notifyDataSetChanged();

                }
            });
        }

    }

    public interface OnLanguageChange {
        void onLanguageChange(String old, String newLocale);
    }
}
