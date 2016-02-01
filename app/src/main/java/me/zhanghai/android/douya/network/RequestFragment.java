/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;

/**
 * An one-shot Fragment for performing a {@link Request} safely across Activity re-creation.
 *
 * <p>Response will only be delivered when this Fragment is in resumed state. This Fragment will be
 * automatically removed once response delivery is done.</p>
 *
 * @param <T> The type of parsed response the request expects.
 */
public class RequestFragment<T, S> extends Fragment {

    private static final String TAG = RequestFragment.class.getName();

    private boolean mTargetedAtActivity;
    private int mActivityRequestCode;
    private S mRequestState;

    private Request<T> mRequest;

    private boolean mHasPendingResponse = false;
    private boolean mPendingSuccessful;
    private T mPendingResult = null;
    private VolleyError mPendingError = null;

    public static <T, S> void startRequest(int requestCode, Request<T> request, S requestState,
                                           FragmentActivity targetActivity) {
        new RequestFragment<T, S>()
                .addTo(targetActivity)
                .targetAtActivity(requestCode)
                .setState(requestState)
                .startRequest(request, targetActivity);
    }

    public static <T, S> void startRequest(int requestCode, Request<T> request, S requestState,
                                           Fragment targetFragment) {
        FragmentActivity activity = targetFragment.getActivity();
        new RequestFragment<T, S>()
                .addTo(activity)
                .targetAtFragment(targetFragment, requestCode)
                .setState(requestState)
                .startRequest(request, activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
        setUserVisibleHint(false);
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
            mRequest = null;
        }
    }

    @Override
    public void setTargetFragment(Fragment fragment, int requestCode) {
        throw new UnsupportedOperationException("Target fragment is managed within this fragment");
    }

    private RequestFragment<T, S> addTo(FragmentActivity activity) {
        activity.getSupportFragmentManager().beginTransaction()
                .add(this, null)
                .commit();
        return this;
    }

    private RequestFragment<T, S> targetAtActivity(int requestCode) {
        mTargetedAtActivity = true;
        mActivityRequestCode = requestCode;
        return this;
    }

    private RequestFragment<T, S> targetAtFragment(Fragment fragment, int requestCode) {
        mTargetedAtActivity = false;
        super.setTargetFragment(fragment, requestCode);
        return this;
    }

    private RequestFragment<T, S> setState(S requestState) {
        mRequestState = requestState;
        return this;
    }

    // Need to pass in a context here because getActivity() returns null when we are just added.
    private RequestFragment<T, S> startRequest(Request<T> request, Context context) {

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

        return this;
    }

    private void onResult(T result) {
        mRequest = null;
        if (isResumed()) {
            deliverResponse(true, result, null);
        } else {
            addPendingResult(result);
        }
    }

    private void onError(VolleyError error) {
        mRequest = null;
        if (isResumed()) {
            deliverResponse(false, null, error);
        } else {
            addPendingError(error);
        }
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

        Listener<T, S> listener;
        int requestCode;
        if (mTargetedAtActivity) {
            //noinspection unchecked
            listener = (Listener<T, S>) getActivity();
            requestCode = mActivityRequestCode;
        } else {
            //noinspection unchecked
            listener = (Listener<T, S>) getTargetFragment();
            requestCode = getTargetRequestCode();
        }

        if (listener != null) {
            listener.onVolleyResponse(requestCode, successful, result, error, mRequestState);
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
