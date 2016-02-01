/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.broadcast.ui;

import android.os.Bundle;
import android.support.annotation.Keep;

import java.util.List;

import de.greenrobot.event.EventBus;
import me.zhanghai.android.douya.eventbus.BroadcastUpdatedEvent;
import me.zhanghai.android.douya.network.api.info.Broadcast;
import me.zhanghai.android.douya.network.api.info.User;
import me.zhanghai.android.douya.user.ui.UserListFragment;

public abstract class BroadcastUserListFragment extends UserListFragment {

    private final String KEY_PREFIX = getClass().getName() + '.';

    public final String EXTRA_BROADCAST = KEY_PREFIX + "broadcast";

    private Broadcast mBroadcast;

    protected BroadcastUserListFragment setArguments(Broadcast broadcast) {
        Bundle arguments = new Bundle();
        arguments.putParcelable(EXTRA_BROADCAST, broadcast);
        setArguments(arguments);
        return this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBroadcast = getArguments().getParcelable(EXTRA_BROADCAST);
    }

    @Override
    public void onStart(){
        super.onStart();

        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();

        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onUserListUpdated(List<User> userList) {
        if (onUpdateBroadcast(mBroadcast, userList)) {
            EventBus.getDefault().post(new BroadcastUpdatedEvent(mBroadcast));
        }
    }

    protected abstract boolean onUpdateBroadcast(Broadcast broadcast, List<User> userList);

    @Keep
    public void onEventMainThread(BroadcastUpdatedEvent event) {
        Broadcast broadcast = event.broadcast;
        if (broadcast.id == mBroadcast.id) {
            mBroadcast = broadcast;
        }
    }

    public Broadcast getBroadcast() {
        return mBroadcast;
    }
}
