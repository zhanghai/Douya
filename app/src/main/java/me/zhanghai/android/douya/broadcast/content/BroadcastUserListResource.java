/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.broadcast.content;

import android.os.Bundle;

import me.zhanghai.android.douya.user.content.RawUserListResource;
import me.zhanghai.android.douya.util.FragmentUtils;

public abstract class BroadcastUserListResource extends RawUserListResource {

    // Not static because we are to be subclassed.
    private final String KEY_PREFIX = getClass().getName() + '.';

    private final String EXTRA_BROADCAST_ID = KEY_PREFIX + "broadcast_id";

    private long mBroadcastId;

    protected BroadcastUserListResource setArguments(long broadcastId) {
        FragmentUtils.getArgumentsBuilder(this)
                .putLong(EXTRA_BROADCAST_ID, broadcastId);
        return this;
    }

    protected long getBroadcastId() {
        return mBroadcastId;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBroadcastId = getArguments().getLong(EXTRA_BROADCAST_ID);
    }
}
