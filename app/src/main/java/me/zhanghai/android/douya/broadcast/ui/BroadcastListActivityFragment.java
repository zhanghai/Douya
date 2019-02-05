/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.broadcast.ui;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.network.api.info.apiv2.SimpleUser;
import me.zhanghai.android.douya.ui.AppBarHost;
import me.zhanghai.android.douya.ui.AppBarWrapperLayout;
import me.zhanghai.android.douya.ui.DoubleClickToolbar;
import me.zhanghai.android.douya.ui.WebViewActivity;
import me.zhanghai.android.douya.util.DoubanUtils;
import me.zhanghai.android.douya.util.FragmentUtils;
import me.zhanghai.android.douya.util.ShareUtils;
import me.zhanghai.android.douya.util.TransitionUtils;

public class BroadcastListActivityFragment extends Fragment implements AppBarHost {

    private static final String KEY_PREFIX = BroadcastListActivityFragment.class.getName() + '.';

    private static final String EXTRA_USER_ID_OR_UID = KEY_PREFIX + "user_id_or_uid";
    private static final String EXTRA_USER = KEY_PREFIX + "user";
    private static final String EXTRA_TOPIC = KEY_PREFIX + "topic";

    @BindView(R.id.appBarWrapper)
    AppBarWrapperLayout mAppBarWrapperLayout;
    @BindView(R.id.toolbar)
    DoubleClickToolbar mToolbar;

    private String mUserIdOrUid;
    private SimpleUser mUser;
    private String mTopic;

    public static BroadcastListActivityFragment newInstance(String userIdOrUid, SimpleUser user,
                                                            String topic) {
        //noinspection deprecation
        BroadcastListActivityFragment fragment = new BroadcastListActivityFragment();
        FragmentUtils.getArgumentsBuilder(fragment)
                .putString(EXTRA_USER_ID_OR_UID, userIdOrUid)
                .putParcelable(EXTRA_USER, user)
                .putString(EXTRA_TOPIC, topic);
        return fragment;
    }

    /**
     * @deprecated Use {@link #newInstance(String, SimpleUser, String)} instead.
     */
    public BroadcastListActivityFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        mUser = arguments.getParcelable(EXTRA_USER);
        if (mUser != null) {
            mUserIdOrUid = mUser.getIdOrUid();
        } else {
            mUserIdOrUid = arguments.getString(EXTRA_USER_ID_OR_UID);
        }
        mTopic = arguments.getString(EXTRA_TOPIC);

        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.broadcast_list_activity_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setTitle(getTitle());
        activity.setSupportActionBar(mToolbar);

        TransitionUtils.setupTransitionOnActivityCreated(this);

        if (savedInstanceState == null) {
            FragmentUtils.add(BroadcastListFragment.newInstance(mUserIdOrUid, mTopic), this,
                    R.id.broadcast_list_fragment);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.broadcast_list, menu);
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
    public void showAppBar() {
        mAppBarWrapperLayout.show();
    }

    @Override
    public void hideAppBar() {
        mAppBarWrapperLayout.hide();
    }

    @Override
    public void setToolBarOnDoubleClickListener(DoubleClickToolbar.OnDoubleClickListener listener) {
        mToolbar.setOnDoubleClickListener(listener);
    }

    private String getTitle() {
        // TODO: Load user.
        if (mUser != null) {
            return getString(R.string.broadcast_list_title_user_format, mUser.name);
        } else if (!TextUtils.isEmpty(mTopic)) {
            return getString(R.string.broadcast_list_title_topic_format, mTopic);
        } else {
            return getString(R.string.broadcast_list_title_default);
        }
    }

    private void share() {
        ShareUtils.shareText(makeUrl(), getActivity());
    }

    private void viewOnWeb() {
        startActivity(WebViewActivity.makeIntent(makeUrl(), true, getActivity()));
    }

    private String makeUrl() {
        //noinspection deprecation
        return DoubanUtils.makeBroadcastListUrl(mUser != null ? mUser.getUidOrId() : mUserIdOrUid,
                mTopic);
    }
}
