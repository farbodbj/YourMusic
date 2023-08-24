package com.bale_bootcamp.yourmusic.presentation.ui.songlist

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.util.UnstableApi
import com.bale_bootcamp.yourmusic.data.model.Song
import com.bale_bootcamp.yourmusic.data.model.SortOrder
import com.bale_bootcamp.yourmusic.data.player.SongPlaybackController
import com.bale_bootcamp.yourmusic.data.repository.SongsRepository
import com.google.common.util.concurrent.MoreExecutors
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "SongListViewModel"
@HiltViewModel
class SongListViewModel @Inject constructor(
    private val songsRepository: SongsRepository,
    private val playbackController: SongPlaybackController
): ViewModel() {

    private var _songsFlow: MutableStateFlow<List<Song>> = MutableStateFlow<List<Song>>(emptyList())
    val songs get() = _songsFlow

    fun getSongs(sortOrder: SortOrder = SortOrder.DATE_ADDED_ASC) {
        viewModelScope.launch {
            val data = songsRepository.getSongsList(sortOrder)
            Log.d(TAG, "data count: ${data.count()}")
            _songsFlow.value = data
        }
    }

    fun addSongs() {
        viewModelScope.launch {
            songs.collect {
                Log.d(TAG, "adding ${it.size} songs to playback controller")
                playbackController.mediaControllerFuture.addListener({
                    Log.d(TAG, "media controller future is done")
                    playbackController.addSongs(it)
                }, MoreExecutors.directExecutor())
            }
        }
    }

    @UnstableApi
    fun onSongClicked(position: Int) {
        Log.d(TAG, "song clicked: $position")
        playbackController.play(position)
    }
}