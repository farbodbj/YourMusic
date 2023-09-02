package com.bale_bootcamp.yourmusic.data.repository

import com.bale_bootcamp.yourmusic.data.local.SongsDatasource
import com.bale_bootcamp.yourmusic.data.model.Song
import com.bale_bootcamp.yourmusic.data.model.SortOrder
import javax.inject.Inject

class SongsRepository @Inject constructor(
    private val songsDatasource: SongsDatasource
) {
    suspend fun getSongsList(sortOrder: SortOrder): List<Song> {
        return songsDatasource.getAllSongs(sortOrder)
    }
}