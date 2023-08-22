package com.bale_bootcamp.yourmusic.presentation.player

import androidx.media3.common.MediaItem
import androidx.media3.session.MediaSession
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture

class MediaLibrarySessionCallback: MediaSession.Callback {
    override fun onAddMediaItems(
        mediaSession: MediaSession,
        controller: MediaSession.ControllerInfo,
        mediaItems: MutableList<MediaItem>
    ): ListenableFuture<MutableList<MediaItem>> {
        val updatedMediaItems = mediaItems.map { mediaItem ->
            mediaItem.buildUpon().setUri(mediaItem.mediaId).build()
        }.toMutableList()

        return Futures.immediateFuture(updatedMediaItems)
    }
}