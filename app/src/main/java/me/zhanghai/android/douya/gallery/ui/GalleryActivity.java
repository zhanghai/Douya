/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.gallery.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

import me.zhanghai.android.douya.network.api.info.apiv2.Image;
import me.zhanghai.android.douya.network.api.info.frodo.ImageWithSize;
import me.zhanghai.android.douya.util.FragmentUtils;

public class GalleryActivity extends AppCompatActivity {

    private static final String KEY_PREFIX = GalleryActivity.class.getName() + '.';

    private static final String EXTRA_IMAGE_LIST = KEY_PREFIX + "image_list";
    private static final String EXTRA_POSITION = KEY_PREFIX + "position";

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

    public static Intent makeIntent(ImageWithSize image, Context context) {
        return makeIntent(image.getLarge().url, context);
    }

    public static Intent makeIntent(me.zhanghai.android.douya.network.api.info.frodo.Image image,
                                    Context context) {
        return makeIntent(image.getLarge(), context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Calls ensureSubDecor().
        findViewById(android.R.id.content);

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            ArrayList<String> imageList = intent.getStringArrayListExtra(EXTRA_IMAGE_LIST);
            int position = intent.getIntExtra(EXTRA_POSITION, 0);
            FragmentUtils.add(GalleryFragment.newInstance(imageList, position), this,
                    android.R.id.content);
        }
    }
}
