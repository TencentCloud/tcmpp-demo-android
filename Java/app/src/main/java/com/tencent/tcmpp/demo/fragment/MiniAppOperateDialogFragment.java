package com.tencent.tcmpp.demo.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.tencent.tcmpp.demo.R;
import com.tencent.tcmpp.demo.activity.MainContentActivity;
import com.tencent.tcmpp.demo.utils.ScreenUtil;
import com.tencent.tmf.mini.api.TmfMiniSDK;
import com.tencent.tmf.mini.api.bean.MiniApp;

public class MiniAppOperateDialogFragment extends DialogFragment {
    private MiniApp mMiniApp;

    public MiniAppOperateDialogFragment(MiniApp miniApp) {
        this.mMiniApp = miniApp;

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mini_app_operate, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setViewListener(view);
    }

    @Override
    public void onStart() {
        super.onStart();
        setUpWindowLocation();

    }

    private void setViewListener(View view) {
        view.findViewById(R.id.tv_mini_operate_preload).setOnClickListener(v -> {
            TmfMiniSDK.preloadMiniApp(getContext(), new Bundle());
            dismissAllowingStateLoss();
            ((MainContentActivity) getActivity()).showToast(getString(R.string.applet_pre_load_success));
        });
        view.findViewById(R.id.tv_mini_operate_clear_cache).setOnClickListener(v -> {
            TmfMiniSDK.deleteMiniApp(mMiniApp.appId);
            dismissAllowingStateLoss();
            ((MainContentActivity) getActivity()).showToast(getString(R.string.applet_clear_cache_success));

        });
        view.findViewById(R.id.tv_mini_operate_reset).setOnClickListener(v -> {
            TmfMiniSDK.stopMiniApp(getContext(), mMiniApp.appId);
            dismissAllowingStateLoss();
            ((MainContentActivity) getActivity()).showToast(getString(R.string.applet_reset_load_success));

        });

        view.findViewById(R.id.tv_mini_operate_cancel).setOnClickListener(v -> dismissAllowingStateLoss());
    }

    private void setUpWindowLocation() {
        WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();

        params.gravity = Gravity.BOTTOM | Gravity.END;
        params.width = ScreenUtil.getScreenWidth(getContext());
        getDialog().getWindow().getDecorView().setBackgroundColor(Color.TRANSPARENT);
        getDialog().getWindow().setAttributes(params);
    }
}
