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

    protected ViewFinder mViewFinder;

    protected ViewGroup mParentView;

    protected int mViewType;

    private boolean isBindView = true;

    protected XGCOnRVItemClickListener mOnRVItemClickListener;

    protected XGCOnRVItemLongClickListener mOnRVItemLongClickListener;

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

    protected abstract void initWidgets();

    public abstract void setData(M data);

    public <T extends View> T findViewById(int id) {
        return mViewFinder.findViewById(id);
    }

    public void setOnRVItemClickListener(XGCOnRVItemClickListener onRVItemClickListener) {
        mOnRVItemClickListener = onRVItemClickListener;
    }

    public void setOnRVItemLongClickListener(XGCOnRVItemLongClickListener onRVItemLongClickListener) {
        mOnRVItemLongClickListener = onRVItemLongClickListener;
    }

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
