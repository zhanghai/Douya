/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.followship.ui;

import java.util.List;

import me.zhanghai.android.douya.content.MoreListResourceFragment;
import me.zhanghai.android.douya.followship.content.FollowingListResource;
import me.zhanghai.android.douya.network.api.info.frodo.SimpleUser;

public class FollowingListFragment extends FollowshipUserListFragment {

    public static FollowingListFragment newInstance(String userIdOrUid) {
        //noinspection deprecation
        return new FollowingListFragment().setArguments(userIdOrUid);
    }

    /**
     * @deprecated Use {@link #newInstance(String)} instead.
     */
    public FollowingListFragment() {}

    @Override
    protected FollowingListFragment setArguments(String userIdOrUid) {
        super.setArguments(userIdOrUid);

        return this;
    }

    @Override
    protected MoreListResourceFragment<?, List<SimpleUser>> onAttachResource() {
        return FollowingListResource.attachTo(getUserIdOrUid(), this);
    }
}
