package com.bale_bootcamp.yourmusic.presentation.di

import com.bale_bootcamp.yourmusic.presentation.player.MediaLibrarySessionCallback
import dagger.Module

@Module
object MediaLibrarySessionCallbackProvider {
    fun provideMediaLibrarySessionCallback() = MediaLibrarySessionCallback()
}