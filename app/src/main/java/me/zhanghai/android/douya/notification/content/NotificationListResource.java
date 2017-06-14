/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.notification.content;

import android.accounts.Account;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Collections;
import java.util.List;

import me.zhanghai.android.douya.account.util.AccountUtils;
import me.zhanghai.android.douya.content.MoreRawListResourceFragment;
import me.zhanghai.android.douya.eventbus.EventBusUtils;
import me.zhanghai.android.douya.eventbus.NotificationDeletedEvent;
import me.zhanghai.android.douya.eventbus.NotificationListUpdatedEvent;
import me.zhanghai.android.douya.eventbus.NotificationUpdatedEvent;
import me.zhanghai.android.douya.network.api.ApiError;
import me.zhanghai.android.douya.network.api.ApiRequest;
import me.zhanghai.android.douya.network.api.ApiService;
import me.zhanghai.android.douya.network.api.info.frodo.Notification;
import me.zhanghai.android.douya.network.api.info.frodo.NotificationList;
import me.zhanghai.android.douya.settings.info.Settings;
import me.zhanghai.android.douya.util.Callback;
import me.zhanghai.android.douya.util.FragmentUtils;

public class NotificationListResource
        extends MoreRawListResourceFragment<Notification, NotificationList> {

    private final Handler mHandler = new Handler();
    private boolean mStopped;

    private Account mAccount;
    private boolean mLoadingFromCache;

    private static final String FRAGMENT_TAG_DEFAULT = NotificationListResource.class.getName();

    private static NotificationListResource newInstance() {
        //noinspection deprecation
        return new NotificationListResource();
    }

    public static NotificationListResource attachTo(Fragment fragment, String tag,
                                                    int requestCode) {
        FragmentActivity activity = fragment.getActivity();
        NotificationListResource instance = FragmentUtils.findByTag(activity, tag);
        if (instance == null) {
            instance = newInstance();
            instance.targetAt(fragment, requestCode);
            FragmentUtils.add(instance, activity, tag);
        }
        return instance;
    }

    public static NotificationListResource attachTo(Fragment fragment) {
        return attachTo(fragment, FRAGMENT_TAG_DEFAULT, REQUEST_CODE_INVALID);
    }

    /**
     * @deprecated Use {@code attachTo()} instead.
     */
    public NotificationListResource() {}

    @Override
    public void onStart() {
        super.onStart();

        mStopped = false;
    }

    @Override
    public void onStop() {
        super.onStop();

        mStopped = true;

        if (!isEmpty()) {
            saveToCache(get());
        }
    }

    @Override
    protected boolean shouldIgnoreStartRequest() {
        return mLoadingFromCache;
    }

    @Override
    public boolean isLoading() {
        return super.isLoading() || mLoadingFromCache;
    }

    @Override
    protected void onLoadOnStart() {
        loadFromCache();
    }

    private void loadFromCache() {

        mLoadingFromCache = true;

        mAccount = AccountUtils.getActiveAccount();
        NotificationListCache.get(mAccount, mHandler, new Callback<List<Notification>>() {
            @Override
            public void onValue(List<Notification> notificationList) {
                onLoadFromCacheFinished(notificationList);
            }
        }, getActivity());

        onLoadStarted();
    }

    private void onLoadFromCacheFinished(List<Notification> notificationList) {

        mLoadingFromCache = false;

        if (mStopped) {
            return;
        }

        boolean hasCache = notificationList != null && !notificationList.isEmpty();
        if (hasCache) {
            setAndNotifyListener(notificationList);
        }

        if (!hasCache || Settings.AUTO_REFRESH_HOME.getValue()) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mStopped) {
                        return;
                    }
                    NotificationListResource.super.onLoadOnStart();
                }
            });
        }
    }

    private void saveToCache(List<Notification> notificationList) {
        NotificationListCache.put(mAccount, notificationList, getActivity());
    }

    @Override
    protected ApiRequest<NotificationList> onCreateRequest(boolean more, int count) {
        mAccount = AccountUtils.getActiveAccount();
        Integer start = more ? (has() ? get().size() : 0) : null;
        return ApiService.getInstance().getNotificationList(start, count);
    }

    @Override
    protected void onLoadStarted() {
        getListener().onLoadNotificationListStarted(getRequestCode());
    }

    @Override
    protected void onLoadFinished(boolean more, int count, boolean successful,
                                  NotificationList response, ApiError error) {
        onLoadFinished(more, count, successful, response != null ? response.notifications : null,
                error);
    }

    private void onLoadFinished(boolean more, int count, boolean successful,
                                List<Notification> response, ApiError error) {
        getListener().onLoadNotificationListFinished(getRequestCode());
        if (successful) {
            if (more) {
                append(response);
                getListener().onNotificationListAppended(getRequestCode(),
                        Collections.unmodifiableList(response));
            } else {
                setAndNotifyListener(response);
            }
            EventBusUtils.postAsync(new NotificationListUpdatedEvent(mAccount, get(), this));
        } else {
            getListener().onLoadNotificationListError(getRequestCode(), error);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNotificationListUpdated(NotificationListUpdatedEvent event) {

        if (event.isFromMyself(this) || mAccount == null) {
            return;
        }

        if (event.account.equals(mAccount)) {
            setAndNotifyListener(event.notificationList);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNotificationUpdated(NotificationUpdatedEvent event) {

        if (event.isFromMyself(this) || isEmpty()) {
            return;
        }

        List<Notification> notificationList = get();
        for (int i = 0, size = notificationList.size(); i < size; ++i) {
            Notification notification = notificationList.get(i);
            if (notification.id == event.notification.id) {
                notificationList.set(i, event.notification);
                getListener().onNotificationChanged(getRequestCode(), i, notificationList.get(i));
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNotificationDeleted(NotificationDeletedEvent event) {

        if (event.isFromMyself(this) || isEmpty()) {
            return;
        }

        List<Notification> notificationList = get();
        for (int i = 0, size = notificationList.size(); i < size; ) {
            Notification notification = notificationList.get(i);
            if (notification.id == event.notificationId) {
                notificationList.remove(i);
                getListener().onNotificationRemoved(getRequestCode(), i);
                --size;
            } else {
                ++i;
            }
        }
    }

    protected void setAndNotifyListener(List<Notification> notificationList) {
        // HACK: This cannot handle unread count > 20, or read elsewhere.
        if (has()) {
            for (Notification notification : get()) {
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
        set(notificationList);
        getListener().onNotificationListChanged(getRequestCode(),
                Collections.unmodifiableList(notificationList));
    }

    private Listener getListener() {
        return (Listener) getTarget();
    }

    public interface Listener {
        void onLoadNotificationListStarted(int requestCode);
        void onLoadNotificationListFinished(int requestCode);
        void onLoadNotificationListError(int requestCode, ApiError error);
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
