/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.item.ui;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.view.ViewCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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
import me.zhanghai.android.douya.ui.BarrierAdapter;
import me.zhanghai.android.douya.ui.ContentStateLayout;
import me.zhanghai.android.douya.ui.OnVerticalScrollWithPagingTouchSlopListener;
import me.zhanghai.android.douya.ui.RatioImageView;
import me.zhanghai.android.douya.ui.TransparentDoubleClickToolbar;
import me.zhanghai.android.douya.ui.WebViewActivity;
import me.zhanghai.android.douya.util.DrawableUtils;
import me.zhanghai.android.douya.util.FragmentUtils;
import me.zhanghai.android.douya.util.LogUtils;
import me.zhanghai.android.douya.util.RecyclerViewUtils;
import me.zhanghai.android.douya.util.ShareUtils;
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
    @BindView(R.id.contentState)
    ContentStateLayout mContentStateLayout;
    @BindView(R.id.content)
    ItemContentRecyclerView mContentList;
    @BindView(R.id.content_state_views)
    ItemContentStateViewsLayout mContentStateViewsLayout;

    private long mItemId;
    private SimpleItemType mSimpleItem;
    private ItemType mItem;

    protected BaseItemFragmentResource<SimpleItemType, ItemType> mResource;

    private BarrierAdapter mAdapter;

    public BaseItemFragment<SimpleItemType, ItemType> setArguments(long itemId,
                                                                   SimpleItemType simpleItem,
                                                                   ItemType item) {
        FragmentUtils.getArgumentsBuilder(this)
                .putLong(EXTRA_ITEM_ID, itemId)
                .putParcelable(EXTRA_SIMPLE_ITEM, simpleItem)
                .putParcelable(EXTRA_ITEM, item);
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
        return inflater.inflate(R.layout.item_fragment, container, false);
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
        float backdropRatio = getBackdropRatio();
        boolean hasBackdrop = backdropRatio > 0;
        if (hasBackdrop) {
            StatusBarColorUtils.set(Color.TRANSPARENT, activity);
            ViewUtils.setLayoutFullscreen(activity);
        }

        mBackdropImage.setRatio(backdropRatio);
        ViewCompat.setBackground(mBackdropScrim, DrawableUtils.makeScrimDrawable(Gravity.TOP));

        mContentList.setLayoutManager(new LinearLayoutManager(activity));
        mAdapter = onCreateAdapter();
        mContentList.setAdapter(mAdapter);
        mContentList.setBackdropRatio(backdropRatio);
        mContentList.setPaddingTopPaddingExtra(getContentListPaddingTopExtra());
        if (hasBackdrop) {
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
                            ViewUtils.setVisibleOrInvisible(mBackdropLayout, false);
                            return;
                        } else {
                            // We scrolled towards top so the first item became visible now.
                            // Won't do anything if it is not hidden.
                            ViewUtils.fadeIn(mBackdropLayout);
                            mScrollY = recyclerView.getPaddingTop() - firstChild.getTop();
                        }
                    } else {
                        mScrollY += dy;
                    }
                    // FIXME: Animate out backdrop layout later.
                    mBackdropLayout.setTranslationY((float) -mScrollY / 2);
                    mBackdropScrim.setTranslationY((float) mScrollY / 2);
                }
            });
            int colorPrimaryDark = ViewUtils.getColorFromAttrRes(R.attr.colorPrimaryDark, 0,
                    activity);
            mContentList.addOnScrollListener(
                    new OnVerticalScrollWithPagingTouchSlopListener(activity) {
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
                            int statusBarColor = hasFirstChildReachedTop ? colorPrimaryDark
                                    : Color.TRANSPARENT;
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
        } else {
            ViewUtils.setVisibleOrGone(mBackdropWrapperLayout, false);
            mContentList.addOnScrollListener(
                    new OnVerticalScrollWithPagingTouchSlopListener(activity) {
                        @Override
                        public void onScrolledUp() {
                            mAppBarWrapperLayout.show();
                        }
                        @Override
                        public void onScrolledDown() {
                            if (hasFirstChildReachedTop()) {
                                mAppBarWrapperLayout.hide();
                            }
                        }
                        private boolean hasFirstChildReachedTop() {
                            return RecyclerViewUtils.hasFirstChildReachedTop(mContentList, 0);
                        }
                    });
                }
        mToolbar.setOnDoubleClickListener(view -> {
            mContentList.smoothScrollToPosition(0);
            return true;
        });

        mContentStateViewsLayout.setBackdropRatio(backdropRatio);
        mContentStateViewsLayout.setPaddingTopPaddingExtra(getContentStateViewsPaddingTopExtra());

        if (mResource.hasSimpleItem()) {
            updateWithSimpleItem(mResource.getSimpleItem());
        }
        mContentStateLayout.setLoading();
        if (mResource.isAnyLoaded()) {
            mResource.notifyChanged();
        }

        if (hasBackdrop && mAdapter.getItemCount() == 0) {
            mToolbar.getBackground().setAlpha(0);
        }
    }

    protected abstract BaseItemFragmentResource<SimpleItemType, ItemType> onAttachResource(
            long itemId, SimpleItemType simpleItem, ItemType item);

    protected float getBackdropRatio() {
        return 16f / 9f;
    }

    protected abstract BarrierAdapter onCreateAdapter();

    protected int getContentListPaddingTopExtra() {
        return 0;
    }

    protected int getContentStateViewsPaddingTopExtra() {
        return 0;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mResource.detach();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.item, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().finish();
                return true;
            case R.id.action_share:
                share();
                return true;
            case R.id.action_view_on_web:
                viewOnWeb();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onLoadError(int requestCode, ApiError error) {
        LogUtils.e(error.toString());
        if (mAdapter.getItemCount() > 0) {
            mAdapter.setError();
        } else {
            mContentStateLayout.setError();
        }
        Activity activity = getActivity();
        ToastUtils.show(ApiError.getErrorString(error, activity), activity);
    }

    @Override
    public void onItemChanged(int requestCode, ItemType newItem) {}

    protected void updateWithSimpleItem(SimpleItemType simpleItem) {
        getActivity().setTitle(simpleItem.title);
    }

    private void share() {
        ShareUtils.shareText(makeUrl(), getActivity());
    }

    private void viewOnWeb() {
        startActivity(WebViewActivity.makeIntent(makeUrl(), true, getActivity()));
    }

    private String makeUrl() {
        if (mResource.hasSimpleItem()) {
            return mResource.getSimpleItem().getUrl();
        } else {
            return makeItemUrl(mResource.getItemId());
        }
    }

    protected abstract String makeItemUrl(long itemId);
}
