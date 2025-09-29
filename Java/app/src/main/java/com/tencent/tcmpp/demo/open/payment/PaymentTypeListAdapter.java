package com.tencent.tcmpp.demo.open.payment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.tencent.tcmpp.demo.R;

import java.util.ArrayList;
import java.util.List;

public class PaymentTypeListAdapter extends RecyclerView.Adapter<PaymentTypeListAdapter.VH> {
    private List<PaymentListFragment.PayTypeItem> paymentItems = new ArrayList<>();
    private MoreClickListen listen;

    public void updateList(List<PaymentListFragment.PayTypeItem> typeItems) {
        this.paymentItems = typeItems;
        notifyDataSetChanged();
    }

    public void setMoreClickListen(MoreClickListen listen) {
        this.listen = listen;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tcmpp_item_pay_type, parent, false);
        return new VH(view);
    }

    @Override
    public int getItemCount() {
        return paymentItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        return paymentItems.get(position).type;
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        holder.bind(paymentItems.get(position));

    }

    public class VH extends RecyclerView.ViewHolder {
        private final TextView desc;
        private final ImageView icon;
        private final CheckBox checkBox;
        private final ImageView more;


        public VH(@NonNull View itemView) {
            super(itemView);
            desc = itemView.findViewById(R.id.tv_pay_type);
            icon = itemView.findViewById(R.id.iv_pay_type);
            more = itemView.findViewById(R.id.iv_pay_type_add);
            checkBox = itemView.findViewById(R.id.cb_pay_type);
        }

        public void bind(PaymentListFragment.PayTypeItem item) {
            if (item.type == PaymentListFragment.PayTypeItem.TYPE_ADD) {
                checkBox.setVisibility(View.INVISIBLE);
                icon.setVisibility(View.INVISIBLE);
                more.setVisibility(View.VISIBLE);
                itemView.setOnClickListener(v -> {
                    if (null != listen) {
                        listen.onMoreClick();
                    }
                });
            } else if (item.type == PaymentListFragment.PayTypeItem.TYPE_NORMAL) {
                more.setVisibility(View.INVISIBLE);
                checkBox.setVisibility(View.VISIBLE);
                itemView.setOnClickListener(v -> {
                    togglePaySelection(item);

                });
                checkBox.setOnClickListener(v -> togglePaySelection(item));
//                if (null == GlideUtil.with()) {
//                    GlideUtil.init(ModuleApplet.sApp);
//                }
//                GlideUtil.with().displayImage(item.onlineSrc, icon);

            }
            if (0 != item.src) {
                icon.setImageResource(item.src);
            }
            checkBox.setChecked(item.checked);
            desc.setText(item.name);
        }

        private void togglePaySelection(PaymentListFragment.PayTypeItem item) {
            for (PaymentListFragment.PayTypeItem payTypeItem : paymentItems) {
                payTypeItem.checked = false;
            }
            item.checked = true;
            notifyDataSetChanged();
            if (null != listen) {
                listen.onCurrentPayType(item);
            }
        }
    }

    public interface MoreClickListen {
        void onMoreClick();

        void onCurrentPayType(PaymentListFragment.PayTypeItem item);
    }
}
