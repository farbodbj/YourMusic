package com.bale_bootcamp.yourmusic.data.di

import com.bale_bootcamp.yourmusic.data.player.MediaSessionCallback
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object MediaSessionCallbackProvider {
    @Singleton
    @Provides
    fun provideMediaLibrarySessionCallback() = MediaSessionCallback()
}