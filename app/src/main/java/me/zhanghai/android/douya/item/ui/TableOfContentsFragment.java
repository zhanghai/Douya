/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.item.ui;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.network.api.info.frodo.Book;
import me.zhanghai.android.douya.util.FragmentUtils;
import me.zhanghai.android.douya.util.TintHelper;

public class TableOfContentsFragment extends Fragment {

    private static final String KEY_PREFIX = TableOfContentsFragment.class.getName() + '.';

    private static final String EXTRA_BOOK = KEY_PREFIX + "book";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.table_of_contents)
    TextView mTableOfContentsText;

    private Book mBook;

    public static TableOfContentsFragment newInstance(Book book) {
        //noinspection deprecation
        TableOfContentsFragment fragment = new TableOfContentsFragment();
        FragmentUtils.getArgumentsBuilder(fragment)
                .putParcelable(EXTRA_BOOK, book);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        mBook = arguments.getParcelable(EXTRA_BOOK);

        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.item_table_of_contents_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(mToolbar);
        TintHelper.onSetSupportActionBar(mToolbar);

        mTableOfContentsText.setText(mBook.tableOfContents);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem book) {
        switch (book.getItemId()) {
            case android.R.id.home:
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(book);
        }
    }
}
