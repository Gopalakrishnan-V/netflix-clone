package com.netflixclone.data

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.netflixclone.data_models.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import java.util.*
import javax.inject.Inject

sealed class FeedItem {
    data class Header(val data: Media) : FeedItem()
    data class HorizontalList(
        val title: String,
        val data: List<Media>,
        val isLarge: Boolean = false,
    ) : FeedItem()
}

@HiltViewModel
class FeedViewModel @Inject constructor() : ViewModel() {
    var feedItemsSourceFactory: FeedItemsSourceFactory? = null

    var feedList = mutableStateOf(Resource<List<FeedItem>>())
        private set

    fun getFeedPagedList(): LiveData<PagedList<com.netflixclone.data_models.FeedItem>> {
        feedItemsSourceFactory = FeedItemsSourceFactory(viewModelScope)
        return LivePagedListBuilder(feedItemsSourceFactory!!, getPagedListConfig()).build()
    }

    private fun getPagedListConfig(): PagedList.Config {
        return PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setInitialLoadSizeHint(5)
            .setPageSize(10)
            .build()
    }

    fun fetchData() = viewModelScope.launch(Dispatchers.IO) {
        feedList.value = Resource(isLoading = true)
        val trendingFirstDeferred = async { fetchTrendingFirst() }
        val popularMoviesDeferred = async { fetchPopularMovies() }
        val netflixTvShowsDeferred = async { discoverNetflixTvShows() }
        val actionMoviesDeferred = async { discoverActionMovies() }
        val popularTvShowsDeferred = async { fetchPopularTvShows() }
        val trendingDeferred = async { fetchTrending() }

        val trendingFirst = trendingFirstDeferred.await()
        val popularMovies = popularMoviesDeferred.await()
        val netflixTvShows = netflixTvShowsDeferred.await()
        val actionMovies = actionMoviesDeferred.await()
        val popularTvShows = popularTvShowsDeferred.await()
        val trending = trendingDeferred.await()

        val results = mutableListOf<FeedItem>()

        if (trendingFirst != null) {
            results.add(FeedItem.Header(trendingFirst))
        }
        if (popularMovies != null) {
            results.add(FeedItem.HorizontalList("Popular Movies",
                popularMovies.map { it.toMediaMovie() }))
        }
        if (netflixTvShows != null) {
            results.add(FeedItem.HorizontalList(
                "Only on Netflix",
                netflixTvShows.map { it.toMediaTvShow() },
                true
            ))
        }
        if (actionMovies != null) {
            results.add(FeedItem.HorizontalList(
                "Blockbuster Action",
                actionMovies.map { it.toMediaMovie() },
                false
            ))
        }
        if (popularTvShows != null) {
            results.add(FeedItem.HorizontalList(
                "Popular Tv Shows",
                popularTvShows.map { it.toMediaTvShow() },
                false
            ))
        }
        if (trending != null) {
            results.add(FeedItem.HorizontalList("Top Trending", trending, true))
        }
        feedList.value = Resource(data = results)
    }

    private suspend fun fetchTrendingFirst(): Media? {
        return try {
            val response = MediaRepository.fetchTrending("day")
            val filteredResults = response.results.filter { it.mediaType != MediaType.PERSON }
            if (filteredResults.isNotEmpty()) {
                val dayOfMonth = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
                val filteredResultsCount = filteredResults.count()
                filteredResults.getOrNull(dayOfMonth % filteredResultsCount)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun fetchPopularMovies(): List<Movie>? {
        return try {
            MediaRepository.fetchPopularMovies(1).results
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun fetchPopularTvShows(): List<TvShow>? {
        return try {
            MediaRepository.fetchPopularTvShows(1).results
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun discoverNetflixTvShows(): List<TvShow>? {
        return try {
            MediaRepository.discoverTvShows(
                voteCountGreater = 5000,
                withWatchProviders = 8,
                watchRegion = "IN"
            ).results
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun discoverActionMovies(): List<Movie>? {
        return try {
            MediaRepository.discoverMovies(
                withGenres = "28",
                sortBy = "vote_average.desc",
                voteCountGreater = 1000,
            ).results
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun fetchTrending(): List<Media>? {
        return try {
            val response = MediaRepository.fetchTrending(timeWindow = "week")
            val filteredResults = response.results.filter { it.mediaType != MediaType.PERSON }

            withContext(Dispatchers.Default) {
                filteredResults.sortedByDescending {
                    when (it) {
                        is Media.Movie -> it.voteAverage
                        is Media.Tv -> it.voteAverage
                        else -> null
                    }
                }
            }
        } catch (e: Exception) {
            null
        }
    }
}