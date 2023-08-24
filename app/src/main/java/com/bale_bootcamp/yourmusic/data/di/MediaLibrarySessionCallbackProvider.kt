package com.bale_bootcamp.yourmusic.data.di

import com.bale_bootcamp.yourmusic.data.player.MediaLibrarySessionCallback
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object MediaLibrarySessionCallbackProvider {
    @Singleton
    @Provides
    fun provideMediaLibrarySessionCallback() = MediaLibrarySessionCallback()
}