/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.eventbus;

import me.zhanghai.android.douya.network.api.info.frodo.Broadcast;

public class BroadcastUpdatedEvent extends Event {

    /**
     * @deprecated Use {@link #update(Broadcast, Object)} or
     * {@link #update(long, Broadcast, Object)} instead.
     */
    public Broadcast broadcast;

    public BroadcastUpdatedEvent(Broadcast broadcast, Object source) {
        super(source);

        //noinspection deprecation
        this.broadcast = broadcast;
    }

    @SuppressWarnings("deprecation")
    private void mergeAndRepost(Broadcast oldBroadcast, Object source) {
        boolean changed = false;
        if (broadcast.parentBroadcast == null && oldBroadcast.parentBroadcast != null) {
            broadcast.parentBroadcast = oldBroadcast.parentBroadcast;
            //noinspection deprecation
            broadcast.parentBroadcastId = null;
            changed = true;
        }
        if (broadcast.rebroadcastedBroadcast == null
                && oldBroadcast.rebroadcastedBroadcast != null) {
            broadcast.rebroadcastedBroadcast = oldBroadcast.rebroadcastedBroadcast;
            changed = true;
        }
        if (changed) {
            EventBusUtils.cancel(this);
            EventBusUtils.postAsync(new BroadcastUpdatedEvent(this.broadcast, source));
        }
    }

    @SuppressWarnings("deprecation")
    public Broadcast update(Broadcast oldBroadcast, Object source) {
        if (oldBroadcast.id == broadcast.id) {
            mergeAndRepost(oldBroadcast, source);
            return broadcast;
        } else if (oldBroadcast.parentBroadcast != null
                && oldBroadcast.parentBroadcast.id == broadcast.id) {
            mergeAndRepost(oldBroadcast.parentBroadcast, source);
            oldBroadcast.parentBroadcast = broadcast;
            return oldBroadcast;
        } else if (oldBroadcast.rebroadcastedBroadcast != null
                && oldBroadcast.rebroadcastedBroadcast.id == broadcast.id) {
            mergeAndRepost(oldBroadcast.rebroadcastedBroadcast, source);
            oldBroadcast.rebroadcastedBroadcast = broadcast;
            return oldBroadcast;
        } else {
            return null;
        }
    }

    @SuppressWarnings("deprecation")
    public Broadcast update(long broadcastId, Broadcast oldBroadcast, Object source) {
        if (oldBroadcast != null) {
            return update(oldBroadcast, source);
        } else if (broadcastId == broadcast.id) {
            return broadcast;
        } else {
            return null;
        }
    }
}
