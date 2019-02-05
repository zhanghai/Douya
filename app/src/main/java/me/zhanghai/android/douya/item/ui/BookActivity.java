/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.item.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import me.zhanghai.android.douya.network.api.info.frodo.Book;
import me.zhanghai.android.douya.network.api.info.frodo.SimpleBook;
import me.zhanghai.android.douya.util.FragmentUtils;

public class BookActivity extends AppCompatActivity {

    private static final String KEY_PREFIX = BookActivity.class.getName() + '.';

    private static final String EXTRA_BOOK_ID = KEY_PREFIX + "book_id";
    private static final String EXTRA_SIMPLE_BOOK = KEY_PREFIX + "simple_book";
    private static final String EXTRA_BOOK = KEY_PREFIX + "book";

    public static Intent makeIntent(long bookId, Context context) {
        return new Intent(context, BookActivity.class)
                .putExtra(EXTRA_BOOK_ID, bookId);
    }

    public static Intent makeIntent(SimpleBook simpleBook, Context context) {
        return makeIntent(simpleBook.id, context)
                .putExtra(EXTRA_SIMPLE_BOOK, simpleBook);
    }

    public static Intent makeIntent(Book book, Context context) {
        return makeIntent((SimpleBook) book, context)
                .putExtra(EXTRA_BOOK, book);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Calls ensureSubDecor().
        findViewById(android.R.id.content);

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            long bookId = intent.getLongExtra(EXTRA_BOOK_ID, -1);
            SimpleBook simpleBook = intent.getParcelableExtra(EXTRA_SIMPLE_BOOK);
            Book book = intent.getParcelableExtra(EXTRA_BOOK);
            FragmentUtils.add(BookFragment.newInstance(bookId, simpleBook, book), this,
                    android.R.id.content);
        }
    }
}
