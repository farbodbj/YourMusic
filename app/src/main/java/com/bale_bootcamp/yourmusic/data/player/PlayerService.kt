package com.bale_bootcamp.yourmusic.data.player

import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
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

//        mediaSession.setSessionActivity(
//            getActivity(
//                this,
//                0,
//                Intent(this, MainActivity::class.java),
//                FLAG_IMMUTABLE or FLAG_UPDATE_CURRENT
//            )
//        )

        Log.d(TAG, "onCreate: called")
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession {
        Log.d(TAG, "on get session called")
        return mediaSession
    }

    override fun onDestroy() {
        mediaSession.run {
            player.release()
            release()
            // find a way for making the session null
        }
        super.onDestroy()
    }
}