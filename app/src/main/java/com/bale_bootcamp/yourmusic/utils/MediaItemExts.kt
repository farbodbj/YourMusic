package com.bale_bootcamp.yourmusic.utils

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.media3.common.MediaItem

object MediaItemExts {
    fun MediaItem.getDuration(context: Context): Long {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(context, Uri.parse(mediaId))
        val time = retriever.extractMetadata(
            MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong() ?: 0
        retriever.release()
        return time
    }
}