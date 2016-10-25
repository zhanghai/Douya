/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.followship.content;

import android.content.Context;
import android.text.TextUtils;

import com.android.volley.VolleyError;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.content.ResourceWriter;
import me.zhanghai.android.douya.eventbus.EventBusUtils;
import me.zhanghai.android.douya.eventbus.UserInfoUpdatedEvent;
import me.zhanghai.android.douya.eventbus.UserInfoWriteFinishedEvent;
import me.zhanghai.android.douya.eventbus.UserInfoWriteStartedEvent;
import me.zhanghai.android.douya.network.Request;
import me.zhanghai.android.douya.network.api.ApiContract.Response.Error.Codes;
import me.zhanghai.android.douya.network.api.ApiError;
import me.zhanghai.android.douya.network.api.ApiRequests;
import me.zhanghai.android.douya.network.api.info.apiv2.UserInfo;
import me.zhanghai.android.douya.util.LogUtils;
import me.zhanghai.android.douya.util.ToastUtils;

class FollowUserWriter extends ResourceWriter<FollowUserWriter, UserInfo> {

    private String mUserIdOrUid;
    private UserInfo mUserInfo;
    private boolean mFollow;

    private FollowUserWriter(String userIdOrUid, UserInfo userInfo, boolean follow,
                             FollowUserManager manager) {
        super(manager);

        mUserIdOrUid = userIdOrUid;
        mUserInfo = userInfo;
        mFollow = follow;

        EventBusUtils.register(this);
    }

    FollowUserWriter(String userIdOrUid, boolean follow, FollowUserManager manager) {
        this(userIdOrUid, null, follow, manager);
    }

    FollowUserWriter(UserInfo userInfo, boolean follow, FollowUserManager manager) {
        this(userInfo.getIdOrUid(), userInfo, follow, manager);
    }

    public String getUserIdOrUid() {
        return mUserIdOrUid;
    }

    public boolean hasUserIdOrUid(String userIdOrUid) {
        return mUserInfo != null ? mUserInfo.hasIdOrUid(userIdOrUid)
                : TextUtils.equals(mUserIdOrUid, userIdOrUid);
    }

    public boolean isFollow() {
        return mFollow;
    }

    @Override
    protected Request<UserInfo> onCreateRequest() {
        return ApiRequests.newFollowshipRequest(mUserIdOrUid, mFollow);
    }

    @Override
    public void onStart() {
        super.onStart();

        EventBusUtils.postAsync(new UserInfoWriteStartedEvent(mUserIdOrUid, this));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        EventBusUtils.unregister(this);
    }

    @Override
    public void onResponse(UserInfo response) {

        ToastUtils.show(mFollow ? R.string.user_follow_successful
                : R.string.user_unfollow_successful, getContext());

        EventBusUtils.postAsync(new UserInfoUpdatedEvent(response, this));

        stopSelf();
    }

    @Override
    public void onErrorResponse(VolleyError error) {

        LogUtils.e(error.toString());
        Context context = getContext();
        ToastUtils.show(context.getString(mFollow ? R.string.user_follow_failed_format
                        : R.string.user_unfollow_failed_format,
                ApiError.getErrorString(error, context)), context);

        boolean notified = false;
        if (mUserInfo != null && error instanceof ApiError) {
            // Correct our local state if needed.
            ApiError apiError = (ApiError) error;
            Boolean shouldBeFollowed = null;
            if (apiError.code == Codes.Followship.ALREADY_FOLLOWED) {
                shouldBeFollowed = true;
            } else if (apiError.code == Codes.Followship.NOT_FOLLOWED_YET) {
                shouldBeFollowed = false;
            }
            if (shouldBeFollowed != null) {
                mUserInfo.fixFollowed(shouldBeFollowed);
                EventBusUtils.postAsync(new UserInfoUpdatedEvent(mUserInfo, this));
                notified = true;
            }
        }
        if (!notified) {
            // Must notify to reset pending status. Off-screen items also needs to be invalidated.
            EventBusUtils.postAsync(new UserInfoWriteFinishedEvent(mUserIdOrUid, this));
        }

        stopSelf();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserInfoUpdated(UserInfoUpdatedEvent event) {

        if (event.isFromMyself(this)) {
            return;
        }

        //noinspection deprecation
        if (event.userInfo.hasIdOrUid(mUserIdOrUid)) {
            mUserIdOrUid = event.userInfo.getIdOrUid();
            mUserInfo = event.userInfo;
        }
    }
}
