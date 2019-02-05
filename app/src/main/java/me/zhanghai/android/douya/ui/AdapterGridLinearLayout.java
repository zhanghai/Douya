/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import me.zhanghai.android.douya.util.ViewUtils;

public class AdapterGridLinearLayout extends AdapterLinearLayout {

    private int mColumnCount = 1;
    private Drawable mHorizontalDivider;

    public AdapterGridLinearLayout(Context context) {
        super(context);
    }

    public AdapterGridLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AdapterGridLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public AdapterGridLinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public int getColumnCount() {
        return mColumnCount;
    }

    public void setColumnCount(int columnCount) {
        if (mColumnCount != columnCount) {
            mColumnCount = columnCount;
            onDataSetChanged();
        }
    }

    public void setHorizontalDivider(int dividerRes) {
        mHorizontalDivider = AppCompatResources.getDrawable(getContext(), dividerRes);
    }

    @Override
    protected void onDataSetChanged() {
        if (mAdapter == null) {
            return;
        }
        removeAllViews();
        for (int rowPosition = 0, count = mAdapter.getItemCount(); rowPosition < count;
             rowPosition += mColumnCount) {
            LinearLayout rowLayout = new LinearLayout(getContext());
            if (mHorizontalDivider != null) {
                rowLayout.setDividerDrawable(mHorizontalDivider);
                rowLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
            }
            for (int position = rowPosition, nextRowPosition = rowPosition + mColumnCount;
                 position < nextRowPosition && position < count; ++position) {
                int viewType = mAdapter.getItemViewType(position);
                RecyclerView.ViewHolder holder = mAdapter.createViewHolder(rowLayout, viewType);
                //noinspection unchecked
                mAdapter.bindViewHolder(holder, position);
                ViewUtils.setWeight(holder.itemView, 1);
                rowLayout.addView(holder.itemView);
            }
            LayoutParams rowLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT);
            addView(rowLayout, rowLayoutParams);
        }
    }

    @Override
    protected void onItemRangeChanged(int positionStart, int itemCount) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void onItemRangeInserted(int positionStart, int itemCount) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void onItemRangeRemoved(int positionStart, int itemCount) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
        throw new UnsupportedOperationException();
    }
}
