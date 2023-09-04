package com.bale_bootcamp.yourmusic.data.repository

import com.bale_bootcamp.yourmusic.data.local.SongsDatasource
import com.bale_bootcamp.yourmusic.data.model.Song
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SongsRepository @Inject constructor(
    private val songsDatasource: SongsDatasource
) {
    fun getSongsList(): Flow<List<Song>> = songsDatasource.songsListFlow
}