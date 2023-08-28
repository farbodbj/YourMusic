package com.bale_bootcamp.yourmusic.presentation.ui.songview

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.util.UnstableApi
import androidx.navigation.fragment.findNavController
import androidx.palette.graphics.Palette
import com.bale_bootcamp.yourmusic.R
import com.bale_bootcamp.yourmusic.data.model.Song
import com.bale_bootcamp.yourmusic.databinding.FragmentSongViewBinding
import com.bale_bootcamp.yourmusic.presentation.ui.SongsSharedViewModel
import com.bale_bootcamp.yourmusic.utils.DefaultGlideLogger
import com.bumptech.glide.Glide
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.FileNotFoundException

private const val TAG = "SongViewFragment"
class SongViewFragment : Fragment() {
    private var _binding: FragmentSongViewBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SongsSharedViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSongViewBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setCurrentSongFlow()
        setUiComponents()
    }

    private fun setUiComponents() {
        setPlayerController()
        setBackground()
        setSongProperties()
        setSongCoverView()
        setHideViewButton()

    }

    private fun setSongProperties () = lifecycleScope.launch {
        viewModel.currentSong.collectLatest { song ->
            val songCoverBitmap = loadSongCoverBitmap(song!!)
            val palette = Palette.Builder(songCoverBitmap).generate()

            binding.apply {
                songTitle.text = song.title
                songTitle.setTextColor(palette.getLightMutedColor(1))

                songArtist.text = song.artist
                songArtist.setTextColor(palette.getLightMutedColor(1))
            }
        }
    }

    private fun setHideViewButton() = binding.hideButton.setOnClickListener{
            findNavController().popBackStack()
        }


    private fun setBackground() = lifecycleScope.launch {
        viewModel.currentSong.collectLatest {song ->
            val songCoverBitmap = loadSongCoverBitmap(song!!)
            val palette = Palette.Builder(songCoverBitmap).generate()

            binding.root.background = getCoverBackgroundGradient(palette)
        }
    }

    private fun getCoverBackgroundGradient(palette: Palette) = GradientDrawable(
        GradientDrawable.Orientation.TOP_BOTTOM,
        intArrayOf(
            palette.getDarkVibrantColor(0),
            palette.getLightVibrantColor(1)
        )
    )


    private fun setSongCoverView() = lifecycleScope.launch {
        viewModel.currentSong.collectLatest {song ->
            loadSongCover(song!!)
        }
    }


    private fun loadSongCover(song: Song) {
        val songCoverBitmap = loadSongCoverBitmap(song)
        Glide.with(binding.root.context)
            .asBitmap()
            .load(songCoverBitmap)
            .addListener(DefaultGlideLogger(TAG))
            .into(binding.songCover)
    }


    private fun loadSongCoverBitmap(song: Song): Bitmap = try {
        val (width, height) = listOf(
            R.dimen.song_view_song_cover_width,
            R.dimen.song_view_song_cover_height
        ).map { resId ->
            binding.root.resources.getDimension(resId).toInt()
        }
        binding.root.context.contentResolver.loadThumbnail(
            song.uri,
            Size(width, height),
            null
        )
    } catch (e: FileNotFoundException) {
        BitmapFactory.decodeResource(requireContext().resources, R.drawable.music_cover_placeholder)
    }


    @UnstableApi
    private fun setPlayerController() = lifecycleScope.launch {
        viewModel.mediaControllerFlow.collectLatest { mediaController ->
            binding.playbackControls.player = mediaController
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}