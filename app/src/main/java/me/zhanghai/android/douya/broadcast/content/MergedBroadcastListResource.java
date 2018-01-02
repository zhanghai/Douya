/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.broadcast.content;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.SparseIntArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import me.zhanghai.android.douya.app.TargetedRetainedFragment;
import me.zhanghai.android.douya.content.MoreListResource;
import me.zhanghai.android.douya.network.api.ApiError;
import me.zhanghai.android.douya.network.api.info.frodo.Broadcast;
import me.zhanghai.android.douya.util.CollectionUtils;
import me.zhanghai.android.douya.util.FragmentUtils;
import me.zhanghai.android.douya.util.LogUtils;

public class MergedBroadcastListResource extends TargetedRetainedFragment
        implements MoreListResource<List<Broadcast>>, ApiV2BroadcastListResource.Listener,
        TimelineBroadcastListResource.Listener {

    private static final int DEFAULT_LOAD_COUNT = 20;

    // Not static because we are to be subclassed.
    private final String KEY_PREFIX = getClass().getName() + '.';

    private final String EXTRA_USER_ID_OR_UID = KEY_PREFIX + "user_id_or_uid";
    private final String EXTRA_TOPIC = KEY_PREFIX + "topic";

    private String mUserIdOrUid;
    private String mTopic;

    protected TimelineBroadcastListResource mFrodoResource;
    protected ApiV2BroadcastListResource mApiV2Resource;

    private boolean mLoadingMore;
    private int mLoadCount;

    private List<Broadcast> mBroadcastList;
    private SparseIntArray mFrodoBroadcastPositionMap = new SparseIntArray();

    private static final String FRAGMENT_TAG_DEFAULT = MergedBroadcastListResource.class.getName();

    private static MergedBroadcastListResource newInstance(String userIdOrUid, String topic) {
        //noinspection deprecation
        return new MergedBroadcastListResource().setArguments(userIdOrUid, topic);
    }

    public static MergedBroadcastListResource attachTo(String userIdOrUid, String topic,
                                                       Fragment fragment, String tag,
                                                       int requestCode) {
        FragmentActivity activity = fragment.getActivity();
        MergedBroadcastListResource instance = FragmentUtils.findByTag(activity, tag);
        if (instance == null) {
            instance = newInstance(userIdOrUid, topic);
            instance.targetAt(fragment, requestCode);
            FragmentUtils.add(instance, activity, tag);
        }
        return instance;
    }

    public static MergedBroadcastListResource attachTo(String userIdOrUid, String topic,
                                                       Fragment fragment) {
        return attachTo(userIdOrUid, topic, fragment, FRAGMENT_TAG_DEFAULT, REQUEST_CODE_INVALID);
    }

    /**
     * @deprecated Use {@code attachTo()} instead.
     */
    public MergedBroadcastListResource() {}

    protected MergedBroadcastListResource setArguments(String userIdOrUid, String topic) {
        Bundle arguments = FragmentUtils.ensureArguments(this);
        arguments.putString(EXTRA_USER_ID_OR_UID, userIdOrUid);
        arguments.putString(EXTRA_TOPIC, topic);
        return this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        mUserIdOrUid = arguments.getString(EXTRA_USER_ID_OR_UID);
        mTopic = arguments.getString(EXTRA_TOPIC);

        mFrodoResource = TimelineBroadcastListResource.attachTo(mUserIdOrUid, mTopic, this);
        mApiV2Resource = ApiV2BroadcastListResource.attachTo(mUserIdOrUid, mTopic, this);
    }

    @Override
    public boolean has() {
        return mBroadcastList != null;
    }

    @Override
    public List<Broadcast> get() {
        return mBroadcastList;
    }

    @Override
    public boolean isEmpty() {
        return CollectionUtils.isEmpty(mBroadcastList);
    }

    @Override
    public void load(boolean more, int count) {

        if (isLoading() || !canLoadMore()) {
            return;
        }

        mLoadingMore = more;
        mLoadCount = count;

        mFrodoResource.load(mLoadingMore, mLoadCount);
        loadApiV2(mLoadingMore, mLoadCount);
    }

    private void loadApiV2(boolean more, int count) {
        if (!more) {
            mApiV2Resource.load(false, count);
        } else {
            if (!mApiV2Resource.isEmpty() && !mFrodoResource.isEmpty()) {
                me.zhanghai.android.douya.network.api.info.apiv2.Broadcast lastApiV2Broadcast =
                        CollectionUtils.last(mApiV2Resource.get());
                Broadcast lastFrodoBroadcast = CollectionUtils.last(mFrodoResource.get());
                if (lastApiV2Broadcast.id <= lastFrodoBroadcast.id) {
                    return;
                }
            }
            mApiV2Resource.load(true, count);
        }
    }

    private void loadApiV2() {
        loadApiV2(true, mLoadCount);
    }

    @Override
    public void load(boolean loadMore) {
        load(loadMore, getDefaultLoadCount());
    }

    protected int getDefaultLoadCount() {
        return DEFAULT_LOAD_COUNT;
    }

    @Override
    public boolean isLoading() {
        return (mFrodoResource != null && mFrodoResource.isLoading())
                || (mApiV2Resource != null && mApiV2Resource.isLoading());
    }

    @Override
    public boolean canLoadMore() {
        return (mFrodoResource == null || mFrodoResource.canLoadMore())
                || (mApiV2Resource == null || mApiV2Resource.canLoadMore());
    }

    @Override
    public boolean isLoadingMore() {
        return mLoadingMore;
    }

    @Override
    public void onLoadBroadcastListStarted(int requestCode) {
        onLoadStarted();
    }

    @Override
    public void onLoadApiV2BroadcastListStarted(int requestCode) {
        if (!mFrodoResource.canLoadMore()) {
            onLoadStarted();
        }
    }

    protected void onLoadStarted() {
        getListener().onLoadBroadcastListStarted(getRequestCode());
    }

    @Override
    public void onLoadBroadcastListFinished(int requestCode) {
        if (mApiV2Resource.isLoading()) {
            return;
        }
        onLoadFinished();
    }

    @Override
    public void onLoadApiV2BroadcastListFinished(int requestCode) {
        if (mFrodoResource.isLoading()) {
            return;
        }
        loadApiV2();
        if (mApiV2Resource.isLoading()) {
            return;
        }
        onLoadFinished();
    }

    private void onLoadFinished() {
        mLoadingMore = false;
        getListener().onLoadBroadcastListFinished(getRequestCode());
    }

    @Override
    public void onLoadBroadcastListError(int requestCode, ApiError error) {
        onLoadError(error);
    }

    @Override
    public void onLoadApiV2BroadcastListError(int requestCode, ApiError error) {
        onLoadError(error);
    }

    private void onLoadError(ApiError error) {
        getListener().onLoadBroadcastListError(getRequestCode(), error);
    }

    @Override
    public void onBroadcastListChanged(int requestCode, List<Broadcast> newBroadcastList) {
        loadApiV2();
        if (mApiV2Resource.isLoading()) {
            return;
        }
        onListChanged();
    }

    @Override
    public void onApiV2BroadcastListChanged(
            int requestCode,
            List<me.zhanghai.android.douya.network.api.info.apiv2.Broadcast> newBroadcastList) {
        if (mFrodoResource.isLoading()) {
            return;
        }
        loadApiV2();
        if (mApiV2Resource.isLoading()) {
            return;
        }
        onListChanged();
    }

    private void onListChanged() {
        rebuildBroadcastList();
        getListener().onBroadcastListChanged(getRequestCode(), Collections.unmodifiableList(
                mBroadcastList));
    }

    @Override
    public void onBroadcastListAppended(int requestCode, List<Broadcast> appendedBroadcastList) {
        LogUtils.wtf("Frodo appended " + mFrodoResource.isLoading()
                + " " + mFrodoResource.isLoadingMore()
                + " " + mFrodoResource.get().size()
                + " " + mApiV2Resource.isLoading()
                + " " + mApiV2Resource.isLoadingMore()
                + " " + mApiV2Resource.get().size());
        loadApiV2();
        LogUtils.wtf("Frodo appended (loadApiV2) " + mFrodoResource.isLoading()
                + " " + mFrodoResource.isLoadingMore()
                + " " + mFrodoResource.get().size()
                + " " + mApiV2Resource.isLoading()
                + " " + mApiV2Resource.isLoadingMore()
                + " " + mApiV2Resource.get().size());
        if (mApiV2Resource.isLoading()) {
            return;
        }
        onListAppended();
    }

    @Override
    public void onApiV2BroadcastListAppended(
            int requestCode,
            List<me.zhanghai.android.douya.network.api.info.apiv2.Broadcast> appendedBroadcastList
    ) {
        LogUtils.wtf("ApiV2 appended " + mFrodoResource.isLoading()
                + " " + mFrodoResource.isLoadingMore()
                + " " + mFrodoResource.get().size()
                + " " + mApiV2Resource.isLoading()
                + " " + mApiV2Resource.isLoadingMore()
                + " " + mApiV2Resource.get().size());
        if (mFrodoResource.isLoading()) {
            return;
        }
        loadApiV2();
        LogUtils.wtf("ApiV2 appended (loadApiV2) " + mFrodoResource.isLoading()
                + " " + mFrodoResource.isLoadingMore()
                + " " + mFrodoResource.get().size()
                + " " + mApiV2Resource.isLoading()
                + " " + mApiV2Resource.isLoadingMore()
                + " " + mApiV2Resource.get().size());
        if (mApiV2Resource.isLoading()) {
            return;
        }
        LogUtils.wtf("ApiV2 appended (Good!) " + mFrodoResource.isLoading()
                + " " + mFrodoResource.isLoadingMore()
                + " " + mFrodoResource.get().size()
                + " " + mApiV2Resource.isLoading()
                + " " + mApiV2Resource.isLoadingMore()
                + " " + mApiV2Resource.get().size());
        onListAppended();
    }

    private void onListAppended() {
        int oldSize = CollectionUtils.size(mBroadcastList);
        rebuildBroadcastList();
        List<Broadcast> appendedList = mBroadcastList.subList(oldSize, mBroadcastList.size());
        getListener().onBroadcastListAppended(getRequestCode(), Collections.unmodifiableList(
                appendedList));
    }

    @Override
    public void onBroadcastChanged(int requestCode, int position, Broadcast newBroadcast) {
        position = mFrodoBroadcastPositionMap.get(position, -1);
        if (position == -1) {
            // We haven't merged it yet.
            return;
        }
        mBroadcastList.set(position, newBroadcast);
        getListener().onBroadcastChanged(getRequestCode(), position, newBroadcast);
    }

    @Override
    public void onBroadcastRemoved(int requestCode, int position) {
        position = mFrodoBroadcastPositionMap.get(position, -1);
        if (position == -1) {
            // We haven't merged it yet.
            return;
        }
        mBroadcastList.remove(position);
        getListener().onBroadcastRemoved(getRequestCode(), position);
    }

    @Override
    public void onBroadcastWriteStarted(int requestCode, int position) {
        position = mFrodoBroadcastPositionMap.get(position, -1);
        if (position == -1) {
            // We haven't merged it yet.
            return;
        }
        getListener().onBroadcastWriteStarted(getRequestCode(), position);
    }

    @Override
    public void onBroadcastWriteFinished(int requestCode, int position) {
        position = mFrodoBroadcastPositionMap.get(position, -1);
        if (position == -1) {
            // We haven't merged it yet.
            return;
        }
        getListener().onBroadcastWriteFinished(getRequestCode(), position);
    }

    private void rebuildBroadcastList() {
        if (mBroadcastList == null) {
            mBroadcastList = new ArrayList<>();
        } else {
            mBroadcastList.clear();
        }
        mFrodoBroadcastPositionMap.clear();
        List<Broadcast> frodoBroadcastList = mFrodoResource.get();
        List<me.zhanghai.android.douya.network.api.info.apiv2.Broadcast> apiV2BroadcastList =
                mApiV2Resource.get();
        int apiV2Index = 0;
        for (int frodoIndex = 0; frodoIndex < frodoBroadcastList.size()
                && apiV2Index < apiV2BroadcastList.size(); ) {
            Broadcast frodoBroadcast = frodoBroadcastList.get(frodoIndex);
            long frodoBroadcastId = frodoBroadcast.id;
            me.zhanghai.android.douya.network.api.info.apiv2.Broadcast apiV2Broadcast =
                    apiV2BroadcastList.get(apiV2Index);
            long apiV2BroadcastId = apiV2Broadcast.id;
            if (frodoBroadcastId == apiV2BroadcastId) {
                mFrodoBroadcastPositionMap.put(frodoIndex, mBroadcastList.size());
                mBroadcastList.add(frodoBroadcast);
                ++frodoIndex;
                ++apiV2Index;
            } else if (frodoBroadcastId > apiV2BroadcastId) {
                mFrodoBroadcastPositionMap.put(frodoIndex, mBroadcastList.size());
                mBroadcastList.add(frodoBroadcast);
                ++frodoIndex;
            } else {
                mBroadcastList.add(apiV2Broadcast.toFrodo());
                ++apiV2Index;
            }
        }
        if (!mFrodoResource.canLoadMore()) {
            for (; apiV2Index < apiV2BroadcastList.size(); ++apiV2Index) {
                mBroadcastList.add(apiV2BroadcastList.get(apiV2Index).toFrodo());
            }
        }
    }

    protected void setAndNotifyListener(List<Broadcast> broadcastList) {
        mBroadcastList = broadcastList;
        getListener().onBroadcastListChanged(getRequestCode(), Collections.unmodifiableList(
                mBroadcastList));
    }

    private TimelineBroadcastListResource.Listener getListener() {
        return (TimelineBroadcastListResource.Listener) getTarget();
    }
}
