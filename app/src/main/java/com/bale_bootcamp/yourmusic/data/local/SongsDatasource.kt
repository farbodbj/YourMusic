package com.bale_bootcamp.yourmusic.data.local

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.provider.MediaStore.Audio.Media
import android.util.Log
import com.bale_bootcamp.yourmusic.data.model.Song
import com.bale_bootcamp.yourmusic.data.model.SortOrder
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Collections
import javax.inject.Inject


private const val TAG = "SongsDatasource"
class SongsDatasource @Inject constructor(
    @ApplicationContext val context: Context
) {

    private val audioColumns = listOf(
        Media.TITLE,
        Media._ID,
        Media.ALBUM,
        Media.ARTIST,
        Media.DURATION
    )

    suspend fun getAllSongs(sortOrder: SortOrder): List<Song> = withContext(Dispatchers.IO) {
        val songs: MutableList<Song> = mutableListOf()
        queryMediaStore(sortOrder)?.use { cursor ->
            Log.i(TAG, "${cursor.count} songs found")
            populateSongsList(cursor, songs)
        }

        Collections.unmodifiableList(songs)
    }


    private fun queryMediaStore(sortOrder: SortOrder): Cursor? {
        val filterMusics = "${Media.IS_MUSIC} != 0"

        return context.contentResolver.query(
            Media.EXTERNAL_CONTENT_URI,
            audioColumns.toTypedArray(),
            filterMusics,
            null,
            sortOrder.sortQuery
        )
    }


    private fun populateSongsList(cursor: Cursor, songsList: MutableList<Song>) {
        while (cursor.moveToNext()) {
            val song = createSongFromCursor(cursor)
            songsList.add(song)
        }
    }

    private fun createSongFromCursor(cursor: Cursor): Song  = with(cursor) {
        val id = getLong(getColumnIndexOrThrow(Media._ID))
        val title = getString(getColumnIndexOrThrow(Media.TITLE))
        val album = getString(getColumnIndexOrThrow(Media.ALBUM))
        val artist = getString(getColumnIndexOrThrow(Media.ARTIST))
        val duration = getLong(getColumnIndexOrThrow(Media.DURATION))
        val contentUri =
            ContentUris.withAppendedId(
                Media.EXTERNAL_CONTENT_URI,
                id
            )

        return Song(id, title, album, artist, duration, contentUri)
    }
}