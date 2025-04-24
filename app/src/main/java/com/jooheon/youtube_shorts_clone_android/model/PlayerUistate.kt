package com.jooheon.youtube_shorts_clone_android.model

sealed interface PlayerUiState {
    data object Loading: PlayerUiState
    data class Shorts(val models: List<ShortsModel>): PlayerUiState
}