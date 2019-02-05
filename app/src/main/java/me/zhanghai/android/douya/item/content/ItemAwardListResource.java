/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.item.content;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import java.util.Collections;
import java.util.List;

import me.zhanghai.android.douya.content.MoreBaseListResourceFragment;
import me.zhanghai.android.douya.network.api.ApiError;
import me.zhanghai.android.douya.network.api.ApiRequest;
import me.zhanghai.android.douya.network.api.ApiService;
import me.zhanghai.android.douya.network.api.info.frodo.CollectableItem;
import me.zhanghai.android.douya.network.api.info.frodo.ItemAwardItem;
import me.zhanghai.android.douya.network.api.info.frodo.ItemAwardList;
import me.zhanghai.android.douya.util.FragmentUtils;

public class ItemAwardListResource
        extends MoreBaseListResourceFragment<ItemAwardList, ItemAwardItem> {

    private static final String KEY_PREFIX = ItemAwardListResource.class.getName() + '.';

    private static final String EXTRA_ITEM_TYPE = KEY_PREFIX + "item_type";
    private static final String EXTRA_ITEM_ID = KEY_PREFIX + "item_id";

    private CollectableItem.Type mItemType;
    private long mItemId;

    private static final String FRAGMENT_TAG_DEFAULT = ItemAwardListResource.class.getName();

    private static ItemAwardListResource newInstance(CollectableItem.Type itemType, long itemId) {
        //noinspection deprecation
        return new ItemAwardListResource().setArguments(itemType, itemId);
    }

    public static ItemAwardListResource attachTo(CollectableItem.Type itemType, long itemId,
                                                 Fragment fragment, String tag, int requestCode) {
        FragmentActivity activity = fragment.getActivity();
        ItemAwardListResource instance = FragmentUtils.findByTag(activity, tag);
        if (instance == null) {
            instance = newInstance(itemType, itemId);
            FragmentUtils.add(instance, activity, tag);
        }
        instance.setTarget(fragment, requestCode);
        return instance;
    }

    public static ItemAwardListResource attachTo(CollectableItem.Type itemType, long itemId,
                                                 Fragment fragment) {
        return attachTo(itemType, itemId, fragment, FRAGMENT_TAG_DEFAULT, REQUEST_CODE_INVALID);
    }

    /**
     * @deprecated Use {@code attachTo()} instead.
     */
    public ItemAwardListResource() {}

    protected ItemAwardListResource setArguments(CollectableItem.Type itemType, long itemId) {
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
    protected ApiRequest<ItemAwardList> onCreateRequest(Integer start, Integer count) {
        return ApiService.getInstance().getItemAwardList(mItemType, mItemId, start, count);
    }

    @Override
    protected void onLoadStarted() {
        getListener().onLoadAwardListStarted(getRequestCode());
    }

    @Override
    protected void onLoadFinished(boolean more, int count, boolean successful,
                                  List<ItemAwardItem> response, ApiError error) {
        if (successful) {
            if (more) {
                append(response);
                getListener().onLoadAwardListFinished(getRequestCode());
                getListener().onAwardListAppended(getRequestCode(),
                        Collections.unmodifiableList(response));
            } else {
                set(response);
                getListener().onLoadAwardListFinished(getRequestCode());
                getListener().onAwardListChanged(getRequestCode(),
                        Collections.unmodifiableList(get()));
            }
        } else {
            getListener().onLoadAwardListFinished(getRequestCode());
            getListener().onLoadAwardListError(getRequestCode(), error);
        }
    }

    private Listener getListener() {
        return (Listener) getTarget();
    }

    public interface Listener {
        void onLoadAwardListStarted(int requestCode);
        void onLoadAwardListFinished(int requestCode);
        void onLoadAwardListError(int requestCode, ApiError error);
        /**
         * @param newAwardList Unmodifiable.
         */
        void onAwardListChanged(int requestCode, List<ItemAwardItem> newAwardList);
        /**
         * @param appendedAwardList Unmodifiable.
         */
        void onAwardListAppended(int requestCode, List<ItemAwardItem> appendedAwardList);
    }
}
