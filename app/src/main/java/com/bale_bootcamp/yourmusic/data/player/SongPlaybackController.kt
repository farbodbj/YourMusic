package com.bale_bootcamp.yourmusic.data.player

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.media3.common.Player
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import com.bale_bootcamp.yourmusic.data.model.Song
import com.google.common.util.concurrent.ListenableFuture
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

private const val TAG = "SongPlaybackController"
@UnstableApi
class SongPlaybackController @Inject constructor(
    @ApplicationContext context: Context,
    var mediaControllerFuture: ListenableFuture<MediaController>
): PlaybackController {

    private val mediaController: MediaController?
        get() = if(mediaControllerFuture.isDone) mediaControllerFuture.get() else null


    override var mediaControllerCallback: (
        (playerState: PlayerState,
         currentMusic: Song?,
         currentPosition: Long,
         totalDuration: Long,
         isShuffleEnabled: Boolean,
         isRepeatOneEnabled: Boolean) -> Unit)? = null


    init {
//        val sessionToken = SessionToken(context, ComponentName(context, PlayerService::class.java))
//        mediaControllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        mediaControllerFuture.addListener({ controllerListener() }, ContextCompat.getMainExecutor(context))
    }

    private fun controllerListener() {
        mediaController?.addListener(object : Player.Listener {
            override fun onEvents(player: Player, events: Player.Events) {
                super.onEvents(player, events)

                with(player) {
                    mediaControllerCallback?.invoke(
                        playbackState.toPlayerState(isPlaying),
                        Song(currentMediaItem!!),
                        currentPosition.coerceAtLeast(0L),
                        duration.coerceAtLeast(0L),
                        shuffleModeEnabled,
                        repeatMode == Player.REPEAT_MODE_ONE
                    )
                }
            }
        })
    }


    private fun Int.toPlayerState(isPlaying: Boolean) =
        when (this) {
            Player.STATE_IDLE -> PlayerState.STOPPED
            Player.STATE_ENDED -> PlayerState.STOPPED
            else -> if (isPlaying) PlayerState.PLAYING else PlayerState.PAUSED
        }

    override fun addSongs(songs: List<Song>) {
        Log.d(TAG, "add songs called, media controller is null: ${mediaController == null}")
        val mediaItems = songs.map {
            Log.d(TAG, "add song: ${it.title}")
            it.toMediaItem()
        }

        mediaController?.addMediaItems(0, mediaItems)
        Log.d(TAG, "addSongs: ${mediaController?.mediaItemCount}")
    }

    override fun play(songIndex: Int) {
        Log.d(TAG, "player ready, play: $songIndex")
        Log.d(TAG, "media items count: ${mediaController?.mediaItemCount}")

        mediaController?.apply {
            seekToDefaultPosition(songIndex)
            playWhenReady = true
            prepare()
        }
    }

    override fun resume() {
        mediaController?.play()
    }

    override fun pause() {
        mediaController?.pause()
    }

    override fun next() {
        mediaController?.seekToNext()
    }

    override fun previous() {
        mediaController?.seekToPrevious()
    }

    override fun seekTo(position: Long) {
        mediaController?.seekTo(position)
    }

    override fun setShuffleMode(isEnabled: Boolean) {
        mediaController?.shuffleModeEnabled = isEnabled
    }

    override fun setRepeatMode(isEnabled: Boolean) {
        mediaController?.repeatMode =
            if (isEnabled)
                Player.REPEAT_MODE_ONE
            else
                Player.REPEAT_MODE_OFF
    }

    override fun destroy() {
        MediaController.releaseFuture(mediaControllerFuture)
        mediaControllerCallback = null
    }
}