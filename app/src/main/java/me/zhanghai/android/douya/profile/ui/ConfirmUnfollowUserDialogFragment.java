/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.profile.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;

import me.zhanghai.android.douya.R;

public class ConfirmUnfollowUserDialogFragment extends AppCompatDialogFragment {

    /**
     * @deprecated Use {@link #newInstance()} instead.
     */
    public ConfirmUnfollowUserDialogFragment() {}

    public static ConfirmUnfollowUserDialogFragment newInstance() {
        //noinspection deprecation
        return new ConfirmUnfollowUserDialogFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity(), getTheme())
                .setMessage(R.string.user_unfollow_confirm)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getListener().unfollowUser();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create();
    }

    private Listener getListener() {
        return (Listener) getParentFragment();
    }

    public static void show(Fragment fragment) {
        ConfirmUnfollowUserDialogFragment.newInstance()
                .show(fragment.getChildFragmentManager(), null);
    }

    public interface Listener {
        void unfollowUser();
    }
}
