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
import me.zhanghai.android.douya.util.FragmentUtils;

public class TableOfContentsActivity extends AppCompatActivity {

    private static final String KEY_PREFIX = TableOfContentsActivity.class.getName() + '.';

    private static final String EXTRA_BOOK = KEY_PREFIX + "book";

    public static Intent makeIntent(Book item, Context context) {
        return new Intent(context, TableOfContentsActivity.class)
                .putExtra(EXTRA_BOOK, item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Calls ensureSubDecor().
        findViewById(android.R.id.content);

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            Book book = intent.getParcelableExtra(EXTRA_BOOK);
            Fragment fragment = TableOfContentsFragment.newInstance(book);
            FragmentUtils.add(fragment, this, android.R.id.content);
        }
    }
}
