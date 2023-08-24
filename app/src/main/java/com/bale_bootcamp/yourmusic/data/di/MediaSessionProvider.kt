package com.bale_bootcamp.yourmusic.data.di

import android.content.Context
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import com.bale_bootcamp.yourmusic.data.player.MediaLibrarySessionCallback
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object MediaSessionProvider {
    @Provides
    @Singleton
    fun provideMediaSession(
        @ApplicationContext context: Context,
        exoPlayer: ExoPlayer,
        callback: MediaLibrarySessionCallback
    ) = MediaSession.Builder(context, exoPlayer)
            .setCallback(callback)
            .build()
}