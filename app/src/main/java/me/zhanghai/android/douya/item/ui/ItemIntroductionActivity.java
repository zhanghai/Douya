/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.item.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;

import me.zhanghai.android.douya.network.api.info.frodo.Book;
import me.zhanghai.android.douya.network.api.info.frodo.CollectableItem;
import me.zhanghai.android.douya.network.api.info.frodo.Game;
import me.zhanghai.android.douya.network.api.info.frodo.Movie;
import me.zhanghai.android.douya.network.api.info.frodo.Music;
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
                    fragment = BookIntroductionFragment.newInstance((Book) item);
                    break;
                case EVENT:
                    // TODO
                    throw new UnsupportedOperationException();
                case GAME:
                    fragment = GameIntroductionFragment.newInstance((Game) item);
                    break;
                case MOVIE:
                case TV:
                    fragment = MovieIntroductionFragment.newInstance((Movie) item);
                    break;
                case MUSIC:
                    fragment = MusicIntroductionFragment.newInstance((Music) item);
                    break;
                default:
                    throw new IllegalArgumentException();
            }
            FragmentUtils.add(fragment, this, android.R.id.content);
        }
    }
}
