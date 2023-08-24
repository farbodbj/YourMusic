package com.bale_bootcamp.yourmusic.data.player

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import com.bale_bootcamp.yourmusic.data.model.Song
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

private const val TAG = "SongPlaybackController"
@UnstableApi
class SongPlaybackController @Inject constructor(
    @ApplicationContext context: Context,
    private val mediaControllerFuture: ListenableFuture<MediaController>
): PlaybackController {

    private val mediaController: MediaController?
        get() = if(mediaControllerFuture.isDone) {
            mediaControllerFuture.get()
        } else {
            Log.w(TAG, "mediaController get() called, when mediaControllerFuture was not ready")
            null
        }

    val mediaItems: MutableList<MediaItem> = mutableListOf()

    override var mediaControllerCallback: (
        (playerState: PlayerState,
         currentMusic: Song?,
         currentPosition: Long,
         totalDuration: Long,
         isShuffleEnabled: Boolean,
         isRepeatOneEnabled: Boolean) -> Unit)? = null


    init {
        mediaControllerFuture.addListener({ controllerListener() }, ContextCompat.getMainExecutor(context))
        setMediaControllerFutureListener()
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

    private fun setMediaControllerFutureListener() {
        mediaControllerFuture.addListener({
            mediaController?.addMediaItems(mediaItems)
        }, MoreExecutors.directExecutor())
    }


    override fun play(songIndex: Int) {
        Log.d(TAG, "player ready, playing: $songIndex")
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