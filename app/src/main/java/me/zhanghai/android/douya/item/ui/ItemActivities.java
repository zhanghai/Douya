/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.item.ui;

import android.content.Context;
import android.content.Intent;

import me.zhanghai.android.douya.BuildConfig;
import me.zhanghai.android.douya.network.api.info.frodo.CollectableItem;
import me.zhanghai.android.douya.network.api.info.frodo.Movie;
import me.zhanghai.android.douya.network.api.info.frodo.SimpleMovie;

public class ItemActivities {

    private ItemActivities() {}

    public static Intent makeIntent(CollectableItem item, Context context) {
        if (!BuildConfig.DEBUG) {
            return null;
        }
        if (item instanceof Movie) {
            return MovieActivity.makeIntent((Movie) item, context);
        } else if (item instanceof SimpleMovie) {
            return MovieActivity.makeIntent((SimpleMovie) item, context);
        } else {
            switch (item.getType()) {
                case MOVIE:
                case TV:
                    return MovieActivity.makeIntent(item.id, context);
                default:
                    return null;
            }
        }
    }
}
