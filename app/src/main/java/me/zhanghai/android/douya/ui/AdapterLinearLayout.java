/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import android.content.Context;
import android.os.Build;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

// This does not support view type, bind payload, getAdapterPosition(), etc.
public class AdapterLinearLayout extends LinearLayout {

    protected RecyclerView.Adapter mAdapter;

    private final RecyclerView.AdapterDataObserver mObserver =
            new RecyclerView.AdapterDataObserver() {
                @Override
                public void onChanged() {
                    onDataSetChanged();
                }
                @Override
                public void onItemRangeChanged(int positionStart, int itemCount) {
                    AdapterLinearLayout.this.onItemRangeChanged(positionStart, itemCount);
                }
                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {
                    AdapterLinearLayout.this.onItemRangeInserted(positionStart, itemCount);
                }
                @Override
                public void onItemRangeRemoved(int positionStart, int itemCount) {
                    AdapterLinearLayout.this.onItemRangeRemoved(positionStart, itemCount);
                }
                @Override
                public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                    AdapterLinearLayout.this.onItemRangeMoved(fromPosition, toPosition, itemCount);
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
        if (mAdapter == null) {
            return;
        }
        removeAllViews();
        for (int position = 0, count = mAdapter.getItemCount(); position < count; ++position) {
            addItemView(position);
        }
    }

    protected void onItemRangeChanged(int positionStart, int itemCount) {
        for (int position = positionStart, positionEnd = positionStart + itemCount;
             position < positionEnd; ++position) {
            updateItemView(position);
        }
    }

    protected void onItemRangeInserted(int positionStart, int itemCount) {
        for (int position = positionStart, positionEnd = positionStart + itemCount;
             position < positionEnd; ++position) {
            addItemView(position);
        }
    }

    protected void onItemRangeRemoved(int positionStart, int itemCount) {
        for (int i = 0; i < itemCount; ++i) {
            removeViewAt(positionStart);
        }
    }

    protected void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
        View[] itemViews = new View[itemCount];
        for (int i = 0; i < itemCount; ++i) {
            itemViews[i] = getChildAt(fromPosition);
            removeViewAt(fromPosition);
        }
        for (int i = 0, position = toPosition; i < itemCount; ++i, ++position) {
            addView(itemViews[i], position);
        }
    }

    private void addItemView(int position) {
        int viewType = mAdapter.getItemViewType(position);
        RecyclerView.ViewHolder holder = mAdapter.createViewHolder(this, viewType);
        //noinspection unchecked
        mAdapter.bindViewHolder(holder, position);
        View itemView = holder.itemView;
        itemView.setTag(holder);
        addView(itemView, position);
    }

    private void updateItemView(int position) {
        View itemView = getChildAt(position);
        RecyclerView.ViewHolder holder = (RecyclerView.ViewHolder) itemView.getTag();
        //noinspection unchecked
        mAdapter.bindViewHolder(holder, position);
    }
}
