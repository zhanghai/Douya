/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.zhanghai.android.douya.util.LogUtils;

public class MergeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private RecyclerView.Adapter[] mAdapters;

    private Map<Integer, Integer> mItemViewTypeToAdapterIndexMap = new HashMap<>();

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
            } else {
                position -= count;
                ++adapterIndex;
            }
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
            } else {
                position -= count;
                ++adapterIndex;
            }
        }
        throw new IllegalStateException("Unknown position: " + position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Integer adapterIndex = mItemViewTypeToAdapterIndexMap.get(viewType);
        if (adapterIndex != null) {
            return mAdapters[adapterIndex].onCreateViewHolder(parent,
                    viewType - (adapterIndex << 16));
        }
        throw new IllegalStateException("Unknown viewType: " + viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        for (RecyclerView.Adapter adapter : mAdapters) {
            int count = adapter.getItemCount();
            if (position < count) {
                //noinspection unchecked
                adapter.onBindViewHolder(holder, position);
                return;
            } else {
                position -= count;
            }
        }
        throw new IllegalStateException("Unknown position: " + position);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position,
                                 List<Object> payloads) {
        for (RecyclerView.Adapter adapter : mAdapters) {
            int count = adapter.getItemCount();
            if (position < count) {
                //noinspection unchecked
                adapter.onBindViewHolder(holder, position, payloads);
                return;
            } else {
                position -= count;
            }
        }
        throw new IllegalStateException("Unknown position: " + position);
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        int viewType = holder.getItemViewType();
        Integer adapterIndex = mItemViewTypeToAdapterIndexMap.get(viewType);
        if (adapterIndex != null) {
            //noinspection unchecked
            mAdapters[adapterIndex].onViewRecycled(holder);
            return;
        }
        throw new IllegalStateException("Unknown viewType: " + viewType);
    }

    @Override
    public boolean onFailedToRecycleView(RecyclerView.ViewHolder holder) {
        int position = holder.getAdapterPosition();
        for (RecyclerView.Adapter adapter : mAdapters) {
            int count = adapter.getItemCount();
            if (position < count) {
                //noinspection unchecked
                return adapter.onFailedToRecycleView(holder);
            } else {
                position -= count;
            }
        }
        throw new IllegalStateException("Unknown position: " + position);
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        int position = holder.getAdapterPosition();
        for (RecyclerView.Adapter adapter : mAdapters) {
            int count = adapter.getItemCount();
            if (position < count) {
                //noinspection unchecked
                adapter.onViewAttachedToWindow(holder);
                return;
            } else {
                position -= count;
            }
        }
        throw new IllegalStateException("Unknown position: " + position);
    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        int position = holder.getAdapterPosition();
        for (RecyclerView.Adapter adapter : mAdapters) {
            int count = adapter.getItemCount();
            if (position < count) {
                //noinspection unchecked
                adapter.onViewDetachedFromWindow(holder);
                return;
            } else {
                position -= count;
            }
        }
        throw new IllegalStateException("Unknown position: " + position);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        for (RecyclerView.Adapter adapter : mAdapters) {
            adapter.onAttachedToRecyclerView(recyclerView);
        }
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        for (RecyclerView.Adapter adapter : mAdapters) {
            adapter.onDetachedFromRecyclerView(recyclerView);
        }
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
