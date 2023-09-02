package com.bale_bootcamp.yourmusic.presentation.ui.player

import androidx.media3.common.MediaItem
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import javax.inject.Inject

private const val TAG = "SongPlaybackController"
@UnstableApi
class SongPlaybackController @Inject constructor(
    val mediaControllerFuture: ListenableFuture<MediaController>
): PlaybackController {

    private val mediaController: MediaController?
        get() = if(mediaControllerFuture.isDone) {
            mediaControllerFuture.get()

        } else {
            Log.w(TAG, "mediaController get() called, when mediaControllerFuture was not ready")
            null
        }

    private val mediaItems: MutableList<MediaItem> = mutableListOf()

    init {
        // onAddMediaItemsListener()
    }

    private fun onAddMediaItemsListener() {
        mediaControllerFuture.addListener({
            Log.i(TAG, "adding media items, count: ${mediaItems.count()}")
            mediaControllerFuture.get().addMediaItems(mediaItems)
        }, MoreExecutors.directExecutor())
    }

    fun addMediaItems(mediaItems: List<MediaItem>) {
        mediaItems.forEach { mediaItem ->
            if (mediaItem !in this.mediaItems) {
                this.mediaItems.add(mediaItem)
            }
        }
        onAddMediaItemsListener()
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

    override fun destroy() {
        MediaController.releaseFuture(mediaControllerFuture)
    }
}