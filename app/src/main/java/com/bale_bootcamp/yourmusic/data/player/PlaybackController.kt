package com.bale_bootcamp.yourmusic.data.player

interface PlaybackController {

    var mediaControllerCallback: (
        (playerState: PlayerState,
         currentMediaItemIndex: Int?,
         currentPosition: Long,
         totalDuration: Long,
         isShuffleEnabled: Boolean,
         isRepeatOneEnabled: Boolean) -> Unit
    )?

    fun play(songIndex: Int)
    fun resume()
    fun pause()
    fun next()
    fun previous()
    fun seekTo(position: Long)
    fun setShuffleMode(isEnabled: Boolean)
    fun setRepeatMode(isEnabled: Boolean)
    fun destroy()
}