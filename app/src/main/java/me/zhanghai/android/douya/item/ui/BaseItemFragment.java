/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.item.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.item.content.BaseItemResource;
import me.zhanghai.android.douya.network.api.ApiError;
import me.zhanghai.android.douya.network.api.info.frodo.CollectableItem;
import me.zhanghai.android.douya.ui.RatioImageView;
import me.zhanghai.android.douya.util.DrawableUtils;
import me.zhanghai.android.douya.util.FragmentUtils;
import me.zhanghai.android.douya.util.StatusBarColorUtils;
import me.zhanghai.android.douya.util.ViewUtils;

public abstract class BaseItemFragment<SimpleItemType extends CollectableItem,
        ItemType extends SimpleItemType> extends Fragment
        implements BaseItemResource.Listener<ItemType> {

    private static final String KEY_PREFIX = BaseItemFragment.class.getName() + '.';

    private static final String EXTRA_ITEM_ID = KEY_PREFIX + "item_id";
    private static final String EXTRA_SIMPLE_ITEM = KEY_PREFIX + "simple_item";
    private static final String EXTRA_ITEM = KEY_PREFIX + "item";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.backdrop)
    RatioImageView mBackdropImage;
    @BindView(R.id.backdrop_scrim)
    View mBackdropScrim;
    @BindView(R.id.backdrop_play)
    ImageView mBackdropPlayImage;
    @BindView(R.id.content)
    RecyclerView mContentList;

    private long mItemId;
    private SimpleItemType mSimpleItem;
    private ItemType mItem;

    private BaseItemResource<SimpleItemType, ItemType> mItemResource;

    public BaseItemFragment<SimpleItemType, ItemType> setArguments(long itemId,
                                                                   SimpleItemType simpleItem,
                                                                   ItemType item) {
        Bundle arguments = FragmentUtils.ensureArguments(this);
        arguments.putLong(EXTRA_ITEM_ID, itemId);
        arguments.putParcelable(EXTRA_SIMPLE_ITEM, simpleItem);
        arguments.putParcelable(EXTRA_ITEM, item);
        return this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        mItemId = arguments.getLong(EXTRA_ITEM_ID);
        mSimpleItem = arguments.getParcelable(EXTRA_SIMPLE_ITEM);
        mItem = arguments.getParcelable(EXTRA_ITEM);

        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.base_item_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mItemResource = onAttachItemResource(mItemId, mSimpleItem, mItem);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(mToolbar);
        StatusBarColorUtils.set(Color.TRANSPARENT, activity);
        ViewUtils.setLayoutFullscreen(activity);

        mBackdropImage.setRatio(16, 9);
        ViewCompat.setBackground(mBackdropScrim, DrawableUtils.makeScrimDrawable(Gravity.TOP));
        mContentList.setLayoutManager(new LinearLayoutManager(activity));
        mContentList.setAdapter(onCreateAdapter());

        if (mItemResource.has()) {
            updateWithItem(mItemResource.get());
        } else if (mItemResource.hasSimpleItem()) {
            updateWithSimpleItem(mItemResource.getSimpleItem());
        }
    }

    protected abstract BaseItemResource<SimpleItemType, ItemType> onAttachItemResource(long itemId,
            SimpleItemType simpleItem, ItemType item);

    protected abstract RecyclerView.Adapter<?> onCreateAdapter();

    @Override
    public void onDestroy() {
        super.onDestroy();

        mItemResource.detach();
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

    @Override
    public void onLoadItemStarted(int requestCode) {

    }

    @Override
    public void onLoadItemFinished(int requestCode) {

    }

    @Override
    public void onLoadItemError(int requestCode, ApiError error) {

    }

    @Override
    public void onItemChanged(int requestCode, ItemType newItem) {
        updateWithItem(newItem);
    }

    protected void updateWithItem(ItemType item) {
        updateWithSimpleItem(item);
    }

    protected void updateWithSimpleItem(SimpleItemType simpleItem) {
        getActivity().setTitle(simpleItem.title);
        mToolbar.setTitle(null);
    }
}
