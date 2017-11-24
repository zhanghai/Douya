/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.content;

import java.util.List;

public abstract class RawListResourceFragment<ResponseType, ResourceType>
        extends ListResourceFragment<ResponseType, List<ResourceType>> {

    @Override
    protected int getSize(List<ResourceType> resource) {
        return resource.size();
    }
}
