/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.item.ui;

import androidx.core.util.Pair;

import java.util.ArrayList;
import java.util.List;

import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.network.api.info.frodo.Music;

public class MusicIntroductionFragment extends BaseItemIntroductionFragment<Music> {

    public static MusicIntroductionFragment newInstance(Music music) {
        //noinspection deprecation
        MusicIntroductionFragment fragment = new MusicIntroductionFragment();
        fragment.setArguments(music);
        return fragment;
    }

    /**
     * @deprecated Use {@link #newInstance(Music)} instead.
     */
    public MusicIntroductionFragment() {}

    @Override
    protected List<Pair<String, String>> makeInformationData() {
        List<Pair<String, String>> data = new ArrayList<>();
        addTextListToData(R.string.item_introduction_music_artists, mItem.getArtistNames(), data);
        addTextListToData(R.string.item_introduction_music_genres, mItem.genres, data);
        addTextListToData(R.string.item_introduction_music_types, mItem.types, data);
        addTextListToData(R.string.item_introduction_music_media, mItem.media, data);
        addTextListToData(R.string.item_introduction_music_release_dates, mItem.releaseDates, data);
        addTextListToData(R.string.item_introduction_music_publishers, mItem.publishers, data);
        addTextListToData(R.string.item_introduction_music_disc_counts, mItem.discCounts, data);
        return data;
    }
}
