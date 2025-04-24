//package com.jooheon.youtube_shorts_clone_android.player
//
//import android.content.Context
//import android.util.Log
//import androidx.annotation.OptIn
//import androidx.lifecycle.DefaultLifecycleObserver
//import androidx.lifecycle.Lifecycle
//import androidx.lifecycle.LifecycleOwner
//import androidx.media3.common.AudioAttributes
//import androidx.media3.common.C
//import androidx.media3.common.Player
//import androidx.media3.common.util.UnstableApi
//import androidx.media3.datasource.DataSource
//import androidx.media3.datasource.DefaultDataSource
//import androidx.media3.datasource.cache.CacheDataSource
//import androidx.media3.exoplayer.DefaultRenderersFactory
//import androidx.media3.exoplayer.ExoPlayer
//import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
//import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
//import androidx.media3.extractor.DefaultExtractorsFactory
//
//@OptIn(UnstableApi::class)
//class ShortsPlaybackManager(
//    private val context: Context,
//    lifecycle: Lifecycle,
//) {
//    private val TAG = ShortsPlaybackManager::class.java.simpleName
//    private lateinit var player: ExoPlayer
//
//    init {
//        initPlayer()
//        observeLifecycle(lifecycle)
//    }
//
//    internal fun getPlayer() = player
//
//    internal fun addListener(listener: Player.Listener) {
//        player.addListener(listener)
//    }
//
//    internal fun release() {
//        player.stop()
//        player.clearMediaItems()
//        player.release()
//    }
//
//    private fun observeLifecycle(lifecycle: Lifecycle) {
//        lifecycle.run {
//            val observer: DefaultLifecycleObserver = object : DefaultLifecycleObserver {
//                override fun onResume(owner: LifecycleOwner) {
//                    super.onResume(owner)
//                    Log.d(TAG, "onResume ${player.currentMediaItem?.mediaMetadata?.title}")
//
//                    player.seekTo(0)
//                    player.play()
//                }
//
//                override fun onPause(owner: LifecycleOwner) {
//                    super.onPause(owner)
//                    Log.d(TAG, "onPause ${player.currentMediaItem?.mediaMetadata?.title}")
//
//                    player.pause()
//                }
//
//                override fun onDestroy(owner: LifecycleOwner) {
//                    super.onDestroy(owner)
//                    Log.d(TAG, "onDestroy ${player.currentMediaItem?.mediaMetadata?.title}")
//
//                    release()
//                    owner.lifecycle.removeObserver(this)
//                }
//            }
//            addObserver(observer)
//        }
//    }
//
//    private fun initPlayer() {
//        val audioAttributes = AudioAttributes.Builder()
//            .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
//            .setUsage(C.USAGE_MEDIA)
//            .build()
//
//        val trackSelector = DefaultTrackSelector(context).apply {
//            setParameters(buildUponParameters().setMaxVideoSizeSd())
//        }
//
//        val mediaSourceFactory = DefaultMediaSourceFactory(
//            DefaultDataSource.Factory(context, cacheDataSource()),
//            DefaultExtractorsFactory()
//        )
//
//        player = ExoPlayer.Builder(
//            context,
//            DefaultRenderersFactory(context),
//        )
//            .setMediaSourceFactory(mediaSourceFactory)
//            .setTrackSelector(trackSelector)
//            .setHandleAudioBecomingNoisy(true)
//            .setAudioAttributes(audioAttributes, true)
//            .build()
//            .apply {
//                repeatMode = ExoPlayer.REPEAT_MODE_ONE
//                videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
//            }
//    }
//
//    private fun cacheDataSource(): DataSource.Factory {
//        return CacheDataSource.Factory()
//            .setCache(ShortsCache.cache)
//            .setUpstreamDataSourceFactory(DefaultDataSource.Factory(context))
//            .setFlags(CacheDataSource.FLAG_BLOCK_ON_CACHE or CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
//    }
//}