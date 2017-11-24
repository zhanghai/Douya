/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.content;

public abstract class ListResourceFragment<ResponseType, ResourceListType>
        extends ResourceFragment<ResponseType, ResourceListType> {

    protected abstract int getSize(ResourceListType resource);

    public boolean isEmpty() {
        return !has() || getSize(get()) == 0;
    }
}
