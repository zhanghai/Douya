/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.broadcast.ui;

import java.util.List;

import me.zhanghai.android.douya.broadcast.content.BroadcastRebroadcasterListResource;
import me.zhanghai.android.douya.network.api.info.Broadcast;
import me.zhanghai.android.douya.network.api.info.User;
import me.zhanghai.android.douya.user.content.UserListResource;

public class BroadcastRebroadcastersListFragment extends BroadcastUserListFragment {

    /**
     * @deprecated Use {@link #newInstance(Broadcast)} instead.
     */
    public BroadcastRebroadcastersListFragment() {}

    public static BroadcastRebroadcastersListFragment newInstance(Broadcast broadcast) {
        //noinspection deprecation
        return (BroadcastRebroadcastersListFragment) new BroadcastRebroadcastersListFragment()
                .setArguments(broadcast);
    }

    @Override
    protected UserListResource onAttachUserListResource() {
        return BroadcastRebroadcasterListResource.attachTo(getBroadcast().id, this);
    }

    @Override
    protected boolean onUpdateBroadcast(Broadcast broadcast, List<User> userList) {
        if (broadcast.rebroadcastCount < userList.size()) {
            broadcast.rebroadcastCount = userList.size();
            return true;
        }
        return false;
    }
}
