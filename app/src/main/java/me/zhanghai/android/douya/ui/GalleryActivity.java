/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.BindInt;
import butterknife.ButterKnife;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.network.api.info.Image;
import me.zhanghai.android.systemuihelper.SystemUiHelper;

public class GalleryActivity extends AppCompatActivity {

    private static final String KEY_PREFIX = GalleryActivity.class.getSimpleName() + '.';

    public static final String EXTRA_IMAGE_LIST = KEY_PREFIX + "image_list";
    public static final String EXTRA_POSITION = KEY_PREFIX + "position";

    @BindInt(android.R.integer.config_mediumAnimTime)
    int mToolbarHideDuration;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.viewPager)
    ViewPager mViewPager;

    private SystemUiHelper mSystemUiHelper;

    public static Intent makeIntent(ArrayList<Image> imageList, int position, Context context) {
        return new Intent(context, GalleryActivity.class)
                .putParcelableArrayListExtra(EXTRA_IMAGE_LIST, imageList)
                .putExtra(EXTRA_POSITION, position);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.gallery_activity);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

        ArrayList<Image> imageList = getIntent().getParcelableArrayListExtra(EXTRA_IMAGE_LIST);
        mViewPager.setAdapter(new GalleryAdapter(imageList, new GalleryAdapter.OnTapListener() {
            @Override
            public void onTap() {
                mSystemUiHelper.toggle();
            }
        }));
        int position = getIntent().getIntExtra(EXTRA_POSITION, 0);
        mViewPager.setCurrentItem(position);
        mViewPager.setPageTransformer(true, new ViewPagerTransformers.Depth());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
