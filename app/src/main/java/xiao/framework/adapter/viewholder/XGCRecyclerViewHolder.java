package xiao.framework.adapter.viewholder;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import xiao.framework.adapter.ViewFinder;
import xiao.framework.adapter.XGCOnRVItemClickListener;
import xiao.framework.adapter.XGCOnRVItemLongClickListener;
import xiao.framework.adapter.XGCRecyclerViewAdapter;


/**
 * Created by xiaoguochang on 2015/12/6.
 */
public abstract class XGCRecyclerViewHolder<M, Adpt extends XGCRecyclerViewAdapter> extends RecyclerView.ViewHolder {
    protected Context mContext;
    protected Adpt mAdapter;
    /**
     * 视图查找器
     */
    protected ViewFinder mViewFinder;
    /**
     * ItemView视图的parent
     */
    protected ViewGroup mParentView;
    /**
     * ItemView视图(RecyclerView.ViewHolder)
     */
    protected int mViewType;
    /**
     * 是否使用ButterKnife绑定视图
     */
    private boolean isBindView = true;
    /**
     * 整个item的点击监听
     */
    protected XGCOnRVItemClickListener mOnRVItemClickListener;
    /**
     * 整个item的长按监听
     */
    protected XGCOnRVItemLongClickListener mOnRVItemLongClickListener;

    /**
     * 子类必须要实现
     *
     * @param context
     * @param parent
     * @param itemView
     * @param viewType
     */
    public XGCRecyclerViewHolder(Context context, Adpt adapter, ViewGroup parent, View itemView, int viewType) {
        super(itemView);

        mContext = context;
        mAdapter = adapter;
        mViewType = viewType;
        mParentView = parent;
        itemView.setOnClickListener(iItemClickListener);
        itemView.setOnLongClickListener(iItemLongClickListener);
        mViewFinder = new ViewFinder(itemView);

        initWidgets();
    }

    protected void setBindView(boolean isBindView) {
        this.isBindView = isBindView;
    }

    /**
     * 初始化各个子视图
     */
    protected abstract void initWidgets();

    public abstract void setData(M data);

    public <T extends View> T findViewById(int id) {
        return mViewFinder.findViewById(id);
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

    /**
     * 设置item的点击事件监听器
     */
    private View.OnClickListener iItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == itemView.getId() && mOnRVItemClickListener != null) {
                int position = getAdapterPosition();
                if (position < 0 || mAdapter.getDatas() == null || position > mAdapter.getItemCount() - 1) {
                    return;
                }
                //整个item的点击回调
                mOnRVItemClickListener.onRVItemClick(mParentView, view, getAdapterPosition());
            }
        }
    };

    /**
     * 设置item的长按事件监听器
     */
    private View.OnLongClickListener iItemLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {
            if (view.getId() == itemView.getId() && mOnRVItemLongClickListener != null) {
                int position = getAdapterPosition();
                if (position < 0 || mAdapter.getDatas() == null || position > mAdapter.getItemCount() - 1) {
                    return false;
                }
                //整个item的点击回调
                return mOnRVItemLongClickListener.onRVItemLongClick(mParentView, view, getAdapterPosition());
            }

            return false;
        }
    };

}
