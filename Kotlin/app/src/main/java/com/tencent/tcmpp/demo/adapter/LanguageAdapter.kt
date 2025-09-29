package com.tencent.tcmpp.demo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tencent.tcmpp.demo.R
import com.tencent.tcmpp.demo.activity.LanguageListActivity
import com.tencent.tcmpp.demo.utils.LocalUtil
import java.util.*

class LanguageAdapter : RecyclerView.Adapter<LanguageAdapter.VH>() {
    
    private var items: List<LanguageListActivity.LanguageItem> = ArrayList()
    private var currentLocale: Locale? = null
    private var languageChange: OnLanguageChange? = null

    fun update(items: List<LanguageListActivity.LanguageItem>) {
        this.items = items
        for (item in items) {
            if (item.selected) {
                currentLocale = item.locale
            }
        }
    }

    fun setOnLanguageChangeListener(onLanguageChange: OnLanguageChange) {
        this.languageChange = onLanguageChange
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.mini_language_item, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private lateinit var textView: TextView
        private lateinit var imageView: ImageView

        fun bind(pos: Int) {
            val item = items[pos]
            textView = itemView.findViewById(R.id.iv_language_name)
            imageView = itemView.findViewById(R.id.iv_language_selection)
            textView.text = item.name
            
            imageView.visibility = if (item.selected) View.VISIBLE else View.INVISIBLE

            itemView.setOnClickListener {
                if (!item.selected) {
                    items.forEach { it.selected = false }
                    item.selected = true
                    currentLocale = item.locale

                    languageChange?.onLanguageChange(
                        currentLocale?.language ?: "",
                        item.locale?.language ?: ""
                    )
                    LocalUtil.setCurrentLocale(item.locale?.toLanguageTag() ?: "")
                    notifyDataSetChanged()
                }
            }
        }
    }

    fun interface OnLanguageChange {
        fun onLanguageChange(old: String, newLocale: String)
    }
}