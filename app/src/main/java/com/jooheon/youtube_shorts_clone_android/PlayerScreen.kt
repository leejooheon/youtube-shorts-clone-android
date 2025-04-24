package com.jooheon.youtube_shorts_clone_android

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jooheon.youtube_shorts_clone_android.model.PlayerUiState
import com.jooheon.youtube_shorts_clone_android.component.ShortsSection

@Composable
fun PlayerScreen(
    viewModel: PlayerViewModel
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadFromAssets(context)
    }

    PlayerScreenInternal(uiState)
}

@Composable
private fun PlayerScreenInternal(
    uiState: PlayerUiState,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        when(uiState) {
            is PlayerUiState.Loading -> CircularProgressIndicator()
            is PlayerUiState.Shorts -> ShortsSection(uiState.models)
        }
    }
}