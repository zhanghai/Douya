/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.updatePaddingRelative
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import me.zhanghai.android.douya.R
import me.zhanghai.android.douya.account.app.activeAccount
import me.zhanghai.android.douya.account.app.userId
import me.zhanghai.android.douya.app.accountManager
import me.zhanghai.android.douya.arch.viewModels
import me.zhanghai.android.douya.databinding.MainFragmentBinding
import me.zhanghai.android.douya.link.UriHandler
import me.zhanghai.android.douya.timeline.TimelineFragment
import me.zhanghai.android.douya.timeline.TimelineFragmentArgs
import me.zhanghai.android.douya.ui.MaterialSwipeRefreshLayout
import me.zhanghai.android.douya.util.OnVerticalScrollWithPagingTouchSlopListener
import me.zhanghai.android.douya.util.awaitViewCreated
import me.zhanghai.android.douya.util.findViewByClass
import me.zhanghai.android.douya.util.getDimensionPixelSizeByAttr
import me.zhanghai.android.douya.util.hasFirstItemReachedTop

class MainFragment : Fragment() {
    private lateinit var binding: MainFragmentBinding

    private lateinit var timelineFragment: TimelineFragment

    private val viewModel: MainViewModel by viewModels {
        { MainViewModel(accountManager.activeAccount!!.userId!!) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = MainFragmentBinding.inflate(inflater, container, false)
        .also { binding = it }
        .root

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val activity = requireActivity() as AppCompatActivity
        activity.setSupportActionBar(binding.toolbar)
        if (savedInstanceState == null) {
            timelineFragment = TimelineFragment().apply {
                arguments = TimelineFragmentArgs(null).toBundle()
            }
            childFragmentManager.commit {
                add(R.id.timelineFragment, timelineFragment)
            }
        } else {
            timelineFragment = childFragmentManager.findFragmentById(
                R.id.timelineFragment
            ) as TimelineFragment
        }
        viewLifecycleOwner.lifecycleScope.launch {
            val view = childFragmentManager.awaitViewCreated(timelineFragment)
            val actionBarSize = getDimensionPixelSizeByAttr(R.attr.actionBarSize)
            view.findViewByClass<MaterialSwipeRefreshLayout>()?.run {
                progressOffset = actionBarSize
            }
            view.findViewByClass<RecyclerView>()?.run {
                updatePaddingRelative(top = paddingTop + actionBarSize)
                addOnScrollListener(object : OnVerticalScrollWithPagingTouchSlopListener(context) {
                    override fun onScrolled(dy: Int) {
                        if (!hasFirstItemReachedTop) {
                            setShowing(true)
                        }
                    }

                    override fun onScrolledUp() {
                        setShowing(true)
                    }

                    override fun onScrolledDown() {
                        if (hasFirstItemReachedTop) {
                            setShowing(false)
                        }
                    }

                    private fun setShowing(showing: Boolean) {
                        binding.appBarLayout.showing = showing
                        binding.sendStatusFab.showing = showing
                    }
                })
            }
        }

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        viewModel.openUriEvent.observe(viewLifecycleOwner) {
            UriHandler.open(it, activity)
        }
        viewModel.sendStatusEvent.observe(viewLifecycleOwner) {
            // TODO
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.main, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            R.id.search -> {
                // TODO
                true
            }
            R.id.notification -> {
                // TODO
                true
            }
            R.id.doumail -> {
                // TODO
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
}
