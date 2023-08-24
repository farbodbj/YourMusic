package com.bale_bootcamp.yourmusic.data.model

import androidx.media3.common.MediaMetadata
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes


data class Song(
    val id: Long,
    val title: String,
    val album: String,
    val artist: String,
    val duration: Long,
    val uri: Uri) {
    constructor(mediaItem: MediaItem): this(
        id = mediaItem.mediaId.toLong(),
        title = mediaItem.mediaMetadata.title.toString(),
        album = mediaItem.mediaMetadata.albumTitle.toString(),
        artist = mediaItem.mediaMetadata.artist.toString(),
        duration = mediaItem.getDuration(),
        uri = mediaItem.requestMetadata.mediaUri!!)

    fun toMediaItem(): MediaItem {
            return MediaItem.Builder()
            .setUri(uri)
            .setMediaId(uri.toString()) // after many hours of debugging I found out the mediaId will be used as the address not uri :/
            .setMimeType(MimeTypes.BASE_TYPE_AUDIO)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(title)
                    .setAlbumTitle(album)
                    .setArtist(artist)
                    .build()
            ).build()
    }

}

fun Long.getAsMinutesColonSeconds(): String = "${this/60000}:${this%60}"

fun MediaItem.getDuration(): Long {
    val retriever = MediaMetadataRetriever()
    retriever.setDataSource(requestMetadata.mediaUri.toString())
    val time = retriever.extractMetadata(
        MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong() ?: 0
    retriever.release()
    return time
}



