package com.jooheon.youtube_shorts_clone_android

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jooheon.youtube_shorts_clone_android.model.PlayerUiState
import com.jooheon.youtube_shorts_clone_android.model.ShortsModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader

class PlayerViewModel: ViewModel() {
    private val assetModels = MutableStateFlow<List<ShortsModel>>(emptyList())

    internal val uiState: StateFlow<PlayerUiState> =
        combine(
            flow { emit(loadFromPlainText()) },
            assetModels
        ) { plainTextModels, assetModels ->
            plainTextModels + assetModels
        }.map {
            PlayerUiState.Shorts(it)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = PlayerUiState.Loading,
        )

    internal suspend fun loadFromAssets(context: Context) {
        assetModels.emit(loadFromAssetsInternal(context))
    }

    private fun loadFromPlainText(): List<ShortsModel> {
        val models = mediaUris.mapIndexed { index, url ->
            val number = index + 1

            val id = "plain-$number"
            val title = "title_$number"
            val artist = "artist_$number"
            val image = ""
            val source = url

            ShortsModel(
                id = id,
                mediaUrl = source,
                creator = artist,
                title = title,
                thumbnail = image,
            )
        }

        return models
    }

    private fun loadFromAssetsInternal(context: Context): List<ShortsModel> {
        val raw = context.assets
            .open("catalog.json")
            .bufferedReader()
            .use(BufferedReader::readText)
        val jsonObject = JSONObject(raw)
        val mediaList = jsonObject.getJSONArray("media")

        val models = mutableListOf<ShortsModel>()
        for(i in 0 until mediaList.length()) {
            val mediaObject =  mediaList.getJSONObject(i)
            val source = mediaObject.getString("source")

            if(!source.contains(".mp3")) {
                parseModel(mediaObject)?.let {
                    models.add(it)
                }
            }
        }

        return models
    }

    private fun parseModel(mediaObject: JSONObject): ShortsModel? {
        try {
            val id = mediaObject.getString("id")
            val title = mediaObject.getString("title")
            val artist = mediaObject.getString("artist")
            val image = mediaObject.getString("image")
            val source = mediaObject.getString("source")

            return ShortsModel(
                id = id,
                mediaUrl = source,
                creator = artist,
                title = title,
                thumbnail = image,
            )
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return null
    }

    companion object {
        private val mediaUris =
            mutableListOf(
                "https://storage.googleapis.com/exoplayer-test-media-0/shortform_1.mp4",
                "https://storage.googleapis.com/exoplayer-test-media-0/shortform_2.mp4",
                "https://storage.googleapis.com/exoplayer-test-media-0/shortform_3.mp4",
                "https://storage.googleapis.com/exoplayer-test-media-0/shortform_4.mp4",
                "https://storage.googleapis.com/exoplayer-test-media-0/shortform_6.mp4",
            )
    }
}