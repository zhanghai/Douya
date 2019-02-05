/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import java.util.List;

public abstract class LongClickableSimpleAdapter<T, VH extends RecyclerView.ViewHolder>
        extends ClickableSimpleAdapter<T, VH> {

    private OnItemLongClickListener<T> mOnItemLongClickListener;

    private RecyclerView mRecyclerView;

    public LongClickableSimpleAdapter() {}

    public LongClickableSimpleAdapter(List<T> list, OnItemClickListener<T> onItemClickListener,
                                      OnItemLongClickListener<T> onItemLongClickListener) {
        super(list, onItemClickListener);

        mOnItemLongClickListener = onItemLongClickListener;
    }

    public OnItemLongClickListener<T> getOnItemLongClickListener() {
        return mOnItemLongClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener<T> onItemLongClickListener) {
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

    @Override
    public void onBindViewHolder(final VH holder, int position, List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);

        T item = getItem(position);
        holder.itemView.setOnLongClickListener(view -> {
            if (mOnItemLongClickListener == null) {
                return false;
            }
            return mOnItemLongClickListener.onItemLongClick(mRecyclerView, holder.itemView, item,
                    holder.getAdapterPosition());
        });
    }

    public interface OnItemLongClickListener<T> {
        boolean onItemLongClick(RecyclerView parent, View itemView, T item, int position);
    }
}
