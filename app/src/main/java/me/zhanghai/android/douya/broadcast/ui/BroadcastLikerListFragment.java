/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.broadcast.ui;

import java.util.List;

import me.zhanghai.android.douya.broadcast.content.BroadcastLikerListResource;
import me.zhanghai.android.douya.content.MoreListResourceFragment;
import me.zhanghai.android.douya.network.api.info.frodo.Broadcast;
import me.zhanghai.android.douya.network.api.info.frodo.SimpleUser;

public class BroadcastLikerListFragment extends BroadcastUserListFragment {

    public static BroadcastLikerListFragment newInstance(Broadcast broadcast) {
        //noinspection deprecation
        return new BroadcastLikerListFragment().setArguments(broadcast);
    }

    /**
     * @deprecated Use {@link #newInstance(Broadcast)} instead.
     */
    public BroadcastLikerListFragment() {}

    @Override
    protected BroadcastLikerListFragment setArguments(Broadcast broadcast) {
        super.setArguments(broadcast);

        return this;
    }

    @Override
    protected MoreListResourceFragment<?, List<SimpleUser>> onAttachResource() {
        return BroadcastLikerListResource.attachTo(getBroadcast().id, this);
    }

    @Override
    protected boolean onUpdateBroadcast(Broadcast broadcast, List<SimpleUser> userList) {
        if (broadcast.likeCount < userList.size()) {
            broadcast.likeCount = userList.size();
            return true;
        }
        return false;
    }
}
