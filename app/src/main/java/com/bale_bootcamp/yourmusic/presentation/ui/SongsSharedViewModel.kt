package com.bale_bootcamp.yourmusic.presentation.ui

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import com.bale_bootcamp.yourmusic.data.model.Song
import com.bale_bootcamp.yourmusic.data.model.SortOrder
import com.bale_bootcamp.yourmusic.data.repository.SongsRepository
import com.bale_bootcamp.yourmusic.presentation.ui.player.SongPlaybackController
import com.google.common.util.concurrent.MoreExecutors
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "SongsSharedViewModel"
@HiltViewModel
class SongsSharedViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val songsRepository: SongsRepository,
    private val playbackController: SongPlaybackController,
): ViewModel() {

    private var _songsFlow: MutableStateFlow<List<Song>> = MutableStateFlow(emptyList())
    val songs get() = _songsFlow

    private val _mediaControllerFlow: MutableStateFlow<MediaController?> = MutableStateFlow(null)
    val mediaControllerFlow get() = _mediaControllerFlow

    private val _currentSong: MutableStateFlow<Song?> = MutableStateFlow(null)
    val currentSong = _currentSong

    init {
        setMediaControllerOnFutureCompletion()
    }

    private fun setMediaControllerOnFutureCompletion() = playbackController
        .mediaControllerFuture.addListener({
            _mediaControllerFlow.value = playbackController.mediaControllerFuture.get()
        }, MoreExecutors.directExecutor())


    fun getSongsLists(sortOrder: SortOrder = SortOrder.DATE_ADDED_ASC) {
        viewModelScope.launch {
            val data = songsRepository.getSongsList(sortOrder)
            Log.d(TAG, "data count: ${data.count()}")
            _songsFlow.value = data
        }
    }

    @UnstableApi
    fun addSongsToPlayer() = viewModelScope.launch {
        songs.collectLatest {songsList ->
            val mediaItems = songsList.map { song ->
                song.mediaItemFromSong()
            }
            playbackController.addMediaItems(mediaItems)
        }
    }

    @UnstableApi
    fun onSongClicked(position: Int) {
        Log.d(TAG, "song clicked: $position")
        playbackController.play(position)
    }

    @UnstableApi
    fun setCurrentSongFlow() {
         viewModelScope.launch {
            mediaControllerFlow.collectLatest {mediaController ->
                _currentSong.value = mediaController?.currentMediaItem?.let { currentItem->
                    Song(currentItem, context)
                }
                addMediaTransitionListener(mediaController)
            }
        }
    }

    private fun addMediaTransitionListener(mediaController: MediaController?) {
        mediaController?.addListener(object: Player.Listener {
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                super.onMediaItemTransition(mediaItem, reason)
                if(mediaItem != null)
                    _currentSong.value = Song(mediaItem, context)
            }
        })
    }
}