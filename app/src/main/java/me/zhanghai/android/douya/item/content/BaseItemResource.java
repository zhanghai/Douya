/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.item.content;

import android.os.Bundle;

import me.zhanghai.android.douya.content.ResourceFragment;
import me.zhanghai.android.douya.network.api.ApiError;
import me.zhanghai.android.douya.network.api.ApiRequest;
import me.zhanghai.android.douya.network.api.info.frodo.CollectableItem;
import me.zhanghai.android.douya.util.FragmentUtils;

public abstract class BaseItemResource<SimpleItemType extends CollectableItem,
        ItemType extends SimpleItemType> extends ResourceFragment<ItemType, ItemType> {

    private final String KEY_PREFIX = getClass().getName() + '.';

    private final String EXTRA_ITEM_ID = KEY_PREFIX + "item_id";
    private final String EXTRA_SIMPLE_ITEM = KEY_PREFIX + "simple_item";
    private final String EXTRA_ITEM = KEY_PREFIX + "item";

    private static final int ITEM_ID_INVALID = -1;

    private long mItemId = ITEM_ID_INVALID;
    private SimpleItemType mSimpleItem;
    private ItemType mExtraItem;

    protected BaseItemResource setArguments(long itemId, SimpleItemType simpleItem, ItemType item) {
        Bundle arguments = FragmentUtils.ensureArguments(this);
        arguments.putLong(EXTRA_ITEM_ID, itemId);
        arguments.putParcelable(EXTRA_SIMPLE_ITEM, simpleItem);
        arguments.putParcelable(EXTRA_ITEM, item);
        return this;
    }

    public long getItemId() {
        ensureArguments();
        return mItemId;
    }

    public SimpleItemType getSimpleItem() {
        // Can be called before onCreate() is called.
        ensureArguments();
        return mSimpleItem;
    }

    public boolean hasSimpleItem() {
        return getSimpleItem() != null;
    }

    @Override
    public ItemType get() {
        ItemType item = super.get();
        if (item == null) {
            // Can be called before onCreate() is called.
            ensureArguments();
            item = mExtraItem;
        }
        return item;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ensureArguments();
    }

    private void ensureArguments() {
        if (mItemId != ITEM_ID_INVALID) {
            return;
        }
        Bundle arguments = getArguments();
        mExtraItem = arguments.getParcelable(EXTRA_ITEM);
        if (mExtraItem != null) {
            mSimpleItem = mExtraItem;
            mItemId = mExtraItem.id;
        } else {
            mSimpleItem = arguments.getParcelable(EXTRA_SIMPLE_ITEM);
            if (mSimpleItem != null) {
                mItemId = mSimpleItem.id;
            } else {
                mItemId = arguments.getLong(EXTRA_ITEM_ID);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (has()) {
            ItemType item = get();
            setArguments(item.id, item, item);
        }
    }

    @Override
    protected ApiRequest<ItemType> onCreateRequest() {
        return onCreateRequest(mItemId);
    }

    protected abstract ApiRequest<ItemType> onCreateRequest(long itemId);

    @Override
    protected void onLoadStarted() {
        getListener().onLoadItemStarted(getRequestCode());
    }

    @Override
    protected void onLoadFinished(boolean successful, ItemType response, ApiError error) {
        getListener().onLoadItemFinished(getRequestCode());
        if (successful) {
            set(response);
            getListener().onItemChanged(getRequestCode(), get());
        } else {
            getListener().onLoadItemError(getRequestCode(), error);
        }
    }

    private Listener<ItemType> getListener() {
        //noinspection unchecked
        return (Listener<ItemType>) getTarget();
    }

    public interface Listener<ItemType> {
        void onLoadItemStarted(int requestCode);
        void onLoadItemFinished(int requestCode);
        void onLoadItemError(int requestCode, ApiError error);
        void onItemChanged(int requestCode, ItemType newItem);
    }
}
