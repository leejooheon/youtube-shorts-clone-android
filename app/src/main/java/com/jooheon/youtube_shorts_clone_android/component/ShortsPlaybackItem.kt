package com.jooheon.youtube_shorts_clone_android.component

import android.graphics.drawable.Drawable
import android.view.ViewGroup
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.jooheon.youtube_shorts_clone_android.player.ShortsPlaybackManager
import com.jooheon.youtube_shorts_clone_android.model.ShortsModel

@OptIn(UnstableApi::class)
@Composable
internal fun ShortsPlaybackItem(
    index: Int,
    model: ShortsModel,
    isFocused: Boolean,
    manager: ShortsPlaybackManager,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var player by remember { mutableStateOf<ExoPlayer?>(null) }
    var artworkDrawable by remember { mutableStateOf<Drawable?>(null) }
    var isPlaying by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val mediaSource = manager.preloadController.getOrAddMediaSource(index, model)
        player = manager.playerManager.acquirePlayer(index)?.apply {
            setMediaSource(mediaSource)
            seekTo(currentPosition)
            playWhenReady = false
            prepare()
        }
    }

    LaunchedEffect(isFocused, player) {
        if (isFocused && player != null) {
            manager.playerManager.play(player!!)
        }
    }
    DisposableEffect(player) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(_isPlaying: Boolean) {
                isPlaying = _isPlaying
            }
        }
        player?.addListener(listener)
        onDispose {
            player?.removeListener(listener)
        }
    }

    LaunchedEffect(model.thumbnail) {
        Glide.with(context)
            .load(model.thumbnail)
            .into(object : CustomTarget<Drawable>() {
                override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                    artworkDrawable = resource
                }
                override fun onLoadCleared(placeholder: Drawable?) {}
            })
    }

    DisposableEffect(Unit) {
        onDispose {
            manager.playerManager.releasePlayer(index, player)
            player = null
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AndroidView(
            factory = {
                PlayerView(it).apply {
                    this.player = player
                    useController = false
                    artworkDisplayMode = PlayerView.ARTWORK_DISPLAY_MODE_FILL
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
                    foreground = artworkDrawable
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    setOnClickListener {
                        player?.playWhenReady = !(player?.playWhenReady ?: true)
                    }
                }
            },
            update = {
                it.player = player
            },
            modifier = Modifier.fillMaxSize()
        )

        PlayPauseOverlay(
            isPlaying = isPlaying,
            onClick = {
                player?.let {
                    if(it.isPlaying) it.pause()
                    else it.play()
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        ShortsContentItem(
            title = model.title,
            creator = model.creator,
            onButtonClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        )
    }
}