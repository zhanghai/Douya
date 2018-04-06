/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.item.ui;

import android.content.Context;
import android.support.annotation.NonNull;

import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.network.api.info.frodo.CollectableItem;
import me.zhanghai.android.douya.network.api.info.frodo.ItemCollectionState;
import me.zhanghai.android.douya.ui.ArrayAdapterCompat;

public class ItemCollectionStateSpinnerAdapter extends ArrayAdapterCompat<String> {

    public ItemCollectionStateSpinnerAdapter(CollectableItem.Type type, @NonNull Context context) {
        super(context, R.layout.simple_spinner_item, getStateNames(type, context));

        setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
    }

    private static String[] getStateNames(CollectableItem.Type type, Context context) {
        ItemCollectionState[] states = ItemCollectionState.values();
        String[] stateNames = new String[states.length];
        for (int i = 0; i < states.length; ++i) {
            stateNames[i] = states[i].getString(type, context);
        }
        return stateNames;
    }
}
