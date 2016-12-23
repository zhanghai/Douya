/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.profile.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.VolleyError;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.customtabshelper.CustomTabsHelperFragment;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.followship.content.FollowUserManager;
import me.zhanghai.android.douya.link.NotImplementedManager;
import me.zhanghai.android.douya.network.api.ApiError;
import me.zhanghai.android.douya.network.api.info.apiv2.Broadcast;
import me.zhanghai.android.douya.network.api.info.apiv2.User;
import me.zhanghai.android.douya.network.api.info.apiv2.UserInfo;
import me.zhanghai.android.douya.network.api.info.frodo.Diary;
import me.zhanghai.android.douya.network.api.info.frodo.Review;
import me.zhanghai.android.douya.network.api.info.frodo.UserItems;
import me.zhanghai.android.douya.profile.content.ProfileResource;
import me.zhanghai.android.douya.profile.util.ProfileUtils;
import me.zhanghai.android.douya.ui.ContentStateLayout;
import me.zhanghai.android.douya.ui.CopyTextDialogFragment;
import me.zhanghai.android.douya.util.FragmentUtils;
import me.zhanghai.android.douya.util.LogUtils;
import me.zhanghai.android.douya.util.ToastUtils;
import me.zhanghai.android.douya.util.ViewUtils;

public class ProfileFragment extends Fragment implements ProfileResource.Listener,
        ProfileHeaderLayout.Listener, ProfileIntroductionLayout.Listener,
        ConfirmUnfollowUserDialogFragment.Listener {

    private static final String KEY_PREFIX = ProfileFragment.class.getName() + '.';

    private static final String EXTRA_USER_ID_OR_UID = KEY_PREFIX + "user_id_or_uid";
    private static final String EXTRA_USER = KEY_PREFIX + "user";
    private static final String EXTRA_USER_INFO = KEY_PREFIX + "user_info";

    @BindView(R.id.scroll)
    ProfileLayout mScrollLayout;
    @BindView(R.id.header)
    ProfileHeaderLayout mHeaderLayout;
    @BindView(R.id.dismiss)
    View mDismissView;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.contentState)
    ContentStateLayout mContentStateLayout;
    @BindView(R.id.content)
    RecyclerView mContentList;

    private String mUserIdOrUid;
    private User mUser;
    private UserInfo mUserInfo;

    private ProfileResource mProfileResource;

    private ProfileAdapter mProfileAdapter;

    public static ProfileFragment newInstance(String userIdOrUid, User user, UserInfo userInfo) {
        //noinspection deprecation
        ProfileFragment fragment = new ProfileFragment();
        Bundle arguments = FragmentUtils.ensureArguments(fragment);
        arguments.putString(EXTRA_USER_ID_OR_UID, userIdOrUid);
        arguments.putParcelable(EXTRA_USER, user);
        arguments.putParcelable(EXTRA_USER_INFO, userInfo);
        return fragment;
    }

    /**
     * @deprecated Use {@link #newInstance(String, User, UserInfo)} instead.
     */
    public ProfileFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        mUserIdOrUid = arguments.getString(EXTRA_USER_ID_OR_UID);
        mUser = arguments.getParcelable(EXTRA_USER);
        mUserInfo = arguments.getParcelable(EXTRA_USER_INFO);

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
        mProfileResource = ProfileResource.attachTo(mUserIdOrUid, mUser, mUserInfo, this);

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

        mDismissView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exit();
            }
        });

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(mToolbar);
        activity.getSupportActionBar().setTitle(null);

        if (mProfileResource.hasUserInfo()) {
            mHeaderLayout.bindUserInfo(mProfileResource.getUserInfo());
        } else if (mProfileResource.hasUser()) {
            mHeaderLayout.bindUser(mProfileResource.getUser());
        }
        mHeaderLayout.setListener(this);

        if (ViewUtils.hasSw600Dp(activity)) {
            mContentList.setLayoutManager(new StaggeredGridLayoutManager(2,
                    StaggeredGridLayoutManager.VERTICAL));
        } else {
            mContentList.setLayoutManager(new LinearLayoutManager(activity));
        }
        mProfileAdapter = new ProfileAdapter(this);
        mContentList.setAdapter(mProfileAdapter);
        if (mProfileResource.isLoaded()) {
            mProfileResource.notifyChangedIfLoaded();
        } else {
            mContentStateLayout.setLoading();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mProfileResource.detach();
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

        // TODO: Block or unblock.
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_send_doumail:
                // HACK: For testing
                //startActivity(ItemCollectionActivity.makeIntent(null, getActivity()));
                // TODO
                NotImplementedManager.showNotYetImplementedToast(getActivity());
                return true;
            case R.id.action_blacklist:
                // TODO
                NotImplementedManager.showNotYetImplementedToast(getActivity());
                return true;
            case R.id.action_report_abuse:
                // TODO
                NotImplementedManager.showNotYetImplementedToast(getActivity());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onLoadError(int requestCode, VolleyError error) {
        LogUtils.e(error.toString());
        mContentStateLayout.setError();
        Activity activity = getActivity();
        ToastUtils.show(ApiError.getErrorString(error, activity), activity);
    }

    @Override
    public void onUserInfoChanged(int requestCode, UserInfo newUserInfo) {
        mHeaderLayout.bindUserInfo(newUserInfo);
    }

    @Override
    public void onUserInfoWriteStarted(int requestCode) {
        mHeaderLayout.bindUserInfo(mProfileResource.getUserInfo());
    }

    @Override
    public void onUserInfoWriteFinished(int requestCode) {
        mHeaderLayout.bindUserInfo(mProfileResource.getUserInfo());
    }

    @Override
    public void onChanged(int requestCode, UserInfo newUserInfo, List<Broadcast> newBroadcastList,
                          List<User> newFollowingList, List<Diary> newDiaryList,
                          List<UserItems> newUserItemList, List<Review> newReviewList) {
        mProfileAdapter.setData(new ProfileAdapter.Data(newUserInfo, newBroadcastList,
                newFollowingList, newDiaryList, newUserItemList, newReviewList));
        mContentStateLayout.setLoaded(true);
    }

    @Override
    public void onEditProfile(UserInfo userInfo) {
        NotImplementedManager.showNotYetImplementedToast(getActivity());
    }

    @Override
    public void onFollowUser(UserInfo userInfo, boolean follow) {
        if (follow) {
            FollowUserManager.getInstance().write(userInfo, true, getActivity());
        } else {
            ConfirmUnfollowUserDialogFragment.show(this);
        }
    }

    @Override
    public void onUnfollowUser() {
        FollowUserManager.getInstance().write(mProfileResource.getUserInfo(), false, getActivity());
    }

    @Override
    public void onCopyText(String text) {
        CopyTextDialogFragment.show(text, this);
    }
}
