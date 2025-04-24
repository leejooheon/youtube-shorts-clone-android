package com.jooheon.youtube_shorts_clone_android.player

import androidx.annotation.OptIn
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.preload.DefaultPreloadManager.Builder
import androidx.media3.exoplayer.util.EventLogger
import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
import com.google.common.collect.Maps
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.util.Collections
import java.util.LinkedList
import java.util.Queue
import kotlin.time.Duration.Companion.milliseconds

@OptIn(UnstableApi::class)
class PlayerManager(
    private val numberOfPlayers: Int,
    preloadBuilder: Builder,
) {
    private val playerFactory = PlayerFactory(preloadBuilder)

    private val availablePlayerQueue: Queue<Int> = LinkedList()
    private val playerMap: BiMap<Int, ExoPlayer> = Maps.synchronizedBiMap(HashBiMap.create())
    private val playerRequestTokenSet: MutableSet<Int> = Collections.synchronizedSet(HashSet<Int>())


    internal fun play(player: Player) {
        pauseAllPlayers(player)
        player.play()
    }

    private fun pauseAllPlayers(keepOngoingPlayer: Player? = null) {
        for (player in playerMap.values) {
            if (player != keepOngoingPlayer) {
                player.pause()
            }
        }
    }

    internal fun releasePlayer(token: Int, player: ExoPlayer?) {
        synchronized(playerMap) {
            playerRequestTokenSet.remove(token)
            player?.stop()
            player?.clearMediaItems()
            if (player != null) {
                val playerNumber = playerMap.inverse()[player]
                availablePlayerQueue.add(playerNumber)
            }
        }
    }

    internal fun destroyPlayers() {
        synchronized(playerMap) {
            for (i in 0 until playerMap.size) {
                playerMap[i]?.release()
                playerMap.remove(i)
            }
        }
    }

    internal suspend fun acquirePlayer(token: Int): ExoPlayer? {
        playerRequestTokenSet.add(token)

        while(playerRequestTokenSet.contains(token)) {
            val player = withContext(Dispatchers.Main) {
                acquirePlayerInternal(token)
            }
            if(player != null) return player
            else delay(500.milliseconds)
        }

        return null
    }

    private fun acquirePlayerInternal(token: Int): ExoPlayer? {
        val player = synchronized(playerMap) {
            when {
                availablePlayerQueue.isNotEmpty() -> {
                    val num = availablePlayerQueue.remove()
                    playerRequestTokenSet.remove(token)
                    playerMap[num]
                }
                playerMap.size < numberOfPlayers -> {
                    val newPlayer = playerFactory.createPlayer()
                    playerMap[playerMap.size] = newPlayer
                    playerRequestTokenSet.remove(token)
                    newPlayer
                }
                else -> null
            }
        }

        return player
    }

    @OptIn(UnstableApi::class)
    class PlayerFactory(private val preloadManagerBuilder: Builder) {
        private var playerCounter = 0

        internal fun createPlayer(): ExoPlayer {
            val player = preloadManagerBuilder.buildExoPlayer()
            player.addAnalyticsListener(EventLogger("player-$playerCounter"))
            playerCounter++
            player.repeatMode = ExoPlayer.REPEAT_MODE_ONE
            return player
        }
    }
}