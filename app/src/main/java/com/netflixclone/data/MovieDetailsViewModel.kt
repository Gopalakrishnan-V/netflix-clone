package com.netflixclone.data

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.netflixclone.data_models.Movie
import com.netflixclone.data_models.Video
import com.netflixclone.network.models.MovieDetailsResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MovieDetailsViewModel(private val repository: MediaRepository) : ViewModel() {

    val movieDetailsLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    val movieDetails: MutableLiveData<MovieDetailsResponse> = MutableLiveData()

    val similarMoviesLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    val similarMovies: MutableLiveData<List<Movie>> = MutableLiveData()

    val movieVideosLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    val movieVideos: MutableLiveData<List<Video>> = MutableLiveData()

    fun fetchMovieDetails(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            movieDetailsLoading.postValue(true)
            try {
                val response = repository.fetchMovieDetails(id)
                movieDetails.postValue(response)
                movieDetailsLoading.postValue(false)
            } catch (e: Exception) {
                movieDetailsLoading.postValue(false)
            }
        }
    }

    fun fetchSimilarMovies(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            similarMoviesLoading.postValue(true)
            try {
                val response = repository.fetchSimilarMovies(id)
                similarMovies.postValue(response.results)
                similarMoviesLoading.postValue(false)
            } catch (e: Exception) {
                similarMoviesLoading.postValue(false)
            }
        }
    }

    fun fetchMovieVideos(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            movieVideosLoading.postValue(true)
            try {
                val response = repository.fetchMovieVideos(id)
                movieVideos.postValue(response.results)
                movieVideosLoading.postValue(false)
            } catch (e: Exception) {
                movieVideosLoading.postValue(false)
            }
        }
    }
}