package com.tencent.tcmpp.demo.pay;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatEditText;

public class PwdEditText extends AppCompatEditText {

    /**
     * 密码长度
     */
    private final int PWD_LENGTH = 6;
    /**
     * 密码框
     */
    private final Rect mRect = new Rect();

    /**
     * 密码画笔
     */
    private final Paint mPwdPaint;

    /**
     * 密码框画笔
     */
    private final Paint mRectPaint;

    private final Paint mWhitePaint;
    /**
     * 输入的密码长度
     */
    private int mInputLength;

    /**
     * 输入结束监听
     */
    private OnInputFinishListener mOnInputFinishListener;


    public PwdEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

        mPwdPaint = new Paint();
        mPwdPaint.setColor(Color.BLACK);
        mPwdPaint.setStyle(Paint.Style.FILL);
        mPwdPaint.setAntiAlias(true);

        mRectPaint = new Paint();
        mRectPaint.setStyle(Paint.Style.STROKE);
        mRectPaint.setColor(Color.LTGRAY);
        mRectPaint.setAntiAlias(true);

        mWhitePaint = new Paint();
        mWhitePaint.setColor(Color.WHITE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int mWidth = getWidth();
        int mHeight = getHeight();

        canvas.drawRect(0, 0, mWidth, mHeight, mWhitePaint);

        int rectWidth = (mWidth) / PWD_LENGTH;

        for (int i = 0; i < PWD_LENGTH; i++) {
            int left = rectWidth * i;
            int top = 2;
            int right = left + rectWidth;
            int bottom = mHeight - top;
            mRect.set(left, top, right, bottom);
            canvas.drawRect(mRect, mRectPaint);
        }

        for (int i = 0; i < mInputLength; i++) {
            int cx = rectWidth / 2 + rectWidth * i;
            int cy = mHeight / 2;
            int PWD_SIZE = 5;
            canvas.drawCircle(cx, cy, PWD_SIZE, mPwdPaint);
        }
    }

    @Override
    protected void onTextChanged(CharSequence text, int start,
            int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        this.mInputLength = text.toString().length();
        invalidate();
        if (mInputLength == PWD_LENGTH && mOnInputFinishListener != null) {
            mOnInputFinishListener.onInputFinish(text.toString());
        }
    }

    public interface OnInputFinishListener {
        void onInputFinish(String password);
    }

    public void setOnInputFinishListener(
            OnInputFinishListener onInputFinishListener) {
        this.mOnInputFinishListener = onInputFinishListener;
    }

}