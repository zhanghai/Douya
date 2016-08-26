/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.followship.content;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import me.zhanghai.android.douya.network.api.ApiRequest;
import me.zhanghai.android.douya.network.api.ApiRequests;
import me.zhanghai.android.douya.network.api.info.apiv2.UserList;
import me.zhanghai.android.douya.util.FragmentUtils;

public class FollowerListResource extends FollowshipUserListResource {

    private static final String FRAGMENT_TAG_DEFAULT = FollowerListResource.class.getName();

    private static FollowerListResource newInstance(String userIdOrUid) {
        //noinspection deprecation
        FollowerListResource resource = new FollowerListResource();
        resource.setArguments(userIdOrUid);
        return resource;
    }

    public static FollowerListResource attachTo(String userIdOrUid, FragmentActivity activity,
                                                      String tag, int requestCode) {
        return attachTo(userIdOrUid, activity, tag, true, null, requestCode);
    }

    public static FollowerListResource attachTo(String userIdOrUid, FragmentActivity activity) {
        return attachTo(userIdOrUid, activity, FRAGMENT_TAG_DEFAULT, REQUEST_CODE_INVALID);
    }

    public static FollowerListResource attachTo(String userIdOrUid, Fragment fragment,
                                                      String tag, int requestCode) {
        return attachTo(userIdOrUid, fragment.getActivity(), tag, false, fragment, requestCode);
    }

    public static FollowerListResource attachTo(String userIdOrUid, Fragment fragment) {
        return attachTo(userIdOrUid, fragment, FRAGMENT_TAG_DEFAULT, REQUEST_CODE_INVALID);
    }

    private static FollowerListResource attachTo(String userIdOrUid, FragmentActivity activity,
                                                 String tag, boolean targetAtActivity,
                                                 Fragment targetFragment, int requestCode) {
        FollowerListResource resource = FragmentUtils.findByTag(activity, tag);
        if (resource == null) {
            resource = newInstance(userIdOrUid);
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
    public FollowerListResource() {}

    @Override
    protected ApiRequest<UserList> onCreateRequest(Integer start, Integer count) {
        return ApiRequests.newFollowerListRequest(getUserIdOrUid(), start, count);
    }
}
