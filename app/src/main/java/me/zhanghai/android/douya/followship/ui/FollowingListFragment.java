/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.followship.ui;

import me.zhanghai.android.douya.followship.content.FollowingListResource;
import me.zhanghai.android.douya.user.content.BaseUserListResource;

public class FollowingListFragment extends FollowshipListFragment {

    /**
     * @deprecated Use {@link #newInstance(String)} instead.
     */
    public FollowingListFragment() {}

    public static FollowingListFragment newInstance(String userIdOrUid) {
        //noinspection deprecation
        FollowingListFragment fragment = new FollowingListFragment();
        fragment.setArguments(userIdOrUid);
        return fragment;
    }

    @Override
    protected BaseUserListResource onAttachUserListResource() {
        return FollowingListResource.attachTo(getUserIdOrUid(), this);
    }
}
