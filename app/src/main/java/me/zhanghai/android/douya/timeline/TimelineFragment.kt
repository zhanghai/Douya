/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.timeline

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updatePaddingRelative
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import me.zhanghai.android.douya.R
import me.zhanghai.android.douya.arch.viewModels
import me.zhanghai.android.douya.databinding.TimelineFragmentBinding
import me.zhanghai.android.douya.util.MergeAdapter
import me.zhanghai.android.douya.util.MoreItemAdapter
import me.zhanghai.android.douya.util.OnVerticalScrollWithPagingTouchSlopListener
import me.zhanghai.android.douya.util.getDimensionPixelSizeByAttr
import me.zhanghai.android.douya.util.getInteger
import me.zhanghai.android.douya.util.hasFirstItemReachedTop
import me.zhanghai.android.douya.util.showToast

class TimelineFragment : Fragment() {
    var appBarLayout: ViewGroup? = null

    var fab: View? = null

    private val args: TimelineFragmentArgs by navArgs()

    private val viewModel: TimelineViewModel by viewModels { { TimelineViewModel(args.userId) } }

    private lateinit var binding: TimelineFragmentBinding

    private val timelineAdapter = TimelineAdapter()
    private val moreItemAdapter = MoreItemAdapter(timelineAdapter, R.layout.timeline_more_item)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = TimelineFragmentBinding.inflate(inflater, container, false)
        .also { binding = it }
        .root

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        val context = requireContext()
        val actionBarSize = context.getDimensionPixelSizeByAttr(R.attr.actionBarSize)
        binding.swipeRefreshLayout.progressOffset = actionBarSize
        binding.timelineRecycler.run {
            updatePaddingRelative(top = paddingTop + actionBarSize)
            layoutManager = StaggeredGridLayoutManager(
                context.getInteger(R.integer.list_card_column_count), RecyclerView.VERTICAL
            )
            itemAnimator = DefaultItemAnimator().apply { supportsChangeAnimations = false }
            adapter = MergeAdapter(timelineAdapter, moreItemAdapter)
            addOnScrollListener(object : OnVerticalScrollWithPagingTouchSlopListener(context) {
                override fun onScrolled(dy: Int) {
                    if (hasFirstItemReachedTop) {
                        onShow()
                    }
                }

                override fun onScrolledUp() {
                    onShow()
                }

                private fun onShow() {
                    //appBarLayout?.show()
                    //fab?.show()
                }

                override fun onScrolledDown() {
                    if (hasFirstItemReachedTop) {
                        //appBarLayout?.hide()
                        //fab?.hide()
                    }
                }

                override fun onScrolledToBottom() {
                    viewModel.loadMore()
                }
            })
        }
        viewModel.timelineAndDiffResult.observe(viewLifecycleOwner) { (timeline, diffResult) ->
            timelineAdapter.items = timeline
            diffResult?.dispatchUpdatesTo(timelineAdapter)
        }
        viewModel.moreLoading.observe(viewLifecycleOwner) { moreItemAdapter.loading = it }
        viewModel.errorEvent.observe(viewLifecycleOwner) { showToast(it) }
    }
}
