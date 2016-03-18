/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.notification.ui;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.android.volley.VolleyError;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.app.RetainDataFragment;
import me.zhanghai.android.douya.network.RequestFragment;
import me.zhanghai.android.douya.network.api.ApiError;
import me.zhanghai.android.douya.network.api.ApiRequest;
import me.zhanghai.android.douya.network.api.ApiRequests;
import me.zhanghai.android.douya.network.api.Frodo;
import me.zhanghai.android.douya.network.api.info.Notification;
import me.zhanghai.android.douya.network.api.info.NotificationList;
import me.zhanghai.android.douya.notification.app.NotificationListCache;
import me.zhanghai.android.douya.settings.info.Settings;
import me.zhanghai.android.douya.ui.LoadMoreAdapter;
import me.zhanghai.android.douya.ui.NoChangeAnimationItemAnimator;
import me.zhanghai.android.douya.ui.OnVerticalScrollListener;
import me.zhanghai.android.douya.util.Callback;
import me.zhanghai.android.douya.util.LogUtils;
import me.zhanghai.android.douya.util.ToastUtils;
import me.zhanghai.android.douya.util.ViewUtils;

public class NotificationListFragment extends Fragment implements RequestFragment.Listener {

    private static final int NOTIFICATION_COUNT_PER_LOAD = 20;

    private static final int REQUEST_CODE_LOAD_NOTIFICATION_LIST = 0;

    private static final String KEY_PREFIX = NotificationListFragment.class.getName() + '.';

    private static final String RETAIN_DATA_KEY_NOTIFICATION_LIST = KEY_PREFIX
            + "notification_list";
    private static final String RETAIN_DATA_KEY_CAN_LOAD_MORE = KEY_PREFIX + "can_load_more";
    private static final String RETAIN_DATA_KEY_LOADING_NOTIFICATION_LIST = KEY_PREFIX
            + "loading_notification_list";
    private static final String RETAIN_DATA_KEY_VIEW_STATE = KEY_PREFIX + "view_state";

    private final Handler mHandler = new Handler();

    @Bind(R.id.swipe_refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @Bind(R.id.notification_list)
    RecyclerView mNotificationList;
    @Bind(R.id.progress)
    ProgressBar mProgress;

    private RetainDataFragment mRetainDataFragment;

    private NotificationAdapter mNotificationAdapter;
    private LoadMoreAdapter mAdapter;
    private boolean mCanLoadMore;

    private boolean mLoadingNotificationList;

    private UnreadNotificationCountListener mUnreadNotificationCountListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.notification_list_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final Activity activity = getActivity();

        mRetainDataFragment = RetainDataFragment.attachTo(this);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadNotificationList(false);
            }
        });

        mNotificationList.setHasFixedSize(true);
        mNotificationList.setItemAnimator(new NoChangeAnimationItemAnimator());
        mNotificationList.setLayoutManager(new LinearLayoutManager(activity));
        List<Notification> notificationList = mRetainDataFragment.remove(
                RETAIN_DATA_KEY_NOTIFICATION_LIST);
        mNotificationAdapter = new NotificationAdapter(notificationList, activity);
        mAdapter = new LoadMoreAdapter(R.layout.load_more_item, mNotificationAdapter);
        mNotificationList.setAdapter(mAdapter);
        mNotificationList.addOnScrollListener(new OnVerticalScrollListener() {
            @Override
            public void onScrolledToBottom() {
                loadNotificationList(true);
            }
        });

        mCanLoadMore = mRetainDataFragment.removeBoolean(RETAIN_DATA_KEY_CAN_LOAD_MORE, true);
        mLoadingNotificationList = mRetainDataFragment.removeBoolean(
                RETAIN_DATA_KEY_LOADING_NOTIFICATION_LIST, false);

        // View only saves state influenced by user action, so we have to do this ourselves.
        ViewState viewState = mRetainDataFragment.remove(RETAIN_DATA_KEY_VIEW_STATE);
        if (viewState != null) {
            onRestoreViewState(viewState);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);

        mRetainDataFragment.put(RETAIN_DATA_KEY_NOTIFICATION_LIST, mNotificationAdapter.getList());
        mRetainDataFragment.put(RETAIN_DATA_KEY_CAN_LOAD_MORE, mCanLoadMore);
        mRetainDataFragment.put(RETAIN_DATA_KEY_LOADING_NOTIFICATION_LIST,
                mLoadingNotificationList);
        mRetainDataFragment.put(RETAIN_DATA_KEY_VIEW_STATE, onSaveViewState());
    }

    @Override
    public void onStart() {
        super.onStart();

        // Only auto-load when initially empty, not loaded but empty.
        if (mNotificationAdapter.getItemCount() == 0 && mCanLoadMore) {
            loadNotificationList();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        saveNotificationListToCache(mNotificationAdapter.getList());
    }

    private ViewState onSaveViewState() {
        return new ViewState(mProgress.getVisibility(), mAdapter.isProgressVisible());
    }

    private void onRestoreViewState(ViewState state) {
        mProgress.setVisibility(state.progressVisibility);
        mAdapter.setProgressVisible(state.adapterProgressVisible);
    }

    @Override
    public void onVolleyResponse(int requestCode, boolean successful, Object result,
                                 VolleyError error, Object requestState) {
        switch (requestCode) {
            case REQUEST_CODE_LOAD_NOTIFICATION_LIST:
                //noinspection unchecked
                onLoadNotificationListResponse(successful, (NotificationList) result, error,
                        (LoadNotificationListState) requestState);
                break;
            default:
                LogUtils.w("Unknown request code " + requestCode + ", with successful=" + successful
                        + ", result=" + result + ", error=" + error);
        }
    }

    public void refresh() {
        // DISABLED: This always make users miss unread notifications. Should wait for another API
        // that can return a full list of notification and mark items as read explicitly.
//        // Don't lose unread notifications if the refresh is not started by user.
//        if (getUnreadNotificationCount() > 0) {
//            return;
//        }
//        if (!mNotificationAdapter.getList().isEmpty()) {
//            mSwipeRefreshLayout.setRefreshing(true);
//        }
//        loadNotificationList(false);
    }

    public void setUnreadNotificationCountListener(UnreadNotificationCountListener listener) {
        mUnreadNotificationCountListener = listener;
    }

    private void onNotificationListUpdated() {
        if (mUnreadNotificationCountListener != null) {
            mUnreadNotificationCountListener.onUnreadNotificationUpdate(
                    getUnreadNotificationCount());
        }
    }

    private int getUnreadNotificationCount() {
        int count = 0;
        for (Notification notification : mNotificationAdapter.getList()) {
            if (!notification.read) {
                ++count;
            }
        }
        return count;
    }

    private void loadNotificationList(boolean loadMore) {

        if (mLoadingNotificationList || (loadMore && !mCanLoadMore)) {
            return;
        }

        // Flawed Frodo API design: should use untilId instead of start.
        Integer start = loadMore ? mNotificationAdapter.getItemCount() : null;
        int count = NOTIFICATION_COUNT_PER_LOAD;
        ApiRequest<NotificationList> request = ApiRequests.newNotificationListRequest(start, count,
                getActivity());
        LoadNotificationListState state = new LoadNotificationListState(loadMore, count);
        RequestFragment.startRequest(REQUEST_CODE_LOAD_NOTIFICATION_LIST, request, state, this);

        mLoadingNotificationList = true;
        setRefreshing(true, loadMore);
    }

    @Frodo("status/notificaitons is buggy in notifications.length")
    private void onLoadNotificationListResponse(boolean successful, NotificationList result,
                                                VolleyError error, LoadNotificationListState state) {

        Activity activity = getActivity();
        if (successful) {

            List<Notification> notificationList = result.notifications;
            // Workaround Frodo API bug.
            //mCanLoadMore = notificationList.size() == state.count;
            mCanLoadMore = notificationList.size() > 0;
            if (state.loadMore) {
                mNotificationAdapter.addAll(notificationList);
            } else {
                mNotificationAdapter.replace(notificationList);
            }
            onNotificationListUpdated();
            setRefreshing(false, state.loadMore);
            mLoadingNotificationList = false;
        } else {

            LogUtils.e(error.toString());
            ToastUtils.show(ApiError.getErrorString(error, activity), activity);
            setRefreshing(false, state.loadMore);
            mLoadingNotificationList = false;
        }
    }

    private void setRefreshing(boolean refreshing, boolean loadMore) {
        mSwipeRefreshLayout.setEnabled(!refreshing);
        if (!refreshing) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
        ViewUtils.setVisibleOrGone(mProgress, refreshing
                && mNotificationAdapter.getItemCount() == 0);
        mAdapter.setProgressVisible(refreshing && mNotificationAdapter.getItemCount() > 0
                && loadMore);
    }

    private void loadNotificationList() {
        NotificationListCache.get(mHandler, new Callback<List<Notification>>() {
            @Override
            public void onValue(List<Notification> notificationList) {
                // FIXME: If after onStop() then RequestFragment should not be added. Use
                // commitAllowingStateLoss()?
                if (isRemoving()) {
                    return;
                }
                boolean hasCache = notificationList != null && notificationList.size() > 0;
                if (hasCache) {
                    mNotificationAdapter.replace(notificationList);
                    onNotificationListUpdated();
                }
                if (!hasCache || Settings.AUTO_REFRESH_HOME.getValue(getActivity())) {
                    // FIXME: Revert to calling refresh() later.
                    if (!mNotificationAdapter.getList().isEmpty()) {
                        mSwipeRefreshLayout.setRefreshing(true);
                    }
                    loadNotificationList(false);
                }
            }
        }, getActivity());
    }

    private void saveNotificationListToCache(List<Notification> notificationList) {
        NotificationListCache.put(notificationList, getActivity());
    }

    public interface UnreadNotificationCountListener {
        void onUnreadNotificationUpdate(int count);
    }

    private static class LoadNotificationListState {

        public boolean loadMore;
        public int count;

        public LoadNotificationListState(boolean loadMore, int count) {
            this.loadMore = loadMore;
            this.count = count;
        }
    }

    private static class ViewState {

        public int progressVisibility;
        public boolean adapterProgressVisible;

        public ViewState(int progressVisibility, boolean adapterProgressVisible) {
            this.progressVisibility = progressVisibility;
            this.adapterProgressVisible = adapterProgressVisible;
        }
    }
}
