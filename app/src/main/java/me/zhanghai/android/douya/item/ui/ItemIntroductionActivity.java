/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.item.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import me.zhanghai.android.douya.network.api.info.frodo.Movie;
import me.zhanghai.android.douya.util.FragmentUtils;

public class ItemIntroductionActivity extends AppCompatActivity {

    private static final String KEY_PREFIX = ItemIntroductionActivity.class.getName() + '.';

    private static final String EXTRA_TITLE = KEY_PREFIX + "title";
    private static final String EXTRA_MOVIE = KEY_PREFIX + "movie";

    public static Intent makeIntent(String title, Context context) {
        return new Intent(context, ItemIntroductionActivity.class)
                .putExtra(EXTRA_TITLE, title);
    }

    public static Intent makeIntent(String title, Movie movie, Context context) {
        return makeIntent(title, context)
                .putExtra(EXTRA_MOVIE, movie);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Calls ensureSubDecor().
        findViewById(android.R.id.content);

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            String title = intent.getStringExtra(EXTRA_TITLE);
            Movie movie = intent.getParcelableExtra(EXTRA_MOVIE);
            FragmentUtils.add(ItemIntroductionFragment.newInstance(title, movie), this,
                    android.R.id.content);
        }
    }
}
