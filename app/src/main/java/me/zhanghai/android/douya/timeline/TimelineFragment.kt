/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.timeline

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import me.zhanghai.android.douya.R
import me.zhanghai.android.douya.arch.viewModels
import me.zhanghai.android.douya.databinding.TimelineFragmentBinding
import me.zhanghai.android.douya.util.MergeAdapter
import me.zhanghai.android.douya.util.MoreItemAdapter
import me.zhanghai.android.douya.util.OnVerticalScrollWithPagingTouchSlopListener
import me.zhanghai.android.douya.util.getBooleanByAttr
import me.zhanghai.android.douya.util.getInteger
import me.zhanghai.android.douya.util.showToast

class TimelineFragment : Fragment() {
    private val timelineAdapter = TimelineAdapter()

    private val viewModel: TimelineViewModel by viewModels {
        {
            TimelineViewModel { timelineAdapter.createDiffCallback(it) }
        }
    }

    private lateinit var binding: TimelineFragmentBinding

    private val moreItemAdapter = MoreItemAdapter(R.layout.timeline_more_item)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = TimelineFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        val activity = requireActivity()
        if (activity.getBooleanByAttr(R.attr.isLightTheme)) {
            activity.window.setBackgroundDrawableResource(R.color.material_grey_100)
        }
        binding.timelineRecycler.run {
            layoutManager = StaggeredGridLayoutManager(
                activity.getInteger(R.integer.list_card_column_count), RecyclerView.VERTICAL
            )
            itemAnimator = DefaultItemAnimator().apply { supportsChangeAnimations = false }
            adapter = MergeAdapter(timelineAdapter, moreItemAdapter)
            addOnScrollListener(object : OnVerticalScrollWithPagingTouchSlopListener(context) {
                override fun onScrolledToBottom() {
                    viewModel.loadMore()
                }
            })
        }
        viewModel.timeline.observe(viewLifecycleOwner) { (timeline, diffResult) ->
            if (diffResult != null) {
                timelineAdapter.items = timeline
                diffResult.dispatchUpdatesTo(timelineAdapter)
            } else {
                timelineAdapter.items = emptyList()
                timelineAdapter.notifyDataSetChanged()
                if (timeline.isNotEmpty()) {
                    binding.timelineRecycler.stopScroll()
                    // Calls RecyclerView.consumePendingUpdateOperations().
                    binding.timelineRecycler.scrollBy(0, 0)
                    timelineAdapter.items = timeline
                    timelineAdapter.notifyDataSetChanged()
                    binding.timelineRecycler.scrollBy(0, 0)
                    binding.timelineRecycler.scrollToPosition(0)
                }
            }
            moreItemAdapter.available = timeline.isNotEmpty()
        }
        viewModel.moreLoading.observe(viewLifecycleOwner) { moreItemAdapter.loading = it }
        viewModel.moreErrorEvent.observe(viewLifecycleOwner) { showToast(it) }
    }
}
