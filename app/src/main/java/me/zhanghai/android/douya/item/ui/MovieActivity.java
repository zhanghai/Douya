/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.item.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import me.zhanghai.android.douya.network.api.info.frodo.Movie;
import me.zhanghai.android.douya.network.api.info.frodo.SimpleMovie;
import me.zhanghai.android.douya.util.FragmentUtils;

public class MovieActivity extends AppCompatActivity {

    private static final String KEY_PREFIX = MovieActivity.class.getName() + '.';

    private static final String EXTRA_MOVIE_ID = KEY_PREFIX + "movie_id";
    private static final String EXTRA_SIMPLE_MOVIE = KEY_PREFIX + "simple_movie";
    private static final String EXTRA_MOVIE = KEY_PREFIX + "movie";

    public static Intent makeIntent(long movieId, Context context) {
        return new Intent(context, MovieActivity.class)
                .putExtra(EXTRA_MOVIE_ID, movieId);
    }

    public static Intent makeIntent(SimpleMovie simpleMovie, Context context) {
        return makeIntent(simpleMovie.id, context)
                .putExtra(EXTRA_SIMPLE_MOVIE, simpleMovie);
    }

    public static Intent makeIntent(Movie movie, Context context) {
        return makeIntent((SimpleMovie) movie, context)
                .putExtra(EXTRA_MOVIE, movie);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Calls ensureSubDecor().
        findViewById(android.R.id.content);

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            long movieId = intent.getLongExtra(EXTRA_MOVIE_ID, -1);
            SimpleMovie simpleMovie = intent.getParcelableExtra(EXTRA_SIMPLE_MOVIE);
            Movie movie = intent.getParcelableExtra(EXTRA_MOVIE);
            FragmentUtils.add(MovieFragment.newInstance(movieId, simpleMovie, movie), this,
                    android.R.id.content);
        }
    }
}
