/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.gallery.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.douya.BuildConfig;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.gallery.app.SaveImageService;
import me.zhanghai.android.douya.network.api.info.apiv2.Image;
import me.zhanghai.android.douya.ui.ViewPagerTransformers;
import me.zhanghai.android.douya.util.IntentUtils;
import me.zhanghai.android.systemuihelper.SystemUiHelper;

public class GalleryActivity extends AppCompatActivity {

    private static final String KEY_PREFIX = GalleryActivity.class.getName() + '.';

    private static final String EXTRA_IMAGE_LIST = KEY_PREFIX + "image_list";
    private static final String EXTRA_POSITION = KEY_PREFIX + "position";

    @BindInt(android.R.integer.config_mediumAnimTime)
    int mToolbarHideDuration;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.viewPager)
    ViewPager mViewPager;

    private SystemUiHelper mSystemUiHelper;

    private MenuItem mSaveMenuItem;
    private MenuItem mShareMenuItem;

    private GalleryAdapter mAdapter;

    public static Intent makeIntent(ArrayList<String> imageList, int position, Context context) {
        return new Intent(context, GalleryActivity.class)
                .putStringArrayListExtra(EXTRA_IMAGE_LIST, imageList)
                .putExtra(EXTRA_POSITION, position);
    }

    public static Intent makeImageListIntent(ArrayList<Image> imageList, int position,
                                             Context context) {
        ArrayList<String> imageUrlList = new ArrayList<>();
        for (Image image : imageList) {
            imageUrlList.add(image.getLargest());
        }
        return makeIntent(imageUrlList, position, context);
    }

    public static Intent makeIntent(String image, Context context) {
        ArrayList<String> imageList = new ArrayList<>();
        imageList.add(image);
        return makeIntent(imageList, 0, context);
    }

    public static Intent makeIntent(Image image, Context context) {
        return makeIntent(image.getLargest(), context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.gallery_activity);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        mSystemUiHelper = new SystemUiHelper(this, SystemUiHelper.LEVEL_IMMERSIVE,
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

        ArrayList<String> imageList = getIntent().getStringArrayListExtra(EXTRA_IMAGE_LIST);
        mAdapter = new GalleryAdapter(imageList, new GalleryAdapter.Listener() {
            @Override
            public void onTap() {
                mSystemUiHelper.toggle();
            }
            @Override
            public void onFileDownloaded() {
                updateOptionsMenu();
            }
        });
        mViewPager.setAdapter(mAdapter);
        int position = getIntent().getIntExtra(EXTRA_POSITION, 0);
        mViewPager.setCurrentItem(position);
        mViewPager.setPageTransformer(true, new ViewPagerTransformers.Depth());
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                updateOptionsMenu();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.gallery, menu);
        mSaveMenuItem = menu.findItem(R.id.action_save);
        mShareMenuItem = menu.findItem(R.id.action_share);
        updateOptionsMenu();
        return true;
    }

    private void updateOptionsMenu() {
        File file = mAdapter.getFile(mViewPager.getCurrentItem());
        boolean hasFile = file != null;
        mSaveMenuItem.setEnabled(hasFile);
        mShareMenuItem.setEnabled(hasFile);
        if (hasFile) {
            ShareActionProvider shareActionProvider = (ShareActionProvider)
                    MenuItemCompat.getActionProvider(mShareMenuItem);
            Uri uri = FileProvider.getUriForFile(this, BuildConfig.FILE_PROVIDIER_AUTHORITY, file);
            shareActionProvider.setShareIntent(IntentUtils.makeSendImage(uri, null));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_save:
                saveImage();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveImage() {
        File file = mAdapter.getFile(mViewPager.getCurrentItem());
        if (file == null) {
            return;
        }
        SaveImageService.start(file, this);
    }
}
