package com.netflixclone.data

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.netflixclone.data_models.Resource
import com.netflixclone.extensions.getInitialSeasonIndex
import com.netflixclone.network.models.TvDetailsResponse
import com.netflixclone.network.models.TvSeasonDetailsResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TvShowDetailsViewModel(private val repository: MediaRepository) : ViewModel() {

    val details: MutableLiveData<Resource<TvDetailsResponse>> =
        MutableLiveData(Resource(false, null, null))
    val selectedSeasonNameIndexPair = MutableLiveData<Pair<String, Int>>(null)
    val selectedSeasonDetails: MutableLiveData<Resource<TvSeasonDetailsResponse>> =
        MutableLiveData(Resource(false, null, null))

    fun fetchTvShowDetails(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            details.postValue(details.value!!.copy(isLoading = true))
            try {
                val tvShowResponse = repository.fetchTvShowDetails(id)
                val initialSeasonIndex = tvShowResponse.getInitialSeasonIndex()
                if (initialSeasonIndex != -1) {
                    val initialSeason = tvShowResponse.seasons[initialSeasonIndex]
                    val firstSeasonDetails =
                        repository.fetchTvShowSeasonDetails(id, initialSeason.seasonNumber)
                    selectedSeasonDetails.postValue(selectedSeasonDetails.value!!.copy(data = firstSeasonDetails))
                    selectedSeasonNameIndexPair.postValue(Pair(initialSeason.name, initialSeasonIndex))
                }
                details.postValue(details.value!!.copy(isLoading = false, data = tvShowResponse))
            } catch (e: Exception) {
                details.postValue(details.value!!.copy(isLoading = false, error = e.message))
            }
        }
    }

    fun fetchSeasonDetails(id: Int, seasonNumber: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            selectedSeasonDetails.postValue(selectedSeasonDetails.value!!.copy(isLoading = true))
            try {
                val response = repository.fetchTvShowSeasonDetails(id, seasonNumber)
                selectedSeasonDetails.postValue(selectedSeasonDetails.value!!.copy(data = response))
            } catch (e: Exception) {
                selectedSeasonDetails.postValue(selectedSeasonDetails.value!!.copy(error = e.message))
            }
        }
    }
}