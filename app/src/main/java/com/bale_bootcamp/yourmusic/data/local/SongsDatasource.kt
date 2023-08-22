package com.bale_bootcamp.yourmusic.data.local

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import android.util.Log
import com.bale_bootcamp.yourmusic.data.model.Song
import com.bale_bootcamp.yourmusic.data.model.SortOrder
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Collections
import javax.inject.Inject


private const val TAG = "SongsDatasource"
class SongsDatasource @Inject constructor(
    @ApplicationContext val context: Context
) {

    private val audioColumns = listOf(
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.ALBUM,
        MediaStore.Audio.Media.ARTIST,
    )

    private lateinit var audioColumnIndexes: Map<String, Int>


    fun getAllSongs(sortOrder: SortOrder): List<Song> {
        val songs: MutableList<Song> = mutableListOf()
        queryMediaStore(sortOrder)!!.use { cursor ->
            setColumnIndexes(cursor)

            if (cursor.count > 0 && this::audioColumnIndexes.isInitialized) {
                Log.i(TAG, "${cursor.count} songs found")
                populateSongsList(cursor, songs)
            }

        }

        return Collections.unmodifiableList(songs)
    }

    private fun queryMediaStore(sortOrder: SortOrder): Cursor? {
        val filterMusics = "${MediaStore.Audio.Media.IS_MUSIC} != 0"

        return context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            audioColumns.toTypedArray(),
            filterMusics,
            null,
            sortOrder.sortQuery
        )
    }


    private fun setColumnIndexes(cursor: Cursor) {
        val (titleColumn, idColumn, albumColumn, artistColumn) =
            audioColumns.map {
                cursor.getColumnIndexOrThrow(it)
            }

        audioColumnIndexes = mapOf(
            "idColumn" to idColumn,
            "titleColumn" to titleColumn,
            "albumColumn" to albumColumn,
            "artistsColumn" to artistColumn
        )
    }


    private fun populateSongsList(cursor: Cursor, songsList: MutableList<Song>) {
        while (cursor.moveToNext()) {
            val song = createSongFromCursor(cursor)
            songsList.add(song)
        }
    }

    private fun createSongFromCursor(cursor: Cursor): Song {
        val id = cursor.getLong(audioColumnIndexes["id"]!!)
        val title = cursor.getString(audioColumnIndexes["title"]!!)
        val album = cursor.getString(audioColumnIndexes["album"]!!)
        val artist = cursor.getString(audioColumnIndexes["artist"]!!)
        val contentUri =
            ContentUris.withAppendedId(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                id
            )

        return Song(id, title, album, artist, contentUri)
    }
}