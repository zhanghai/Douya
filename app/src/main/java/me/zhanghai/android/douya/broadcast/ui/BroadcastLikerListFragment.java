/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.broadcast.ui;

import java.util.List;

import me.zhanghai.android.douya.broadcast.content.BroadcastLikerListResource;
import me.zhanghai.android.douya.network.api.info.apiv2.Broadcast;
import me.zhanghai.android.douya.network.api.info.apiv2.User;
import me.zhanghai.android.douya.user.content.BaseUserListResource;

public class BroadcastLikerListFragment extends BroadcastUserListFragment {

    /**
     * @deprecated Use {@link #newInstance(Broadcast)} instead.
     */
    public BroadcastLikerListFragment() {}

    public static BroadcastLikerListFragment newInstance(Broadcast broadcast) {
        //noinspection deprecation
        BroadcastLikerListFragment fragment = new BroadcastLikerListFragment();
        fragment.setArguments(broadcast);
        return fragment;
    }

    @Override
    protected BaseUserListResource onAttachUserListResource() {
        return BroadcastLikerListResource.attachTo(getBroadcast().id, this);
    }

    @Override
    protected boolean onUpdateBroadcast(Broadcast broadcast, List<User> userList) {
        if (broadcast.likeCount < userList.size()) {
            broadcast.likeCount = userList.size();
            return true;
        }
        return false;
    }
}
