/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.item.ui;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.item.content.BaseItemFragmentResource;
import me.zhanghai.android.douya.network.api.ApiError;
import me.zhanghai.android.douya.network.api.info.frodo.CollectableItem;
import me.zhanghai.android.douya.ui.AppBarWrapperLayout;
import me.zhanghai.android.douya.ui.OnVerticalScrollWithPagingTouchSlopListener;
import me.zhanghai.android.douya.ui.RatioImageView;
import me.zhanghai.android.douya.ui.TransparentDoubleClickToolbar;
import me.zhanghai.android.douya.util.DrawableUtils;
import me.zhanghai.android.douya.util.FragmentUtils;
import me.zhanghai.android.douya.util.LogUtils;
import me.zhanghai.android.douya.util.RecyclerViewUtils;
import me.zhanghai.android.douya.util.StatusBarColorUtils;
import me.zhanghai.android.douya.util.ToastUtils;
import me.zhanghai.android.douya.util.ViewUtils;

public abstract class BaseItemFragment<SimpleItemType extends CollectableItem,
        ItemType extends SimpleItemType> extends Fragment
        implements BaseItemFragmentResource.Listener<ItemType> {

    private static final String KEY_PREFIX = BaseItemFragment.class.getName() + '.';

    private static final String EXTRA_ITEM_ID = KEY_PREFIX + "item_id";
    private static final String EXTRA_SIMPLE_ITEM = KEY_PREFIX + "simple_item";
    private static final String EXTRA_ITEM = KEY_PREFIX + "item";

    @BindView(R.id.appBarWrapper)
    AppBarWrapperLayout mAppBarWrapperLayout;
    @BindView(R.id.toolbar)
    TransparentDoubleClickToolbar mToolbar;
    @BindView(R.id.backdrop_wrapper)
    ViewGroup mBackdropWrapperLayout;
    @BindView(R.id.backdrop_layout)
    ViewGroup mBackdropLayout;
    @BindView(R.id.backdrop)
    RatioImageView mBackdropImage;
    @BindView(R.id.backdrop_scrim)
    View mBackdropScrim;
    @BindView(R.id.backdrop_play)
    ImageView mBackdropPlayImage;
    @BindView(R.id.content)
    ItemContentRecyclerView mContentList;

    private long mItemId;
    private SimpleItemType mSimpleItem;
    private ItemType mItem;

    private BaseItemFragmentResource<SimpleItemType, ItemType> mResource;

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

        mResource = onAttachResource(mItemId, mSimpleItem, mItem);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(mToolbar);
        StatusBarColorUtils.set(Color.TRANSPARENT, activity);
        ViewUtils.setLayoutFullscreen(activity);

        mBackdropImage.setRatio(16, 9);
        ViewCompat.setBackground(mBackdropScrim, DrawableUtils.makeScrimDrawable(Gravity.TOP));

        mContentList.setLayoutManager(new LinearLayoutManager(activity));
        mContentList.setAdapter(onCreateAdapter());
        mContentList.setBackdropRatio(mBackdropImage.getRatio());
        mContentList.setBackdropWrapper(mBackdropWrapperLayout);
        mContentList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private int mScrollY;
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (recyclerView.getChildCount() == 0) {
                    return;
                }
                View firstChild = recyclerView.getChildAt(0);
                int firstPosition = recyclerView.getChildAdapterPosition(firstChild);
                boolean firstItemInLayout = firstPosition == 0;
                if (mScrollY == 0) {
                    if (!firstItemInLayout) {
                        // We are restored from previous scroll position and we don't have a
                        // scrollY.
                        return;
                    } else {
                        mScrollY = recyclerView.getPaddingTop() - firstChild.getTop();
                    }
                } else {
                    mScrollY += dy;
                }
                // FIXME: Animate out backdrop layout later.
                mBackdropLayout.setTranslationY((float) -mScrollY / 2);
            }
        });
        int colorPrimaryDark = ViewUtils.getColorFromAttrRes(R.attr.colorPrimaryDark, 0, activity);
        mContentList.addOnScrollListener(new OnVerticalScrollWithPagingTouchSlopListener(activity) {
            private int mStatusBarColor = Color.TRANSPARENT;
            @Override
            public void onScrolledUp() {
                if (mAppBarWrapperLayout.isHidden()) {
                    mToolbar.setTransparent(!hasFirstChildReachedTop());
                }
                mAppBarWrapperLayout.show();
            }
            @Override
            public void onScrolledDown() {
                if (hasFirstChildReachedTop()) {
                    mAppBarWrapperLayout.hide();
                }
            }
            @Override
            public void onScrolled(int dy) {
                boolean initialize = dy == 0;
                boolean hasFirstChildReachedTop = hasFirstChildReachedTop();
                int statusBarColor = hasFirstChildReachedTop ? colorPrimaryDark : Color.TRANSPARENT;
                if (mStatusBarColor != statusBarColor) {
                    mStatusBarColor = statusBarColor;
                    if (initialize) {
                        StatusBarColorUtils.set(mStatusBarColor, activity);
                    } else {
                        StatusBarColorUtils.animateTo(mStatusBarColor, activity);
                    }
                }
                if (mAppBarWrapperLayout.isShowing()) {
                    if (initialize) {
                        mToolbar.setTransparent(!hasFirstChildReachedTop);
                    } else {
                        mToolbar.animateToTransparent(!hasFirstChildReachedTop);
                    }
                }
            }
            private boolean hasFirstChildReachedTop() {
                return RecyclerViewUtils.hasFirstChildReachedTop(mContentList,
                        mToolbar.getBottom());
            }
        });
        mToolbar.getBackground().setAlpha(0);
        mToolbar.setOnDoubleClickListener(view -> {
            mContentList.smoothScrollToPosition(0);
            return true;
        });

        if (mResource.isLoaded()) {
            mResource.notifyChangedIfLoaded();
        } else if (mResource.hasSimpleItem()) {
            updateWithSimpleItem(mResource.getSimpleItem());
        } else {
            // TODO
            //mContentStateLayout.setLoading();
        }
    }

    protected abstract BaseItemFragmentResource<SimpleItemType, ItemType> onAttachResource(
            long itemId, SimpleItemType simpleItem, ItemType item);

    protected abstract RecyclerView.Adapter<?> onCreateAdapter();

    @Override
    public void onDestroy() {
        super.onDestroy();

        mResource.detach();
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
    public void onLoadError(int requestCode, ApiError error) {
        LogUtils.e(error.toString());
        // TODO
        //mContentStateLayout.setError();
        Activity activity = getActivity();
        ToastUtils.show(ApiError.getErrorString(error, activity), activity);
    }

    @Override
    public void onItemChanged(int requestCode, ItemType newItem) {}

    protected void updateWithSimpleItem(SimpleItemType simpleItem) {
        getActivity().setTitle(simpleItem.title);
    }
}
