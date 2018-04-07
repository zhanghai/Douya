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
import android.text.Editable;
import android.text.TextWatcher;
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
import me.zhanghai.android.douya.ui.ConfirmDiscardContentDialogFragment;
import me.zhanghai.android.douya.ui.FragmentFinishable;
import me.zhanghai.android.douya.util.DoubanUtils;
import me.zhanghai.android.douya.util.FragmentUtils;
import me.zhanghai.android.douya.util.StringCompat;
import me.zhanghai.android.douya.util.ViewUtils;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class ItemCollectionFragment extends Fragment
        implements ConfirmDiscardContentDialogFragment.Listener {

    private static final String KEY_PREFIX = ItemCollectionFragment.class.getName() + '.';

    private static final String EXTRA_COLLECTABLE_ITEM = KEY_PREFIX + "collectable_item";
    private static final String EXTRA_COLLECTION_STATE = KEY_PREFIX + "collection_state";

    private static final String STATE_CHANGED = KEY_PREFIX + "changed";

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
    private ItemCollectionState mExtraCollectionState;

    private boolean mChanged;

    /**
     * @deprecated Use {@link #newInstance(CollectableItem, ItemCollectionState)} instead.
     */
    public ItemCollectionFragment() {}

    public static ItemCollectionFragment newInstance(CollectableItem collectableItem,
                                                     ItemCollectionState collectionState) {
        //noinspection deprecation
        ItemCollectionFragment fragment = new ItemCollectionFragment();
        Bundle arguments = FragmentUtils.ensureArguments(fragment);
        arguments.putParcelable(EXTRA_COLLECTABLE_ITEM, collectableItem);
        arguments.putSerializable(EXTRA_COLLECTION_STATE, collectionState);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        mCollectableItem = arguments.getParcelable(EXTRA_COLLECTABLE_ITEM);
        mExtraCollectionState = (ItemCollectionState) arguments.getSerializable(
                EXTRA_COLLECTION_STATE);

        if (savedInstanceState != null) {
            mChanged = savedInstanceState.getBoolean(STATE_CHANGED);
        }

        setHasOptionsMenu(true);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(STATE_CHANGED, mChanged);
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
            ItemCollectionState collectionState;
            if (mExtraCollectionState != null) {
                collectionState = mExtraCollectionState;
            } else if (mCollectableItem.collection != null) {
                collectionState = mCollectableItem.collection.getState();
            } else {
                collectionState = null;
            }
            if (collectionState != null) {
                mStateSpinner.setSelection(collectionState.ordinal(), false);
            }
        }
        mStateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mChanged = true;
                updateRatingVisibility();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        mStateThisItemText.setText(mCollectableItem.getType().getThisItem(activity));
        updateRatingVisibility();
        if (savedInstanceState == null && mCollectableItem.collection != null
                && mCollectableItem.collection.rating != null) {
            mRatingBar.setRating(mCollectableItem.collection.rating.getRatingBarValue());
        }
        mRatingBar.setOnRatingChangeListener((ratingBar, rating) -> {
            mChanged = true;
            updateRatingHint();
        });
        updateRatingHint();
        if (savedInstanceState == null && mCollectableItem.collection != null) {
            mTagsEdit.setText(StringCompat.join(" ", mCollectableItem.collection.tags));
        }
        mTagsEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                mChanged = true;
            }
        });
        if (savedInstanceState == null && mCollectableItem.collection != null) {
            mCommentEdit.setText(mCollectableItem.collection.comment);
        }
        mCommentEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                mChanged = true;
            }
        });
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
        boolean showDelete = mCollectableItem.collection != null && mExtraCollectionState == null;
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

    private void updateRatingVisibility() {
        boolean hasRating = getCollectionState() != ItemCollectionState.TODO;
        ViewUtils.setVisibleOrGone(mRatingLayout, hasRating);
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
        if (mChanged) {
            ConfirmDiscardContentDialogFragment.show(this);
        } else {
            finish();
        }
    }

    @Override
    public void discardContent() {
        finish();
    }

    private void finish() {
        FragmentFinishable.finish(getActivity());
    }

    private ItemCollectionState getCollectionState() {
        return ItemCollectionState.values()[mStateSpinner.getSelectedItemPosition()];
    }
}
