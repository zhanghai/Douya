/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.broadcast.content;

import android.accounts.Account;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import java.util.List;

import me.zhanghai.android.douya.account.util.AccountUtils;
import me.zhanghai.android.douya.network.api.info.frodo.Broadcast;
import me.zhanghai.android.douya.settings.info.Settings;
import me.zhanghai.android.douya.util.FragmentUtils;

public class HomeMergedBroadcastListResource extends MergedBroadcastListResource {

    private static final String FRAGMENT_TAG_DEFAULT =
            HomeMergedBroadcastListResource.class.getName();

    private final Handler mHandler = new Handler();
    private boolean mStopped;

    private Account mAccount;
    private boolean mLoadingFromCache;

    private static HomeMergedBroadcastListResource newInstance() {
        //noinspection deprecation
        return new HomeMergedBroadcastListResource().setArguments();
    }

    public static HomeMergedBroadcastListResource attachTo(Fragment fragment, String tag,
                                                           int requestCode) {
        FragmentActivity activity = fragment.getActivity();
        HomeMergedBroadcastListResource instance = FragmentUtils.findByTag(activity, tag);
        if (instance == null) {
            instance = newInstance();
            instance.targetAt(fragment, requestCode);
            FragmentUtils.add(instance, activity, tag);
        }
        return instance;
    }

    public static HomeMergedBroadcastListResource attachTo(Fragment fragment) {
        return attachTo(fragment, FRAGMENT_TAG_DEFAULT, REQUEST_CODE_INVALID);
    }

    /**
     * @deprecated Use {@code attachTo()} instead.
     */
    @SuppressWarnings("deprecation")
    public HomeMergedBroadcastListResource() {}

    protected HomeMergedBroadcastListResource setArguments() {
        super.setArguments(null, null);
        return this;
    }

    @Override
    public void onStart() {
        super.onStart();

        mStopped = false;

        loadOnStart();
    }

    private void loadOnStart() {
        if (!has()) {
            onLoadOnStart();
        }
    }

    private void onLoadOnStart() {
        loadFromCache();
    }

    private void superOnLoadOnStart() {
        load(false);
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
    public boolean isLoading() {
        return super.isLoading() || mLoadingFromCache;
    }

    private void loadFromCache() {

        setLoadingFromCache(true);

        mAccount = AccountUtils.getActiveAccount();
        HomeBroadcastListCache.get(mAccount, mHandler, this::onLoadFromCacheFinished,
                getActivity());

        onLoadStarted();
    }

    @Override
    protected void onLoadStarted() {
        mAccount = AccountUtils.getActiveAccount();
        super.onLoadStarted();
    }

    private void onLoadFromCacheFinished(List<Broadcast> broadcastList) {

        setLoadingFromCache(false);

        if (mStopped) {
            return;
        }

        boolean hasCache = broadcastList != null && !broadcastList.isEmpty();
        if (hasCache) {
            setAndNotifyListener(broadcastList);
        }

        if (!hasCache || Settings.AUTO_REFRESH_HOME.getValue()) {
            mHandler.post(() -> {
                if (mStopped) {
                    return;
                }
                superOnLoadOnStart();
            });
        }
    }

    private void setLoadingFromCache(boolean loadingFromCache) {
        mLoadingFromCache = loadingFromCache;
        mFrodoResource.setIgnoreStartRequest(mLoadingFromCache);
        mApiV2Resource.setIgnoreStartRequest(mLoadingFromCache);
    }

    private void saveToCache(List<Broadcast> broadcastList) {
        HomeBroadcastListCache.put(mAccount, broadcastList, getActivity());
    }
}
