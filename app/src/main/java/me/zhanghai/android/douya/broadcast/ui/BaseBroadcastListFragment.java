/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.broadcast.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.FriendlySwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.customtabshelper.CustomTabsHelperFragment;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.broadcast.content.BroadcastListResource;
import me.zhanghai.android.douya.broadcast.content.LikeBroadcastManager;
import me.zhanghai.android.douya.broadcast.content.RebroadcastBroadcastManager;
import me.zhanghai.android.douya.link.NotImplementedManager;
import me.zhanghai.android.douya.network.api.ApiError;
import me.zhanghai.android.douya.network.api.info.apiv2.Broadcast;
import me.zhanghai.android.douya.ui.AppBarHost;
import me.zhanghai.android.douya.ui.DoubleClickToolBar;
import me.zhanghai.android.douya.ui.FastSmoothScrollStaggeredGridLayoutManager;
import me.zhanghai.android.douya.ui.FriendlyFloatingActionButton;
import me.zhanghai.android.douya.ui.LoadMoreAdapter;
import me.zhanghai.android.douya.ui.NoChangeAnimationItemAnimator;
import me.zhanghai.android.douya.ui.OnVerticalScrollWithPagingTouchSlopListener;
import me.zhanghai.android.douya.util.CardUtils;
import me.zhanghai.android.douya.util.CheatSheetUtils;
import me.zhanghai.android.douya.util.LogUtils;
import me.zhanghai.android.douya.util.RecyclerViewUtils;
import me.zhanghai.android.douya.util.ToastUtils;
import me.zhanghai.android.douya.util.TransitionUtils;
import me.zhanghai.android.douya.util.ViewUtils;

public abstract class BaseBroadcastListFragment extends Fragment
        implements BroadcastListResource.Listener, BroadcastAdapter.Listener {

    @BindView(R.id.swipe_refresh)
    FriendlySwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.broadcast_list)
    RecyclerView mBroadcastList;
    @BindView(R.id.progress)
    ProgressBar mProgress;
    @BindView(R.id.send)
    FriendlyFloatingActionButton mSendFab;

    private BroadcastListResource mBroadcastListResource;

    private BroadcastAdapter mBroadcastAdapter;
    private LoadMoreAdapter mAdapter;

    protected BaseBroadcastListFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.broadcast_list_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        CustomTabsHelperFragment.attachTo(this);
        mBroadcastListResource = onAttachBroadcastListResource();

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onSwipeRefresh();
            }
        });

        mBroadcastList.setHasFixedSize(true);
        mBroadcastList.setItemAnimator(new NoChangeAnimationItemAnimator());
        // Always use StaggeredGridLayoutManager so that instance state can be saved.
        Activity activity = getActivity();
        int columnCount = CardUtils.getColumnCount(activity);
        mBroadcastList.setLayoutManager(new FastSmoothScrollStaggeredGridLayoutManager(columnCount,
                StaggeredGridLayoutManager.VERTICAL));
        mBroadcastAdapter = new BroadcastAdapter(mBroadcastListResource.get(), this);
        mAdapter = new LoadMoreAdapter(R.layout.load_more_card_item, mBroadcastAdapter);
        mBroadcastList.setAdapter(mAdapter);
        final AppBarHost appBarHost = (AppBarHost) getParentFragment();
        mBroadcastList.addOnScrollListener(
                new OnVerticalScrollWithPagingTouchSlopListener(activity) {
                    @Override
                    public void onScrolled(int dy) {
                        if (!RecyclerViewUtils.hasFirstChildReachedTop(mBroadcastList)) {
                            onShow();
                        }
                    }
                    @Override
                    public void onScrolledUp() {
                        onShow();
                    }
                    private void onShow() {
                        appBarHost.showAppBar();
                        mSendFab.show();
                    }
                    @Override
                    public void onScrolledDown() {
                        if (RecyclerViewUtils.hasFirstChildReachedTop(mBroadcastList)) {
                            appBarHost.hideAppBar();
                            mSendFab.hide();
                        }
                    }
                    @Override
                    public void onScrolledToBottom() {
                        mBroadcastListResource.load(true);
                    }
                });
        appBarHost.setToolBarOnDoubleClickListener(new DoubleClickToolBar.OnDoubleClickListener() {
            @Override
            public boolean onDoubleClick(View view) {
                mBroadcastList.smoothScrollToPosition(0);
                return true;
            }
        });

        updateRefreshing();

        CheatSheetUtils.setup(mSendFab);
        mSendFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSendBroadcast();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mBroadcastListResource.detach();
    }

    @Override
    public void onLoadBroadcastListStarted(int requestCode) {
        updateRefreshing();
    }

    @Override
    public void onLoadBroadcastListFinished(int requestCode) {
        updateRefreshing();
    }

    @Override
    public void onLoadBroadcastListError(int requestCode, ApiError error) {
        LogUtils.e(error.toString());
        Activity activity = getActivity();
        ToastUtils.show(ApiError.getErrorString(error, activity), activity);
    }

    @Override
    public void onBroadcastListChanged(int requestCode, List<Broadcast> newBroadcastList) {
        mBroadcastAdapter.replace(newBroadcastList);
    }

    @Override
    public void onBroadcastListAppended(int requestCode, List<Broadcast> appendedBroadcastList) {
        mBroadcastAdapter.addAll(appendedBroadcastList);
    }

    @Override
    public void onBroadcastChanged(int requestCode, int position, Broadcast newBroadcast) {
        mBroadcastAdapter.set(position, newBroadcast);
    }

    @Override
    public void onBroadcastRemoved(int requestCode, int position) {
        mBroadcastAdapter.remove(position);
    }

    @Override
    public void onBroadcastWriteFinished(int requestCode, int position) {
        mBroadcastAdapter.notifyItemChanged(position);
    }

    @Override
    public void onBroadcastWriteStarted(int requestCode, int position) {
        mBroadcastAdapter.notifyItemChanged(position);
    }

    private void updateRefreshing() {
        boolean loading = mBroadcastListResource.isLoading();
        boolean empty = mBroadcastListResource.isEmpty();
        boolean loadingMore = mBroadcastListResource.isLoadingMore();
        mSwipeRefreshLayout.setRefreshing(loading && (mSwipeRefreshLayout.isRefreshing() || !empty)
                && !loadingMore);
        ViewUtils.setVisibleOrGone(mProgress, loading && empty);
        mAdapter.setProgressVisible(loading && !empty && loadingMore);
    }

    @Override
    public void onLikeBroadcast(Broadcast broadcast, boolean like) {
        LikeBroadcastManager.getInstance().write(broadcast, like, getActivity());
    }

    @Override
    public void onRebroadcastBroadcast(Broadcast broadcast, boolean rebroadcast) {
        RebroadcastBroadcastManager.getInstance().write(broadcast, rebroadcast, getActivity());
    }

    @Override
    public void onCommentBroadcast(Broadcast broadcast, View sharedView) {
        // Open ime for comment if there is none; otherwise we always let the user see what others
        // have already said first, to help to make the world a better place.
        openBroadcast(broadcast, sharedView, broadcast.canComment() && broadcast.commentCount == 0);
    }

    @Override
    public void onOpenBroadcast(Broadcast broadcast, View sharedView) {
        openBroadcast(broadcast, sharedView, false);
    }

    private void openBroadcast(Broadcast broadcast, View sharedView, boolean showSendComment) {
        Activity activity = getActivity();
        Intent intent = BroadcastActivity.makeIntent(broadcast, showSendComment,
                activity.getTitle().toString(), activity);
        Bundle options = TransitionUtils.makeActivityOptionsBundle(activity, sharedView);
        ActivityCompat.startActivity(activity, intent, options);
    }

    protected void setPaddingTop(int paddingTop) {
        mSwipeRefreshLayout.setProgressViewOffset(paddingTop);
        mBroadcastList.setPadding(mBroadcastList.getPaddingLeft(), paddingTop,
                mBroadcastList.getPaddingRight(), mBroadcastList.getPaddingBottom());
    }

    protected abstract BroadcastListResource onAttachBroadcastListResource();

    protected void onSwipeRefresh() {
        mBroadcastListResource.load(false);
    }

    protected void onSendBroadcast() {
        NotImplementedManager.sendBroadcast(null, getActivity());
    }
}
