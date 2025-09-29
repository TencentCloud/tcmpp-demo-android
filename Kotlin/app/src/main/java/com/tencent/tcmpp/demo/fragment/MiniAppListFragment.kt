package com.tencent.tcmpp.demo.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.ResultReceiver
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tencent.tcmpp.demo.R
import com.tencent.tcmpp.demo.bean.ItemBean
import com.tencent.tcmpp.demo.ui.MiniAppItemDecoration
import com.tencent.tcmpp.demo.utils.GlobalConfigureUtil
import com.tencent.tcmpp.demo.utils.MiniAppCategoryHelper
import com.tencent.tmf.mini.api.TmfMiniSDK
import com.tencent.tmf.mini.api.bean.MiniApp
import com.tencent.tmf.mini.api.bean.MiniCode
import com.tencent.tmf.mini.api.bean.MiniScene
import com.tencent.tmf.mini.api.bean.MiniStartOptions
import com.tencent.tmf.mini.api.bean.SearchOptions
import com.tencent.tmfmini.sdk.core.utils.GsonUtils

class MiniAppListFragment : Fragment() {
    
    companion object {
        private const val TAG = "MiniAppListFragment"
        const val TYPE_RECENT = "recent"
        const val TYPE_MY = "my"
        
        fun newInstance(type: String): MiniAppListFragment {
            val args = Bundle()
            val fragment = MiniAppListFragment()
            args.putString("type", type)
            fragment.arguments = args
            return fragment
        }
    }
    
    private var type: String? = null
    private val mItemList = ArrayList<ItemBean>()
    private lateinit var miniAppRecyclerViewAdapter: MiniAppRecyclerViewAdapter
    
    private val mResultReceiver = object : ResultReceiver(Handler(Looper.getMainLooper())) {
        override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {
            if (resultCode != MiniCode.CODE_OK) {
                // mini program startup error
                val errMsg = resultData?.getString(MiniCode.KEY_ERR_MSG)
                Toast.makeText(context, "$errMsg$resultCode", Toast.LENGTH_SHORT).show()
            } else {
                refreshData()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        arguments?.let {
            type = it.getString("type")
        }
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mini_app_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mRvMiniAppListView = view.findViewById<RecyclerView>(R.id.rv_my_mini_list)
        miniAppRecyclerViewAdapter = MiniAppRecyclerViewAdapter()
        val layoutManager = LinearLayoutManager(activity)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        mRvMiniAppListView.addItemDecoration(MiniAppItemDecoration(requireActivity()))
        mRvMiniAppListView.layoutManager = layoutManager
        mRvMiniAppListView.adapter = miniAppRecyclerViewAdapter
    }

    override fun onResume() {
        super.onResume()
        refreshData()
    }

    private fun refreshData() {
        mItemList.clear()
        when (type) {
            TYPE_RECENT -> loadRecentList()
            TYPE_MY -> loadAllMiniAppList()
        }
    }

    private fun loadRecentList() {
        TmfMiniSDK.getRecentList { recentList ->
            if (!recentList.isNullOrEmpty()) {
                for (app in recentList) {
                    mItemList.add(ItemBean(0, app.name, app))
                }
                Log.e(TAG, "miniLit recent ${GsonUtils.toJson(recentList)}")
            }
            miniAppRecyclerViewAdapter.notifyDataSetChanged()
        }
    }

    private fun loadAllMiniAppList() {
        val searchOptions = SearchOptions("")
        TmfMiniSDK.searchMiniApp(searchOptions) { code, msg, data ->
            val list = ArrayList<ItemBean>()
            if (code == MiniCode.CODE_OK && data != null) {
                for (app in data) {
                    list.add(ItemBean(0, app.name, app))
                }
            } else {
                Toast.makeText(context, "$msg$code", Toast.LENGTH_SHORT).show()
            }
            Log.e(TAG, "miniLit${GsonUtils.toJson(list)}")
            setItemBeans(list)
        }
    }

    private fun setItemBeans(beans: List<ItemBean>?) {
        if (beans == null) {
            mItemList.clear()
        } else {
            mItemList.clear()
            mItemList.addAll(beans)
        }
        miniAppRecyclerViewAdapter.notifyDataSetChanged()
    }

    private fun startMiniApp(app: MiniApp) {
        val miniStartOptions = MiniStartOptions().apply {
            if (GlobalConfigureUtil.getGlobalConfig(requireContext()).mockApi) {
                params = "noServer=1"
            }
            resultReceiver = mResultReceiver
        }
        TmfMiniSDK.startMiniApp(
            activity,
            app.appId,
            MiniScene.LAUNCH_SCENE_MAIN_ENTRY,
            app.appVerType,
            miniStartOptions
        )
    }

    private fun showMiniAppOperatePanel(app: MiniApp) {
        val miniAppOperateDialogFragment = MiniAppOperateDialogFragment(app)
        miniAppOperateDialogFragment.show(childFragmentManager, "dialog_of_mini_app")
    }

    private inner class MiniAppRecyclerViewAdapter : 
        RecyclerView.Adapter<MiniAppRecyclerViewAdapter.MiniAppViewHolder>() {

        override fun getItemViewType(position: Int): Int {
            val bean = mItemList[position]
            return bean.mType
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MiniAppViewHolder {
            val layoutId = R.layout.mini_app_list_item_common
            val itemView = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
            return MiniAppViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: MiniAppViewHolder, position: Int) {
            val itemBean = mItemList[position]
            holder.bindView(itemBean)
        }

        override fun getItemCount(): Int {
            return mItemList.size
        }

        inner class MiniAppViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val img: ImageView = itemView.findViewById(R.id.iv_mini_app_item_icon)
            private val miniAppName: TextView = itemView.findViewById(R.id.tv_mini_app_item_name)
            private val miniAppDesc: TextView = itemView.findViewById(R.id.tv_mini_app_item_dec)
            private val miniAppCategory: TextView = itemView.findViewById(R.id.tv_mini_app_item_category)
            private val imageViewMore: ImageView = itemView.findViewById(R.id.iv_mini_app_more)

            fun bindView(bean: ItemBean) {
                Glide.with(itemView).load(bean.mAppInfo.iconUrl).placeholder(R.mipmap.ic_launcher).into(img)
                miniAppName.text = bean.mAppInfo.name
                miniAppDesc.text = bean.mAppInfo.appIntro
                
                // get category info from mini appInfo
                val categories = MiniAppCategoryHelper.getCategoryFromString(bean.mAppInfo.appCategory)
                if (categories.isNotEmpty()) {
                    miniAppCategory.text = categories[0].secondLevelCategory
                } else {
                    miniAppCategory.text = bean.mAppInfo.appCategory
                }
                
                itemView.setOnClickListener { startMiniApp(bean.mAppInfo) }
                imageViewMore.setOnClickListener { showMiniAppOperatePanel(bean.mAppInfo) }
            }
        }
    }
}