package com.tencent.tcmpp.demo.open.payment;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Locale;

import com.google.gson.reflect.TypeToken;
import com.tencent.tcmpp.demo.R;
import com.tencent.tmfmini.sdk.core.utils.GsonUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PaymentListFragment extends Fragment {
    public static final String LOCAL_PAYMENT_CACHE = "local_payment";
    PaymentTypeListAdapter adapter;
    PayTypeItem payTypeItem;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_pay_method, container, false);
        setPage(view);
        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            adapter.updateList(serverItems());
        }
    }

    private void setPage(View view) {
        if (getArguments() != null) {
            double total = getArguments().getDouble("total");
            TextView totalView = view.findViewById(R.id.pay_method_account);
            totalView.setText(String.format(Locale.getDefault(), "%.2f", total));
        }

        RecyclerView recyclerView = view.findViewById(R.id.rv_pay_method);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PaymentTypeListAdapter();
        adapter.setMoreClickListen(new PaymentTypeListAdapter.MoreClickListen() {
            @Override
            public void onMoreClick() {
                ((PaymentMethodActivity) requireActivity()).showBindCard();
            }

            @Override
            public void onCurrentPayType(PayTypeItem item) {
                payTypeItem = item;

            }
        });
        recyclerView.setAdapter(adapter);

        List<PayTypeItem> payTypeItems = serverItems();
        if (!payTypeItems.isEmpty()) {
            payTypeItem = payTypeItems.get(0);
            payTypeItem.checked = true;
        }
        adapter.updateList(payTypeItems);

        view.findViewById(R.id.btn_pay_method_confirm).setOnClickListener(v -> {
                    PaymentManager.g().showPwdConfirm(getActivity(), payTypeItem.src, payTypeItem.name, payTypeItem.payModel, payTypeItem.onlineId);
                }
        );
    }

    private List<PayTypeItem> serverItems() {

        List<PayTypeItem> result = new ArrayList<>();
        String rawData = requireArguments().getString("rawData");
        if (!TextUtils.isEmpty(rawData)) {
            try {
                JSONObject raw = new JSONObject(rawData);

                JSONArray cardListJson = raw.optJSONArray("payModelList");
                if (null != cardListJson) {
                    for (int i = 0; i < cardListJson.length(); i++) {
                        JSONObject object = cardListJson.optJSONObject(i);
                        String payModelId = object.optString("payModelId");
                        String payModelName = object.optString("payModelName");
                        String payModelIcon = object.optString("payModelIcon");
                        String payModel = object.optString("payModel");
                        PayTypeItem item = new PayTypeItem(payModelId, payModelIcon, payModelName, payModel, false);
                        result.add(item);
                    }
                }
            } catch (JSONException ignored) {
            }
        }
        List<PayTypeItem> local = loadCachedItems(requireContext());
        if (!local.isEmpty()) {
            result.addAll(local);
        }
        result.add(new PayTypeItem(0, requireContext().getString(R.string.tcmpp_payment_add), false, PayTypeItem.TYPE_ADD));
        return result;
    }

    public static List<PayTypeItem> loadCachedItems(Context context) {
        List<PayTypeItem> result = new ArrayList<>();
        String ret = context.getSharedPreferences("payment", Context.MODE_MULTI_PROCESS).getString(LOCAL_PAYMENT_CACHE, "");
//        String ret = SPUtils.getInstance().getString(LOCAL_PAYMENT_CACHE, "");
        if (!TextUtils.isEmpty(ret)) {
            Type listType = new TypeToken<List<PayTypeItem>>() {
            }.getType();
            List<PayTypeItem> cached = GsonUtils.fromJson(ret, listType);
            result.addAll(cached);
        }
        return result;
    }


    public static class PayTypeItem {
        public static final int TYPE_NORMAL = 0;
        public static final int TYPE_ADD = 1;
        public static final int TYPE_WALLET = 2;
        public int src;
        public String name;
        public boolean checked;
        public int type;

        public String onlineSrc;
        public String onlineId;
        public double balence;
        public String payModel;

        public PayTypeItem(int src, String name, boolean checked, int type) {
            this.src = src;
            this.name = name;
            this.checked = checked;
            this.type = type;
            this.payModel = "bankcard";
        }

        public PayTypeItem(String onlineId, String src, String name, String model, boolean checked) {
            this.onlineId = onlineId;
            this.onlineSrc = src;
            this.name = name;
            this.checked = checked;
            this.payModel = model;
        }

        public PayTypeItem(String onlineSrc, String name, double balence) {
            this.onlineId = onlineSrc;
            this.name = name;
            this.balence = balence;
            this.type = PayTypeItem.TYPE_WALLET;
        }
    }

}
