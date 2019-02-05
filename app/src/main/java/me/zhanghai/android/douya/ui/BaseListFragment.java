/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.List;

import androidx.swiperefreshlayout.widget.FriendlySwipeRefreshLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.content.MoreListResourceFragment;
import me.zhanghai.android.douya.network.api.ApiError;
import me.zhanghai.android.douya.util.LogUtils;
import me.zhanghai.android.douya.util.ToastUtils;
import me.zhanghai.android.douya.util.ViewUtils;

public abstract class BaseListFragment<T> extends Fragment {

    @BindView(R.id.swipe_refresh)
    protected FriendlySwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.list)
    protected RecyclerView mList;
    @BindView(R.id.progress)
    protected ProgressBar mProgress;

    protected SimpleAdapter<T, ?> mItemAdapter;
    protected LoadMoreAdapter mAdapter;

    protected MoreListResourceFragment<?, List<T>> mResource;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.base_list_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mResource = onAttachResource();

        mSwipeRefreshLayout.setOnRefreshListener(this::onSwipeRefresh);

        // TODO: OK?
        //mList.setHasFixedSize(true);
        mList.setItemAnimator(new NoChangeAnimationItemAnimator());
        mList.setLayoutManager(onCreateLayoutManager());
        mItemAdapter = onCreateAdapter();
        if (mResource.has()) {
            mItemAdapter.replace(mResource.get());
        }
        mAdapter = new LoadMoreAdapter(mItemAdapter);
        mList.setAdapter(mAdapter);
        onAttachScrollListener();

        updateRefreshing();
    }

    protected RecyclerView.LayoutManager onCreateLayoutManager() {
        return new LinearLayoutManager(getActivity());
    }

    abstract protected SimpleAdapter<T, ?> onCreateAdapter();

    protected void onAttachScrollListener() {
        mList.addOnScrollListener(new OnVerticalScrollListener() {
            @Override
            public void onScrolledToBottom() {
                mResource.load(true);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mResource.detach();
    }

    protected abstract MoreListResourceFragment<?, List<T>> onAttachResource();

    protected void onLoadListStarted() {
        updateRefreshing();
    }

    protected void onLoadListFinished() {
        updateRefreshing();
    }

    protected void onLoadListError(ApiError error) {
        LogUtils.e(error.toString());
        Activity activity = getActivity();
        ToastUtils.show(ApiError.getErrorString(error, activity), activity);
    }

    protected void onListChanged(List<T> newList) {
        mItemAdapter.replace(newList);
        //noinspection unchecked
        onListUpdated(mResource.get());
    }

    protected void onListAppended(List<T> appendedList) {
        mItemAdapter.addAll(appendedList);
        //noinspection unchecked
        onListUpdated(mResource.get());
    }

    protected void onItemChanged(int position, T newItem) {
        mItemAdapter.set(position, newItem);
        //noinspection unchecked
        onListUpdated(mResource.get());
    }

    protected void onItemInserted(int position, T insertedItem) {
        mItemAdapter.add(position, insertedItem);
        //noinspection unchecked
        onListUpdated(mResource.get());
    }

    protected void onItemRemoved(int position) {
        mItemAdapter.remove(position);
        //noinspection unchecked
        onListUpdated(mResource.get());
    }

    protected void onListUpdated(List<T> list) {}

    protected void onItemWriteFinished(int position) {
        mItemAdapter.notifyItemChanged(position);
    }

    protected void onItemWriteStarted(int position) {
        mItemAdapter.notifyItemChanged(position);
    }

    private void updateRefreshing() {
        boolean loading = mResource.isLoading();
        boolean empty = mResource.isEmpty();
        boolean loadingMore = mResource.isLoadingMore();
        mSwipeRefreshLayout.setRefreshing(loading && (mSwipeRefreshLayout.isRefreshing() || !empty)
                && !loadingMore);
        ViewUtils.setVisibleOrGone(mProgress, loading && empty);
        mAdapter.setLoading(loading && !empty && loadingMore);
    }

    protected void onSwipeRefresh() {
        mResource.load(false);
    }
}
