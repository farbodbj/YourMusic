package com.bale_bootcamp.yourmusic.data.local

import android.content.ContentUris
import android.content.Context
import android.database.ContentObserver
import android.database.Cursor
import android.provider.MediaStore.Audio.Media
import com.bale_bootcamp.yourmusic.data.model.Song
import com.bale_bootcamp.yourmusic.data.model.SortOrder
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
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

    fun getAllSongs(sortOrder: SortOrder): Flow<List<Song>> = queryMediaStore(sortOrder)

    private fun queryMediaStore(sortOrder: SortOrder) = callbackFlow {
        val callback = object : ContentObserver(null) {
            override fun onChange(selfChange: Boolean) {
                super.onChange(selfChange)
                trySend(convertCursorToSongs(getCursor(sortOrder)))
            }
        }
        trySend(convertCursorToSongs(getCursor(sortOrder)))
        context.contentResolver.registerContentObserver(Media.EXTERNAL_CONTENT_URI, true, callback)
        awaitClose { context.contentResolver.unregisterContentObserver(callback) }

    }.flowOn(Dispatchers.IO)


    private fun convertCursorToSongs(cursor: Cursor?) = cursor?.let {populateSongsList(it)}.orEmpty()


    private fun getCursor(sortOrder: SortOrder): Cursor? {
        val filterMusics = "${Media.IS_MUSIC} != 0"
        return context.contentResolver.query(
            Media.EXTERNAL_CONTENT_URI,
            audioColumns.toTypedArray(),
            filterMusics,
            null,
            sortOrder.sortQuery
        )
    }


    private fun populateSongsList(cursor: Cursor): List<Song> {
        val songsList = mutableListOf<Song>()
        while (cursor.moveToNext()) {
            val song = createSongFromCursor(cursor)
            songsList.add(song)
        }
        return songsList
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