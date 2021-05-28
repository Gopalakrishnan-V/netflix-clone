package com.netflixclone.screens

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.netflixclone.data_models.MediaBsData
import com.netflixclone.databinding.BottomSheetMediaDetailsBinding

class MediaDetailsBottomSheet(private val data: MediaBsData) : BottomSheetDialogFragment() {
    lateinit var binding: BottomSheetMediaDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = BottomSheetMediaDetailsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupUI()
        updateUI()
    }

    private fun setupUI() {
        binding.closeIcon.setOnClickListener { dismiss() }
        binding.detailsButton.setOnClickListener {
            if (data.mediaType == "movie") {
                val intent = Intent(activity, MovieDetailsActivity::class.java)
                intent.putExtra("id", data.mediaId)
                startActivity(intent)
                dismiss()
            } else if (data.mediaType == "tv") {
                val intent = Intent(activity, TvDetailsActivity::class.java)
                intent.putExtra("id", data.mediaId)
                startActivity(intent)
                dismiss()
            }
        }
    }

    private fun updateUI() {
        if (data.mediaType == "movie") {
            Glide.with(this).load(data.posterUrl).transform(CenterCrop(), RoundedCorners(8))
                .into(binding.posterImage)
            binding.titleText.text = data.title
            binding.yearText.text = data.releaseYear
            binding.runtimeText.visibility = View.GONE
            binding.overviewText.text = data.overview
        } else if (data.mediaType == "tv") {
            Glide.with(this).load(data.posterUrl).transform(CenterCrop(), RoundedCorners(8))
                .into(binding.posterImage)
            binding.titleText.text = data.title
            binding.yearText.text = data.releaseYear
            binding.runtimeText.visibility = View.GONE
            binding.overviewText.text = data.overview
        }
    }

    companion object {
        fun newInstance(data: MediaBsData): MediaDetailsBottomSheet {
            return MediaDetailsBottomSheet(data)
        }
    }
}