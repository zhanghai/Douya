/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.util.ViewUtils;

public class ProgressAdapter extends RecyclerView.Adapter<ProgressAdapter.ViewHolder> {

    private boolean mHasItem;
    private boolean mProgressVisible = true;

    private ViewHolder mViewHolder;

    public ProgressAdapter() {
        setHasStableIds(true);
    }

    public boolean hasItem() {
        return mHasItem;
    }

    public void setHasItem(boolean hasItem) {

        if (mHasItem == hasItem) {
            return;
        }

        mHasItem = hasItem;
        if (mHasItem) {
            notifyItemInserted(0);
        } else {
            notifyItemRemoved(0);
        }
    }

    public boolean isProgressVisible() {
        return mProgressVisible;
    }

    public void setProgressVisible(boolean progressVisible) {

        if (mProgressVisible == progressVisible) {
            return;
        }

        mProgressVisible = progressVisible;
        if (mHasItem) {
            if (mViewHolder != null) {
                onBindViewHolder(mViewHolder, 0);
            } else {
                notifyItemChanged(0);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mHasItem ? 1 : 0;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder holder = new ViewHolder(ViewUtils.inflate(R.layout.progress_item, parent));
        ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
        if (layoutParams instanceof StaggeredGridLayoutManager.LayoutParams) {
            StaggeredGridLayoutManager.LayoutParams staggeredGridLayoutParams =
                    (StaggeredGridLayoutManager.LayoutParams) layoutParams;
            staggeredGridLayoutParams.setFullSpan(true);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ViewUtils.setVisibleOrInvisible(holder.progress, mProgressVisible);
        mViewHolder = holder;
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        mViewHolder = null;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.progress)
        public ProgressBar progress;

        public ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}
