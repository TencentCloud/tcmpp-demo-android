package com.tencent.tcmpp.demo.ui;

import static androidx.appcompat.content.res.AppCompatResources.getDrawable;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tencent.tcmpp.demo.R;
import com.tencent.tcmpp.demo.utils.ScreenUtil;

public class MiniAppItemDecoration extends RecyclerView.ItemDecoration {
    private  final int DOC_HEIGHT = 2;
    private Paint mPaint;
    Drawable mDividingLineDrawable;

    public MiniAppItemDecoration(Context context) {
        mPaint = new Paint();
        mDividingLineDrawable = getDrawable(context, R.mipmap.mini_app_common_item_decor);
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.bottom = DOC_HEIGHT;
    }

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDraw(c, parent, state);
        int count = parent.getChildCount();
        mPaint.setColor(parent.getResources().getColor(R.color.applet_c_828282));
        for (int i = 0; i < count; i++) {
            View view = parent.getChildAt(i);
            int top = view.getBottom();
            int bottom = view.getBottom() + ScreenUtil.dp2px(DOC_HEIGHT, parent.getContext());
            TextView textView = view.findViewById(R.id.tv_mini_app_item_name);
            ImageView more = view.findViewById(R.id.iv_mini_app_more);
            int left = textView.getLeft();
            int right = more.getRight();
            mDividingLineDrawable.setBounds(left , top, right, bottom);
            mDividingLineDrawable.draw(c);
        }
    }
}
