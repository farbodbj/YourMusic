package com.bale_bootcamp.yourmusic.data.player

interface PlaybackController {
    fun play(songIndex: Int)
    fun destroy()
}