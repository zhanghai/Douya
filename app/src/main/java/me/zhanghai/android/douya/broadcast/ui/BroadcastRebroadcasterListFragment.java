/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.broadcast.ui;

import java.util.List;

import me.zhanghai.android.douya.broadcast.content.BroadcastRebroadcasterListResource;
import me.zhanghai.android.douya.network.api.info.apiv2.Broadcast;
import me.zhanghai.android.douya.network.api.info.apiv2.SimpleUser;
import me.zhanghai.android.douya.user.content.BaseUserListResource;

public class BroadcastRebroadcasterListFragment extends BroadcastUserListFragment {

    public static BroadcastRebroadcasterListFragment newInstance(Broadcast broadcast) {
        //noinspection deprecation
        return new BroadcastRebroadcasterListFragment().setArguments(broadcast);
    }

    /**
     * @deprecated Use {@link #newInstance(Broadcast)} instead.
     */
    public BroadcastRebroadcasterListFragment() {}

    @Override
    protected BroadcastRebroadcasterListFragment setArguments(Broadcast broadcast) {
        super.setArguments(broadcast);
        return this;
    }

    @Override
    protected BaseUserListResource<?> onAttachUserListResource() {
        return BroadcastRebroadcasterListResource.attachTo(getBroadcast().id, this);
    }

    @Override
    protected boolean onUpdateBroadcast(Broadcast broadcast, List<SimpleUser> userList) {
        if (broadcast.rebroadcastCount < userList.size()) {
            broadcast.rebroadcastCount = userList.size();
            return true;
        }
        return false;
    }
}
