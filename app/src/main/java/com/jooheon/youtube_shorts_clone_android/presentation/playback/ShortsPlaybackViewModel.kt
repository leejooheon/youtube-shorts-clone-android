package com.jooheon.youtube_shorts_clone_android.presentation.playback

import androidx.lifecycle.ViewModel
import com.jooheon.youtube_shorts_clone_android.model.ShortsModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ShortsPlaybackViewModel: ViewModel() {
    fun init(model: ShortsModel?) {
        model ?: return
        _shortsModel.tryEmit(model)
    }

    private val _shortsModel = MutableStateFlow(ShortsModel.default)
    val shortFormModel = _shortsModel.asStateFlow()


}