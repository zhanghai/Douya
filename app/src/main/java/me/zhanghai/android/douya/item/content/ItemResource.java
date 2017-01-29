/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.item.content;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.android.volley.VolleyError;

import me.zhanghai.android.douya.content.ResourceFragment;
import me.zhanghai.android.douya.network.api.ApiRequest;
import me.zhanghai.android.douya.network.api.ApiRequests;
import me.zhanghai.android.douya.network.api.info.frodo.CollectableItem;
import me.zhanghai.android.douya.util.FragmentUtils;

public class ItemResource extends ResourceFragment<CollectableItem, CollectableItem> {

    private static final String KEY_PREFIX = ItemResource.class.getName() + '.';

    private static final String EXTRA_ITEM_ID = KEY_PREFIX + "item_id";

    private long mItemId;

    private static final String FRAGMENT_TAG_DEFAULT = ItemResource.class.getName();

    private static ItemResource newInstance(long itemId) {
        //noinspection deprecation
        return new ItemResource().setArguments(itemId);
    }

    public static ItemResource attachTo(long itemId, Fragment fragment, String tag,
                                        int requestCode) {
        FragmentActivity activity = fragment.getActivity();
        ItemResource instance = FragmentUtils.findByTag(activity, tag);
        if (instance == null) {
            instance = newInstance(itemId);
            instance.targetAt(fragment, requestCode);
            FragmentUtils.add(instance, activity, tag);
        }
        return instance;
    }

    public static ItemResource attachTo(long itemId, Fragment fragment) {
        return attachTo(itemId, fragment, FRAGMENT_TAG_DEFAULT, REQUEST_CODE_INVALID);
    }

    /**
     * @deprecated Use {@code attachTo()} instead.
     */
    public ItemResource() {}

    protected ItemResource setArguments(long itemId) {
        FragmentUtils.ensureArguments(this)
                .putLong(EXTRA_ITEM_ID, itemId);
        return this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mItemId = getArguments().getLong(EXTRA_ITEM_ID);
    }

    @Override
    protected ApiRequest<CollectableItem> onCreateRequest() {
        return ApiRequests.newItemRequest(mItemId);
    }

    @Override
    protected void onLoadStarted() {
        getListener().onLoadItemStarted(getRequestCode());
    }

    @Override
    protected void onLoadFinished(boolean successful, CollectableItem response, VolleyError error) {
        getListener().onLoadItemFinished(getRequestCode());
        if (successful) {
            set(response);
            getListener().onItemChanged(getRequestCode(), get());
        } else {
            getListener().onLoadItemError(getRequestCode(), error);
        }
    }

    private Listener getListener() {
        return (Listener) getTarget();
    }

    public interface Listener {
        void onLoadItemStarted(int requestCode);
        void onLoadItemFinished(int requestCode);
        void onLoadItemError(int requestCode, VolleyError error);
        void onItemChanged(int requestCode, CollectableItem newItem);
    }
}
