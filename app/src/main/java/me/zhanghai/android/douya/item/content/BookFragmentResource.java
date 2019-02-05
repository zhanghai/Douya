/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.item.content;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import java.util.List;

import me.zhanghai.android.douya.network.api.ApiError;
import me.zhanghai.android.douya.network.api.info.frodo.Book;
import me.zhanghai.android.douya.network.api.info.frodo.CollectableItem;
import me.zhanghai.android.douya.network.api.info.frodo.Doulist;
import me.zhanghai.android.douya.network.api.info.frodo.ItemAwardItem;
import me.zhanghai.android.douya.network.api.info.frodo.Photo;
import me.zhanghai.android.douya.network.api.info.frodo.Rating;
import me.zhanghai.android.douya.network.api.info.frodo.SimpleBook;
import me.zhanghai.android.douya.network.api.info.frodo.SimpleCelebrity;
import me.zhanghai.android.douya.network.api.info.frodo.SimpleItemCollection;
import me.zhanghai.android.douya.network.api.info.frodo.SimpleItemForumTopic;
import me.zhanghai.android.douya.network.api.info.frodo.SimpleReview;
import me.zhanghai.android.douya.util.FragmentUtils;

public class BookFragmentResource extends BaseItemFragmentResource<SimpleBook, Book> {

    private static final String FRAGMENT_TAG_DEFAULT = BookFragmentResource.class.getName();

    private static BookFragmentResource newInstance(long bookId, SimpleBook simpleBook,
                                                    Book book) {
        //noinspection deprecation
        return new BookFragmentResource().setArguments(bookId, simpleBook, book);
    }

    public static BookFragmentResource attachTo(long bookId, SimpleBook simpleBook, Book book,
                                                Fragment fragment, String tag, int requestCode) {
        FragmentActivity activity = fragment.getActivity();
        BookFragmentResource instance = FragmentUtils.findByTag(activity, tag);
        if (instance == null) {
            instance = newInstance(bookId, simpleBook, book);
            FragmentUtils.add(instance, activity, tag);
        }
        instance.setTarget(fragment, requestCode);
        return instance;
    }

    public static BookFragmentResource attachTo(long bookId, SimpleBook simpleBook, Book book,
                                                Fragment fragment) {
        return attachTo(bookId, simpleBook, book, fragment, FRAGMENT_TAG_DEFAULT,
                REQUEST_CODE_INVALID);
    }

    /**
     * @deprecated Use {@code attachTo()} instead.
     */
    public BookFragmentResource() {}

    @Override
    protected BookFragmentResource setArguments(long itemId, SimpleBook simpleItem, Book item) {
        super.setArguments(itemId, simpleItem, item);
        return this;
    }

    @Override
    protected BaseItemResource<SimpleBook, Book> onAttachItemResource(long itemId,
                                                                      SimpleBook simpleItem,
                                                                      Book item) {
        return BookResource.attachTo(itemId, simpleItem, item, this);
    }

    @Override
    protected CollectableItem.Type getDefaultItemType() {
        return CollectableItem.Type.BOOK;
    }

    @Override
    protected boolean hasPhotoList() {
        return false;
    }

    @Override
    protected boolean hasCelebrityList() {
        return false;
    }

    @Override
    protected boolean hasAwardList() {
        return false;
    }

    @Override
    protected void notifyChanged(int requestCode, Book newItem, Rating newRating,
                                 List<Photo> newPhotoList, List<SimpleCelebrity> newCelebrityList,
                                 List<ItemAwardItem> newAwardList,
                                 List<SimpleItemCollection> newItemCollectionList,
                                 List<SimpleReview> newGameGuideList,
                                 List<SimpleReview> newReviewList,
                                 List<SimpleItemForumTopic> newForumTopicList,
                                 List<CollectableItem> newRecommendationList,
                                 List<Doulist> newRelatedDoulistList) {
        getListener().onChanged(requestCode, newItem, newRating, newItemCollectionList,
                newReviewList, newForumTopicList, newRecommendationList, newRelatedDoulistList);
    }

    private Listener getListener() {
        return (Listener) getTarget();
    }

    public interface Listener extends BaseItemFragmentResource.Listener<Book> {
        void onLoadError(int requestCode, ApiError error);
        void onChanged(int requestCode, Book newBook, Rating newRating,
                       List<SimpleItemCollection> newItemCollectionList,
                       List<SimpleReview> newReviewList,
                       List<SimpleItemForumTopic> newForumTopicList,
                       List<CollectableItem> newRecommendationList,
                       List<Doulist> newRelatedDoulistList);
    }
}
