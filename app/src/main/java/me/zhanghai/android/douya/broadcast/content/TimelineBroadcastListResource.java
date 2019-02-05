/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.broadcast.content;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Collections;
import java.util.List;

import me.zhanghai.android.douya.content.MoreRawListResourceFragment;
import me.zhanghai.android.douya.eventbus.BroadcastDeletedEvent;
import me.zhanghai.android.douya.eventbus.BroadcastUpdatedEvent;
import me.zhanghai.android.douya.eventbus.BroadcastWriteFinishedEvent;
import me.zhanghai.android.douya.eventbus.BroadcastWriteStartedEvent;
import me.zhanghai.android.douya.eventbus.EventBusUtils;
import me.zhanghai.android.douya.network.api.ApiError;
import me.zhanghai.android.douya.network.api.ApiRequest;
import me.zhanghai.android.douya.network.api.ApiService;
import me.zhanghai.android.douya.network.api.info.frodo.Broadcast;
import me.zhanghai.android.douya.network.api.info.frodo.TimelineList;
import me.zhanghai.android.douya.util.FragmentUtils;

public class TimelineBroadcastListResource
        extends MoreRawListResourceFragment<TimelineList, Broadcast> {

    // Not static because we are to be subclassed.
    private final String KEY_PREFIX = getClass().getName() + '.';

    private final String EXTRA_USER_ID_OR_UID = KEY_PREFIX + "user_id_or_uid";
    private final String EXTRA_TOPIC = KEY_PREFIX + "topic";

    private String mUserIdOrUid;
    private String mTopic;

    private static final String FRAGMENT_TAG_DEFAULT =
            TimelineBroadcastListResource.class.getName();

    private static TimelineBroadcastListResource newInstance(String userIdOrUid, String topic) {
        //noinspection deprecation
        return new TimelineBroadcastListResource().setArguments(userIdOrUid, topic);
    }

    public static TimelineBroadcastListResource attachTo(String userIdOrUid, String topic,
                                                         Fragment fragment, String tag,
                                                         int requestCode) {
        FragmentActivity activity = fragment.getActivity();
        TimelineBroadcastListResource instance = FragmentUtils.findByTag(activity, tag);
        if (instance == null) {
            instance = newInstance(userIdOrUid, topic);
            FragmentUtils.add(instance, activity, tag);
        }
        instance.setTarget(fragment, requestCode);
        return instance;
    }

    public static TimelineBroadcastListResource attachTo(String userIdOrUid, String topic,
                                                         Fragment fragment) {
        return attachTo(userIdOrUid, topic, fragment, FRAGMENT_TAG_DEFAULT, REQUEST_CODE_INVALID);
    }

    /**
     * @deprecated Use {@code attachTo()} instead.
     */
    public TimelineBroadcastListResource() {}

    protected TimelineBroadcastListResource setArguments(String userIdOrUid, String topic) {
        FragmentUtils.getArgumentsBuilder(this)
                .putString(EXTRA_USER_ID_OR_UID, userIdOrUid)
                .putString(EXTRA_TOPIC, topic);
        return this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        mUserIdOrUid = arguments.getString(EXTRA_USER_ID_OR_UID);
        mTopic = arguments.getString(EXTRA_TOPIC);
    }

    @Override
    protected ApiRequest<TimelineList> onCreateRequest(boolean more, int count) {
        Long untilId = null;
        if (more && has()) {
            List<Broadcast> broadcastList = get();
            int size = broadcastList.size();
            if (size > 0) {
                untilId = broadcastList.get(size - 1).id;
            }
        }
        return ApiService.getInstance().getTimelineList(mUserIdOrUid, mTopic, untilId, count);
    }

    @Override
    protected ApiRequest<TimelineList> onCreateRequest(Integer start, Integer count) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void onLoadStarted() {
        getListener().onLoadBroadcastListStarted(getRequestCode());
    }

    @Override
    protected void onLoadFinished(boolean more, int count, boolean successful,
                                  TimelineList response, ApiError error) {
        onLoadFinished(more, count, successful, successful ? response.toBroadcastList() : null,
                error);
    }

    private void onLoadFinished(boolean more, int count, boolean successful,
                                List<Broadcast> response, ApiError error) {
        if (successful) {
            if (more) {
                append(response);
                getListener().onLoadBroadcastListFinished(getRequestCode());
                getListener().onBroadcastListAppended(getRequestCode(),
                        Collections.unmodifiableList(response));
            } else {
                setAndNotifyListener(response, true);
            }
            for (Broadcast broadcast : response) {
                EventBusUtils.postAsync(new BroadcastUpdatedEvent(broadcast, this));
            }
            // Frodo API is sometimes buggy that broadcast list size may not be count. In this case,
            // we simply load more until no more broadcast is returned.
            setCanLoadMore(count == 0 || response.size() > 0);
        } else {
            getListener().onLoadBroadcastListFinished(getRequestCode());
            getListener().onLoadBroadcastListError(getRequestCode(), error);
        }
    }

    protected void setAndNotifyListener(List<Broadcast> broadcastList, boolean notifyFinished) {
        set(broadcastList);
        if (notifyFinished) {
            getListener().onLoadBroadcastListFinished(getRequestCode());
        }
        getListener().onBroadcastListChanged(getRequestCode(), Collections.unmodifiableList(get()));
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onBroadcastUpdated(BroadcastUpdatedEvent event) {

        if (event.isFromMyself(this) || isEmpty()) {
            return;
        }

        List<Broadcast> broadcastList = get();
        for (int i = 0, size = broadcastList.size(); i < size; ++i) {
            Broadcast updatedBroadcast = event.update(broadcastList.get(i), this);
            if (updatedBroadcast != null) {
                broadcastList.set(i, updatedBroadcast);
                getListener().onBroadcastChanged(getRequestCode(), i, updatedBroadcast);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onBroadcastDeleted(BroadcastDeletedEvent event) {

        if (event.isFromMyself(this) || isEmpty()) {
            return;
        }

        List<Broadcast> broadcastList = get();
        for (int i = 0, size = broadcastList.size(); i < size; ) {
            Broadcast broadcast = broadcastList.get(i);
            if (broadcast.id == event.broadcastId) {
                broadcastList.remove(i);
                getListener().onBroadcastRemoved(getRequestCode(), i);
                --size;
            } else {
                if (broadcast.parentBroadcast != null
                        && broadcast.parentBroadcast.id == event.broadcastId) {
                    // Same behavior as Frodo API.
                    // FIXME: Won't reach here if another list shares this broadcast instance.
                    broadcast.parentBroadcast = null;
                    getListener().onBroadcastChanged(getRequestCode(), i, broadcast);
                } else if (broadcast.rebroadcastedBroadcast != null
                        && broadcast.rebroadcastedBroadcast.id == event.broadcastId) {
                    broadcast.rebroadcastedBroadcast.isDeleted = true;
                    getListener().onBroadcastChanged(getRequestCode(), i, broadcast);
                }
                ++i;
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onBroadcastWriteStarted(BroadcastWriteStartedEvent event) {

        if (event.isFromMyself(this) || isEmpty()) {
            return;
        }

        List<Broadcast> broadcastList = get();
        for (int i = 0, size = broadcastList.size(); i < size; ++i) {
            Broadcast broadcast = broadcastList.get(i);
            if (broadcast.getEffectiveBroadcastId() == event.broadcastId) {
                getListener().onBroadcastWriteStarted(getRequestCode(), i);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onBroadcastWriteFinished(BroadcastWriteFinishedEvent event) {

        if (event.isFromMyself(this) || isEmpty()) {
            return;
        }

        List<Broadcast> broadcastList = get();
        for (int i = 0, size = broadcastList.size(); i < size; ++i) {
            Broadcast broadcast = broadcastList.get(i);
            if (broadcast.getEffectiveBroadcastId() == event.broadcastId) {
                getListener().onBroadcastWriteFinished(getRequestCode(), i);
            }
        }
    }

    private Listener getListener() {
        return (Listener) getTarget();
    }

    public interface Listener extends BaseBroadcastListResource.Listener {
        void onBroadcastWriteStarted(int requestCode, int position);
        void onBroadcastWriteFinished(int requestCode, int position);
    }
}
