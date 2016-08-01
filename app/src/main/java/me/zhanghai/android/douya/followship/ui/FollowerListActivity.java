/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.followship.ui;

import android.content.Context;
import android.content.Intent;

public class FollowerListActivity extends FollowshipListActivity {

    public static Intent makeIntent(String userIdOrUid, Context context) {
        return new Intent(context, FollowerListActivity.class)
                .putExtra(EXTRA_USER_ID_OR_UID, userIdOrUid);
    }

    @Override
    protected FollowshipListActivityFragment onCreateActivityFragment(String userIdOrUid) {
        return FollowerListActivityFragment.newInstance(userIdOrUid);
    }
}
