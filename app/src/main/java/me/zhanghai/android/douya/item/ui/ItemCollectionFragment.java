/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.item.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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
import me.zhanghai.android.douya.network.api.info.frodo.ItemCollectionState;
import me.zhanghai.android.douya.network.api.info.frodo.SimpleItemCollection;
import me.zhanghai.android.douya.ui.ConfirmDiscardContentDialogFragment;
import me.zhanghai.android.douya.ui.FragmentFinishable;
import me.zhanghai.android.douya.util.DoubanUtils;
import me.zhanghai.android.douya.util.FragmentUtils;
import me.zhanghai.android.douya.util.MoreTextUtils;
import me.zhanghai.android.douya.util.StringCompat;
import me.zhanghai.android.douya.util.ViewUtils;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class ItemCollectionFragment extends Fragment
        implements ConfirmDiscardContentDialogFragment.Listener {

    private static final String KEY_PREFIX = ItemCollectionFragment.class.getName() + '.';

    private static final String EXTRA_COLLECTABLE_ITEM = KEY_PREFIX + "collectable_item";
    private static final String EXTRA_STATE = KEY_PREFIX + "state";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.state_layout)
    ViewGroup mStateLayout;
    @BindView(R.id.state)
    Spinner mStateSpinner;
    @BindView(R.id.state_this_item)
    TextView mStateThisItemText;
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

    private MenuItem mDeleteMenuItem;

    private CollectableItem mCollectableItem;
    private ItemCollectionState mExtraState;

    /**
     * @deprecated Use {@link #newInstance(CollectableItem, ItemCollectionState)} instead.
     */
    public ItemCollectionFragment() {}

    public static ItemCollectionFragment newInstance(CollectableItem collectableItem,
                                                     ItemCollectionState state) {
        //noinspection deprecation
        ItemCollectionFragment fragment = new ItemCollectionFragment();
        Bundle arguments = FragmentUtils.ensureArguments(fragment);
        arguments.putParcelable(EXTRA_COLLECTABLE_ITEM, collectableItem);
        arguments.putSerializable(EXTRA_STATE, state);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        mCollectableItem = arguments.getParcelable(EXTRA_COLLECTABLE_ITEM);
        mExtraState = (ItemCollectionState) arguments.getSerializable(EXTRA_STATE);

        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.item_collection_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setTitle(mCollectableItem.title);
        activity.setSupportActionBar(mToolbar);

        mStateLayout.setOnClickListener(view -> mStateSpinner.performClick());
        mStateSpinner.setAdapter(new ItemCollectionStateSpinnerAdapter(mCollectableItem.getType(),
                mStateSpinner.getContext()));
        if (savedInstanceState == null) {
            ItemCollectionState state;
            if (mExtraState != null) {
                state = mExtraState;
            } else if (mCollectableItem.collection != null) {
                state = mCollectableItem.collection.getState();
            } else {
                state = null;
            }
            if (state != null) {
                mStateSpinner.setSelection(state.ordinal(), false);
            }
        }
        mStateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                onStateChanged();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        mStateThisItemText.setText(mCollectableItem.getType().getThisItem(activity));
        onStateChanged();
        if (savedInstanceState == null && mCollectableItem.collection != null
                && mCollectableItem.collection.rating != null) {
            mRatingBar.setRating(mCollectableItem.collection.rating.getRatingBarValue());
        }
        mRatingBar.setOnRatingChangeListener((ratingBar, rating) -> updateRatingHint());
        updateRatingHint();
        if (savedInstanceState == null && mCollectableItem.collection != null) {
            mTagsEdit.setText(StringCompat.join(" ", mCollectableItem.collection.tags));
        }
        if (savedInstanceState == null && mCollectableItem.collection != null) {
            mCommentEdit.setText(mCollectableItem.collection.comment);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.item_collection, menu);
        mDeleteMenuItem = menu.findItem(R.id.action_delete);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        updateOptionsMenu();
    }

    private void updateOptionsMenu() {
        if (mDeleteMenuItem == null) {
            return;
        }
        boolean showDelete = mCollectableItem.collection != null && (mExtraState == null
                || getState() == mCollectableItem.collection.getState());
        mDeleteMenuItem.setVisible(showDelete);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onFinish();
                return true;
            case R.id.action_delete:
                onDelete();
                return true;
            case R.id.action_save:
                save();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void onStateChanged() {
        boolean hasRating = getState() != ItemCollectionState.TODO;
        ViewUtils.setVisibleOrGone(mRatingLayout, hasRating);
        updateOptionsMenu();
    }

    private void updateRatingHint() {
        mRatingHintText.setText(DoubanUtils.getRatingHint((int) mRatingBar.getRating(),
                mRatingHintText.getContext()));
    }

    private void onDelete() {

    }

    private void save() {

    }

    public void onFinish() {
        if (isChanged()) {
            ConfirmDiscardContentDialogFragment.show(this);
        } else {
            finish();
        }
    }

    private boolean isChanged() {
        SimpleItemCollection collection = mCollectableItem.collection;
        ItemCollectionState state = getState();
        if (collection != null) {
            boolean equalsExtraState = mExtraState != null && state == mExtraState;
            boolean equalsCollectionState = state == collection.getState();
            if (!(equalsExtraState || equalsCollectionState)) {
                return true;
            }
        }
        if (state != ItemCollectionState.TODO) {
            float originalRating = collection != null && collection.rating != null ?
                    collection.rating.getRatingBarValue() : 0;
            float rating = mRatingBar.getRating();
            if (rating != originalRating) {
                return true;
            }
        }
        String tags = mTagsEdit.getText().toString();
        String originalTags = collection != null ? StringCompat.join(" ", collection.tags) : "";
        if (!TextUtils.equals(tags, originalTags)) {
            return true;
        }
        String comment = mCommentEdit.getText().toString();
        String originalComment = collection != null ? MoreTextUtils.nullToEmpty(collection.comment)
                : "";
        if (!TextUtils.equals(comment, originalComment)) {
            return true;
        }
        return false;
    }

    @Override
    public void discardContent() {
        finish();
    }

    private void finish() {
        FragmentFinishable.finish(getActivity());
    }

    private ItemCollectionState getState() {
        return ItemCollectionState.values()[mStateSpinner.getSelectedItemPosition()];
    }
}
