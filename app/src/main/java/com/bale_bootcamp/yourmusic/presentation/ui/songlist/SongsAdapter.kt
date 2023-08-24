package com.bale_bootcamp.yourmusic.presentation.ui.songlist

import android.util.Log
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bale_bootcamp.yourmusic.R
import com.bale_bootcamp.yourmusic.data.model.Song
import com.bale_bootcamp.yourmusic.data.model.getAsMinutesColonSeconds
import com.bale_bootcamp.yourmusic.databinding.SongViewHolderBinding
import com.bumptech.glide.Glide
import java.io.FileNotFoundException

private const val TAG = "SongsAdapter"
class SongsAdapter(
    private val songOnClickListener: ((Int)->Unit)
): ListAdapter<Song, SongsAdapter.SongViewHolder>(DiffCallback) {
    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Song>() {
            override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder = SongViewHolder(
        SongViewHolderBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
        )


    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        holder.bind(getItem(position))
        Log.d(TAG, "onclick listener set")
        holder.onSongClickListener = songOnClickListener
    }

    class SongViewHolder(
        private val binding: SongViewHolderBinding,
    ): RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        lateinit var onSongClickListener: ((Int)->Unit)

        companion object {
            private const val TAG = "SongViewHolder"
        }

        private val songCoverResId = listOf(R.dimen.song_cover_width, R.dimen.song_cover_height)
        private val size: Size get() {
            val (width, height) = songCoverResId.map {
                    resId -> binding.root.resources.getDimension(resId).toInt()
            }

            return Size(width, height)
        }

        fun bind(song: Song) {
            bindSongProperties(song)
            bindSongCover(song)
            binding.root.setOnClickListener(this)
        }

        private fun bindSongProperties(song: Song) {
            binding.apply {
                songTitle.text = song.title
                artistName.text = song.artist
                songDuration.text = song.duration.getAsMinutesColonSeconds()
            }
        }


        private fun bindSongCover(song: Song) {
            try {
                val songCoverBitmap = binding.root.context.contentResolver.loadThumbnail(song.uri, size, null)
                Glide.with(binding.root.context)
                    .asBitmap()
                    .load(songCoverBitmap)
                    .into(binding.songCover)
            }
            catch (e: FileNotFoundException) {
                Log.d(TAG, "no album art found for ${song.title}, loading the placeholder.")
                Glide.with(binding.root.context)
                    .load(R.drawable.music_cover_placeholder)
                    .into(binding.songCover)
            }
        }

        override fun onClick(p0: View?) {
            Log.d(TAG, "song clicked at position $absoluteAdapterPosition")
            onSongClickListener(absoluteAdapterPosition)
        }
    }
}