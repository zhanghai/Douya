/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.item.content;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import me.zhanghai.android.douya.network.api.info.frodo.CollectableItem;
import me.zhanghai.android.douya.network.api.info.frodo.Music;
import me.zhanghai.android.douya.network.api.info.frodo.SimpleMusic;
import me.zhanghai.android.douya.util.FragmentUtils;

public class MusicResource extends BaseItemResource<SimpleMusic, Music> {

    private static final String FRAGMENT_TAG_DEFAULT = MusicResource.class.getName();

    private static MusicResource newInstance(long musicId, SimpleMusic simpleMusic, Music music) {
        //noinspection deprecation
        MusicResource instance = new MusicResource();
        instance.setArguments(musicId, simpleMusic, music);
        return instance;
    }

    public static MusicResource attachTo(long musicId, SimpleMusic simpleMusic, Music music,
                                         Fragment fragment, String tag, int requestCode) {
        FragmentActivity activity = fragment.getActivity();
        MusicResource instance = FragmentUtils.findByTag(activity, tag);
        if (instance == null) {
            instance = newInstance(musicId, simpleMusic, music);
            FragmentUtils.add(instance, activity, tag);
        }
        instance.setTarget(fragment, requestCode);
        return instance;
    }

    public static MusicResource attachTo(long musicId, SimpleMusic simpleMusic, Music music,
                                         Fragment fragment) {
        return attachTo(musicId, simpleMusic, music, fragment, FRAGMENT_TAG_DEFAULT,
                REQUEST_CODE_INVALID);
    }

    /**
     * @deprecated Use {@code attachTo()} instead.
     */
    public MusicResource() {}

    @Override
    protected CollectableItem.Type getDefaultItemType() {
        return CollectableItem.Type.MUSIC;
    }
}
