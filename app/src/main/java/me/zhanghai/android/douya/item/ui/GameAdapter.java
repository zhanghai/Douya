/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.item.ui;

import androidx.recyclerview.widget.RecyclerView;

import me.zhanghai.android.douya.network.api.info.frodo.SimpleItemCollection;
import me.zhanghai.android.douya.ui.BarrierAdapter;

public class GameAdapter extends BarrierAdapter {

    private GameDataAdapter mDataAdapter;

    public GameAdapter(GameDataAdapter.Listener listener) {
        super(new GameDataAdapter(listener));

        RecyclerView.Adapter<?>[] adapters = getAdapters();
        mDataAdapter = (GameDataAdapter) adapters[0];
    }

    public void setData(GameDataAdapter.Data data) {
        mDataAdapter.setData(data);
    }

    public void notifyItemCollectionChanged() {
        mDataAdapter.notifyItemCollectionChanged();
    }

    public void setItemCollectionListItem(int position, SimpleItemCollection newItemCollection) {
        mDataAdapter.notifyItemCollectionListItemChanged(position, newItemCollection);
    }

    public void notifyItemCollectionListItemChanged(int position) {
        mDataAdapter.notifyItemCollectionListItemChanged(position, null);
    }
}
