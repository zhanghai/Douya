/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.profile.ui;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import me.zhanghai.android.customtabshelper.CustomTabsHelperFragment;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.followship.content.FollowUserManager;
import me.zhanghai.android.douya.link.NotImplementedManager;
import me.zhanghai.android.douya.network.api.ApiError;
import me.zhanghai.android.douya.network.api.info.frodo.Broadcast;
import me.zhanghai.android.douya.network.api.info.apiv2.User;
import me.zhanghai.android.douya.network.api.info.frodo.Diary;
import me.zhanghai.android.douya.network.api.info.frodo.SimpleReview;
import me.zhanghai.android.douya.network.api.info.frodo.SimpleUser;
import me.zhanghai.android.douya.network.api.info.frodo.UserItems;
import me.zhanghai.android.douya.profile.content.ProfileResource;
import me.zhanghai.android.douya.profile.util.ProfileUtils;
import me.zhanghai.android.douya.ui.ContentStateLayout;
import me.zhanghai.android.douya.ui.CopyTextDialogFragment;
import me.zhanghai.android.douya.ui.DoubleClickToolbar;
import me.zhanghai.android.douya.ui.WebViewActivity;
import me.zhanghai.android.douya.util.DoubanUtils;
import me.zhanghai.android.douya.util.FragmentUtils;
import me.zhanghai.android.douya.util.LogUtils;
import me.zhanghai.android.douya.util.ShareUtils;
import me.zhanghai.android.douya.util.ToastUtils;
import me.zhanghai.android.douya.util.ViewUtils;

public class ProfileFragment extends Fragment implements ProfileResource.Listener,
        ProfileHeaderLayout.Listener, ProfileIntroductionLayout.Listener,
        ConfirmUnfollowUserDialogFragment.Listener {

    private static final String KEY_PREFIX = ProfileFragment.class.getName() + '.';

    private static final String EXTRA_USER_ID_OR_UID = KEY_PREFIX + "user_id_or_uid";
    private static final String EXTRA_SIMPLE_USER = KEY_PREFIX + "simple_user";
    private static final String EXTRA_USER = KEY_PREFIX + "user";

    @BindView(R.id.scroll)
    ProfileLayout mScrollLayout;
    @BindView(R.id.header)
    ProfileHeaderLayout mHeaderLayout;
    @BindView(R.id.dismiss)
    View mDismissView;
    @BindView(R.id.toolbar)
    DoubleClickToolbar mToolbar;
    @BindViews({
            R.id.profile_header_animate_changes_layout_1,
            R.id.profile_header_animate_changes_layout_2
    })
    ViewGroup[] mAnimateChangesLayouts;
    @BindView(R.id.contentState)
    ContentStateLayout mContentStateLayout;
    @BindView(R.id.content)
    RecyclerView mContentList;

    private String mUserIdOrUid;
    private me.zhanghai.android.douya.network.api.info.apiv2.SimpleUser mSimpleUser;
    private User mUser;

    private ProfileResource mResource;

    private ProfileAdapter mAdapter;

    public static ProfileFragment newInstance(
            String userIdOrUid,
            me.zhanghai.android.douya.network.api.info.apiv2.SimpleUser simpleUser, User user) {
        //noinspection deprecation
        ProfileFragment fragment = new ProfileFragment();
        FragmentUtils.getArgumentsBuilder(fragment)
                .putString(EXTRA_USER_ID_OR_UID, userIdOrUid)
                .putParcelable(EXTRA_SIMPLE_USER, simpleUser)
                .putParcelable(EXTRA_USER, user);
        return fragment;
    }

    /**
     * @deprecated Use {@link #newInstance(String, me.zhanghai.android.douya.network.api.info.apiv2.SimpleUser, User)} instead.
     */
    public ProfileFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        mUserIdOrUid = arguments.getString(EXTRA_USER_ID_OR_UID);
        mSimpleUser = arguments.getParcelable(EXTRA_SIMPLE_USER);
        mUser = arguments.getParcelable(EXTRA_USER);

        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        int layoutRes = ProfileUtils.shouldUseWideLayout(inflater.getContext()) ?
                R.layout.profile_fragment_wide : R.layout.profile_fragment;
        return inflater.inflate(layoutRes, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        CustomTabsHelperFragment.attachTo(this);
        mResource = ProfileResource.attachTo(mUserIdOrUid, mSimpleUser, mUser, this);

        mScrollLayout.setListener(new ProfileLayout.Listener() {
            @Override
            public void onEnterAnimationEnd() {}
            @Override
            public void onExitAnimationEnd() {
                getActivity().finish();
            }
        });
        if (savedInstanceState == null) {
            mScrollLayout.enter();
        }

        mDismissView.setOnClickListener(view -> exit());

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(mToolbar);
        activity.getSupportActionBar().setTitle(null);

        if (mResource.hasUser()) {
            mHeaderLayout.bindUser(mResource.getUser());
        } else if (mResource.hasSimpleUser()) {
            mHeaderLayout.bindSimpleUser(mResource.getSimpleUser());
        }
        mHeaderLayout.setListener(this);
        mToolbar.setOnDoubleClickListener(view -> {
            if (!mScrollLayout.isHeaderCollapsed()) {
                return false;
            }
            mScrollLayout.animateHeaderViewScroll(false);
            return true;
        });

        if (ViewUtils.hasSw600Dp(activity)) {
            mContentList.setLayoutManager(new StaggeredGridLayoutManager(2,
                    StaggeredGridLayoutManager.VERTICAL));
        } else {
            mContentList.setLayoutManager(new LinearLayoutManager(activity));
        }
        mAdapter = new ProfileAdapter(this);
        mContentList.setAdapter(mAdapter);

        mContentStateLayout.setLoading();
        if (mResource.isAnyLoaded()) {
            mResource.notifyChangedIfLoaded();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mResource.detach();
    }

    public void onBackPressed() {
        exit();
    }

    private void exit() {
        mScrollLayout.exit();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.profile, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        updateOptionsMenu();
    }

    private void updateOptionsMenu() {
        // TODO: Block or unblock.
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_send_doumail:
                sendDoumail();
                return true;
            case R.id.action_blacklist:
                // TODO
                NotImplementedManager.showNotYetImplementedToast(getActivity());
                return true;
            case R.id.action_report_abuse:
                // TODO
                NotImplementedManager.showNotYetImplementedToast(getActivity());
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
    public void onUserChanged(int requestCode, User newUser) {
        // WORKAROUND: Fix for LayoutTransition visual glitch when view is scrolling.
        if (!mScrollLayout.isHeaderOpen()) {
            for (ViewGroup animateChangesLayout : mAnimateChangesLayouts) {
                animateChangesLayout.setLayoutTransition(null);
            }
        }
        mHeaderLayout.bindUser(newUser);
        updateOptionsMenu();
    }

    @Override
    public void onUserWriteStarted(int requestCode) {
        mHeaderLayout.bindUser(mResource.getUser());
    }

    @Override
    public void onUserWriteFinished(int requestCode) {
        mHeaderLayout.bindUser(mResource.getUser());
    }

    @Override
    public void onChanged(int requestCode, User newUser, List<Broadcast> newBroadcastList,
                          List<SimpleUser> newFollowingList, List<Diary> newDiaryList,
                          List<UserItems> newUserItemList, List<SimpleReview> newReviewList) {
        mAdapter.setData(new ProfileDataAdapter.Data(newUser, newBroadcastList, newFollowingList,
                newDiaryList, newUserItemList, newReviewList));
        if (mAdapter.getItemCount() > 0) {
            mContentStateLayout.setLoaded(true);
        }
        updateOptionsMenu();
    }

    private void sendDoumail() {
        String userIdOrUid = mResource.getUserIdOrUid();
        NotImplementedManager.sendDoumail(userIdOrUid, getActivity());
    }

    private void share() {
        ShareUtils.shareText(makeUrl(), getActivity());
    }

    private void viewOnWeb() {
        startActivity(WebViewActivity.makeIntent(makeUrl(), true, getActivity()));
    }

    private String makeUrl() {
        if (mResource.hasSimpleUser()) {
            return mResource.getSimpleUser().getUrl();
        } else {
            return DoubanUtils.makeUserUrl(mResource.getUserIdOrUid());
        }
    }

    @Override
    public void onEditProfile(User user) {
        NotImplementedManager.editProfile(getActivity());
    }

    @Override
    public void onFollowUser(User user, boolean follow) {
        if (follow) {
            FollowUserManager.getInstance().write(user, true, getActivity());
        } else {
            ConfirmUnfollowUserDialogFragment.show(this);
        }
    }

    @Override
    public void onUnfollowUser() {
        FollowUserManager.getInstance().write(mResource.getUser(), false, getActivity());
    }

    @Override
    public void onCopyText(String text) {
        CopyTextDialogFragment.show(text, this);
    }
}
