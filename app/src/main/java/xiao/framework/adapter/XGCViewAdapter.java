package xiao.framework.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import xiao.framework.adapter.viewholder.XGCViewHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class XGCViewAdapter<T, H extends XGCViewHolder> extends BaseAdapter {
    protected Context mContext = null;

    protected List<T> mDatas = new ArrayList<T>();

    public XGCViewAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public T getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        H holder;

        int itemViewType = getItemViewType(position);
        if (convertView == null) {
            holder = createViewHolder(itemViewType);
            holder.setPosition(position);
            holder.setViewType(itemViewType);
            convertView = holder.inflate(mContext, parent, false);
        } else {
            holder = (H) convertView.getTag();
            holder.setPosition(position);
            holder.setViewType(itemViewType);
        }

        setItemData(position, holder, getItem(position), itemViewType);

        return convertView;
    }

    protected abstract H createViewHolder(int viewType);

    protected abstract void setItemData(int position, H holder, T model, int viewType);

    public List<T> getDatas() {
        return mDatas;
    }

    public void addNewDatas(List<T> datas) {
        if (datas != null) {
            mDatas.addAll(0, datas);
            notifyDataSetChanged();
        }
    }

    public void addMoreDatas(List<T> datas) {
        if (datas != null) {
            mDatas.addAll(mDatas.size(), datas);
            notifyDataSetChanged();
        }
    }

    public void setDatas(List<T> datas) {
        if (datas != null) {
            mDatas = datas;
        } else {
            mDatas.clear();
        }
        notifyDataSetChanged();
    }

    public void addItem(int position, T model) {
        mDatas.add(position, model);
        notifyDataSetChanged();
    }

    public void addFirstItem(T model) {
        addItem(0, model);
    }

    public void addLastItem(T model) {
        addItem(mDatas.size(), model);
    }

    public void setItem(int location, T newModel) {
        mDatas.set(location, newModel);
        notifyDataSetChanged();
    }

    public void setItem(T oldModel, T newModel) {
        setItem(mDatas.indexOf(oldModel), newModel);
    }

    public void moveItem(int fromPosition, int toPosition) {
        Collections.swap(mDatas, fromPosition, toPosition);
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        mDatas.remove(position);
        notifyDataSetChanged();
    }

    public void removeItem(T model) {
        mDatas.remove(model);
        notifyDataSetChanged();
    }

    public void clear() {
        mDatas.clear();
        notifyDataSetChanged();
    }
}
