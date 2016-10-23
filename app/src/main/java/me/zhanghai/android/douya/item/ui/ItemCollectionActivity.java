/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.item.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import me.zhanghai.android.douya.network.api.info.frodo.ItemCollection;
import me.zhanghai.android.douya.util.FragmentUtils;

public class ItemCollectionActivity extends AppCompatActivity {

    private static final String KEY_PREFIX = ItemCollectionActivity.class.getName() + '.';

    private static final String EXTRA_COLLECTION = KEY_PREFIX + "collection";

    public static Intent makeIntent(ItemCollection collection, Context context) {
        return new Intent(context, ItemCollectionActivity.class)
                .putExtra(EXTRA_COLLECTION, collection);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Calls ensureSubDecor().
        findViewById(android.R.id.content);

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            ItemCollection collection = intent.getParcelableExtra(EXTRA_COLLECTION);
            FragmentUtils.add(ItemCollectionFragment.newInstance(collection), this,
                    android.R.id.content);
        }
    }
}
