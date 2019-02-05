/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.item.content;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import java.util.List;

import me.zhanghai.android.douya.network.api.ApiError;
import me.zhanghai.android.douya.network.api.info.frodo.CollectableItem;
import me.zhanghai.android.douya.network.api.info.frodo.Doulist;
import me.zhanghai.android.douya.network.api.info.frodo.ItemAwardItem;
import me.zhanghai.android.douya.network.api.info.frodo.SimpleItemCollection;
import me.zhanghai.android.douya.network.api.info.frodo.Movie;
import me.zhanghai.android.douya.network.api.info.frodo.Photo;
import me.zhanghai.android.douya.network.api.info.frodo.Rating;
import me.zhanghai.android.douya.network.api.info.frodo.SimpleItemForumTopic;
import me.zhanghai.android.douya.network.api.info.frodo.SimpleReview;
import me.zhanghai.android.douya.network.api.info.frodo.SimpleCelebrity;
import me.zhanghai.android.douya.network.api.info.frodo.SimpleMovie;
import me.zhanghai.android.douya.util.FragmentUtils;

public class MovieFragmentResource extends BaseItemFragmentResource<SimpleMovie, Movie> {

    private static final String FRAGMENT_TAG_DEFAULT = MovieFragmentResource.class.getName();

    private static MovieFragmentResource newInstance(long movieId, SimpleMovie simpleMovie,
                                                     Movie movie) {
        //noinspection deprecation
        return new MovieFragmentResource().setArguments(movieId, simpleMovie, movie);
    }

    public static MovieFragmentResource attachTo(long movieId, SimpleMovie simpleMovie, Movie movie,
                                                 Fragment fragment, String tag, int requestCode) {
        FragmentActivity activity = fragment.getActivity();
        MovieFragmentResource instance = FragmentUtils.findByTag(activity, tag);
        if (instance == null) {
            instance = newInstance(movieId, simpleMovie, movie);
            FragmentUtils.add(instance, activity, tag);
        }
        instance.setTarget(fragment, requestCode);
        return instance;
    }

    public static MovieFragmentResource attachTo(long movieId, SimpleMovie simpleMovie, Movie movie,
                                                 Fragment fragment) {
        return attachTo(movieId, simpleMovie, movie, fragment, FRAGMENT_TAG_DEFAULT,
                REQUEST_CODE_INVALID);
    }

    /**
     * @deprecated Use {@code attachTo()} instead.
     */
    public MovieFragmentResource() {}

    @Override
    protected MovieFragmentResource setArguments(long itemId, SimpleMovie simpleItem, Movie item) {
        super.setArguments(itemId, simpleItem, item);
        return this;
    }

    @Override
    protected BaseItemResource<SimpleMovie, Movie> onAttachItemResource(long itemId,
                                                                        SimpleMovie simpleItem,
                                                                        Movie item) {
        return MovieResource.attachTo(itemId, simpleItem, item, this);
    }

    @Override
    protected CollectableItem.Type getDefaultItemType() {
        return CollectableItem.Type.MOVIE;
    }

    @Override
    protected boolean hasPhotoList() {
        return true;
    }

    @Override
    protected boolean hasCelebrityList() {
        return true;
    }

    @Override
    protected boolean hasAwardList() {
        return true;
    }

    @Override
    protected void notifyChanged(int requestCode, Movie newItem, Rating newRating,
                                 List<Photo> newPhotoList, List<SimpleCelebrity> newCelebrityList,
                                 List<ItemAwardItem> newAwardList,
                                 List<SimpleItemCollection> newItemCollectionList,
                                 List<SimpleReview> newGameGuideList,
                                 List<SimpleReview> newReviewList,
                                 List<SimpleItemForumTopic> newForumTopicList,
                                 List<CollectableItem> newRecommendationList,
                                 List<Doulist> newRelatedDoulistList) {
        getListener().onChanged(requestCode, newItem, newRating, newPhotoList, newCelebrityList,
                newAwardList, newItemCollectionList, newReviewList, newForumTopicList,
                newRecommendationList, newRelatedDoulistList);
    }

    private Listener getListener() {
        return (Listener) getTarget();
    }

    public interface Listener extends BaseItemFragmentResource.Listener<Movie> {
        void onLoadError(int requestCode, ApiError error);
        void onChanged(int requestCode, Movie newMovie, Rating newRating, List<Photo> newPhotoList,
                       List<SimpleCelebrity> newCelebrityList, List<ItemAwardItem> newAwardList,
                       List<SimpleItemCollection> newItemCollectionList,
                       List<SimpleReview> newReviewList,
                       List<SimpleItemForumTopic> newForumTopicList,
                       List<CollectableItem> newRecommendationList,
                       List<Doulist> newRelatedDoulistList);
    }
}
