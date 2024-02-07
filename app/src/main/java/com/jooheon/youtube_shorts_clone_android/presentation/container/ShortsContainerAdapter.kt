package com.jooheon.youtube_shorts_clone_android.presentation.container

import android.annotation.SuppressLint
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.jooheon.youtube_shorts_clone_android.model.ShortsModel
import com.jooheon.youtube_shorts_clone_android.presentation.playback.ShortsPlaybackFragment

class ShortsContainerAdapter(
    fragment: Fragment,
    private var dataSet: MutableList<ShortsModel>,
): FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = dataSet.size

    override fun createFragment(position: Int): Fragment {
        return ShortsPlaybackFragment.newInstance(dataSet[position])
    }

    @SuppressLint("NotifyDataSetChanged")
    internal fun submitList(newDataSet: List<ShortsModel>) {
        dataSet.addAll(newDataSet)
        notifyItemRangeInserted(dataSet.size, newDataSet.size)
    }
}