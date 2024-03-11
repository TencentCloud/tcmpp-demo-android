package com.tencent.tcmpp.demo.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatImageView;
import com.tencent.tcmpp.demo.R;

public class RoundImageView extends AppCompatImageView {

    private RectF mRect;
    private Path mPath;
    private float mRadius;

    public RoundImageView(Context context) {
        this(context, null);
    }

    public RoundImageView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public RoundImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getAttributes(context, attrs);
        initView(context);
    }

    private void getAttributes(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RoundImageView);
        mRadius = ta.getDimension(R.styleable.RoundImageView_radius, -1);
        ta.recycle();
    }

    private void initView(Context context) {
        mRect = new RectF();
        mPath = new Path();
        setLayerType(LAYER_TYPE_SOFTWARE, null);        // 禁用硬件加速
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mRadius < 0) {
            clipCircle(w, h);
        } else {
            clipRoundRect(w, h);
        }
    }

    private void clipRoundRect(int width, int height) {
        mRect.left = 0;
        mRect.top = 0;
        mRect.right = width;
        mRect.bottom = height;
        mPath.addRoundRect(mRect, mRadius, mRadius, Path.Direction.CW);
    }

    private void clipCircle(int width, int height) {
        int radius = Math.min(width, height)/2;
        mPath.addCircle(width/2, height/2, radius, Path.Direction.CW);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.clipPath(mPath);
        super.onDraw(canvas);
    }
}