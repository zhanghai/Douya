/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.broadcast.ui;

import java.util.List;

import me.zhanghai.android.douya.broadcast.content.BroadcastRebroadcasterListResource;
import me.zhanghai.android.douya.network.api.info.apiv2.Broadcast;
import me.zhanghai.android.douya.network.api.info.apiv2.User;
import me.zhanghai.android.douya.user.content.BaseUserListResource;

public class BroadcastRebroadcasterListFragment extends BroadcastUserListFragment {

    /**
     * @deprecated Use {@link #newInstance(Broadcast)} instead.
     */
    public BroadcastRebroadcasterListFragment() {}

    public static BroadcastRebroadcasterListFragment newInstance(Broadcast broadcast) {
        //noinspection deprecation
        BroadcastRebroadcasterListFragment fragment = new BroadcastRebroadcasterListFragment();
        fragment.setArguments(broadcast);
        return fragment;
    }

    @Override
    protected BaseUserListResource onAttachUserListResource() {
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
