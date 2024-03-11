package xiao.framework.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import xiao.framework.adapter.viewholder.XGCRecyclerViewHolder;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by xiaoguochang on 2015/12/6.
 */
public abstract class XGCRecyclerViewAdapter<M, VH extends XGCRecyclerViewHolder> extends RecyclerView.Adapter<VH> {
    protected Context mContext = null;

    protected List<M> mDatas = new ArrayList<M>();

    protected XGCOnRVItemClickListener mOnRVItemClickListener;

    protected XGCOnRVItemLongClickListener mOnRVItemLongClickListener;

    public XGCRecyclerViewAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        VH holder = createViewHolder(mContext, parent, viewType);
        holder.setOnRVItemClickListener(mOnRVItemClickListener);
        holder.setOnRVItemLongClickListener(mOnRVItemLongClickListener);

        return holder;
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        setItemData(position, holder, getItem(position), getItemViewType(position));
    }

    protected abstract VH createViewHolder(Context context, ViewGroup parent, int viewType);

    protected abstract void setItemData(int position, VH holder, M model, int viewType);


    protected static View inflate(Context context, int resource, ViewGroup root){
        return LayoutInflater.from(context).inflate(resource, root, false);
    }

    public void setOnRVItemClickListener(XGCOnRVItemClickListener onRVItemClickListener) {
        mOnRVItemClickListener = onRVItemClickListener;
    }

    public void setOnRVItemLongClickListener(XGCOnRVItemLongClickListener onRVItemLongClickListener) {
        mOnRVItemLongClickListener = onRVItemLongClickListener;
    }

    public M getItem(int position) {
        return mDatas.get(position);
    }

    public List<M> getDatas() {
        return mDatas;
    }

    public void addNewDatas(int position, List<M> datas) {
        if (datas != null) {
            mDatas.addAll(position, datas);
            notifyItemRangeInserted(position, datas.size());
        }
    }

    public void addMoreDatas(List<M> datas) {
        if (datas != null) {
            mDatas.addAll(mDatas.size(), datas);
            notifyItemRangeInserted(mDatas.size(), datas.size());
        }
    }

    public void setDatas(List<M> datas) {
        if (datas != null) {
            mDatas.clear();
            mDatas.addAll(datas);
        }
        notifyDataSetChanged();
    }

    public void clear() {
        mDatas.clear();
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        mDatas.remove(position);
        notifyItemRemoved(position);
    }

    public void removeItem(M model) {
        removeItem(mDatas.indexOf(model));
    }

    public void addItem(int position, M model) {
        mDatas.add(position, model);
        notifyItemInserted(position);
    }

    public void addFirstItem(M model) {
        addItem(0, model);
    }

    public void addLastItem(M model) {
        addItem(mDatas.size(), model);
    }

    public void setItem(int location, M newModel) {
        mDatas.set(location, newModel);
        notifyItemChanged(location);
    }

    public void setItem(M oldModel, M newModel) {
        setItem(mDatas.indexOf(oldModel), newModel);
    }

    public void moveItem(int fromPosition, int toPosition) {
        mDatas.add(toPosition, mDatas.remove(fromPosition));
        notifyItemMoved(fromPosition, toPosition);
    }
}
