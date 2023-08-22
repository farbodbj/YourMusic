package com.bale_bootcamp.yourmusic.data.model

import android.net.Uri

data class Song(
    val id: Long,
    val title: String,
    val album: String,
    val artist: String,
    val duration: Long,
    val uri: Uri)

fun Long.getAsMinutesColonSeconds(): String = "${this/60000}:${this%60}"


