/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.followship.ui;

import me.zhanghai.android.douya.followship.content.FollowerListResource;
import me.zhanghai.android.douya.user.content.BaseUserListResource;

public class FollowerListFragment extends FollowshipListFragment {

    /**
     * @deprecated Use {@link #newInstance(String)} instead.
     */
    public FollowerListFragment() {}

    public static FollowerListFragment newInstance(String userIdOrUid) {
        //noinspection deprecation
        FollowerListFragment fragment = new FollowerListFragment();
        fragment.setArguments(userIdOrUid);
        return fragment;
    }

    @Override
    protected BaseUserListResource onAttachUserListResource() {
        return FollowerListResource.attachTo(getUserIdOrUid(), this);
    }
}
