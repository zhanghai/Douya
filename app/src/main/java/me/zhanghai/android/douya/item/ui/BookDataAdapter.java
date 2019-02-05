/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.item.ui;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.gallery.ui.GalleryActivity;
import me.zhanghai.android.douya.network.api.info.frodo.Book;
import me.zhanghai.android.douya.network.api.info.frodo.CollectableItem;
import me.zhanghai.android.douya.network.api.info.frodo.Doulist;
import me.zhanghai.android.douya.network.api.info.frodo.Rating;
import me.zhanghai.android.douya.network.api.info.frodo.SimpleItemCollection;
import me.zhanghai.android.douya.network.api.info.frodo.SimpleItemForumTopic;
import me.zhanghai.android.douya.network.api.info.frodo.SimpleReview;
import me.zhanghai.android.douya.ui.RatioImageView;
import me.zhanghai.android.douya.ui.WebViewActivity;
import me.zhanghai.android.douya.util.ImageUtils;
import me.zhanghai.android.douya.util.RecyclerViewUtils;
import me.zhanghai.android.douya.util.StringCompat;
import me.zhanghai.android.douya.util.StringUtils;
import me.zhanghai.android.douya.util.ViewUtils;

public class BookDataAdapter extends BaseItemDataAdapter<Book> {

    private enum Items {
        HEADER,
        ITEM_COLLECTION,
        BADGE_LIST,
        INTRODUCTION,
        AUTHOR,
        TABLE_OF_CONTENTS,
        RATING,
        ITEM_COLLECTION_LIST,
        REVIEW_LIST,
        FORUM_TOPIC_LIST,
        RECOMMENDATION_LIST,
        RELATED_DOULIST_LIST
    }

    private Data mData;

    public BookDataAdapter(Listener listener) {
        super(listener);
    }

    @Override
    protected Listener getListener() {
        return (Listener) super.getListener();
    }

    public void setData(Data data) {
        mData = data;
        notifyDataChanged();
    }

    public void notifyItemCollectionChanged() {
        int position = Items.ITEM_COLLECTION.ordinal();
        if (position < getItemCount()) {
            notifyItemChanged(position);
        }
    }

    public void notifyItemCollectionListItemChanged(int position,
                                                    SimpleItemCollection newItemCollection) {
        notifyItemCollectionListItemChanged(Items.ITEM_COLLECTION_LIST.ordinal(), position,
                newItemCollection);
    }

    @Override
    public int getTotalItemCount() {
        return Items.values().length;
    }

    @Override
    protected boolean isItemLoaded(int position) {
        if (mData == null) {
            return false;
        }
        if (mData.book == null) {
            return false;
        }
        switch (Items.values()[position]) {
            case HEADER:
                return true;
            case ITEM_COLLECTION:
                return true;
            case BADGE_LIST:
                return mData.rating != null;
            case INTRODUCTION:
                return true;
            case AUTHOR:
                return true;
            case TABLE_OF_CONTENTS:
                return true;
            case RATING:
                return mData.rating != null;
            case ITEM_COLLECTION_LIST:
                return mData.itemCollectionList != null;
            case REVIEW_LIST:
                return mData.reviewList != null;
            case FORUM_TOPIC_LIST:
                return mData.forumTopicList != null;
            case RECOMMENDATION_LIST:
                return mData.recommendationList != null;
            case RELATED_DOULIST_LIST:
                return mData.relatedDoulistList != null;
            default:
                throw new IllegalArgumentException();
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (Items.values()[viewType]) {
            case HEADER:
                return createHeaderHolder(parent);
            case ITEM_COLLECTION:
                return createItemCollectionHolder(parent);
            case BADGE_LIST:
                return createBadgeListHolder(parent);
            case INTRODUCTION:
                return createIntroductionHolder(parent);
            case AUTHOR:
                return createAuthorHolder(parent);
            case TABLE_OF_CONTENTS:
                return createTableOfContentsHolder(parent);
            case RATING:
                return createRatingHolder(parent);
            case ITEM_COLLECTION_LIST:
                return createItemCollectionListHolder(parent);
            case REVIEW_LIST:
                return createReviewListHolder(parent);
            case FORUM_TOPIC_LIST:
                return createForumTopicListHolder(parent);
            case RECOMMENDATION_LIST:
                return createRecommendationListHolder(parent);
            case RELATED_DOULIST_LIST:
                return createRelatedDoulistListHolder(parent);
            default:
                throw new IllegalArgumentException();
        }
    }

    private HeaderHolder createHeaderHolder(ViewGroup parent) {
        return new HeaderHolder(ViewUtils.inflate(R.layout.item_fragment_book_header, parent));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position,
                                 @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);

        switch (Items.values()[position]) {
            case HEADER:
                bindHeaderHolder(holder, mData.book);
                break;
            case ITEM_COLLECTION:
                bindItemCollectionHolder(holder, mData.book);
                break;
            case BADGE_LIST:
                bindBadgeListHolder(holder, mData.book, mData.rating);
                break;
            case INTRODUCTION:
                bindIntroductionHolder(holder, mData.book);
                break;
            case AUTHOR:
                bindAuthorHolder(holder, mData.book);
                break;
            case TABLE_OF_CONTENTS:
                bindTableOfContentsHolder(holder, mData.book);
                break;
            case RATING:
                bindRatingHolder(holder, mData.book, mData.rating);
                break;
            case ITEM_COLLECTION_LIST:
                bindItemCollectionListHolder(holder, mData.book, mData.itemCollectionList,
                        payloads);
                break;
            case REVIEW_LIST:
                bindReviewListHolder(holder, mData.book, mData.reviewList);
                break;
            case FORUM_TOPIC_LIST:
                bindForumTopicListHolder(holder, mData.book, mData.forumTopicList);
                break;
            case RECOMMENDATION_LIST:
                bindRecommendationListHolder(holder, mData.book, mData.recommendationList);
                break;
            case RELATED_DOULIST_LIST:
                bindRelatedDoulistListHolder(holder, mData.book, mData.relatedDoulistList);
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    private AuthorHolder createAuthorHolder(ViewGroup parent) {
        return new AuthorHolder(ViewUtils.inflate(R.layout.item_fragment_author, parent));
    }

    private TableOfContentsHolder createTableOfContentsHolder(ViewGroup parent) {
        return new TableOfContentsHolder(ViewUtils.inflate(R.layout.item_fragment_table_of_contents,
                parent));
    }

    private void bindHeaderHolder(RecyclerView.ViewHolder holder, Book book) {
        HeaderHolder headerHolder = (HeaderHolder) holder;
        headerHolder.coverImage.setRatio(2, 3);
        ImageUtils.loadImage(headerHolder.coverImage, book.cover);
        headerHolder.coverImage.setOnClickListener(view -> {
            Context context = view.getContext();
            context.startActivity(GalleryActivity.makeIntent(book.cover, context));
        });
        headerHolder.titleText.setText(book.title);
        Context context = RecyclerViewUtils.getContext(holder);
        String slashDelimiter = context.getString(R.string.item_information_delimiter_slash);
        headerHolder.subtitleText.setText(StringCompat.join(slashDelimiter, book.subtitles));
        headerHolder.authorsText.setText(StringCompat.join(slashDelimiter, book.authors));
        String translators = StringCompat.join(slashDelimiter, book.translators);
        if (!TextUtils.isEmpty(translators)) {
            translators = context.getString(R.string.item_information_book_translators_format,
                    translators);
        }
        headerHolder.translatorsText.setText(translators);
        String spaceDelimiter = context.getString(R.string.item_information_delimiter_space);
        String detail = StringUtils.joinNonEmpty(spaceDelimiter, book.getYearMonth(context),
                StringCompat.join(slashDelimiter, book.presses), StringCompat.join(slashDelimiter,
                        book.getPageCountStrings()));
        headerHolder.detailText.setText(detail);
    }

    private void bindBadgeListHolder(RecyclerView.ViewHolder holder, Book book, Rating rating) {
        BadgeListHolder badgeListHolder = (BadgeListHolder) holder;
        badgeListHolder.badgeListLayout.setTop250(null);
        badgeListHolder.badgeListLayout.setRating(rating, book);
        badgeListHolder.badgeListLayout.setGenre(0, null, CollectableItem.Type.BOOK);
    }

    @Override
    protected void bindIntroductionHolder(RecyclerView.ViewHolder holder, Book book) {
        super.bindIntroductionHolder(holder, book);

        IntroductionHolder introductionHolder = (IntroductionHolder) holder;
        introductionHolder.introductionLayout.setOnClickListener(view -> {
            Context context = view.getContext();
            context.startActivity(ItemIntroductionActivity.makeIntent(book, context));
        });
    }

    private void bindAuthorHolder(RecyclerView.ViewHolder holder, Book book) {
        AuthorHolder authorHolder = (AuthorHolder) holder;
        authorHolder.introductionText.setText(book.authorIntroduction);
        authorHolder.itemView.setOnClickListener(view -> {
            Context context = view.getContext();
            context.startActivity(WebViewActivity.makeIntent(book.url, true, context));
        });
    }

    private void bindTableOfContentsHolder(RecyclerView.ViewHolder holder, Book book) {
        TableOfContentsHolder tableOfContentsHolder = (TableOfContentsHolder) holder;
        tableOfContentsHolder.tableOfContentsText.setText(book.tableOfContents);
        tableOfContentsHolder.itemView.setOnClickListener(view -> {
            Context context = view.getContext();
            context.startActivity(TableOfContentsActivity.makeIntent(book, context));
        });
    }

    public interface Listener extends BaseItemDataAdapter.Listener<Book> {}

    public static class Data {

        public Book book;
        public Rating rating;
        public List<SimpleItemCollection> itemCollectionList;
        public List<SimpleReview> reviewList;
        public List<SimpleItemForumTopic> forumTopicList;
        public List<CollectableItem> recommendationList;
        public List<Doulist> relatedDoulistList;

        public Data(Book book, Rating rating, List<SimpleItemCollection> itemCollectionList,
                    List<SimpleReview> reviewList, List<SimpleItemForumTopic> forumTopicList,
                    List<CollectableItem> recommendationList, List<Doulist> relatedDoulistList) {
            this.book = book;
            this.rating = rating;
            this.itemCollectionList = itemCollectionList;
            this.reviewList = reviewList;
            this.forumTopicList = forumTopicList;
            this.recommendationList = recommendationList;
            this.relatedDoulistList = relatedDoulistList;
        }
    }

    static class HeaderHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.cover)
        public RatioImageView coverImage;
        @BindView(R.id.title)
        public TextView titleText;
        @BindView(R.id.subtitle)
        public TextView subtitleText;
        @BindView(R.id.authors)
        public TextView authorsText;
        @BindView(R.id.translators)
        public TextView translatorsText;
        @BindView(R.id.detail)
        public TextView detailText;

        public HeaderHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }

    static class AuthorHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.introduction)
        public TextView introductionText;

        public AuthorHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }

    static class TableOfContentsHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.table_of_contents)
        public TextView tableOfContentsText;

        public TableOfContentsHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}
