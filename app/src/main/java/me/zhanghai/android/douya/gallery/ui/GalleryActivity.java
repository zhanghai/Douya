/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.gallery.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.zhanghai.android.douya.ui.ImageItem;
import me.zhanghai.android.douya.util.FragmentUtils;

public class GalleryActivity extends AppCompatActivity {

    private static final String KEY_PREFIX = GalleryActivity.class.getName() + '.';

    private static final String EXTRA_IMAGE_LIST = KEY_PREFIX + "image_list";
    private static final String EXTRA_POSITION = KEY_PREFIX + "position";

    private static Intent makeIntent(ArrayList<Uri> imageList, int position, Context context) {
        return new Intent(context, GalleryActivity.class)
                .putParcelableArrayListExtra(EXTRA_IMAGE_LIST, imageList)
                .putExtra(EXTRA_POSITION, position);
    }

    public static Intent makeIntent(List<Uri> imageList, int position, Context context) {
        return makeIntent(new ArrayList<>(imageList), position, context);
    }

    public static Intent makeUrlListIntent(List<String> imageUrlList, int position,
                                           Context context) {
        ArrayList<Uri> imageUriList = new ArrayList<>();
        for (String imageUrl: imageUrlList) {
            imageUriList.add(Uri.parse(imageUrl));
        }
        return makeIntent(imageUriList, position, context);
    }

    public static Intent makeImageListIntent(List<? extends ImageItem> imageList, int position,
                                             Context context) {
        ArrayList<Uri> imageUriList = new ArrayList<>();
        for (ImageItem image : imageList) {
            imageUriList.add(Uri.parse(image.getLargeUrl()));
        }
        return makeIntent(imageUriList, position, context);
    }

    public static Intent makeIntent(Uri imageUri, Context context) {
        return makeIntent(Collections.singletonList(imageUri), 0, context);
    }

    public static Intent makeIntent(String imageUrl, Context context) {
        return makeIntent(Uri.parse(imageUrl), context);
    }

    public static Intent makeIntent(ImageItem image, Context context) {
        return makeIntent(image.getLargeUrl(), context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Calls ensureSubDecor().
        findViewById(android.R.id.content);

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            ArrayList<Uri> imageList = intent.getParcelableArrayListExtra(EXTRA_IMAGE_LIST);
            int position = intent.getIntExtra(EXTRA_POSITION, 0);
            FragmentUtils.add(GalleryFragment.newInstance(imageList, position), this,
                    android.R.id.content);
        }
    }
}
