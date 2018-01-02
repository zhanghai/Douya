/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.content;

public interface MoreListResource<ResourceListType> {

    boolean has();

    ResourceListType get();

    boolean isEmpty();

    void load(boolean more, int count);

    void load(boolean loadMore);

    boolean isLoading();

    boolean canLoadMore();

    boolean isLoadingMore();

    void detach();
}
