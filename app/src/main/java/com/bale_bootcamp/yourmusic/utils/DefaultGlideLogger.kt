package com.bale_bootcamp.yourmusic.utils

import android.graphics.Bitmap
import android.util.Log
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

class DefaultGlideLogger<T>(
    private val tag: String
) : RequestListener<T> {

    override fun onLoadFailed(
        e: GlideException?,
        model: Any?,
        target: Target<T>?,
        isFirstResource: Boolean
    ): Boolean {
        Log.e(tag, "onLoadFailed: ${e?.message}")
        return false
    }

    override fun onResourceReady(
        resource: T?,
        model: Any?,
        target: Target<T>?,
        dataSource: DataSource?,
        isFirstResource: Boolean
    ): Boolean {
        Log.d(tag, "onResourceReady: image loaded")
        return false
    }
}
