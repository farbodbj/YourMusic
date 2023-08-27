package com.bale_bootcamp.yourmusic.presentation.ui.player

interface PlaybackController {
    fun play(songIndex: Int)
    fun destroy()
}