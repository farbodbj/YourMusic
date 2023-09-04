@file:SuppressLint("UnsafeOptInUsageError")
package com.bale_bootcamp.yourmusic.presentation.ui.sharedcomponent

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.session.MediaController
import com.bale_bootcamp.yourmusic.presentation.ui.player.SongPlaybackController
import com.google.common.util.concurrent.MoreExecutors
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "SongsSharedViewModel"
@HiltViewModel
class SongsSharedViewModel @Inject constructor(
    private val playbackController: SongPlaybackController,
): ViewModel() {

    private val _mediaControllerFlow: MutableStateFlow<MediaController?> = MutableStateFlow(null)
    val mediaControllerFlow get() = _mediaControllerFlow

    private val _songsPlaybackUiState: StateFlow<SongsPlaybackUiState> = MutableStateFlow(
        SongsPlaybackUiState(
        songsFlow = MutableStateFlow(emptyList()),
        currentSong = MutableStateFlow(null)
        )
    )
    val songsPlaybackUiState: StateFlow<SongsPlaybackUiState>
        get() = _songsPlaybackUiState

    init {
        addMediaControllerLoadSongsListener()
        addMediaControllerFutureListener()
    }


    private fun uiStateModificationScope(block: suspend SongsPlaybackUiState.() -> Unit) = viewModelScope.launch {
        songsPlaybackUiState.collectLatest {
            block(it)
        }
    }

    private fun loadSongsListener() = viewModelScope.launch(Dispatchers.IO) {
        uiStateModificationScope {
            playbackController.songsList.collectLatest {
                songsFlow.value = it
            }
        }
    }


    private fun addMediaControllerLoadSongsListener() = playbackController.mediaControllerFuture.addListener(
        this::loadSongsListener,
        MoreExecutors.directExecutor()
    )


    private fun addMediaControllerFutureListener() = playbackController.mediaControllerFuture.addListener({
        _mediaControllerFlow.value = playbackController.mediaControllerFuture.get()
    }, MoreExecutors.directExecutor())


    fun onSongClicked(position: Int) {
        Log.d(TAG, "song clicked: $position")
        playbackController.play(position)
    }


    fun setCurrentSongFlow() = viewModelScope.launch {
        playbackController.currentSong.collect {
            uiStateModificationScope {
                currentSong.value = it
            }
        }
    }
}