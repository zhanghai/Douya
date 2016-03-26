/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.broadcast.content;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import java.util.List;

import me.zhanghai.android.douya.network.api.ApiRequest;
import me.zhanghai.android.douya.network.api.ApiRequests;
import me.zhanghai.android.douya.network.api.info.User;
import me.zhanghai.android.douya.user.content.UserListResource;

public class BroadcastLikerListResource extends UserListResource {

    private static final String KEY_PREFIX = BroadcastLikerListResource.class.getName() + '.';

    public static final String EXTRA_BROADCAST_ID = KEY_PREFIX + "broadcast_id";

    private static final String FRAGMENT_TAG_DEFAULT = BroadcastLikerListResource.class.getName();

    private static BroadcastLikerListResource newInstance(long broadcastId) {
        //noinspection deprecation
        BroadcastLikerListResource resource = new BroadcastLikerListResource();
        Bundle arguments = new Bundle();
        arguments.putLong(EXTRA_BROADCAST_ID, broadcastId);
        resource.setArguments(arguments);
        return resource;
    }

    public static BroadcastLikerListResource attachTo(long broadcastId, FragmentActivity activity,
                                                      String tag, int requestCode) {
        return attachTo(broadcastId, activity, tag, true, null, requestCode);
    }

    public static BroadcastLikerListResource attachTo(long broadcastId, FragmentActivity activity) {
        return attachTo(broadcastId, activity, FRAGMENT_TAG_DEFAULT, REQUEST_CODE_INVALID);
    }

    public static BroadcastLikerListResource attachTo(long broadcastId, Fragment fragment,
                                                      String tag, int requestCode) {
        return attachTo(broadcastId, fragment.getActivity(), tag, false, fragment, requestCode);
    }

    public static BroadcastLikerListResource attachTo(long broadcastId, Fragment fragment) {
        return attachTo(broadcastId, fragment, FRAGMENT_TAG_DEFAULT, REQUEST_CODE_INVALID);
    }

    private static BroadcastLikerListResource attachTo(long broadcastId, FragmentActivity activity,
                                                      String tag, boolean targetAtActivity,
                                                      Fragment targetFragment, int requestCode) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        BroadcastLikerListResource resource = (BroadcastLikerListResource) fragmentManager
                .findFragmentByTag(tag);
        if (resource == null) {
            resource = newInstance(broadcastId);
            if (targetAtActivity) {
                resource.targetAtActivity(requestCode);
            } else {
                resource.targetAtFragment(targetFragment, requestCode);
            }
            fragmentManager.beginTransaction()
                    .add(resource, tag)
                    .commit();
        }
        return resource;
    }

    /**
     * @deprecated Use {@code attachTo()} instead.
     */
    public BroadcastLikerListResource() {}

    @Override
    protected ApiRequest<List<User>> onCreateRequest(Integer start, Integer count) {
        return ApiRequests.newBroadcastLikerListRequest(getArguments().getLong(EXTRA_BROADCAST_ID),
                start, count, getActivity());
    }
}
