/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.widget.TextView;

import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.util.ClipboardUtils;
import me.zhanghai.android.douya.util.FragmentUtils;

public class CopyTextDialogFragment extends AppCompatDialogFragment {

    private static final String KEY_PREFIX = CopyTextDialogFragment.class.getName() + '.';

    public static final String EXTRA_TEXT = KEY_PREFIX + "text";

    private String mText;

    /**
     * @deprecated Use {@link #newInstance(String)} instead.
     */
    public CopyTextDialogFragment() {}

    public static CopyTextDialogFragment newInstance(String text) {
        //noinspection deprecation
        CopyTextDialogFragment fragment = new CopyTextDialogFragment();
        FragmentUtils.ensureArguments(fragment)
                .putString(EXTRA_TEXT, text);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mText = getArguments().getString(EXTRA_TEXT);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity(), getTheme())
                .setMessage(mText)
                .setPositiveButton(R.string.copy_to_clipboard,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ClipboardUtils.copyText(null, mText, getActivity());
                            }
                        })
                .create();
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null) {
            TextView messageText = (TextView) dialog.findViewById(android.R.id.message);
            if (messageText != null) {
                messageText.setTextIsSelectable(true);
            }
        }
    }

    public static void show(String text, Fragment fragment) {
        CopyTextDialogFragment.newInstance(text)
                .show(fragment.getChildFragmentManager(), null);
    }
}
