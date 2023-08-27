package com.bale_bootcamp.yourmusic.presentation.ui.di

import android.content.ComponentName
import android.content.Context
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.bale_bootcamp.yourmusic.presentation.ui.player.PlayerService
import com.google.common.util.concurrent.ListenableFuture
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MediaControllerFutureProvider {
    @Provides
    @Singleton
    fun provideMediaControllerFuture(@ApplicationContext context: Context): ListenableFuture<MediaController> {
        val sessionToken = SessionToken(context, ComponentName(context, PlayerService::class.java))
        return MediaController.Builder(context, sessionToken).buildAsync()
    }
}