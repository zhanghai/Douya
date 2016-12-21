/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import me.zhanghai.android.douya.app.TargetedRetainedFragment;

/**
 * <p>Response will only be delivered when this Fragment is in resumed state.</p>
 *
 * @param <ResponseType> The type of parsed response the request expects.
 */
public abstract class RequestFragment<RequestStateType, ResponseType>
        extends TargetedRetainedFragment implements Response.Listener<ResponseType>,
        Response.ErrorListener {

    private boolean mRequesting;
    private Request<ResponseType> mRequest;
    private RequestStateType mRequestState;

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mRequest != null) {
            mRequest.cancel();
            mRequesting = false;
            clearRequest();
        }
    }

    public boolean isRequesting() {
        return mRequesting;
    }

    public Request<ResponseType> getRequest() {
        return mRequest;
    }

    public RequestStateType getRequestState() {
        return mRequestState;
    }

    protected void start(RequestStateType requestState) {

        if (mRequesting || shouldIgnoreStartRequest()) {
            return;
        }
        mRequesting = true;

        mRequest = onCreateRequest(requestState);
        mRequestState = requestState;
        mRequest.setListener(this).setErrorListener(this);
        Volley.getInstance().addToRequestQueue(mRequest);

        onRequestStarted();
    }

    protected boolean shouldIgnoreStartRequest() {
        return false;
    }

    protected abstract Request<ResponseType> onCreateRequest(RequestStateType requestState);

    protected abstract void onRequestStarted();

    @Override
    public final void onResponse(ResponseType response) {
        onVolleyResponse(true, response, null);
    }

    @Override
    public final void onErrorResponse(VolleyError error) {
        onVolleyResponse(false, null, error);
    }

    private void onVolleyResponse(final boolean successful, final ResponseType response,
                                  final VolleyError error) {
        postOnResumed(new Runnable() {
            @Override
            public void run() {
                mRequesting = false;
                onRequestFinished(successful, mRequestState, response, error);
                clearRequest();
            }
        });
    }

    private void clearRequest() {
        mRequest.setListener(null).setErrorListener(null);
        mRequest = null;
        mRequestState = null;
    }

    protected abstract void onRequestFinished(boolean successful, RequestStateType requestState,
                                              ResponseType response, VolleyError error);
}
