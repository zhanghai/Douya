/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.broadcast.content;

import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.android.volley.VolleyError;

import java.util.Collections;
import java.util.List;

import de.greenrobot.event.EventBus;
import me.zhanghai.android.douya.content.ResourceFragment;
import me.zhanghai.android.douya.eventbus.BroadcastDeletedEvent;
import me.zhanghai.android.douya.eventbus.BroadcastUpdatedEvent;
import me.zhanghai.android.douya.network.RequestFragment;
import me.zhanghai.android.douya.network.api.ApiRequest;
import me.zhanghai.android.douya.network.api.ApiRequests;
import me.zhanghai.android.douya.network.api.info.Broadcast;

public class BroadcastListResource extends ResourceFragment
        implements RequestFragment.Listener<List<Broadcast>, BroadcastListResource.State> {

    private static final int DEFAULT_COUNT_PER_LOAD = 20;

    // Not static because we are to be subclassed.
    private final String KEY_PREFIX = getClass().getName() + '.';

    public final String EXTRA_USER_ID_OR_UID = KEY_PREFIX + "user_id_or_uid";
    public final String EXTRA_TOPIC = KEY_PREFIX + "topic";

    private List<Broadcast> mBroadcastList;

    private boolean mCanLoadMore = true;
    private boolean mLoading;

    private static final String FRAGMENT_TAG_DEFAULT = BroadcastListResource.class.getName();

    private static BroadcastListResource newInstance(String userIdOrUid, String topic) {
        //noinspection deprecation
        BroadcastListResource resource = new BroadcastListResource();
        resource.setArguments(userIdOrUid, topic);
        return resource;
    }

    public static BroadcastListResource attachTo(String userIdOrUid, String topic,
                                                 FragmentActivity activity, String tag,
                                                 int requestCode) {
        return attachTo(userIdOrUid, topic, activity, tag, true, null, requestCode);
    }

    public static BroadcastListResource attachTo(String userIdOrUid, String topic,
                                                 FragmentActivity activity) {
        return attachTo(userIdOrUid, topic, activity, FRAGMENT_TAG_DEFAULT, REQUEST_CODE_INVALID);
    }

    public static BroadcastListResource attachTo(String userIdOrUid, String topic,
                                                 Fragment fragment, String tag, int requestCode) {
        return attachTo(userIdOrUid, topic, fragment.getActivity(), tag, false, fragment,
                requestCode);
    }

    public static BroadcastListResource attachTo(String userIdOrUid, String topic,
                                                 Fragment fragment) {
        return attachTo(userIdOrUid, topic, fragment, FRAGMENT_TAG_DEFAULT, REQUEST_CODE_INVALID);
    }

    private static BroadcastListResource attachTo(String userIdOrUid, String topic,
                                                  FragmentActivity activity, String tag,
                                                  boolean targetAtActivity, Fragment targetFragment,
                                                  int requestCode) {
        BroadcastListResource resource = findByTag(activity, tag);
        if (resource == null) {
            resource = newInstance(userIdOrUid, topic);
            if (targetAtActivity) {
                resource.targetAtActivity(requestCode);
            } else {
                resource.targetAtFragment(targetFragment, requestCode);
            }
            resource.addTo(activity, tag);
        }
        return resource;
    }

    /**
     * @deprecated Use {@code attachTo()} instead.
     */
    public BroadcastListResource() {}

    protected void setArguments(String userIdOrUid, String topic) {
        Bundle arguments = new Bundle();
        arguments.putString(EXTRA_USER_ID_OR_UID, userIdOrUid);
        arguments.putString(EXTRA_TOPIC, topic);
        setArguments(arguments);
    }

    protected String getUserIdOrUid() {
        return getArguments().getString(EXTRA_USER_ID_OR_UID);
    }

    protected String getTopic() {
        return getArguments().getString(EXTRA_TOPIC);
    }

    /**
     * @return Unmodifiable broadcast list, or {@code null}.
     */
    public List<Broadcast> get() {
        return mBroadcastList != null ? Collections.unmodifiableList(mBroadcastList) : null;
    }

    @Override
    public void onStart() {
        super.onStart();

        EventBus.getDefault().register(this);

        if (mBroadcastList == null || (mBroadcastList.isEmpty() && mCanLoadMore)) {
            loadOnStart();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        EventBus.getDefault().unregister(this);
    }

    public void load(boolean loadMore, int count) {

        if (mLoading || (loadMore && !mCanLoadMore)) {
            return;
        }

        mLoading = true;
        getListener().onLoadBroadcastList(getRequestCode(), loadMore);

        Long untilId = null;
        if (loadMore && mBroadcastList != null) {
            int size = mBroadcastList.size();
            if (size > 0) {
                untilId = mBroadcastList.get(size - 1).id;
            }
        }
        ApiRequest<List<Broadcast>> request = ApiRequests.newBroadcastListRequest(getUserIdOrUid(),
                getTopic(), untilId, count, getActivity());
        State state = new State(loadMore, count);
        RequestFragment.startRequest(request, state, this);
    }

    public void load(boolean loadMore) {
        load(loadMore, DEFAULT_COUNT_PER_LOAD);
    }

    protected void loadOnStart() {
        load(false);
    }

    @Override
    public void onVolleyResponse(int requestCode, boolean successful, List<Broadcast> result,
                                 VolleyError error, State requestState) {
        onLoadComplete(successful, result, error, requestState.loadMore, requestState.count);
    }

    private void onLoadComplete(boolean successful, List<Broadcast> broadcastList,
                                VolleyError error, boolean loadMore, int count) {

        mLoading = false;
        getListener().onLoadBroadcastListComplete(getRequestCode(), loadMore);

        if (successful) {
            mCanLoadMore = broadcastList.size() == count;
            if (loadMore) {
                mBroadcastList.addAll(broadcastList);
                getListener().onBroadcastListAppended(getRequestCode(),
                        Collections.unmodifiableList(broadcastList));
            } else {
                set(broadcastList);
            }
        } else {
            getListener().onLoadBroadcastListError(getRequestCode(), error);
        }
    }

    @Keep
    public void onEventMainThread(BroadcastUpdatedEvent event) {

        if (mBroadcastList == null) {
            return;
        }

        Broadcast updatedBroadcast = event.broadcast;
        for (int i = 0, size = mBroadcastList.size(); i < size; ++i) {
            Broadcast broadcast = mBroadcastList.get(i);
            boolean changed = false;
            if (broadcast.id == updatedBroadcast.id) {
                mBroadcastList.set(i, updatedBroadcast);
                changed = true;
            } else if (broadcast.rebroadcastedBroadcast != null
                    && broadcast.rebroadcastedBroadcast.id == updatedBroadcast.id) {
                broadcast.rebroadcastedBroadcast = updatedBroadcast;
                changed = true;
            }
            if (changed) {
                getListener().onBroadcastChanged(getRequestCode(), i, mBroadcastList.get(i));
            }
        }
    }

    @Keep
    public void onEventMainThread(BroadcastDeletedEvent event) {

        if (mBroadcastList == null) {
            return;
        }

        long removedBroadcastId = event.broadcastId;
        for (int i = 0, size = mBroadcastList.size(); i < size; ) {
            Broadcast broadcast = mBroadcastList.get(i);
            if (broadcast.id == removedBroadcastId
                    || (broadcast.rebroadcastedBroadcast != null
                        && broadcast.rebroadcastedBroadcast.id == removedBroadcastId)) {
                mBroadcastList.remove(i);
                getListener().onBroadcastRemoved(getRequestCode(), i);
                --size;
            } else {
                ++i;
            }
        }
    }

    protected void setLoading(boolean loading) {
        mLoading = loading;
    }

    protected void set(List<Broadcast> broadcastList) {
        mBroadcastList = broadcastList;
        getListener().onBroadcastListChanged(getRequestCode(),
                Collections.unmodifiableList(broadcastList));
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
        void onLoadBroadcastList(int requestCode, boolean loadMore);
        void onLoadBroadcastListComplete(int requestCode, boolean loadMore);
        /**
         * @param appendedBroadcastList Unmodifiable.
         */
        void onBroadcastListAppended(int requestCode, List<Broadcast> appendedBroadcastList);
        /**
         * @param newBroadcastList Unmodifiable.
         */
        void onBroadcastListChanged(int requestCode, List<Broadcast> newBroadcastList);
        void onLoadBroadcastListError(int requestCode, VolleyError error);
        void onBroadcastChanged(int requestCode, int position, Broadcast newBroadcast);
        void onBroadcastRemoved(int requestCode, int position);
    }
}
