package com.netflixclone.data

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.netflixclone.data_models.TvShow
import com.netflixclone.data_models.Video
import com.netflixclone.network.models.TvDetailsResponse
import com.netflixclone.network.models.TvSeasonDetailsResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TvShowDetailsViewModel(private val repository: MediaRepository) : ViewModel() {

    val tvDetailsLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    val tvDetails: MutableLiveData<TvDetailsResponse> = MutableLiveData()

    val selectedSeasonDetailsLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    val selectedSeasonDetails: MutableLiveData<TvSeasonDetailsResponse> = MutableLiveData()
    val selectedSeasonIndex: MutableLiveData<Int> = MutableLiveData(0)

    val similarTvShowsLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    val similarTvShows: MutableLiveData<List<TvShow>> = MutableLiveData()

    val tvShowVideosLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    val tvShowVideos: MutableLiveData<List<Video>> = MutableLiveData()

    fun fetchTvShowDetails(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            tvDetailsLoading.postValue(true)
            try {
                val response = repository.fetchTvShowDetails(id)
                tvDetails.postValue(response)
                tvDetailsLoading.postValue(false)
            } catch (e: Exception) {
                tvDetailsLoading.postValue(false)
            }
        }
    }

    fun fetchSimilarTvShows(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            similarTvShowsLoading.postValue(true)
            try {
                val response = repository.fetchSimilarTvShows(id)
                similarTvShows.postValue(response.results)
                similarTvShowsLoading.postValue(false)
            } catch (e: Exception) {
                similarTvShowsLoading.postValue(false)
            }
        }
    }

    fun fetchTvShowVideos(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            tvShowVideosLoading.postValue(true)
            try {
                val response = repository.fetchTvShowVideos(id)
                tvShowVideos.postValue(response.results)
                tvShowVideosLoading.postValue(false)
            } catch (e: Exception) {
                tvShowVideosLoading.postValue(false)
            }
        }
    }

    fun fetchSeasonDetails(id: Int, seasonNumber: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            selectedSeasonDetailsLoading.postValue(true)
            try {
                val response = repository.fetchTvShowSeasonDetails(id, seasonNumber)
                selectedSeasonDetails.postValue(response)
                selectedSeasonDetailsLoading.postValue(false)
            } catch (e: Exception) {
                selectedSeasonDetailsLoading.postValue(false)
            }
        }
    }
}