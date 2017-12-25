/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.broadcast.content;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Collections;
import java.util.List;

import me.zhanghai.android.douya.content.MoreBaseListResourceFragment;
import me.zhanghai.android.douya.eventbus.BroadcastDeletedEvent;
import me.zhanghai.android.douya.eventbus.BroadcastUpdatedEvent;
import me.zhanghai.android.douya.eventbus.EventBusUtils;
import me.zhanghai.android.douya.network.api.ApiError;
import me.zhanghai.android.douya.network.api.info.frodo.Broadcast;
import me.zhanghai.android.douya.network.api.info.frodo.BroadcastList;

public abstract class BaseBroadcastListResource
        extends MoreBaseListResourceFragment<BroadcastList, Broadcast> {

    @Override
    protected void onLoadStarted() {
        getListener().onLoadBroadcastListStarted(getRequestCode());
    }

    @Override
    protected void onLoadFinished(boolean more, int count, boolean successful,
                                  List<Broadcast> response, ApiError error) {
        getListener().onLoadBroadcastListFinished(getRequestCode());
        if (successful) {
            if (more) {
                append(response);
                getListener().onBroadcastListAppended(getRequestCode(),
                        Collections.unmodifiableList(response));
            } else {
                set(response);
                getListener().onBroadcastListChanged(getRequestCode(),
                        Collections.unmodifiableList(get()));
            }
            for (Broadcast Broadcast : response) {
                EventBusUtils.postAsync(new BroadcastUpdatedEvent(Broadcast, this));
            }
        } else {
            getListener().onLoadBroadcastListError(getRequestCode(), error);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBroadcastUpdated(BroadcastUpdatedEvent event) {

        if (event.isFromMyself(this) || isEmpty()) {
            return;
        }

        List<Broadcast> BroadcastList = get();
        for (int i = 0, size = BroadcastList.size(); i < size; ++i) {
            Broadcast Broadcast = BroadcastList.get(i);
            if (Broadcast.id == event.broadcast.id) {
                BroadcastList.set(i, event.broadcast);
                getListener().onBroadcastChanged(getRequestCode(), i, BroadcastList.get(i));
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBroadcastDeleted(BroadcastDeletedEvent event) {

        if (event.isFromMyself(this) || isEmpty()) {
            return;
        }

        List<Broadcast> BroadcastList = get();
        for (int i = 0, size = BroadcastList.size(); i < size; ) {
            Broadcast Broadcast = BroadcastList.get(i);
            if (Broadcast.id == event.broadcastId) {
                BroadcastList.remove(i);
                getListener().onBroadcastRemoved(getRequestCode(), i);
                --size;
            } else {
                ++i;
            }
        }
    }

    private Listener getListener() {
        return (Listener) getTarget();
    }

    public interface Listener {
        void onLoadBroadcastListStarted(int requestCode);
        void onLoadBroadcastListFinished(int requestCode);
        void onLoadBroadcastListError(int requestCode, ApiError error);
        /**
         * @param newBroadcastList Unmodifiable.
         */
        void onBroadcastListChanged(int requestCode, List<Broadcast> newBroadcastList);
        /**
         * @param appendedBroadcastList Unmodifiable.
         */
        void onBroadcastListAppended(int requestCode, List<Broadcast> appendedBroadcastList);
        void onBroadcastChanged(int requestCode, int position, Broadcast newBroadcast);
        void onBroadcastRemoved(int requestCode, int position);
    }
}
