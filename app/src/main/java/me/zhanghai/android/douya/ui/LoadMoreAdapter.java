/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import androidx.recyclerview.widget.RecyclerView;

public class LoadMoreAdapter extends MergeAdapter {

    private ContentStateAdapter mContentStateAdapter;

    public LoadMoreAdapter(RecyclerView.Adapter<?>... dataAdapters) {
        super(appendAdapter(dataAdapters, new ContentStateAdapter()));

        RecyclerView.Adapter<?>[] adapters = getAdapters();
        mContentStateAdapter = (ContentStateAdapter) adapters[adapters.length - 1];
        updateHasContentStateItem();
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
                updateHasContentStateItem();
            }
        });
    }

    private static RecyclerView.Adapter<?>[] appendAdapter(RecyclerView.Adapter<?>[] adapters,
                                                           RecyclerView.Adapter<?> adapter) {
        RecyclerView.Adapter<?>[] mergedAdapters = new RecyclerView.Adapter<?>[adapters.length + 1];
        System.arraycopy(adapters, 0, mergedAdapters, 0, adapters.length);
        mergedAdapters[adapters.length] = adapter;
        return mergedAdapters;
    }

    private void updateHasContentStateItem() {
        int count = getItemCount() - mContentStateAdapter.getItemCount();
        mContentStateAdapter.setHasItem(count > 0);
    }

    public void setLoading(boolean loading) {
        mContentStateAdapter.setState(loading ? ContentStateLayout.STATE_LOADING
                : ContentStateLayout.STATE_EMPTY);
    }
}
