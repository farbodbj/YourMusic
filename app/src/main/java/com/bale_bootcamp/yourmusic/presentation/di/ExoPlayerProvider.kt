package com.bale_bootcamp.yourmusic.presentation.di

import android.content.Context
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.exoplayer.ExoPlayer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
@InstallIn(SingletonComponent::class)
@Module
object ExoPlayerProvider {
    @Provides
    @Singleton
    private fun provideAudioAttributes() = AudioAttributes.Builder()
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .setUsage(C.USAGE_MEDIA)
            .build()


    @Provides
    @Singleton
    fun provideExoPlayer(@ApplicationContext context: Context, audioAttributes: AudioAttributes) = ExoPlayer
            .Builder(context)
            .setAudioAttributes(audioAttributes, true)
            .build()
}