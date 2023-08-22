package com.bale_bootcamp.yourmusic.presentation.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bale_bootcamp.yourmusic.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint


private const val TAG = "MainActivity"
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}