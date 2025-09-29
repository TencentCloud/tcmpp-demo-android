package com.tencent.tcmpp.demo.open.payment

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tencent.tcmpp.demo.R
import com.tencent.tcmpp.demo.open.payment.PaymentListFragment.Companion.LOCAL_PAYMENT_CACHE
import com.tencent.tmfmini.sdk.core.utils.GsonUtils

class BindCardFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.frag_bind_card, container, false)
        setPage(view)
        return view
    }

    override fun onResume() {
        super.onResume()
    }

    private fun setPage(view: View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_bind_card_type)
        recyclerView.layoutManager = LinearLayoutManager(context)
        val adapter = CardTypeListAdapter()
        recyclerView.adapter = adapter
        adapter.updateList(defaultItems())

        val button = view.findViewById<Button>(R.id.btn_bind_submit_info)
        button.setOnClickListener {
            val cardNumEt = view.findViewById<EditText>(R.id.et_bind_card_num)
            val name = cardNumEt.text.toString().trim()
            if (TextUtils.isEmpty(name)) {
                return@setOnClickListener
            }

            val payTypeItem = PaymentListFragment.PayTypeItem(
                R.drawable.demo_payment_card,
                name,
                false,
                0
            )

            val items = PaymentListFragment.loadCachedItems(requireContext()).toMutableList()
            items.add(payTypeItem)

            requireActivity().getSharedPreferences("payment", Context.MODE_MULTI_PROCESS)
                .edit()
                .putString(LOCAL_PAYMENT_CACHE, GsonUtils.toJson(items))
                .apply()

            cardNumEt.setText("")
            (requireActivity() as PaymentMethodActivity).showPayList()
        }
    }

    private fun defaultItems(): List<CardTypeItem> {
        val items = mutableListOf<CardTypeItem>()
        items.add(
            CardTypeItem(
                R.drawable.pay_method_wechat,
                requireContext().getString(R.string.tcmpp_payment_we_chat_pay),
                true
            )
        )
        items.add(CardTypeItem(R.drawable.pay_method_visa, "Visa", false))
        return items
    }

    data class CardTypeItem(
        val src: Int,
        val name: String,
        var checked: Boolean
    )
}