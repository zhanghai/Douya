/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.item.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import me.zhanghai.android.douya.network.api.info.frodo.CollectableItem;
import me.zhanghai.android.douya.network.api.info.frodo.Movie;
import me.zhanghai.android.douya.util.FragmentUtils;

public class ItemIntroductionActivity extends AppCompatActivity {

    private static final String KEY_PREFIX = ItemIntroductionActivity.class.getName() + '.';

    private static final String EXTRA_ITEM = KEY_PREFIX + "item";

    public static Intent makeIntent(CollectableItem item, Context context) {
        return new Intent(context, ItemIntroductionActivity.class)
                .putExtra(EXTRA_ITEM, item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Calls ensureSubDecor().
        findViewById(android.R.id.content);

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            CollectableItem item = intent.getParcelableExtra(EXTRA_ITEM);
            Fragment fragment;
            switch (item.getType()) {
                case APP:
                    // TODO
                    throw new UnsupportedOperationException();
                case BOOK:
                    // TODO
                    throw new UnsupportedOperationException();
                case EVENT:
                    // TODO
                    throw new UnsupportedOperationException();
                case GAME:
                    // TODO
                    throw new UnsupportedOperationException();
                case MOVIE:
                case TV:
                    fragment = MovieIntroductionFragment.newInstance((Movie) item);
                    break;
                case MUSIC:
                    // TODO
                    throw new UnsupportedOperationException();
                default:
                    throw new IllegalArgumentException();
            }
            FragmentUtils.add(fragment, this, android.R.id.content);
        }
    }
}
