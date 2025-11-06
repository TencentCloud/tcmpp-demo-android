package com.tencent.tcmpp.demo.fragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tencent.tcmpp.demo.R;

public class LoginDialogFragment extends DialogFragment {
    private String mMiniAppName;
    private AuthListener mAuthListener;

    public static LoginDialogFragment newInstance(String miniAppName) {
        LoginDialogFragment fragment = new LoginDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("miniAppName", miniAppName);
        fragment.setArguments(bundle);
        return fragment;
    }

    //Used to set login authorization, listen for callbacks
    public void setAuthListener(AuthListener authListener) {
        mAuthListener = authListener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_FRAME, R.style.DialogFragmentNoPaddingStyle);
        Bundle arguments = getArguments();
        if (arguments == null) {
            return;
        }
        mMiniAppName = arguments.getString("miniAppName");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View dialog = inflater.inflate(R.layout.dialog_fragment_mock_login, container, false);
        TextView appNameView = dialog.findViewById(R.id.tv_mini_app_name);
        Button btnAgree = dialog.findViewById(R.id.btn_agree);
        btnAgree.setOnClickListener(v -> {
            // Handle agree button click
            // Complete login and authorization operations, and return the username
            if (mAuthListener != null) {
                mAuthListener.onAuthCallback(true, "Super app user");
            }
            dismiss();
        });
        Button btnRefuse = dialog.findViewById(R.id.btn_refuse);
        btnRefuse.setOnClickListener(v -> {
            // Handle refuse button click
            if (mAuthListener != null) {
                mAuthListener.onAuthCallback(false, null);
            }
            dismiss();
        });
        appNameView.setText(mMiniAppName);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog == null) {
            return;
        }
        if (dialog.getWindow() == null) {
            return;
        }
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAuthListener = null;
    }

    public interface AuthListener {
        void onAuthCallback(boolean isAuth, String userName);
    }
}
