/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.item.ui;

import android.support.v7.widget.RecyclerView;

import java.util.List;

import me.zhanghai.android.douya.item.content.BaseItemFragmentResource;
import me.zhanghai.android.douya.item.content.MovieFragmentResource;
import me.zhanghai.android.douya.network.api.info.frodo.CelebrityList;
import me.zhanghai.android.douya.network.api.info.frodo.Movie;
import me.zhanghai.android.douya.network.api.info.frodo.Photo;
import me.zhanghai.android.douya.network.api.info.frodo.Rating;
import me.zhanghai.android.douya.network.api.info.frodo.Review;
import me.zhanghai.android.douya.network.api.info.frodo.SimpleMovie;
import me.zhanghai.android.douya.util.ImageUtils;
import me.zhanghai.android.douya.util.ViewUtils;

public class MovieFragment extends BaseItemFragment<SimpleMovie, Movie>
        implements MovieFragmentResource.Listener {

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
    protected BaseItemFragmentResource<SimpleMovie, Movie> onAttachResource(long itemId,
                                                                            SimpleMovie simpleItem,
                                                                            Movie item) {
        return MovieFragmentResource.attachTo(itemId, simpleItem, item, this);
    }

    @Override
    protected RecyclerView.Adapter<?> onCreateAdapter() {
        mAdapter = new MovieAdapter();
        return mAdapter;
    }

    @Override
    public void updateWithSimpleItem(SimpleMovie simpleMovie) {
        super.updateWithSimpleItem(simpleMovie);

        // FIXME: Remove, this is only for testing.
        ImageUtils.loadImage(mBackdropImage, simpleMovie.cover);
        ViewUtils.setVisibleOrGone(mBackdropPlayImage, false);
    }

    @Override
    public void onChanged(int requestCode, Movie newMovie, Rating newRating,
                          CelebrityList newCelebrityList, List<Photo> newPhotoList,
                          List<Review> newReviewList) {
        update(newMovie, newRating, newCelebrityList, newPhotoList, newReviewList);
    }

    private void update(Movie movie, Rating rating, CelebrityList celebrityList,
                        List<Photo> photoList, List<Review> reviewList) {

        super.updateWithSimpleItem(movie);

        boolean hasTrailer = movie.trailer != null;
        if (hasTrailer) {
            ImageUtils.loadImage(mBackdropImage, movie.trailer.coverUrl);
        } else if (!photoList.isEmpty()) {
            ImageUtils.loadLargeImage(mBackdropImage, photoList.get(0));
        } else if (movie.poster != null) {
            ImageUtils.loadLargeImage(mBackdropImage, movie.poster);
        } else {
            ImageUtils.loadLargeImage(mBackdropImage, movie.cover);
        }
        ViewUtils.setVisibleOrGone(mBackdropPlayImage, hasTrailer);

        mAdapter.setData(new MovieAdapter.Data(movie, rating, celebrityList, photoList,
                reviewList));
    }
}
