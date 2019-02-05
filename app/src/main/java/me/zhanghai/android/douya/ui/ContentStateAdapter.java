/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.util.ViewUtils;

public class ContentStateAdapter extends RecyclerView.Adapter<ContentStateAdapter.ViewHolder> {

    private boolean mHasItem;

    private int mState = ContentStateLayout.STATE_EMPTY;

    private ViewHolder mViewHolder;

    public ContentStateAdapter() {
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

    public void setState(int state) {
        if (mState == state) {
            return;
        }
        mState = state;
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

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewHolder holder = new ViewHolder(ViewUtils.inflate(R.layout.content_state_item, parent));
        ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
        if (layoutParams instanceof StaggeredGridLayoutManager.LayoutParams) {
            StaggeredGridLayoutManager.LayoutParams staggeredGridLayoutParams =
                    (StaggeredGridLayoutManager.LayoutParams) layoutParams;
            staggeredGridLayoutParams.setFullSpan(true);
        }
        holder.errorText.setText(R.string.load_error);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // HACK: There's a glitch on first frame of progress bar.
        holder.contentStateLayout.setAnimationEnabled(mState == ContentStateLayout.STATE_LOADING);
        holder.contentStateLayout.setState(mState);
        mViewHolder = holder;
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        mViewHolder = null;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.content_state)
        public ContentStateLayout contentStateLayout;
        @BindView(R.id.error)
        public TextView errorText;

        public ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}
