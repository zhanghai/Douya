/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.review.content;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import me.zhanghai.android.douya.network.api.ApiRequest;
import me.zhanghai.android.douya.network.api.ApiService;
import me.zhanghai.android.douya.network.api.info.frodo.ReviewList;
import me.zhanghai.android.douya.util.FragmentUtils;

public class UserReviewListResource extends BaseReviewListResource {

    private static final String KEY_PREFIX = UserReviewListResource.class.getName() + '.';

    private final String EXTRA_USER_ID_OR_UID = KEY_PREFIX + "user_id_or_uid";

    private String mUserIdOrUid;

    private static final String FRAGMENT_TAG_DEFAULT = UserReviewListResource.class.getName();

    private static UserReviewListResource newInstance(String userIdOrUid) {
        //noinspection deprecation
        return new UserReviewListResource().setArguments(userIdOrUid);
    }

    public static UserReviewListResource attachTo(String userIdOrUid, Fragment fragment, String tag,
                                                  int requestCode) {
        FragmentActivity activity = fragment.getActivity();
        UserReviewListResource instance = FragmentUtils.findByTag(activity, tag);
        if (instance == null) {
            instance = newInstance(userIdOrUid);
            FragmentUtils.add(instance, activity, tag);
        }
        instance.setTarget(fragment, requestCode);
        return instance;
    }

    public static UserReviewListResource attachTo(String userIdOrUid, Fragment fragment) {
        return attachTo(userIdOrUid, fragment, FRAGMENT_TAG_DEFAULT, REQUEST_CODE_INVALID);
    }

    /**
     * @deprecated Use {@code attachTo()} instead.
     */
    public UserReviewListResource() {}

    protected UserReviewListResource setArguments(String userIdOrUid) {
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
    protected ApiRequest<ReviewList> onCreateRequest(Integer start, Integer count) {
        return ApiService.getInstance().getUserReviewList(mUserIdOrUid, start, count);
    }
}
