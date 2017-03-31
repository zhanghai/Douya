/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.item.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.network.api.info.frodo.CollectableItem;
import me.zhanghai.android.douya.network.api.info.frodo.ItemCollectionState;
import me.zhanghai.android.douya.network.api.info.frodo.Movie;
import me.zhanghai.android.douya.ui.RatioImageView;
import me.zhanghai.android.douya.util.ImageUtils;
import me.zhanghai.android.douya.util.StringUtils;
import me.zhanghai.android.douya.util.ViewUtils;

public class MovieAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM_HEADER = 0;

    private static final int ITEM_COUNT = 1;

    private Data mData;

    public MovieAdapter() {
        setHasStableIds(true);
    }

    public void setData(Data data) {
        mData = data;
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mData != null ? ITEM_COUNT : 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM_HEADER:
                return new HeaderHolder(ViewUtils.inflate(R.layout.movie_header_item, parent));
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Context context = holder.itemView.getContext();
        switch (position) {
            case ITEM_HEADER: {
                HeaderHolder headerHolder = (HeaderHolder) holder;
                ImageUtils.loadImageWithRatio(headerHolder.mPosterImage, mData.movie.poster);
                headerHolder.mTitleText.setText(mData.movie.title);
                headerHolder.mOriginalTitleText.setText(mData.movie.originalTitle);
                String detail = StringUtils.joinNonEmpty("\t", mData.movie.getYearMonth(context),
                        mData.movie.getEpisodeCountString(), mData.movie.getDurationString());
                headerHolder.mDetailText.setText(detail);
                headerHolder.mGenresText.setText(CollectableItem.getListAsString(
                        mData.movie.genres));
                headerHolder.mTodoButton.setText(ItemCollectionState.TODO.getString(
                        CollectableItem.Type.MOVIE, context));
                headerHolder.mDoingButton.setText(ItemCollectionState.DOING.getString(
                        CollectableItem.Type.MOVIE, context));
                headerHolder.mDoneButton.setText(ItemCollectionState.DONE.getString(
                        CollectableItem.Type.MOVIE, context));
                break;
            }
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        recyclerView.setClipChildren(false);
    }

    public static class Data {

        public Movie movie;

        public Data(Movie movie) {
            this.movie = movie;
        }
    }

    static class HeaderHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.poster)
        RatioImageView mPosterImage;
        @BindView(R.id.title)
        TextView mTitleText;
        @BindView(R.id.original_title)
        TextView mOriginalTitleText;
        @BindView(R.id.detail)
        TextView mDetailText;
        @BindView(R.id.genres)
        TextView mGenresText;
        @BindView(R.id.todo)
        Button mTodoButton;
        @BindView(R.id.doing)
        Button mDoingButton;
        @BindView(R.id.done)
        Button mDoneButton;

        public HeaderHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}
