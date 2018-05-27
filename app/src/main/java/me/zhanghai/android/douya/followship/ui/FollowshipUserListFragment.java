/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.followship.ui;

import android.os.Bundle;

import me.zhanghai.android.douya.network.api.info.frodo.SimpleUser;
import me.zhanghai.android.douya.ui.SimpleAdapter;
import me.zhanghai.android.douya.user.ui.BaseUserListFragment;
import me.zhanghai.android.douya.user.ui.UserAdapter;
import me.zhanghai.android.douya.util.FragmentUtils;

public abstract class FollowshipUserListFragment extends BaseUserListFragment {

    // Not static because we are to be subclassed.
    private final String KEY_PREFIX = getClass().getName() + '.';

    private final String EXTRA_USER_ID_OR_UID = KEY_PREFIX + "user_id_or_uid";

    private String mUserIdOrUid;

    protected FollowshipUserListFragment setArguments(String userIdOrUid) {
        FragmentUtils.getArgumentsBuilder(this)
                .putString(EXTRA_USER_ID_OR_UID, userIdOrUid);
        return this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUserIdOrUid = getArguments().getString(EXTRA_USER_ID_OR_UID);
    }

    @Override
    protected SimpleAdapter<SimpleUser, ?> onCreateAdapter() {
        return new UserAdapter();
    }

    protected String getUserIdOrUid() {
        return mUserIdOrUid;
    }
}
