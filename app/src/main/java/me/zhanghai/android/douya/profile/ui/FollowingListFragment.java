/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.profile.ui;

import me.zhanghai.android.douya.profile.content.FollowingListResource;
import me.zhanghai.android.douya.user.content.BaseUserListResource;

public class FollowingListFragment extends FollowshipListFragment {

    @Override
    protected BaseUserListResource onAttachUserListResource() {
        return FollowingListResource.attachTo(getUserIdOrUid(), this);
    }
}
