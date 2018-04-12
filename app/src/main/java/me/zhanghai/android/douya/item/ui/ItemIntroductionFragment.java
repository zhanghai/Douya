/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.item.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.network.api.info.frodo.Celebrity;
import me.zhanghai.android.douya.network.api.info.frodo.Movie;
import me.zhanghai.android.douya.ui.AdapterGridLinearLayout;
import me.zhanghai.android.douya.ui.AdapterLinearLayout;
import me.zhanghai.android.douya.util.CollectionUtils;
import me.zhanghai.android.douya.util.FragmentUtils;
import me.zhanghai.android.douya.util.StringCompat;
import me.zhanghai.android.douya.util.TintHelper;

public class ItemIntroductionFragment extends Fragment {

    private static final String KEY_PREFIX = ItemIntroductionFragment.class.getName() + '.';

    private static final String EXTRA_TITLE = KEY_PREFIX + "title";
    private static final String EXTRA_MOVIE = KEY_PREFIX + "movie";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.introduction)
    TextView mIntroductionText;
    @BindView(R.id.cast_and_credits)
    AdapterLinearLayout mCastAndCreditsLayout;
    @BindView(R.id.information)
    AdapterGridLinearLayout mInformationLayout;

    private String mTitle;
    private Movie mMovie;

    public static ItemIntroductionFragment newInstance(String title, Movie movie) {
        //noinspection deprecation
        ItemIntroductionFragment fragment = new ItemIntroductionFragment();
        Bundle arguments = FragmentUtils.ensureArguments(fragment);
        arguments.putString(EXTRA_TITLE, title);
        arguments.putParcelable(EXTRA_MOVIE, movie);
        return fragment;
    }

    /**
     * @deprecated Use {@link #newInstance(String, Movie)} instead.
     */
    public ItemIntroductionFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        mTitle = arguments.getString(EXTRA_TITLE);
        mMovie = arguments.getParcelable(EXTRA_MOVIE);

        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.item_introduction_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(mToolbar);
        TintHelper.onSetSupportActionBar(mToolbar);

        activity.setTitle(mTitle);

        mIntroductionText.setText(mMovie.introduction);

        ItemIntroductionPairListAdapter castAndCreditsAdapter =
                new ItemIntroductionPairListAdapter();
        castAndCreditsAdapter.replace(makeCastAndCreditsData());
        mCastAndCreditsLayout.setAdapter(castAndCreditsAdapter);

        ItemIntroductionPairListAdapter informationAdapter = new ItemIntroductionPairListAdapter();
        informationAdapter.replace(makeInformationData());
        mInformationLayout.setColumnCount(2);
        // HACK: Disabled for not looking good; anyway we always have the space from word break.
        //mInformationLayout.setHorizontalDivider(R.drawable.transparent_divider_vertical_16dp);
        mInformationLayout.setAdapter(informationAdapter);
    }

    private List<Pair<String, String>> makeCastAndCreditsData() {
        List<Pair<String, String>> data = new ArrayList<>();
        String delimiter = getString(R.string.item_introduction_movie_cast_and_credits_delimiter);
        addCelebrityListToData(R.string.item_introduction_movie_directors, mMovie.directors,
                delimiter, data);
        addCelebrityListToData(R.string.item_introduction_movie_actors, mMovie.actors, delimiter,
                data);
        return data;
    }

    private List<Pair<String, String>> makeInformationData() {
        List<Pair<String, String>> data = new ArrayList<>();
        String delimiter = getString(R.string.item_introduction_movie_information_delimiter);
        addTextToData(R.string.item_introduction_movie_original_title, mMovie.originalTitle, data);
        addTextListToData(R.string.item_introduction_movie_genres, mMovie.genres, delimiter, data);
        addTextListToData(R.string.item_introduction_movie_countries, mMovie.countries, delimiter,
                data);
        addTextListToData(R.string.item_introduction_movie_languages, mMovie.languages, delimiter,
                data);
        addTextListToData(R.string.item_introduction_movie_release_dates, mMovie.releaseDates,
                delimiter, data);
        addTextToData(R.string.item_introduction_movie_episode_count,
                mMovie.getEpisodeCountString(), data);
        addTextListToData(R.string.item_introduction_movie_durations, mMovie.durations, delimiter,
                data);
        addTextListToData(R.string.item_introduction_movie_alternative_titles,
                mMovie.alternativeTitles, delimiter, data);
        return data;
    }

    private void addTextToData(int titleRes, String text, List<Pair<String, String>> data) {
        if (!TextUtils.isEmpty(text)) {
            String title = getString(titleRes);
            data.add(new Pair<>(title, text));
        }
    }

    private void addTextListToData(int titleRes, List<String> textList, String delimiter,
                                   List<Pair<String, String>> data) {
        if (!CollectionUtils.isEmpty(textList)) {
            String title = getString(titleRes);
            String text = StringCompat.join(delimiter, textList);
            data.add(new Pair<>(title, text));
        }
    }

    private void addCelebrityListToData(int titleRes, List<Celebrity> celebrityList,
                                        String delimiter, List<Pair<String, String>> data) {
        if (!CollectionUtils.isEmpty(celebrityList)) {
            List<String> celebrityNameList = new ArrayList<>();
            for (Celebrity director : celebrityList) {
                celebrityNameList.add(director.name);
            }
            addTextListToData(titleRes, celebrityNameList, delimiter, data);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
