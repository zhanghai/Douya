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
import me.zhanghai.android.douya.network.api.info.frodo.ItemAwardItem;
import me.zhanghai.android.douya.network.api.info.frodo.Music;
import me.zhanghai.android.douya.network.api.info.frodo.Photo;
import me.zhanghai.android.douya.network.api.info.frodo.Rating;
import me.zhanghai.android.douya.network.api.info.frodo.SimpleCelebrity;
import me.zhanghai.android.douya.network.api.info.frodo.SimpleItemCollection;
import me.zhanghai.android.douya.network.api.info.frodo.SimpleItemForumTopic;
import me.zhanghai.android.douya.network.api.info.frodo.SimpleMusic;
import me.zhanghai.android.douya.network.api.info.frodo.SimpleReview;
import me.zhanghai.android.douya.util.FragmentUtils;

public class MusicFragmentResource extends BaseItemFragmentResource<SimpleMusic, Music> {

    private static final String FRAGMENT_TAG_DEFAULT = MusicFragmentResource.class.getName();

    private static MusicFragmentResource newInstance(long musicId, SimpleMusic simpleMusic,
                                                     Music music) {
        //noinspection deprecation
        return new MusicFragmentResource().setArguments(musicId, simpleMusic, music);
    }

    public static MusicFragmentResource attachTo(long musicId, SimpleMusic simpleMusic, Music music,
                                                 Fragment fragment, String tag, int requestCode) {
        FragmentActivity activity = fragment.getActivity();
        MusicFragmentResource instance = FragmentUtils.findByTag(activity, tag);
        if (instance == null) {
            instance = newInstance(musicId, simpleMusic, music);
            FragmentUtils.add(instance, activity, tag);
        }
        instance.setTarget(fragment, requestCode);
        return instance;
    }

    public static MusicFragmentResource attachTo(long musicId, SimpleMusic simpleMusic, Music music,
                                                 Fragment fragment) {
        return attachTo(musicId, simpleMusic, music, fragment, FRAGMENT_TAG_DEFAULT,
                REQUEST_CODE_INVALID);
    }

    /**
     * @deprecated Use {@code attachTo()} instead.
     */
    public MusicFragmentResource() {}

    @Override
    protected MusicFragmentResource setArguments(long itemId, SimpleMusic simpleItem, Music item) {
        super.setArguments(itemId, simpleItem, item);
        return this;
    }

    @Override
    protected BaseItemResource<SimpleMusic, Music> onAttachItemResource(long itemId,
                                                                        SimpleMusic simpleItem,
                                                                        Music item) {
        return MusicResource.attachTo(itemId, simpleItem, item, this);
    }

    @Override
    protected CollectableItem.Type getDefaultItemType() {
        return CollectableItem.Type.MUSIC;
    }

    @Override
    protected boolean hasPhotoList() {
        return false;
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
    protected void notifyChanged(int requestCode, Music newItem, Rating newRating,
                                 List<Photo> newPhotoList, List<SimpleCelebrity> newCelebrityList,
                                 List<ItemAwardItem> newAwardList,
                                 List<SimpleItemCollection> newItemCollectionList,
                                 List<SimpleReview> newGameGuideList,
                                 List<SimpleReview> newReviewList,
                                 List<SimpleItemForumTopic> newForumTopicList,
                                 List<CollectableItem> newRecommendationList,
                                 List<Doulist> newRelatedDoulistList) {
        getListener().onChanged(requestCode, newItem, newRating, newItemCollectionList,
                newReviewList, newForumTopicList, newRecommendationList, newRelatedDoulistList);
    }

    private Listener getListener() {
        return (Listener) getTarget();
    }

    public interface Listener extends BaseItemFragmentResource.Listener<Music> {
        void onLoadError(int requestCode, ApiError error);
        void onChanged(int requestCode, Music newMusic, Rating newRating,
                       List<SimpleItemCollection> newItemCollectionList,
                       List<SimpleReview> newReviewList,
                       List<SimpleItemForumTopic> newForumTopicList,
                       List<CollectableItem> newRecommendationList,
                       List<Doulist> newRelatedDoulistList);
    }
}
