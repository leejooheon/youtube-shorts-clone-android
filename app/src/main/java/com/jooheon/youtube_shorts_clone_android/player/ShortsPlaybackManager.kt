package com.jooheon.youtube_shorts_clone_android.player

import android.content.Context
import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.source.preload.DefaultPreloadManager
import com.jooheon.youtube_shorts_clone_android.model.ShortsModel

@OptIn(UnstableApi::class)
class ShortsPlaybackManager(
    context: Context,
    numberOfPlayers: Int,
    models: List<ShortsModel>,
) {
    val playerManager: PlayerManager
    val preloadController: PreloadController

    init {
        val preloadControl = PreloadController.PreloadControl()
        val loadControl = DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                LOAD_CONTROL_MIN_BUFFER_MS,
                LOAD_CONTROL_MAX_BUFFER_MS,
                LOAD_CONTROL_BUFFER_FOR_PLAYBACK_MS,
                DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS,
            )
            .setPrioritizeTimeOverSizeThresholds(true)
            .build()

        val preloadManagerBuilder = DefaultPreloadManager
            .Builder(context.applicationContext, preloadControl)
            .setLoadControl(loadControl)

        playerManager = PlayerManager(
            numberOfPlayers = numberOfPlayers,
            preloadBuilder = preloadManagerBuilder
        )

        preloadController = PreloadController(
            control = preloadControl,
            models = models,
            preloadBuilder = preloadManagerBuilder
        )
    }

    fun release() {
        playerManager.destroyPlayers()
        preloadController.release()
    }

    companion object {
        private const val LOAD_CONTROL_MIN_BUFFER_MS = 5_000
        private const val LOAD_CONTROL_MAX_BUFFER_MS = 20_000
        private const val LOAD_CONTROL_BUFFER_FOR_PLAYBACK_MS = 500
    }
}

@Composable
internal fun rememberShortsPlaybackManager(
    numberOfPlayers: Int,
    models: List<ShortsModel>,
    context: Context = LocalContext.current,
): ShortsPlaybackManager {
    return remember {
        ShortsPlaybackManager(context, numberOfPlayers, models)
    }
}