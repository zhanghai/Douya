/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.item.content;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import java.util.List;

import me.zhanghai.android.douya.network.api.ApiError;
import me.zhanghai.android.douya.network.api.ApiRequest;
import me.zhanghai.android.douya.network.api.ApiService;
import me.zhanghai.android.douya.network.api.info.frodo.ReviewList;
import me.zhanghai.android.douya.network.api.info.frodo.SimpleReview;
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
        FragmentUtils.getArgumentsBuilder(this)
                .putLong(EXTRA_ITEM_ID, itemId);
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

    @Override
    protected BaseReviewListResource.Listener getListener() {
        return new ListenerWrapper((Listener) getTarget());
    }

    public interface Listener {
        void onLoadGameGuideListStarted(int requestCode);
        void onLoadGameGuideListFinished(int requestCode);
        void onLoadGameGuideListError(int requestCode, ApiError error);
        /**
         * @param newGameGuideList Unmodifiable.
         */
        void onGameGuideListChanged(int requestCode, List<SimpleReview> newGameGuideList);
        /**
         * @param appendedGameGuideList Unmodifiable.
         */
        void onGameGuideListAppended(int requestCode, List<SimpleReview> appendedGameGuideList);
        void onGameGuideChanged(int requestCode, int position, SimpleReview newGameGuide);
        void onGameGuideRemoved(int requestCode, int position);
    }

    private static class ListenerWrapper implements BaseReviewListResource.Listener {

        private Listener mListener;

        public ListenerWrapper(Listener listener) {
            mListener = listener;
        }

        @Override
        public void onLoadReviewListStarted(int requestCode) {
            mListener.onLoadGameGuideListStarted(requestCode);
        }

        @Override
        public void onLoadReviewListFinished(int requestCode) {
            mListener.onLoadGameGuideListFinished(requestCode);
        }

        @Override
        public void onLoadReviewListError(int requestCode, ApiError error) {
            mListener.onLoadGameGuideListError(requestCode, error);
        }

        @Override
        public void onReviewListChanged(int requestCode, List<SimpleReview> newReviewList) {
            mListener.onGameGuideListChanged(requestCode, newReviewList);
        }

        @Override
        public void onReviewListAppended(int requestCode, List<SimpleReview> appendedReviewList) {
            mListener.onGameGuideListAppended(requestCode, appendedReviewList);
        }

        @Override
        public void onReviewChanged(int requestCode, int position, SimpleReview newReview) {
            mListener.onGameGuideChanged(requestCode, position, newReview);
        }

        @Override
        public void onReviewRemoved(int requestCode, int position) {
            mListener.onGameGuideRemoved(requestCode, position);
        }
    }
}
