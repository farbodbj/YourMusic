package com.bale_bootcamp.yourmusic.data.repository

import com.bale_bootcamp.yourmusic.data.local.SongsDatasource
import com.bale_bootcamp.yourmusic.data.model.Song
import com.bale_bootcamp.yourmusic.data.model.SortOrder
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SongsRepository @Inject constructor(
    private val songsDatasource: SongsDatasource
) {
    fun getSongsList(sortOrder: SortOrder = SortOrder.DATE_ADDED_ASC): Flow<List<Song>> = songsDatasource.getAllSongs(sortOrder)

}