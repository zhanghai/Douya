/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.content;

import java.util.List;

public abstract class RawListResourceFragment<ResourceType, ResponseType>
        extends ListResourceFragment<List<ResourceType>, ResponseType> {

    @Override
    protected int getSize(List<ResourceType> resource) {
        return get().size();
    }
}
