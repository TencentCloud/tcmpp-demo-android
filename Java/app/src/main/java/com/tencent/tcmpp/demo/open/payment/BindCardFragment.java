package com.tencent.tcmpp.demo.open.payment;



import static com.tencent.tcmpp.demo.open.payment.PaymentListFragment.LOCAL_PAYMENT_CACHE;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tencent.tcmpp.demo.R;
import com.tencent.tmfmini.sdk.core.utils.GsonUtils;

import java.util.ArrayList;
import java.util.List;

public class BindCardFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_bind_card, container, false);
        setPage(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void setPage(View view) {

        RecyclerView recyclerView = view.findViewById(R.id.rv_bind_card_type);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        CardTypeListAdapter adapter = new CardTypeListAdapter();
        recyclerView.setAdapter(adapter);
        adapter.updateList(defaultItems());

        Button button = view.findViewById(R.id.btn_bind_submit_info);
        button.setOnClickListener(v -> {
            EditText cardNumEt = view.findViewById(R.id.et_bind_card_num);
            String name = cardNumEt.getText().toString().trim();
            if (TextUtils.isEmpty(name)) {
                return;
            }
            PaymentListFragment.PayTypeItem payTypeItem = new PaymentListFragment.PayTypeItem(R.drawable.demo_payment_card, name, false, 0);
            List<PaymentListFragment.PayTypeItem> items = PaymentListFragment.loadCachedItems(requireContext());
            items.add(payTypeItem);
            requireActivity().getSharedPreferences("payment", Context.MODE_MULTI_PROCESS)
                    .edit()
                    .putString(LOCAL_PAYMENT_CACHE, GsonUtils.toJson(items))
                    .apply();
            cardNumEt.setText("");
            ((PaymentMethodActivity) requireActivity()).showPayList();

        });
    }

    private List<CardTypeItem> defaultItems() {
        List<CardTypeItem> items = new ArrayList<>();
        items.add(new CardTypeItem(R.drawable.pay_method_wechat, requireContext().getString(R.string.tcmpp_payment_we_chat_pay), true));
        items.add(new CardTypeItem(R.drawable.pay_method_visa, "Visa", false));
        return items;
    }


    public static class CardTypeItem {
        public int src;
        public String name;
        public boolean checked;

        public CardTypeItem(int src, String name, boolean checked) {
            this.src = src;
            this.name = name;
            this.checked = checked;
        }
    }
}
