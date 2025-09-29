package com.tencent.tcmpp.demo.open.payment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tencent.tcmpp.demo.R

class PaymentTypeListAdapter : RecyclerView.Adapter<PaymentTypeListAdapter.VH>() {
    
    private var paymentItems: List<PaymentListFragment.PayTypeItem> = ArrayList()
    private var listen: MoreClickListen? = null

    fun updateList(typeItems: List<PaymentListFragment.PayTypeItem>) {
        this.paymentItems = typeItems
        notifyDataSetChanged()
    }

    fun setMoreClickListen(listen: MoreClickListen) {
        this.listen = listen
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.tcmpp_item_pay_type, parent, false)
        return VH(view)
    }

    override fun getItemCount(): Int = paymentItems.size

    override fun getItemViewType(position: Int): Int = paymentItems[position].type

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(paymentItems[position])
    }

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val desc: TextView = itemView.findViewById(R.id.tv_pay_type)
        private val icon: ImageView = itemView.findViewById(R.id.iv_pay_type)
        private val checkBox: CheckBox = itemView.findViewById(R.id.cb_pay_type)
        private val more: ImageView = itemView.findViewById(R.id.iv_pay_type_add)

        fun bind(item: PaymentListFragment.PayTypeItem) {
            when (item.type) {
                PaymentListFragment.PayTypeItem.TYPE_ADD -> {
                    checkBox.visibility = View.INVISIBLE
                    icon.visibility = View.INVISIBLE
                    more.visibility = View.VISIBLE
                    itemView.setOnClickListener {
                        listen?.onMoreClick()
                    }
                }
                PaymentListFragment.PayTypeItem.TYPE_NORMAL -> {
                    more.visibility = View.INVISIBLE
                    checkBox.visibility = View.VISIBLE
                    itemView.setOnClickListener {
                        togglePaySelection(item)
                    }
                    checkBox.setOnClickListener {
                        togglePaySelection(item)
                    }
                }
            }
            
            if (item.src != 0) {
                icon.setImageResource(item.src)
            }
            checkBox.isChecked = item.checked
            desc.text = item.name
        }

        private fun togglePaySelection(item: PaymentListFragment.PayTypeItem) {
            paymentItems.forEach { it.checked = false }
            item.checked = true
            notifyDataSetChanged()
            listen?.onCurrentPayType(item)
        }
    }

    interface MoreClickListen {
        fun onMoreClick()
        fun onCurrentPayType(item: PaymentListFragment.PayTypeItem)
    }
}