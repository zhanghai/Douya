/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.util.ViewUtils;

public class LoadMoreAdapter extends MergeAdapter {

    private LoadMoreViewAdapter mViewAdapter;

    public LoadMoreAdapter(int loadMoreLayoutRes, RecyclerView.Adapter<?>... adapters) {
        super(mergeAdapters(adapters, new LoadMoreViewAdapter(loadMoreLayoutRes)));

        adapters = getAdapters();
        mViewAdapter = (LoadMoreViewAdapter) adapters[adapters.length - 1];
    }

    private static RecyclerView.Adapter<?>[] mergeAdapters(RecyclerView.Adapter<?>[] adapters,
                                                           RecyclerView.Adapter<?> adapter) {
        RecyclerView.Adapter<?>[] mergedAdapters = new RecyclerView.Adapter<?>[adapters.length + 1];
        System.arraycopy(adapters, 0, mergedAdapters, 0, adapters.length);
        mergedAdapters[adapters.length] = adapter;
        return mergedAdapters;
    }

    public boolean isProgressVisible() {
        return mViewAdapter.isProgressVisible();
    }

    public void setProgressVisible(boolean progressVisible) {
        mViewAdapter.setProgressVisible(progressVisible);
    }

    static class LoadMoreViewAdapter extends RecyclerView.Adapter<LoadMoreViewAdapter.ViewHolder> {

        private int mLoadMoreLayoutRes;

        private ViewHolder mViewHolder;
        private boolean mProgressVisible;

        public LoadMoreViewAdapter(int loadMoreLayoutResId) {

            mLoadMoreLayoutRes = loadMoreLayoutResId;

            setHasStableIds(true);
        }

        public boolean isProgressVisible() {
            return mProgressVisible;
        }

        public void setProgressVisible(boolean progressVisible) {
            if (mProgressVisible != progressVisible) {
                mProgressVisible = progressVisible;
                if (mViewHolder != null) {
                    onBindViewHolder(mViewHolder, 0);
                } else {
                    notifyItemChanged(0);
                }
            }
        }

        @Override
        public int getItemCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ViewHolder holder = new ViewHolder(ViewUtils.inflate(mLoadMoreLayoutRes, parent));
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

            @Bind(R.id.progress)
            public ProgressBar progress;

            public ViewHolder(View itemView) {
                super(itemView);

                ButterKnife.bind(this, itemView);
            }
        }
    }
}
