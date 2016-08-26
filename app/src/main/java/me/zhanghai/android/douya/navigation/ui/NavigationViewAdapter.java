/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.navigation.ui;

import android.support.design.widget.NavigationView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.util.ViewUtils;

public class NavigationViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_ACCOUNT_LIST = -1;

    private NavigationView mNavigationView;
    private RecyclerView.Adapter mMenuAdapter;
    private NavigationAccountListLayout.Adapter mAccountListAdapter;
    private NavigationAccountListLayout.Listener mAccountListListener;

    private boolean mShowingAccountList;

    private NavigationViewAdapter(NavigationView navigationView, RecyclerView.Adapter menuAdapter,
                                  NavigationAccountListLayout.Adapter accountListAdapter,
                                  NavigationAccountListLayout.Listener accountListListener) {

        mNavigationView = navigationView;
        mMenuAdapter = menuAdapter;
        mAccountListAdapter = accountListAdapter;
        mAccountListListener = accountListListener;

        // NavigationMenuAdapter only calls notifyDataSetChanged().
        mMenuAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                notifyDataSetChanged();
            }
            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                onChanged();
            }
            @Override
            public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
                onChanged();
            }
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                onChanged();
            }
            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                onChanged();
            }
            @Override
            public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                onChanged();
            }
        });
    }

    public static NavigationViewAdapter override(NavigationView navigationView,
                                                 NavigationAccountListLayout.Adapter accountListAdapter,
                                                 NavigationAccountListLayout.Listener accountListListener) {
        RecyclerView recyclerView = (RecyclerView) navigationView.getChildAt(
                navigationView.getChildCount() - 1);
        NavigationViewAdapter adapter = new NavigationViewAdapter(navigationView,
                recyclerView.getAdapter(), accountListAdapter, accountListListener);
        recyclerView.setAdapter(adapter);
        return adapter;
    }

    @Override
    public long getItemId(int position) {
        if (mShowingAccountList && position >= mNavigationView.getHeaderCount()) {
            return position;
        } else {
            return mMenuAdapter.getItemId(position);
        }
    }

    @Override
    public int getItemCount() {
        if (mShowingAccountList) {
            return mNavigationView.getHeaderCount() + 1;
        } else {
            return mMenuAdapter.getItemCount();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mShowingAccountList && position >= mNavigationView.getHeaderCount()) {
            return VIEW_TYPE_ACCOUNT_LIST;
        } else {
            return mMenuAdapter.getItemViewType(position);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ACCOUNT_LIST) {
            AccountListViewHolder holder = new AccountListViewHolder(ViewUtils.inflate(
                    R.layout.navigation_account_list, parent));
            holder.accountListLayout.setAdapter(mAccountListAdapter);
            holder.accountListLayout.setListener(mAccountListListener);
            return holder;
        } else {
            return mMenuAdapter.onCreateViewHolder(parent, viewType);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == VIEW_TYPE_ACCOUNT_LIST) {
            ((AccountListViewHolder) holder).accountListLayout.bind();
        } else {
            //noinspection unchecked
            mMenuAdapter.onBindViewHolder(holder, position);
        }
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        if (holder.getItemViewType() != VIEW_TYPE_ACCOUNT_LIST) {
            //noinspection unchecked
            mMenuAdapter.onViewRecycled(holder);
        }
    }

    public void showAccountList(boolean show) {

        if (mShowingAccountList == show) {
            return;
        }

        int headerCount = mNavigationView.getHeaderCount();
        int menuCount = mMenuAdapter.getItemCount() - headerCount;
        if (show) {
            notifyItemRangeRemoved(headerCount, menuCount);
            notifyItemInserted(headerCount);
        } else {
            notifyItemRemoved(headerCount);
            notifyItemRangeInserted(headerCount, menuCount);
        }
        mShowingAccountList = show;
    }

    private class AccountListViewHolder extends RecyclerView.ViewHolder {

        private NavigationAccountListLayout accountListLayout;

        public AccountListViewHolder(View itemView) {
            super(itemView);

            accountListLayout = (NavigationAccountListLayout) itemView;
        }
    }
}
