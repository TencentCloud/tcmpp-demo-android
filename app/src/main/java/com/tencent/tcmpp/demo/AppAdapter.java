package com.tencent.tcmpp.demo;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tencent.tcmpp.demo.utils.GlideUtil;
import com.tencent.tmf.mini.api.bean.MiniApp;

import xiao.framework.adapter.XGCRecyclerViewAdapter;
import xiao.framework.adapter.viewholder.XGCRecyclerViewHolder;

public class AppAdapter extends XGCRecyclerViewAdapter<MiniApp, AppAdapter.AppHolder> {

    public AppAdapter(Context context) {
        super(context);
    }

    @Override
    protected AppHolder createViewHolder(Context context, ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.applet_item_layout, parent, false);
        return new AppHolder(context, this, parent, itemView, viewType);
    }

    @Override
    protected void setItemData(int position, AppHolder holder, MiniApp model, int viewType) {
        holder.setData(model);
    }

    public void remove(String appId, int appVerType){
        if(getDatas() != null) {
            for (int i = 0; i < getDatas().size(); i++) {
                MiniApp app = getDatas().get(i);
                if(app.appId.equals(appId) && app.appVerType == appVerType) {
                    getDatas().remove(i);
                    break;
                }
            }
        }
    }

    public static class AppHolder extends XGCRecyclerViewHolder<MiniApp, AppAdapter> {

        private ImageView mIconImg;
        private TextView mNameText;
        private TextView mVersionText;
        private TextView mFlagText;

        /**
         * 子类必须要实现
         *
         * @param context
         * @param adapter
         * @param parent
         * @param itemView
         * @param viewType
         */
        public AppHolder(Context context, AppAdapter adapter, ViewGroup parent, View itemView, int viewType) {
            super(context, adapter, parent, itemView, viewType);
        }

        @Override
        protected void initWidgets() {
            mIconImg = (ImageView) itemView.findViewById(R.id.icon_img);
            mNameText = (TextView) itemView.findViewById(R.id.name_text);
            mVersionText = (TextView) itemView.findViewById(R.id.version_text);
            mFlagText = (TextView) itemView.findViewById(R.id.flag_text);
        }

        @Override
        public void setData(MiniApp data) {
            if (TextUtils.isEmpty(data.iconUrl)) {
                GlideUtil.with().displayCircleImage(R.mipmap.applet_test, mIconImg);
            } else {
                GlideUtil.with().displayCircleImage(data.iconUrl, mIconImg);
            }

            mNameText.setText(data.name);
            if(!TextUtils.isEmpty(data.version)) {
                mVersionText.setText("V" + data.version);
            }
            switch (data.appVerType) {
                case MiniApp.TYPE_DEVELOP:
                    mFlagText.setVisibility(View.VISIBLE);
                    mFlagText.setText("DEV");
                    break;
                case MiniApp.TYPE_PREVIEW:
                    mFlagText.setVisibility(View.VISIBLE);
                    mFlagText.setText("PRE");
                    break;
                case MiniApp.TYPE_EXPERIENCE:
                    mFlagText.setVisibility(View.VISIBLE);
                    mFlagText.setText("EXP");
                    break;
                case MiniApp.TYPE_ONLINE:
                    mFlagText.setVisibility(View.GONE);
                    break;
            }
        }
    }
}
