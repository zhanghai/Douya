/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import androidx.recyclerview.widget.RecyclerView;

public class BarrierAdapter extends MergeAdapter {

    private BarrierDataAdapter mDataAdapter;
    private ContentStateAdapter mContentStateAdapter;

    private boolean mHasError;

    public BarrierAdapter(BarrierDataAdapter dataAdapter) {
        super(dataAdapter, new ContentStateAdapter());

        mDataAdapter = dataAdapter;
        RecyclerView.Adapter<?>[] adapters = getAdapters();
        mContentStateAdapter = (ContentStateAdapter) adapters[adapters.length - 1];
        updateContentState();
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
                updateContentState();
            }
        });
    }

    public void setError() {
        mHasError = true;
        updateContentState();
    }

    private void updateContentState() {
        int count = mDataAdapter.getItemCount();
        boolean hasItem = count > 0 && count < mDataAdapter.getTotalItemCount();
        mContentStateAdapter.setHasItem(hasItem);
        if (!hasItem) {
            mContentStateAdapter.setState(ContentStateLayout.STATE_EMPTY);
            mHasError = false;
        } else {
            if (mHasError) {
                mContentStateAdapter.setState(ContentStateLayout.STATE_ERROR);
            } else {
                mContentStateAdapter.setState(ContentStateLayout.STATE_LOADING);
            }
        }
    }
}
