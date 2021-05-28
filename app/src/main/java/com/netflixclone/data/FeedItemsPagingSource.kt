package com.netflixclone.data

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import com.netflixclone.data_models.FeedItem
import com.netflixclone.network.services.ApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class FeedItemsSourceFactory(val scope: CoroutineScope) :
    DataSource.Factory<Int, FeedItem>() {
    var feedItemDataSourceLiveData: MutableLiveData<FeedItemsDataSource> = MutableLiveData()

    override fun create(): DataSource<Int, FeedItem> {
        val feedItemsDataSource = FeedItemsDataSource(scope)
        feedItemDataSourceLiveData.postValue(feedItemsDataSource)
        return feedItemsDataSource
    }
}

private const val STARTING_PAGE_INDEX = 1

sealed class ApiState {
    object Loading : ApiState()
    class Error(val error: Throwable) : ApiState()
    object Success : ApiState()
}

class FeedItemsDataSource(
    val scope: CoroutineScope,
) : PageKeyedDataSource<Int, FeedItem>() {

    val networkStatusLiveData: MutableLiveData<ApiState> = MutableLiveData()

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, FeedItem>
    ) {
        networkStatusLiveData.postValue(ApiState.Loading)
        executeQuery(STARTING_PAGE_INDEX) { it, next ->
            callback.onResult(it, null, next)
            networkStatusLiveData.postValue(ApiState.Success)
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, FeedItem>) {
        networkStatusLiveData.postValue(ApiState.Loading)
        val offset = params.key
        executeQuery(offset) { it, next ->
            callback.onResult(it, next)
            networkStatusLiveData.postValue(ApiState.Success)
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, FeedItem>) {
        networkStatusLiveData.postValue(ApiState.Loading)
        val offset = params.key
        executeQuery(offset) { it, next ->
            callback.onResult(it, next)
            networkStatusLiveData.postValue(ApiState.Success)
        }
    }

    private fun executeQuery(page: Int, callback: (List<FeedItem>, Int?) -> Unit) {
        scope.launch {
            try {
                val response = ApiClient.Firebase.fetchFeedItems(page)
                val next = if (page < response.totalPages) page + 1 else null
                callback(response.results, next)
            } catch (e: Exception) {
            }
        }
    }
}