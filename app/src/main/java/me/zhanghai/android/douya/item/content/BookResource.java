/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.item.content;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import me.zhanghai.android.douya.network.api.info.frodo.Book;
import me.zhanghai.android.douya.network.api.info.frodo.CollectableItem;
import me.zhanghai.android.douya.network.api.info.frodo.SimpleBook;
import me.zhanghai.android.douya.util.FragmentUtils;

public class BookResource extends BaseItemResource<SimpleBook, Book> {

    private static final String FRAGMENT_TAG_DEFAULT = BookResource.class.getName();

    private static BookResource newInstance(long bookId, SimpleBook simpleBook, Book book) {
        //noinspection deprecation
        BookResource instance = new BookResource();
        instance.setArguments(bookId, simpleBook, book);
        return instance;
    }

    public static BookResource attachTo(long bookId, SimpleBook simpleBook, Book book,
                                        Fragment fragment, String tag, int requestCode) {
        FragmentActivity activity = fragment.getActivity();
        BookResource instance = FragmentUtils.findByTag(activity, tag);
        if (instance == null) {
            instance = newInstance(bookId, simpleBook, book);
            FragmentUtils.add(instance, activity, tag);
        }
        instance.setTarget(fragment, requestCode);
        return instance;
    }

    public static BookResource attachTo(long bookId, SimpleBook simpleBook, Book book,
                                        Fragment fragment) {
        return attachTo(bookId, simpleBook, book, fragment, FRAGMENT_TAG_DEFAULT,
                REQUEST_CODE_INVALID);
    }

    /**
     * @deprecated Use {@code attachTo()} instead.
     */
    public BookResource() {}

    @Override
    protected CollectableItem.Type getDefaultItemType() {
        return CollectableItem.Type.BOOK;
    }
}
