package com.bale_bootcamp.yourmusic.presentation.ui.sharedcomponent

import com.bale_bootcamp.yourmusic.data.model.Song
import kotlinx.coroutines.flow.MutableStateFlow

data class SongsPlaybackUiState(
    val songsFlow: MutableStateFlow<List<Song>>,
    val currentSong: MutableStateFlow<Song?>,
)
