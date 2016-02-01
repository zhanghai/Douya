/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.TypedValue;
import android.view.KeyEvent;

import me.zhanghai.android.douya.R;

public class SimpleDialogFragment extends DialogFragment {

    private static final String ARGUMENT_REQUEST_CODE = "request_code";
    private static final String ARGUMENT_THEME = "theme";
    private static final String ARGUMENT_ICON_ID = "icon_id";
    private static final String ARGUMENT_TITLE = "tle";
    private static final String ARGUMENT_MESSAGE = "message";
    private static final String ARGUMENT_IS_SINGLE_CHOICE = "is_single_choice";
    private static final String ARGUMENT_CHOICE_ITEMS = "choice_items";
    private static final String ARGUMENT_CHOICE_CHECKED_ITEM = "checked_item";
    private static final String ARGUMENT_POSITIVE_BUTTON_TEXT = "positive_button_text";
    private static final String ARGUMENT_NEUTRAL_BUTTON_TEXT = "neutral_button_text";
    private static final String ARGUMENT_NEGATIVE_BUTTON_TEXT = "negative_button_text";
    private static final String ARGUMENT_CANCELABLE = "cancelable";

    private SimpleDialogListener mListener;
    private int mRequestCode;

    private static SimpleDialogFragment makeClose(Fragment targetFragment, int requestCode,
                                                  Integer titleId, int messageId, Context context) {
        return new Builder(context)
                .setTargetFragment(targetFragment)
                .setRequestCode(requestCode)
                .setTitle(titleId)
                .setMessage(messageId)
                .setPositiveButtonText(R.string.close)
                .build();
    }

    public static SimpleDialogFragment makeClose(Fragment fragment, int requestCode,
                                                 Integer titleId, int messageId) {
        return makeClose(fragment, requestCode, titleId, messageId, fragment.getActivity());
    }

    public static SimpleDialogFragment makeClose(Integer titleId, int messageId,
                                                 Fragment fragment) {
        // NOTE: Prevent message from being sent to parent activity unexpectedly.
        return makeClose(fragment, 0, titleId, messageId);
    }

    public static SimpleDialogFragment makeClose(Activity activity, int requestCode,
                                                 Integer titleId, int messageId) {
        return makeClose(null, requestCode, titleId, messageId, activity);
    }

    public static SimpleDialogFragment makeClose(Integer titleId, int messageId,
                                                 Activity activity) {
        return makeClose(activity, 0, titleId, messageId);
    }

    private static SimpleDialogFragment makeOkCancel(Fragment targetFragment, int requestCode,
                                                     Integer titleId, int messageId,
                                                     Context context) {
        return new Builder(context)
                .setTargetFragment(targetFragment)
                .setRequestCode(requestCode)
                .setTitle(titleId)
                .setMessage(messageId)
                .setPositiveButtonText(R.string.ok)
                .setNegativeButtonText(R.string.cancel)
                .build();
    }

    public static SimpleDialogFragment makeOkCancel(Fragment fragment, int requestCode,
                                                    Integer titleId, int messageId) {
        return makeOkCancel(fragment, requestCode, titleId, messageId, fragment.getActivity());
    }

    public static SimpleDialogFragment makeOkCancel(Integer titleId, int messageId,
                                                    Fragment fragment) {
        return makeOkCancel(fragment, 0, titleId, messageId);
    }

    public static SimpleDialogFragment makeOkCancel(Activity activity, int requestCode,
                                                    Integer titleId, int messageId) {
        return makeOkCancel(null, requestCode, titleId, messageId, activity);
    }

    public static SimpleDialogFragment makeOkCancel(Integer titleId, int messageId,
                                                    Activity activity) {
        return makeOkCancel(activity, 0, titleId, messageId);
    }

    private static SimpleDialogFragment makeYesNo(Fragment targetFragment, int requestCode,
                                                 Integer titleId, int messageId, Context context) {
        return new Builder(context)
                .setTargetFragment(targetFragment)
                .setRequestCode(requestCode)
                .setTitle(titleId)
                .setMessage(messageId)
                .setPositiveButtonText(R.string.yes)
                .setNegativeButtonText(R.string.no)
                .build();
    }

    public static SimpleDialogFragment makeYesNo(Fragment fragment, int requestCode,
                                                 Integer titleId, int messageId) {
        return makeYesNo(fragment, requestCode, titleId, messageId, fragment.getActivity());
    }

    public static SimpleDialogFragment makeYesNo(Integer titleId, int messageId,
                                                 Fragment fragment) {
        return makeYesNo(fragment, 0, titleId, messageId);
    }

    public static SimpleDialogFragment makeYesNo(Activity activity, int requestCode,
                                                 Integer titleId, int messageId) {
        return makeYesNo(null, requestCode, titleId, messageId, activity);
    }

    public static SimpleDialogFragment makeYesNo(Integer titleId, int messageId,
                                                 Activity activity) {
        return makeYesNo(activity, 0, titleId, messageId);
    }

    public static SimpleDialogFragment makeSingleChoice(Fragment targetFragment, int requestCode,
                                                        Integer titleId, CharSequence[] items,
                                                        int checkedItem, Context context) {
        return new Builder(context)
                .setTargetFragment(targetFragment)
                .setRequestCode(requestCode)
                .setTitle(titleId)
                .setSingleChoice(items, checkedItem)
                .setNegativeButtonText(R.string.cancel)
                .build();
    }

    public static SimpleDialogFragment makeSingleChoice(Fragment fragment, int requestCode,
                                                        Integer titleId, CharSequence[] items,
                                                        int checkedItem) {
        return makeSingleChoice(fragment, requestCode, titleId, items, checkedItem,
                fragment.getActivity());
    }

    public static SimpleDialogFragment makeSingleChoice(Integer titleId, CharSequence[] items,
                                                        int checkedItem, Fragment fragment) {
        return makeSingleChoice(fragment, 0, titleId, items, checkedItem);
    }

    public static SimpleDialogFragment makeSingleChoice(Activity activity, int requestCode,
                                                        Integer titleId, CharSequence[] items,
                                                        int checkedItem) {
        return makeSingleChoice(null, requestCode, titleId, items, checkedItem, activity);
    }

    public static SimpleDialogFragment makeSingleChoice(Integer titleId, CharSequence[] items,
                                                        int checkedItem, Activity activity) {
        return makeSingleChoice(activity, 0, titleId, items, checkedItem);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Fragment targetFragment = getTargetFragment();
        if (targetFragment == null) {
            Activity activity = getActivity();
            if (activity instanceof SimpleDialogListenerProvider) {
                mListener = ((SimpleDialogListenerProvider) activity).getDialogListener();
            }
            mRequestCode = getArguments().getInt(ARGUMENT_REQUEST_CODE);
        } else {
            if (targetFragment instanceof SimpleDialogListenerProvider) {
                mListener = ((SimpleDialogListenerProvider) targetFragment).getDialogListener();
            }
            mRequestCode = getTargetRequestCode();
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder alertDialogBuilder;
        Bundle arguments = getArguments();

        int theme = arguments.getInt(ARGUMENT_THEME);
        if (theme == 0) {
            alertDialogBuilder = new AlertDialog.Builder(getActivity());
        } else {
            alertDialogBuilder = new AlertDialog.Builder(getActivity(), theme);
        }

        alertDialogBuilder
                .setIcon(arguments.getInt(ARGUMENT_ICON_ID))
                .setTitle(arguments.getCharSequence(ARGUMENT_TITLE))
                .setMessage(arguments.getCharSequence(ARGUMENT_MESSAGE));
        if (arguments.getBoolean(ARGUMENT_IS_SINGLE_CHOICE)) {
            alertDialogBuilder.setSingleChoiceItems(
                    arguments.getCharSequenceArray(ARGUMENT_CHOICE_ITEMS),
                    arguments.getInt(ARGUMENT_CHOICE_CHECKED_ITEM),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (mListener != null) {
                                mListener.onSingleChoiceItemClicked(mRequestCode, which);
                            }
                        }
                    });
        }
        alertDialogBuilder
                .setPositiveButton(arguments.getCharSequence(ARGUMENT_POSITIVE_BUTTON_TEXT),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (mListener != null) {
                                    mListener.onPositiveButtonClicked(mRequestCode);
                                }
                            }
                        })
                .setNeutralButton(arguments.getCharSequence(ARGUMENT_NEUTRAL_BUTTON_TEXT),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (mListener != null) {
                                    mListener.onNeutralButtonClicked(mRequestCode);
                                }
                            }
                        })
                .setNegativeButton(arguments.getCharSequence(ARGUMENT_NEGATIVE_BUTTON_TEXT),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (mListener != null) {
                                    mListener.onNegativeButtonClicked(mRequestCode);
                                }
                            }
                        })
                .setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent keyEvent) {
                        if (mListener != null) {
                            return mListener.onKey(mRequestCode, keyCode, keyEvent);
                        } else {
                            return false;
                        }
                    }
                })
                .setCancelable(arguments.getBoolean(ARGUMENT_CANCELABLE));

        return alertDialogBuilder.create();
    }

    /*
     * NOTE:
     * From DialogFragment.onCreateDialog documentation:
     * DialogFragment own the Dialog.setOnCancelListener and Dialog.setOnDismissListener callbacks.
     * You must not set them yourself.
     * To find out about these events, override onCancel(DialogInterface) and
     * onDismiss(DialogInterface).
     */

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);

        if (mListener != null) {
            mListener.onCancel(mRequestCode);
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        if (mListener != null) {
            mListener.onDismiss(mRequestCode);
        }
    }

    public AlertDialog getAlertDialog() {
        return (AlertDialog) getDialog();
    }

    public void show(FragmentManager manager) {
        show(manager, null);
    }

    public void showIfNotAdded(FragmentManager manager) {
        if (!isAdded()) {
            show(manager);
        }
    }

    public static class SimpleDialogListener {
        public void onSingleChoiceItemClicked(int requestCode, int index) {}
        public void onPositiveButtonClicked(int requestCode) {}
        public void onNeutralButtonClicked(int requestCode) {}
        public void onNegativeButtonClicked(int requestCode) {}
        public void onCancel(int requestCode) {}
        public void onDismiss(int requestCode) {}
        public boolean onKey(int requestCode, int keyCode, KeyEvent keyEvent) {
            return false;
        }
    }

    public interface SimpleDialogListenerProvider {
        SimpleDialogListener getDialogListener();
    }

    public static class Builder {

        private Context mContext;

        private Fragment mTargetFragment;
        private int mRequestCode;
        private int mTheme;
        private int mIconId;
        private CharSequence mTitle;
        private CharSequence mMessage;
        private boolean mIsSingleChoice;
        private CharSequence[] mChoiceItems;
        private int mChoiceCheckedItem;
        private CharSequence mNegativeButtonText;
        private CharSequence mNeutralButtonText;
        private CharSequence mPositiveButtonText;
        private boolean mCancelable;

        public Builder(Context context) {
            mContext = context;
        }

        public Builder setTargetFragment(Fragment fragment) {
            mTargetFragment = fragment;
            return this;
        }

        public Builder setRequestCode(int requestCode) {
            mRequestCode = requestCode;
            return this;
        }

        public Builder setTheme(int theme) {
            mTheme = theme;
            return this;
        }

        public Builder setIcon(int iconId) {
            mIconId = iconId;
            return this;
        }

        public Builder setIconAttribute(int attrId) {
            TypedValue typedValue = new TypedValue();
            mContext.getTheme().resolveAttribute(attrId, typedValue, true);
            return setIcon(typedValue.resourceId);
        }

        public Builder setTitle(CharSequence title) {
            mTitle = title;
            return this;
        }

        public Builder setTitle(int titleId) {
            return setTitle(mContext.getText(titleId));
        }

        public Builder setTitle(Integer titleId) {
            if (titleId != null) {
                setTitle((int) titleId);
            }
            return this;
        }

        public Builder setMessage(CharSequence message) {
            mMessage = message;
            return this;
        }

        public Builder setMessage(int messageId) {
            return setMessage(mContext.getText(messageId));
        }

        public Builder setSingleChoice(CharSequence[] items, int checkedItem) {
            mIsSingleChoice = true;
            mChoiceItems = items;
            mChoiceCheckedItem = checkedItem;
            return this;
        }

        public Builder setPositiveButtonText(CharSequence text) {
            mPositiveButtonText = text;
            return this;
        }

        public Builder setPositiveButtonText(int textId) {
            return setPositiveButtonText(mContext.getText(textId));
        }

        public Builder setNeutralButtonText(CharSequence text) {
            mNeutralButtonText = text;
            return this;
        }

        public Builder setNeutralButtonText(int textId) {
            return setNeutralButtonText(mContext.getText(textId));
        }

        public Builder setNegativeButtonText(CharSequence text) {
            mNegativeButtonText = text;
            return this;
        }

        public Builder setNegativeButtonText(int textId) {
            return setNegativeButtonText(mContext.getText(textId));
        }

        public Builder setCancelable(boolean cancelable) {
            mCancelable = cancelable;
            return this;
        }

        public SimpleDialogFragment build() {

            Bundle arguments = new Bundle();
            if (mTargetFragment == null) {
                arguments.putInt(ARGUMENT_REQUEST_CODE, mRequestCode);
            }
            arguments.putInt(ARGUMENT_THEME, mTheme);
            arguments.putInt(ARGUMENT_ICON_ID, mIconId);
            arguments.putCharSequence(ARGUMENT_TITLE, mTitle);
            arguments.putCharSequence(ARGUMENT_MESSAGE, mMessage);
            arguments.putBoolean(ARGUMENT_IS_SINGLE_CHOICE, mIsSingleChoice);
            arguments.putCharSequenceArray(ARGUMENT_CHOICE_ITEMS, mChoiceItems);
            arguments.putInt(ARGUMENT_CHOICE_CHECKED_ITEM, mChoiceCheckedItem);
            arguments.putCharSequence(ARGUMENT_POSITIVE_BUTTON_TEXT, mPositiveButtonText);
            arguments.putCharSequence(ARGUMENT_NEUTRAL_BUTTON_TEXT, mNeutralButtonText);
            arguments.putCharSequence(ARGUMENT_NEGATIVE_BUTTON_TEXT, mNegativeButtonText);
            arguments.putBoolean(ARGUMENT_CANCELABLE, mCancelable);

            SimpleDialogFragment simpleDialogFragment = new SimpleDialogFragment();
            if (mTargetFragment != null) {
                simpleDialogFragment.setTargetFragment(mTargetFragment, mRequestCode);
            }
            simpleDialogFragment.setArguments(arguments);
            return simpleDialogFragment;
        }
    }
}
