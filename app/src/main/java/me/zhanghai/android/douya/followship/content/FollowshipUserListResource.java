/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.followship.content;

import me.zhanghai.android.douya.user.content.UserListResource;
import me.zhanghai.android.douya.util.FragmentUtils;

public abstract class FollowshipUserListResource extends UserListResource {

    // Not static because we are to be subclassed.
    private final String KEY_PREFIX = getClass().getName() + '.';

    private final String EXTRA_USER_ID_OR_UID = KEY_PREFIX + "user_id_or_uid";

    protected FollowshipUserListResource setArguments(String userIdOrUid) {
        FragmentUtils.getArgumentsBuilder(this)
                .putString(EXTRA_USER_ID_OR_UID, userIdOrUid);
        return this;
    }

    protected String getUserIdOrUid() {
        return getArguments().getString(EXTRA_USER_ID_OR_UID);
    }
}
