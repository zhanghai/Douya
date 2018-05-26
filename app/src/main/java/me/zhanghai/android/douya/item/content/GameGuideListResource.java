/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.item.content;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import me.zhanghai.android.douya.network.api.ApiRequest;
import me.zhanghai.android.douya.network.api.ApiService;
import me.zhanghai.android.douya.network.api.info.frodo.ReviewList;
import me.zhanghai.android.douya.review.content.BaseReviewListResource;
import me.zhanghai.android.douya.util.FragmentUtils;

public class GameGuideListResource extends BaseReviewListResource {

    private static final String KEY_PREFIX = GameGuideListResource.class.getName() + '.';

    private static final String EXTRA_ITEM_ID = KEY_PREFIX + "item_id";

    private long mItemId;

    private static final String FRAGMENT_TAG_DEFAULT = GameGuideListResource.class.getName();

    private static GameGuideListResource newInstance(long itemId) {
        //noinspection deprecation
        return new GameGuideListResource().setArguments(itemId);
    }

    public static GameGuideListResource attachTo(long itemId, Fragment fragment, String tag,
                                                 int requestCode) {
        FragmentActivity activity = fragment.getActivity();
        GameGuideListResource instance = FragmentUtils.findByTag(activity, tag);
        if (instance == null) {
            instance = newInstance(itemId);
            FragmentUtils.add(instance, activity, tag);
        }
        instance.setTarget(fragment, requestCode);
        return instance;
    }

    public static GameGuideListResource attachTo(long itemId, Fragment fragment) {
        return attachTo(itemId, fragment, FRAGMENT_TAG_DEFAULT, REQUEST_CODE_INVALID);
    }

    /**
     * @deprecated Use {@code attachTo()} instead.
     */
    public GameGuideListResource() {}

    protected GameGuideListResource setArguments(long itemId) {
        Bundle arguments = FragmentUtils.ensureArguments(this);
        arguments.putLong(EXTRA_ITEM_ID, itemId);
        return this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mItemId = getArguments().getLong(EXTRA_ITEM_ID);
    }

    @Override
    protected ApiRequest<ReviewList> onCreateRequest(Integer start, Integer count) {
        return ApiService.getInstance().getGameGuideList(mItemId, start, count);
    }
}
