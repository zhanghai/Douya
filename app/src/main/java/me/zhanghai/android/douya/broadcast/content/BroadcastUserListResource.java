/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.broadcast.content;

import android.os.Bundle;

import me.zhanghai.android.douya.user.content.UserListResource;

public abstract class BroadcastUserListResource extends UserListResource {

    // Not static because we are to be subclassed.
    private final String KEY_PREFIX = getClass().getName() + '.';

    public final String EXTRA_BROADCAST_ID = KEY_PREFIX + "broadcast_id";

    protected void setArguments(long broadcastId) {
        Bundle arguments = new Bundle();
        arguments.putLong(EXTRA_BROADCAST_ID, broadcastId);
        setArguments(arguments);
    }

    protected long getBroadcastId() {
        return getArguments().getLong(EXTRA_BROADCAST_ID);
    }
}
