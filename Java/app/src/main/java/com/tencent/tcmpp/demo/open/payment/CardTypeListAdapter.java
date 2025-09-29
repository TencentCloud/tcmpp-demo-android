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

public class CardTypeListAdapter extends RecyclerView.Adapter<CardTypeListAdapter.VH> {
    private List<BindCardFragment.CardTypeItem> paymentItems = new ArrayList<>();
    private MoreClickListen listen;

    public void updateList(List<BindCardFragment.CardTypeItem> typeItems) {
        this.paymentItems = typeItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tcmpp_item_card_type, parent, false);
        return new VH(view);
    }

    @Override
    public int getItemCount() {
        return paymentItems.size();
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        holder.bind(paymentItems.get(position));

    }

    public class VH extends RecyclerView.ViewHolder {
        private final TextView desc;
        private final ImageView icon;
        private final CheckBox checkBox;


        public VH(@NonNull View itemView) {
            super(itemView);
            desc = itemView.findViewById(R.id.tv_pay_type);
            icon = itemView.findViewById(R.id.iv_pay_type);
            checkBox = itemView.findViewById(R.id.cb_pay_type);
        }

        public void bind(BindCardFragment.CardTypeItem item) {
            if (0 != item.src) {
                icon.setImageResource(item.src);
            } else {
                icon.setImageResource(R.mipmap.ic_launcher);
            }
            itemView.setOnClickListener(v -> togglePaySelection(item));
            checkBox.setOnClickListener(v -> togglePaySelection(item));
            checkBox.setChecked(item.checked);
            desc.setText(item.name);
        }

        private void togglePaySelection(BindCardFragment.CardTypeItem item) {
            for (BindCardFragment.CardTypeItem payTypeItem : paymentItems) {
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

        void onCurrentPayType(BindCardFragment.CardTypeItem item);
    }
}
