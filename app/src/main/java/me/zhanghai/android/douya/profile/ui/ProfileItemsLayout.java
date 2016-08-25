/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.profile.ui;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.network.api.info.apiv2.UserInfo;
import me.zhanghai.android.douya.network.api.info.frodo.CollectedItem;
import me.zhanghai.android.douya.network.api.info.frodo.Item;
import me.zhanghai.android.douya.network.api.info.frodo.UserItems;
import me.zhanghai.android.douya.ui.FriendlyCardView;
import me.zhanghai.android.douya.util.ViewUtils;

public abstract class ProfileItemsLayout extends FriendlyCardView {

    @BindView(R.id.title)
    TextView mTitleText;
    @BindView(R.id.item_list)
    RecyclerView mItemList;
    @BindView(R.id.empty)
    TextView mEmptyView;
    @BindView(R.id.view_more)
    TextView mViewMoreText;
    @BindView(R.id.secondary)
    TextView mSecondaryText;
    @BindView(R.id.tertiary)
    TextView mTertiaryText;

    private String mUserIdOrUid;

    private ProfileItemAdapter mItemAdapter;

    public ProfileItemsLayout(Context context) {
        super(context);

        init();
    }

    public ProfileItemsLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public ProfileItemsLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    private void init() {

        ViewUtils.inflateInto(R.layout.profile_items_layout, this);
        ButterKnife.bind(this);

        mItemList.setHasFixedSize(true);
        mItemList.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));
        mItemAdapter = new ProfileItemAdapter();
        mItemList.setAdapter(mItemAdapter);
    }

    protected void bind(UserItems primaryItems, UserItems secondaryItems, UserItems tertiaryItems) {

        final Context context = getContext();
        CollectedItem.State state = primaryItems.getState();
        Item.Type type = primaryItems.getType();
        String stateString = state.getString(type, context);
        mTitleText.setText(stateString);
        OnClickListener viewMoreListener = new OnClickListener() {
            @Override
            public void onClick(View view) {
                onViewPrimaryItems();
            }
        };
        mTitleText.setOnClickListener(viewMoreListener);
        mViewMoreText.setOnClickListener(viewMoreListener);

        mItemAdapter.replace(primaryItems.items);
        ViewUtils.setVisibleOrGone(mItemList, !primaryItems.items.isEmpty());
        mEmptyView.setText(context.getString(R.string.profile_items_empty_format, stateString,
                type.getName(context)));
        ViewUtils.setVisibleOrGone(mEmptyView, primaryItems.items.isEmpty());

        if (primaryItems.total > primaryItems.items.size()) {
            mViewMoreText.setText(context.getString(R.string.view_more_with_count_format,
                    primaryItems.total));
        } else {
            mViewMoreText.setVisibility(GONE);
        }

        if (secondaryItems != null && secondaryItems.total > 0) {
            mSecondaryText.setText(context.getString(R.string.profile_items_non_primary_format,
                    secondaryItems.getState().getString(secondaryItems.getType(), context),
                    secondaryItems.total));
            mSecondaryText.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    onViewSecondaryItems();
                }
            });
        } else {
            mSecondaryText.setVisibility(GONE);
        }

        if (tertiaryItems != null && tertiaryItems.total > 0) {
            mTertiaryText.setText(context.getString(R.string.profile_items_non_primary_format,
                    tertiaryItems.getState().getString(tertiaryItems.getType(), context),
                    tertiaryItems.total));
            mTertiaryText.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    onViewTertiaryItems();
                }
            });
        } else {
            mTertiaryText.setVisibility(GONE);
        }
    }

    public void bind(UserInfo userInfo, List<UserItems> userItemList) {

        mUserIdOrUid = userInfo.getIdOrUid();

        UserItems primaryItems = null;
        UserItems secondaryItems = null;
        UserItems tertiaryItems = null;
        Item.Type type = getItemType();
        for (UserItems userItems : userItemList) {
            if (userItems.getType() == type) {
                switch (userItems.getState()) {
                    case TODO:
                        tertiaryItems = userItems;
                        break;
                    case DOING:
                        secondaryItems = userItems;
                        break;
                    case DONE:
                        primaryItems = userItems;
                        break;
                }
            }
        }
        if (primaryItems == null) {
            // HACK: Frodo API omits the done item if empty, but we need it.
            primaryItems = new UserItems();
            //noinspection deprecation
            primaryItems.type = type.getApiString();
            //noinspection deprecation
            primaryItems.state = CollectedItem.State.DONE.getApiString();
        }
        bind(primaryItems, secondaryItems, tertiaryItems);
    }

    protected String getUserIdOrUid() {
        return mUserIdOrUid;
    }

    protected abstract Item.Type getItemType();

    protected abstract void onViewPrimaryItems();

    protected abstract void onViewSecondaryItems();

    protected abstract void onViewTertiaryItems();
}
