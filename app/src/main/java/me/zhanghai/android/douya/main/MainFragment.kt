/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.observe
import me.zhanghai.android.douya.R
import me.zhanghai.android.douya.arch.viewModels
import me.zhanghai.android.douya.databinding.MainFragmentBinding
import me.zhanghai.android.douya.timeline.TimelineFragment
import me.zhanghai.android.douya.timeline.TimelineFragmentArgs

class MainFragment : Fragment() {
    private lateinit var binding: MainFragmentBinding

    private lateinit var timelineFragment: TimelineFragment

    private val viewModel: MainViewModel by viewModels { { MainViewModel() } }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = MainFragmentBinding.inflate(inflater, container, false)
        .also { binding = it }
        .root

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

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
        timelineFragment.run {
            appBarLayout = binding.appBarLayout
            fab = binding.sendFab
        }

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        viewModel.sendEvent.observe(viewLifecycleOwner) {
            // TODO
        }
    }
}
