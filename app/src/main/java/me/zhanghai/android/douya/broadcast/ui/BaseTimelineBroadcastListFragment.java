/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.broadcast.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import me.zhanghai.android.customtabshelper.CustomTabsHelperFragment;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.broadcast.content.DeleteBroadcastManager;
import me.zhanghai.android.douya.broadcast.content.LikeBroadcastManager;
import me.zhanghai.android.douya.broadcast.content.RebroadcastBroadcastManager;
import me.zhanghai.android.douya.broadcast.content.TimelineBroadcastListResource;
import me.zhanghai.android.douya.link.NotImplementedManager;
import me.zhanghai.android.douya.network.api.info.frodo.Broadcast;
import me.zhanghai.android.douya.ui.AppBarHost;
import me.zhanghai.android.douya.ui.FastSmoothScrollStaggeredGridLayoutManager;
import me.zhanghai.android.douya.ui.FriendlyFloatingActionButton;
import me.zhanghai.android.douya.ui.OnVerticalScrollWithPagingTouchSlopListener;
import me.zhanghai.android.douya.ui.SimpleAdapter;
import me.zhanghai.android.douya.util.CardUtils;
import me.zhanghai.android.douya.util.CheatSheetUtils;
import me.zhanghai.android.douya.util.RecyclerViewUtils;
import me.zhanghai.android.douya.util.TransitionUtils;

public abstract class BaseTimelineBroadcastListFragment extends BaseBroadcastListFragment
        implements TimelineBroadcastListResource.Listener, BroadcastAdapter.Listener {

    @BindView(R.id.send)
    FriendlyFloatingActionButton mSendFab;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.broadcast_list_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        CustomTabsHelperFragment.attachTo(this);

        CheatSheetUtils.setup(mSendFab);
        mSendFab.setOnClickListener(view -> onSendBroadcast());
    }

    @Override
    protected RecyclerView.LayoutManager onCreateLayoutManager() {
        return new FastSmoothScrollStaggeredGridLayoutManager(
                CardUtils.getColumnCount(getActivity()), StaggeredGridLayoutManager.VERTICAL);
    }

    @Override
    protected SimpleAdapter<Broadcast, ?> onCreateAdapter() {
        return new BroadcastAdapter(this);
    }

    @Override
    protected void onAttachScrollListener() {
        AppBarHost appBarHost = (AppBarHost) getParentFragment();
        mList.addOnScrollListener(
                new OnVerticalScrollWithPagingTouchSlopListener(getActivity()) {
                    @Override
                    public void onScrolled(int dy) {
                        if (!RecyclerViewUtils.hasFirstChildReachedTop(mList)) {
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
                        if (RecyclerViewUtils.hasFirstChildReachedTop(mList)) {
                            appBarHost.hideAppBar();
                            mSendFab.hide();
                        }
                    }
                    @Override
                    public void onScrolledToBottom() {
                        mResource.load(true);
                    }
                });
        appBarHost.setToolBarOnDoubleClickListener(view -> {
            mList.smoothScrollToPosition(0);
            return true;
        });
    }

    @Override
    public void onBroadcastWriteStarted(int requestCode, int position) {
        onItemWriteStarted(position);
    }

    @Override
    public void onBroadcastWriteFinished(int requestCode, int position) {
        onItemWriteStarted(position);
    }

    @Override
    public void onLikeBroadcast(Broadcast broadcast, boolean like) {
        LikeBroadcastManager.getInstance().write(broadcast, like, getActivity());
    }

    @Override
    public void onRebroadcastBroadcast(Broadcast broadcast, boolean quick) {
        if (quick) {
            RebroadcastBroadcastManager.getInstance().write(broadcast, null, getActivity());
        } else {
            // TODO: Dialog
        }
    }

    @Override
    public void onUnrebroadcastBroadcast(Broadcast broadcast, boolean quick) {
        if (quick) {
            DeleteBroadcastManager.getInstance().write(broadcast, getActivity());
        } else {
            // TODO: Dialog
        }
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
        mList.setPadding(mList.getPaddingLeft(), paddingTop,
                mList.getPaddingRight(), mList.getPaddingBottom());
    }

    protected void onSendBroadcast() {
        NotImplementedManager.sendBroadcast(null, getActivity());
    }
}
