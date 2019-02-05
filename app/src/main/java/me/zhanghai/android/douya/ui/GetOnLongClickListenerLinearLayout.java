package me.zhanghai.android.douya.ui;

import android.content.Context;
import android.os.Build;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class GetOnLongClickListenerLinearLayout extends LinearLayout {

    private OnLongClickListener mOnLongClickListener;

    public GetOnLongClickListenerLinearLayout(Context context) {
        super(context);
    }

    public GetOnLongClickListenerLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public GetOnLongClickListenerLinearLayout(Context context, @Nullable AttributeSet attrs,
                                              int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public GetOnLongClickListenerLinearLayout(Context context, AttributeSet attrs, int defStyleAttr,
                                              int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public OnLongClickListener getOnLongClickListener() {
        return mOnLongClickListener;
    }

    @Override
    public void setOnLongClickListener(@Nullable OnLongClickListener listener) {
        super.setOnLongClickListener(listener);

        mOnLongClickListener = listener;
    }
}
