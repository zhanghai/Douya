/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.item.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.network.api.info.frodo.CollectableItem;
import me.zhanghai.android.douya.network.api.info.frodo.SimpleItemCollection;
import me.zhanghai.android.douya.network.api.info.frodo.ItemCollectionState;
import me.zhanghai.android.douya.util.DoubanUtils;
import me.zhanghai.android.douya.util.FragmentUtils;
import me.zhanghai.android.douya.util.ViewUtils;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class ItemCollectionFragment extends Fragment {

    private static final String KEY_PREFIX = ItemCollectionFragment.class.getName() + '.';

    private static final String EXTRA_COLLECTION = KEY_PREFIX + "collection";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.state_layout)
    ViewGroup mStateLayout;
    @BindView(R.id.state)
    Spinner mStateSpinner;
    @BindView(R.id.rating_layout)
    ViewGroup mRatingLayout;
    @BindView(R.id.rating)
    MaterialRatingBar mRatingBar;
    @BindView(R.id.rating_hint)
    TextView mRatingHintText;
    @BindView(R.id.tags)
    EditText mTagsEdit;
    @BindView(R.id.comment)
    EditText mCommentEdit;

    private SimpleItemCollection mCollection;

    private ItemCollectionState mCollectionState;

    /**
     * @deprecated Use {@link #newInstance(SimpleItemCollection)} instead.
     */
    public ItemCollectionFragment() {}

    public static ItemCollectionFragment newInstance(SimpleItemCollection collection) {
        //noinspection deprecation
        ItemCollectionFragment fragment = new ItemCollectionFragment();
        FragmentUtils.ensureArguments(fragment)
                .putParcelable(EXTRA_COLLECTION, collection);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCollection = getArguments().getParcelable(EXTRA_COLLECTION);

        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.item_collection_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        //activity.setTitle(getTitle());
        activity.setSupportActionBar(mToolbar);

        mStateLayout.setOnClickListener(view -> mStateSpinner.performClick());
        // TODO
        mStateSpinner.setAdapter(new ItemCollectionStateAdapter(CollectableItem.Type.MOVIE,
                mStateSpinner.getContext()));
        mStateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ItemCollectionState newCollectionState = ItemCollectionState.values()[position];
                if (mCollectionState != newCollectionState) {
                    mCollectionState = newCollectionState;
                    onCollectionStateChanged();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        mRatingBar.setOnRatingChangeListener((ratingBar, rating) -> mRatingHintText.setText(
                DoubanUtils.getRatingHint((int) rating, mRatingHintText.getContext())));
    }

    private void onCollectionStateChanged() {
        ViewUtils.setVisibleOrGone(mRatingLayout, mCollectionState != ItemCollectionState.TODO);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
