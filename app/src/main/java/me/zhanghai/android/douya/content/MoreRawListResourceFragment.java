/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.content;

import java.util.List;

public abstract class MoreRawListResourceFragment<ResponseType, ResourceType>
        extends MoreListResourceFragment<ResponseType, List<ResourceType>> {

    @Override
    protected int getSize(List<ResourceType> resource) {
        return resource.size();
    }

    @Override
    protected List<ResourceType> addAll(List<ResourceType> resource, List<ResourceType> more) {
        resource.addAll(more);
        return resource;
    }
}
