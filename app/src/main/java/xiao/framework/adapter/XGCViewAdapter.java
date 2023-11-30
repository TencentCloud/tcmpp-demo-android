package xiao.framework.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import xiao.framework.adapter.viewholder.XGCViewHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Created by xiaoguochang on 2015/12/4.
 * 通用的Adapter，实现了getCount、getItem等方法,以及封装了getView的逻辑
 * <p/>
 * 多种itemview使用注意：
 * 必须实现getViewTypeCount和getItemViewType两个方法
 */
public abstract class XGCViewAdapter<T, H extends XGCViewHolder> extends BaseAdapter {
    protected Context mContext = null;
    /**
     * 数据
     */
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
            //需要更新holder中的position和type，需要注意的是更新需要放在inflate之前被调用，因为inflate中
            //会调用initWidgets初始化视图组件，多种itemview时initWidgets可能需要使用position和type
            holder.setPosition(position);
            holder.setViewType(itemViewType);
            convertView = holder.inflate(mContext, parent, false);
        } else {
            holder = (H) convertView.getTag();
            //需要更新holder中的position和type
            holder.setPosition(position);
            holder.setViewType(itemViewType);
        }

        //设置item数据
        setItemData(position, holder, getItem(position), itemViewType);

        return convertView;
    }

    /**
     * 创建ViewHolder(viewItem视图类型相同)
     * 当viewItemType为多种时：
     * 方法一：可以将所有item数据都放在一个ViewHolder中，那么需要在ViewHolder中根据type做处理，但这种处理方式结构不够清晰
     * 方法二：为不同的viewItemType创建不同的ViewHolder，每个item数据和逻辑分离，结构更清晰，推荐使用
     *
     * @return
     */
    protected abstract H createViewHolder(int viewType);

    /**
     * 设置每项数据到View上
     *
     * @param position Item索引
     * @param holder   ViewHolder
     * @param model    数据实体
     */
    protected abstract void setItemData(int position, H holder, T model, int viewType);

    /**
     * 获取数据集合
     *
     * @return
     */
    public List<T> getDatas() {
        return mDatas;
    }

    /**
     * 在集合头部添加新的数据集合
     *
     * @param datas
     */
    public void addNewDatas(List<T> datas) {
        if (datas != null) {
            mDatas.addAll(0, datas);
            notifyDataSetChanged();
        }
    }

    /**
     * 在集合尾部添加更多数据集合（上拉从服务器获取更多的数据集合，例如新浪微博列表上拉加载更晚时间发布的微博数据）
     *
     * @param datas
     */
    public void addMoreDatas(List<T> datas) {
        if (datas != null) {
            mDatas.addAll(mDatas.size(), datas);
            notifyDataSetChanged();
        }
    }

    /**
     * 设置全新的数据集合，如果传入null，则清空数据列表（第一次从服务器加载数据，或者下拉刷新当前界面数据表）
     *
     * @param datas
     */
    public void setDatas(List<T> datas) {
        if (datas != null) {
            mDatas = datas;
        } else {
            mDatas.clear();
        }
        notifyDataSetChanged();
    }

    /**
     * 在指定位置添加数据条目
     *
     * @param position
     * @param model
     */
    public void addItem(int position, T model) {
        mDatas.add(position, model);
        notifyDataSetChanged();
    }

    /**
     * 在集合头部添加数据条目
     *
     * @param model
     */
    public void addFirstItem(T model) {
        addItem(0, model);
    }

    /**
     * 在集合末尾添加数据条目
     *
     * @param model
     */
    public void addLastItem(T model) {
        addItem(mDatas.size(), model);
    }

    /**
     * 替换指定索引的数据条目
     *
     * @param location
     * @param newModel
     */
    public void setItem(int location, T newModel) {
        mDatas.set(location, newModel);
        notifyDataSetChanged();
    }

    /**
     * 替换指定数据条目
     *
     * @param oldModel
     * @param newModel
     */
    public void setItem(T oldModel, T newModel) {
        setItem(mDatas.indexOf(oldModel), newModel);
    }

    /**
     * 交换两个数据条目的位置
     *
     * @param fromPosition
     * @param toPosition
     */
    public void moveItem(int fromPosition, int toPosition) {
        Collections.swap(mDatas, fromPosition, toPosition);
        notifyDataSetChanged();
    }

    /**
     * 删除指定索引数据条目
     *
     * @param position
     */
    public void removeItem(int position) {
        mDatas.remove(position);
        notifyDataSetChanged();
    }

    /**
     * 删除指定数据条目
     *
     * @param model
     */
    public void removeItem(T model) {
        mDatas.remove(model);
        notifyDataSetChanged();
    }

    /**
     * 清空数据列表
     */
    public void clear() {
        mDatas.clear();
        notifyDataSetChanged();
    }
}
