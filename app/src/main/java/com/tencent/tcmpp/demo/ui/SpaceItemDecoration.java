package com.tencent.tcmpp.demo.ui;

import android.content.res.Resources;
import android.graphics.Rect;
import android.util.TypedValue;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by robincxiao on 2018/4/24.
 */

public class SpaceItemDecoration extends RecyclerView.ItemDecoration {
    private int column;
    private int left, right, top, bottom;


    public SpaceItemDecoration(int left, int right, int top, int bottom, int column) {
        this.left = dpToPx(left);
        this.right = dpToPx(right);
        this.top = dpToPx(top);
        this.bottom = dpToPx(bottom);
        this.column = column;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if(left > 0){
            outRect.left = left;
        }
        if(right > 0){
            outRect.right = right;
        }
        if(top > 0){
            outRect.top = top;
        }
        if(bottom > 0){
            outRect.bottom = bottom;
        }
    }

    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, Resources.getSystem().getDisplayMetrics());
    }

    boolean isFirstRow(int pos) {
        return pos < column;
    }

    boolean isLastRow(int pos, int total) {
        return total - pos <= column;
    }

    boolean isFirstColumn(int pos) {
        return pos % column == 0;
    }

    boolean isSecondColumn(int pos) {
        return isFirstColumn(pos - 1);
    }

    boolean isEndColumn(int pos) {
        return isFirstColumn(pos + 1);
    }

    boolean isNearEndColumn(int pos) {
        return isEndColumn(pos + 1);
    }

}
