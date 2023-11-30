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
    /**
     * 数据
     */
    protected List<M> mDatas = new ArrayList<M>();
    /**
     * 整个item的点击监听
     */
    protected XGCOnRVItemClickListener mOnRVItemClickListener;
    /**
     * 整个item的长按监听
     */
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
        //设置监听回调
        holder.setOnRVItemClickListener(mOnRVItemClickListener);
        holder.setOnRVItemLongClickListener(mOnRVItemLongClickListener);

        return holder;
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        //设置item数据
        setItemData(position, holder, getItem(position), getItemViewType(position));
    }

    /**
     * 创建ViewHolder
     * 当viewItemType为多种时：
     * 方法一：可以将所有item数据都放在一个ViewHolder中，那么需要在ViewHolder中根据type做处理，但这种处理方式结构不够清晰
     * 方法二：为不同的viewItemType创建不同的ViewHolder，每个item数据和逻辑分离，结构更清晰，推荐使用
     *
     * @return Integer:视图的类型；VH：每个视图所对应的ViewHolder
     */
    protected abstract VH createViewHolder(Context context, ViewGroup parent, int viewType);

    /**
     * 设置每项数据到View上
     * 包含不同类型视图时，需要根据viewType，设置不同视图的数据
     *
     * @param position Item索引
     * @param holder   ViewHolder
     * @param model    数据实体
     */
    protected abstract void setItemData(int position, VH holder, M model, int viewType);


    protected static View inflate(Context context, int resource, ViewGroup root){
        return LayoutInflater.from(context).inflate(resource, root, false);
    }


    /**
     * 设置item的点击事件监听器
     *
     * @param onRVItemClickListener
     */
    public void setOnRVItemClickListener(XGCOnRVItemClickListener onRVItemClickListener) {
        mOnRVItemClickListener = onRVItemClickListener;
    }

    /**
     * 设置item的长按事件监听器
     *
     * @param onRVItemLongClickListener
     */
    public void setOnRVItemLongClickListener(XGCOnRVItemLongClickListener onRVItemLongClickListener) {
        mOnRVItemLongClickListener = onRVItemLongClickListener;
    }

    public M getItem(int position) {
        return mDatas.get(position);
    }

    /**
     * 获取数据集合
     *
     * @return
     */
    public List<M> getDatas() {
        return mDatas;
    }

    /**
     * 在集合头部添加新的数据集合
     *
     * @param datas
     */
    public void addNewDatas(int position, List<M> datas) {
        if (datas != null) {
            mDatas.addAll(position, datas);
            notifyItemRangeInserted(position, datas.size());
        }
    }

    /**
     * 在集合尾部添加更多数据集合
     *
     * @param datas
     */
    public void addMoreDatas(List<M> datas) {
        if (datas != null) {
            mDatas.addAll(mDatas.size(), datas);
            notifyItemRangeInserted(mDatas.size(), datas.size());
        }
    }

    /**
     * 设置全新的数据集合，如果传入null，则清空数据列表
     *
     * @param datas
     */
    public void setDatas(List<M> datas) {
        if (datas != null) {
            mDatas.clear();
            mDatas.addAll(datas);
        }
        notifyDataSetChanged();
    }

    /**
     * 清空数据列表
     */
    public void clear() {
        mDatas.clear();
        notifyDataSetChanged();
    }

    /**
     * 删除指定索引数据条目
     *
     * @param position
     */
    public void removeItem(int position) {
        mDatas.remove(position);
        notifyItemRemoved(position);
    }

    /**
     * 删除指定数据条目
     *
     * @param model
     */
    public void removeItem(M model) {
        removeItem(mDatas.indexOf(model));
    }

    /**
     * 在指定位置添加数据条目
     *
     * @param position
     * @param model
     */
    public void addItem(int position, M model) {
        mDatas.add(position, model);
        notifyItemInserted(position);
    }

    /**
     * 在集合头部添加数据条目
     *
     * @param model
     */
    public void addFirstItem(M model) {
        addItem(0, model);
    }

    /**
     * 在集合末尾添加数据条目
     *
     * @param model
     */
    public void addLastItem(M model) {
        addItem(mDatas.size(), model);
    }

    /**
     * 替换指定索引的数据条目
     *
     * @param location
     * @param newModel
     */
    public void setItem(int location, M newModel) {
        mDatas.set(location, newModel);
        notifyItemChanged(location);
    }

    /**
     * 替换指定数据条目
     *
     * @param oldModel
     * @param newModel
     */
    public void setItem(M oldModel, M newModel) {
        setItem(mDatas.indexOf(oldModel), newModel);
    }

    /**
     * 移动数据条目的位置
     *
     * @param fromPosition
     * @param toPosition
     */
    public void moveItem(int fromPosition, int toPosition) {
        mDatas.add(toPosition, mDatas.remove(fromPosition));
        notifyItemMoved(fromPosition, toPosition);
    }
}
