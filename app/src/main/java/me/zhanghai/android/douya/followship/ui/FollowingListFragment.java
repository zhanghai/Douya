/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.followship.ui;

import me.zhanghai.android.douya.followship.content.FollowingListResource;
import me.zhanghai.android.douya.user.content.BaseUserListResource;

public class FollowingListFragment extends FollowshipListFragment {

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
    protected BaseUserListResource<?> onAttachUserListResource() {
        return FollowingListResource.attachTo(getUserIdOrUid(), this);
    }
}
