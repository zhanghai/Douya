/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.broadcast.content;

import me.zhanghai.android.douya.user.content.RawUserListResource;
import me.zhanghai.android.douya.util.FragmentUtils;

public abstract class BroadcastUserListResource extends RawUserListResource {

    // Not static because we are to be subclassed.
    private final String KEY_PREFIX = getClass().getName() + '.';

    public final String EXTRA_BROADCAST_ID = KEY_PREFIX + "broadcast_id";

    protected void setArguments(long broadcastId) {
        FragmentUtils.ensureArguments(this)
                .putLong(EXTRA_BROADCAST_ID, broadcastId);
    }

    protected long getBroadcastId() {
        return getArguments().getLong(EXTRA_BROADCAST_ID);
    }
}
