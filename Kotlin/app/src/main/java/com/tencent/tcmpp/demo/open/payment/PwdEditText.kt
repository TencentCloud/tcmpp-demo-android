package com.tencent.tcmpp.demo.open.payment

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText

class PwdEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatEditText(context, attrs, defStyleAttr) {

    companion object {
        private const val PWD_LENGTH = 6
        private const val PWD_SIZE = 5
    }

    private val mRect = Rect()
    private val mPwdPaint: Paint
    private val mRectPaint: Paint
    private val mWhitePaint: Paint
    private var mInputLength = 0
    private var mOnInputFinishListener: OnInputFinishListener? = null

    init {
        mPwdPaint = Paint().apply {
            color = Color.BLACK
            style = Paint.Style.FILL
            isAntiAlias = true
        }

        mRectPaint = Paint().apply {
            style = Paint.Style.STROKE
            color = Color.LTGRAY
            isAntiAlias = true
        }

        mWhitePaint = Paint().apply {
            color = Color.WHITE
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val mWidth = width
        val mHeight = height

        canvas.drawRect(0f, 0f, mWidth.toFloat(), mHeight.toFloat(), mWhitePaint)

        val rectWidth = mWidth / PWD_LENGTH

        for (i in 0 until PWD_LENGTH) {
            val left = rectWidth * i
            val top = 2
            val right = left + rectWidth
            val bottom = mHeight - top
            mRect.set(left, top, right, bottom)
            canvas.drawRect(mRect, mRectPaint)
        }

        for (i in 0 until mInputLength) {
            val cx = (rectWidth / 2 + rectWidth * i).toFloat()
            val cy = (mHeight / 2).toFloat()
            canvas.drawCircle(cx, cy, PWD_SIZE.toFloat(), mPwdPaint)
        }
    }

    override fun onTextChanged(
        text: CharSequence?,
        start: Int,
        lengthBefore: Int,
        lengthAfter: Int
    ) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)
        mInputLength = text?.length ?: 0
        invalidate()
        if (mInputLength == PWD_LENGTH && mOnInputFinishListener != null) {
            mOnInputFinishListener?.onInputFinish(text.toString())
        }
    }

    fun setOnInputFinishListener(onInputFinishListener: OnInputFinishListener) {
        this.mOnInputFinishListener = onInputFinishListener
    }

    interface OnInputFinishListener {
        fun onInputFinish(password: String)
    }
}