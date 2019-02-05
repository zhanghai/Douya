/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.doulist.content;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import me.zhanghai.android.douya.network.api.ApiRequest;
import me.zhanghai.android.douya.network.api.ApiService;
import me.zhanghai.android.douya.network.api.info.frodo.CollectableItem;
import me.zhanghai.android.douya.network.api.info.frodo.DoulistList;
import me.zhanghai.android.douya.util.FragmentUtils;

public class ItemRelatedDoulistListResource extends BaseDoulistResource {

    private static final String KEY_PREFIX = ItemRelatedDoulistListResource.class.getName() + '.';

    private static final String EXTRA_ITEM_TYPE = KEY_PREFIX + "item_type";
    private static final String EXTRA_ITEM_ID = KEY_PREFIX + "item_id";

    private CollectableItem.Type mItemType;
    private long mItemId;

    private static final String FRAGMENT_TAG_DEFAULT =
            ItemRelatedDoulistListResource.class.getName();

    private static ItemRelatedDoulistListResource newInstance(CollectableItem.Type itemType,
                                                              long itemId) {
        //noinspection deprecation
        return new ItemRelatedDoulistListResource().setArguments(itemType, itemId);
    }

    public static ItemRelatedDoulistListResource attachTo(CollectableItem.Type itemType,
                                                          long itemId, Fragment fragment,
                                                          String tag, int requestCode) {
        FragmentActivity activity = fragment.getActivity();
        ItemRelatedDoulistListResource instance = FragmentUtils.findByTag(activity, tag);
        if (instance == null) {
            instance = newInstance(itemType, itemId);
            FragmentUtils.add(instance, activity, tag);
        }
        instance.setTarget(fragment, requestCode);
        return instance;
    }

    public static ItemRelatedDoulistListResource attachTo(CollectableItem.Type itemType,
                                                          long itemId, Fragment fragment) {
        return attachTo(itemType, itemId, fragment, FRAGMENT_TAG_DEFAULT, REQUEST_CODE_INVALID);
    }

    /**
     * @deprecated Use {@code attachTo()} instead.
     */
    public ItemRelatedDoulistListResource() {}

    protected ItemRelatedDoulistListResource setArguments(CollectableItem.Type itemType,
                                                          long itemId) {
        FragmentUtils.getArgumentsBuilder(this)
                .putSerializable(EXTRA_ITEM_TYPE, itemType)
                .putLong(EXTRA_ITEM_ID, itemId);
        return this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mItemType = (CollectableItem.Type) getArguments().getSerializable(EXTRA_ITEM_TYPE);
        mItemId = getArguments().getLong(EXTRA_ITEM_ID);
    }

    @Override
    protected ApiRequest<DoulistList> onCreateRequest(Integer start, Integer count) {
        return ApiService.getInstance().getItemRelatedDoulistList(mItemType, mItemId, start, count);
    }
}
