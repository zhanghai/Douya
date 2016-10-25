/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.notification.app;

import android.accounts.Account;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.android.volley.VolleyError;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Collections;
import java.util.List;

import me.zhanghai.android.douya.account.util.AccountUtils;
import me.zhanghai.android.douya.content.ResourceFragment;
import me.zhanghai.android.douya.eventbus.EventBusUtils;
import me.zhanghai.android.douya.eventbus.NotificationDeletedEvent;
import me.zhanghai.android.douya.eventbus.NotificationListUpdatedEvent;
import me.zhanghai.android.douya.eventbus.NotificationUpdatedEvent;
import me.zhanghai.android.douya.network.RequestFragment;
import me.zhanghai.android.douya.network.api.ApiRequest;
import me.zhanghai.android.douya.network.api.ApiRequests;
import me.zhanghai.android.douya.network.api.info.frodo.Notification;
import me.zhanghai.android.douya.network.api.info.frodo.NotificationList;
import me.zhanghai.android.douya.settings.info.Settings;
import me.zhanghai.android.douya.util.Callback;
import me.zhanghai.android.douya.util.FragmentUtils;

public class NotificationListResource extends ResourceFragment
        implements RequestFragment.Listener<NotificationList, NotificationListResource.State> {

    private static final int DEFAULT_COUNT_PER_LOAD = 20;

    private final Handler mHandler = new Handler();

    private Account mAccount;

    private boolean mStopped;

    private List<Notification> mNotificationList;

    private boolean mCanLoadMore = true;
    private boolean mLoading;
    private boolean mLoadingMore;

    private static final String FRAGMENT_TAG_DEFAULT = NotificationListResource.class.getName();

    private static NotificationListResource newInstance() {
        //noinspection deprecation
        return new NotificationListResource();
    }

    public static NotificationListResource attachTo(FragmentActivity activity, String tag,
                                                    int requestCode) {
        return attachTo(activity, tag, true, null, requestCode);
    }

    public static NotificationListResource attachTo(FragmentActivity activity) {
        return attachTo(activity, FRAGMENT_TAG_DEFAULT, REQUEST_CODE_INVALID);
    }

    public static NotificationListResource attachTo(Fragment fragment, String tag,
                                                    int requestCode) {
        return attachTo(fragment.getActivity(), tag, false, fragment, requestCode);
    }

    public static NotificationListResource attachTo(Fragment fragment) {
        return attachTo(fragment, FRAGMENT_TAG_DEFAULT, REQUEST_CODE_INVALID);
    }

    private static NotificationListResource attachTo(FragmentActivity activity, String tag,
                                                     boolean targetAtActivity,
                                                     Fragment targetFragment, int requestCode) {
        NotificationListResource resource = FragmentUtils.findByTag(activity, tag);
        if (resource == null) {
            resource = newInstance();
            if (targetAtActivity) {
                resource.targetAtActivity(requestCode);
            } else {
                resource.targetAtFragment(targetFragment, requestCode);
            }
            FragmentUtils.add(resource, activity, tag);
        }
        return resource;
    }

    /**
     * @deprecated Use {@code attachTo()} instead.
     */
    public NotificationListResource() {}

    /**
     * @return Unmodifiable notification list, or {@code null}.
     */
    public List<Notification> get() {
        return mNotificationList != null ? Collections.unmodifiableList(mNotificationList) : null;
    }

    public boolean has() {
        return mNotificationList != null;
    }

    public boolean isEmpty() {
        return mNotificationList == null || mNotificationList.isEmpty();
    }

    public boolean isLoading() {
        return mLoading;
    }

    public boolean isLoadingMore() {
        return mLoadingMore;
    }

    @Override
    public void onStart() {
        super.onStart();

        mStopped = false;

        EventBusUtils.register(this);

        if (mNotificationList == null || (mNotificationList.isEmpty() && mCanLoadMore)) {
            loadOnStart();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        EventBusUtils.unregister(this);

        mStopped = true;

        if (mNotificationList != null && mNotificationList.size() > 0) {
            saveToCache();
        }
    }

    public void load(boolean loadMore, int count) {

        if (mLoading || (loadMore && !mCanLoadMore)) {
            return;
        }

        mLoading = true;
        mLoadingMore = loadMore;
        getListener().onLoadNotificationListStarted(getRequestCode());

        onStartLoad();
        // Flawed Frodo API design: should use untilId instead of start.
        Integer start = loadMore && mNotificationList != null ? mNotificationList.size() : null;
        ApiRequest<NotificationList> request = ApiRequests.newNotificationListRequest(start, count);
        State state = new State(loadMore, count);
        RequestFragment.startRequest(request, state, this);
    }

    public void load(boolean loadMore) {
        load(loadMore, DEFAULT_COUNT_PER_LOAD);
    }

    private void loadOnStart() {
        loadFromCache();
    }

    private void loadFromCache() {

        if (isLoading()) {
            return;
        }

        setLoading(true);

        onStartLoad();
        NotificationListCache.get(mAccount, mHandler, new Callback<List<Notification>>() {
            @Override
            public void onValue(List<Notification> notificationList) {
                onLoadFromCacheComplete(notificationList);
            }
        }, getActivity());
    }

    private void onStartLoad() {
        mAccount = AccountUtils.getActiveAccount();
    }

    @Override
    public void onVolleyResponse(int requestCode, final boolean successful,
                                 final NotificationList result, final VolleyError error,
                                 final State requestState) {
        postOnResumed(new Runnable() {
            @Override
            public void run() {
                onLoadFinished(successful, result != null ? result.notifications : null, error,
                        requestState.loadMore, requestState.count);
            }
        });
    }

    private void onLoadFinished(boolean successful, List<Notification> notificationList,
                                VolleyError error, boolean loadMore, int count) {

        mLoading = false;
        mLoadingMore = false;
        getListener().onLoadNotificationListFinished(getRequestCode());

        if (successful) {
            mCanLoadMore = notificationList.size() == count;
            if (loadMore) {
                mNotificationList.addAll(notificationList);
                getListener().onNotificationListAppended(getRequestCode(),
                        Collections.unmodifiableList(notificationList));
            } else {
                set(notificationList);
            }
            EventBusUtils.postAsync(new NotificationListUpdatedEvent(mAccount, mNotificationList,
                    this));
        } else {
            getListener().onLoadNotificationListError(getRequestCode(), error);
        }
    }

    private void onLoadFromCacheComplete(List<Notification> notificationList) {

        setLoading(false);

        if (mStopped) {
            return;
        }

        boolean hasCache = notificationList != null && notificationList.size() > 0;
        if (hasCache) {
            set(notificationList);
        }

        if (!hasCache || Settings.AUTO_REFRESH_HOME.getValue()) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mStopped) {
                        return;
                    }
                    load(false);
                }
            });
        }
    }

    private void saveToCache() {
        NotificationListCache.put(mAccount, mNotificationList, getActivity());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNotificationListUpdated(NotificationListUpdatedEvent event) {

        if (event.isFromMyself(this) || mAccount == null) {
            return;
        }

        if (event.account.equals(mAccount)) {
            set(event.notificationList);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNotificationUpdated(NotificationUpdatedEvent event) {

        if (event.isFromMyself(this) || mNotificationList == null) {
            return;
        }

        for (int i = 0, size = mNotificationList.size(); i < size; ++i) {
            Notification notification = mNotificationList.get(i);
            if (notification.id == event.notification.id) {
                mNotificationList.set(i, event.notification);
                getListener().onNotificationChanged(getRequestCode(), i, mNotificationList.get(i));
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNotificationDeleted(NotificationDeletedEvent event) {

        if (event.isFromMyself(this) || mNotificationList == null) {
            return;
        }

        for (int i = 0, size = mNotificationList.size(); i < size; ) {
            Notification notification = mNotificationList.get(i);
            if (notification.id == event.notificationId) {
                mNotificationList.remove(i);
                getListener().onNotificationRemoved(getRequestCode(), i);
                --size;
            } else {
                ++i;
            }
        }
    }

    protected void setLoading(boolean loading) {
        if (mLoading == loading) {
            return;
        }
        mLoading = loading;
        if (mLoading) {
            getListener().onLoadNotificationListStarted(getRequestCode());
        } else {
            getListener().onLoadNotificationListFinished(getRequestCode());
        }
    }

    protected void set(List<Notification> notificationList) {
        // HACK: This cannot handle unread count > 20, or read elsewhere.
        if (mNotificationList != null) {
            for (Notification notification : mNotificationList) {
                if (!notification.read) {
                    for (Notification newNotification : notificationList) {
                        if (newNotification.id == notification.id) {
                            newNotification.read = false;
                            break;
                        }
                    }
                }
            }
        }
        mNotificationList = notificationList;
        getListener().onNotificationListChanged(getRequestCode(),
                Collections.unmodifiableList(notificationList));
    }

    private Listener getListener() {
        return (Listener) getTarget();
    }

    static class State {

        public boolean loadMore;
        public int count;

        public State(boolean loadMore, int count) {
            this.loadMore = loadMore;
            this.count = count;
        }
    }

    public interface Listener {
        void onLoadNotificationListStarted(int requestCode);
        void onLoadNotificationListFinished(int requestCode);
        void onLoadNotificationListError(int requestCode, VolleyError error);
        /**
         * @param newNotificationList Unmodifiable.
         */
        void onNotificationListChanged(int requestCode, List<Notification> newNotificationList);
        /**
         * @param appendedNotificationList Unmodifiable.
         */
        void onNotificationListAppended(int requestCode,
                                        List<Notification> appendedNotificationList);
        void onNotificationChanged(int requestCode, int position, Notification newNotification);
        void onNotificationRemoved(int requestCode, int position);
    }
}
