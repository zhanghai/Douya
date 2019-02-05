/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.item.ui;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.ui.SimpleAdapter;
import me.zhanghai.android.douya.util.ViewUtils;

public class ItemIntroductionPairListAdapter
        extends SimpleAdapter<Pair<String, String>, ItemIntroductionPairListAdapter.ViewHolder> {

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ViewUtils.inflate(R.layout.item_introduction_pair_item, parent));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Pair<String, String> roleNames = getItem(position);
        holder.titleText.setText(roleNames.first);
        holder.textText.setText(roleNames.second);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.title)
        public TextView titleText;
        @BindView(R.id.text)
        public TextView textText;

        public ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}
