/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import androidx.recyclerview.widget.RecyclerView;

public abstract class BarrierDataAdapter<VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> {

    private int mBoundItemCount;

    public BarrierDataAdapter() {
        setHasStableIds(true);
    }

    @Override
    public int getItemCount() {
        int count = 0;
        for (int totalCount = getTotalItemCount(); count < totalCount; ++count) {
            if (!isItemLoaded(count)) {
                break;
            }
        }
        return count;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public abstract int getTotalItemCount();

    protected abstract boolean isItemLoaded(int position);

    protected void notifyDataChanged() {
        int newItemCount = getItemCount();
        if (newItemCount <= mBoundItemCount) {
            return;
        }
        notifyItemRangeInserted(mBoundItemCount, newItemCount - mBoundItemCount);
        mBoundItemCount = newItemCount;
    }
}
