/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.settings.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.douya.BuildConfig;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.profile.ui.ProfileActivity;
import me.zhanghai.android.douya.scalpel.ScalpelHelperFragment;
import me.zhanghai.android.douya.ui.KonamiCodeDetector;

public class AboutFragment extends Fragment implements ConfirmEnableScalpelDialogFragment.Listener {

    @BindView(R.id.container)
    LinearLayout mContainerLayout;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.version)
    TextView mVersionText;
    @BindView(R.id.douban)
    Button mDoubanButton;

    public static AboutFragment newInstance() {
        //noinspection deprecation
        return new AboutFragment();
    }

    /**
     * @deprecated Use {@link #newInstance()} instead.
     */
    public AboutFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.about_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ScalpelHelperFragment.attachTo(this);

        final AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(mToolbar);
        activity.getSupportActionBar().setTitle(null);

        // Seems that ScrollView intercepts touch event, so we have to set the onTouchListener on a
        // view inside it.
        mContainerLayout.setOnTouchListener(new KonamiCodeDetector(activity) {
            @Override
            public void onDetected() {
                onEnableScalpel();
            }
        });

        mVersionText.setText(getString(R.string.about_version_format, BuildConfig.VERSION_NAME));
        mDoubanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.startActivity(ProfileActivity.makeIntent("douban-douya", activity));
            }
        });
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

    private void onEnableScalpel() {
        ConfirmEnableScalpelDialogFragment.show(this);
    }

    @Override
    public void enableScalpel() {
        ScalpelHelperFragment.setEnabled(true);
    }
}
