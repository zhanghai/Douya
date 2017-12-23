/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.content;

import me.zhanghai.android.douya.network.api.ApiError;
import me.zhanghai.android.douya.network.api.ApiRequest;

public abstract class MoreListResourceFragment<ResponseType, ResourceListType>
        extends ListResourceFragment<ResponseType, ResourceListType> {

    private static final int DEFAULT_LOAD_COUNT = 20;

    private boolean mLoadingMore;
    private boolean mCanLoadMore = true;

    private int mLoadCount;

    protected abstract ResourceListType addAll(ResourceListType resource, ResourceListType more);

    @Override
    protected void set(ResourceListType resource) {
        super.set(resource);

        mCanLoadMore = getSize(resource) == mLoadCount;
    }

    protected void append(ResourceListType more) {
        super.set(addAll(get(), more));

        mCanLoadMore = getSize(more) == mLoadCount;
    }

    protected void setCanLoadMore(boolean canLoadMore) {
        mCanLoadMore = canLoadMore;
    }

    public boolean isLoadingMore() {
        return mLoadingMore;
    }

    @Override
    protected void onLoadOnStart() {
        load(false);
    }

    @Override
    public final void load() {
        throw new UnsupportedOperationException("Use load(boolean, int) instead");
    }

    public void load(boolean more, int count) {

        if (isLoading() || (more && !mCanLoadMore)) {
            return;
        }

        mLoadingMore = more;
        mLoadCount = count;
        super.load();
    }

    public void load(boolean loadMore) {
        load(loadMore, getDefaultLoadCount());
    }

    protected int getDefaultLoadCount() {
        return DEFAULT_LOAD_COUNT;
    }

    @Override
    protected final ApiRequest<ResponseType> onCreateRequest() {
        return onCreateRequest(mLoadingMore, mLoadCount);
    }

    protected ApiRequest<ResponseType> onCreateRequest(boolean more, int count) {
        return onCreateRequest(more && has() ? getSize(get()) : null, count);
    }

    protected abstract ApiRequest<ResponseType> onCreateRequest(Integer start, Integer count);

    @Override
    protected final void onLoadFinished(boolean successful, ResponseType response,
                                        ApiError error) {
        onLoadFinished(mLoadingMore, mLoadCount, successful, response, error);
        mLoadingMore = false;
    }

    protected abstract void onLoadFinished(boolean more, int count, boolean successful,
                                           ResponseType response, ApiError error);
}
