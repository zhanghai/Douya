/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.item.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.network.api.info.frodo.ItemCollection;
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
    @BindView(R.id.todo)
    RadioButton mTodoButton;
    @BindView(R.id.doing)
    RadioButton mDoingButton;
    @BindView(R.id.done)
    RadioButton mDoneButton;
    @BindView(R.id.rating_layout)
    ViewGroup mRatingLayout;
    @BindView(R.id.rating)
    MaterialRatingBar mRatingBar;
    @BindView(R.id.rating_hint)
    TextView mRatingHintText;
    @BindView(R.id.tags)
    EditText mTagsEdit;
    @BindView(R.id.comment_layout)
    TextInputLayout mCommentLayout;
    @BindView(R.id.comment)
    EditText mCommentEdit;

    private ItemCollection mCollection;

    private ItemCollectionState mCollectionState;

    /**
     * @deprecated Use {@link #newInstance(ItemCollection)} instead.
     */
    public ItemCollectionFragment() {}

    public static ItemCollectionFragment newInstance(ItemCollection collection) {
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
        return inflater.inflate(R.layout.item_collection_dialog_fragment, container, false);
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

        // As in https://developer.android.com/guide/topics/ui/controls/radiobutton.html .
        View.OnClickListener collectionStateButtonOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!((RadioButton) view).isChecked()) {
                    return;
                }
                ItemCollectionState oldCollectionState = mCollectionState;
                switch (view.getId()) {
                    case R.id.todo:
                        mCollectionState = ItemCollectionState.TODO;
                        break;
                    case R.id.doing:
                        mCollectionState = ItemCollectionState.DOING;
                        break;
                    case R.id.done:
                        mCollectionState = ItemCollectionState.DONE;
                        break;
                }
                if (mCollectionState != oldCollectionState) {
                    onCollectionStateChanged();
                }
            }
        };
        mTodoButton.setOnClickListener(collectionStateButtonOnClickListener);
        mDoingButton.setOnClickListener(collectionStateButtonOnClickListener);
        mDoneButton.setOnClickListener(collectionStateButtonOnClickListener);
        mRatingBar.setOnRatingChangeListener(new MaterialRatingBar.OnRatingChangeListener() {
            @Override
            public void onRatingChanged(MaterialRatingBar ratingBar, float rating) {
                mRatingHintText.setText(DoubanUtils.getRatingHint((int) rating,
                        mRatingHintText.getContext()));
            }
        });
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
