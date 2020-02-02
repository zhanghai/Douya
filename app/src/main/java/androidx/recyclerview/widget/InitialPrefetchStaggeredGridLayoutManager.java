/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package androidx.recyclerview.widget;

import android.content.Context;
import android.os.Parcelable;
import android.util.AttributeSet;

public class InitialPrefetchStaggeredGridLayoutManager extends StaggeredGridLayoutManager {

    private int mInitialPrefetchItemCount = 2;

    private SavedState mPendingSavedState;

    public InitialPrefetchStaggeredGridLayoutManager(int spanCount, int orientation) {
        super(spanCount, orientation);
    }

    public InitialPrefetchStaggeredGridLayoutManager(Context context, AttributeSet attrs,
                                                     int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public int getInitialPrefetchItemCount() {
        return mInitialPrefetchItemCount;
    }

    public void setInitialPrefetchItemCount(int itemCount) {
        mInitialPrefetchItemCount = itemCount;
    }

    /*
     * @see LinearLayoutManager#collectInitialPrefetchPositions(int, LayoutPrefetchRegistry)
     */
    @Override
    public void collectInitialPrefetchPositions(int adapterItemCount,
                                                LayoutPrefetchRegistry layoutPrefetchRegistry) {
        final boolean fromEnd;
        final int anchorPos;
        if (mPendingSavedState != null
                && getPendingSavedStateAnchorPosition() != RecyclerView.NO_POSITION) {
            // use restored state, since it hasn't been resolved yet
            fromEnd = mPendingSavedState.mAnchorLayoutFromEnd;
            anchorPos = getPendingSavedStateAnchorPosition();
        } else {
            fromEnd = getShouldReverseLayout();
            if (mPendingScrollPosition == RecyclerView.NO_POSITION) {
                anchorPos = fromEnd ? adapterItemCount - 1 : 0;
            } else {
                anchorPos = mPendingScrollPosition;
            }
        }

        final int direction = fromEnd
                ? LayoutState.ITEM_DIRECTION_HEAD
                : LayoutState.ITEM_DIRECTION_TAIL;
        int targetPos = anchorPos;
        for (int i = 0; i < mInitialPrefetchItemCount; i++) {
            if (targetPos >= 0 && targetPos < adapterItemCount) {
                layoutPrefetchRegistry.addPosition(targetPos, 0);
            } else {
                break; // no more to prefetch
            }
            targetPos += direction;
        }
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof SavedState) {
            mPendingSavedState = (SavedState) state;
        }

        super.onRestoreInstanceState(state);
    }

    @Override
    public void onLayoutCompleted(RecyclerView.State state) {
        super.onLayoutCompleted(state);

        mPendingSavedState = null;
    }

    /*
     * @see StaggeredGridLayoutManager#applyPendingSavedState(StaggeredGridLayoutManager.AnchorInfo)
     */
    private int getPendingSavedStateAnchorPosition() {
        if (mPendingSavedState.mSpanOffsetsSize > 0
                && mPendingSavedState.mSpanOffsetsSize != getSpanCount()) {
            return mPendingSavedState.mVisibleAnchorPosition;
        } else {
            return mPendingSavedState.mAnchorPosition;
        }
    }

    /*
     * @see StaggeredGridLayoutManager#resolveShouldLayoutReverse()
     */
    private boolean getShouldReverseLayout() {
        if (getOrientation() == VERTICAL || !isLayoutRTL()) {
            return mReverseLayout;
        } else {
            return !mReverseLayout;
        }
    }
}
