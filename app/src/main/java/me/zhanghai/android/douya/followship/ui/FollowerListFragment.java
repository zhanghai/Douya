/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.followship.ui;

import java.util.List;

import me.zhanghai.android.douya.content.MoreListResourceFragment;
import me.zhanghai.android.douya.followship.content.FollowerListResource;
import me.zhanghai.android.douya.network.api.info.frodo.SimpleUser;

public class FollowerListFragment extends FollowshipUserListFragment {

    public static FollowerListFragment newInstance(String userIdOrUid) {
        //noinspection deprecation
        return new FollowerListFragment().setArguments(userIdOrUid);
    }

    /**
     * @deprecated Use {@link #newInstance(String)} instead.
     */
    public FollowerListFragment() {}

    @Override
    protected FollowerListFragment setArguments(String userIdOrUid) {
        super.setArguments(userIdOrUid);

        return this;
    }

    @Override
    protected MoreListResourceFragment<?, List<SimpleUser>> onAttachResource() {
        return FollowerListResource.attachTo(getUserIdOrUid(), this);
    }
}
