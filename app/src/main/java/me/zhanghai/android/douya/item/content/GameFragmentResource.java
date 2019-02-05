/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.item.content;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import java.util.List;

import me.zhanghai.android.douya.network.api.ApiError;
import me.zhanghai.android.douya.network.api.info.frodo.CollectableItem;
import me.zhanghai.android.douya.network.api.info.frodo.Doulist;
import me.zhanghai.android.douya.network.api.info.frodo.Game;
import me.zhanghai.android.douya.network.api.info.frodo.ItemAwardItem;
import me.zhanghai.android.douya.network.api.info.frodo.Photo;
import me.zhanghai.android.douya.network.api.info.frodo.Rating;
import me.zhanghai.android.douya.network.api.info.frodo.SimpleCelebrity;
import me.zhanghai.android.douya.network.api.info.frodo.SimpleGame;
import me.zhanghai.android.douya.network.api.info.frodo.SimpleItemCollection;
import me.zhanghai.android.douya.network.api.info.frodo.SimpleItemForumTopic;
import me.zhanghai.android.douya.network.api.info.frodo.SimpleReview;
import me.zhanghai.android.douya.util.FragmentUtils;

public class GameFragmentResource extends BaseItemFragmentResource<SimpleGame, Game> {

    private static final String FRAGMENT_TAG_DEFAULT = GameFragmentResource.class.getName();

    private static GameFragmentResource newInstance(long gameId, SimpleGame simpleGame, Game game) {
        //noinspection deprecation
        return new GameFragmentResource().setArguments(gameId, simpleGame, game);
    }

    public static GameFragmentResource attachTo(long gameId, SimpleGame simpleGame, Game game,
                                                Fragment fragment, String tag, int requestCode) {
        FragmentActivity activity = fragment.getActivity();
        GameFragmentResource instance = FragmentUtils.findByTag(activity, tag);
        if (instance == null) {
            instance = newInstance(gameId, simpleGame, game);
            FragmentUtils.add(instance, activity, tag);
        }
        instance.setTarget(fragment, requestCode);
        return instance;
    }

    public static GameFragmentResource attachTo(long gameId, SimpleGame simpleGame, Game game,
                                                Fragment fragment) {
        return attachTo(gameId, simpleGame, game, fragment, FRAGMENT_TAG_DEFAULT,
                REQUEST_CODE_INVALID);
    }

    /**
     * @deprecated Use {@code attachTo()} instead.
     */
    public GameFragmentResource() {}

    @Override
    protected GameFragmentResource setArguments(long itemId, SimpleGame simpleItem, Game item) {
        super.setArguments(itemId, simpleItem, item);
        return this;
    }

    @Override
    protected BaseItemResource<SimpleGame, Game> onAttachItemResource(long itemId,
                                                                      SimpleGame simpleItem,
                                                                      Game item) {
        return GameResource.attachTo(itemId, simpleItem, item, this);
    }

    @Override
    protected CollectableItem.Type getDefaultItemType() {
        return CollectableItem.Type.GAME;
    }

    @Override
    protected boolean hasPhotoList() {
        return true;
    }

    @Override
    protected boolean hasCelebrityList() {
        return false;
    }

    @Override
    protected boolean hasAwardList() {
        return false;
    }

    @Override
    protected void notifyChanged(int requestCode, Game newItem, Rating newRating,
                                 List<Photo> newPhotoList, List<SimpleCelebrity> newCelebrityList,
                                 List<ItemAwardItem> newAwardList,
                                 List<SimpleItemCollection> newItemCollectionList,
                                 List<SimpleReview> newGameGuideList,
                                 List<SimpleReview> newReviewList,
                                 List<SimpleItemForumTopic> newForumTopicList,
                                 List<CollectableItem> newRecommendationList,
                                 List<Doulist> newRelatedDoulistList) {
        getListener().onChanged(requestCode, newItem, newRating, newPhotoList,
                newItemCollectionList, newGameGuideList, newReviewList, newForumTopicList,
                newRecommendationList, newRelatedDoulistList);
    }

    private Listener getListener() {
        return (Listener) getTarget();
    }

    public interface Listener extends BaseItemFragmentResource.Listener<Game> {
        void onLoadError(int requestCode, ApiError error);
        void onChanged(int requestCode, Game newGame, Rating newRating, List<Photo> newPhotoList,
                       List<SimpleItemCollection> newItemCollectionList,
                       List<SimpleReview> newGameGuideList, List<SimpleReview> newReviewList,
                       List<SimpleItemForumTopic> newForumTopicList,
                       List<CollectableItem> newRecommendationList,
                       List<Doulist> newRelatedDoulistList);
    }
}
