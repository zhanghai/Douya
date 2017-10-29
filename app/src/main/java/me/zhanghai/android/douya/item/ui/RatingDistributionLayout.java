/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.item.ui;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.util.ViewUtils;

public class RatingDistributionLayout extends TableLayout {

    private static final int[] COLORS = new int[] {
            0xFF57BB8A,
            0xFF9ACE6A,
            0xFFFFEB3B,
            0xFFFFBB50,
            0xFFFF8A65,
    };

    private RowHolder[] mRowHolders = new RowHolder[5];

    public RatingDistributionLayout(Context context) {
        super(context);

        init();
    }

    public RatingDistributionLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    private void init() {

        setDividerDrawable(ContextCompat.getDrawable(getContext(),
                R.drawable.transparent_divider_vertical_2dp));
        setShowDividers(SHOW_DIVIDER_MIDDLE);
        setColumnShrinkable(1, true);
        setColumnStretchable(1, true);

        for (int i = 0; i < mRowHolders.length; ++i) {
            View rowView = ViewUtils.inflate(R.layout.item_rating_distribution_layout_row, this);
            RowHolder rowHolder = new RowHolder(rowView);
            rowHolder.mStarsText.setText(makeStars(mRowHolders.length - i));
            rowHolder.mBarView.setBackgroundColor(COLORS[i]);
            mRowHolders[i] = rowHolder;
            addView(rowView);
        }

        // FIXME: Only for debugging.
        setCompact(true);
        for (int i = 0; i < mRowHolders.length; i++) {
            RowHolder rowHolder = mRowHolders[i];
            ((LinearLayout) rowHolder.mBarView.getParent()).setWeightSum(5);
            ((LinearLayout.LayoutParams) rowHolder.mBarView.getLayoutParams()).weight = 5 - i;
        }
    }

    private static String makeStars(int count) {
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (int i = 0; i < count; ++i) {
            if (first) {
                first = false;
            } else {
                builder.append(' ');
            }
            builder.append('★');
        }
        return builder.toString();
    }

    public void setCompact(boolean compact) {
        setShowDividers(compact ? SHOW_DIVIDER_NONE : SHOW_DIVIDER_MIDDLE);
        setColumnCollapsed(0, compact);
        for (RowHolder row : mRowHolders) {
            ViewUtils.setVisibleOrGone(row.mCountText, !compact);
        }
    }

    static class RowHolder {

        @BindView(R.id.rating_distribution_stars)
        public TextView mStarsText;
        @BindView(R.id.rating_distribution_bar)
        public View mBarView;
        @BindView(R.id.rating_distribution_count)
        public TextView mCountText;

        public RowHolder(View rowView) {
            ButterKnife.bind(this, rowView);
        }
    }
}
