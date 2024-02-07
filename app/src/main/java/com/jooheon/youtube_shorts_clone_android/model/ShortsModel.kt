package com.jooheon.youtube_shorts_clone_android.model

import android.os.Parcelable
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import kotlinx.parcelize.Parcelize

@Parcelize
data class ShortsModel(
    val id: String,
    val mediaUrl: String,
    val creator: String,
    val title: String,
    val thumbnail: String,
): Parcelable {
    companion object {
        val default = ShortsModel(
            id = "default-id",
            mediaUrl = "",
            creator = "default-creator",
            title = "default-title",
            thumbnail = "",
        )
    }
    fun toMediaItem() = MediaItem.Builder()
        .setMediaId(id)
        .setUri(mediaUrl)
        .setMediaMetadata(
            MediaMetadata.Builder()
                .setTitle(title)
                .setArtist(creator)
                .setArtworkUri(thumbnail.toUri())
                .build()
        ).build()

}