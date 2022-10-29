package com.netflixclone.data

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.netflixclone.data_models.Resource
import com.netflixclone.network.models.MovieDetailsResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieDetailsViewModel @Inject constructor() : ViewModel() {
    val details: MutableLiveData<Resource<MovieDetailsResponse>> =
        MutableLiveData(Resource(false, null, null))

    fun fetchMovieDetails(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            details.postValue(details.value!!.copy(isLoading = true))
            try {
                val response = MediaRepository.fetchMovieDetails(id)
                details.postValue(details.value!!.copy(isLoading = false, data = response))
            } catch (e: Exception) {
                details.postValue(details.value!!.copy(isLoading = false, error = e.message))
            }
        }
    }
}