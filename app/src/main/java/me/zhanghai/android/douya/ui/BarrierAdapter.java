/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import android.support.v7.widget.RecyclerView;

public class BarrierAdapter extends MergeAdapter {

    private BarrierDataAdapter mDataAdapter;
    private ProgressAdapter mProgressAdapter;

    public BarrierAdapter(BarrierDataAdapter dataAdapter) {
        super(dataAdapter, new ProgressAdapter());

        mDataAdapter = dataAdapter;
        RecyclerView.Adapter<?>[] adapters = getAdapters();
        mProgressAdapter = (ProgressAdapter) adapters[adapters.length - 1];
        updateHasProgressItem();
        registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                onChanged();
            }
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                onChanged();
            }
            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                onChanged();
            }
            @Override
            public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                onChanged();
            }
            @Override
            public void onChanged() {
                updateHasProgressItem();
            }
        });
    }

    private void updateHasProgressItem() {
        int count = mDataAdapter.getItemCount();
        mProgressAdapter.setHasItem(count > 0 && count < mDataAdapter.getTotalItemCount());
    }
}
