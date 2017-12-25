/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.followship.ui;

public class FollowingListActivityFragment extends FollowshipListActivityFragment {

    public static FollowingListActivityFragment newInstance(String userIdOrUid) {
        //noinspection deprecation
        return new FollowingListActivityFragment().setArguments(userIdOrUid);
    }

    /**
     * @deprecated Use {@link #newInstance(String)} instead.
     */
    public FollowingListActivityFragment() {}

    @Override
    protected FollowingListActivityFragment setArguments(String userIdOrUid) {
        super.setArguments(userIdOrUid);
        return this;
    }

    @Override
    protected FollowshipUserListFragment onCreateListFragment() {
        return FollowingListFragment.newInstance(getUserIdOrUid());
    }
}
