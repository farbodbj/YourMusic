package com.bale_bootcamp.yourmusic.presentation.ui.songlist

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import com.bale_bootcamp.yourmusic.data.model.Song
import com.bale_bootcamp.yourmusic.data.model.SortOrder
import com.bale_bootcamp.yourmusic.data.player.SongPlaybackController
import com.bale_bootcamp.yourmusic.data.repository.SongsRepository
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

    private var _playerStateFlow: MutableStateFlow<@Player.State Int> = MutableStateFlow(Player.STATE_IDLE)
    val playerState get() = _playerStateFlow

    private var _currentMusicFlow: MutableStateFlow<Int?> = MutableStateFlow(0)
    val currentMusic get() = _currentMusicFlow

    private var _currentPositionFlow: MutableStateFlow<Long> = MutableStateFlow(0L)
    val currentPosition get() = _currentPositionFlow

    private var _totalDurationFlow: MutableStateFlow<Long> = MutableStateFlow(0L)
    val totalDuration get() = _totalDurationFlow

    private var _isShuffleEnabledFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isShuffleEnabled get() = _isShuffleEnabledFlow

    private var _isRepeatOneEnabledFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isRepeatOneEnabled get() = _isRepeatOneEnabledFlow


    fun getSongsLists(sortOrder: SortOrder = SortOrder.DATE_ADDED_ASC) {
        viewModelScope.launch {
            val data = songsRepository.getSongsList(sortOrder)
            Log.d(TAG, "data count: ${data.count()}")
            _songsFlow.value = data
        }
    }


    @UnstableApi
    fun addSongs() {
        viewModelScope.launch {
            songs.collectLatest {songsList ->
                val mediaItems = songsList.map { song-> song.mediaItemFromSong() }
                playbackController.mediaItems.addAll(mediaItems)
            }
        }
    }

    @UnstableApi
    fun onSongClicked(position: Int) {
        Log.d(TAG, "song clicked: $position")
        playbackController.play(position)
    }

    @UnstableApi
    fun setMediaControllerCallback() {
        playbackController.mediaControllerCallback = {
                playerState, currentMusic, currentPosition,
                totalDuration, isShuffleEnabled, isRepeatOneEnabled ->

            _playerStateFlow.value = playerState.state
            _currentMusicFlow.value = currentMusic
            _currentPositionFlow.value = currentPosition
            _totalDurationFlow.value = totalDuration
            _isShuffleEnabledFlow.value = isShuffleEnabled
            _isRepeatOneEnabledFlow.value = isRepeatOneEnabled
        }
    }

    fun playPause() {
        playbackController.play(currentMusic.value ?: 0)
    }

    fun next() {
        playbackController.next()
    }

    fun previous() {
        playbackController.previous()
    }

}