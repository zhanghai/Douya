/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.item.content;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import me.zhanghai.android.douya.R;

public class ConfirmUncollectItemDialogFragment extends AppCompatDialogFragment {

    /**
     * @deprecated Use {@link #newInstance()} instead.
     */
    public ConfirmUncollectItemDialogFragment() {}

    public static ConfirmUncollectItemDialogFragment newInstance() {
        //noinspection deprecation
        return new ConfirmUncollectItemDialogFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity(), getTheme())
                .setMessage(R.string.item_uncollect_confirm)
                .setPositiveButton(R.string.ok, (dialog, which) -> getListener().uncollect())
                .setNegativeButton(R.string.cancel, null)
                .create();
    }

    private Listener getListener() {
        return (Listener) getParentFragment();
    }

    public static void show(Fragment fragment) {
        ConfirmUncollectItemDialogFragment.newInstance()
                .show(fragment.getChildFragmentManager(), null);
    }

    public interface Listener {
        void uncollect();
    }
}
