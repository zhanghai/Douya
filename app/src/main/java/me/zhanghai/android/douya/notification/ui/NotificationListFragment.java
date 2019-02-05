/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.notification.ui;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.eventbus.EventBusUtils;
import me.zhanghai.android.douya.eventbus.NotificationUpdatedEvent;
import me.zhanghai.android.douya.main.ui.MainActivity;
import me.zhanghai.android.douya.network.api.ApiError;
import me.zhanghai.android.douya.network.api.info.frodo.Notification;
import me.zhanghai.android.douya.notification.content.NotificationListResource;
import me.zhanghai.android.douya.ui.LoadMoreAdapter;
import me.zhanghai.android.douya.ui.NoChangeAnimationItemAnimator;
import me.zhanghai.android.douya.ui.OnVerticalScrollListener;
import me.zhanghai.android.douya.util.LogUtils;
import me.zhanghai.android.douya.util.ToastUtils;
import me.zhanghai.android.douya.util.ViewUtils;

public class NotificationListFragment extends Fragment implements NotificationListResource.Listener,
        NotificationAdapter.Listener {

    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.notification_list)
    RecyclerView mNotificationList;
    @BindView(R.id.progress)
    ProgressBar mProgress;

    private NotificationListResource mNotificationListResource;

    private NotificationAdapter mNotificationAdapter;
    private LoadMoreAdapter mAdapter;

    public static NotificationListFragment newInstance() {
        //noinspection deprecation
        return new NotificationListFragment();
    }

    /**
     * @deprecated Use {@link #newInstance()} instead.
     */
    public NotificationListFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.notification_list_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mNotificationListResource = NotificationListResource.attachTo(this);

        mSwipeRefreshLayout.setOnRefreshListener(this::refresh);

        mNotificationList.setHasFixedSize(true);
        mNotificationList.setItemAnimator(new NoChangeAnimationItemAnimator());
        Activity activity = getActivity();
        mNotificationList.setLayoutManager(new LinearLayoutManager(activity));
        mNotificationAdapter = new NotificationAdapter(mNotificationListResource.get(), activity);
        mNotificationAdapter.setListener(this);
        mAdapter = new LoadMoreAdapter(mNotificationAdapter);
        mNotificationList.setAdapter(mAdapter);
        mNotificationList.addOnScrollListener(new OnVerticalScrollListener() {
            @Override
            public void onScrolledToBottom() {
                mNotificationListResource.load(true);
            }
        });

        updateRefreshing();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mNotificationListResource.detach();
    }

    @Override
    public void onLoadNotificationListStarted(int requestCode) {
        updateRefreshing();
    }

    @Override
    public void onLoadNotificationListFinished(int requestCode) {
        updateRefreshing();
    }

    @Override
    public void onLoadNotificationListError(int requestCode, ApiError error) {
        LogUtils.e(error.toString());
        Activity activity = getActivity();
        ToastUtils.show(ApiError.getErrorString(error, activity), activity);
    }

    @Override
    public void onNotificationListChanged(int requestCode, List<Notification> newNotificationList) {
        mNotificationAdapter.replace(newNotificationList);
        onNotificationListUpdated();
    }

    @Override
    public void onNotificationListAppended(int requestCode,
                                           List<Notification> appendedNotificationList) {
        mNotificationAdapter.addAll(appendedNotificationList);
        onNotificationListUpdated();
    }

    @Override
    public void onNotificationChanged(int requestCode, int position, Notification newNotification) {
        mNotificationAdapter.set(position, newNotification);
        onNotificationListUpdated();
    }

    @Override
    public void onNotificationRemoved(int requestCode, int position) {
        mNotificationAdapter.remove(position);
        onNotificationListUpdated();
    }

    private void updateRefreshing() {
        boolean loading = mNotificationListResource.isLoading();
        boolean empty = mNotificationListResource.isEmpty();
        boolean loadingMore = mNotificationListResource.isLoadingMore();
        mSwipeRefreshLayout.setRefreshing(loading && (mSwipeRefreshLayout.isRefreshing() || !empty)
                && !loadingMore);
        ViewUtils.setVisibleOrGone(mProgress, loading && empty);
        mAdapter.setLoading(loading && !empty && loadingMore);
    }

    @Override
    public void onMarkNotificationAsRead(Notification notification) {
        notification.read = true;
        EventBusUtils.postAsync(new NotificationUpdatedEvent(notification, this));
    }

    private void onNotificationListUpdated() {
        MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            activity.onNotificationUnreadCountUpdate(getUnreadCount());
        }
    }

    public int getUnreadCount() {
        if (!mNotificationListResource.has()) {
            return 0;
        }
        int count = 0;
        for (Notification notification : mNotificationListResource.get()) {
            if (!notification.read) {
                ++count;
            }
        }
        return count;
    }

    public void refresh() {
        mNotificationListResource.load(false);
    }
}
