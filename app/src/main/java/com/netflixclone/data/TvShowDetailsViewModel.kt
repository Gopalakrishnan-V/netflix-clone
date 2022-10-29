package com.netflixclone.data

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.netflixclone.data_models.Resource
import com.netflixclone.extensions.getInitialSeasonIndex
import com.netflixclone.network.models.TvDetailsResponse
import com.netflixclone.network.models.TvSeasonDetailsResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TvShowDetailsViewModel @Inject constructor() : ViewModel() {

    val details: MutableLiveData<Resource<TvDetailsResponse>> =
        MutableLiveData(Resource(false, null, null))
    val selectedSeasonNameIndexPair = MutableLiveData<Pair<String, Int>>(null)
    val selectedSeasonDetails: MutableLiveData<Resource<TvSeasonDetailsResponse>> =
        MutableLiveData(Resource(false, null, null))

    fun fetchTvShowDetails(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            details.postValue(details.value!!.copy(isLoading = true))
            try {
                val tvShowResponse = MediaRepository.fetchTvShowDetails(id)
                val initialSeasonIndex = tvShowResponse.getInitialSeasonIndex()
                if (initialSeasonIndex != -1) {
                    val initialSeason = tvShowResponse.seasons[initialSeasonIndex]
                    val firstSeasonDetails =
                        MediaRepository.fetchTvShowSeasonDetails(id, initialSeason.seasonNumber)
                    selectedSeasonDetails.postValue(selectedSeasonDetails.value!!.copy(data = firstSeasonDetails))
                    selectedSeasonNameIndexPair.postValue(Pair(initialSeason.name,
                        initialSeasonIndex))
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
                val response = MediaRepository.fetchTvShowSeasonDetails(id, seasonNumber)
                selectedSeasonDetails.postValue(selectedSeasonDetails.value!!.copy(data = response))
            } catch (e: Exception) {
                selectedSeasonDetails.postValue(selectedSeasonDetails.value!!.copy(error = e.message))
            }
        }
    }
}