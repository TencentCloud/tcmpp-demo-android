package com.tencent.tcmpp.demo.debug;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tencent.tcmpp.demo.R;
import xiao.framework.adapter.viewholder.XGCRecyclerViewHolder;


public class ServerConfigHolder extends XGCRecyclerViewHolder<ServerConfigEntity, ServerConfigAdapter> {

    private LinearLayout mLayout;
    private TextView mTitleText;
    private TextView mTcpHostText;
    private TextView mHttpUrlText;
    private ImageView mSelectImg;


    /**
     * 子类必须要实现
     *
     * @param context
     * @param adapter
     * @param parent
     * @param itemView
     * @param viewType
     */
    public ServerConfigHolder(Context context,
            ServerConfigAdapter adapter, ViewGroup parent, View itemView, int viewType) {
        super(context, adapter, parent, itemView, viewType);
    }

    @Override
    protected void initWidgets() {
        mLayout = (LinearLayout) findViewById(R.id.layout);
        mTitleText = (TextView) findViewById(R.id.title_text);
        mTcpHostText = (TextView) findViewById(R.id.tcp_host_text);
        mHttpUrlText = (TextView) findViewById(R.id.http_url_text);
        mSelectImg = (ImageView) findViewById(R.id.select_img);
    }

    @Override
    public void setData(ServerConfigEntity data) {
        mTitleText.setText(data.title);
        mTcpHostText.setText(data.tcpHost+ " " + data.httpUrl + " " + data.productId);
//        mHttpUrlText.setText(data.httpUrl);
        if (data.isSelect) {
            mSelectImg.setVisibility(View.VISIBLE);
        } else {
            mSelectImg.setVisibility(View.GONE);
        }
    }
}
