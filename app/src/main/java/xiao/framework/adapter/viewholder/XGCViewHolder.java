package xiao.framework.adapter.viewholder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import xiao.framework.adapter.ViewFinder;
import xiao.framework.adapter.XGCViewAdapter;


/**
 * Created by xiaoguochang on 2015/12/4.
 */
public abstract class XGCViewHolder<Adpt extends XGCViewAdapter> {
    protected Context mContext;
    protected Adpt mAdapter;

    protected ViewFinder mViewFinder;

    protected ViewGroup mParentView;

    protected View mItemView;

    private boolean isBindView = true;

    protected int mViewType;

    protected int mPosition;

    public XGCViewHolder(Adpt adapter) {
        mAdapter = adapter;
    }

    public View inflate(Context context, ViewGroup parent, boolean attachToRoot) {
        mContext = context;
        mParentView = parent;
        mItemView = LayoutInflater.from(context).inflate(getItemLayout(), parent, attachToRoot);
        // 设置tag
        mItemView.setTag(this);
        mViewFinder = new ViewFinder(mItemView);

        initWidgets();

        return mItemView;
    }

    protected void setBindView(boolean isBindView){
        this.isBindView = isBindView;
    }

    protected abstract int getItemLayout();

    protected abstract void initWidgets();

    public <T extends View> T findViewById(int id) {
        return mViewFinder.findViewById(id);
    }

    public void setPosition(int position) {
        mPosition = position;
    }

    public int getPosition() {
        return mPosition;
    }

    public int getViewType() {
        return mViewType;
    }

    public void setViewType(int mViewType) {
        this.mViewType = mViewType;
    }
}
