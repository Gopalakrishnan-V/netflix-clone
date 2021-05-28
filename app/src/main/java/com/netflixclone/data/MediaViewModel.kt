package com.netflixclone.data

import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import com.netflixclone.data_models.Movie
import com.netflixclone.data_models.TvShow
import kotlinx.coroutines.flow.Flow

class MediaViewModel(private val repository: MediaRepository) : ViewModel() {

    fun getUpcomingMovies(): Flow<PagingData<Movie>> {
        return repository.getUpcomingMovies()
    }

    fun getPopularMovies(): Flow<PagingData<Movie>> {
        return repository.getPopularMoviesStream()
    }

    fun getPopularTvShows(): Flow<PagingData<TvShow>> {
        return repository.getPopularTvShowsStream()
    }
}