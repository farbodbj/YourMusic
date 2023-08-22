package com.bale_bootcamp.yourmusic.data.model

import android.provider.MediaStore

private const val DATE_ADDED: String = MediaStore.Audio.Media.DATE_ADDED
private const val TITLE: String = MediaStore.Audio.Media.TITLE
enum class SortOrder(val sortQuery: String) {
    DATE_ADDED_ASC("$DATE_ADDED ASC"),
    DATE_ADDED_DSC ("$DATE_ADDED DSC"),
    TITLE_ASC ("$TITLE ASC"),
    TITLE_DSC ("$TITLE DSC");
}