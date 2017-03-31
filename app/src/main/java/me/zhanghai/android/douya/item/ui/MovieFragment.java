/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.item.ui;

import android.support.v7.widget.RecyclerView;

import me.zhanghai.android.douya.item.content.BaseItemResource;
import me.zhanghai.android.douya.item.content.MovieResource;
import me.zhanghai.android.douya.network.api.info.frodo.Movie;
import me.zhanghai.android.douya.network.api.info.frodo.SimpleMovie;
import me.zhanghai.android.douya.util.ImageUtils;
import me.zhanghai.android.douya.util.ViewUtils;

public class MovieFragment extends BaseItemFragment<SimpleMovie, Movie> {

    private MovieAdapter mAdapter;

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

    @Override
    protected RecyclerView.Adapter<?> onCreateAdapter() {
        mAdapter = new MovieAdapter();
        return mAdapter;
    }

    @Override
    public void updateWithItem(Movie movie) {
        super.updateWithItem(movie);

        boolean hasTrailer = movie.trailer != null;
        if (hasTrailer) {
            ImageUtils.loadImage(mBackdropImage, movie.trailer.coverUrl);
        } else if (movie.poster != null) {
            ImageUtils.loadImage(mBackdropImage, movie.poster);
        } else {
            ImageUtils.loadImage(mBackdropImage, movie.cover);
        }
        ViewUtils.setVisibleOrGone(mBackdropPlayImage, hasTrailer);

        mAdapter.setData(new MovieAdapter.Data(movie));
    }

    @Override
    public void updateWithSimpleItem(SimpleMovie simpleMovie) {
        super.updateWithSimpleItem(simpleMovie);

        // FIXME: Remove, this is only for testing.
        ImageUtils.loadImage(mBackdropImage, simpleMovie.cover);
        ViewUtils.setVisibleOrGone(mBackdropPlayImage, false);
    }
}
