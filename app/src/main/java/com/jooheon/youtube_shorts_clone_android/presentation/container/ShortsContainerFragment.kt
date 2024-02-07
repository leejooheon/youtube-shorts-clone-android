package com.jooheon.youtube_shorts_clone_android.presentation.container

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager2.widget.ViewPager2
import com.jooheon.youtube_shorts_clone_android.databinding.FragmentShortsContainerBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ShortsContainerFragment: Fragment() {
    private var _binding: FragmentShortsContainerBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ShortsContainerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[ShortsContainerViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShortsContainerBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        observeViewModel()

        viewModel.loadData(requireContext())
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

    private fun initView() {
        val shortsContainerAdapter = ShortsContainerAdapter(
            fragment = this,
            dataSet = mutableListOf(),
        )

        binding.pager.adapter = shortsContainerAdapter
        binding.pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                if (state == ViewPager2.SCROLL_STATE_IDLE || state == ViewPager2.SCROLL_STATE_DRAGGING) {
//                    val videoList = viewModel.shortFormModels.value
//                    if (binding.pager.currentItem == (videoList.size - 1)) {
//                        fetchVideo(binding)
//                    }
                }
            }
        })
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.modelListState.collectLatest {
                    (binding.pager.adapter as? ShortsContainerAdapter)?.submitList(it)
                }
            }
        }
    }
}