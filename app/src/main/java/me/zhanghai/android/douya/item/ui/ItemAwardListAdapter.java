/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.item.ui;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.network.api.info.frodo.ItemAwardItem;
import me.zhanghai.android.douya.ui.SimpleAdapter;
import me.zhanghai.android.douya.util.ViewUtils;

public class ItemAwardListAdapter
        extends SimpleAdapter<ItemAwardItem, ItemAwardListAdapter.ViewHolder> {

    public ItemAwardListAdapter() {
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        // Deliberately using plain hash code to identify only this instance.
        return getItem(position).hashCode();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(ViewUtils.inflate(R.layout.item_award_item, parent));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ItemAwardItem awardItem = getItem(position);
        holder.titleText.setText(awardItem.award.title);
        String category = awardItem.categories.get(0).category.title;
        if (awardItem.categories.size() > 1) {
            category = holder.categoryText.getContext().getString(
                    R.string.item_award_category_multiple_format, category,
                    awardItem.categories.size());
        }
        holder.categoryText.setText(category);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.title)
        public TextView titleText;
        @BindView(R.id.category)
        public TextView categoryText;

        public ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}
