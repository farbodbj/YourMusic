package com.bale_bootcamp.yourmusic.presentation.player

import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.app.PendingIntent.getActivity
import android.content.Intent
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.bale_bootcamp.yourmusic.presentation.ui.MainActivity
import dagger.hilt.EntryPoint
import javax.inject.Inject

@EntryPoint
@UnstableApi
class PlayerService: MediaSessionService() {
    @Inject
    private lateinit var mediaSession: MediaSession

    override fun onCreate() {
        super.onCreate()

        mediaSession.setSessionActivity(
            getActivity(
                this,
                0,
                Intent(this, MainActivity::class.java),
                FLAG_IMMUTABLE or FLAG_UPDATE_CURRENT
            )
        )
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession = mediaSession

    override fun onDestroy() {
        mediaSession.run {
            player.release()
            release()
            // find a way for making the session null
        }
        super.onDestroy()
    }
}