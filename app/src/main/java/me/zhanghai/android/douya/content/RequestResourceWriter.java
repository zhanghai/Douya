/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.content;

import me.zhanghai.android.douya.network.api.ApiRequest;

public abstract class RequestResourceWriter<W extends RequestResourceWriter, T>
        extends ResourceWriter<W> implements ApiRequest.Callback<T> {

    private ApiRequest<T> mRequest;

    public RequestResourceWriter(ResourceWriterManager<W> manager) {
        super(manager);
    }

    @Override
    public void onStart() {
        mRequest = onCreateRequest();
        mRequest.enqueue(this);
    }

    protected abstract ApiRequest<T> onCreateRequest();

    public void onDestroy() {
        mRequest.cancel();
        mRequest = null;
    }
}
