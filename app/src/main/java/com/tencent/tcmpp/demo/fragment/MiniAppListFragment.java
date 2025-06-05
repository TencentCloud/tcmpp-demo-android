package com.tencent.tcmpp.demo.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tencent.tcmpp.demo.R;
import com.tencent.tcmpp.demo.bean.ItemBean;
import com.tencent.tcmpp.demo.ui.MiniAppItemDecoration;
import com.tencent.tcmpp.demo.utils.GlobalConfigureUtil;
import com.tencent.tcmpp.demo.utils.MiniAppCategoryHelper;
import com.tencent.tmf.mini.api.TmfMiniSDK;
import com.tencent.tmf.mini.api.bean.MiniApp;
import com.tencent.tmf.mini.api.bean.MiniCode;
import com.tencent.tmf.mini.api.bean.MiniScene;
import com.tencent.tmf.mini.api.bean.MiniStartOptions;
import com.tencent.tmf.mini.api.bean.SearchOptions;
import com.tencent.tmfmini.sdk.core.utils.GsonUtils;

import java.util.ArrayList;
import java.util.List;

public class MiniAppListFragment extends Fragment {
    private static final String TAG = "MiniAppListFragment";
    public static String TYPE_RECENT = "recent";
    public static String TYPE_MY = "my";
    private String type;

    private List<ItemBean> mItemList = new ArrayList<>();
    private MiniAppRecyclerViewAdapter miniAppRecyclerViewAdapter;
    private final ResultReceiver mResultReceiver = new ResultReceiver(new Handler()) {
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            if (resultCode != MiniCode.CODE_OK) {
                //mini program startup error
                String errMsg = resultData.getString(MiniCode.KEY_ERR_MSG);
                Toast.makeText(getContext(), errMsg + resultCode, Toast.LENGTH_SHORT).show();
            } else {
                refreshData();
            }
        }
    };

    public MiniAppListFragment() {
    }

    public static MiniAppListFragment newInstance(String type) {
        Bundle args = new Bundle();
        MiniAppListFragment fragment = new MiniAppListFragment();
        args.putString("type", type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        if (null != getArguments()) {
            type = getArguments().getString("type");
        }
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mini_app_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView mRvMiniAppListView = view.findViewById(R.id.rv_my_mini_list);
        miniAppRecyclerViewAdapter = new MiniAppRecyclerViewAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRvMiniAppListView.addItemDecoration(new MiniAppItemDecoration(getActivity()));
        mRvMiniAppListView.setLayoutManager(layoutManager);
        mRvMiniAppListView.setAdapter(miniAppRecyclerViewAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshData();
    }

    private void refreshData() {
        mItemList.clear();

        if (TYPE_RECENT.equals(type)) {
            loadRecentList();
        } else if (TYPE_MY.equals(type)) {
            loadAllMiniAppList();
        }
    }

    private void loadRecentList() {
        TmfMiniSDK.getRecentList(recentList -> {
            if (!recentList.isEmpty()) {
                for (MiniApp app : recentList) {
                    mItemList.add(new ItemBean(0, app.name, app));
                }
            }
            Log.e(TAG, "miniLit recent " + GsonUtils.toJson(recentList));

            miniAppRecyclerViewAdapter.notifyDataSetChanged();
        });

    }

    private void loadAllMiniAppList() {
        SearchOptions searchOptions = new SearchOptions("");
        TmfMiniSDK.searchMiniApp(searchOptions, (code, msg, data) -> {
            List<ItemBean> list = new ArrayList<>();
            if (code == MiniCode.CODE_OK && data != null) {
                for (MiniApp app : data) {
                    list.add(new ItemBean(0, app.name, app));
                }
            } else {
                Toast.makeText(getContext(), msg + code, Toast.LENGTH_SHORT).show();
            }
            Log.e(TAG, "miniLit" + GsonUtils.toJson(list));
            setItemBeans(list);
        });
    }

    private void setItemBeans(List<ItemBean> beans) {
        if (beans == null) {
            mItemList.clear();
        } else {
            mItemList = beans;
        }
        miniAppRecyclerViewAdapter.notifyDataSetChanged();
    }

    private void startMiniApp(MiniApp app) {
        MiniStartOptions miniStartOptions = new MiniStartOptions();
        if (GlobalConfigureUtil.getGlobalConfig(getContext()).mockApi) {
            miniStartOptions.params = "noServer=1";
        }
        miniStartOptions.resultReceiver = mResultReceiver;
        TmfMiniSDK.startMiniApp(getActivity(),
                app.appId,
                MiniScene.LAUNCH_SCENE_MAIN_ENTRY,
                app.appVerType,
                miniStartOptions);
    }

    private void showMiniAppOperatePanel(MiniApp app) {
        MiniAppOperateDialogFragment miniAppOperateDialogFragment = new MiniAppOperateDialogFragment(app);
        miniAppOperateDialogFragment.show(getChildFragmentManager(), "dialog_of_mini_app");
    }


    private class MiniAppRecyclerViewAdapter extends
            RecyclerView.Adapter<MiniAppRecyclerViewAdapter.MiniAppViewHolder> {

        @Override
        public int getItemViewType(int position) {
            ItemBean bean = mItemList.get(position);
            return bean.mType;
        }

        @NonNull
        @Override
        public MiniAppRecyclerViewAdapter.MiniAppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            int layoutId = R.layout.mini_app_list_item_common;
            View itemView = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
            return new MiniAppRecyclerViewAdapter.MiniAppViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MiniAppRecyclerViewAdapter.MiniAppViewHolder holder, int position) {
            ItemBean itemBean = mItemList.get(position);
            holder.bindView(itemBean);

        }

        @Override
        public int getItemCount() {
            return mItemList.size();
        }

        class MiniAppViewHolder extends RecyclerView.ViewHolder {

            ImageView img;
            TextView miniAppName;
            TextView miniAppDesc;
            TextView miniAppCategory;
            ImageView imageViewMore;

            MiniAppViewHolder(@NonNull View itemView) {
                super(itemView);
                img = itemView.findViewById(R.id.iv_mini_app_item_icon);
                miniAppName = itemView.findViewById(R.id.tv_mini_app_item_name);
                miniAppDesc = itemView.findViewById(R.id.tv_mini_app_item_dec);
                miniAppCategory = itemView.findViewById(R.id.tv_mini_app_item_category);
                imageViewMore = itemView.findViewById(R.id.iv_mini_app_more);
            }

            void bindView(ItemBean bean) {
                Glide.with(itemView).load(bean.mAppInfo.iconUrl).placeholder(R.mipmap.ic_launcher).into(img);
                miniAppName.setText(bean.mAppInfo.name);
                miniAppDesc.setText(bean.mAppInfo.appIntro);
                // get category info from mini appInfo
                List<MiniAppCategoryHelper.MiniAppCategory> categories = MiniAppCategoryHelper.getCategoryFromString(bean.mAppInfo.appCategory);
                if (!categories.isEmpty()) {
                    miniAppCategory.setText(categories.get(0).secondLevelCategory);
                } else {
                    miniAppCategory.setText(bean.mAppInfo.appCategory);
                }
                itemView.setOnClickListener(v -> startMiniApp(bean.mAppInfo));
                imageViewMore.setOnClickListener(v -> showMiniAppOperatePanel(bean.mAppInfo));

            }
        }
    }


}
