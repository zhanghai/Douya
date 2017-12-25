/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.followship.ui;

public class FollowerListActivityFragment extends FollowshipListActivityFragment {

    public static FollowerListActivityFragment newInstance(String userIdOrUid) {
        //noinspection deprecation
        return new FollowerListActivityFragment().setArguments(userIdOrUid);
    }

    /**
     * @deprecated Use {@link #newInstance(String)} instead.
     */
    public FollowerListActivityFragment() {}

    @Override
    protected FollowerListActivityFragment setArguments(String userIdOrUid) {
        super.setArguments(userIdOrUid);
        return this;
    }

    @Override
    protected FollowshipUserListFragment onCreateListFragment() {
        return FollowerListFragment.newInstance(getUserIdOrUid());
    }
}
