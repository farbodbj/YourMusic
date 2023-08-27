package com.bale_bootcamp.yourmusic.presentation.ui.player

import androidx.media3.common.Player

enum class PlayerState {
    PLAYING,
    PAUSED,
    STOPPED;

    val state: Int @Player.State get() = when (this) {
            PLAYING -> Player.STATE_READY
            PAUSED -> Player.STATE_READY
            STOPPED -> Player.STATE_IDLE
    }
}

fun Int.toPlayerState(isPlaying: Boolean) =
    when (this) {
        Player.STATE_IDLE -> PlayerState.STOPPED
        Player.STATE_ENDED -> PlayerState.STOPPED
        else -> if (isPlaying) PlayerState.PLAYING else PlayerState.PAUSED
    }