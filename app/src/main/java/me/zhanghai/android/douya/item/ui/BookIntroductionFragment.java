/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.item.ui;

import androidx.core.util.Pair;

import java.util.ArrayList;
import java.util.List;

import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.network.api.info.frodo.Book;

public class BookIntroductionFragment extends BaseItemIntroductionFragment<Book> {

    public static BookIntroductionFragment newInstance(Book book) {
        //noinspection deprecation
        BookIntroductionFragment fragment = new BookIntroductionFragment();
        fragment.setArguments(book);
        return fragment;
    }

    /**
     * @deprecated Use {@link #newInstance(Book)} instead.
     */
    public BookIntroductionFragment() {}

    @Override
    protected List<Pair<String, String>> makeInformationData() {
        List<Pair<String, String>> data = new ArrayList<>();
        addTextListToData(R.string.item_introduction_book_authors, mItem.authors, data);
        addTextListToData(R.string.item_introduction_book_presses, mItem.presses, data);
        addTextListToData(R.string.item_introduction_book_subtitles, mItem.subtitles, data);
        addTextListToData(R.string.item_introduction_book_translators, mItem.translators, data);
        addTextListToData(R.string.item_introduction_book_release_dates, mItem.releaseDates, data);
        addTextListToData(R.string.item_introduction_book_page_counts, mItem.pageCounts, data);
        addTextListToData(R.string.item_introduction_book_prices, mItem.prices, data);
        return data;
    }
}
