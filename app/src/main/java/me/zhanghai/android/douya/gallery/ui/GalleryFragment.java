/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.gallery.ui;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.gallery.app.SaveImageService;
import me.zhanghai.android.douya.ui.ViewPagerTransformers;
import me.zhanghai.android.douya.util.FileUtils;
import me.zhanghai.android.douya.util.FragmentUtils;
import me.zhanghai.android.douya.util.IntentUtils;
import me.zhanghai.android.douya.util.ToastUtils;
import me.zhanghai.android.effortlesspermissions.AfterPermissionDenied;
import me.zhanghai.android.effortlesspermissions.EffortlessPermissions;
import me.zhanghai.android.effortlesspermissions.OpenAppDetailsDialogFragment;
import me.zhanghai.android.systemuihelper.SystemUiHelper;
import pub.devrel.easypermissions.AfterPermissionGranted;

public class GalleryFragment extends Fragment {

    private static final String KEY_PREFIX = GalleryFragment.class.getName() + '.';

    private static final String EXTRA_IMAGE_LIST = KEY_PREFIX + "image_list";
    private static final String EXTRA_POSITION = KEY_PREFIX + "position";

    private static final int REQUEST_CODE_SAVE_IMAGE_PERMISSION = 1;
    private static final String[] PERMISSIONS_SAVE_IMAGE = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @BindInt(android.R.integer.config_mediumAnimTime)
    int mToolbarHideDuration;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.viewPager)
    ViewPager mViewPager;

    private MenuItem mSaveMenuItem;
    private MenuItem mShareMenuItem;

    private ArrayList<String> mImageList;
    private int mInitialPosition;

    private SystemUiHelper mSystemUiHelper;

    private GalleryAdapter mAdapter;

    public static GalleryFragment newInstance(ArrayList<String> imageList, int position) {
        //noinspection deprecation
        GalleryFragment fragment = new GalleryFragment();
        Bundle arguments = FragmentUtils.ensureArguments(fragment);
        arguments.putStringArrayList(EXTRA_IMAGE_LIST, imageList);
        arguments.putInt(EXTRA_POSITION, position);
        return fragment;
    }

    /**
     * @deprecated Use {@link #newInstance(ArrayList, int)} instead.
     */
    public GalleryFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        mImageList = arguments.getStringArrayList(EXTRA_IMAGE_LIST);
        mInitialPosition = arguments.getInt(EXTRA_POSITION);

        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.gallery_fragment, container, false);
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
        activity.setSupportActionBar(mToolbar);

        mSystemUiHelper = new SystemUiHelper(activity, SystemUiHelper.LEVEL_IMMERSIVE,
                SystemUiHelper.FLAG_IMMERSIVE_STICKY,
                new SystemUiHelper.OnVisibilityChangeListener() {
                    @Override
                    public void onVisibilityChange(boolean visible) {
                        if (visible) {
                            mToolbar.animate()
                                    .alpha(1)
                                    .translationY(0)
                                    .setDuration(mToolbarHideDuration)
                                    .setInterpolator(new FastOutSlowInInterpolator())
                                    .start();
                        } else {
                            mToolbar.animate()
                                    .alpha(0)
                                    .translationY(-mToolbar.getBottom())
                                    .setDuration(mToolbarHideDuration)
                                    .setInterpolator(new FastOutSlowInInterpolator())
                                    .start();
                        }
                    }
                });
        // This will set up window flags.
        mSystemUiHelper.show();

        mAdapter = new GalleryAdapter(mImageList, new GalleryAdapter.Listener() {
            @Override
            public void onTap() {
                mSystemUiHelper.toggle();
            }
            @Override
            public void onFileDownloaded(int position) {
                if (position == mViewPager.getCurrentItem()) {
                    updateOptionsMenu();
                }
            }
        });
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(mInitialPosition);
        mViewPager.setPageTransformer(true, new ViewPagerTransformers.Depth());
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                updateTitle();
                updateOptionsMenu();
            }
        });
        updateTitle();
    }

    private void updateTitle() {
        int size = mImageList.size();
        if (size <= 1) {
            return;
        }
        getActivity().setTitle(getString(R.string.gallery_title_multiple_format,
                mViewPager.getCurrentItem() + 1, size));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.gallery, menu);
        mSaveMenuItem = menu.findItem(R.id.action_save);
        mShareMenuItem = menu.findItem(R.id.action_share);
        updateOptionsMenu();
    }

    private void updateOptionsMenu() {
        boolean hasFile = mAdapter.getFile(mViewPager.getCurrentItem()) != null;
        mSaveMenuItem.setEnabled(hasFile);
        mShareMenuItem.setEnabled(hasFile);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().finish();
                return true;
            case R.id.action_save:
                saveImage();
                return true;
            case R.id.action_share:
                shareImage();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EffortlessPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults,
                this);
    }

    @AfterPermissionGranted(REQUEST_CODE_SAVE_IMAGE_PERMISSION)
    private void saveImage() {
        if (EffortlessPermissions.hasPermissions(this, PERMISSIONS_SAVE_IMAGE)) {
            saveImageWithPermission();
        } else if (EffortlessPermissions.somePermissionPermanentlyDenied(this,
                PERMISSIONS_SAVE_IMAGE)) {
            OpenAppDetailsDialogFragment.show(
                    R.string.gallery_save_permission_permanently_denied_message,
                    R.string.gallery_save_permission_permanently_denied_open_settings, this);
        } else  {
            EffortlessPermissions.requestPermissions(this,
                    R.string.gallery_save_permission_request_message,
                    REQUEST_CODE_SAVE_IMAGE_PERMISSION, PERMISSIONS_SAVE_IMAGE);
        }
    }

    @AfterPermissionDenied(REQUEST_CODE_SAVE_IMAGE_PERMISSION)
    private void onSaveImagePermissionDenied() {
        ToastUtils.show(R.string.gallery_save_permission_denied, getActivity());
    }

    private void saveImageWithPermission() {
        File file = mAdapter.getFile(mViewPager.getCurrentItem());
        if (file == null) {
            return;
        }
        SaveImageService.start(file, getActivity());
    }

    private void shareImage() {
        File file = mAdapter.getFile(mViewPager.getCurrentItem());
        if (file == null) {
            return;
        }
        Uri uri = FileUtils.getContentUri(file, getActivity());
        startActivity(Intent.createChooser(IntentUtils.makeSendImage(uri, null), getText(
                R.string.share_activity_chooser_title)));
    }
}
