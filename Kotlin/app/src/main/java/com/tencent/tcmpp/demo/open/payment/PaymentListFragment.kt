package com.tencent.tcmpp.demo.open.payment

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.reflect.TypeToken
import com.tencent.tcmpp.demo.R
import com.tencent.tmfmini.sdk.core.utils.GsonUtils
import org.json.JSONException
import org.json.JSONObject
import java.lang.reflect.Type

class PaymentListFragment : Fragment() {

    companion object {
        const val LOCAL_PAYMENT_CACHE = "local_payment"

        fun loadCachedItems(context: Context): List<PayTypeItem> {
            val result = mutableListOf<PayTypeItem>()
            val ret = context.getSharedPreferences("payment", Context.MODE_MULTI_PROCESS)
                .getString(LOCAL_PAYMENT_CACHE, "")

            if (!TextUtils.isEmpty(ret)) {
                val listType: Type = object : TypeToken<List<PayTypeItem>>() {}.type
                val cached: List<PayTypeItem> = GsonUtils.fromJson(ret, listType)
                result.addAll(cached)
            }
            return result
        }
    }

    private lateinit var adapter: PaymentTypeListAdapter
    private var payTypeItem: PayTypeItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.frag_pay_method, container, false)
        setPage(view)
        return view
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            adapter.updateList(serverItems())
        }
    }

    private fun setPage(view: View) {
        arguments?.let { args ->
            val total = args.getDouble("total")
            val totalView = view.findViewById<TextView>(R.id.pay_method_account)
            totalView.text = String.format("%.2f", total)
        }

        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_pay_method)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = PaymentTypeListAdapter()
        adapter.setMoreClickListen(object : PaymentTypeListAdapter.MoreClickListen {
            override fun onMoreClick() {
                (requireActivity() as PaymentMethodActivity).showBindCard()
            }

            override fun onCurrentPayType(item: PayTypeItem) {
                payTypeItem = item
            }
        })
        recyclerView.adapter = adapter

        val payTypeItems = serverItems()
        if (payTypeItems.isNotEmpty()) {
            payTypeItem = payTypeItems[0]
            payTypeItem?.checked = true
        }
        adapter.updateList(payTypeItems)

        view.findViewById<View>(R.id.btn_pay_method_confirm).setOnClickListener {
            payTypeItem?.let { item ->
                activity?.let { act ->
                    PaymentManager.g().showPwdConfirm(
                        act,
                        item.src,
                        item.name,
                        item.payModel,
                        item.onlineId
                    )
                }
            }
        }
    }

    private fun serverItems(): List<PayTypeItem> {
        val result = mutableListOf<PayTypeItem>()
        val rawData = requireArguments().getString("rawData")

        if (!TextUtils.isEmpty(rawData)) {
            try {
                val raw = JSONObject(rawData!!)
                val cardListJson = raw.optJSONArray("payModelList")

                cardListJson?.let { jsonArray ->
                    for (i in 0 until jsonArray.length()) {
                        val obj = jsonArray.optJSONObject(i)
                        val payModelId = obj.optString("payModelId")
                        val payModelName = obj.optString("payModelName")
                        val payModelIcon = obj.optString("payModelIcon")
                        val payModel = obj.optString("payModel")
                        val item = PayTypeItem(payModelId, payModelIcon, payModelName, payModel, false)
                        result.add(item)
                    }
                }
            } catch (ignored: JSONException) {
            }
        }

        val local = loadCachedItems(requireContext())
        if (local.isNotEmpty()) {
            result.addAll(local)
        }

        result.add(
            PayTypeItem(
                0,
                requireContext().getString(R.string.tcmpp_payment_add),
                false,
                PayTypeItem.TYPE_ADD
            )
        )
        return result
    }

    data class PayTypeItem(
        var src: Int = 0,
        var name: String = "",
        var checked: Boolean = false,
        var type: Int = TYPE_NORMAL,
        var onlineSrc: String = "",
        var onlineId: String = "",
        var balence: Double = 0.0,
        var payModel: String = "bankcard"
    ) {
        companion object {
            const val TYPE_NORMAL = 0
            const val TYPE_ADD = 1
            const val TYPE_WALLET = 2
        }

        constructor(src: Int, name: String, checked: Boolean, type: Int) : this() {
            this.src = src
            this.name = name
            this.checked = checked
            this.type = type
            this.payModel = "bankcard"
        }

        constructor(onlineId: String, src: String, name: String, model: String, checked: Boolean) : this() {
            this.onlineId = onlineId
            this.onlineSrc = src
            this.name = name
            this.checked = checked
            this.payModel = model
        }

        constructor(onlineSrc: String, name: String, balence: Double) : this() {
            this.onlineId = onlineSrc
            this.name = name
            this.balence = balence
            this.type = TYPE_WALLET
        }
    }
}