package com.bale_bootcamp.yourmusic.presentation.ui.player

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import com.bale_bootcamp.yourmusic.data.model.Song
import com.bale_bootcamp.yourmusic.data.repository.SongsRepository
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "SongPlaybackController"
@UnstableApi
@Singleton
class SongPlaybackController @Inject constructor(
    @ApplicationContext private val context: Context,
    val mediaControllerFuture: ListenableFuture<MediaController>,
    songsRepository: SongsRepository,
    private val coroutineScope: CoroutineScope
): PlaybackController {

    private val mediaController: MediaController?
        get() = if(mediaControllerFuture.isDone) {
            mediaControllerFuture.get()
        } else {
            Log.w(TAG, "mediaController get() called, when mediaControllerFuture was not ready")
            null
        }

    val songsList = songsRepository.getSongsList()

    private var _currentSong: MutableStateFlow<Song?> = MutableStateFlow(null)
    val currentSong get() = _currentSong


    init {
        setMediaControllerListener()
    }


    private fun setMediaControllerListener() {
        mediaControllerFuture.addListener({
            addMediaTransitionListener(mediaController)
            coroutineScope.launch(Dispatchers.Main) {
                setControllerMediaItems()
            }
        }, MoreExecutors.directExecutor())
    }


    private fun addMediaTransitionListener(mediaController: MediaController?) {
        mediaController?.addListener(object: Player.Listener {
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                super.onMediaItemTransition(mediaItem, reason)
                Log.d(TAG, "media item count: ${mediaController.mediaItemCount}")
                if(mediaItem != null)
                    _currentSong.value = Song(mediaItem, context)
                }
        })
    }


    private suspend fun setControllerMediaItems() = songsList.collect { songsList ->
        mediaController?.setMediaItems(songsList.map { song->
            song.mediaItemFromSong()
        })
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