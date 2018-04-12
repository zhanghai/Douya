/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class AdapterLinearLayout extends LinearLayout {

    protected RecyclerView.Adapter mAdapter;

    private final RecyclerView.AdapterDataObserver mObserver =
            new RecyclerView.AdapterDataObserver() {
                @Override
                public void onChanged() {
                    onDataSetChanged();
                }
            };

    public AdapterLinearLayout(Context context) {
        super(context);
    }

    public AdapterLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AdapterLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public AdapterLinearLayout(Context context, AttributeSet attrs, int defStyleAttr,
                               int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public RecyclerView.Adapter getAdapter() {
        return mAdapter;
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        if (mAdapter != null) {
            mAdapter.unregisterAdapterDataObserver(mObserver);
        }
        mAdapter = adapter;
        if (mAdapter != null) {
            adapter.registerAdapterDataObserver(mObserver);
        }
        onDataSetChanged();
    }

    protected void onDataSetChanged() {
        removeAllViews();
        if (mAdapter == null) {
            return;
        }
        for (int position = 0, count = mAdapter.getItemCount(); position < count; ++position) {
            int viewType = mAdapter.getItemViewType(position);
            RecyclerView.ViewHolder holder = mAdapter.createViewHolder(this, viewType);
            //noinspection unchecked
            mAdapter.bindViewHolder(holder, position);
            addView(holder.itemView);
        }
    }
}
