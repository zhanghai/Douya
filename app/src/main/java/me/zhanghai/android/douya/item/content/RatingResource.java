/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.item.content;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import me.zhanghai.android.douya.content.ResourceFragment;
import me.zhanghai.android.douya.network.api.ApiError;
import me.zhanghai.android.douya.network.api.ApiRequest;
import me.zhanghai.android.douya.network.api.ApiService;
import me.zhanghai.android.douya.network.api.info.frodo.CollectableItem;
import me.zhanghai.android.douya.network.api.info.frodo.Rating;
import me.zhanghai.android.douya.util.FragmentUtils;

public class RatingResource extends ResourceFragment<Rating, Rating> {

    private static final String KEY_PREFIX = RatingResource.class.getName() + '.';

    private static final String EXTRA_ITEM_TYPE = KEY_PREFIX + "item_type";
    private static final String EXTRA_ITEM_ID = KEY_PREFIX + "item_id";

    private CollectableItem.Type mItemType;
    private long mItemId;

    private static final String FRAGMENT_TAG_DEFAULT = RatingResource.class.getName();

    private static RatingResource newInstance(CollectableItem.Type itemType, long itemId) {
        //noinspection deprecation
        return new RatingResource().setArguments(itemType, itemId);
    }

    public static RatingResource attachTo(CollectableItem.Type itemType, long itemId,
                                                 Fragment fragment, String tag, int requestCode) {
        FragmentActivity activity = fragment.getActivity();
        RatingResource instance = FragmentUtils.findByTag(activity, tag);
        if (instance == null) {
            instance = newInstance(itemType, itemId);
            FragmentUtils.add(instance, activity, tag);
        }
        instance.setTarget(fragment, requestCode);
        return instance;
    }

    public static RatingResource attachTo(CollectableItem.Type itemType, long itemId,
                                                 Fragment fragment) {
        return attachTo(itemType, itemId, fragment, FRAGMENT_TAG_DEFAULT, REQUEST_CODE_INVALID);
    }

    /**
     * @deprecated Use {@code attachTo()} instead.
     */
    public RatingResource() {}

    protected RatingResource setArguments(CollectableItem.Type itemType, long itemId) {
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
    protected ApiRequest<Rating> onCreateRequest() {
        return ApiService.getInstance().getItemRating(mItemType, mItemId);
    }

    @Override
    protected void onLoadStarted() {
        getListener().onLoadRatingStarted(getRequestCode());
    }

    @Override
    protected void onLoadFinished(boolean successful, Rating response, ApiError error) {
        if (successful) {
            set(response);
            getListener().onLoadRatingFinished(getRequestCode());
            getListener().onRatingChanged(getRequestCode(), response);
        } else {
            getListener().onLoadRatingFinished(getRequestCode());
            getListener().onLoadRatingError(getRequestCode(), error);
        }
    }

    private Listener getListener() {
        return (Listener) getTarget();
    }

    public interface Listener {
        void onLoadRatingStarted(int requestCode);
        void onLoadRatingFinished(int requestCode);
        void onLoadRatingError(int requestCode, ApiError error);
        void onRatingChanged(int requestCode, Rating newRating);
    }
}
