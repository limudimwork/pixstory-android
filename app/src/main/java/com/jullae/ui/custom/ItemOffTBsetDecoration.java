package com.jullae.ui.custom;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by master on 9/3/18.
 */

public class ItemOffTBsetDecoration extends RecyclerView.ItemDecoration {

    private int mItemOffset;

    public ItemOffTBsetDecoration(int itemOffset) {
        mItemOffset = itemOffset;
    }

    public ItemOffTBsetDecoration(@NonNull Context context, @DimenRes int itemOffsetId) {
        this(context.getResources().getDimensionPixelSize(itemOffsetId));
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if (parent.getChildAdapterPosition(view) != parent.getAdapter().getItemCount() - 1 && parent.getChildAdapterPosition(view) != 0) {
            outRect.set(0, mItemOffset, 0, mItemOffset);
        } else if (parent.getChildAdapterPosition(view) == parent.getAdapter().getItemCount() - 1) {

            outRect.set(0, mItemOffset, 0, 0);

        } else {
            outRect.set(0, 0, 0, mItemOffset);

        }
    }
}