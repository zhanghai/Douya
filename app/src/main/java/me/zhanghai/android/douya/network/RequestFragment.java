/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import me.zhanghai.android.douya.app.TargetedRetainedFragment;
import me.zhanghai.android.douya.util.FragmentUtils;

/**
 * An one-shot Fragment for performing a {@link Request} safely across Activity re-creation.
 *
 * <p>Response will only be delivered when this Fragment is in resumed state. This Fragment will be
 * automatically removed once response delivery is done.</p>
 *
 * <p>Response is not guaranteed to be delivered if this fragment (and its hosting activity) gets
 * destroyed, because it is impossible to continue tracking the request in this case. The recreated
 * instance of this fragment will then remove itself.</p>
 *
 * @param <T> The type of parsed response the request expects.
 */
public class RequestFragment<T, S> extends TargetedRetainedFragment {

    private static final String TAG = RequestFragment.class.getName();

    private Request<T> mRequest;
    private S mRequestState;

    public static <T, S> void startRequest(Request<T> request, S requestState,
                                           FragmentActivity targetActivity, int requestCode) {
        RequestFragment<T, S> fragment = new RequestFragment<>(request, requestState);
        fragment.targetAtActivity(requestCode);
        fragment.startRequest();
        FragmentUtils.add(fragment, targetActivity);
    }

    public static <T, S> void startRequest(Request<T> request, S requestState,
                                           FragmentActivity targetActivity) {
        startRequest(request, requestState, targetActivity, REQUEST_CODE_INVALID);
    }

    public static <T, S> void startRequest(Request<T> request, S requestState,
                                           Fragment targetFragment, int requestCode) {
        RequestFragment<T, S> fragment = new RequestFragment<>(request, requestState);
        fragment.targetAtFragment(targetFragment, requestCode);
        FragmentActivity activity = targetFragment.getActivity();
        fragment.startRequest();
        FragmentUtils.add(fragment, activity);
    }

    public static <T, S> void startRequest(Request<T> request, S requestState,
                                           Fragment targetFragment) {
        startRequest(request, requestState, targetFragment, REQUEST_CODE_INVALID);
    }

    /**
     * @deprecated Use {@code startRequest()} instead.
     */
    public RequestFragment() {}

    @SuppressLint("ValidFragment")
    private RequestFragment(Request<T> request, S requestState) {
        mRequest = request;
        mRequestState = requestState;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (mRequest == null) {
            // We are being created because a previous instance is destroyed with its activity.
            // Since it's impossible for us to listen to the request in this case, we'll just ignore
            // it and remove this fragment.
            FragmentUtils.remove(this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mRequest != null) {
            mRequest.cancel();
            clearRequest();
        }
    }

    // Need to pass in a context here because getActivity() returns null when we are just added.
    private void startRequest() {
        mRequest
                .setListener(new Response.Listener<T>() {
                    @Override
                    public void onResponse(T response) {
                        onVolleyResponse(true, response, null);
                    }
                })
                .setErrorListener(new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        onVolleyResponse(false, null, error);
                    }
                });
        Volley.getInstance().addToRequestQueue(mRequest);
    }

    private void onVolleyResponse(final boolean successful, final T result,
                                  final VolleyError error) {
        clearRequest();
        postOnResumed(new Runnable() {
            @Override
            public void run() {
                deliverResponse(successful, result, error);
            }
        });
    }

    private void clearRequest() {
        mRequest.setListener(null);
        mRequest.setErrorListener(null);
        mRequest = null;
    }

    private void deliverResponse(boolean successful, T result, VolleyError error) {

        //noinspection unchecked
        Listener<T, S> listener = (Listener<T, S>) getTarget();
        if (listener != null) {
            listener.onVolleyResponse(getRequestCode(), successful, result, error, mRequestState);
        } else {
            Log.e(TAG, "listener is null when trying to deliver response");
        }

        mRequestState = null;

        FragmentUtils.remove(this);
    }

    public interface Listener<T, S> {
        void onVolleyResponse(int requestCode, boolean successful, T result, VolleyError error,
                              S requestState);
    }
}
