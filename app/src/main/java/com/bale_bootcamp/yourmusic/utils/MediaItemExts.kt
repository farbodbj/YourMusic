package com.bale_bootcamp.yourmusic.utils

import android.media.MediaMetadataRetriever
import androidx.media3.common.MediaItem

object MediaItemExts {
    fun MediaItem.getDuration(): Long {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(requestMetadata.mediaUri.toString())
        val time = retriever.extractMetadata(
            MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong() ?: 0
        retriever.release()
        return time
    }
}