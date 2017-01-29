/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
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

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.item.content.BaseItemResource;
import me.zhanghai.android.douya.network.api.info.frodo.CollectableItem;
import me.zhanghai.android.douya.util.FragmentUtils;

public abstract class BaseItemFragment<SimpleItemType extends CollectableItem,
        ItemType extends SimpleItemType> extends Fragment {

    private static final String KEY_PREFIX = BaseItemFragment.class.getName() + '.';

    private static final String EXTRA_ITEM_ID = KEY_PREFIX + "item_id";
    private static final String EXTRA_SIMPLE_ITEM = KEY_PREFIX + "simple_item";
    private static final String EXTRA_ITEM = KEY_PREFIX + "item";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

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

        mItemResource = onAttachItemResource(mItemId, mSimpleItem, mItem);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        //activity.setTitle(getTitle());
        activity.setSupportActionBar(mToolbar);
    }

    protected abstract BaseItemResource<SimpleItemType, ItemType> onAttachItemResource(long itemId,
            SimpleItemType simpleItem, ItemType item);

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
