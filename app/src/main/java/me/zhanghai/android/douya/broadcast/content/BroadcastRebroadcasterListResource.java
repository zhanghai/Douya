/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.broadcast.content;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import java.util.List;

import me.zhanghai.android.douya.network.api.ApiRequest;
import me.zhanghai.android.douya.network.api.ApiService;
import me.zhanghai.android.douya.network.api.info.apiv2.SimpleUser;
import me.zhanghai.android.douya.util.FragmentUtils;

public class BroadcastRebroadcasterListResource extends BroadcastUserListResource {

    private static final String FRAGMENT_TAG_DEFAULT =
            BroadcastRebroadcasterListResource.class.getName();

    private static BroadcastRebroadcasterListResource newInstance(long broadcastId) {
        //noinspection deprecation
        return new BroadcastRebroadcasterListResource().setArguments(broadcastId);
    }

    public static BroadcastRebroadcasterListResource attachTo(long broadcastId, Fragment fragment,
                                                              String tag, int requestCode) {
        FragmentActivity activity = fragment.getActivity();
        BroadcastRebroadcasterListResource instance = FragmentUtils.findByTag(activity, tag);
        if (instance == null) {
            instance = newInstance(broadcastId);
            instance.targetAt(fragment, requestCode);
            FragmentUtils.add(instance, activity, tag);
        }
        return instance;
    }

    public static BroadcastRebroadcasterListResource attachTo(long broadcastId, Fragment fragment) {
        return attachTo(broadcastId, fragment, FRAGMENT_TAG_DEFAULT, REQUEST_CODE_INVALID);
    }

    /**
     * @deprecated Use {@code attachTo()} instead.
     */
    public BroadcastRebroadcasterListResource() {}

    @Override
    protected BroadcastRebroadcasterListResource setArguments(long broadcastId) {
        super.setArguments(broadcastId);
        return this;
    }

    @Override
    protected ApiRequest<List<SimpleUser>> onCreateRequest(Integer start, Integer count) {
        return ApiService.getInstance().getBroadcastRebroadcasterList(getBroadcastId(), start,
                count);
    }
}
