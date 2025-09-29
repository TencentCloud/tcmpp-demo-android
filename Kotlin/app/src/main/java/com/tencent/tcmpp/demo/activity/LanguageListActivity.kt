package com.tencent.tcmpp.demo.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tencent.tcmpp.demo.R
import com.tencent.tcmpp.demo.adapter.LanguageAdapter
import com.tencent.tcmpp.demo.sp.impl.CommonSp
import com.tencent.tcmpp.demo.utils.DynamicLanguageUtil
import com.tencent.tcmpp.demo.utils.LocalUtil
import java.util.*

class LanguageListActivity : BaseActivity() {
    
    private lateinit var mCurrentLocale: String
    private lateinit var mSelectedLocale: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_language_list)
        DynamicLanguageUtil.setAppLanguage(this, LocalUtil.current().language)

        mCurrentLocale = LocalUtil.SUPPORTED_LOCALES[CommonSp.getInstance().miniLanguage].language
        mSelectedLocale = mCurrentLocale
        setLanguageList()
        addClickListen()
    }

    override fun onStart() {
        super.onStart()
        setLanguageList()
    }

    private fun addClickListen() {
        findViewById<View>(R.id.iv_language_back_img).setOnClickListener {
            val intent = Intent()
            intent.putExtra("isLanguageChange", mCurrentLocale != mSelectedLocale)
            setResult(MainContentActivity.REQ_CODE_OF_LANGUAGE_LIST, intent)
            finish()
        }
    }

    private fun setLanguageList() {
        val recyclerView = findViewById<RecyclerView>(R.id.rv_language_list)

        val adapter = LanguageAdapter()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        adapter.setOnLanguageChangeListener { _, newLocale ->
            mSelectedLocale = newLocale
        }
        recyclerView.adapter = adapter
        adapter.update(getLanguageItems())

        val title = findViewById<TextView>(R.id.tv_language_title)
        title.text = resources.getText(R.string.applet_main_tool_language)
    }

    private fun getLanguageItems(): List<LanguageItem> {
        val current = CommonSp.getInstance().miniLanguage
        val items = ArrayList<LanguageItem>()
        for (i in LocalUtil.SUPPORTED_LOCALES.indices) {
            val locale = LocalUtil.SUPPORTED_LOCALES[i]
            val item = LanguageItem().apply {
                this.locale = locale
                selected = current == i
                name = resources.getStringArray(R.array.applet_language_name)[i]
            }
            items.add(item)
        }
        return items
    }

    data class LanguageItem(
        var name: String = "",
        var selected: Boolean = false,
        var locale: Locale? = null
    )
}