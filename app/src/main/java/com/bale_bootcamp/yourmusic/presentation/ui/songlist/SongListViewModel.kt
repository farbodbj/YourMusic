package com.bale_bootcamp.yourmusic.presentation.ui.songlist

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bale_bootcamp.yourmusic.data.model.Song
import com.bale_bootcamp.yourmusic.data.model.SortOrder
import com.bale_bootcamp.yourmusic.data.repository.SongsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "SongListViewModel"
@HiltViewModel
class SongListViewModel @Inject constructor(
    private val songsRepository: SongsRepository
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
}