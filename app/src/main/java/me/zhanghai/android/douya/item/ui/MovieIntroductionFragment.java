/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.item.ui;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.network.api.info.frodo.Movie;
import me.zhanghai.android.douya.ui.AdapterLinearLayout;
import me.zhanghai.android.douya.util.ViewUtils;

public class MovieIntroductionFragment extends BaseItemIntroductionFragment<Movie> {

    @BindView(R.id.cast_and_credits_wrapper)
    ViewGroup mCastAndCreditsWrapperLayout;
    @BindView(R.id.cast_and_credits)
    AdapterLinearLayout mCastAndCreditsLayout;

    public static MovieIntroductionFragment newInstance(Movie movie) {
        //noinspection deprecation
        MovieIntroductionFragment fragment = new MovieIntroductionFragment();
        fragment.setArguments(movie);
        return fragment;
    }

    /**
     * @deprecated Use {@link #newInstance(Movie)} instead.
     */
    public MovieIntroductionFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup contentLayout = view.findViewById(R.id.content);
        View castAndCreditsView = inflater.inflate(
                R.layout.item_introduction_fragment_movie_cast_and_credits, contentLayout, false);
        contentLayout.addView(castAndCreditsView, 1);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        List<Pair<String, String>> castAndCreditsData = makeCastAndCreditsData();
        boolean hasCastAndCreditsData = !castAndCreditsData.isEmpty();
        ViewUtils.setVisibleOrGone(mCastAndCreditsWrapperLayout, hasCastAndCreditsData);
        if (hasCastAndCreditsData) {
            ItemIntroductionPairListAdapter castAndCreditsAdapter =
                    new ItemIntroductionPairListAdapter();
            castAndCreditsAdapter.replace(castAndCreditsData);
            mCastAndCreditsLayout.setAdapter(castAndCreditsAdapter);
        }
    }

    @Override
    protected List<Pair<String, String>> makeInformationData() {
        List<Pair<String, String>> data = new ArrayList<>();
        addTextToData(R.string.item_introduction_movie_original_title, mItem.originalTitle, data);
        addTextListToData(R.string.item_introduction_movie_genres, mItem.genres, data);
        addTextListToData(R.string.item_introduction_movie_countries, mItem.countries, data);
        addTextListToData(R.string.item_introduction_movie_languages, mItem.languages, data);
        addTextListToData(R.string.item_introduction_movie_release_dates, mItem.releaseDates, data);
        addTextToData(R.string.item_introduction_movie_episode_count, mItem.getEpisodeCountString(),
                data);
        addTextListToData(R.string.item_introduction_movie_durations, mItem.durations, data);
        addTextListToData(R.string.item_introduction_movie_alternative_titles,
                mItem.alternativeTitles, data);
        return data;
    }

    private List<Pair<String, String>> makeCastAndCreditsData() {
        List<Pair<String, String>> data = new ArrayList<>();
        addCelebrityListToData(R.string.item_introduction_movie_directors, mItem.directors, data);
        addCelebrityListToData(R.string.item_introduction_movie_actors, mItem.actors, data);
        return data;
    }
}
