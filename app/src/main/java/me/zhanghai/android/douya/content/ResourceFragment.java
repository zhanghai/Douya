/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.content;

import com.android.volley.VolleyError;

import org.greenrobot.eventbus.Subscribe;

import me.zhanghai.android.douya.eventbus.EventBusUtils;
import me.zhanghai.android.douya.eventbus.PreventNoSubscriptionExceptionEvent;
import me.zhanghai.android.douya.network.Request;
import me.zhanghai.android.douya.network.RequestFragment;

public abstract class ResourceFragment<ResourceType, ResponseType>
        extends RequestFragment<Void, ResponseType> {

    ResourceType mResource;

    public boolean has() {
        return get() != null;
    }

    public ResourceType get() {
        return mResource;
    }

    protected void set(ResourceType resource) {
        mResource = resource;
    }

    @Override
    public final boolean isRequesting() {
        throw new UnsupportedOperationException("Use isLoading() instead");
    }

    public boolean isLoading() {
        return super.isRequesting();
    }

    @Override
    public void onStart() {
        super.onStart();

        EventBusUtils.register(this);

        loadOnStart();
    }

    @Override
    public void onStop() {
        super.onStop();

        EventBusUtils.unregister(this);
    }

    protected void loadOnStart() {
        if (!has()) {
            onLoadOnStart();
        }
    }

    protected void onLoadOnStart() {
        load();
    }

    public void load() {
        start(null);
    }

    @Override
    protected final Request<ResponseType> onCreateRequest(Void requestState) {
        return onCreateRequest();
    }

    protected abstract Request<ResponseType> onCreateRequest();

    @Override
    protected final void onRequestStarted() {
        onLoadStarted();
    }

    protected abstract void onLoadStarted();

    @Override
    protected void onRequestFinished(boolean successful, Void requestState, ResponseType response,
                                     VolleyError error) {
        onLoadFinished(successful, response, error);
    }

    protected abstract void onLoadFinished(boolean successful, ResponseType response,
                                           VolleyError error);

    @Subscribe
    public final void onPreventNoSubscriptionException(PreventNoSubscriptionExceptionEvent event) {}
}
