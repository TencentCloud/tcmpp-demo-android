package com.tencent.tcmpp.demo.debug;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tencent.tcmpp.demo.R;
import xiao.framework.adapter.XGCRecyclerViewAdapter;

public class ServerConfigAdapter extends XGCRecyclerViewAdapter<ServerConfigEntity, ServerConfigHolder> {

    public ServerConfigAdapter(Context context) {
        super(context);
    }

    @Override
    protected ServerConfigHolder createViewHolder(Context context, ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.applet_server_info_item, parent, false);
        return new ServerConfigHolder(context, this, parent, itemView, viewType);
    }

    @Override
    protected void setItemData(int position, ServerConfigHolder holder, ServerConfigEntity model, int viewType) {
        holder.setData(model);
    }
}
