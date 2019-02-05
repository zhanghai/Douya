/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.broadcast.content;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import java.util.List;

import me.zhanghai.android.douya.app.TargetedRetainedFragment;
import me.zhanghai.android.douya.network.api.ApiError;
import me.zhanghai.android.douya.network.api.info.frodo.Broadcast;
import me.zhanghai.android.douya.network.api.info.frodo.Comment;
import me.zhanghai.android.douya.util.FragmentUtils;

public class BroadcastAndCommentListResource extends TargetedRetainedFragment
        implements BroadcastResource.Listener, BroadcastCommentListResource.Listener {

    private static final String KEY_PREFIX = BroadcastAndCommentListResource.class.getName() + '.';

    private static final String EXTRA_BROADCAST_ID = KEY_PREFIX + "broadcast_id";
    private static final String EXTRA_BROADCAST = KEY_PREFIX + "broadcast";

    private static final int BROADCAST_ID_INVALID = -1;

    private long mBroadcastId = BROADCAST_ID_INVALID;
    private Broadcast mBroadcast;

    private BroadcastResource mBroadcastResource;
    private BroadcastCommentListResource mCommentListResource;

    private static final String FRAGMENT_TAG_DEFAULT =
            BroadcastAndCommentListResource.class.getName();

    private static BroadcastAndCommentListResource newInstance(long broadcastId,
                                                               Broadcast broadcast) {
        //noinspection deprecation
        return new BroadcastAndCommentListResource().setArguments(broadcastId, broadcast);
    }

    public static BroadcastAndCommentListResource attachTo(long broadcastId, Broadcast broadcast,
                                                           Fragment fragment, String tag,
                                                           int requestCode) {
        FragmentActivity activity = fragment.getActivity();
        BroadcastAndCommentListResource instance = FragmentUtils.findByTag(activity, tag);
        if (instance == null) {
            instance = newInstance(broadcastId, broadcast);
            FragmentUtils.add(instance, activity, tag);
        }
        instance.setTarget(fragment, requestCode);
        return instance;
    }

    public static BroadcastAndCommentListResource attachTo(long broadcastId, Broadcast broadcast,
                                                           Fragment fragment) {
        return attachTo(broadcastId, broadcast, fragment, FRAGMENT_TAG_DEFAULT,
                REQUEST_CODE_INVALID);
    }

    /**
     * @deprecated Use {@code attachTo()} instead.
     */
    public BroadcastAndCommentListResource() {}

    protected BroadcastAndCommentListResource setArguments(long broadcastId, Broadcast broadcast) {
        FragmentUtils.getArgumentsBuilder(this)
                .putLong(EXTRA_BROADCAST_ID, broadcastId)
                .putParcelable(EXTRA_BROADCAST, broadcast);
        return this;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        ensureResourcesTarget();
    }

    private void ensureResourcesTarget() {
        if (mBroadcastResource != null) {
            mBroadcastResource.setTarget(this);
        }
        if (mCommentListResource != null) {
            mCommentListResource.setTarget(this);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ensureBroadcastAndIdFromArguments();

        mBroadcastResource = BroadcastResource.attachTo(mBroadcastId, mBroadcast, this);
        ensureCommentListResourceIfHasBroadcast();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        getArguments().putParcelable(EXTRA_BROADCAST, mBroadcast);
    }

    /**
     * @deprecated In most cases you may want to use {@link #getEffectiveBroadcastId()}.
     */
    public long getBroadcastId() {
        ensureBroadcastAndIdFromArguments();
        return mBroadcastId;
    }

    /**
     * @deprecated In most cases you may want to use {@link #getEffectiveBroadcast()}.
     */
    public Broadcast getBroadcast() {
        // Can be called before onCreate() is called.
        ensureBroadcastAndIdFromArguments();
        return mBroadcast;
    }

    /**
     * @deprecated In most cases you may want to use {@link #hasEffectiveBroadcast()}.
     */
    public boolean hasBroadcast() {
        // Can be called before onCreate() is called.
        ensureBroadcastAndIdFromArguments();
        return mBroadcast != null;
    }

    public boolean isEffectiveBroadcastId(long broadcastId) {
        // Can be called before onCreate() is called.
        ensureBroadcastAndIdFromArguments();
        return hasEffectiveBroadcast() && getEffectiveBroadcastId() == broadcastId;
    }

    public long getEffectiveBroadcastId() {
        // Can be called before onCreate() is called.
        ensureBroadcastAndIdFromArguments();
        if (mBroadcast == null) {
            throw new IllegalStateException("getEffectiveBroadcastId() called when broadcast is" +
                    " not yet loaded");
        }
        return getEffectiveBroadcast().id;
    }

    public Broadcast getEffectiveBroadcast() {
        // Can be called before onCreate() is called.
        ensureBroadcastAndIdFromArguments();
        return mBroadcast != null ? mBroadcast.getEffectiveBroadcast() : null;
    }

    public boolean hasEffectiveBroadcast() {
        return getEffectiveBroadcast() != null;
    }

    public boolean isLoadingBroadcast() {
        return mBroadcastResource == null || mBroadcastResource.isLoading();
    }

    public List<Comment> getCommentList() {
        return mCommentListResource != null ? mCommentListResource.get() : null;
    }

    public boolean isCommentListEmpty() {
        return mCommentListResource == null || mCommentListResource.isEmpty();
    }

    public boolean isLoadingCommentList() {
        return mCommentListResource == null || mCommentListResource.isLoading();
    }

    public boolean isLoadingMoreCommentList() {
        return mCommentListResource != null && mCommentListResource.isLoadingMore();
    }

    private void ensureBroadcastAndIdFromArguments() {
        if (mBroadcastId == BROADCAST_ID_INVALID) {
            Bundle arguments = getArguments();
            mBroadcast = arguments.getParcelable(EXTRA_BROADCAST);
            if (mBroadcast != null) {
                mBroadcastId = mBroadcast.id;
            } else {
                mBroadcastId = arguments.getLong(EXTRA_BROADCAST_ID);
            }
        }
    }

    public void loadBroadcast() {
        if (mBroadcastResource != null) {
            mBroadcastResource.load();
        }
    }

    public void loadCommentList(boolean loadMore) {
        if (mCommentListResource != null) {
            mCommentListResource.load(loadMore);
        }
    }

    @Override
    public void onLoadBroadcastStarted(int requestCode) {
        getListener().onLoadBroadcastStarted(getRequestCode());
    }

    @Override
    public void onLoadBroadcastFinished(int requestCode) {
        getListener().onLoadBroadcastFinished(getRequestCode());
    }

    @Override
    public void onLoadBroadcastError(int requestCode, ApiError error) {
        getListener().onLoadBroadcastError(getRequestCode(), error);
    }

    @Override
    public void onBroadcastChanged(int requestCode, Broadcast newBroadcast) {
        mBroadcast = newBroadcast;
        getListener().onBroadcastChanged(getRequestCode(), newBroadcast);
        ensureCommentListResourceIfHasBroadcast();
    }

    @Override
    public void onBroadcastRemoved(int requestCode) {
        mBroadcast = null;
        getListener().onBroadcastRemoved(getRequestCode());
    }

    @Override
    public void onBroadcastWriteStarted(int requestCode) {
        getListener().onBroadcastWriteStarted(getRequestCode());
    }

    @Override
    public void onBroadcastWriteFinished(int requestCode) {
        getListener().onBroadcastWriteFinished(getRequestCode());
    }

    private void ensureCommentListResourceIfHasBroadcast() {
        if (mCommentListResource != null) {
            return;
        }
        if (mBroadcast == null) {
            return;
        }
        mCommentListResource = BroadcastCommentListResource.attachTo(
                mBroadcast.getEffectiveBroadcastId(), this);
    }

    @Override
    public void onLoadCommentListStarted(int requestCode) {
        getListener().onLoadCommentListStarted(getRequestCode());
    }

    @Override
    public void onLoadCommentListFinished(int requestCode) {
        getListener().onLoadCommentListFinished(getRequestCode());
    }

    @Override
    public void onLoadCommentListError(int requestCode, final ApiError error) {
        getListener().onLoadCommentListError(getRequestCode(), error);
    }

    @Override
    public void onCommentListChanged(int requestCode, List<Comment> newCommentList) {
        getListener().onCommentListChanged(getRequestCode(), newCommentList);
    }

    @Override
    public void onCommentListAppended(int requestCode, List<Comment> appendedCommentList) {
        getListener().onCommentListAppended(getRequestCode(), appendedCommentList);
    }

    @Override
    public void onCommentRemoved(int requestCode, int position) {
        getListener().onCommentRemoved(getRequestCode(), position);
    }

    private Listener getListener() {
        return (Listener) getTarget();
    }

    public interface Listener extends BroadcastResource.Listener,
            BroadcastCommentListResource.Listener {}
}
