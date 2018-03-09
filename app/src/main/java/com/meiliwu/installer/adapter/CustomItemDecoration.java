package com.meiliwu.installer.adapter;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.meiliwu.installer.utils.DisplayUtil;

/**
 * Author:  ZhangTao
 * Date: 2018/3/9.
 */

public class CustomItemDecoration extends RecyclerView.ItemDecoration {
    private int left;
    private int top;
    private int right;
    private int bottom;

    public CustomItemDecoration(int left, int top, int right, int bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        //index为偶数的
        if (parent.getChildLayoutPosition(view) % 2 == 0) {
            outRect.left = 0;
            outRect.right = 0;
            outRect.top = 0;
        } else if (parent.getChildLayoutPosition(view) % 2 == 1) {
            outRect.left = DisplayUtil.dip2px(parent.getContext(), left);
            outRect.right = 0;
            outRect.top = 0;
        }
        outRect.bottom = DisplayUtil.dip2px(parent.getContext(), bottom);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
    }

}
