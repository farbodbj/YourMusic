package com.bale_bootcamp.yourmusic.data.player

import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.bale_bootcamp.yourmusic.utils.NullifyField.resetField
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


private const val TAG = "PlayerService"
@UnstableApi
@AndroidEntryPoint
class PlayerService: MediaSessionService() {
    @Inject
    lateinit var mediaSession: MediaSession


    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "player service created")
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo) = mediaSession

    override fun onDestroy() {
        mediaSession.run {
            player.release()
            release()
        }
        // better to use an annotation for the field
        resetField(this, "mediaSession")
        super.onDestroy()
    }
}