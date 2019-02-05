/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.doumail.content;

import android.accounts.Account;
import android.os.Handler;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import me.zhanghai.android.douya.account.util.AccountUtils;
import me.zhanghai.android.douya.content.ResourceFragment;
import me.zhanghai.android.douya.network.api.ApiError;
import me.zhanghai.android.douya.network.api.ApiRequest;
import me.zhanghai.android.douya.network.api.ApiService;
import me.zhanghai.android.douya.network.api.info.frodo.NotificationCount;
import me.zhanghai.android.douya.settings.info.Settings;
import me.zhanghai.android.douya.util.FragmentUtils;

public class NotificationCountResource
        extends ResourceFragment<NotificationCount, NotificationCount> {

    private final Handler mHandler = new Handler();
    private boolean mStopped;

    private Account mAccount;
    private boolean mLoadingFromCache;

    private static final String FRAGMENT_TAG_DEFAULT = NotificationCountResource.class.getName();

    private static NotificationCountResource newInstance() {
        //noinspection deprecation
        return new NotificationCountResource();
    }

    public static NotificationCountResource attachTo(Fragment fragment, String tag,
                                                     int requestCode) {
        FragmentActivity activity = fragment.getActivity();
        NotificationCountResource instance = FragmentUtils.findByTag(activity, tag);
        if (instance == null) {
            instance = newInstance();
            FragmentUtils.add(instance, activity, tag);
        }
        instance.setTarget(fragment, requestCode);
        return instance;
    }

    public static NotificationCountResource attachTo(Fragment fragment) {
        return attachTo(fragment, FRAGMENT_TAG_DEFAULT, REQUEST_CODE_INVALID);
    }

    /**
     * @deprecated Use {@code attachTo()} instead.
     */
    public NotificationCountResource() {}

    @Override
    public void onStart() {
        super.onStart();

        mStopped = false;
    }

    @Override
    public void onStop() {
        super.onStop();

        mStopped = true;

        if (has()) {
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
        NotificationCountCache.get(mAccount, mHandler, this::onLoadFromCacheFinished, getActivity());

        onLoadStarted();
    }

    private void onLoadFromCacheFinished(NotificationCount notificationCount) {

        mLoadingFromCache = false;

        if (mStopped) {
            return;
        }

        boolean hasCache = notificationCount != null;
        if (hasCache) {
            setAndNotifyListener(notificationCount);
        }

        if (!hasCache || Settings.AUTO_REFRESH_HOME.getValue()) {
            mHandler.post(() -> {
                if (mStopped) {
                    return;
                }
                NotificationCountResource.super.onLoadOnStart();
            });
        }
    }

    private void saveToCache(NotificationCount notificationCount) {
        NotificationCountCache.put(mAccount, notificationCount, getActivity());
    }

    @Override
    protected ApiRequest<NotificationCount> onCreateRequest() {
        mAccount = AccountUtils.getActiveAccount();
        return ApiService.getInstance().getNotificationCount();
    }

    @Override
    protected void onLoadStarted() {
        getListener().onLoadNotificationCountStarted(getRequestCode());
    }

    @Override
    protected void onLoadFinished(boolean successful,NotificationCount response, ApiError error) {
        if (successful) {
            setAndNotifyListener(response);
        } else {
            getListener().onLoadNotificationCountFinished(getRequestCode());
            getListener().onLoadNotificationCountError(getRequestCode(), error);
        }
    }

    protected void setAndNotifyListener(NotificationCount notificationCount) {
        set(notificationCount);
        getListener().onLoadNotificationCountFinished(getRequestCode());
        getListener().onNotificationCountChanged(getRequestCode(), notificationCount);
    }

    private Listener getListener() {
        return (Listener) getTarget();
    }

    public interface Listener {
        void onLoadNotificationCountStarted(int requestCode);
        void onLoadNotificationCountFinished(int requestCode);
        void onLoadNotificationCountError(int requestCode, ApiError error);
        void onNotificationCountChanged(int requestCode, NotificationCount newNotificationCount);
    }
}
