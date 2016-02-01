/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

// The listeners passes ViewHolder and item so that it won't be affected by changing position during
// animation.
public abstract class ClickableSimpleAdapter<T, VH extends RecyclerView.ViewHolder>
        extends SimpleAdapter<T, VH> {

    private OnItemClickListener<T, VH> mOnItemClickListener;
    private OnItemLongClickListener<T, VH> mOnItemLongClickListener;

    private RecyclerView mRecyclerView;

    public ClickableSimpleAdapter() {}

    public ClickableSimpleAdapter(List<T> list, OnItemClickListener<T, VH> onItemClickListener,
                                  OnItemLongClickListener<T, VH> onItemLongClickListener) {
        super(list);

        mOnItemClickListener = onItemClickListener;
        mOnItemLongClickListener = onItemLongClickListener;
    }

    public OnItemClickListener<T, VH> getOnItemClickListener() {
        return mOnItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener<T, VH> onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public OnItemLongClickListener<T, VH> getOnItemLongClickListener() {
        return mOnItemLongClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener<T, VH> onItemLongClickListener) {
        mOnItemLongClickListener = onItemLongClickListener;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        mRecyclerView = recyclerView;
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);

        mRecyclerView = null;
    }

    // Set listener with position in onBindViewHolder() so we are not affected by merge adapters.
    @Override
    public void onBindViewHolder(final VH holder, int position, List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);

        final T item = getItem(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(mRecyclerView, item, holder);
                }
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (mOnItemLongClickListener != null) {
                    return mOnItemLongClickListener.onItemLongClick(mRecyclerView, item, holder);
                }
                return false;
            }
        });
    }

    public interface OnItemClickListener<T, VH> {
        void onItemClick(RecyclerView parent, T item, VH holder);
    }

    public interface OnItemLongClickListener<T, VH> {
        boolean onItemLongClick(RecyclerView parent, T item, VH holder);
    }
}
