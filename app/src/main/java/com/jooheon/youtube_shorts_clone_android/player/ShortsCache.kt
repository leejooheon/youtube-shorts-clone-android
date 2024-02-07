package com.jooheon.youtube_shorts_clone_android.player

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import java.io.File

@OptIn(UnstableApi::class)
object ShortsCache {
    private const val CACHE_MAX_SIZE = 256 * 1000 * 1000L
    internal lateinit var cache: SimpleCache

    fun init(context: Context) {
        cache = SimpleCache(
            cacheDir(context),
            LeastRecentlyUsedCacheEvictor(CACHE_MAX_SIZE),
            StandaloneDatabaseProvider(context)
        )
    }

    fun release(context: Context) {
        cache.release()
        cacheDir(context).delete()
    }

    private fun cacheDir(context: Context): File {
        val cacheDirectory = File(context.externalCacheDir, "/shorts").also { directory ->
            if (directory.exists()) return@also
            directory.mkdir()
        }

        return cacheDirectory
    }
}