/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import java.util.List;

public abstract class ClickableSimpleAdapter<T, VH extends RecyclerView.ViewHolder>
        extends SimpleAdapter<T, VH> {

    private OnItemClickListener<T> mOnItemClickListener;

    private RecyclerView mRecyclerView;

    public ClickableSimpleAdapter() {}

    public ClickableSimpleAdapter(List<T> list, OnItemClickListener<T> onItemClickListener) {
        super(list);

        mOnItemClickListener = onItemClickListener;
    }

    public OnItemClickListener<T> getOnItemClickListener() {
        return mOnItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener<T> onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
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
        holder.itemView.setOnClickListener(view -> {
            if (mOnItemClickListener == null) {
                return;
            }
            mOnItemClickListener.onItemClick(mRecyclerView, holder.itemView, item,
                    holder.getAdapterPosition());
        });
    }

    public interface OnItemClickListener<T> {
        void onItemClick(RecyclerView parent, View itemView, T item, int position);
    }
}
