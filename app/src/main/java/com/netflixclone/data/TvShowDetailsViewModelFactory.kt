package com.netflixclone.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class TvShowDetailsViewModelFactory(private val repository: MediaRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TvShowDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TvShowDetailsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}