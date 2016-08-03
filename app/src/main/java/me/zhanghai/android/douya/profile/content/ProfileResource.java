/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.profile.content;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.android.volley.VolleyError;

import java.util.List;

import me.zhanghai.android.douya.broadcast.content.BroadcastListResource;
import me.zhanghai.android.douya.content.ResourceFragment;
import me.zhanghai.android.douya.diary.content.UserDiaryListResource;
import me.zhanghai.android.douya.followship.content.FollowingListResource;
import me.zhanghai.android.douya.network.api.info.apiv2.Broadcast;
import me.zhanghai.android.douya.network.api.info.apiv2.User;
import me.zhanghai.android.douya.network.api.info.apiv2.UserInfo;
import me.zhanghai.android.douya.network.api.info.frodo.Diary;
import me.zhanghai.android.douya.network.api.info.frodo.Review;
import me.zhanghai.android.douya.network.api.info.frodo.UserItems;
import me.zhanghai.android.douya.review.content.UserReviewListResource;
import me.zhanghai.android.douya.user.content.UserInfoResource;
import me.zhanghai.android.douya.util.FragmentUtils;

public class ProfileResource extends ResourceFragment implements UserInfoResource.Listener,
        BroadcastListResource.Listener, FollowingListResource.Listener,
        UserDiaryListResource.Listener, UserItemListResource.Listener,
        UserReviewListResource.Listener {

    private static final String KEY_PREFIX = ProfileResource.class.getName() + '.';

    public static final String EXTRA_USER_ID_OR_UID = KEY_PREFIX + "user_id_or_uid";
    public static final String EXTRA_USER = KEY_PREFIX + "user";
    public static final String EXTRA_USER_INFO = KEY_PREFIX + "user_info";

    private String mUserIdOrUid;
    private User mUser;

    private UserInfo mUserInfo;

    private UserInfoResource mUserInfoResource;
    private BroadcastListResource mBroadcastListResource;
    private FollowingListResource mFollowingListResource;
    private UserDiaryListResource mDiaryListResource;
    private UserItemListResource mUserItemListResource;
    private UserReviewListResource mReviewListResource;

    private boolean mHasError;

    private static final String FRAGMENT_TAG_DEFAULT = ProfileResource.class.getName();

    private static ProfileResource newInstance(String userIdOrUid, User user, UserInfo userInfo) {
        //noinspection deprecation
        ProfileResource resource = new ProfileResource();
        resource.setArguments(userIdOrUid, user, userInfo);
        return resource;
    }

    public static ProfileResource attachTo(String userIdOrUid, User user, UserInfo userInfo,
                                           FragmentActivity activity, String tag,
                                           int requestCode) {
        return attachTo(userIdOrUid, user, userInfo, activity, tag, true, null, requestCode);
    }

    public static ProfileResource attachTo(String userIdOrUid, User user, UserInfo userInfo,
                                           FragmentActivity activity) {
        return attachTo(userIdOrUid, user, userInfo, activity, FRAGMENT_TAG_DEFAULT,
                REQUEST_CODE_INVALID);
    }

    public static ProfileResource attachTo(String userIdOrUid, User user, UserInfo userInfo,
                                           Fragment fragment, String tag, int requestCode) {
        return attachTo(userIdOrUid, user, userInfo, fragment.getActivity(), tag, false, fragment,
                requestCode);
    }

    public static ProfileResource attachTo(String userIdOrUid, User user, UserInfo userInfo,
                                           Fragment fragment) {
        return attachTo(userIdOrUid, user, userInfo, fragment, FRAGMENT_TAG_DEFAULT,
                REQUEST_CODE_INVALID);
    }

    private static ProfileResource attachTo(String userIdOrUid, User user, UserInfo userInfo,
                                            FragmentActivity activity, String tag,
                                            boolean targetAtActivity, Fragment targetFragment,
                                            int requestCode) {
        ProfileResource resource = FragmentUtils.findByTag(activity, tag);
        if (resource == null) {
            resource = newInstance(userIdOrUid, user, userInfo);
            if (targetAtActivity) {
                resource.targetAtActivity(requestCode);
            } else {
                resource.targetAtFragment(targetFragment, requestCode);
            }
            FragmentUtils.add(resource, activity, tag);
        }
        return resource;
    }

    /**
     * @deprecated Use {@code attachTo()} instead.
     */
    public ProfileResource() {}

    protected void setArguments(String userIdOrUid, User user, UserInfo userInfo) {
        Bundle arguments = FragmentUtils.ensureArguments(this);
        arguments.putString(EXTRA_USER_ID_OR_UID, userIdOrUid);
        arguments.putParcelable(EXTRA_USER, user);
        arguments.putParcelable(EXTRA_USER_INFO, userInfo);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ensureUserInfoAndUserAndIdOrUidFromArguments();

        mUserInfoResource = UserInfoResource.attachTo(mUserIdOrUid, mUser, mUserInfo, this);
        mBroadcastListResource = BroadcastListResource.attachTo(mUserIdOrUid, null, this);
        mFollowingListResource = FollowingListResource.attachTo(mUserIdOrUid, this);
        mDiaryListResource = UserDiaryListResource.attachTo(mUserIdOrUid, this);
        mUserItemListResource = UserItemListResource.attachTo(mUserIdOrUid, this);
        mReviewListResource = UserReviewListResource.attachTo(mUserIdOrUid, this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Bundle arguments = getArguments();
        arguments.putString(EXTRA_USER_ID_OR_UID, mUserIdOrUid);
        arguments.putParcelable(EXTRA_USER, mUser);
        arguments.putParcelable(EXTRA_USER_INFO, mUserInfo);
    }

    public String getUserIdOrUid() {
        ensureUserInfoAndUserAndIdOrUidFromArguments();
        return mUserIdOrUid;
    }

    public User getUser() {
        // Can be called before onCreate() is called.
        ensureUserInfoAndUserAndIdOrUidFromArguments();
        return mUser;
    }

    public boolean hasUser() {
        // Can be called before onCreate() is called.
        ensureUserInfoAndUserAndIdOrUidFromArguments();
        return mUser != null;
    }

    public UserInfo getUserInfo() {
        // Can be called before onCreate() is called.
        ensureUserInfoAndUserAndIdOrUidFromArguments();
        return mUserInfo;
    }

    public boolean hasUserInfo() {
        // Can be called before onCreate() is called.
        ensureUserInfoAndUserAndIdOrUidFromArguments();
        return mUserInfo != null;
    }

    private void ensureUserInfoAndUserAndIdOrUidFromArguments() {
        if (mUserIdOrUid == null) {
            Bundle arguments = getArguments();
            mUserInfo = arguments.getParcelable(EXTRA_USER_INFO);
            if (mUserInfo != null) {
                mUser = mUserInfo;
                mUserIdOrUid = mUserInfo.getIdOrUid();
            } else {
                mUser = arguments.getParcelable(EXTRA_USER);
                if (mUser != null) {
                    mUserIdOrUid = mUser.getIdOrUid();
                } else {
                    mUserIdOrUid = arguments.getString(EXTRA_USER_ID_OR_UID);
                }
            }
        }
    }

    @Override
    public void onLoadUserInfoStarted(int requestCode) {}

    @Override
    public void onLoadUserInfoFinished(int requestCode) {}

    @Override
    public void onLoadUserInfoError(int requestCode, VolleyError error) {
        notifyError(error);
    }

    @Override
    public void onUserInfoChanged(int requestCode, UserInfo newUserInfo) {
        mUserInfo = newUserInfo;
        mUser = newUserInfo;
        mUserIdOrUid = newUserInfo.getIdOrUid();
        getListener().onUserInfoChanged(getRequestCode(), newUserInfo);
        notifyChangedIfLoaded();
    }

    @Override
    public void onUserInfoWriteStarted(int requestCode) {
        getListener().onUserInfoWriteStarted(getRequestCode());
    }

    @Override
    public void onUserInfoWriteFinished(int requestCode) {
        getListener().onUserInfoWriteFinished(getRequestCode());
    }

    @Override
    public void onLoadBroadcastListStarted(int requestCode) {}

    @Override
    public void onLoadBroadcastListFinished(int requestCode) {}

    @Override
    public void onLoadBroadcastListError(int requestCode, VolleyError error) {
        notifyError(error);
    }

    @Override
    public void onBroadcastListChanged(int requestCode, List<Broadcast> newBroadcastList) {
        notifyChangedIfLoaded();
    }

    @Override
    public void onBroadcastListAppended(int requestCode, List<Broadcast> appendedBroadcastList) {
        notifyChangedIfLoaded();
    }

    @Override
    public void onBroadcastChanged(int requestCode, int position, Broadcast newBroadcast) {
        notifyChangedIfLoaded();
    }

    @Override
    public void onBroadcastRemoved(int requestCode, int position) {
        notifyChangedIfLoaded();
    }

    @Override
    public void onBroadcastWriteStarted(int requestCode, int position) {}

    @Override
    public void onBroadcastWriteFinished(int requestCode, int position) {}

    @Override
    public void onLoadUserListStarted(int requestCode, boolean loadMore) {}

    @Override
    public void onLoadUserListFinished(int requestCode, boolean loadMore) {}

    @Override
    public void onLoadUserListError(int requestCode, VolleyError error) {
        notifyError(error);
    }

    @Override
    public void onUserListChanged(int requestCode, List<User> newUserList) {
        notifyChangedIfLoaded();
    }

    @Override
    public void onUserListAppended(int requestCode, List<User> appendedUserList) {
        notifyChangedIfLoaded();
    }

    @Override
    public void onLoadDiaryListStarted(int requestCode) {}

    @Override
    public void onLoadDiaryListFinished(int requestCode) {}

    @Override
    public void onLoadDiaryListError(int requestCode, VolleyError error) {
        notifyError(error);
    }

    @Override
    public void onDiaryListChanged(int requestCode, List<Diary> newDiaryList) {
        notifyChangedIfLoaded();
    }

    @Override
    public void onDiaryListAppended(int requestCode, List<Diary> appendedDiaryList) {
        notifyChangedIfLoaded();
    }

    @Override
    public void onDiaryChanged(int requestCode, int position, Diary newDiary) {
        notifyChangedIfLoaded();
    }

    @Override
    public void onDiaryRemoved(int requestCode, int position) {
        notifyChangedIfLoaded();
    }

    @Override
    public void onLoadUserItemListStarted(int requestCode) {}

    @Override
    public void onLoadUserItemListFinished(int requestCode) {}

    @Override
    public void onLoadUserItemListError(int requestCode, VolleyError error) {
        notifyError(error);
    }

    @Override
    public void onUserItemListChanged(int requestCode, List<UserItems> newUserItemList) {
        notifyChangedIfLoaded();
    }

    @Override
    public void onLoadReviewListStarted(int requestCode) {}

    @Override
    public void onLoadReviewListFinished(int requestCode) {}

    @Override
    public void onLoadReviewListError(int requestCode, VolleyError error) {
        notifyError(error);
    }

    @Override
    public void onReviewListChanged(int requestCode, List<Review> newReviewList) {
        notifyChangedIfLoaded();
    }

    @Override
    public void onReviewListAppended(int requestCode, List<Review> appendedReviewList) {
        notifyChangedIfLoaded();
    }

    @Override
    public void onReviewChanged(int requestCode, int position, Review newReview) {
        notifyChangedIfLoaded();
    }

    @Override
    public void onReviewRemoved(int requestCode, int position) {
        notifyChangedIfLoaded();
    }

    public boolean isLoaded() {
        return hasUserInfo() && mBroadcastListResource != null && mBroadcastListResource.has()
                && mFollowingListResource != null && mFollowingListResource.has()
                && mDiaryListResource.has() && mUserItemListResource.has()
                && mReviewListResource.has();
    }

    public void notifyChangedIfLoaded() {
        if (isLoaded()) {
            getListener().onChanged(getRequestCode(), getUserInfo(), mBroadcastListResource.get(),
                    mFollowingListResource.get(), mDiaryListResource.get(),
                    mUserItemListResource.get(), mReviewListResource.get());
        }
    }

    private void notifyError(VolleyError error) {
        if (!mHasError) {
            mHasError = true;
            getListener().onLoadError(getRequestCode(), error);
        }
    }

    private Listener getListener() {
        return (Listener) getTarget();
    }

    public interface Listener {
        void onLoadError(int requestCode, VolleyError error);
        void onUserInfoChanged(int requestCode, UserInfo newUserInfo);
        void onUserInfoWriteStarted(int requestCode);
        void onUserInfoWriteFinished(int requestCode);
        void onChanged(int requestCode, UserInfo newUserInfo, List<Broadcast> newBroadcastList,
                       List<User> newFollowingList, List<Diary> newDiaryList,
                       List<UserItems> newUserItemList, List<Review> newReviewList);
    }
}
