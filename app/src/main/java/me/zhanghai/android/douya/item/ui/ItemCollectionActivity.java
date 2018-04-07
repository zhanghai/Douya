/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.item.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.network.api.info.frodo.CollectableItem;
import me.zhanghai.android.douya.network.api.info.frodo.ItemCollectionState;
import me.zhanghai.android.douya.ui.FragmentFinishable;
import me.zhanghai.android.douya.util.FragmentUtils;

public class ItemCollectionActivity extends AppCompatActivity implements FragmentFinishable {

    private static final String KEY_PREFIX = ItemCollectionActivity.class.getName() + '.';

    private static final String EXTRA_ITEM = KEY_PREFIX + "item";
    private static final String EXTRA_STATE = KEY_PREFIX + "state";

    private ItemCollectionFragment mFragment;

    private boolean mShouldFinish;

    public static Intent makeIntent(CollectableItem item, Context context) {
        return new Intent(context, ItemCollectionActivity.class)
                .putExtra(EXTRA_ITEM, item);
    }

    public static Intent makeIntent(CollectableItem item, ItemCollectionState state,
                                    Context context) {
        return makeIntent(item, context)
                .putExtra(EXTRA_STATE, state);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Intent intent = getIntent();
        CollectableItem item = intent.getParcelableExtra(EXTRA_ITEM);

        switch (item.getType()) {
            case APP:
                break;
            case BOOK:
                break;
            case EVENT:
                break;
            case GAME:
                break;
            case MOVIE:
            case TV:
                setTheme(R.style.Theme_Douya_Movie_DialogWhenLarge);
                break;
            case MUSIC:
                break;
        }

        super.onCreate(savedInstanceState);

        // Calls ensureSubDecor().
        findViewById(android.R.id.content);

        if (savedInstanceState == null) {
            ItemCollectionState state = (ItemCollectionState) intent.getSerializableExtra(
                    EXTRA_STATE);
            mFragment = ItemCollectionFragment.newInstance(item, state);
            FragmentUtils.add(mFragment, this, android.R.id.content);
        } else {
            mFragment = FragmentUtils.findById(this, android.R.id.content);
        }
    }

    @Override
    public void finish() {
        if (!mShouldFinish) {
            mFragment.onFinish();
            return;
        }
        super.finish();
    }

    @Override
    public void finishAfterTransition() {
        if (!mShouldFinish) {
            mFragment.onFinish();
            return;
        }
        super.finishAfterTransition();
    }

    @Override
    public void finishFromFragment() {
        mShouldFinish = true;
        super.finish();
    }

    @Override
    public void finishAfterTransitionFromFragment() {
        mShouldFinish = true;
        super.supportFinishAfterTransition();
    }
}
