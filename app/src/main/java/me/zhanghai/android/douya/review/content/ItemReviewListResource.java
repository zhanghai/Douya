/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.review.content;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import me.zhanghai.android.douya.network.api.ApiRequest;
import me.zhanghai.android.douya.network.api.ApiRequests;
import me.zhanghai.android.douya.network.api.info.frodo.ReviewList;
import me.zhanghai.android.douya.util.FragmentUtils;

public class ItemReviewListResource extends BaseReviewListResource {

    private static final String KEY_PREFIX = ItemReviewListResource.class.getName() + '.';

    public static final String EXTRA_ITEM_ID = KEY_PREFIX + "item_id";

    private long mItemId;

    private static final String FRAGMENT_TAG_DEFAULT = ItemReviewListResource.class.getName();

    private static ItemReviewListResource newInstance(long itemId) {
        //noinspection deprecation
        ItemReviewListResource resource = new ItemReviewListResource();
        resource.setArguments(itemId);
        return resource;
    }

    public static ItemReviewListResource attachTo(long itemId, FragmentActivity activity,
                                                  String tag, int requestCode) {
        return attachTo(itemId, activity, tag, true, null, requestCode);
    }

    public static ItemReviewListResource attachTo(long itemId, FragmentActivity activity) {
        return attachTo(itemId, activity, FRAGMENT_TAG_DEFAULT, REQUEST_CODE_INVALID);
    }

    public static ItemReviewListResource attachTo(long itemId, Fragment fragment, String tag,
                                                  int requestCode) {
        return attachTo(itemId, fragment.getActivity(), tag, false, fragment, requestCode);
    }

    public static ItemReviewListResource attachTo(long itemId, Fragment fragment) {
        return attachTo(itemId, fragment, FRAGMENT_TAG_DEFAULT, REQUEST_CODE_INVALID);
    }

    private static ItemReviewListResource attachTo(long itemId, FragmentActivity activity,
                                                   String tag, boolean targetAtActivity,
                                                   Fragment targetFragment, int requestCode) {
        ItemReviewListResource resource = FragmentUtils.findByTag(activity, tag);
        if (resource == null) {
            resource = newInstance(itemId);
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
    public ItemReviewListResource() {}

    private void setArguments(long itemId) {
        FragmentUtils.ensureArguments(this)
                .putLong(EXTRA_ITEM_ID, itemId);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mItemId = getArguments().getLong(EXTRA_ITEM_ID);
    }

    @Override
    protected ApiRequest<ReviewList> onCreateRequest(Integer start, Integer count) {
        return ApiRequests.newItemReviewListRequest(mItemId, start, count);
    }
}
