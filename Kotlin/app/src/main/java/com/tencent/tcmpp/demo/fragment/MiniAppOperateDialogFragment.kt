package com.tencent.tcmpp.demo.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.tencent.tcmpp.demo.R
import com.tencent.tcmpp.demo.activity.MainContentActivity
import com.tencent.tcmpp.demo.utils.ScreenUtil
import com.tencent.tmf.mini.api.TmfMiniSDK
import com.tencent.tmf.mini.api.bean.MiniApp

class MiniAppOperateDialogFragment(private val mMiniApp: MiniApp) : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mini_app_operate, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewListener(view)
    }

    override fun onStart() {
        super.onStart()
        setUpWindowLocation()
    }

    private fun setViewListener(view: View) {
        view.findViewById<View>(R.id.tv_mini_operate_preload).setOnClickListener {
            TmfMiniSDK.preloadMiniApp(context, Bundle())
            dismissAllowingStateLoss()
            (activity as? MainContentActivity)?.showToast(getString(R.string.applet_pre_load_success))
        }
        
        view.findViewById<View>(R.id.tv_mini_operate_clear_cache).setOnClickListener {
            TmfMiniSDK.deleteMiniApp(mMiniApp.appId)
            dismissAllowingStateLoss()
            (activity as? MainContentActivity)?.showToast(getString(R.string.applet_clear_cache_success))
        }
        
        view.findViewById<View>(R.id.tv_mini_operate_reset).setOnClickListener {
            TmfMiniSDK.stopMiniApp(context, mMiniApp.appId)
            dismissAllowingStateLoss()
            (activity as? MainContentActivity)?.showToast(getString(R.string.applet_reset_load_success))
        }

        view.findViewById<View>(R.id.tv_mini_operate_cancel).setOnClickListener {
            dismissAllowingStateLoss()
        }
    }

    private fun setUpWindowLocation() {
        val params = dialog?.window?.attributes
        params?.let {
            it.gravity = Gravity.BOTTOM or Gravity.END
            it.width = ScreenUtil.getScreenWidth(requireContext())
            dialog?.window?.decorView?.setBackgroundColor(Color.TRANSPARENT)
            dialog?.window?.attributes = it
        }
    }
}