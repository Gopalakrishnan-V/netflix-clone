package com.netflixclone.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.netflixclone.databinding.FragmentDownloadsBinding

const val POSTER_IMAGE = "https://i.ibb.co/12fHwfg/netflix-downloads.png"

class DownloadsFragment : BottomNavFragment() {
    private lateinit var binding: FragmentDownloadsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDownloadsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
        Glide.with(binding.posterImage).load(POSTER_IMAGE).into(binding.posterImage)
    }

    override fun onFirstDisplay() {
    }
}