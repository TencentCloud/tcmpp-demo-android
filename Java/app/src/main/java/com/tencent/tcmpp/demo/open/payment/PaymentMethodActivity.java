package com.tencent.tcmpp.demo.open.payment;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import com.tencent.tcmpp.demo.activity.BaseActivity;

import com.tencent.tcmpp.demo.R;


public class PaymentMethodActivity extends BaseActivity {
    private PaymentListFragment mPayFragment;
    private BindCardFragment mBindCardFragment;
    private TextView titleTextView;
    private double totalFee;
    private String rawData;
    private boolean isShowList = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_payment_method);
        totalFee = getIntent().getDoubleExtra("totalFee", 88.0);
        rawData = getIntent().getStringExtra("rawData");
        initToolbarClick();
        initChildFragment();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initToolbarClick() {
        titleTextView = findViewById(R.id.tv_pay_title_info);
        titleTextView.setText(R.string.tcmpp_payment_choose);
        findViewById(R.id.iv_pay_back).setOnClickListener(v -> {
            if (isShowList) {
                PaymentManager.g().notifyPaymentCancel();
                finish();
            } else {
                showPayList();
                isShowList = true;
            }
        });
    }

    private void initChildFragment() {
        mPayFragment = new PaymentListFragment();
        Bundle bundle = new Bundle();
        bundle.putDouble("total", totalFee);
        bundle.putString("rawData", rawData);
        mPayFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.frag_container_pay_method, mPayFragment, "pay_list")
                .commit();
    }

    public void showBindCard() {
        if (null == mBindCardFragment) {
            mBindCardFragment = new BindCardFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.frag_container_pay_method, mBindCardFragment, "bind_card")
                    .commitAllowingStateLoss();
        }
        titleTextView.setText(R.string.tcmpp_payment_add_card);
        getSupportFragmentManager().beginTransaction()
                .hide(mPayFragment)
                .show(mBindCardFragment)
                .commitAllowingStateLoss();
        isShowList = false;
    }

    public void showPayList() {
        titleTextView.setText(R.string.tcmpp_payment_choose);

        getSupportFragmentManager().beginTransaction()
                .hide(mBindCardFragment)
                .show(mPayFragment).commitAllowingStateLoss();
        isShowList = true;
    }

    @Override
    public void onBackPressed() {
        if (isShowList) {
            PaymentManager.g().notifyPaymentCancel();
        }
    }
}
