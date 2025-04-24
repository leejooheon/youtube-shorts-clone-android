package com.jooheon.youtube_shorts_clone_android.component

import android.util.Log
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.media3.common.util.UnstableApi
import com.jooheon.youtube_shorts_clone_android.player.rememberShortsPlaybackManager
import com.jooheon.youtube_shorts_clone_android.model.ShortsModel

@OptIn(UnstableApi::class)
@Composable
fun ShortsSection(
    models: List<ShortsModel>,
    modifier: Modifier = Modifier,
) {
    val shortsPlaybackManager = rememberShortsPlaybackManager(
        numberOfPlayers = 3,
        models = models
    )
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { Int.MAX_VALUE }
    )

    LaunchedEffect(pagerState.currentPage) {
        shortsPlaybackManager.preloadController.setCurrentPlayingIndex(pagerState.currentPage)
    }

    DisposableEffect(Unit) {
        onDispose {
            shortsPlaybackManager.release()
        }
    }

    VerticalPager(
        state = pagerState,
        modifier = modifier.fillMaxSize()
    ) { page ->
        LaunchedEffect(page) {
            Log.d("ShortsSection", "onItemAttached: $page")
            shortsPlaybackManager.preloadController.onItemAttached(page)
        }

        ShortsPlaybackItem(
            index = page,
            model = models[page.mod(models.size)],
            isFocused = pagerState.currentPage == page,
            manager = shortsPlaybackManager
        )
    }
}