/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.SparseIntArray;
import android.view.ViewGroup;

import java.util.List;

import me.zhanghai.android.douya.util.LogUtils;

public class MergeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private RecyclerView.Adapter[] mAdapters;

    private SparseIntArray mItemViewTypeToAdapterIndexMap = new SparseIntArray();

    public MergeAdapter(RecyclerView.Adapter... adapters) {

        mAdapters = adapters;

        for (RecyclerView.Adapter adapter : mAdapters) {
            adapter.registerAdapterDataObserver(new AdapterDataObserver(adapter));
        }

        boolean hasStableIds = true;
        for (RecyclerView.Adapter adapter : mAdapters) {
            if (!adapter.hasStableIds()) {
                hasStableIds = false;
                LogUtils.w("not all adapters have stable ids: " + adapter);
            }
        }
        super.setHasStableIds(hasStableIds);
    }

    public RecyclerView.Adapter[] getAdapters() {
        return mAdapters;
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        throw new UnsupportedOperationException("cannot set hasStableIds on MergeAdapter");
    }

    @Override
    public int getItemCount() {
        int count = 0;
        for (RecyclerView.Adapter adapter : mAdapters) {
            count += adapter.getItemCount();
        }
        return count;
    }

    @Override
    public long getItemId(int position) {
        int adapterIndex = 0;
        for (RecyclerView.Adapter adapter : mAdapters) {
            int count = adapter.getItemCount();
            if (position < count) {
                return 31 * adapterIndex + adapter.getItemId(position);
            }
            position -= count;
            ++adapterIndex;
        }
        throw new IllegalStateException("Unknown position: " + position);
    }

    @Override
    public int getItemViewType(int position) {
        int adapterIndex = 0;
        for (RecyclerView.Adapter adapter : mAdapters) {
            int count = adapter.getItemCount();
            if (position < count) {
                int itemViewType = (adapterIndex << 16) + adapter.getItemViewType(position);
                mItemViewTypeToAdapterIndexMap.put(itemViewType, adapterIndex);
                return itemViewType;
            }
            position -= count;
            ++adapterIndex;
        }
        throw new IllegalStateException("Unknown position: " + position);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int adapterIndex = getAdapterIndexForViewType(viewType);
        return mAdapters[adapterIndex].onCreateViewHolder(parent, viewType - (adapterIndex << 16));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        for (RecyclerView.Adapter adapter : mAdapters) {
            int count = adapter.getItemCount();
            if (position < count) {
                //noinspection unchecked
                adapter.onBindViewHolder(holder, position);
                return;
            }
            position -= count;
        }
        throw new IllegalStateException("Unknown position: " + position);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position,
                                 @NonNull List<Object> payloads) {
        for (RecyclerView.Adapter adapter : mAdapters) {
            int count = adapter.getItemCount();
            if (position < count) {
                //noinspection unchecked
                adapter.onBindViewHolder(holder, position, payloads);
                return;
            }
            position -= count;
        }
        throw new IllegalStateException("Unknown position: " + position);
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        int adapterIndex = getAdapterIndexForViewType(holder.getItemViewType());
        //noinspection unchecked
        mAdapters[adapterIndex].onViewRecycled(holder);
    }

    @Override
    public boolean onFailedToRecycleView(@NonNull RecyclerView.ViewHolder holder) {
        int position = holder.getAdapterPosition();
        for (RecyclerView.Adapter adapter : mAdapters) {
            int count = adapter.getItemCount();
            if (position < count) {
                //noinspection unchecked
                return adapter.onFailedToRecycleView(holder);
            }
            position -= count;
        }
        throw new IllegalStateException("Unknown position: " + position);
    }

    @Override
    public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
        int position = holder.getAdapterPosition();
        for (RecyclerView.Adapter adapter : mAdapters) {
            int count = adapter.getItemCount();
            if (position < count) {
                //noinspection unchecked
                adapter.onViewAttachedToWindow(holder);
                return;
            }
            position -= count;
        }
        throw new IllegalStateException("Unknown position: " + position);
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull RecyclerView.ViewHolder holder) {
        int position = holder.getAdapterPosition();
        for (RecyclerView.Adapter adapter : mAdapters) {
            int count = adapter.getItemCount();
            if (position < count) {
                //noinspection unchecked
                adapter.onViewDetachedFromWindow(holder);
                return;
            }
            position -= count;
        }
        throw new IllegalStateException("Unknown position: " + position);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        for (RecyclerView.Adapter adapter : mAdapters) {
            adapter.onAttachedToRecyclerView(recyclerView);
        }
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        for (RecyclerView.Adapter adapter : mAdapters) {
            adapter.onDetachedFromRecyclerView(recyclerView);
        }
    }

    private int getAdapterIndexForViewType(int viewType) {
        int mapIndex = mItemViewTypeToAdapterIndexMap.indexOfKey(viewType);
        if (mapIndex < 0) {
            throw new IllegalStateException("Unknown viewType: " + viewType);
        }
        return mItemViewTypeToAdapterIndexMap.valueAt(mapIndex);
    }

    private class AdapterDataObserver extends RecyclerView.AdapterDataObserver {

        private RecyclerView.Adapter mAdapter;

        public AdapterDataObserver(RecyclerView.Adapter adapter) {
            mAdapter = adapter;
        }

        @Override
        public void onChanged() {
            notifyDataSetChanged();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            notifyItemRangeChanged(getItemPosition(positionStart), itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            notifyItemRangeChanged(getItemPosition(positionStart), itemCount, payload);
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            notifyItemRangeInserted(getItemPosition(positionStart), itemCount);
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            notifyItemRangeRemoved(getItemPosition(positionStart), itemCount);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            if (itemCount != 1) {
                throw new IllegalArgumentException("Moving more than 1 item is not supported yet");
            }
            notifyItemMoved(getItemPosition(fromPosition), getItemPosition(toPosition));
        }

        private int getItemPosition(int position) {
            for (RecyclerView.Adapter adapter : mAdapters) {
                if (adapter == mAdapter) {
                    break;
                } else {
                    position += adapter.getItemCount();
                }
            }
            return position;
        }
    }
}
