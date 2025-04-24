package com.jooheon.youtube_shorts_clone_android.player

import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.preload.DefaultPreloadManager
import androidx.media3.exoplayer.source.preload.DefaultPreloadManager.Status.STAGE_LOADED_FOR_DURATION_MS
import androidx.media3.exoplayer.source.preload.TargetPreloadStatusControl
import com.jooheon.youtube_shorts_clone_android.model.ShortsModel
import kotlin.math.abs

@UnstableApi
class PreloadController(
    private val control: PreloadControl,
    private val models: List<ShortsModel>,
    preloadBuilder: DefaultPreloadManager.Builder,
) {
    private val currentMediaItemsAndIndexes: ArrayDeque<Pair<MediaItem, Int>> = ArrayDeque()
    private val preloadManager = preloadBuilder
        .build()
        .also { it.invalidate() }

    init {
        for (i in 0 until MANAGED_ITEM_COUNT) {
            addMediaItem(index = i, isAddingToRightEnd = true)
        }
    }

    fun release() {
        preloadManager.release()
        currentMediaItemsAndIndexes.clear()
    }

    fun getOrAddMediaSource(
        index: Int,
        model: ShortsModel
    ): MediaSource {
        val item = model.toMediaItem()

        return preloadManager.getMediaSource(item) ?: run {
            preloadManager.add(item, index)
            preloadManager.getMediaSource(item)!!
        }
    }

    fun setCurrentPlayingIndex(index: Int) {
        control.currentPlayingIndex = index
        preloadManager.setCurrentPlayingIndex(index)
        preloadManager.invalidate()
    }

    fun onItemAttached(index: Int) {
        if (!currentMediaItemsAndIndexes.isEmpty()) {
            val leftMostIndex = currentMediaItemsAndIndexes.first().second
            val rightMostIndex = currentMediaItemsAndIndexes.last().second

            if (rightMostIndex - index <= 2) {
                Log.d(TAG, "onItemAttached: Approaching to the rightmost item")
                for (i in 1 until ITEM_ADD_REMOVE_COUNT + 1) {
                    addMediaItem(index = rightMostIndex + i, isAddingToRightEnd = true)
                    removeMediaItem(isRemovingFromRightEnd = false)
                }
            } else if (index - leftMostIndex <= 2) {
                Log.d(TAG, "onItemAttached: Approaching to the leftmost item")
                for (i in 1 until ITEM_ADD_REMOVE_COUNT + 1) {
                    addMediaItem(index = leftMostIndex - i, isAddingToRightEnd = false)
                    removeMediaItem(isRemovingFromRightEnd = true)
                }
            }
        }
    }

    private fun addMediaItem(index: Int, isAddingToRightEnd: Boolean) {
        if (index < 0) {
            return
        }
        Log.d(TAG, "addMediaItem: Adding item at index $index")
        val model = models[index.mod(models.size)]
        val mediaItem = model.toMediaItem()
        preloadManager.add(mediaItem, index)
        if (isAddingToRightEnd) {
            currentMediaItemsAndIndexes.addLast(Pair(mediaItem, index))
        } else {
            currentMediaItemsAndIndexes.addFirst(Pair(mediaItem, index))
        }
    }

    private fun removeMediaItem(isRemovingFromRightEnd: Boolean) {
        if (currentMediaItemsAndIndexes.size <= MANAGED_ITEM_COUNT) {
            return
        }
        val itemAndIndex = if (isRemovingFromRightEnd) {
            currentMediaItemsAndIndexes.removeLast()
        } else {
            currentMediaItemsAndIndexes.removeFirst()
        }

        Log.d(TAG, "removeMediaItem: Removing item at index ${itemAndIndex.second}")
        preloadManager.remove(itemAndIndex.first)
    }

    @UnstableApi
    class PreloadControl(var currentPlayingIndex: Int = C.INDEX_UNSET):
        TargetPreloadStatusControl<Int> {

        override fun getTargetPreloadStatus(rankingData: Int): DefaultPreloadManager.Status? {
            if (abs(rankingData - currentPlayingIndex) == 2) {
                return DefaultPreloadManager.Status(STAGE_LOADED_FOR_DURATION_MS, 500L)
            } else if (abs(rankingData - currentPlayingIndex) == 1) {
                return DefaultPreloadManager.Status(STAGE_LOADED_FOR_DURATION_MS, 1000L)
            }
            return null
        }
    }

    companion object {
        private const val TAG = "PreloadController"
        private const val ITEM_ADD_REMOVE_COUNT = 4
        private const val MANAGED_ITEM_COUNT = 10
    }
}