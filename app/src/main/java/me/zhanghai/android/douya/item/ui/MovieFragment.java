/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.item.ui;

import me.zhanghai.android.douya.item.content.BaseItemResource;
import me.zhanghai.android.douya.item.content.MovieResource;
import me.zhanghai.android.douya.network.api.info.frodo.Movie;
import me.zhanghai.android.douya.network.api.info.frodo.SimpleMovie;

public class MovieFragment extends BaseItemFragment<SimpleMovie, Movie> {

    public static MovieFragment newInstance(long movieId, SimpleMovie simpleMovie, Movie movie) {
        //noinspection deprecation
        MovieFragment fragment = new MovieFragment();
        fragment.setArguments(movieId, simpleMovie, movie);
        return fragment;
    }

    /**
     * @deprecated Use {@link #newInstance(long, SimpleMovie, Movie)} instead.
     */
    public MovieFragment() {}

    @Override
    protected BaseItemResource<SimpleMovie, Movie> onAttachItemResource(long movieId,
                                                                        SimpleMovie simpleMovie,
                                                                        Movie movie) {
        return MovieResource.attachTo(movieId, simpleMovie, movie, this);
    }
}
