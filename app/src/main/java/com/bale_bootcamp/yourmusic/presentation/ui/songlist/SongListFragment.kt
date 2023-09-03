package com.bale_bootcamp.yourmusic.presentation.ui.songlist

import android.Manifest
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES.TIRAMISU
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.util.UnstableApi
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ListAdapter
import com.bale_bootcamp.yourmusic.data.model.Song
import com.bale_bootcamp.yourmusic.databinding.FragmentSongListBinding
import com.bale_bootcamp.yourmusic.presentation.ui.SongsSharedViewModel
import com.bale_bootcamp.yourmusic.utils.PermissionUtil.checkAndAskPermission
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


private const val TAG = "SongListFragment"
@AndroidEntryPoint
class SongListFragment : Fragment() {

    private var _binding: FragmentSongListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SongsSharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSongListBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    @UnstableApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkPermission(this::loadSongs) {
            Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
        }
        setUiComponents()
    }

    private fun checkPermission(onPermissionGranted: () -> Unit, onPermissionDenied: () -> Unit) {
        val permission = if (VERSION.SDK_INT > TIRAMISU) (Manifest.permission.READ_MEDIA_AUDIO) else (Manifest.permission.READ_EXTERNAL_STORAGE)
        checkAndAskPermission(permission, onPermissionGranted, onPermissionDenied)
    }

    private fun setUiComponents() {
        setSongsList()
        setPlayerController()
    }


    private fun setSongsList() {
        setSongsAdapter()
        collectSongs()
    }


    private fun setSongsAdapter() {
        val songsAdapter = SongsAdapter {position->
            viewModel.onSongClicked(position)
        }

        binding.songList.adapter = songsAdapter

        binding.playbackControls.setOnClickListener {
            Log.d(TAG, "playback controls holder clicked navigating to song view")
            val direction = SongListFragmentDirections.actionSongListFragmentToSongViewFragment()
            findNavController().navigate(direction)
        }
    }

    @UnstableApi
    fun setPlayerController() = lifecycleScope.launch {
        viewModel.mediaControllerFlow.collectLatest { mediaController ->
            binding.playbackControls.player = mediaController
        }
    }



    private fun loadSongs() {
        viewModel.getSongsLists()
        viewModel.addSongsToPlayer()
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}