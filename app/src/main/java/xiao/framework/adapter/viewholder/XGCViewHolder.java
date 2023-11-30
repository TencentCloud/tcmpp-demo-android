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
    /**
     * 视图查找器
     */
    protected ViewFinder mViewFinder;
    /**
     * ItemView视图的parent
     */
    protected ViewGroup mParentView;
    /**
     * ItemView视图
     */
    protected View mItemView;
    /**
     * 是否使用ButterKnife绑定视图
     */
    private boolean isBindView = true;
    /**
     * item类型
     */
    protected int mViewType;
    /**
     * item所在的位置
     */
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

    /**
     * 获取ItemView的布局Id
     * 多种itemview时，需要根据mViewType返回不同的layout
     *
     * @return Item View布局
     */
    protected abstract int getItemLayout();

    /**
     * 初始化各个子视图
     *
     * 多种itemview时，需要根据mViewType初始化不同的view
     */
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
