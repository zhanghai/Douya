/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.item.content;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import java.util.Collections;
import java.util.List;

import me.zhanghai.android.douya.content.MoreBaseListResourceFragment;
import me.zhanghai.android.douya.network.api.ApiError;
import me.zhanghai.android.douya.network.api.ApiRequest;
import me.zhanghai.android.douya.network.api.ApiService;
import me.zhanghai.android.douya.network.api.info.frodo.CollectableItem;
import me.zhanghai.android.douya.network.api.info.frodo.ItemCollection;
import me.zhanghai.android.douya.network.api.info.frodo.ItemCollectionList;
import me.zhanghai.android.douya.util.FragmentUtils;

public class ItemCollectionListResource
        extends MoreBaseListResourceFragment<ItemCollectionList, ItemCollection> {

    private static final String KEY_PREFIX = ItemCollectionListResource.class.getName() + '.';

    private static final String EXTRA_ITEM_TYPE = KEY_PREFIX + "item_type";
    private static final String EXTRA_ITEM_ID = KEY_PREFIX + "item_id";

    private CollectableItem.Type mItemType;
    private long mItemId;

    private static final String FRAGMENT_TAG_DEFAULT = ItemCollectionListResource.class.getName();

    private static ItemCollectionListResource newInstance(CollectableItem.Type itemType,
                                                          long itemId) {
        //noinspection deprecation
        return new ItemCollectionListResource().setArguments(itemType, itemId);
    }

    public static ItemCollectionListResource attachTo(CollectableItem.Type itemType, long itemId,
                                                      Fragment fragment, String tag,
                                                      int requestCode) {
        FragmentActivity activity = fragment.getActivity();
        ItemCollectionListResource instance = FragmentUtils.findByTag(activity, tag);
        if (instance == null) {
            instance = newInstance(itemType, itemId);
            instance.targetAt(fragment, requestCode);
            FragmentUtils.add(instance, activity, tag);
        }
        return instance;
    }

    public static ItemCollectionListResource attachTo(CollectableItem.Type itemType, long itemId,
                                                      Fragment fragment) {
        return attachTo(itemType, itemId, fragment, FRAGMENT_TAG_DEFAULT, REQUEST_CODE_INVALID);
    }

    /**
     * @deprecated Use {@code attachTo()} instead.
     */
    public ItemCollectionListResource() {}

    protected ItemCollectionListResource setArguments(CollectableItem.Type itemType, long itemId) {
        Bundle arguments = FragmentUtils.ensureArguments(this);
        arguments.putSerializable(EXTRA_ITEM_TYPE, itemType);
        arguments.putLong(EXTRA_ITEM_ID, itemId);
        return this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mItemType = (CollectableItem.Type) getArguments().getSerializable(EXTRA_ITEM_TYPE);
        mItemId = getArguments().getLong(EXTRA_ITEM_ID);
    }

    @Override
    protected ApiRequest<ItemCollectionList> onCreateRequest(Integer start, Integer count) {
        return ApiService.getInstance().getItemCollectionList(mItemType, mItemId, start, count);
    }

    @Override
    protected void onLoadStarted() {
        getListener().onLoadItemCollectionListStarted(getRequestCode());
    }

    @Override
    protected void onLoadFinished(boolean more, int count, boolean successful,
                                  List<ItemCollection> response, ApiError error) {
        getListener().onLoadItemCollectionListFinished(getRequestCode());
        if (successful) {
            if (more) {
                append(response);
                getListener().onItemCollectionListAppended(getRequestCode(),
                        Collections.unmodifiableList(response));
            } else {
                set(response);
                getListener().onItemCollectionListChanged(getRequestCode(),
                        Collections.unmodifiableList(get()));
            }
        } else {
            getListener().onLoadItemCollectionListError(getRequestCode(), error);
        }
    }

    private Listener getListener() {
        return (Listener) getTarget();
    }

    public interface Listener {
        void onLoadItemCollectionListStarted(int requestCode);
        void onLoadItemCollectionListFinished(int requestCode);
        void onLoadItemCollectionListError(int requestCode, ApiError error);
        /**
         * @param newItemCollectionList Unmodifiable.
         */
        void onItemCollectionListChanged(int requestCode,
                                         List<ItemCollection> newItemCollectionList);
        /**
         * @param appendedItemCollectionList Unmodifiable.
         */
        void onItemCollectionListAppended(int requestCode,
                                          List<ItemCollection> appendedItemCollectionList);
    }
}
