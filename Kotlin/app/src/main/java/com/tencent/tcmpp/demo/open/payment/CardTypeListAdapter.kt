package com.tencent.tcmpp.demo.open.payment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tencent.tcmpp.demo.R

class CardTypeListAdapter : RecyclerView.Adapter<CardTypeListAdapter.VH>() {
    
    private var paymentItems: List<BindCardFragment.CardTypeItem> = ArrayList()
    private var listen: MoreClickListen? = null

    fun updateList(typeItems: List<BindCardFragment.CardTypeItem>) {
        this.paymentItems = typeItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.tcmpp_item_card_type, parent, false)
        return VH(view)
    }

    override fun getItemCount(): Int = paymentItems.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(paymentItems[position])
    }

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val desc: TextView = itemView.findViewById(R.id.tv_pay_type)
        private val icon: ImageView = itemView.findViewById(R.id.iv_pay_type)
        private val checkBox: CheckBox = itemView.findViewById(R.id.cb_pay_type)

        fun bind(item: BindCardFragment.CardTypeItem) {
            if (item.src != 0) {
                icon.setImageResource(item.src)
            } else {
                icon.setImageResource(R.mipmap.ic_launcher)
            }
            
            itemView.setOnClickListener { togglePaySelection(item) }
            checkBox.setOnClickListener { togglePaySelection(item) }
            checkBox.isChecked = item.checked
            desc.text = item.name
        }

        private fun togglePaySelection(item: BindCardFragment.CardTypeItem) {
            paymentItems.forEach { it.checked = false }
            item.checked = true
            notifyDataSetChanged()
            listen?.onCurrentPayType(item)
        }
    }

    interface MoreClickListen {
        fun onMoreClick()
        fun onCurrentPayType(item: BindCardFragment.CardTypeItem)
    }
}