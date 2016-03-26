/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import me.zhanghai.android.douya.app.TargetedRetainedFragment;

/**
 * An one-shot Fragment for performing a {@link Request} safely across Activity re-creation.
 *
 * <p>Response will only be delivered when this Fragment is in resumed state. This Fragment will be
 * automatically removed once response delivery is done.</p>
 *
 * @param <T> The type of parsed response the request expects.
 */
public class RequestFragment<T, S> extends TargetedRetainedFragment {

    private static final String TAG = RequestFragment.class.getName();

    private S mRequestState;

    private Request<T> mRequest;

    private boolean mHasPendingResponse = false;
    private boolean mPendingSuccessful;
    private T mPendingResult = null;
    private VolleyError mPendingError = null;

    public static <T, S> void startRequest(int requestCode, Request<T> request, S requestState,
                                           FragmentActivity targetActivity) {
        RequestFragment<T, S> fragment = addTo(targetActivity);
        fragment.targetAtActivity(requestCode);
        fragment.setState(requestState);
        fragment.startRequest(request, targetActivity);
    }

    public static <T, S> void startRequest(Request<T> request, S requestState,
                                           FragmentActivity targetActivity) {
        startRequest(REQUEST_CODE_INVALID, request, requestState, targetActivity);
    }

    public static <T, S> void startRequest(int requestCode, Request<T> request, S requestState,
                                           Fragment targetFragment) {
        FragmentActivity activity = targetFragment.getActivity();
        RequestFragment<T, S> fragment = addTo(activity);
        fragment.targetAtFragment(targetFragment, requestCode);
        fragment.setState(requestState);
        fragment.startRequest(request, activity);
    }

    public static <T, S> void startRequest(Request<T> request, S requestState,
                                           Fragment targetFragment) {
        startRequest(REQUEST_CODE_INVALID, request, requestState, targetFragment);
    }

    private static <T, S> RequestFragment<T, S> addTo(FragmentActivity activity) {
        RequestFragment<T, S> fragment = new RequestFragment<>();
        activity.getSupportFragmentManager().beginTransaction()
                .add(fragment, null)
                .commit();
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mHasPendingResponse) {
            deliverResponse(mPendingSuccessful, mPendingResult, mPendingError);
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

    private void setState(S requestState) {
        mRequestState = requestState;
    }

    // Need to pass in a context here because getActivity() returns null when we are just added.
    private void startRequest(Request<T> request, Context context) {

        mRequest = request;
        mRequest
                .setListener(new Response.Listener<T>() {
                    @Override
                    public void onResponse(T response) {
                        onResult(response);
                    }
                })
                .setErrorListener(new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        onError(error);
                    }
                });

        Volley.getInstance(context).addToRequestQueue(mRequest);
    }

    private void onResult(T result) {
        clearRequest();
        if (isResumed()) {
            deliverResponse(true, result, null);
        } else {
            addPendingResult(result);
        }
    }

    private void onError(VolleyError error) {
        clearRequest();
        if (isResumed()) {
            deliverResponse(false, null, error);
        } else {
            addPendingError(error);
        }
    }

    private void clearRequest() {
        mRequest.setListener(null);
        mRequest.setErrorListener(null);
        mRequest = null;
    }

    private void addPendingResult(T result) {
        mHasPendingResponse = true;
        mPendingSuccessful = true;
        mPendingResult = result;
    }

    private void addPendingError(VolleyError error) {
        mHasPendingResponse = true;
        mPendingSuccessful = false;
        mPendingError = error;
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
        mHasPendingResponse = false;
        mPendingResult = null;
        mPendingError = null;

        FragmentActivity activity = getActivity();
        if (activity != null) {
            activity.getSupportFragmentManager().beginTransaction()
                    .remove(this)
                    .commit();
        } else {
            Log.e(TAG, "getActivity() return null when trying to remove this fragment");
        }
    }

    public interface Listener<T, S> {
        void onVolleyResponse(int requestCode, boolean successful, T result, VolleyError error,
                              S requestState);
    }
}
