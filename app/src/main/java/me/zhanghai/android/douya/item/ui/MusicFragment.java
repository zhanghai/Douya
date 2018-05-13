/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.item.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import me.zhanghai.android.douya.eventbus.EventBusUtils;
import me.zhanghai.android.douya.eventbus.MusicPlayingStateChangedEvent;
import me.zhanghai.android.douya.gallery.ui.GalleryActivity;
import me.zhanghai.android.douya.item.content.BaseItemFragmentResource;
import me.zhanghai.android.douya.item.content.ConfirmUncollectItemDialogFragment;
import me.zhanghai.android.douya.item.content.MusicFragmentResource;
import me.zhanghai.android.douya.item.content.UncollectItemManager;
import me.zhanghai.android.douya.network.api.info.frodo.CollectableItem;
import me.zhanghai.android.douya.network.api.info.frodo.Doulist;
import me.zhanghai.android.douya.network.api.info.frodo.Music;
import me.zhanghai.android.douya.network.api.info.frodo.Rating;
import me.zhanghai.android.douya.network.api.info.frodo.SimpleItemCollection;
import me.zhanghai.android.douya.network.api.info.frodo.SimpleItemForumTopic;
import me.zhanghai.android.douya.network.api.info.frodo.SimpleMusic;
import me.zhanghai.android.douya.network.api.info.frodo.SimpleReview;
import me.zhanghai.android.douya.ui.BarrierAdapter;
import me.zhanghai.android.douya.ui.CopyTextDialogFragment;
import me.zhanghai.android.douya.util.DoubanUtils;
import me.zhanghai.android.douya.util.ImageUtils;
import me.zhanghai.android.douya.util.ViewUtils;

public class MusicFragment extends BaseItemFragment<SimpleMusic, Music>
        implements MusicFragmentResource.Listener, MusicDataAdapter.Listener,
        ConfirmUncollectItemDialogFragment.Listener {

    private MusicAdapter mAdapter;

    private boolean mBackdropBound;

    public static MusicFragment newInstance(long musicId, SimpleMusic simpleMusic, Music music) {
        //noinspection deprecation
        MusicFragment fragment = new MusicFragment();
        fragment.setArguments(musicId, simpleMusic, music);
        return fragment;
    }

    /**
     * @deprecated Use {@link #newInstance(long, SimpleMusic, Music)} instead.
     */
    public MusicFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EventBusUtils.register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        EventBusUtils.unregister(this);
    }

    @Override
    protected BaseItemFragmentResource<SimpleMusic, Music> onAttachResource(long itemId,
                                                                            SimpleMusic simpleItem,
                                                                            Music item) {
        return MusicFragmentResource.attachTo(itemId, simpleItem, item, this);
    }

    @Override
    protected float getBackdropRatio() {
        return ViewUtils.isInPortait(getContext()) ? 1 : 2;
    }

    @Override
    protected BarrierAdapter onCreateAdapter() {
        mAdapter = new MusicAdapter(this);
        return mAdapter;
    }

    @Override
    public void onChanged(int requestCode, Music newMusic, Rating newRating,
                          List<SimpleItemCollection> newItemCollectionList,
                          List<SimpleReview> newReviewList,
                          List<SimpleItemForumTopic> newForumTopicList,
                          List<CollectableItem> newRecommendationList,
                          List<Doulist> newRelatedDoulistList) {
        update(newMusic, newRating, newItemCollectionList, newReviewList, newForumTopicList,
                newRecommendationList, newRelatedDoulistList);
    }

    private void update(Music music, Rating rating, List<SimpleItemCollection> itemCollectionList,
                        List<SimpleReview> reviewList, List<SimpleItemForumTopic> forumTopicList,
                        List<CollectableItem> recommendationList,
                        List<Doulist> relatedDoulistList) {

        if (music != null) {
            super.updateWithSimpleItem(music);
        }

        if (music == null) {
            return;
        }

        if (!mBackdropBound) {
            if (ViewUtils.isInPortait(getActivity())) {
                ImageUtils.loadItemBackdropAndFadeIn(mBackdropImage, music.cover.getLargeUrl(),
                        null);
                mBackdropLayout.setOnClickListener(view -> {
                    // TODO
                    Context context = view.getContext();
                    context.startActivity(GalleryActivity.makeIntent(music.cover, context));
                });
            } else {
                mBackdropImage.setBackgroundColor(music.getThemeColor());
                ViewUtils.fadeIn(mBackdropImage);
            }
            mBackdropBound = true;
        }

        mAdapter.setData(new MusicDataAdapter.Data(music, rating, itemCollectionList, reviewList,
                forumTopicList, recommendationList, relatedDoulistList));
        if (mAdapter.getItemCount() > 0) {
            mContentStateLayout.setLoaded(true);
        }
    }

    @Override
    protected String makeItemUrl(long itemId) {
        return DoubanUtils.makeMusicUrl(itemId);
    }

    @Override
    public void onItemCollectionChanged(int requestCode) {
        mAdapter.notifyItemCollectionChanged();
    }

    @Override
    public void onItemCollectionListItemChanged(int requestCode, int position,
                                                SimpleItemCollection newItemCollection) {
        mAdapter.setItemCollectionListItem(position, newItemCollection);
    }

    @Override
    public void onItemCollectionListItemWriteStarted(int requestCode, int position) {
        mAdapter.notifyItemCollectionListItemChanged(position);
    }

    @Override
    public void onItemCollectionListItemWriteFinished(int requestCode, int position) {
        mAdapter.notifyItemCollectionListItemChanged(position);
    }

    @Override
    public void onUncollectItem(Music music) {
        ConfirmUncollectItemDialogFragment.show(this);
    }

    @Override
    public void uncollect() {
        if (!mResource.hasItem()) {
            return;
        }
        Music music = mResource.getItem();
        UncollectItemManager.getInstance().write(music.getType(), music.id, getActivity());
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onMusicPlayingStateChanged(MusicPlayingStateChangedEvent event) {

        if (event.isFromMyself(this) || mAdapter == null) {
            return;
        }

        mAdapter.notifyTrackListChanged();
    }

    @Override
    public void copyText(String text) {
        CopyTextDialogFragment.show(text, this);
    }
}
