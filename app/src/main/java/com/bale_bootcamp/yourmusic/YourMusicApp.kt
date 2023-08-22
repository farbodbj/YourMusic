package com.bale_bootcamp.yourmusic

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class YourMusicApp: Application() {
    val name = "YourMusic"
}