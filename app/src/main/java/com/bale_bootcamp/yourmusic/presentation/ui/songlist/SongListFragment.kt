package com.bale_bootcamp.yourmusic.presentation.ui.songlist

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.util.UnstableApi
import androidx.recyclerview.widget.ListAdapter
import com.bale_bootcamp.yourmusic.data.model.Song
import com.bale_bootcamp.yourmusic.databinding.FragmentSongListBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


private const val TAG = "SongListFragment"
@AndroidEntryPoint
class SongListFragment : Fragment() {

    private var _binding: FragmentSongListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SongListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSongListBinding.inflate(layoutInflater, container, false)
        Log.d(TAG, "songs fragment created")
        return binding.root
    }

    @UnstableApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadSongs()
        setUiComponents()
        collectSongs()
        viewModel.addSongs()
        viewModel.setMediaControllerCallback()
        setPlayer()
    }


    private fun setUiComponents() {
        setSongsAdapter()
    }


    private fun setSongsAdapter() {
        val songsAdapter = SongsAdapter { position->
            viewModel.onSongClicked(position)
        }
        binding.songList.adapter = songsAdapter
    }

    @UnstableApi
    fun setPlayer() {
        binding.playbackControls.exoPlay.setOnClickListener { viewModel.playPause() }
        binding.playbackControls.exoPrev.setOnClickListener { viewModel.previous() }
        binding.playbackControls.exoNext.setOnClickListener { viewModel.next() }

    }

    private fun loadSongs() {
        viewModel.getSongsLists()
    }

    @Suppress("UNCHECKED_CAST")
    private fun collectSongs() {
        lifecycleScope.launch {
            viewModel.songs.collect {songs ->
                Log.d(TAG, "songs count: ${songs.count()}")
                (binding.songList.adapter as ListAdapter<Song, SongsAdapter.SongViewHolder>).submitList(songs)
            }
        }
    }

}