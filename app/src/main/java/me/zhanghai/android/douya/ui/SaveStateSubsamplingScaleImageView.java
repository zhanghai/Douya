/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.ImageViewState;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

public class SaveStateSubsamplingScaleImageView extends SubsamplingScaleImageView {

    private ImageViewState mPendingSavedState;

    public SaveStateSubsamplingScaleImageView(Context context, AttributeSet attr) {
        super(context, attr);
    }

    public SaveStateSubsamplingScaleImageView(Context context) {
        super(context);
    }

    public void setImageRestoringSavedState(ImageSource imageSource) {
        setImage(imageSource, mPendingSavedState);
        mPendingSavedState = null;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();

        SavedState savedState = new SavedState(superState);
        savedState.state = getState();
        return savedState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());

        mPendingSavedState = savedState.state;
    }

    private static class SavedState extends BaseSavedState {

        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {
                    @Override
                    public SavedState createFromParcel(Parcel source) {
                        return new SavedState(source);
                    }
                    @Override
                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };

        public ImageViewState state;

        public SavedState(Parcel in) {
            super(in);

            state = (ImageViewState) in.readSerializable();
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);

            dest.writeSerializable(state);
        }
    }
}
