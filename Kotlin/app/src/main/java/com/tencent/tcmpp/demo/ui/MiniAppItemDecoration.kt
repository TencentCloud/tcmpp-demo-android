package com.tencent.tcmpp.demo.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.tencent.tcmpp.demo.R
import com.tencent.tcmpp.demo.utils.ScreenUtil

class MiniAppItemDecoration(context: Context) : RecyclerView.ItemDecoration() {
    
    companion object {
        private const val DOC_HEIGHT = 2
    }
    
    private val mPaint = Paint()
    private val mDividingLineDrawable: Drawable?

    init {
        mDividingLineDrawable = AppCompatResources.getDrawable(context, R.mipmap.mini_app_common_item_decor)
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.bottom = DOC_HEIGHT
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        val count = parent.childCount
        mPaint.color = parent.resources.getColor(R.color.applet_c_828282)
        
        for (i in 0 until count) {
            val view = parent.getChildAt(i)
            val top = view.bottom
            val bottom = view.bottom + ScreenUtil.dp2px(DOC_HEIGHT.toFloat(), parent.context)
            val textView = view.findViewById<TextView>(R.id.tv_mini_app_item_name)
            val more = view.findViewById<ImageView>(R.id.iv_mini_app_more)
            val left = textView.left
            val right = more.right
            
            mDividingLineDrawable?.setBounds(left, top, right, bottom)
            mDividingLineDrawable?.draw(c)
        }
    }
}