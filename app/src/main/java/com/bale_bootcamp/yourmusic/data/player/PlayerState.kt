package com.bale_bootcamp.yourmusic.data.player

import androidx.media3.common.Player

enum class PlayerState {
    PLAYING,
    PAUSED,
    STOPPED
}

fun Int.toPlayerState(isPlaying: Boolean) =
    when (this) {
        Player.STATE_IDLE -> PlayerState.STOPPED
        Player.STATE_ENDED -> PlayerState.STOPPED
        else -> if (isPlaying) PlayerState.PLAYING else PlayerState.PAUSED
    }