package com.bale_bootcamp.yourmusic.presentation.ui.songlist

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import com.bale_bootcamp.yourmusic.data.model.Song
import com.bale_bootcamp.yourmusic.data.model.SortOrder
import com.bale_bootcamp.yourmusic.data.player.SongPlaybackController
import com.bale_bootcamp.yourmusic.data.repository.SongsRepository
import com.google.common.util.concurrent.MoreExecutors
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "SongListViewModel"
@HiltViewModel
class SongListViewModel @Inject constructor(
    private val songsRepository: SongsRepository,
    private val playbackController: SongPlaybackController,
): ViewModel() {

    private var _songsFlow: MutableStateFlow<List<Song>> = MutableStateFlow(emptyList())
    val songs get() = _songsFlow

    private val _mediaController: MutableStateFlow<MediaController?> = MutableStateFlow(null)
    val mediaController get() = _mediaController

    init {
        setMediaControllerOnFutureCompletion()
    }

    private fun setMediaControllerOnFutureCompletion() = playbackController
        .mediaControllerFuture.addListener({
            _mediaController.value = playbackController.mediaControllerFuture.get()
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
}