package com.netflixclone.data

import androidx.lifecycle.ViewModelProvider

object Injection {
    private fun provideMediaRepository(): MediaRepository {
        return MediaRepository
    }

    fun provideMediaViewModelFactory(): ViewModelProvider.Factory {
        return MediaViewModelFactory(provideMediaRepository())
    }

    fun provideMovieDetailsViewModelFactory(): ViewModelProvider.Factory {
        return MovieDetailsViewModelFactory(provideMediaRepository())
    }

    fun provideTvShowDetailsViewModelFactory(): ViewModelProvider.Factory {
        return TvShowDetailsViewModelFactory(provideMediaRepository())
    }

    fun provideFeedViewModelFactory(): ViewModelProvider.Factory {
        return FeedViewModelFactory()
    }

    fun provideSearchResultsViewModelFactory(): ViewModelProvider.Factory {
        return SearchResultsViewModelFactory(provideMediaRepository())
    }
}